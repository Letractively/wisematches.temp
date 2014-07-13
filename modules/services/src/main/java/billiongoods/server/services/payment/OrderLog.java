package billiongoods.server.services.payment;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderLog {
    Long getParcelId();

    Date getTimeStamp();

    String getParameter();

    String getCommentary();


    boolean isOrderChange();

    OrderState getOrderState();


    boolean isParcelChange();

    ParcelState getParcelState();
}
