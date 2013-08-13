package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.paypal.PayPalException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderErrorForm {
    private String token;
    private PayPalException exception;

    public OrderErrorForm() {
    }

    public OrderErrorForm(String token, PayPalException exception) {
        this.token = token;
        this.exception = exception;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PayPalException getException() {
        return exception;
    }

    public void setException(PayPalException exception) {
        this.exception = exception;
    }
}
