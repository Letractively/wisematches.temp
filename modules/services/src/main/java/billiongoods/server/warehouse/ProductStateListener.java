package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductStateListener {
	void productPriceChanged(ProductDescription description, Price oldPrice, Price newPrice);

	void productStockChanged(ProductDescription description, StockInfo oldStock, StockInfo newStock);

	void productStateChanged(ProductDescription description, ProductState oldState, ProductState newState);
}
