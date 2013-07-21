package billiongoods.server.web.servlet.mvc.account.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class RecoveryConfirmationForm {
    @Length(max = 150, message = "account.register.email.err.max")
    @Email(message = "account.register.email.err.format")
    private String email;

    @Length(max = 100, message = "account.register.pwd.err.max")
    private String password;

    @Length(max = 100, message = "account.register.pwd-cfr.err.max")
    private String confirm;

    private String token;

    private boolean rememberMe = true;

    private boolean recoveryAccount = false;

    public RecoveryConfirmationForm() {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public boolean isRecoveryAccount() {
        return recoveryAccount;
    }

    public void setRecoveryAccount(boolean recoveryAccount) {
        this.recoveryAccount = recoveryAccount;
    }

    @Override
    public String toString() {
        return "RecoveryConfirmationForm{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirm='" + confirm + '\'' +
                ", token='" + token + '\'' +
                ", rememberMe=" + rememberMe +
                ", recoveryAccount=" + recoveryAccount +
                '}';
    }
}
