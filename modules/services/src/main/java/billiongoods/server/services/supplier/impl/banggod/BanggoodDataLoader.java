package billiongoods.server.services.supplier.impl.banggod;

import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.services.supplier.impl.DefaultSupplierDescription;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.SupplierInfo;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodDataLoader implements SupplierDataLoader {
	private final HttpClient client;

	private static final int DEFAULT_TIMEOUT = 3000;
	private static final HttpHost HOST = new HttpHost("www.banggood.com");

//	private static final Pattern PRICE_PATTERN = Pattern.compile("<div.*?id=\"(price_sub|regular_div)\".*?>\\(?(.+?)\\)?</div>");

	private static final Logger log = LoggerFactory.getLogger("billiongoods.price.BanggoodDataLoader");

	private final ScriptEngine javascript = new ScriptEngineManager().getEngineByName("javascript");

	public BanggoodDataLoader() {
		final SocketConfig.Builder socketConfig = SocketConfig.custom();
		socketConfig.setSoTimeout(DEFAULT_TIMEOUT);

		final RequestConfig.Builder requestConfig = RequestConfig.custom();
		requestConfig.setCircularRedirectsAllowed(false);

		final HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultSocketConfig(socketConfig.build());
		builder.setDefaultRequestConfig(requestConfig.build());

		builder.setDefaultCookieStore(new BasicCookieStore());

		builder.setUserAgent("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36");

		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (proxyHost != null && proxyPort != null) {
			builder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
		}

		client = builder.build();
	}

	@Override
	public void initialize() {
		final HttpPost post = new HttpPost("/ajax_module.php");
		final List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("action", "setDefaltCountry"));
		nvps.add(new BasicNameValuePair("bizhong", "USD"));
		nvps.add(new BasicNameValuePair("counrty", "176"));

		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		try {
			final HttpResponse execute = client.execute(HOST, post);
			final String s = EntityUtils.toString(execute.getEntity());
			log.info("Country change response: " + s);
		} catch (Exception ex) {
			log.info("Country can't be changed: {}", ex.getMessage());
		}
	}

	@Override
	public SupplierDescription loadDescription(SupplierInfo supplierInfo) throws DataLoadingException {
		return loadDescription(supplierInfo.getReferenceUri(), 0);
	}

	private SupplierDescription loadDescription(String uri, int iteration) throws DataLoadingException {
		if (iteration >= 5) {
			throw new DataLoadingException("Price can't be loaded after iteration " + iteration + " from " + uri);
		}

		try {
			final HttpGet request = new HttpGet(uri.startsWith("/") ? uri : "/" + uri);
			request.setHeader("Accept", "text/plain");
			request.setHeader("Accept-Language", "en");

			final HttpResponse execute = client.execute(HOST, request);
			final String s = EntityUtils.toString(execute.getEntity());
			if (s.startsWith("<html><head><meta http-equiv=")) {
				final String uri1 = parseJavaScriptRedirect(s);
				log.info("JavaScript redirect received for {} . Parsed to {}", uri, uri1);
				return loadDescription(uri1, iteration + 1);
			} else {
				return parseDescription(s);
			}
		} catch (SocketTimeoutException ex) {
			return loadDescription(uri, iteration + 1);
		} catch (Exception ex) {
			throw new DataLoadingException("Price can't be loaded: " + uri + " " + ex.getMessage(), ex);
		}
	}

	private SupplierDescription parseDescription(String data) throws IOException, DataLoadingException {
/*
		final Double[] d = new Double[2];

		int index = 0;
		final Matcher matcher = PRICE_PATTERN.matcher(data);
		while (matcher.find()) {
			log.info("Price parsed from {}", matcher.group(0));
			d[index++] = Double.valueOf(matcher.group(2).replace("US$", ""));
		}
		if (d[0] == null) {
			throw new DataLoadingException("Price can't be parsed from " + data);
		}

		final Price price = new Price(d[0], d[1]);
*/
		final Document doc = Jsoup.parse(data);

		final Elements priceEl = doc.select("#price_sub");
		final Elements pricePrimordialEl = doc.select("#regular_div");

		double price = Double.parseDouble(priceEl.text());
		Double primordial = null;
		if (pricePrimordialEl != null && pricePrimordialEl.isEmpty()) {
			final String text = pricePrimordialEl.text().trim();
			if (text.length() != 0) {
				primordial = Double.valueOf(text.substring(1, text.length() - 1).trim());
			}
		}

		final Map<String, Collection<String>> parameters = new HashMap<>();

		final Elements paramsUl = doc.select("ul.attrParent");
		for (Element element : paramsUl) {
			final String name = element.parent().select("span.tit").first().child(0).text();

			final List<String> values = new ArrayList<>();
			final Elements select = element.select("a.attr_options");
			for (Element v : select) {
				values.add(v.text());
			}
			parameters.put(name, values);
		}
		return new DefaultSupplierDescription(new Price(price, primordial), parameters);
	}

	protected String parseJavaScriptRedirect(String response) {
		try {
			response = response.substring(response.indexOf("JavaScript") + 12, response.lastIndexOf("</script>"));
			javascript.eval(response.replace("window.location.href=", " var res="));
			return (String) javascript.get("res");
		} catch (ScriptException ex) {
			return null;
		}
	}
}
