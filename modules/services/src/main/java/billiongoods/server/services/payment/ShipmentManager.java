package billiongoods.server.services.payment;

import billiongoods.server.services.basket.Basket;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ShipmentManager {
	ShipmentRates getShipmentRates(Basket basket);

	double getShipmentCost(Basket basket, ShipmentType shipmentType);


	double getFreeShipmentAmount();

	double getDefaultShipmentCost();

	double getRegisteredShipmentCost();
}
