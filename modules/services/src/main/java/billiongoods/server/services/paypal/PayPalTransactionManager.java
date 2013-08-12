package billiongoods.server.services.paypal;

import billiongoods.server.services.payment.Order;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PayPalTransactionManager {
	PayPalTransaction getTransaction(Long id);


	PayPalTransaction beginTransaction(Order order);

	void checkoutInitiated(PayPalTransaction tnxId, SetExpressCheckoutResponseType response);

	void checkoutValidated(PayPalTransaction tnxId, GetExpressCheckoutDetailsResponseType response);

	void checkoutConfirmed(PayPalTransaction tnxId, DoExpressCheckoutPaymentResponseType response);

	void commitTransaction(PayPalTransaction tnxId, TransactionResolution resolution);


	Long registerMessage(PayPalMessage message);
}
