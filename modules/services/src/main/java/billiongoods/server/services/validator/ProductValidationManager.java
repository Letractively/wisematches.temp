package billiongoods.server.services.validator;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductValidationManager {
	void addValidationProgressListener(ValidationProgressListener l);

	void removeValidationProgressListener(ValidationProgressListener l);


	void startPriceValidation();

	void stopPriceValidation();

	boolean isInProgress();


	ValidationSummary getValidationSummary();
}
