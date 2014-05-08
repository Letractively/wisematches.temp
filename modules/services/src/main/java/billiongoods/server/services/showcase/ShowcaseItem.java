package billiongoods.server.services.showcase;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ShowcaseItem {
	String getName();

	Integer getSection();

	Integer getPosition();


	Integer getCategory();


	boolean isArrival();

	boolean isSubCategories();
}
