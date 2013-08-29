package billiongoods.server.services.price.impl.loader;

import billiongoods.server.services.price.impl.PriceLoader;
import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.SupplierInfo;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodPriceLoader implements PriceLoader {
    private final DefaultHttpClient client;
    private final BasicHttpContext localContext = new BasicHttpContext();

    private static final int DEFAULT_TIMEOUT = 3000;
    private static final HttpHost HOST = new HttpHost("www.banggood.com");

    private static final Pattern REDIRECT_CHAIN = Pattern.compile("arr=\\[([^\\]]*)\\]");
    private static final Pattern REDIRECT_TOKENS = Pattern.compile("strbuf\\[(\\d+)\\]='([^']+)'");
    private static final Pattern PRICE_PATTERN = Pattern.compile("<div.*?id=\"(price_sub|regular_div)\".*?>\\(?(.+?)\\)?</div>");

    private static final Logger log = LoggerFactory.getLogger("billiongoods.price.BanggoodPriceLoader");

    public BanggoodPriceLoader() {
        final HttpParams params = new BasicHttpParams();
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        params.setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36");

        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (proxyHost != null && proxyPort != null) {
            final HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        HttpClientParams.setRedirecting(params, true);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_TIMEOUT);

        client = new DefaultHttpClient(params);
        localContext.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());

        initialize();
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
            final HttpResponse execute = client.execute(HOST, post, localContext);
            final String s = EntityUtils.toString(execute.getEntity());
            log.info("Country change response: " + s);
        } catch (Exception ex) {
            log.info("Country can't be changed: {}", ex.getMessage());
        }
    }

    @Override
    public Price loadPrice(SupplierInfo supplierInfo) throws PriceLoadingException {
        return loadPrice(supplierInfo.getReferenceUri(), 0);
    }

    private Price loadPrice(String uri, int iteration) throws PriceLoadingException {
        if (iteration >= 5) {
            throw new PriceLoadingException("Price can't be loaded after iteration " + iteration + " from " + uri);
        }

        try {
            final HttpGet request = new HttpGet("/" + uri);
            request.setHeader("Accept", "text/plain");
            request.setHeader("Accept-Language", "en");

            final HttpResponse execute = client.execute(HOST, request, localContext);
            final String s = EntityUtils.toString(execute.getEntity());
            if (s.startsWith("<html><head><meta http-equiv=")) {
                final String uri1 = parseJavaScriptRedirect(s);
                log.info("JavaScript redirect received for {} . Parsed to {}", uri, uri1);
                return loadPrice(uri1, iteration + 1);
            } else {
                return parsePrice(s);
            }
        } catch (SocketTimeoutException ex) {
            return loadPrice(uri, iteration + 1);
        } catch (Exception ex) {
            throw new PriceLoadingException("Price can't be loaded: " + uri + " " + ex.getMessage(), ex);
        }
    }

    private Price parsePrice(String data) throws IOException, PriceLoadingException {
        final Double[] d = new Double[2];

        int index = 0;
        final Matcher matcher = PRICE_PATTERN.matcher(data);
        while (matcher.find()) {
            log.info("Price parsed from {}", matcher.group(0));
            d[index++] = Double.valueOf(matcher.group(2));
        }
        if (d[0] == null) {
            throw new PriceLoadingException("Price can't be parsed from " + data);
        }
        return new Price(d[0], d[1]);
    }

    protected String parseJavaScriptRedirect(String response) {
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
        return b.toString();
    }
}
