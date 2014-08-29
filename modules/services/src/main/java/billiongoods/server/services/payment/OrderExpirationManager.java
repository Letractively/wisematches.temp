package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderExpirationManager {
	void addOrderExpirationListener(OrderExpirationListener l);

	void removeOrderExpirationListener(OrderExpirationListener l);
}
