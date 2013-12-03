package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Environment {
    LIVE("https://www.paypal.com/cgi-bin/webscr"),
    SANDBOX("https://www.sandbox.paypal.com/cgi-bin/webscr");

    private final String endpoint;

    Environment(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCode() {
        return name().toLowerCase();
    }

    public String getPayPalEndpoint() {
        return endpoint;
    }
}
