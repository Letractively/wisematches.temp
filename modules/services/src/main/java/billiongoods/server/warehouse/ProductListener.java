package billiongoods.server.warehouse;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductListener {
	void productCreated(Product product);

	void productRemoved(Product product);

	void productUpdated(Product product, Set<String> updatedFields);
}
