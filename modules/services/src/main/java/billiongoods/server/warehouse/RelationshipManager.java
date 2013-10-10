package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface RelationshipManager {
	Group getGroup(Integer id);

	Group removeGroup(Integer id);


	Group createGroup(String name, Integer categoryId);

	Group updateGroup(Integer id, String name, Integer categoryId);


	List<Group> searchGroups(String name);

	List<Group> searchGroups(Integer categoryId);


	List<Group> getGroups(Integer productId);


	Group addGroupItem(Integer groupId, Integer productId);

	Group removeGroupItem(Integer groupId, Integer productId);


	List<Relationship> getRelationships(Integer productId);

	void addRelationship(Integer productId, Integer groupId, RelationshipType type);

	void removeRelationship(Integer productId, Integer groupId, RelationshipType type);
}
