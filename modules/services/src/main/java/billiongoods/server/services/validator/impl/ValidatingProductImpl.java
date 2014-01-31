package billiongoods.server.services.validator.impl;

import billiongoods.server.services.validator.ValidatingProduct;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;
import billiongoods.server.warehouse.SupplierInfo;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ValidatingProductImpl implements ValidatingProduct {
	private final Integer id;
	private final Price price;
	private final StockInfo stockInfo;
	private final SupplierInfo supplierInfo;

	ValidatingProductImpl(Integer id, Price price, StockInfo stockInfo, SupplierInfo supplierInfo) {
		this.id = id;
		this.price = price;
		this.stockInfo = stockInfo;
		this.supplierInfo = supplierInfo;
	}

	public Integer getId() {
		return id;
	}

	public Price getPrice() {
		return price;
	}

	public StockInfo getStockInfo() {
		return stockInfo;
	}

	public SupplierInfo getSupplierInfo() {
		return supplierInfo;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ValidatingProductImpl{");
		sb.append("id=").append(id);
		sb.append(", price=").append(price);
		sb.append(", stockInfo=").append(stockInfo);
		sb.append(", supplierInfo=").append(supplierInfo);
		sb.append('}');
		return sb.toString();
	}
}