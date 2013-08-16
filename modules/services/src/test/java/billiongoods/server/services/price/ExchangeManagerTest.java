package billiongoods.server.services.price;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ExchangeManagerTest {
	public ExchangeManagerTest() {
	}

	@Test
	public void testMathToString() {
		assertEquals("0.00", PriceMath.string(0.000f));
		assertEquals("0.32", PriceMath.string(0.32422f));
		assertEquals("0.01", PriceMath.string(0.012323f));
		assertEquals("32534.10", PriceMath.string(32534.1f));
		assertEquals("32534.00", PriceMath.string(32534.000f));
	}
}
