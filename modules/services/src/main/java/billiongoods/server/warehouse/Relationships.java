package billiongoods.server.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Relationships {
	private Map<RelationshipType, List<ProductDescription>> productsMap = new HashMap<>();

	public Relationships(List<Relationship> relationships) {
		for (Relationship relationship : relationships) {
			final RelationshipType type = relationship.getType();

			List<ProductDescription> descriptions = productsMap.get(type);
			if (descriptions == null) {
				descriptions = new ArrayList<>();
				productsMap.put(type, descriptions);
			}
			descriptions.addAll(relationship.getDescriptions());
		}
	}

	public List<ProductDescription> getAssociations(RelationshipType type) {
		return productsMap.get(type);
	}
}
