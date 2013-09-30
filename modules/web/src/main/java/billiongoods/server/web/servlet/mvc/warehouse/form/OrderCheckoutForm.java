package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.payment.Address;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderCheckoutForm implements Address {
    private int[] itemQuantities;
    private Integer[] itemNumbers;

    private String name;
    private String region;
    private String city;
    private String postalCode;
    private String streetAddress;

    private String action = null;
    private boolean notifications = true;

    private ShipmentType shipment = ShipmentType.FREE;

    public OrderCheckoutForm() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
