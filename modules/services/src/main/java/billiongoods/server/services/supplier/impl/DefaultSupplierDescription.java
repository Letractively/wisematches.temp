package billiongoods.server.services.supplier.impl;

import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;

import java.util.Collection;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultSupplierDescription implements SupplierDescription {
	private final Price price;
	private final StockInfo stockInfo;
	private final Map<String, Collection<String>> parameters;

	public DefaultSupplierDescription(Price price, StockInfo stockInfo, Map<String, Collection<String>> parameters) {
		this.price = price;
		this.stockInfo = stockInfo;
		this.parameters = parameters;
	}

	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public StockInfo getStockInfo() {
		return stockInfo;
	}

	@Override
	public Map<String, Collection<String>> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("DefaultSupplierDescription{");
		sb.append("price=").append(price);
		sb.append(", stockInfo=").append(stockInfo);
		sb.append(", parameters=").append(parameters);
		sb.append('}');
		return sb.toString();
	}
}
