package billiongoods.server.services.payment;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderLog {
	Date getTimeStamp();

	String getParameter();

	String getCommentary();

	OrderState getOrderState();
}
