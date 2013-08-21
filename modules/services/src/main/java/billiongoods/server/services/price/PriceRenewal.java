package billiongoods.server.services.price;

import billiongoods.server.warehouse.Price;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceRenewal {
	public Date getTimestamp();

	public Integer getArticleId();

	public Price getOldPrice();

	public Price getOldSupplierPrice();

	public Price getNewPrice();

	public Price getNewSupplierPrice();
}