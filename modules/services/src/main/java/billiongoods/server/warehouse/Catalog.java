package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Catalog {
	Category getCategory(Integer id);

	List<Category> getRootCategories();
}
