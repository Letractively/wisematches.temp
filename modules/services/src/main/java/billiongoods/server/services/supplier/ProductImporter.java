package billiongoods.server.services.supplier;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Property;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductImporter {
	ImportingSummary getImportingSummary();

	ImportingSummary importProducts(Category category, List<Property> properties, List<Integer> groups,
									InputStream descStream, InputStream imagesStream,
									boolean validatePrice) throws IOException;
}
