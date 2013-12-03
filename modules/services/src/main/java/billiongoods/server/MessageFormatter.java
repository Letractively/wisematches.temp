package billiongoods.server;

import billiongoods.core.Language;
import billiongoods.core.Localization;
import billiongoods.server.warehouse.ProductPreview;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DelegatingMessageSource;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MessageFormatter extends DelegatingMessageSource implements MessageSource {
	private static final StringBuilder sb = new StringBuilder();
	private static final Formatter FORMATTER = new Formatter(sb);

	private static final Map<Locale, DateFormat> DATE_FORMATTER = new ConcurrentHashMap<>();
	private static final Map<Locale, DateFormat> TIME_FORMATTER = new ConcurrentHashMap<>();

	static {
		for (Language lang : Language.values()) {
			DATE_FORMATTER.put(lang.getLocale(), DateFormat.getDateInstance(DateFormat.LONG, lang.getLocale()));
			TIME_FORMATTER.put(lang.getLocale(), DateFormat.getTimeInstance(DateFormat.SHORT, lang.getLocale()));
		}
	}

	public static final Pattern PRODUCT_CODE_PATTER = Pattern.compile("^A(\\d{6})$");

	public MessageFormatter() {
	}

	public static String getProductCode(Integer id) {
		synchronized (FORMATTER) {
			sb.setLength(0);
			return "A" + FORMATTER.format("%06d", id).toString();
		}
	}

	public static String getProductCode(ProductPreview art) {
		return getProductCode(art.getId());
	}

	public static Integer extractProductId(String code) {
		if (code == null) {
			return null;
		}
		final Matcher matcher = PRODUCT_CODE_PATTER.matcher(code.trim());
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		return null;
	}

	public String getMessage(String code, Locale locale) {
		return super.getMessage(code, null, locale);
	}

	public String getMessage(String code, Object a1, Locale locale) {
		return super.getMessage(code, new Object[]{a1}, locale);
	}

	public String getMessage(String code, Object a1, Object a2, Locale locale) {
		return super.getMessage(code, new Object[]{a1, a2}, locale);
	}

	public String getMessage(String code, Object a1, Object a2, Object a3, Locale locale) {
		return super.getMessage(code, new Object[]{a1, a2, a3}, locale);
	}

	public String formatDate(Date date, Locale locale) {
		return DATE_FORMATTER.get(locale).format(date);
	}

	public String formatDate(long date, Locale locale) {
		return DATE_FORMATTER.get(locale).format(new Date(date));
	}

	public String formatTime(Date date, Locale locale) {
		return TIME_FORMATTER.get(locale).format(date);
	}

	public String formatDateTime(Date date, Locale locale) {
		return formatDate(date, locale) + " " + formatTime(date, locale);
	}

	public String formatPropertyValue(Object o, Locale locale) {
		if (o instanceof String) {
			return (String) o;
		} else if (o instanceof Boolean) {
			final Localization localization = Language.byLocale(locale).getLocalization();
			return o == Boolean.TRUE ? localization.getYes() : localization.getNo();
		}
		return String.valueOf(o);
	}

	public String formatElapsedTime(Date date, Locale locale) {
		final long startTime = date.getTime();
		final long endTime = System.currentTimeMillis();
		return formatTimeMinutes((endTime - startTime) / 1000 / 60, locale);
	}

	public String formatRemainedTime(Date date, Locale locale) {
		final long startTime = System.currentTimeMillis();
		final long endTime = date.getTime();
		return formatTimeMinutes((endTime - startTime) / 1000 / 60, locale);
	}

	public int getAge(Date date) {
		DateMidnight birthdate = new DateMidnight(date);
		DateTime now = new DateTime();
		Years age = Years.yearsBetween(birthdate, now);
		return age.getYears();
	}

	public long getTimeMillis(Date date) {
		return date.getTime();
	}

	public String formatTimeMillis(long time, Locale locale) {
		return formatTimeMinutes(time / 60 / 1000, locale);
	}

	public String formatTimeMinutes(long time, Locale locale) {
		final int days = (int) (time / 60 / 24);
		final int hours = (int) ((time - (days * 24 * 60)) / 60);
		final int minutes = (int) (time % 60);

		final Language language = Language.byLocale(locale);
		final Localization localization = language.getLocalization();
		if (days == 0 && hours == 0 && minutes == 0) {
			return localization.getMomentAgoLabel();
		}

		if (hours <= 0 && minutes <= 0) {
			return days + " " + localization.getDaysLabel(days);
		}
		if (days <= 0 && minutes <= 0) {
			return hours + " " + localization.getHoursLabel(hours);
		}
		if (days <= 0 && hours <= 0) {
			return minutes + " " + localization.getMinutesLabel(minutes);
		}

		final StringBuilder b = new StringBuilder();
		if (days > 0) {
			b.append(days).append(localization.getDaysCode()).append(" ");
		}
		if (hours > 0) {
			b.append(hours).append(localization.getHoursCode()).append(" ");
		}
		if ((days == 0 || hours == 0) && (minutes > 0)) {
			b.append(minutes).append(localization.getMinutesCode()).append(" ");
		}
		return b.toString().trim();
	}

	public String formatDays(int days, Locale locale) {
		return Language.byLocale(locale).getLocalization().getDaysLabel(days);
	}

	public String formatHours(int hours, Locale locale) {
		return Language.byLocale(locale).getLocalization().getHoursLabel(hours);
	}

	public String formatMinutes(int minutes, Locale locale) {
		return Language.byLocale(locale).getLocalization().getMinutesLabel(minutes);
	}

	public String getWordEnding(int quantity, Locale locale) {
		return Language.byLocale(locale).getLocalization().getWordEnding(quantity);
	}

	public String getNumeralEnding(int value, Locale locale) {
		return Language.byLocale(locale).getLocalization().getNumeralEnding(value);
	}
}
