package billiongoods.server.services.notify;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TransformationException extends NotificationException {
	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(Throwable cause) {
		super(cause);
	}

	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}
}
