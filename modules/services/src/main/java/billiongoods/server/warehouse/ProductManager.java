package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductManager extends SearchManager<ProductPreview, ProductContext, ProductFilter> {
	void addProductListener(ProductListener l);

	void removeProductListener(ProductListener l);


	void addProductStateListener(ProductStateListener l);

	void removeProductStateListener(ProductStateListener l);


	Product getProduct(Integer id);

	Integer searchBySku(String sku);


	ProductPreview getPreview(Integer id);

	List<ProductPreview> getPreviews(Integer... id);


	SupplierInfo getSupplierInfo(Integer id);

	Filtering getFilteringAbility(ProductContext context, ProductFilter filter);


	Product createProduct(ProductEditor editor);

	Product updateProduct(Integer id, ProductEditor editor);

	Product removeProduct(Integer id);


	int updateDescriptions(String from, String to);


	void updateSold(Integer id, int quantity);

	void updateRecommendation(Integer id, boolean recommended);

	void updateProductInformation(Integer id, Price price, Price supplierPrice, StockInfo stockInfo);
}
