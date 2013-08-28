package billiongoods.server.services.price.impl.loader;

import billiongoods.server.services.price.impl.PriceLoader;
import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.SupplierInfo;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodPriceLoader implements PriceLoader {
	private static final Pattern REDIRECT_CHAIN = Pattern.compile("arr=\\[([^\\]]*)\\]");
	private static final Pattern REDIRECT_TOKENS = Pattern.compile("strbuf\\[(\\d+)\\]='([^']+)'");
	private static final Pattern PRICE_PATTERN = Pattern.compile("<div.*?id=\"(price_sub|regular_div)\".*?>\\(?(.+?)\\)?</div>");

	private static final Logger log = LoggerFactory.getLogger("billiongoods.price.BanggoodPriceLoader");

	public BanggoodPriceLoader() {
	}

	@Override
	public Price loadPrice(SupplierInfo supplierInfo) throws PriceLoadingException {
		return loadPrice(supplierInfo.getReferenceUrl(), 0);
	}

	private Price loadPrice(URL url, int iteration) throws PriceLoadingException {
		if (iteration >= 5) {
			throw new PriceLoadingException("Price can't be loaded after iteration " + iteration + " from URL " + url);
		}

		try {
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setReadTimeout(5000);
			urlConnection.setConnectTimeout(5000);
			urlConnection.setDefaultUseCaches(false);
			urlConnection.setInstanceFollowRedirects(true);

			final String s = IOUtils.toString(urlConnection.getInputStream());
			if (s.startsWith("<html><head><meta http-equiv=")) {
				return loadPrice(parseJavaScriptRedirect(s), iteration + 1);
			} else {
				return parsePrice(s);
			}
		} catch (SocketTimeoutException ex) {
			return loadPrice(url, iteration + 1);
		} catch (Exception ex) {
			throw new PriceLoadingException("Price can't be loaded: " + url.toExternalForm(), ex);
		}
	}

	private Price parsePrice(String data) throws IOException {
		final Double[] d = new Double[2];

		int index = 0;
		final Matcher matcher = PRICE_PATTERN.matcher(data);
		while (matcher.find()) {
			d[index++] = Double.valueOf(matcher.group(2));
		}
		if (d[0] == null) {
			return null;
		}
		return new Price(d[0], d[1]);
	}

	protected URL parseJavaScriptRedirect(String response) throws MalformedURLException {
		final Map<String, String> tokens = new HashMap<>();
		final Matcher matcher = REDIRECT_TOKENS.matcher(response);
		while (matcher.find()) {
			tokens.put(matcher.group(1), matcher.group(2));
		}

		final Matcher matcher1 = REDIRECT_CHAIN.matcher(response);
		matcher1.find();
		StringBuilder b = new StringBuilder();
		for (String s : matcher1.group(1).split(",")) {
			b.append(tokens.get(s));
		}
		return new URL("http://www.banggood.com" + b.toString());
	}
}
