package billiongoods.server.services.validator;

import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;
import billiongoods.server.warehouse.SupplierInfo;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ValidatingProduct {
	Integer getId();

	Price getPrice();

	StockInfo getStockInfo();

	SupplierInfo getSupplierInfo();
}
