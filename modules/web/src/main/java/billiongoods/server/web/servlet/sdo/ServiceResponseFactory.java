package billiongoods.server.web.servlet.sdo;

import billiongoods.core.MessageFormatter;

import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class ServiceResponseFactory {
    private final MessageFormatter messageSource;

    private static final ServiceResponse SUCCESS = new ServiceResponse(new ServiceResponse.Success(null));
    private static final ServiceResponse FAILURE = new ServiceResponse(new ServiceResponse.Failure(null));

    public ServiceResponseFactory(MessageFormatter messageSource) {
        this.messageSource = messageSource;
    }

    public ServiceResponse success() {
        return SUCCESS;
    }

    public ServiceResponse failure() {
        return FAILURE;
    }

    public ServiceResponse success(Object data) {
        return new ServiceResponse(new ServiceResponse.Success(data));
    }

    public ServiceResponse failure(Object data) {
        return new ServiceResponse(new ServiceResponse.Failure(data));
    }

    public ServiceResponse failure(String code, Locale locale) {
        return new ServiceResponse(new ServiceResponse.Failure(code, messageSource.getMessage(code, locale)));
    }

    public ServiceResponse failure(String code, Object[] args, Locale locale) {
        return new ServiceResponse(new ServiceResponse.Failure(code, messageSource.getMessage(code, args, locale)));
    }

}
