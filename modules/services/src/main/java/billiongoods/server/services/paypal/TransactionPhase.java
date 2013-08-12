package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum TransactionPhase {
    NEW,
    INITIATED,
    VALIDATED,
    CONFIRMED,
    DONE
}
