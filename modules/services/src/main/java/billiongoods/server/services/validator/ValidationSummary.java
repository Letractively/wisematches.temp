package billiongoods.server.services.validator;

import java.util.Collection;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationSummary {
	Date getStartDate();

	Date getFinishDate();


	int getValidProducts();

	int getBrokenProducts();


	int getValidatedProducts();

	Collection<ProductValidation> getProductValidations();
}
