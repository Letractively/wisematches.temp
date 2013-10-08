package billiongoods.server.services.validator;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductValidationManager {
	void addPriceValidatorListener(ProductValidationListener l);

	void removePriceValidatorListener(ProductValidationListener l);


	void startPriceValidation();

	void stopPriceValidation();

	boolean isInProgress();

	ValidationSummary getValidationSummary();
}
