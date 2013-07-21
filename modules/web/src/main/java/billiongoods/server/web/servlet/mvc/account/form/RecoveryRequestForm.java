/*
 * Copyright (c) 2010, BillionGoods.
 */

package billiongoods.server.web.servlet.mvc.account.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class RecoveryRequestForm {
    @Email(message = "account.register.email.err.format")
    @Length(max = 150, message = "account.register.email.err.max")
    private String email;
    private boolean recoveryAccount = false;

    public RecoveryRequestForm() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRecoveryAccount() {
        return recoveryAccount;
    }

    public void setRecoveryAccount(boolean recoveryAccount) {
        this.recoveryAccount = recoveryAccount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RecoveryRequestForm");
        sb.append("{email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
