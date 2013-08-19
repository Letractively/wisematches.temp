package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface RelationshipManager {
	Group createGroup(String name);

	Group deleteGroup(Integer id);

	List<Group> getGroups(Integer articleId);

	Group addGroupItem(Integer groupId, Integer article);

	Group removeGroupItem(Integer groupId, Integer article);


	Relationships getRelationships(ArticleDescription description);

	void changeRelationship(Integer articleId, RelationshipType type, Integer groupId);
}
