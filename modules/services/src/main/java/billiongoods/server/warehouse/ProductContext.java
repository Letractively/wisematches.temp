package billiongoods.server.warehouse;

import java.util.EnumSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductContext {
	private final String search;
	private final boolean arrival;
	private final Category category;
	private final boolean subCategories;
	private final StockState stockState;
	private final boolean onlyRecommended;
	private final EnumSet<ProductState> productStates;

	public static final EnumSet<ProductState> ACTIVE_ONLY = EnumSet.of(ProductState.ACTIVE);
	public static final EnumSet<ProductState> PROMOTED_ONLY = EnumSet.of(ProductState.PROMOTED);
	public static final EnumSet<ProductState> NOT_REMOVED = EnumSet.of(ProductState.DISABLED, ProductState.ACTIVE, ProductState.PROMOTED);

	public static final EnumSet<ProductState> VISIBLE = EnumSet.of(ProductState.ACTIVE, ProductState.PROMOTED);

	public ProductContext(Category category) {
		this(category, false, null, false);
	}

	public ProductContext(Category category, boolean subCategories, String search, boolean arrival) {
		this(category, subCategories, search, arrival, VISIBLE, null);
	}

	public ProductContext(Category category, boolean subCategories, String search, boolean arrival, EnumSet<ProductState> productStates, StockState stockState) {
		this(category, subCategories, search, arrival, false, productStates, stockState);
	}

	public ProductContext(Category category, boolean subCategories, String search, boolean arrival, boolean onlyRecommended, EnumSet<ProductState> productStates, StockState stockState) {
		this.search = search;
		this.arrival = arrival;
		this.category = category;
		this.stockState = stockState;
		this.subCategories = subCategories;
		this.productStates = productStates;
		this.onlyRecommended = onlyRecommended;
	}

	public String getSearch() {
		return search;
	}

	public boolean isArrival() {
		return arrival;
	}

	public Category getCategory() {
		return category;
	}

	public boolean isSubCategories() {
		return subCategories;
	}

	public boolean isOnlyRecommended() {
		return onlyRecommended;
	}

	public StockState getStockState() {
		return stockState;
	}

	public EnumSet<ProductState> getProductStates() {
		return productStates;
	}
}
