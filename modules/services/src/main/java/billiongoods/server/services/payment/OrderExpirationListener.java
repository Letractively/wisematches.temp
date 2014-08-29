package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderExpirationListener {
	void orderExpiring(Order order);
}
