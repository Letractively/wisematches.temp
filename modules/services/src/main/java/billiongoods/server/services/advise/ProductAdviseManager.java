package billiongoods.server.services.advise;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductPreview;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductAdviseManager {
	void addRecommendation(Integer pid);

	void removeRecommendation(Integer pid);


	List<ProductPreview> getRecommendations();

	List<ProductPreview> getRecommendations(Category category, int count);


	void reloadRecommendations();
}