package billiongoods.server.services.supplier;

import billiongoods.server.warehouse.StockInfo;
import billiongoods.server.warehouse.SupplierInfo;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierDataLoader {
	void initialize();

	StockInfo loadStockInfo(SupplierInfo supplierInfo) throws DataLoadingException;

	SupplierDescription loadDescription(SupplierInfo supplierInfo) throws DataLoadingException;
}
