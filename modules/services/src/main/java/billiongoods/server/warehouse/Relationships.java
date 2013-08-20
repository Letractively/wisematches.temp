package billiongoods.server.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Relationships {
	private Map<RelationshipType, List<ArticleDescription>> articlesMap = new HashMap<>();

	public Relationships(List<Relationship> relationships) {
		for (Relationship relationship : relationships) {
			final RelationshipType type = relationship.getType();

			List<ArticleDescription> descriptions = articlesMap.get(type);
			if (descriptions == null) {
				descriptions = new ArrayList<>();
				articlesMap.put(type, descriptions);
			}
			descriptions.addAll(relationship.getDescriptions());
		}
	}

	public List<ArticleDescription> getAssociations(RelationshipType type) {
		return articlesMap.get(type);
	}
}
