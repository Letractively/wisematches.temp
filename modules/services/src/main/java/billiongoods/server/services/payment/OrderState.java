package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderState {
	// Initial states
	NEW(false), // nothing
	BILLING(false), // PayPal token here

	// Working states
	ACCEPTED(false), // email here
	PROCESSING(false), // BangGood internal number here
	SHIPPING(false), // China post tracking number here
	SHIPPED(false), // International tracking number here if possible

	// Something wrong states
	FAILED(true), // comment here must be
	SUSPENDED(false), // indicates that order was suspended by any reason (must be in comment)

	// Final states
	CANCELLED(true),  // indicates that order was cancelled by any reason (must be in comment)
	CLOSED(true),    // indicates that order was processed, shipped and received by customer.
	REMOVED(true); // indicates that order was removed by customer

	private final String code;
	private final boolean finalState;

	OrderState(boolean finalState) {
		this.code = name().toLowerCase();
		this.finalState = finalState;
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

	public boolean isFinalState() {
		return finalState;
	}
}
