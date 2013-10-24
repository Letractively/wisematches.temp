package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductStateListener {
	void productPriceChanged(ProductPreview preview, Price oldPrice, Price newPrice);

	void productStockChanged(ProductPreview preview, StockInfo oldStock, StockInfo newStock);

	void productStateChanged(ProductPreview preview, ProductState oldState, ProductState newState);
}
