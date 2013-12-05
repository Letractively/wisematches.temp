package billiongoods.server.services.timezone;

import billiongoods.core.Language;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TimeZoneManagerTest {
	public TimeZoneManagerTest() {
	}

	@Test
	public void test() {
		final TimeZoneManager tz = new TimeZoneManager();

		final Collection<TimeZoneEntry> timeZoneEntries = tz.getTimeZoneEntries(Language.RU);
		assertEquals(78, timeZoneEntries.size());
	}
}
