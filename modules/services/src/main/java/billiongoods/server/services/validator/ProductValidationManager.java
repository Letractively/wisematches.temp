package billiongoods.server.services.validator;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductValidationManager {
	void addValidationProgressListener(ValidationProgressListener l);

	void removeValidationProgressListener(ValidationProgressListener l);


	boolean isInProgress();


	void startPriceValidation();

	void stopPriceValidation();


	ValidationSummary getValidationSummary();
}
