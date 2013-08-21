package billiongoods.server.services.price;

import billiongoods.server.warehouse.Price;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PriceTest {
	public PriceTest() {
	}

	@Test
	public void testToString() {
		assertEquals("0.00", Price.string(0.000f));
		assertEquals("0.32", Price.string(0.32422f));
		assertEquals("0.01", Price.string(0.012323f));
		assertEquals("32534.10", Price.string(32534.1f));
		assertEquals("32534.00", Price.string(32534.000f));
	}
}
