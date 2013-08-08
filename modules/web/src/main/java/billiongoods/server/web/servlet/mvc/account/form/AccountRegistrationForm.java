/*
 * Copyright (c) 2010, BillionGoods.
 */

package billiongoods.server.web.servlet.mvc.account.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountRegistrationForm {
	@NotEmpty(message = "account.register.email.err.blank")
	@Length(max = 150, message = "account.register.email.err.max")
	@Email(message = "account.register.email.err.format")
	private String email;

	@NotEmpty(message = "account.register.username.err.blank")
	@Length(max = 100, message = "account.register.username.err.max")
	private String username;

	@NotEmpty(message = "account.register.pwd.err.blank")
	@Length(max = 100, message = "account.register.pwd.err.max")
	private String password;

	@NotEmpty(message = "account.register.pwd-cfr.err.blank")
	@Length(max = 100, message = "account.register.pwd-cfr.err.max")
	private String confirm;

	private boolean rememberMe = true;

	public AccountRegistrationForm() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("AccountRegistrationForm");
		sb.append("{email='").append(email).append('\'');
		sb.append(", username='").append(username).append('\'');
		sb.append(", rememberMe=").append(rememberMe);
		sb.append('}');
		return sb.toString();
	}
}