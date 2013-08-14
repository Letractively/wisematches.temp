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
	FAILED; // comment here must be

	public boolean isNew() {
		return this == NEW;
	}

	public boolean isBilling() {
		return this == BILLING;
	}

	public boolean isRejected() {
		return this == REJECTED;
	}

	public boolean isAccepted() {
		return this == ACCEPTED;
	}

	public boolean isProcessing() {
		return this == PROCESSING;
	}

	public boolean isShipping() {
		return this == SHIPPING;
	}

	public boolean isShipped() {
		return this == SHIPPED;
	}

	public boolean isFailed() {
		return this == FAILED;
	}
}
