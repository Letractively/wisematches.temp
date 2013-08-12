package billiongoods.server.services.paypal;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PayPalTransaction {
    Long getId();

    Long getOrderId();

    Date getTimestamp();

    TransactionPhase getPhase();

    TransactionResolution getResolution();
}
