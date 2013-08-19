package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.RelationshipType;
import billiongoods.server.warehouse.Relationships;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultRelationships implements Relationships {
	private Map<RelationshipType, List<ArticleDescription>> listMap = new HashMap<>();

	public DefaultRelationships(List<HibernateRelationship> relationships) {
		for (HibernateRelationship relationship : relationships) {
			listMap.put(relationship.getRelationshipType(), relationship.getGroup().getDescriptions());
		}
	}

	@Override
	public List<ArticleDescription> getAssociations(RelationshipType type) {
		return listMap.get(type);
	}
}
