package billiongoods.server.services.validator;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationManager {
	void addValidationProgressListener(ValidationListener l);

	void removeValidationProgressListener(ValidationListener l);


	boolean isInProgress();


	void startPriceValidation();

	void stopPriceValidation();


	ValidationSummary getValidationSummary();
}
