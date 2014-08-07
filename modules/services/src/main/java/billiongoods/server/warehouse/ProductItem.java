package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductItem {
	int getQuantity();

	double getAmount();

	ProductPreview getProduct();
}
