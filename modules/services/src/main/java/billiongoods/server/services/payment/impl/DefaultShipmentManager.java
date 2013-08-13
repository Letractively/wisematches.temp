package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.ShipmentManager;
import billiongoods.server.services.payment.ShipmentRates;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultShipmentManager implements ShipmentManager {
	private static final float DEFAULT_MAIL_COST = 0f;
	private static final float REGISTERED_MAIL_COST = 1.70f;
	private static final float REGISTERED_MAIL_AMOUNT = 25f;

	public DefaultShipmentManager() {
	}

	@Override
	public ShipmentRates getShipmentRates(Basket basket) {
		float amount = basket.getAmount();
		float weight = basket.getWeight();

		return new ShipmentRates(amount, weight, new float[]{0, getShipmentCost(amount, weight, ShipmentType.REGISTERED)});
	}

	@Override
	public float getShipmentCost(Basket basket, ShipmentType shipmentType) {
		return getShipmentCost(basket.getAmount(), basket.getWeight(), shipmentType);
	}

	private float getShipmentCost(float amount, float weight, ShipmentType shipmentType) {
		if (shipmentType == ShipmentType.FREE) {
			return DEFAULT_MAIL_COST;
		}

		if (shipmentType == ShipmentType.REGISTERED) {
			return amount >= REGISTERED_MAIL_AMOUNT ? DEFAULT_MAIL_COST : REGISTERED_MAIL_COST;
		}
		throw new IllegalArgumentException("Unsupported shipment type: " + shipmentType);
	}
}
