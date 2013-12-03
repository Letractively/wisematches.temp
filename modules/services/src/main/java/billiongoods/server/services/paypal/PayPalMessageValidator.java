package billiongoods.server.services.paypal;

import com.paypal.core.*;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
class PayPalMessageValidator {
    private Map<String, String> ipnMap = new HashMap<>();
    private Map<String, String> configurationMap = null;
    private HttpConfiguration httpConfiguration = null;
    private String ipnEndpoint = Constants.EMPTY_STRING;
    private boolean isIpnVerified = false;
    private StringBuffer payload;

    private static final String ENCODING = "windows-1252";
    private static final long serialVersionUID = -7187275404183441828L;

    /**
     * Populates HttpConfiguration with connection specifics parameters
     */
    private void initialize() {
        httpConfiguration = new HttpConfiguration();
        ipnEndpoint = getIPNEndpoint();
        httpConfiguration.setEndPointUrl(ipnEndpoint);
        httpConfiguration.setConnectionTimeout(Integer
                .parseInt(configurationMap
                        .get(Constants.HTTP_CONNECTION_TIMEOUT)));
        httpConfiguration.setMaxRetry(Integer.parseInt(configurationMap
                .get(Constants.HTTP_CONNECTION_RETRY)));
        httpConfiguration.setReadTimeout(Integer.parseInt(configurationMap
                .get(Constants.HTTP_CONNECTION_READ_TIMEOUT)));
        httpConfiguration.setMaxHttpConnection(Integer
                .parseInt(configurationMap
                        .get(Constants.HTTP_CONNECTION_MAX_CONNECTION)));
    }

    public PayPalMessageValidator(Map<String, String[]> ipnMap, Map<String, String> configurationMap) {
        this.configurationMap = SDKUtil.combineDefaultMap(configurationMap);
        initialize();
        payload = new StringBuffer("cmd=_notify-validate");
        if (ipnMap != null) {
            for (Map.Entry<String, String[]> entry : ipnMap.entrySet()) {
                String name = entry.getKey();
                String[] value = entry.getValue();
                try {
                    this.ipnMap.put(URLDecoder.decode(name, ENCODING),
                            URLDecoder.decode(value[0], ENCODING));
                    payload.append("&").append(name).append("=")
                            .append(URLEncoder.encode(value[0], ENCODING));
                } catch (Exception e) {
                    LoggingManager.debug(PayPalMessageValidator.class, e.getMessage());
                }
            }
        }

    }

    /**
     * This method post back ipn payload to PayPal system for verification
     */
    public boolean validate() {
        Map<String, String> headerMap = new HashMap<String, String>();
        URL url = null;
        String res = Constants.EMPTY_STRING;
        HttpConnection connection = ConnectionManager.getInstance()
                .getConnection();

        try {

            connection.createAndconfigureHttpConnection(httpConfiguration);
            url = new URL(this.ipnEndpoint);
            headerMap.put("Host", url.getHost());
            res = Constants.EMPTY_STRING;
            if (!this.isIpnVerified) {
                res = connection.execute(null, payload.toString(), headerMap);
            }

        } catch (Exception e) {
            LoggingManager.debug(PayPalMessageValidator.class, e.getMessage());
        }

        // check notification validation
        if (res.equals("VERIFIED")) {
            isIpnVerified = true;
        }

        return isIpnVerified;
    }

    public Map<String, String> getIpnMap() {
        return ipnMap;
    }

    public String getIpnValue(String ipnName) {
        return this.ipnMap.get(ipnName);

    }

    private String getIPNEndpoint() {
        String ipnEPoint = null;
        ipnEPoint = configurationMap.get(Constants.IPN_ENDPOINT);
        if (ipnEPoint == null) {
            String mode = configurationMap.get(Constants.MODE);
            if (mode != null
                    && (Constants.SANDBOX.equalsIgnoreCase(configurationMap
                    .get(Constants.MODE).trim()))) {
                ipnEPoint = Constants.IPN_SANDBOX_ENDPOINT;
            } else if (mode != null
                    && (Constants.LIVE.equalsIgnoreCase(configurationMap.get(
                    Constants.MODE).trim()))) {
                ipnEPoint = Constants.IPN_LIVE_ENDPOINT;
            }
        }
        return ipnEPoint;
    }

    @Override
    public String toString() {
        return payload.toString();
    }
}
