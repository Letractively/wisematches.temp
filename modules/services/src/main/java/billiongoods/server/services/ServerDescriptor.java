package billiongoods.server.services;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class ServerDescriptor {
    private final String webHostName;
    private final String mailHostName;
    private final String alertsMailBox;

    public ServerDescriptor(String webHostName, String mailHostName, String alertsMailBox) {
        this.webHostName = webHostName;
        this.mailHostName = mailHostName;
        this.alertsMailBox = alertsMailBox;
    }

    public String getWebHostName() {
        return webHostName;
    }

    public String getMailHostName() {
        return mailHostName;
    }

    public String getAlertsMailBox() {
        return alertsMailBox;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ServerDescriptor");
        sb.append("{webHostName='").append(webHostName).append('\'');
        sb.append(", mailHostName='").append(mailHostName).append('\'');
        sb.append(", alertsMailBox='").append(alertsMailBox).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
