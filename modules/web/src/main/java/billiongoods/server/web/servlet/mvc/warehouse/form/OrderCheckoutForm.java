package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.address.Address;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderCheckoutForm {
	private Basket basket;
	private Address address;
	private ShipmentType shipmentType;
	private boolean enabledTracking;

	public OrderCheckoutForm(Basket basket, Address address, ShipmentType shipmentType, boolean enabledTracking) {
		this.basket = basket;
		this.address = address;
		this.shipmentType = shipmentType;
		this.enabledTracking = enabledTracking;
	}

	public Basket getBasket() {
		return basket;
	}

	public Address getAddress() {
		return address;
	}

	public ShipmentType getShipmentType() {
		return shipmentType;
	}

	public boolean isEnabledTracking() {
		return enabledTracking;
	}
}
