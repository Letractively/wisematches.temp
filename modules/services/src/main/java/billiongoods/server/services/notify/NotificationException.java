package billiongoods.server.services.notify;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationException extends Exception {
	public NotificationException(String message) {
		super(message);
	}

	public NotificationException(Throwable cause) {
		super(cause);
	}

	public NotificationException(String message, Throwable cause) {
		super(message, cause);
	}
}
