package billiongoods.server.services.price;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceValidatorListener {
	void priceValidationStarted(Date date, int articlesCount);

	void priceValidated(Integer articleId, PriceRenewal renewal);

	void priceValidationFinished(Date date, int checkedArticled, List<PriceRenewal> renewals);
}
