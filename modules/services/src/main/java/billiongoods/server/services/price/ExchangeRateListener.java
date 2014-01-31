package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ExchangeRateListener {
	void exchangeRateUpdated(double oldRate, double newRate);
}
