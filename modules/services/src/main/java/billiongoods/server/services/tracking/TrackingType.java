package billiongoods.server.services.tracking;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum TrackingType {
	/**
	 * Indicates that notification must be sent when description of a product has been added.
	 */
	DESCRIPTION,
	/**
	 * Indicates that notification must be sent when a product is available again.
	 */
	AVAILABILITY
}
