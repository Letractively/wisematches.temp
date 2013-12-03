package billiongoods.server.services.supplier.impl.banggod;

import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.services.supplier.impl.DefaultSupplierDescription;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;
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
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodDataLoader implements SupplierDataLoader, InitializingBean, DisposableBean {
	private HttpClient client;
	private BasicCookieStore cookieStore;
	private HttpClientConnectionManager connectionManager;

	private final ScriptEngine javascript = new ScriptEngineManager().getEngineByName("javascript");

	private static final int DEFAULT_TIMEOUT = 5000;
	private static final HttpHost HOST = new HttpHost("www.banggood.com");
	private static final SimpleDateFormat RESTOCK_DATE_FROMAT = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.price.BanggoodDataLoader");

	public BanggoodDataLoader() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		createClient();
	}

	@Override
	public void destroy() throws Exception {
		closeClient();
	}

	private void createClient() {
		closeClient();

		cookieStore = new BasicCookieStore();
		connectionManager = new BasicHttpClientConnectionManager();

		final SocketConfig.Builder socketConfig = SocketConfig.custom();
		socketConfig.setSoTimeout(DEFAULT_TIMEOUT);

		final RequestConfig.Builder requestConfig = RequestConfig.custom();
		requestConfig.setSocketTimeout(DEFAULT_TIMEOUT);
		requestConfig.setConnectTimeout(DEFAULT_TIMEOUT);
		requestConfig.setAuthenticationEnabled(false);
		requestConfig.setCircularRedirectsAllowed(false);

		final HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultSocketConfig(socketConfig.build());
		builder.setDefaultRequestConfig(requestConfig.build());

		builder.setUserAgent("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36");

		builder.setDefaultCookieStore(cookieStore);
		builder.setConnectionManager(connectionManager);

		builder.setConnectionReuseStrategy(new DefaultConnectionReuseStrategy());

		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (proxyHost != null && proxyPort != null) {
			builder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
		}

		client = builder.build();
	}

	private void closeClient() {
		if (client instanceof Closeable) {
			try {
				((Closeable) client).close();
			} catch (IOException ex) {
				log.info("Client can't be closed", ex);
			}
		}

		if (connectionManager != null) {
			connectionManager.shutdown();
			connectionManager = null;
		}

		if (cookieStore != null) {
			cookieStore.clear();
		}
	}


	@Override
	public void initialize() {
		createClient();
		changeCountryCode();
	}

	private void changeCountryCode() {
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
		final StockInfo stockInfo = loadStockInfo(supplierInfo);
		final ProductDetails details = loadProductDetails(supplierInfo);
		return new DefaultSupplierDescription(details.getPrice(), stockInfo, details.getParameters());
	}

	private StockInfo loadStockInfo(SupplierInfo supplier) throws DataLoadingException {
		try {
			final HttpPost request = new HttpPost("/ajax_module.php");
			request.addHeader("Origin", supplier.getWholesaler().getSite());
			request.addHeader("Referer", supplier.getReferenceUrl().toString());

			final List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("action", "get_stocks"));
			nvps.add(new BasicNameValuePair("sku", supplier.getReferenceCode()));
			nvps.add(new BasicNameValuePair("warehouse", "CN"));
			nvps.add(new BasicNameValuePair("products_id", supplier.getReferenceId()));

			request.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			final HttpResponse execute = client.execute(HOST, request);
			String s = EntityUtils.toString(execute.getEntity()).trim();
			s = s.substring(s.indexOf("[") + 1, s.lastIndexOf("]"));
			final String[] split = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

			final String status = split[0];
			if ("\"success\"".equals(status)) {
				final String msg = split[1].replace("\"", "").toLowerCase();
				if (msg.contains("in stock") ||
						msg.contains("usually dispatched in 1-3") ||
						msg.contains("usually dispatched in 2-4") ||
						msg.contains("usually dispatched in 3-6")) {
					return new StockInfo(null, null);
				} else if (msg.contains("out of stock, expected restock in 15")) {
					return new StockInfo(null, new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000));
				} else if (msg.contains("expect restock on")) {
					final Date parse = RESTOCK_DATE_FROMAT.parse(msg.substring(18).replaceAll("st|nd|rd|th", ""));
					return new StockInfo(null, parse);
				} else if (msg.contains("units available") || msg.contains("units left")) {
					int fi = msg.indexOf(" units");
					int bi = msg.lastIndexOf(" ", fi - 1);
					if (bi < 0) {
						bi = 0;
					}
					return new StockInfo(Integer.parseInt(msg.substring(bi, fi).trim()), null);
				} else if (msg.contains("out of stock") || msg.contains("sold out currently!")) {
					return new StockInfo(0, null);
				} else if (msg.contains("coming soon")) {
					return new StockInfo(0, null);
				} else if (msg.contains("in transit")) {
					return new StockInfo(0, null);
				} else if (msg.contains("factory product")) {
					return new StockInfo(null, new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000));
				}
				log.error("Unparseable stock info: {}", msg);
				return new StockInfo(0, null);
			}
			throw new DataLoadingException("StockInfo incorrect status: " + status);
		} catch (DataLoadingException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DataLoadingException("StockInfo - " + ex.getMessage(), ex);
		}
	}

	private ProductDetails loadProductDetails(SupplierInfo supplier) throws DataLoadingException {
		return loadProductDetails(supplier, 0);
	}

	private ProductDetails loadProductDetails(SupplierInfo supplier, int iteration) throws DataLoadingException {
		try {
			final String uri = supplier.getReferenceUri();
			final HttpGet request = new HttpGet(uri.startsWith("/") ? uri : "/" + uri);
			request.setHeader("Accept", "text/plain");
			request.setHeader("Accept-Language", "en");

			final HttpResponse execute = client.execute(HOST, request);
			final String s = EntityUtils.toString(execute.getEntity());
			if (s.startsWith("<html><head><meta http-equiv=")) {
				final String uri1 = parseJavaScriptRedirect(s);
				log.info("JavaScript redirect received for {} . Parsed to {}", uri, uri1);
				return loadProductDetails(supplier, iteration + 1);
			} else if (s.startsWith("<html><body><h1>400 Bad request")) {
				log.info("400 Bad request received. Reinitialize client for {}", uri);
				initialize();
				return loadProductDetails(supplier, iteration + 1);
			} else {
				return parseProductDetails(s);
			}
		} catch (Exception ex) {
			throw new DataLoadingException("PriceInfo - " + ex.getMessage(), ex);
		}
	}

	private ProductDetails parseProductDetails(String data) throws IOException, DataLoadingException {
		final Document doc = Jsoup.parse(data);

		final Elements priceEl = doc.select("#price_sub");
		final Elements pricePrimordialEl = doc.select("#regular_div");

		if (priceEl.isEmpty()) {
			throw new DataLoadingException("No price in received data: " + data);
		}

		double price = Double.parseDouble(priceEl.text().replace("US$", ""));
		Double primordial = null;
		if (pricePrimordialEl != null && !pricePrimordialEl.isEmpty()) {
			final String text = pricePrimordialEl.text().trim();
			if (text.length() != 0) {
				primordial = Double.valueOf(text.substring(1, text.length() - 1).replace("US$", "").trim());
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
		return new ProductDetails(new Price(price, primordial), parameters);
	}

	protected String parseJavaScriptRedirect(String response) throws DataLoadingException {
		try {
			response = response.substring(response.indexOf("JavaScript") + 12, response.lastIndexOf("</script>"));
			javascript.eval(response.replace("window.location.href=", " var res="));
			return (String) javascript.get("res");
		} catch (ScriptException ex) {
			throw new DataLoadingException("Dynamic redirect script can't be processed: " + response, ex);
		}
	}

	private static final class ProductDetails {
		private final Price price;
		private final Map<String, Collection<String>> parameters;

		private ProductDetails(Price price, Map<String, Collection<String>> parameters) {
			this.price = price;
			this.parameters = parameters;
		}

		public Price getPrice() {
			return price;
		}

		public Map<String, Collection<String>> getParameters() {
			return parameters;
		}
	}
}
