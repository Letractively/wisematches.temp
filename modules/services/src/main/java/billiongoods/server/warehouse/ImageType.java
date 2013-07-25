package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ImageType {
	GRID,
	VIEW,
	CUSTOM;

	private final String name;

	private ImageType() {
		this.name = name().toLowerCase();
	}

	public String getName() {
		return name;
	}
}
