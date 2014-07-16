package billiongoods.server.services.payment;

import billiongoods.server.services.address.Address;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Shipment {
	@Column(name = "shipmentAmount", updatable = false)
	private double amount;

	@Embedded
	private Address address;

	@Column(name = "shipmentType", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private ShipmentType type;

	@Deprecated
	private Shipment() {
	}

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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Shipment{");
		sb.append("amount=").append(amount);
		sb.append(", address=").append(address);
		sb.append(", type=").append(type);
		sb.append('}');
		return sb.toString();
	}
}
