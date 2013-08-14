package billiongoods.server.web.servlet.mvc.warehouse.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderViewForm {
    private Long order;
    private String email;
    private boolean payPalRedirect;

    public OrderViewForm() {
    }

    public OrderViewForm(Long order, String email) {
        this(order, email, false);
    }

    public OrderViewForm(Long order, String email, boolean payPalRedirect) {
        this.order = order;
        this.email = email;
        this.payPalRedirect = payPalRedirect;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPayPalRedirect() {
        return payPalRedirect;
    }

    public void setPayPalRedirect(boolean payPalRedirect) {
        this.payPalRedirect = payPalRedirect;
    }

    public boolean isEmpty() {
        return order == null && email == null;
    }

    @Override
    public String toString() {
        return "OrderViewForm{" +
                "order=" + order +
                ", email='" + email + '\'' +
                '}';
    }
}
