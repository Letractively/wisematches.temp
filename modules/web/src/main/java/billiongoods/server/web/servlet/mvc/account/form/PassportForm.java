package billiongoods.server.web.servlet.mvc.account.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PassportForm {
	private String username;
	private String timeZone;
	private String language;

	public PassportForm() {
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
