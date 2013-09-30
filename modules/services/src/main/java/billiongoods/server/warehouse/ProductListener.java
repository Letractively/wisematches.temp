package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductListener {
	void productCreated(Product product);

	void productUpdated(Product product);

	void productRemoved(Product product);
}
