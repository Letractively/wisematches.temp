package billiongoods.server.services.paypal;

import billiongoods.server.services.payment.Address;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderItem;
import com.paypal.core.Constants;
import com.paypal.ipn.IPNMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import urn.ebay.api.PayPalAPI.*;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.CoreComponentTypes.MeasureType;
import urn.ebay.apis.eBLBaseComponents.*;

import java.text.SimpleDateFormat;
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

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.paypal.ExpressCheckout");

    public PayPalExpressCheckout() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sdkConfig.put(Constants.MODE, configuration.getEnvironment().getCode());

        sdkConfig.put("acct1" + Constants.CREDENTIAL_USERNAME_SUFFIX, configuration.getUser());
        sdkConfig.put("acct1" + Constants.CREDENTIAL_PASSWORD_SUFFIX, configuration.getPassword());
        sdkConfig.put("acct1" + Constants.CREDENTIAL_SIGNATURE_SUFFIX, configuration.getSignature());
/*

		sdkConfig.put(Constants.USE_HTTP_PROXY, "true");
		sdkConfig.put(Constants.HTTP_PROXY_HOST, "surf-proxy.intranet.db.com");
		sdkConfig.put(Constants.HTTP_PROXY_PORT, "8080");
*/

        service = new PayPalAPIInterfaceServiceService(sdkConfig);
    }


    public String initiateExpressCheckout(Order order, String orderURL, String returnURL, String cancelURL) throws PayPalException {
        final Long tnxId = transactionManager.beginTransaction(order);
        log.info("PayPal transaction started: " + tnxId);

        final SetExpressCheckoutResponseType response = setExpressCheckout(tnxId, order, orderURL, returnURL, cancelURL);
        transactionManager.checkoutInitiated(tnxId, response);

        if (response.getAck() == AckCodeType.SUCCESS) {
            return response.getToken();
        } else {
            dumpErrorResponse(tnxId, response);
            transactionManager.commitTransaction(tnxId, TransactionResolution.FAILED);
        }
        return null;
    }

    public String processExpressCheckout(String token, boolean approved) throws PayPalException {
        final GetExpressCheckoutDetailsResponseType response = getExpressCheckout(token);
        final GetExpressCheckoutDetailsResponseDetailsType details = response.getGetExpressCheckoutDetailsResponseDetails();

        final Long tnxId = Long.decode(details.getInvoiceID());
        transactionManager.checkoutValidated(tnxId, response);

        if (response.getAck() == AckCodeType.SUCCESS) {
            dumpErrorResponse(tnxId, response);
        }

        if (approved && response.getAck() == AckCodeType.SUCCESS) {
            final DoExpressCheckoutPaymentResponseType doResponse = doExpressCheckout(details);
            transactionManager.checkoutConfirmed(tnxId, doResponse);

            if (doResponse.getAck() == AckCodeType.SUCCESS) {
                transactionManager.commitTransaction(tnxId, TransactionResolution.VERIFIED);
                return response.getGetExpressCheckoutDetailsResponseDetails().getPayerInfo().getPayer();
            } else {
                dumpErrorResponse(tnxId, response);
                transactionManager.commitTransaction(tnxId, TransactionResolution.FAILED);
            }
        } else {
            transactionManager.commitTransaction(tnxId, TransactionResolution.FAILED);
        }
        return details.getPayerInfo().getPayer();
    }

    public String getExpressCheckoutEndPoint(String token) {
        return configuration.getEnvironment().getPayPalEndpoint() + "?cmd=_express-checkout&token=" + token;
    }


    public void registerIPNMessage(Map<String, String[]> parameterMap) {
        try {
            final IPNMessage ipnMessage = new IPNMessage(parameterMap, sdkConfig);
            if (ipnMessage.validate()) {
                final Long aLong = transactionManager.registerMessage(parameterMap);
                log.info("IPN message registered: " + aLong);
            } else {
                log.info("Invalid IPN message received: " + parameterMap);
            }
        } catch (Exception ex) {
            log.error("IPN message can't be processed: " + parameterMap, ex);
        }
    }


    private SetExpressCheckoutResponseType setExpressCheckout(Long tnxId, Order order,
                                                              String orderURL, String returnURL, String cancelURL) throws PayPalException {
        final Address address = order.getAddress();

        final AddressType addressType = new AddressType();
        addressType.setName(address.getName());
        addressType.setPostalCode(address.getPostalCode());
        addressType.setCountry(CountryCodeType.US); //TODO: for testing only: RU
        addressType.setCityName(address.getCity());
        addressType.setStateOrProvince(address.getRegion());
        addressType.setStreet1(address.getStreetAddress());

        final SetExpressCheckoutRequestDetailsType request = new SetExpressCheckoutRequestDetailsType();
        request.setLocaleCode("RU");
        request.setAddress(addressType);
        request.setAddressOverride("1");
        request.setSolutionType(SolutionTypeType.MARK);
        request.setChannelType(ChannelType.MERCHANT);
        request.setReturnURL(returnURL);
        request.setCancelURL(cancelURL);
        request.setPaymentDetails(Collections.singletonList(createPaymentDetails(order, orderURL)));

        request.setInvoiceID(String.valueOf(tnxId));

        try {
            final SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(request);

            final SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
            setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

            return service.setExpressCheckout(setExpressCheckoutReq);
        } catch (Exception ex) {
            throw new PayPalException("SetExpressCheckout can't be executed", ex);
        }
    }

    private GetExpressCheckoutDetailsResponseType getExpressCheckout(String token) throws PayPalException {
        final GetExpressCheckoutDetailsRequestType request = new GetExpressCheckoutDetailsRequestType(token);

        final GetExpressCheckoutDetailsReq req = new GetExpressCheckoutDetailsReq();
        req.setGetExpressCheckoutDetailsRequest(request);

        try {
            return service.getExpressCheckoutDetails(req);
        } catch (Exception ex) {
            throw new PayPalException("GetExpressCheckout can't be executed", ex);
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
            return service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
        } catch (Exception ex) {
            throw new PayPalException("DoExpressCheckout can't be executed", ex);
        }
    }

    private void dumpErrorResponse(Long tnxId, AbstractResponseType response) {
        StringBuilder b = new StringBuilder();
        for (ErrorType errorType : response.getErrors()) {
            b.append("code=").append(errorType.getErrorCode());
            b.append(", severity=").append(errorType.getSeverityCode());
            b.append(", shortMessage=").append(errorType.getShortMessage());
            b.append(", longMessage=").append(errorType.getLongMessage());
        }
        log.error("Unsuccess SetExpressCheckout status received: " +
                "tnx=" + tnxId +
                ", ack=" + response.getAck() +
                ", timestamp=" + response.getTimestamp() +
                ", errors=[" + b + "]");
    }

    private PaymentDetailsType createPaymentDetails(Order order, String orderURL) {
        final List<PaymentDetailsItemType> res = new ArrayList<>();

        final CurrencyCodeType usd = CurrencyCodeType.USD;
        final List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            final PaymentDetailsItemType item = new PaymentDetailsItemType();
            item.setName(orderItem.getName());
            item.setNumber(orderItem.getCode());
            item.setItemWeight(new MeasureType("кг", (double) orderItem.getWeight()));
            item.setQuantity(orderItem.getQuantity());
            item.setAmount(new BasicAmountType(usd, String.valueOf(orderItem.getAmount())));
            item.setDescription(orderItem.getOptions());

            res.add(item);
        }

        final PaymentDetailsType paymentDetails = new PaymentDetailsType();
        paymentDetails.setOrderURL(orderURL);
        paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);
        paymentDetails.setPaymentDetailsItem(res);
        paymentDetails.setOrderTotal(new BasicAmountType(usd, String.valueOf(order.getTotalAmount())));
        paymentDetails.setShippingTotal(new BasicAmountType(usd, String.valueOf(order.getShipment())));

        return paymentDetails;
    }


    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setTransactionManager(PayPalTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
