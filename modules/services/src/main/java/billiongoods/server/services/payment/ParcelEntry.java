package billiongoods.server.services.payment;

import java.util.Arrays;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ParcelEntry {
	private final int number;
	private final Integer[] items;

	/**
	 * @param number the number of the parcel
	 * @param items  the array of all products to be included into this parcel.
	 */
	public ParcelEntry(int number, Integer... items) {
		this.number = number;
		this.items = items;
	}

	public int getNumber() {
		return number;
	}

	public Integer[] getItems() {
		return items;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ParcelEntry{");
		sb.append("number=").append(number);
		sb.append(", items=").append(Arrays.toString(items));
		sb.append('}');
		return sb.toString();
	}
}
