package billiongoods.server.services.arivals;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Property;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleImporter {
	ImportingSummary getImportingSummary();

	ImportingSummary importArticles(Category category, List<Property> properties, List<Integer> groups,
									InputStream descStream, InputStream imagesStream,
									boolean validatePrice) throws IOException;
}
