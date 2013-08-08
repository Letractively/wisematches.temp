package billiongoods.server.services.payment.impl;

import org.junit.Test;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalOrderManagerTest {
    public PayPalOrderManagerTest() {
    }

    @Test
    public void test() throws Exception {
//      https://devtools-paypal.com/
//      https://www.paypalobjects.com/webstatic/en_US/developer/docs/pdf/pp_expresscheckout_advancedfeaturesguide.pdf
//      https://developer.paypal.com/webapps/developer/docs/classic/products/

        final PaymentDetailsType paymentDetails = new PaymentDetailsType();
        paymentDetails.setPaymentAction(PaymentActionCodeType.ORDER);

        final PaymentDetailsItemType item = new PaymentDetailsItemType();
        item.setName("item");
        item.setQuantity(12);
        item.setAmount(new BasicAmountType(CurrencyCodeType.USD, "12.23"));


        final List<PaymentDetailsItemType> lineItems = new ArrayList<>();
        lineItems.add(item);
        paymentDetails.setPaymentDetailsItem(lineItems);

        final BasicAmountType orderTotal = new BasicAmountType();
        orderTotal.setValue("146.76");
        orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
        paymentDetails.setOrderTotal(orderTotal);

        final List<PaymentDetailsType> paymentDetailsList = new ArrayList<>();
        paymentDetailsList.add(paymentDetails);

        SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
        setExpressCheckoutRequestDetails.setReturnURL("http://www.billiongoods.ru/warehouse/order/accepted");
        setExpressCheckoutRequestDetails.setCancelURL("http://www.billiongoods.ru/warehouse/order/rejected");

        setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);

        final SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
        setExpressCheckoutRequest.setVersion("104.0");

        final SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
        setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

        final Map<String, String> sdkConfig = new HashMap<>();
        sdkConfig.put("mode", "sandbox");
        sdkConfig.put("acct1.UserName", "sandbox_api1.billiongoods.ru");
        sdkConfig.put("acct1.Password", "1375949959");
        sdkConfig.put("acct1.Signature", "AgHNIFX28Hy.bnTjAvRtEzymKrv4AEkCZeSgSOQ0BSztEJZ4hpOMZRf3");


        final PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);

        final SetExpressCheckoutResponseType setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);

        System.out.println(setExpressCheckoutResponse.getAck());

        System.out.println("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=" + setExpressCheckoutResponse.getToken());

        System.out.println(setExpressCheckoutResponse);

    }
}
