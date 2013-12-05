package billiongoods.server.services.settings;

import billiongoods.core.Language;
import billiongoods.core.Settings;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@MappedSuperclass
public class MemberSettings implements Settings {
	@Column(name = "language")
	@Enumerated(EnumType.ORDINAL)
	protected Language language;

	@Column(name = "timezone")
	protected TimeZone timeZone;

	@Column(name = "ordersGate")
	@Enumerated(EnumType.ORDINAL)
	protected NotificationGate ordersGate;

	protected MemberSettings() {
	}

	public MemberSettings(Language language, TimeZone timeZone, NotificationGate ordersGate) {
		this.language = language;
		this.timeZone = timeZone;
		this.ordersGate = ordersGate;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public Language getLanguage() {
		return language;
	}

	public NotificationGate getOrdersGate() {
		return ordersGate;
	}
}
