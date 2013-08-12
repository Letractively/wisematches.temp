package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderState {
	NEW, // nothing
	BILLING, // PayPal token here
	REJECTED, // manualReason here
	ACCEPTED, // email here
	PROCESSING, // BangGood internal number here
	SHIPPING, // China post tracking number here
	SHIPPED, // International tracking number here if possible
	FAILED // comment here must be
}
