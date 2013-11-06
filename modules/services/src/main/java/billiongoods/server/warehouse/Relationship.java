package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Relationship {
	Group getGroup();

	RelationshipType getType();

	List<ProductPreview> getProductPreviews();
}
