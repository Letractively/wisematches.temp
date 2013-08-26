package billiongoods.server.services.price;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationSummary {
	Date getStartDate();

	Date getFinishDate();

	int getValidatedArticles();

	List<PriceRenewal> getPriceRenewals();

	List<PriceBreakdown> getPriceBreakdowns();
}
