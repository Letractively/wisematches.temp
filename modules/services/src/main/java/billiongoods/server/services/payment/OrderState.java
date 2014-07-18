package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderState {
	/**
	 * New order. Can be only in case of error
	 */
    NEW(false), // 0
    /**
	 * Paypal token has been created
	 */
    BILLING(false), // 1

	// Working states
	/**
	 * Was paid and ready to process
	 */
    ACCEPTED(false), // 2
    /**
	 * In processing state. Appropriate order was created on supplier side.
	 */
    PROCESSING(false), // 3 BangGood internal number here
    SHIPPING(false), // 4 China post tracking number here
    SHIPPED(false), // 5 International tracking number here if possible

	// Something wrong states
    FAILED(true), // 6 comment here must be
    SUSPENDED(false), // 7 indicates that order was suspended by any reason (must be in comment)

	// Final states
    CANCELLED(true),  // 8 indicates that order was cancelled by any reason (must be in comment)
    CLOSED(true);    // 9 indicates that order was processed, shipped and received by customer.

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
