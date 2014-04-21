package billiongoods.server.services.supplier.impl.banggood;

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
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodMobileDataLoader implements SupplierDataLoader, InitializingBean, DisposableBean {
	private HttpClient client;
	private CookieStore cookieStore;
	private HttpClientConnectionManager connectionManager;

	private static final int DEFAULT_TIMEOUT = 5000;
	private static final int DEFAULT_RESTOCK_TIME = 15 * 24 * 60 * 60 * 1000;

	private static final HttpHost HOST = new HttpHost("m.banggood.com");

	private static final Logger log = LoggerFactory.getLogger("billiongoods.price.BanggoodMobileDataLoader");

	public BanggoodMobileDataLoader() {
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

		cookieStore = new CookieStore() {
			private final BasicCookieStore store = new BasicCookieStore();

			@Override
			public void addCookie(Cookie cookie) {
				if (cookie.getName().equals("viewed_products")) {
					return;
				}
				store.addCookie(cookie);
			}

			@Override
			public List<Cookie> getCookies() {
				return store.getCookies();
			}

			@Override
			public boolean clearExpired(Date date) {
				return store.clearExpired(date);
			}

			@Override
			public void clear() {
				store.clear();
			}
		};
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
	}

	@Override
	public SupplierDescription loadDescription(SupplierInfo supplierInfo) throws DataLoadingException {
		final ProductDetails details = loadProductDetails(supplierInfo);
		if (details == null) {
			return null;
		}
		final StockInfo stockInfo = loadStockInfo(supplierInfo);
		return new DefaultSupplierDescription(details.getPrice(), stockInfo, details.getParameters());
	}

	protected StockInfo loadStockInfo(SupplierInfo supplier) throws DataLoadingException {
		try {
			final String referenceUri = supplier.getReferenceUri();

			final HttpPost request = new HttpPost("/index.php?com=detail&t=getStockMsg");
			request.setHeader("Origin", "http://m.banggood.com");
			request.setHeader("Referer", "http://m.banggood.com" + (referenceUri.startsWith("/") ? referenceUri : "/" + referenceUri));
			request.setHeader("contentType", "application/x-www-form-urlencoded; charset=UTF-8");

			final List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("products_id", supplier.getReferenceId()));
			nvps.add(new BasicNameValuePair("warehouse", "CN"));
			nvps.add(new BasicNameValuePair("wh", "CN"));
			nvps.add(new BasicNameValuePair("attr_poa_slt", "-1"));
			nvps.add(new BasicNameValuePair("ipcountry", "176")); // Russian Federation
			nvps.add(new BasicNameValuePair("getship", "0"));

			request.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			final HttpResponse execute = client.execute(HOST, request);
			final Map<String, Object> values = parseStockMsg(EntityUtils.toString(execute.getEntity()).trim());

			final int count = getInt("stocks", values);
			int shipDays = getInt("shipDays", values);
			if (shipDays == Integer.MIN_VALUE) {
				shipDays = partShipDays((String) values.get("msg"));
			}

			LocalDate arrivalDate = null;
			final String arrivaltimes = (String) values.get("expected_arrivaltimes");
			if (arrivaltimes != null && !arrivaltimes.isEmpty() && !"0000-00-00".equals(arrivaltimes)) {
				arrivalDate = LocalDate.parse(arrivaltimes, DateTimeFormatter.ISO_DATE).plusDays(3);
			}
			return new StockInfo(count, shipDays, arrivalDate);
		} catch (Exception ex) {
			throw new DataLoadingException("StockInfo - " + ex.getMessage(), ex);
		}
	}

	private int getInt(String name, Map<String, Object> values) {
		final Object o = values.get(name);
		if (o == null) {
			return Integer.MIN_VALUE;
		}
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		if (o instanceof String) {
			return Integer.parseInt((String) o);
		}
		return Integer.MIN_VALUE;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> parseStockMsg(String msg) throws DataLoadingException {
		try {
			return new ObjectMapper().readValue(msg, HashMap.class);
		} catch (IOException e) {
			throw new DataLoadingException("StockMsg can't be parsed: " + msg, e);
		}
	}

	protected ProductDetails loadProductDetails(SupplierInfo supplier) throws DataLoadingException {
		try {
			final String uri = supplier.getReferenceUri();
			final HttpGet request = new HttpGet(uri.startsWith("/") ? uri : "/" + uri);
			request.setHeader("Accept", "text/plain");
			request.setHeader("Accept-Language", "en");

			final HttpResponse execute = client.execute(HOST, request);
			if (execute.getStatusLine().getStatusCode() == 404) {
				return null;
			}
			return parseProductDetails(EntityUtils.toString(execute.getEntity()));
		} catch (DataLoadingException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DataLoadingException("PriceInfo - " + ex.getMessage(), ex);
		}
	}

	protected ProductDetails parseProductDetails(String data) throws IOException, DataLoadingException {
		final Document doc = Jsoup.parse(data);

		if (doc.select("title").text().contains("All Categories")) { // Product not found
			return null;
		}

		final Elements priceEl = doc.select("#product_price");
		final Elements pricePrimordialEl = doc.select("#product_discount");

		if (priceEl.size() != 1) {
			throw new DataLoadingException("No price in received data: " + data);
		}

		double price = Double.parseDouble(priceEl.get(0).ownText().trim().replace("US$", ""));
		Double primordial = null;
		if (pricePrimordialEl != null && pricePrimordialEl.size() == 1) {
			final String text = pricePrimordialEl.get(0).ownText().trim();
			if (text.length() != 0) {
				primordial = Double.valueOf(text.replace("US$", ""));
			}
		}

		final Map<String, Collection<String>> parameters = new HashMap<>();
		final Elements divs = doc.select("div.item02");
		for (Element div : divs) {
			for (Element option : div.children()) {
				if (option.id().startsWith("dvAttr")) { // it's attribute
					final String name = option.select("select").get(0).attr("title");
					final Elements values = option.select("option");

					final List<String> res = new ArrayList<>(values.size());
					for (Element value : values) {
						if ("-1".equals(value.attr("value"))) { // Please select
							continue;
						}
						res.add(value.text());
					}
					parameters.put(name, res);
				}
			}
		}
		return new ProductDetails(new Price(price, primordial), parameters);
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

	protected int partShipDays(String s) {
		final String msg = s.replace("\"", "").toLowerCase();
		if (msg.contains("dispatched in 1 business")) {
			return 1;
		} else if (msg.contains("dispatched in 1-3 business")) {
			return 3;
		} else if (msg.contains("dispatched in 2-4 business")) {
			return 4;
		} else if (msg.contains("dispatched in 3-6 business")) {
			return 6;
		} else if (msg.contains("dispatched in 6-9 business")) {
			return 9;
		} else if (msg.contains("expected restock on")) {
			return 0;
		}

		log.error("Unparseable stock info: {}", msg);
		return 0;
	}
}
