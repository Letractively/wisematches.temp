package billiongoods.server.warehouse;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Supplier {
	BANGGOOD("http://www.banggood.com") {
		@Override
		public String getReferenceId(SupplierInfo info) {
			String s = info.getReferenceUri();
			return s.substring(s.lastIndexOf("-") + 1, s.length() - 5);
		}
	};

	private final String site;

	Supplier(String site) {
		this.site = site;
	}

	public String getSite() {
		return site;
	}

	public URL getReferenceUrl(String path) {
		try {
			if (path == null) {
				return new URL(site);
			}
			return new URL(site + (path.startsWith("/") ? path : "/" + path));
		} catch (MalformedURLException ex) {
			throw new IllegalStateException("Very bad, http is illegal URL: " + site + path);
		}
	}

	public URL getReferenceUrl(SupplierInfo info) {
		return getReferenceUrl(info.getReferenceUri());
	}

	public abstract String getReferenceId(SupplierInfo info);
}
