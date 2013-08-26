package billiongoods.server.services.price;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceValidation {
	Date getTimestamp();

	Integer getArticleId();
}
