package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ShipmentRates {
    private double amount;
    private double weight;
    private double[] rates;

    public ShipmentRates(double amount, double weight, double[] rates) {
        this.amount = amount;
        this.weight = weight;
        this.rates = rates;
    }

    public double getAmount() {
        return amount;
    }

    public double getWeight() {
        return weight;
    }

    public double getShipmentCost(ShipmentType type) {
        return rates[type.ordinal()];
    }

    public boolean isFreeShipment(ShipmentType type) {
        return Double.compare(rates[type.ordinal()], 0.0f) == 0;
    }
}
