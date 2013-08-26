package billiongoods.server.warehouse;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Supplier {
    BANGGOOD("http://www.banggood.com/");

    private final String site;

    Supplier(String site) {
        this.site = site;
    }

    public String getSite() {
        return site;
    }

    public URL getReferenceUrl(String path) {
        try {
            return new URL(site + path);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Very bad, http is illegal URL: " + site + path);
        }
    }

    public URL getReferenceUrl(SupplierInfo info) {
        return getReferenceUrl(info.getReferenceUri());
    }
}
