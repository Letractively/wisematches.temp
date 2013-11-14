package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderState {
	// Initial states
	NEW, // nothing
	BILLING, // PayPal token here

	// Working states
	ACCEPTED, // email here
	PROCESSING, // BangGood internal number here
	SHIPPING, // China post tracking number here
	SHIPPED, // International tracking number here if possible

	// Something wrong states
	FAILED, // comment here must be
	SUSPENDED, // indicates that order was suspended by any reason (must be in comment)

	// Final states
	CANCELLED,  // indicates that order was cancelled by any reason (must be in comment)
	CLOSED,    // indicates that order was processed, shipped and received by customer.
	REMOVED; // indicates that order was removed by customer

	private final String code;

	OrderState() {
		this.code = name().toLowerCase();
	}

	public String getCode() {
		return code;
	}

	public boolean isNew() {
		return this == NEW;
	}

	public boolean isBilling() {
		return this == BILLING;
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

	public boolean isSuspended() {
		return this == SUSPENDED;
	}

	public boolean isCancelled() {
		return this == CANCELLED;
	}

	public boolean isClosed() {
		return this == CLOSED;
	}
}
