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

    @NotEmpty(message = "account.register.nickname.err.blank")
    @Length(max = 100, message = "account.register.nickname.err.max")
    private String nickname;

    @NotEmpty(message = "account.register.pwd.err.blank")
    @Length(max = 100, message = "account.register.pwd.err.max")
    private String password;

    @NotEmpty(message = "account.register.pwd-cfr.err.blank")
    @Length(max = 100, message = "account.register.pwd-cfr.err.max")
    private String confirm;

    @NotEmpty(message = "account.register.language.err.blank")
    private String language;

    @NotEmpty(message = "account.register.timezone.err.blank")
    private String timezone;

    private boolean rememberMe = true;

    public AccountRegistrationForm() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", language='").append(language).append('\'');
        sb.append(", timezone='").append(timezone).append('\'');
        sb.append(", rememberMe=").append(rememberMe);
        sb.append('}');
        return sb.toString();
    }
}