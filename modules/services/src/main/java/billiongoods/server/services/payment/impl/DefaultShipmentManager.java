package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.ShipmentManager;
import billiongoods.server.services.payment.ShipmentRates;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultShipmentManager implements ShipmentManager {
	private double freeShipmentAmount = FREE_SHIPMENT_AMOUNT;
	private double defaultShipmentCost = DEFAULT_SHIPMENT_COST;
	private double registeredShipmentCost = REGISTERED_SHIPMENT_COST;

	private static final double DEFAULT_SHIPMENT_COST = 0d;
	private static final double FREE_SHIPMENT_AMOUNT = 1000d;
	private static final double REGISTERED_SHIPMENT_COST = 70d;

	public DefaultShipmentManager() {
	}

	public DefaultShipmentManager(double freeShipmentAmount, double defaultShipmentCost, double registeredShipmentCost) {
		this.freeShipmentAmount = freeShipmentAmount;
		this.defaultShipmentCost = defaultShipmentCost;
		this.registeredShipmentCost = registeredShipmentCost;
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
			return defaultShipmentCost;
		}

		if (shipmentType == ShipmentType.REGISTERED) {
			return amount >= freeShipmentAmount ? defaultShipmentCost : registeredShipmentCost;
		}
		throw new IllegalArgumentException("Unsupported shipment type: " + shipmentType);
	}

	@Override
	public double getFreeShipmentAmount() {
		return freeShipmentAmount;
	}

	@Override
	public double getDefaultShipmentCost() {
		return defaultShipmentCost;
	}

	@Override
	public double getRegisteredShipmentCost() {
		return registeredShipmentCost;
	}
}
