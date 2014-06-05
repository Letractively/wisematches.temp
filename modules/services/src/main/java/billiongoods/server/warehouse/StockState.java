package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum StockState {
	IN_STOCK(true),
	OUT_STOCK(true),
	SOLD_OUT(false);

	private final boolean accessible;

	StockState(boolean accessible) {
		this.accessible = accessible;
	}

	public boolean isAccessible() {
		return accessible;
	}
}
