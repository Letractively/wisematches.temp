package billiongoods.server.services.image;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ImageType {
	PREVIEW,
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
