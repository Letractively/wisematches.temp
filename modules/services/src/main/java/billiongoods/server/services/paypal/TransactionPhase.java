package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum TransactionPhase {
    CREATED,
    INVOICING, // SetExpressCheckout sent and answer received
    VERIFICATION, // GetExpressCheckout sent and answer received
    CONFIRMATION, // DoExpressCheckout sent and answer received
    FINISHED
}
