package billiongoods.core;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Settings {
	TimeZone getTimeZone();

	Language getLanguage();
}
