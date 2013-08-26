package billiongoods.server.services.price;

import billiongoods.server.warehouse.Price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceRenewal extends PriceValidation {
	Price getOldPrice();

	Price getOldSupplierPrice();

	Price getNewPrice();

	Price getNewSupplierPrice();
}