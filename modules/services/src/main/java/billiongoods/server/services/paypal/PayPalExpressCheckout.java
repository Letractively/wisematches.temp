package billiongoods.server.services.paypal;

import billiongoods.server.MessageFormatter;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderItem;
import billiongoods.server.services.payment.Shipment;
import billiongoods.server.warehouse.Price;
import com.paypal.core.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import urn.ebay.api.PayPalAPI.*;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.CoreComponentTypes.MeasureType;
import urn.ebay.apis.eBLBaseComponents.*;

import java.util.*;

/**
 * https://devtools-paypal.com/
 * https://www.paypalobjects.com/webstatic/en_US/developer/docs/pdf/pp_expresscheckout_advancedfeaturesguide.pdf
 * https://developer.paypal.com/webapps/developer/docs/classic/products/
 * http://www.integratingstuff.com/2010/07/17/paypal-express-checkout-with-java/
 * http://www.paypalobjects.com/en_US/ebook/PP_NVPAPI_DeveloperGuide/Appx_fieldreference.html#2829277
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalExpressCheckout implements InitializingBean {
	private Configuration configuration;
	private PayPalTransactionManager transactionManager;

	private PayPalAPIInterfaceServiceService service;
	private final Map<String, String> sdkConfig = new HashMap<>();

	private static final CurrencyCodeType CURRENCY_CODE = CurrencyCodeType.RUB;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.paypal.ExpressCheckout");

	public PayPalExpressCheckout() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sdkConfig.put(Constants.MODE, configuration.getEnvironment().getCode());

		sdkConfig.put("acct1" + Constants.CREDENTIAL_USERNAME_SUFFIX, configuration.getUser());
		sdkConfig.put("acct1" + Constants.CREDENTIAL_PASSWORD_SUFFIX, configuration.getPassword());
		sdkConfig.put("acct1" + Constants.CREDENTIAL_SIGNATURE_SUFFIX, configuration.getSignature());

		final String proxyHost = System.getProperty("http.proxyHost");
		final String proxyPort = System.getProperty("http.proxyPort");

		sdkConfig.put(Constants.USE_HTTP_PROXY, String.valueOf(proxyHost != null && proxyPort != null));
		sdkConfig.put(Constants.HTTP_PROXY_HOST, proxyHost);
		sdkConfig.put(Constants.HTTP_PROXY_PORT, proxyPort);

		service = new PayPalAPIInterfaceServiceService(sdkConfig);
	}

	public String getExpressCheckoutEndPoint(String token) {
		return configuration.getEnvironment().getPayPalEndpoint() + "?cmd=_express-checkout&token=" + token;
	}

	public PayPalMessage registerIPNMessage(Map<String, String[]> parameterMap) throws PayPalException {
		try {
			final PayPalMessageValidator ipnMessage = new PayPalMessageValidator(parameterMap, sdkConfig);
			if (ipnMessage.validate()) {
				return transactionManager.registerMessage(ipnMessage.getIpnMap());
			} else {
				return null;
			}
		} catch (Exception ex) {
			throw new PayPalSystemException("IPN", "IPN Message can't be registered", ex);
		}
	}


	public PayPalTransaction initiateExpressCheckout(Order order, String orderURL, String returnURL, String cancelURL) throws PayPalException {
		final PayPalTransaction transaction = transactionManager.beginTransaction(order);
		log.info("PayPal transaction started: " + transaction.getId());

		try {
			final SetExpressCheckoutResponseType response = setExpressCheckout(transaction.getId(), order, orderURL, returnURL, cancelURL);
			transactionManager.checkoutInitiated(transaction, response);
			return transaction;
		} catch (PayPalException ex) {
			transactionManager.rollbackTransaction(transaction, TransactionPhase.INVOICING, ex);
			throw ex;
		}
	}

	public PayPalTransaction finalizeExpressCheckout(String token, boolean approved) throws PayPalException {
		final PayPalTransaction transaction = transactionManager.getTransaction(token);
		if (transaction == null) {
			throw new PayPalSystemException(token, "There is no transaction for token: " + token);
		}

		final GetExpressCheckoutDetailsResponseType response;
		try {
			response = getExpressCheckout(token);
		} catch (PayPalException ex) {
			transactionManager.rollbackTransaction(transaction, TransactionPhase.VERIFICATION, ex); // rollback is not possible.
			throw ex;
		}

		final GetExpressCheckoutDetailsResponseDetailsType details = response.getGetExpressCheckoutDetailsResponseDetails();
		try {
			transactionManager.checkoutValidated(transaction, response);

			if (approved) {
				final DoExpressCheckoutPaymentResponseType doResponse = doExpressCheckout(details);
				transactionManager.checkoutConfirmed(transaction, doResponse);
			}
			transactionManager.commitTransaction(transaction, approved);
			return transaction;
		} catch (PayPalException ex) {
			transactionManager.rollbackTransaction(transaction, TransactionPhase.CONFIRMATION, ex);
			throw ex;
		}
	}

	private SetExpressCheckoutResponseType setExpressCheckout(Long tnxId, Order order, String orderURL,
															  String returnURL, String cancelURL) throws PayPalException {
		final Shipment shipment = order.getShipment();

		final Address address = shipment.getAddress();

		final AddressType addressType = new AddressType();
		addressType.setName(address.getFirstName() + " " + address.getLastName());
		addressType.setPostalCode(address.getPostcode());
		addressType.setCountry(CountryCodeType.RU);
		addressType.setCityName(address.getCity());
		addressType.setStateOrProvince(address.getRegion());
		addressType.setStreet1(address.getLocation());

		final List<PaymentDetailsItemType> paymentDetailsItem = new ArrayList<>();

		final List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			final PaymentDetailsItemType item = new PaymentDetailsItemType();
			item.setName(orderItem.getProduct().getName());
			item.setNumber(MessageFormatter.getProductCode(orderItem.getProduct()));
			item.setItemWeight(new MeasureType("кг", orderItem.getWeight()));
			item.setQuantity(orderItem.getQuantity());
			item.setAmount(new BasicAmountType(CURRENCY_CODE, Price.string(orderItem.getAmount())));
			item.setDescription(orderItem.getOptions());

			paymentDetailsItem.add(item);
		}

		if (order.getCoupon() != null && order.getDiscount() > 0d) {
			final PaymentDetailsItemType item = new PaymentDetailsItemType();
			item.setName("Скидка");
			item.setDescription("Купон " + order.getCoupon());
			item.setAmount(new BasicAmountType(CURRENCY_CODE, Price.string(order.getDiscount() * -1)));

			paymentDetailsItem.add(item);
		}

		final PaymentDetailsType paymentDetails = new PaymentDetailsType();
		paymentDetails.setOrderURL(orderURL);
		paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);
		paymentDetails.setPaymentDetailsItem(paymentDetailsItem);

		paymentDetails.setShipToAddress(addressType);

		paymentDetails.setItemTotal(new BasicAmountType(CURRENCY_CODE, Price.string(order.getAmount() - order.getDiscount())));
		paymentDetails.setShippingTotal(new BasicAmountType(CURRENCY_CODE, Price.string(shipment.getAmount())));
		paymentDetails.setOrderTotal(new BasicAmountType(CURRENCY_CODE, Price.string(order.getAmount() + shipment.getAmount() - order.getDiscount())));

		final SetExpressCheckoutRequestDetailsType request = new SetExpressCheckoutRequestDetailsType();
		request.setLocaleCode("RU");
		request.setAddressOverride("1");
		request.setNoShipping("0");
		request.setReqConfirmShipping("0");
		request.setChannelType(ChannelType.MERCHANT);
		request.setSolutionType(SolutionTypeType.SOLE);
		request.setReturnURL(returnURL);
		request.setCancelURL(cancelURL);
		request.setPaymentDetails(Collections.singletonList(paymentDetails));
		request.setCppLogoImage("http://static.billiongoods.ru/images/logo.png");

		request.setInvoiceID(String.valueOf(tnxId));

		try {
			final SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(request);

			final SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
			setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

			final SetExpressCheckoutResponseType response = service.setExpressCheckout(setExpressCheckoutReq);
			if (response.getAck() != AckCodeType.SUCCESS) {
				throw new PayPalQueryException("ID" + tnxId, new PayPalQueryError(response));
			}
			return response;
		} catch (PayPalException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new PayPalSystemException("ID" + tnxId, "SetExpressCheckout can't be executed. TnxId: " + tnxId, ex);
		}
	}

	private GetExpressCheckoutDetailsResponseType getExpressCheckout(String token) throws PayPalException {
		final GetExpressCheckoutDetailsRequestType request = new GetExpressCheckoutDetailsRequestType(token);

		final GetExpressCheckoutDetailsReq req = new GetExpressCheckoutDetailsReq();
		req.setGetExpressCheckoutDetailsRequest(request);

		try {
			final GetExpressCheckoutDetailsResponseType response = service.getExpressCheckoutDetails(req);
			if (response.getAck() != AckCodeType.SUCCESS) {
				throw new PayPalQueryException("TK" + token, new PayPalQueryError(response));
			}
			return response;
		} catch (PayPalException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new PayPalSystemException("TK" + token, "SetExpressCheckout can't be executed. Token: " + token, ex);
		}
	}

	private DoExpressCheckoutPaymentResponseType doExpressCheckout(GetExpressCheckoutDetailsResponseDetailsType details) throws PayPalException {
		final PayerInfoType payerInfo = details.getPayerInfo();

		final DoExpressCheckoutPaymentRequestDetailsType requestDetails = new DoExpressCheckoutPaymentRequestDetailsType();
		requestDetails.setToken(details.getToken());
		requestDetails.setPayerID(payerInfo.getPayerID());
		requestDetails.setPaymentAction(PaymentActionCodeType.SALE);
		requestDetails.setPaymentDetails(details.getPaymentDetails());

		final DoExpressCheckoutPaymentRequestType request = new DoExpressCheckoutPaymentRequestType();
		request.setDoExpressCheckoutPaymentRequestDetails(requestDetails);

		final DoExpressCheckoutPaymentReq doRequest = new DoExpressCheckoutPaymentReq();
		doRequest.setDoExpressCheckoutPaymentRequest(request);

		try {
			final DoExpressCheckoutPaymentResponseType response = service.doExpressCheckoutPayment(doRequest);
			if (response.getAck() != AckCodeType.SUCCESS) {
				throw new PayPalQueryException(details.getToken(), new PayPalQueryError(response));
			}
			return response;
		} catch (PayPalException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new PayPalSystemException(requestDetails.getToken(), "SetExpressCheckout can't be executed. Token: " + requestDetails.getToken(), ex);
		}
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setTransactionManager(PayPalTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
