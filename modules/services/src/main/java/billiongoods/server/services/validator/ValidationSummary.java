package billiongoods.server.services.validator;

import java.util.Collection;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationSummary {
	Date getStartDate();

	Date getFinishDate();


	int getIteration();


	int getTotalCount();

	int getProcessedProducts();


	Collection<ValidationChange> getUpdatedProducts();

	Collection<ValidatingProduct> getBrokenProducts();
}
