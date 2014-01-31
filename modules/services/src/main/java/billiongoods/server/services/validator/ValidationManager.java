package billiongoods.server.services.validator;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationManager {
	void addValidationListener(ValidationListener l);

	void removeValidationListener(ValidationListener l);


	void startValidation();

	void cancelValidation();


	void validateBroken();

	void validateExchangeRate();


	boolean isInProgress();


	ValidationSummary getValidationSummary();
}
