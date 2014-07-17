package billiongoods.server.services.payment;

import java.time.temporal.Temporal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderLog {
	String getParameter();

	String getCommentary();


	Long getOrderId();

	boolean isOrderChange();

	OrderState getOrderState();


	Long getParcelId();

	boolean isParcelChange();

	ParcelState getParcelState();


	Temporal getTimeStamp();
}