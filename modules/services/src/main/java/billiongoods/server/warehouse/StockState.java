package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum StockState {
	IN_STOCK,
	OUT_STOCK,

	/**
	 * This state must be removed. It can be checked on view side to deside.
	 */
	@Deprecated
	LIMITED_NUMBER,

	SOLD_OUT
}
