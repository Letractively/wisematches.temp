package billiongoods.server.services.price.impl;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.services.price.MarkupType;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.services.price.impl.loader.BanggoodPriceLoader;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.Supplier;
import billiongoods.server.warehouse.impl.HibernateSupplierInfo;
import org.junit.Test;

import java.io.FileReader;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernatePriceValidatorTest {
	public HibernatePriceValidatorTest() {
	}

	@Test
	public void asd() throws Exception {
		PriceConverter priceConverter = new DefaultPriceConverter();
		BanggoodPriceLoader priceLoader = new BanggoodPriceLoader();

		final CSVReader reader = new CSVReader(new FileReader("C:\\Temp\\banggood\\store_product.csv"));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Integer id = Integer.parseInt(nextLine[0]);
			Double price = Double.parseDouble(nextLine[1]);
			Double primordialPrice = null;
			if (!"NULL".equals(nextLine[2])) {
				primordialPrice = Double.parseDouble(nextLine[2]);
			}

			String uri = nextLine[3];

			final Price currentPrice = new Price(price, primordialPrice);
			final Price loadedPrice = priceLoader.loadPrice(new HibernateSupplierInfo(uri, null, Supplier.BANGGOOD, null));

			if (!currentPrice.equals(loadedPrice)) {
				final Price price1 = priceConverter.convert(loadedPrice, 34.2d, MarkupType.REGULAR);

				StringBuilder sb = new StringBuilder("update store_product ");
				sb.append(" set price=" + price1.getAmount());
				sb.append(", buyPrice=" + loadedPrice.getAmount());
				if (loadedPrice.getPrimordialAmount() != null) {
					sb.append(", buyPrimordialPrice=" + loadedPrice.getPrimordialAmount());
				} else {
					sb.append(", buyPrimordialPrice=NULL");
				}
				if (price1.getPrimordialAmount() != null) {
					sb.append(", primordialPrice=" + price1.getPrimordialAmount());
				} else {
					sb.append(", primordialPrice=NULL");
				}
				sb.append(" where id=" + id);
			}
		}
	}
}
