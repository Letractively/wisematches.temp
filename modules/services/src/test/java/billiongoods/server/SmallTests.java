package billiongoods.server;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SmallTests {
	public SmallTests() {
	}

	@Test
	public void asd() {
		final Clock clock = Clock.systemUTC();
		System.out.println(clock);

		final Instant instant = clock.instant();
		System.out.println(instant);
	}
}
