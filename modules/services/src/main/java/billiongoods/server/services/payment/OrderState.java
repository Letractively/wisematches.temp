package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderState {
	NEW,
	VERIFIED,
	REJECTED,
	PLACED,
	PACKING,
	SHIPPED,
	DELIVERED
}
