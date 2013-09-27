package billiongoods.server.services.supplier;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DataLoadingException extends Exception {
	public DataLoadingException(String message) {
		super(message);
	}

	public DataLoadingException(String message, Throwable cause) {
		super(message, cause);
	}
}
