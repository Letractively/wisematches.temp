package billiongoods.server.services.showcase;

import billiongoods.server.warehouse.ProductContext;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ShowcaseItem {
	String getName();

	String getMoreInfoUri();

	ProductContext getProductContext();
}
