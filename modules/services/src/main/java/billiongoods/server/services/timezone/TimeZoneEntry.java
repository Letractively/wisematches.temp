package billiongoods.server.services.timezone;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TimeZoneEntry {
	private final String id;
	private final String displayName;
	private final TimeZone timeZone;

	public TimeZoneEntry(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
		this.timeZone = TimeZone.getTimeZone(id);
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}
}
