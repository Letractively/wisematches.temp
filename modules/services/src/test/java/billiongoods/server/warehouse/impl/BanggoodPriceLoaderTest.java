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
        final Price price = priceValidator.loadPrice(new HibernateSupplierInfo("New-PCB-For-WLtoys-V912-RC-Helicopter-p-83053.html", "SKU068185 ", Supplier.BANGGOOD, null));
        System.out.println(price);
        assertNotNull(price);
    }
}
