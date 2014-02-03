package billiongoods.server.services.wishlist;

import billiongoods.core.Personality;
import billiongoods.core.search.SearchManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface WishlistManager extends SearchManager<Integer, Personality, Void> {
	void addWishProducts(Personality person, Integer... productId);

	void removeWishProducts(Personality person, Integer... productId);
}
