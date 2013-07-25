package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleDescription {
	Long getId();

	String getName();

	boolean isActive();


	Category getCategory();


	float getSellPrice();

	float getSellDiscount(); // Float.NaN


	/**
	 * Returns date when article will be available again or {@code null} if it's available right now.
	 *
	 * @return date when article will be available again or {@code null} if it's available right now.
	 */
	Date getRestockDate();

	/**
	 * Returns date when article was registered in the warehouse. Never null.
	 *
	 * @return the date when article was registered in the warehouse. Never null.
	 */
	Date getRegistrationDate();
}