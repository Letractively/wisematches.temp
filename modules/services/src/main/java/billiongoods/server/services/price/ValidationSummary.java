package billiongoods.server.services.price;

import java.util.Collection;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationSummary {
	Date getStartDate();

	Date getFinishDate();

	int getValidatedProducts();

	Collection<PriceRenewal> getPriceRenewals();

	Collection<PriceBreakdown> getPriceBreakdowns();
}
