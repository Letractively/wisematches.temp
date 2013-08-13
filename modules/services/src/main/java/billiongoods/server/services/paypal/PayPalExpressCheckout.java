package billiongoods.server.services.paypal;

import billiongoods.server.services.payment.Address;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderItem;
import billiongoods.server.services.price.PriceConverter;
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

	private static final CurrencyCodeType CURRENCY_CODE = CurrencyCodeType.USD;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.paypal.ExpressCheckout");

	public PayPalExpressCheckout() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sdkConfig.put(Constants.MODE, configuration.getEnvironment().getCode());

		sdkConfig.put("acct1" + Constants.CREDENTIAL_USERNAME_SUFFIX, configuration.getUser());
		sdkConfig.put("acct1" + Constants.CREDENTIAL_PASSWORD_SUFFIX, configuration.getPassword());
		sdkConfig.put("acct1" + Constants.CREDENTIAL_SIGNATURE_SUFFIX, configuration.getSignature());

		sdkConfig.put(Constants.USE_HTTP_PROXY, "true");
		sdkConfig.put(Constants.HTTP_PROXY_HOST, "surf-proxy.intranet.db.com");
		sdkConfig.put(Constants.HTTP_PROXY_PORT, "8080");

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
			throw new PayPalSystemException("IPN Message can't be registered", ex);
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
		final GetExpressCheckoutDetailsResponseType response;
		try {
			response = getExpressCheckout(token);
		} catch (PayPalException ex) {
//            transactionManager.rollbackTransaction(, TransactionPhase.VERIFICATION, ex); // rollback is not possible.
			throw ex;
		}

		final GetExpressCheckoutDetailsResponseDetailsType details = response.getGetExpressCheckoutDetailsResponseDetails();
		final PayPalTransaction transaction = transactionManager.getTransaction(Long.decode(details.getInvoiceID()));

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
		final Address address = order.getAddress();

		final AddressType addressType = new AddressType();
		addressType.setName(address.getName());
		addressType.setPostalCode(address.getPostalCode());
		addressType.setCountry(CountryCodeType.RU);
		addressType.setCityName(address.getCity());
		addressType.setStateOrProvince(address.getRegion());
		addressType.setStreet1(address.getStreetAddress());

		final List<PaymentDetailsItemType> paymentDetailsItem = new ArrayList<>();

		float price = 0f;
		final List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			final PaymentDetailsItemType item = new PaymentDetailsItemType();
			item.setName(orderItem.getName());
			item.setNumber(orderItem.getCode());
			item.setItemWeight(new MeasureType("кг", (double) orderItem.getWeight()));
			price += orderItem.getAmount();
			item.setQuantity(orderItem.getQuantity());
			item.setAmount(new BasicAmountType(CURRENCY_CODE, String.valueOf(orderItem.getAmount())));
			item.setDescription(orderItem.getOptions());

			paymentDetailsItem.add(item);
		}

		final PaymentDetailsType paymentDetails = new PaymentDetailsType();
		paymentDetails.setOrderURL(orderURL);
		paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);
		paymentDetails.setPaymentDetailsItem(paymentDetailsItem);

		paymentDetails.setItemTotal(new BasicAmountType(CURRENCY_CODE, String.valueOf(order.getAmount())));
		paymentDetails.setShippingTotal(new BasicAmountType(CURRENCY_CODE, String.valueOf(order.getShipment())));
		paymentDetails.setOrderTotal(new BasicAmountType(CURRENCY_CODE, String.valueOf(PriceConverter.roundPrice(order.getAmount() + order.getShipment()))));

		final SetExpressCheckoutRequestDetailsType request = new SetExpressCheckoutRequestDetailsType();
		request.setLocaleCode("RU");
//		request.setAddress(addressType);
		request.setAddressOverride("0");
		request.setChannelType(ChannelType.MERCHANT);
		request.setSolutionType(SolutionTypeType.MARK);
		request.setReturnURL(returnURL);
		request.setCancelURL(cancelURL);
		request.setPaymentDetails(Collections.singletonList(paymentDetails));

		request.setInvoiceID(String.valueOf(tnxId));

		try {
			final SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(request);

			final SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
			setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

			final SetExpressCheckoutResponseType response = service.setExpressCheckout(setExpressCheckoutReq);
			if (response.getAck() != AckCodeType.SUCCESS) {
				throw new PayPalQueryException(new PayPalQueryError(response));
			}
			return response;
		} catch (PayPalException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new PayPalSystemException("SetExpressCheckout can't be executed. TnxId: " + tnxId, ex);
		}
	}

	private GetExpressCheckoutDetailsResponseType getExpressCheckout(String token) throws PayPalException {
		final GetExpressCheckoutDetailsRequestType request = new GetExpressCheckoutDetailsRequestType(token);

		final GetExpressCheckoutDetailsReq req = new GetExpressCheckoutDetailsReq();
		req.setGetExpressCheckoutDetailsRequest(request);

		try {
			final GetExpressCheckoutDetailsResponseType response = service.getExpressCheckoutDetails(req);
			if (response.getAck() != AckCodeType.SUCCESS) {
				throw new PayPalQueryException(new PayPalQueryError(response));
			}
			return response;
		} catch (PayPalException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new PayPalSystemException("SetExpressCheckout can't be executed. Token: " + token, ex);
		}
	}

	private DoExpressCheckoutPaymentResponseType doExpressCheckout(GetExpressCheckoutDetailsResponseDetailsType details) throws PayPalException {
		final DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();
		doExpressCheckoutPaymentRequestDetails.setToken(details.getToken());
		doExpressCheckoutPaymentRequestDetails.setPayerID(details.getPayerInfo().getPayerID());
		doExpressCheckoutPaymentRequestDetails.setPaymentAction(PaymentActionCodeType.SALE);

		final DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType();
		doExpressCheckoutPaymentRequest.setDoExpressCheckoutPaymentRequestDetails(doExpressCheckoutPaymentRequestDetails);

		final DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
		doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

		try {
			final DoExpressCheckoutPaymentResponseType response = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
			if (response.getAck() != AckCodeType.SUCCESS) {
				throw new PayPalQueryException(new PayPalQueryError(response));
			}
			return response;
		} catch (PayPalException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new PayPalSystemException("SetExpressCheckout can't be executed. Token: " + details.getToken(), ex);
		}
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setTransactionManager(PayPalTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
