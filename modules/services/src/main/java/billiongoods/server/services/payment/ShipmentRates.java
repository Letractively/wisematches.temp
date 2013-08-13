package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ShipmentRates {
	private float amount;
	private float weight;
	private float[] rates;

	public ShipmentRates(float amount, float weight, float[] rates) {
		this.amount = amount;
		this.weight = weight;
		this.rates = rates;
	}

	public float getAmount() {
		return amount;
	}

	public float getWeight() {
		return weight;
	}

	public float getShipmentCost(ShipmentType type) {
		return rates[type.ordinal()];
	}

	public boolean isFreeShipment(ShipmentType type) {
		return Float.compare(rates[type.ordinal()], 0.0f) == 0;
	}
}
