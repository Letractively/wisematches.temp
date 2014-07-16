package billiongoods.server.services.payment;

import java.util.Arrays;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ParcelEntry {
	private final int number;
	private final Integer[] products;

	/**
	 * @param number   the number of the parcel
	 * @param products the array of all products to be included into this parcel.
	 */
	public ParcelEntry(int number, Integer... products) {
		this.number = number;
		this.products = products;
	}

	public int getNumber() {
		return number;
	}

	public Integer[] getProducts() {
		return products;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ParcelEntry{");
		sb.append("number=").append(number);
		sb.append(", products=").append(Arrays.toString(products));
		sb.append('}');
		return sb.toString();
	}
}
