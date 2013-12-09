package billiongoods.server.web.servlet.mvc.account.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PasswordForm {
	private String password;
	private String confirm;

	public PasswordForm() {
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
}
