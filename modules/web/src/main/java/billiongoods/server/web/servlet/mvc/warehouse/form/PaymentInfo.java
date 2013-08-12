package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PaymentInfo {
	private float amount;
	private ShipmentType shipment;

	public static final float REGISTERED_SHIPMENT_AMOUNT = 1.70f;
	public static final float FREE_REGISTERED_SHIPMENT_AMOUNT = 25.f;

	public PaymentInfo(Basket basket, ShipmentType shipment) {
		this.amount = basket.getAmount();
		this.shipment = shipment;
	}

	public float getAmount() {
		return amount;
	}

	public float getTotal() {
		return getAmount() + getShipment();
	}

	public float getShipment() {
		return (amount >= FREE_REGISTERED_SHIPMENT_AMOUNT || shipment == ShipmentType.FREE ? 0.f : REGISTERED_SHIPMENT_AMOUNT);
	}

	public float getAmountForRegistered() {
		return FREE_REGISTERED_SHIPMENT_AMOUNT - amount;
	}

	public boolean isFreeRegisteredShipment() {
		return amount >= FREE_REGISTERED_SHIPMENT_AMOUNT;
	}
}
