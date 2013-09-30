package billiongoods.server.services.payment;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderLog {
	String getCode();

	Date getTimeStamp();

	String getParameter();

	String getCommentary();

	OrderState getOrderState();
}
