package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.MarkupType;
import org.junit.Test;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernatePriceConverterTest {
	public HibernatePriceConverterTest() {
	}

	@Test
	public void testFormula() {
		final HibernatePriceConverter converter = new HibernatePriceConverter(36.0);
		final String v = converter.formula("v", "Math.round", MarkupType.REGULAR);
		System.out.println(v);

		final double convert = converter.convert(27.57, 36.0, MarkupType.REGULAR);
		System.out.println(convert);
	}
}
