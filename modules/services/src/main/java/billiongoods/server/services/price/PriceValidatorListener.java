package billiongoods.server.services.price;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceValidatorListener {
	void priceValidationStarted(Date date, int articlesCount);

	void priceValidated(Integer articleId, PriceRenewal renewal);

	void priceValidationFinished(Date date, ValidationSummary summary);
}
