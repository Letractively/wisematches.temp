package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductManager extends SearchManager<ProductDescription, ProductContext, ProductFilter> {
	void addProductListener(ProductListener l);

	void removeProductListener(ProductListener l);


	Product getProduct(Integer id);

	Product getProduct(String sku);

	boolean hasProduct(Integer productId);

	ProductDescription getDescription(Integer id);


	SupplierInfo getSupplierInfo(Integer id);

	FilteringAbility getFilteringAbility(ProductContext context, ProductFilter filter);


	Product createProduct(ProductEditor editor);

	Product updateProduct(Integer id, ProductEditor editor);

	Product removeProduct(Integer id);


	void updateSold(Integer id, int quantity);


	void validated(Integer id, Price price, Price supplierPrice, StockInfo stockInfo);
}
