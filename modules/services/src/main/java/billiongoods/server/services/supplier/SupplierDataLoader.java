package billiongoods.server.services.supplier;

import billiongoods.server.warehouse.SupplierInfo;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierDataLoader {
	void initialize();

	SupplierDescription loadDescription(SupplierInfo supplierInfo) throws DataLoadingException;
}
