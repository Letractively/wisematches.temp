package billiongoods.server.services.payment;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderLog {
	Date getTimeStamp();

	String getParameter();

	String getCommentary();


	Long getOrderId();

	boolean isOrderChange();

	OrderState getOrderState();


	Long getParcelId();

	boolean isParcelChange();

	ParcelState getParcelState();
}
