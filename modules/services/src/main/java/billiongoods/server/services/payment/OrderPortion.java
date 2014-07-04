package billiongoods.server.services.payment;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderPortion {
	Long getId();


	Date getTimestamp();

	OrderState getOrderState();


	int getItemsCount();

	List<OrderItem> getOrderItems();


	String getRefundToken();

	Date getExpectedResume();

	String getReferenceTracking();

	String getChinaMailTracking();

	String getInternationalTracking();
}
