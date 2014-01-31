package billiongoods.server.services.payment;

import billiongoods.server.services.address.Address;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Shipment {
	private final double amount;
	private final Address address;
	private final ShipmentType type;

	public Shipment(double amount, Address address, ShipmentType type) {
		this.amount = amount;
		this.address = address;
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public Address getAddress() {
		return address;
	}

	public ShipmentType getType() {
		return type;
	}
}
