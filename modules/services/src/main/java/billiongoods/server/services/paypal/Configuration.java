package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Configuration {
    private String user;
    private String password;
    private String signature;
    private Environment environment;

    public Configuration(String user, String password, String signature, Environment environment) {
        this.user = user;
        this.password = password;
        this.signature = signature;
        this.environment = environment;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getSignature() {
        return signature;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
