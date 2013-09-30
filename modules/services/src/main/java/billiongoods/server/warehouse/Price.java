package billiongoods.server.warehouse;

import javax.persistence.Embeddable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class Price {
	private double amount;
	private Double primordialAmount;

	private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.ENGLISH);

	static {
		SYMBOLS.setMonetaryDecimalSeparator('.');
	}

	private static final DecimalFormat FORMAT = new DecimalFormat("0.00", SYMBOLS);

	@Deprecated
	Price() {
	}

	public Price(double amount) {
		this(amount, null);
	}

	public Price(double amount, Double primordialAmount) {
		this.amount = amount;
		this.primordialAmount = primordialAmount;
	}

	public double getAmount() {
		return amount;
	}

	public Double getPrimordialAmount() {
		return primordialAmount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Price)) return false;

		Price price1 = (Price) o;

		if (Double.compare(price1.amount, amount) != 0) return false;
		if (primordialAmount != null ? !primordialAmount.equals(price1.primordialAmount) : price1.primordialAmount != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = (int) (temp ^ (temp >>> 32));
		result = 31 * result + (primordialAmount != null ? primordialAmount.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Price{");
		sb.append("amount=").append(amount);
		sb.append(", primordialAmount=").append(primordialAmount);
		sb.append('}');
		return sb.toString();
	}

	public static double round(double price) {
		return java.lang.Math.round(price * 100d) / 100d;
	}

	public static String string(double price) {
		return FORMAT.format(round(price));
	}
}
