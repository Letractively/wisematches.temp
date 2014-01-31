package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.ShipmentManager;
import billiongoods.server.services.payment.ShipmentRates;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultShipmentManager implements ShipmentManager {
	private static final double DEFAULT_MAIL_COST = 0d;
	private static final double REGISTERED_MAIL_COST = 70d;
	private static final double REGISTERED_MAIL_AMOUNT = 1000d;

	public DefaultShipmentManager() {
	}

	@Override
	public ShipmentRates getShipmentRates(Basket basket) {
		double amount = basket.getAmount();
		double weight = basket.getWeight();

		return new ShipmentRates(amount, weight, new double[]{0, getShipmentCost(amount, weight, ShipmentType.REGISTERED)});
	}

	@Override
	public double getShipmentCost(Basket basket, ShipmentType shipmentType) {
		return getShipmentCost(basket.getAmount(), basket.getWeight(), shipmentType);
	}

	private double getShipmentCost(double amount, double weight, ShipmentType shipmentType) {
		if (shipmentType == ShipmentType.FREE) {
			return DEFAULT_MAIL_COST;
		}

		if (shipmentType == ShipmentType.REGISTERED) {
			return amount >= REGISTERED_MAIL_AMOUNT ? DEFAULT_MAIL_COST : REGISTERED_MAIL_COST;
		}
		throw new IllegalArgumentException("Unsupported shipment type: " + shipmentType);
	}
}
