package billiongoods.server.services.price.impl;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PriceLoadingException extends Exception {
	public PriceLoadingException(String message) {
		super(message);
	}

	public PriceLoadingException(String message, Throwable cause) {
		super(message, cause);
	}
}
