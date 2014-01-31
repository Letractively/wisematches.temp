package billiongoods.server.services.notify;

import java.io.Serializable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Notification implements Serializable {
	private final long id;
	private final long timestamp;
	private final String code;
	private final String subject;
	private final String message;
	private final Sender sender;
	private final Recipient recipient;

	public Notification(long id, String code, String subject, String message, Recipient recipient, Sender sender) {
		this.id = id;
		this.timestamp = System.currentTimeMillis();
		this.code = code;
		this.subject = subject;
		this.message = message;
		this.recipient = recipient;
		this.sender = sender;
	}

	public long getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getCode() {
		return code;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public Sender getSender() {
		return sender;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Notification");
		sb.append("{id=").append(id);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", code='").append(code).append('\'');
		sb.append(", subject='").append(subject).append('\'');
		sb.append(", message='").append(message).append('\'');
		sb.append(", recipient=").append(recipient);
		sb.append(", sender=").append(sender);
		sb.append('}');
		return sb.toString();
	}
}
