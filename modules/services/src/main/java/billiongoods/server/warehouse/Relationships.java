package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Relationships {
	/**
	 * Returns associations by specified group type.
	 *
	 * @return the associations by specified group type.
	 */
	List<ArticleDescription> getAssociations(RelationshipType type);
}
