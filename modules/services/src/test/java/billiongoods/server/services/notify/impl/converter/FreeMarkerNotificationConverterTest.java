package billiongoods.server.services.notify.impl.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FreeMarkerNotificationConverterTest {
	public FreeMarkerNotificationConverterTest() {
	}

	@Test
	public void getTemplate() {
		final FreeMarkerNotificationConverter converter = new FreeMarkerNotificationConverter();
		assertEquals("this.is.code", converter.getTemplate("this.is.code"));
		assertEquals("this.is.code", converter.getTemplate("this.is.code.c1"));
		assertEquals("this.is.code", converter.getTemplate("this.is.code.b.c1"));
	}
}
