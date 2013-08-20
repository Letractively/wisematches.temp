package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface RelationshipManager {
	Group getGroup(Integer id);

	Group createGroup(String name);

	Group removeGroup(Integer id);

	Group updateGroup(Integer id, String name);


	List<Group> getGroups(Integer articleId);

	Group addGroupItem(Integer groupId, Integer article);

	Group removeGroupItem(Integer groupId, Integer article);


	List<Relationship> getRelationships(Integer articleId);

	void addRelationship(Integer articleId, Integer groupId, RelationshipType type);

	void removeRelationship(Integer articleId, Integer groupId, RelationshipType type);
}
