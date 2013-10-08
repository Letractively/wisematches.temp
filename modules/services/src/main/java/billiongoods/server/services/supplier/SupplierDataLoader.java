package billiongoods.server.services.supplier;

import billiongoods.server.warehouse.SupplierInfo;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierDataLoader {
	void initialize();

	Availability loadAvailability(SupplierInfo supplierInfo) throws DataLoadingException;

	SupplierDescription loadDescription(SupplierInfo supplierInfo) throws DataLoadingException;
}
