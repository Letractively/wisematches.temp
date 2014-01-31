package billiongoods.server.services.validator;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationListener {
	void validationStarted(ValidationSummary summary);

	void validationProcessed(ValidationChange validation);

	void validationFinished(ValidationSummary summary);
}
