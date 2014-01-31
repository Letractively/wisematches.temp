package billiongoods.server.services.timezone;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.core.Language;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TimeZoneManager implements InitializingBean {
	private Map<Language, Collection<TimeZoneEntry>> timeZones = new HashMap<>();

	public TimeZoneManager() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for (Language language : Language.values()) {
			final Collection<TimeZoneEntry> collections = new ArrayList<>();
			timeZones.put(language, collections);

			final InputStream resourceAsStream = getClass().getResourceAsStream("/i18n/tz/timezones_" + language.getCode() + ".csv");
			if (resourceAsStream != null) {
				final CSVReader reader = new CSVReader(new InputStreamReader(resourceAsStream, "UTF-8"));
				String[] strings1 = reader.readNext();
				while (strings1 != null) {
					collections.add(new TimeZoneEntry(strings1[0], strings1[1]));
					strings1 = reader.readNext();
				}
			}
		}
	}

	public Collection<TimeZoneEntry> getTimeZoneEntries(Language language) {
		return timeZones.get(language);
	}

	public TimeZoneEntry getTimeZoneEntry(TimeZone timeZone, Language language) {
		final Collection<TimeZoneEntry> timeZoneEntries = timeZones.get(language);
		for (TimeZoneEntry timeZoneEntry : timeZoneEntries) {
			if (timeZoneEntry.getTimeZone().equals(timeZone)) {
				return timeZoneEntry;
			}
		}
		return null;
	}
}
