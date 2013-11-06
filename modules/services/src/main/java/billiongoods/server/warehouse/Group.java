package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Group {
	Integer getId();

	String getName();

	GroupType getType();

	Integer getCategoryId();

	List<ProductPreview> getProductPreviews();
}
