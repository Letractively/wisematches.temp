package billiongoods.server.services.advise;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductDescription;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductAdviseManager {
	void addRecommendation(Integer pid);

	void removeRecommendation(Integer pid);


	List<ProductDescription> getRecommendations();

	List<ProductDescription> getRecommendations(Category category, int count);


	void reloadRecommendations();
}