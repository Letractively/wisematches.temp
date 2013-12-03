package billiongoods.server.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Relationships {
	private Map<RelationshipType, List<ProductPreview>> productsMap = new HashMap<>();

	public Relationships(List<Relationship> relationships) {
		for (Relationship relationship : relationships) {
			final RelationshipType type = relationship.getType();

			List<ProductPreview> previews = productsMap.get(type);
			if (previews == null) {
				previews = new ArrayList<>();
				productsMap.put(type, previews);
			}
			previews.addAll(relationship.getProductPreviews());
		}
	}

	public List<ProductPreview> getAssociations(RelationshipType type) {
		return productsMap.get(type);
	}
}
