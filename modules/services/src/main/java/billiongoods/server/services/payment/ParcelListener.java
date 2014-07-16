package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ParcelListener {
	void parcelStateChanged(Order order, Parcel parcel, ParcelState oldState, ParcelState newState);
}
