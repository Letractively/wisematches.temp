package billiongoods.server.services.paypal;

import urn.ebay.apis.eBLBaseComponents.AbstractResponseType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class PayPalQueryError {
    @Column(name = "errorAck")
    private String ack;

    @Column(name = "errorCode")
    private String code;

    @Column(name = "errorSeverity")
    private String severity;

    @Column(name = "shortMessage")
    private String shortMessage;

    @Column(name = "longMessage")
    private String longMessage;

    @Deprecated
    PayPalQueryError() {
    }

    public PayPalQueryError(ErrorType errorType) {
        this(null, errorType.getErrorCode(), errorType.getSeverityCode().getValue(), errorType.getShortMessage(), errorType.getLongMessage());
    }

    public PayPalQueryError(AbstractResponseType response) {
        this(response.getErrors() != null && response.getErrors().size() > 0 ? response.getErrors().get(0) : new ErrorType());
        this.ack = response.getAck().getValue();
    }

    public PayPalQueryError(String ack, String code, String severity, String shortMessage, String longMessage) {
        this.ack = ack;
        this.code = code;
        this.severity = severity;
        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
    }

    public String getAck() {
        return ack;
    }

    public String getCode() {
        return code;
    }

    public String getSeverity() {
        return severity;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getLongMessage() {
        return longMessage;
    }

    @Override
    public String toString() {
        return "PayPalQueryError{" +
                "ack='" + ack + '\'' +
                ", code='" + code + '\'' +
                ", severity='" + severity + '\'' +
                ", shortMessage='" + shortMessage + '\'' +
                ", longMessage='" + longMessage + '\'' +
                '}';
    }
}
