package billiongoods.server.warehouse.impl;

import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.services.price.impl.loader.BanggoodPriceLoader;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.Supplier;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodPriceLoaderTest {
	public BanggoodPriceLoaderTest() {
	}

	@Test
	public void asd() throws PriceLoadingException {
		BanggoodPriceLoader priceValidator = new BanggoodPriceLoader();
		final Price price = priceValidator.loadPrice(new HibernateSupplierInfo("71838", "SKU072712 ", Supplier.BANGGOOD, null));
		assertNotNull(price);
	}
}
