package billiongoods.server.web.servlet.mvc.account.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Deprecated
public final class TimeZoneInfo {
    private final String offsetName;
    private final TimeZone timeZone;

    private static final Collection<TimeZoneInfo> timeZones = new ArrayList<TimeZoneInfo>();

    static {
        final String[] availableIDs = TimeZone.getAvailableIDs();
        for (String id : availableIDs) {
            if (!id.contains("/")) {
                continue;
            }
            if (id.contains("GMT")) {
                continue;
            }
            timeZones.add(new TimeZoneInfo(TimeZone.getTimeZone(id)));
        }
    }

    private TimeZoneInfo(TimeZone timeZone) {
        this.timeZone = timeZone;

        final int rawOffset = timeZone.getRawOffset();
        int k = rawOffset / 1000 / 60;
        String s = String.valueOf(Math.abs(k % 60));
        if (s.length() == 1) {
            s = "0" + s;
        }
        offsetName = (rawOffset >= 0 ? "+" : "") + (k / 60) + ":" + s;
    }

    public String getId() {
        return timeZone.getID();
    }

    public String getOffsetName() {
        return offsetName;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public static Collection<TimeZoneInfo> getTimeZones() {
        return timeZones;
    }
}
