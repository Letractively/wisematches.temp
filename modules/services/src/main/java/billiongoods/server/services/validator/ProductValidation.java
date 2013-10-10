package billiongoods.server.services.validator;

import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductValidation {
	Date getTimestamp();

	Integer getProductId();


	Price getOldPrice();

	Price getNewPrice();


	Price getOldSupplierPrice();

	Price getNewSupplierPrice();


	StockInfo getOldStockInfo();

	StockInfo getNewStockInfo();


	boolean hasChanges();

	String getErrorMessage();
}
