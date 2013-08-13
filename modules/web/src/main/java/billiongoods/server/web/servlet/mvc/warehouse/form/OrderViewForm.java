package billiongoods.server.web.servlet.mvc.warehouse.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderViewForm {
    private Long order;
    private String token;
    private TokenType tokenType;

    public OrderViewForm() {
    }

    public OrderViewForm(Long order, String token, TokenType tokenType) {
        this.order = order;
        this.token = token;
        this.tokenType = tokenType;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isEmpty() {
        return tokenType == null;
    }

    public static enum TokenType {
        EMAIL,
        TOKEN
    }

    @Override
    public String toString() {
        return "OrderViewForm{" +
                "order=" + order +
                ", token='" + token + '\'' +
                ", tokenType=" + tokenType +
                '}';
    }
}
