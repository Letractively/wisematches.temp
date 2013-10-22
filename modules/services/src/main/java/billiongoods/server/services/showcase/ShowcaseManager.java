package billiongoods.server.services.showcase;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ShowcaseManager {
	void addShowcaseListener(ShowcaseListener l);

	void removeShowcaseListener(ShowcaseListener l);


	Showcase getShowcase();


	void reloadShowcase();
}
