package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.payment.ShipmentType;
import billiongoods.server.web.servlet.mvc.privacy.form.AddressForm;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BasketCheckoutForm extends AddressForm {
	private int[] itemQuantities;
	private Integer[] itemNumbers;

	private String action = null;

	private String coupon = null;
	private boolean remember = true;
	private boolean notifications = true;
	private boolean selectionTab = true;

	private ShipmentType shipment = ShipmentType.FREE;

	public BasketCheckoutForm() {
	}

	public Integer[] getItemNumbers() {
		return itemNumbers;
	}

	public void setItemNumbers(Integer[] itemNumbers) {
		this.itemNumbers = itemNumbers;
	}

	public int[] getItemQuantities() {
		return itemQuantities;
	}

	public void setItemQuantities(int[] itemQuantities) {
		this.itemQuantities = itemQuantities;
	}

	public ShipmentType getShipment() {
		return shipment;
	}

	public void setShipment(ShipmentType shipment) {
		this.shipment = shipment;
	}

	public boolean isNotifications() {
		return notifications;
	}

	public void setNotifications(boolean notifications) {
		this.notifications = notifications;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isRemember() {
		return remember;
	}

	public void setRemember(boolean remember) {
		this.remember = remember;
	}

	public boolean isSelectionTab() {
		return selectionTab;
	}

	public void setSelectionTab(boolean selectionTab) {
		this.selectionTab = selectionTab;
	}
}
