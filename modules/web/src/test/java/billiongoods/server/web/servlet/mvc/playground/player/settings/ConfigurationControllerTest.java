package billiongoods.server.web.servlet.mvc.playground.player.settings;

import org.junit.Test;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ConfigurationControllerTest {
    public ConfigurationControllerTest() {
    }

    @Test
    public void asd() {
        final String[] availableIDs = TimeZone.getAvailableIDs();
        for (String id : availableIDs) {
            if (!id.contains("/")) {
                continue;
            }
            if (id.contains("GMT")) {
                continue;
            }
            final TimeZone timeZone = TimeZone.getTimeZone(id);
            final int rawOffset = timeZone.getRawOffset();
            int k = rawOffset / 1000 / 60;
            String s = String.valueOf(Math.abs(k % 60));
            if (s.length() == 1) {
                s = "0" + s;
            }
//            System.out.println("(Время по гринвичу " + (rawOffset >= 0 ? "+" : "") + (k / 60) + ":" + s + ") " + id);
        }
    }
}
