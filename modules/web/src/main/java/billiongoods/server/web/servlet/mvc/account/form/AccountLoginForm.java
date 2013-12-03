package billiongoods.server.web.servlet.mvc.account.form;

import java.io.Serializable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountLoginForm implements Serializable {
	public String j_username;
	public String j_password;
	public String rememberMe = "true";
	public String error;

	public AccountLoginForm() {
	}

	public String getJ_username() {
		return j_username;
	}

	public void setJ_username(String j_username) {
		this.j_username = j_username;
	}

	public String getJ_password() {
		return j_password;
	}

	public void setJ_password(String j_password) {
		this.j_password = j_password;
	}

	public String getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(String rememberMe) {
		this.rememberMe = rememberMe;
	}

	public boolean hasUsername() {
		return j_username != null && j_username.trim().length() != 0;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("AccountLoginForm");
		sb.append("{j_username='").append(j_username).append('\'');
		sb.append("{j_password='").append("<<PROHIBITED>>").append('\'');
		sb.append(", rememberMe='").append(rememberMe).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
