package billiongoods.server.services.price.impl.loader;

import billiongoods.server.services.price.impl.PriceLoader;
import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.SupplierInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodPriceLoader implements PriceLoader {
    private static final Pattern PRICE_PATTERN = Pattern.compile("<div (?:[^\\s]+).*?id=\"(price_sub|regular_div)\".*?>\\(?(.+?)\\)?</div>");

    public BanggoodPriceLoader() {
    }

    @Override
    public Price loadPrice(SupplierInfo supplierInfo) throws PriceLoadingException {
        final URL url = supplierInfo.getReferenceUrl();
        try {
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDefaultUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);

            try (final InputStream inputStream = urlConnection.getInputStream()) {
                final Price price = parsePrice(inputStream);
                if (price == null) {
                    throw new PriceLoadingException("Price wasn't loaded from server response: " + url.toExternalForm());
                }
                return price;
            }
        } catch (IOException ex) {
            throw new PriceLoadingException("Price can't be loaded: " + url.toExternalForm(), ex);
        }
    }

    private Price parsePrice(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        double price = Double.NaN;
        int linesAfterPrice = -1;
        String s = reader.readLine();
        while (s != null) {
            final Matcher matcher = PRICE_PATTERN.matcher(s.trim());
            if (matcher.matches()) {
                final String type = matcher.group(1);
                final String p = matcher.group(2);

                if ("price_sub".equals(type)) {
                    linesAfterPrice = 0;
                    price = Double.parseDouble(p);
                }
                if ("regular_div".equals(type)) {
                    return new Price(price, Double.parseDouble(p));
                }
            }

            if (linesAfterPrice > 10) {
                return new Price(price, null);
            } else if (linesAfterPrice >= 0) {
                linesAfterPrice++;
            }

            s = reader.readLine();
        }
        return null;
    }
}
