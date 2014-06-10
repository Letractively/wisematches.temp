package billiongoods.server.services.supplier.impl;

import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultSupplierDescription implements SupplierDescription {
	private final Price price;
	private final StockInfo stockInfo;
	private final Collection<URL> images;
	private final Map<String, Collection<String>> parameters;

	public DefaultSupplierDescription(Price price, StockInfo stockInfo, Collection<URL> images, Map<String, Collection<String>> parameters) {
		this.price = price;
		this.images = images;
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
	public Collection<URL> getImages() {
		return images;
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
		sb.append(", images=").append(images);
		sb.append(", parameters=").append(parameters);
		sb.append('}');
		return sb.toString();
	}
}
