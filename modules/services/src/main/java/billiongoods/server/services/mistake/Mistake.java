package billiongoods.server.services.mistake;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Mistake {
	Integer getId();

	Date getCreated();

	Date getResolved();


	Integer getProductId();

	String getDescription();

	MistakeScope getScope();
}
