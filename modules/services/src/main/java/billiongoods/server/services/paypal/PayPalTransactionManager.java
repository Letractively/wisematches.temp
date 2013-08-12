package billiongoods.server.services.paypal;

import billiongoods.server.services.payment.Order;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;

import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PayPalTransactionManager {
    PayPalTransaction getTransaction(Long id);


    Long beginTransaction(Order order);

    void checkoutInitiated(Long tnxId, SetExpressCheckoutResponseType response);

    void checkoutValidated(Long tnxId, GetExpressCheckoutDetailsResponseType response);

    void checkoutConfirmed(Long tnxId, DoExpressCheckoutPaymentResponseType response);

    void commitTransaction(Long tnxId, TransactionResolution resolution);


    Long registerMessage(Map<String, String[]> parameterMap);
}
