package billiongoods.server;

import billiongoods.core.*;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DelegatingMessageSource;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MessageFormatter extends DelegatingMessageSource implements MessageSource {
    private static final Map<Locale, DateFormat> DATE_FORMATTER = new ConcurrentHashMap<>();
    private static final Map<Locale, DateFormat> TIME_FORMATTER = new ConcurrentHashMap<>();

    static {
        for (Language lang : Language.values()) {
            DATE_FORMATTER.put(lang.getLocale(), DateFormat.getDateInstance(DateFormat.LONG, lang.getLocale()));
            TIME_FORMATTER.put(lang.getLocale(), DateFormat.getTimeInstance(DateFormat.SHORT, lang.getLocale()));
        }
    }

    public MessageFormatter() {
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

    public String getPlayerNick(Member player, Locale locale) {
        return player.getNickname();
    }

    public String getVisitorNick(Visitor player, Locale locale) {
        return getVisitorNick(player.getLanguage(), locale);
    }

    public String getVisitorNick(Language language, Locale locale) {
        return getMessage("game.player.guest", locale);
    }

    public String getPersonalityNick(Personality p, Locale locale) {
        if (p instanceof Visitor) {
            return getVisitorNick((Visitor) p, locale);
        } else if (p instanceof Member) {
            return getPlayerNick((Member) p, locale);
        }
        throw new IllegalArgumentException("Unsupported personality type: " + p.getClass());
    }


    public String formatDate(Date date, Locale locale) {
        return DATE_FORMATTER.get(locale).format(date);
    }

    public String formatTime(Date date, Locale locale) {
        return TIME_FORMATTER.get(locale).format(date);
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
//
//
//	public void setMessageSource(MessageSource messageSource) {
//		this.messageSource = messageSource;
//	}
}
