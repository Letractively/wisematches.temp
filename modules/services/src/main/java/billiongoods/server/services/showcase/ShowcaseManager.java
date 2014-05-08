package billiongoods.server.services.showcase;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ShowcaseManager {
	void addShowcaseListener(ShowcaseListener l);

	void removeShowcaseListener(ShowcaseListener l);


	void createItem(Integer section, Integer position, String name, Integer category, boolean arrival, boolean sub);

	void removeItem(Integer section, Integer position);

	Showcase getShowcase();

	void reloadShowcase();
}
