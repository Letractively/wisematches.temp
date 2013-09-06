package billiongoods.server.services.price;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceValidatorListener {
	void priceValidationStarted(Date date, int totalCount);

	void priceValidated(Integer productId, PriceRenewal renewal);

	void priceValidationFinished(Date date, ValidationSummary summary);
}
