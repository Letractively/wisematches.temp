package billiongoods.server.services.tracking;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductTracking {
	Integer getId();


	Date getRegistration();

	Integer getProductId();


	Long getPersonId();

	String getPersonEmail();


	TrackingType getTrackingType();
}
