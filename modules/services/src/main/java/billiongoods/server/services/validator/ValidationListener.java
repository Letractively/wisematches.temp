package billiongoods.server.services.validator;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationListener {
	void validationStarted(ValidationSummary summary);

	void validationFinished(ValidationSummary summary, List<ValidatingProduct> brokenValidations);

	void validationProcessed(ValidationChange validation);
}
