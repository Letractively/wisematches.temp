package billiongoods.server.services;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class ServerDescriptor {
	private final String webHostName;
	private final String mailHostName;

	public ServerDescriptor(String webHostName, String mailHostName) {
		this.webHostName = webHostName;
		this.mailHostName = mailHostName;
	}

	public String getWebHostName() {
		return webHostName;
	}

	public String getMailHostName() {
		return mailHostName;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ServerDescriptor");
		sb.append("{webHostName='").append(webHostName).append('\'');
		sb.append(", mailHostName='").append(mailHostName).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
