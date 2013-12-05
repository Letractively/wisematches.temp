package billiongoods.server.services.timezone;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.core.Language;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TimeZoneManager {
	private Map<Language, Collection<TimeZoneEntry>> timeZones = new HashMap<>();

	public TimeZoneManager() {
	}

	public Collection<TimeZoneEntry> getTimeZoneEntries(Language language) {
		Collection<TimeZoneEntry> collections = timeZones.get(language);
		if (collections != null) {
			return collections;
		}

		collections = new ArrayList<>();
		timeZones.put(language, collections);

		try {
			final CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/i18n/tz/timezones_" + language.getCode() + ".csv"), "UTF-8"));
			String[] strings1 = reader.readNext();
			while (strings1 != null) {
				collections.add(new TimeZoneEntry(strings1[0], strings1[1]));
				strings1 = reader.readNext();
			}
			return collections;
		} catch (IOException ex) {
			return null;
		}
	}
}
