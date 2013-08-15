package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ExchangeManager {
    double convertPrice(double price);

    double convertPrice(double price, double exchange);

    double getExchangeRate();

    void setExchangeRate(double exchangeRate);
}
