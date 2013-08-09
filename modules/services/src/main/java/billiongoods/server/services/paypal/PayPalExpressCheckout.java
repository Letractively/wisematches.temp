package billiongoods.server.services.paypal;

import billiongoods.server.services.payment.Address;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderItem;
import com.paypal.core.Constants;
import org.springframework.beans.factory.InitializingBean;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
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
	private PayPalSettings settings;
	private PayPalAPIInterfaceServiceService service;

	public PayPalExpressCheckout() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Map<String, String> sdkConfig = new HashMap<>();
		sdkConfig.put(Constants.MODE, settings.getMode().getCode());
		sdkConfig.put("billiongoods" + Constants.CREDENTIAL_USERNAME_SUFFIX, settings.getUser());
		sdkConfig.put("billiongoods" + Constants.CREDENTIAL_PASSWORD_SUFFIX, settings.getPassword());
		sdkConfig.put("billiongoods" + Constants.CREDENTIAL_SIGNATURE_SUFFIX, settings.getSignature());

		sdkConfig.put(Constants.USE_HTTP_PROXY, "true");
		sdkConfig.put(Constants.HTTP_PROXY_HOST, "surf-proxy.intranet.db.com");
		sdkConfig.put(Constants.HTTP_PROXY_PORT, "8080");

		service = new PayPalAPIInterfaceServiceService(sdkConfig);
	}

	public PayPalMode getMode() {
		return settings.getMode();
	}

	public CheckoutToken createExpressCheckout(Order order, WebAddressResolver resolver) throws PayPalException {
		final Address address = order.getAddress();

		final AddressType addressType = new AddressType();
		addressType.setName(address.getName());
		addressType.setPostalCode(address.getPostalCode());
		addressType.setCountry(CountryCodeType.RU);
		addressType.setCityName(address.getCity());
		addressType.setStateOrProvince(address.getRegion());
		addressType.setStreet1(address.getStreetAddress());

		final SetExpressCheckoutRequestDetailsType request = new SetExpressCheckoutRequestDetailsType();
		request.setLocaleCode("RU");
		request.setNoShipping("1");
		request.setAddress(addressType);
		request.setAddressOverride("1");
		request.setSolutionType(SolutionTypeType.MARK);
		request.setChannelType(ChannelType.MERCHANT);
		request.setReturnURL(resolver.getReturnURL());
		request.setCancelURL(resolver.getCancelURL());
		request.setPaymentDetails(Collections.singletonList(createPaymentDetails(order, resolver)));
		request.setInvoiceID(String.valueOf(order.getId()));

		final SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(request);
		setExpressCheckoutRequest.setVersion("104.0");

		final SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
		setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

		try {
			final SetExpressCheckoutResponseType response = service.setExpressCheckout(setExpressCheckoutReq);
			if (response.getAck() == AckCodeType.SUCCESS) {
				return new CheckoutToken(response.getToken(), response.getBuild(), response.getCorrelationID(), response.getTimestamp(), response.getVersion());
			}
			throw new PayPalException("Reject from PayPal received: " + response.getAck() + " [" + response.getErrors() + "]");
		} catch (Exception ex) {
			throw new PayPalException("", ex);
		}
	}


	private PaymentDetailsType createPaymentDetails(Order order, WebAddressResolver resolver) {
		final List<PaymentDetailsItemType> res = new ArrayList<>();

		float total = 0f;
		final CurrencyCodeType usd = CurrencyCodeType.USD;
		final List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			final PaymentDetailsItemType item = new PaymentDetailsItemType();
			item.setName(orderItem.getName());
			item.setNumber(orderItem.getCode());
			item.setItemURL(resolver.getArticleURL(orderItem.getNumber()));
			item.setItemWeight(new MeasureType("кг", (double) orderItem.getWeight()));
			item.setQuantity(orderItem.getQuantity());
			item.setAmount(new BasicAmountType(usd, String.valueOf(orderItem.getAmount())));
			item.setDescription(orderItem.getOptions());

			res.add(item);
			total += orderItem.getQuantity() * orderItem.getAmount();
		}

		final PaymentDetailsType paymentDetails = new PaymentDetailsType();
		paymentDetails.setOrderURL(resolver.getOrderURL(order));
		paymentDetails.setCustom(String.valueOf(order.getId()));
		paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);
		paymentDetails.setPaymentDetailsItem(res);
		paymentDetails.setOrderTotal(new BasicAmountType(usd, String.valueOf(total)));

		return paymentDetails;
	}

	public PayPalSettings getSettings() {
		return settings;
	}

	public void setSettings(PayPalSettings settings) {
		this.settings = settings;
	}
}
