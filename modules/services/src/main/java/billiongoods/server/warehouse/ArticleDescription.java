package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleDescription {
	Integer getId();

	String getName();


	/**
	 * Returns price for the article.
	 *
	 * @return the price for the article.
	 */
	Price getPrice();

	/**
	 * Returns item weight in kg
	 *
	 * @return the item weight in kg
	 */
	double getWeight();


	/**
	 * Returns category id for this article.
	 *
	 * @return the category id for this article.
	 */
	Integer getCategoryId();


	/**
	 * Returns moderator's commentary for this article. This value never must be shown for customers.
	 *
	 * @return the moderator's commentary for this article. This value never must be shown for customers.
	 */
	String getCommentary();

	/**
	 * Returns preview image id.
	 */
	String getPreviewImageId();

	/**
	 * Returns date when article was registered in the warehouse. Never null.
	 *
	 * @return the date when article was registered in the warehouse. Never null.
	 */
	Date getRegistrationDate();


	/**
	 * Returns current article state.
	 *
	 * @return the current article state.
	 */
	ArticleState getState();

	/**
	 * Returns information about article's stock state
	 *
	 * @return the information about article's stock state
	 */
	StockInfo getStockInfo();
}
