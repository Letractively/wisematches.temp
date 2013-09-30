package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductDescription extends ProductImager {
	@Override
	Integer getId();

	String getName();


	/**
	 * Returns price for the product.
	 *
	 * @return the price for the product.
	 */
	Price getPrice();

	/**
	 * Returns item weight in kg
	 *
	 * @return the item weight in kg
	 */
	double getWeight();


	/**
	 * Returns category id for this product.
	 *
	 * @return the category id for this product.
	 */
	@Override
	Integer getCategoryId();


	/**
	 * Returns moderator's commentary for this product. This value never must be shown for customers.
	 *
	 * @return the moderator's commentary for this product. This value never must be shown for customers.
	 */
	String getCommentary();

	/**
	 * Returns preview image id.
	 */
	String getPreviewImageId();

	/**
	 * Returns date when product was registered in the warehouse. Never null.
	 *
	 * @return the date when product was registered in the warehouse. Never null.
	 */
	Date getRegistrationDate();

	/**
	 * Returns current product state.
	 *
	 * @return the current product state.
	 */
	ProductState getState();

	/**
	 * Returns information about product's stock state
	 *
	 * @return the information about product's stock state
	 */
	StockInfo getStockInfo();

	/**
	 * Returns reference to supplier
	 *
	 * @return the reference to supplier
	 */
	SupplierInfo getSupplierInfo();
}
