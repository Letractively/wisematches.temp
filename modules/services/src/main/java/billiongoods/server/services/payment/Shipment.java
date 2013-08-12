package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Shipment {
	private final float amount;
	private final Address address;
	private final ShipmentType type;

	public Shipment(float amount, Address address, ShipmentType type) {
		this.amount = amount;
		this.address = address;
		this.type = type;
	}

	public float getAmount() {
		return amount;
	}

	public Address getAddress() {
		return address;
	}

	public ShipmentType getType() {
		return type;
	}
}
