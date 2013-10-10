package billiongoods.server.services.validator;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationProgressListener {
	void validationStarted(Date date, int totalCount);

	void productValidated(Integer productId, ProductValidation validation);

	void validationFinished(Date date, ValidationSummary summary);
}
