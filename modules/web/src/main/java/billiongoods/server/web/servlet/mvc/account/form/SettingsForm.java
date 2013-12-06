package billiongoods.server.web.servlet.mvc.account.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SettingsForm {
	private String username;
	private String timeZone;

	public SettingsForm() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
}
