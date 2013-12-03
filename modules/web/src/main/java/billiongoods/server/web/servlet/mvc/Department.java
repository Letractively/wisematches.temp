package billiongoods.server.web.servlet.mvc;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Department {
	UNDEFINED,
	ACCOUNT,
	PRIVACY,
	ASSISTANCE,
	MAINTAIN,
	WAREHOUSE;

	private final String code;

	private Department() {
		this.code = name().toLowerCase();
	}

	public String getCode() {
		return code;
	}

	public String getStyle() {
		return code;
	}

	public boolean isUndefined() {
		return this == UNDEFINED;
	}

	public boolean isPrivacy() {
		return this == PRIVACY;
	}

	public boolean isAccount() {
		return this == ACCOUNT;
	}

	public boolean isMaintain() {
		return this == MAINTAIN;
	}

	public boolean isWarehouse() {
		return this == WAREHOUSE;
	}

	public boolean isAssistance() {
		return this == ASSISTANCE;
	}
}
