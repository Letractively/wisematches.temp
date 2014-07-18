package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderState {
	// Initial states
	/**
	 * New order. Can be only in case of error
	 */
	NEW(false),
	/**
	 * Paypal token has been created
	 */
	BILLING(false),

	// Working states
	/**
	 * Was paid and ready to process
	 */
	ACCEPTED(false), // email here
	/**
	 * In processing state. Appropriate order was created on supplier side.
	 */
	PROCESSING(false), // BangGood internal number here
	SHIPPING(false), // China post tracking number here
	SHIPPED(false), // International tracking number here if possible

	// Something wrong states
	FAILED(true), // comment here must be
	SUSPENDED(false), // indicates that order was suspended by any reason (must be in comment)

	// Final states
	CANCELLED(true),  // indicates that order was cancelled by any reason (must be in comment)
	CLOSED(true);    // indicates that order was processed, shipped and received by customer.

	private final String code;
	private final boolean finished;

	OrderState(boolean finished) {
		this.code = name().toLowerCase();
		this.finished = finished;
	}

	public String getCode() {
		return code;
	}

	public boolean isFinished() {
		return finished;
	}
}
