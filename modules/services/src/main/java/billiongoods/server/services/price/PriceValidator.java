package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceValidator {
	void addPriceValidatorListener(PriceValidatorListener l);

	void removePriceValidatorListener(PriceValidatorListener l);


	void startPriceValidation();

	void stopPriceValidation();

	boolean isInProgress();

	ValidationSummary getValidationSummary();
}
