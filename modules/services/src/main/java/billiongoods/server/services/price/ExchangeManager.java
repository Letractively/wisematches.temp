package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ExchangeManager {
	double getExchangeRate();

	void setExchangeRate(double exchangeRate);
}
