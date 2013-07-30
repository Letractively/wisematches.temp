package billiongoods.server.web.validation;

import org.springframework.context.MessageSource;

import javax.validation.MessageInterpolator;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SpringMessageInterpolator implements MessageInterpolator {
    private MessageSource messageSource;

    public SpringMessageInterpolator() {
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return messageSource.getMessage(messageTemplate, null, null);
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return messageSource.getMessage(messageTemplate, null, locale);
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
