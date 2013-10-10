package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Group {
	Integer getId();

	String getName();

	Integer getCategoryId();

	List<ProductDescription> getDescriptions();
}
