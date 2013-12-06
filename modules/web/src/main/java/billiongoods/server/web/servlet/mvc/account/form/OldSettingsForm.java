package billiongoods.server.web.servlet.mvc.account.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OldSettingsForm {
	@Length(max = 150, message = "account.register.email.err.max")
	@Email(message = "account.register.email.err.format")
	private String email;

	@Length(max = 100, message = "account.register.pwd.err.max")
	private String password;

	@Length(max = 100, message = "account.register.pwd-cfr.err.max")
	private String confirm;

	private boolean changeEmail = false;

	private boolean changePassword = false;

	private String tab = "tabCommon";

	public OldSettingsForm() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public boolean isChangeEmail() {
		return changeEmail;
	}

	public void setChangeEmail(boolean changeEmail) {
		this.changeEmail = changeEmail;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}
}
