package billiongoods.server.services.price;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class PriceMath {
    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.ENGLISH);

    static {
        SYMBOLS.setMonetaryDecimalSeparator('.');
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("0.00", SYMBOLS);

    private PriceMath() {
    }

    public static double round(double price) {
        return java.lang.Math.round(price * 100d) / 100d;
    }

    public static String string(double price) {
        return FORMAT.format(round(price));
    }
}
