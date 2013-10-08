package billiongoods.server.services.validator;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductValidationListener {
	void productValidationStarted(Date date, int totalCount);

	void productValidated(Integer productId, ProductValidation validation);

	void productValidationFinished(Date date, ValidationSummary summary);
}
