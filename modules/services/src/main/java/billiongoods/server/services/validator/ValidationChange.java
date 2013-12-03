package billiongoods.server.services.validator;

import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidationChange {
	Date getTimestamp();

	Integer getProductId();


	boolean hasChanges();

	boolean isValidated();


	Price getOldPrice();

	Price getNewPrice();


	Price getOldSupplierPrice();

	Price getNewSupplierPrice();


	StockInfo getOldStockInfo();

	StockInfo getNewStockInfo();
}
