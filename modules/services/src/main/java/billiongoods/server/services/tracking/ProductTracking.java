package billiongoods.server.services.tracking;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductTracking {
	Long getPersonId();

	String getPersonEmail();


	Integer getProductId();

	Date getRegistration();


	TrackingType getTrackingType();
}
