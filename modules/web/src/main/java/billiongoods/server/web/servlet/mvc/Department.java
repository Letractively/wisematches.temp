package billiongoods.server.web.servlet.mvc;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Department {
	ACCOUNT,
	ASSISTANCE,
	MAINTAIN,
	WAREHOUSE;

	private final String name;

	private Department() {
		this.name = name().toLowerCase();
	}

	public String getStyle() {
		return name;
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
