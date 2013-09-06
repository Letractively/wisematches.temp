package billiongoods.server.warehouse;

import java.util.EnumSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductContext {
	private final String name;
	private final boolean arrival;
	private final Category category;
	private final boolean subCategories;
	private final EnumSet<ProductState> productStates;

	public static final EnumSet<ProductState> ACTIVE_ONLY = EnumSet.of(ProductState.ACTIVE);
	public static final EnumSet<ProductState> PROMOTED_ONLY = EnumSet.of(ProductState.PROMOTED);
	public static final EnumSet<ProductState> NOT_REMOVED = EnumSet.of(ProductState.DISABLED, ProductState.ACTIVE, ProductState.PROMOTED);

	public static final EnumSet<ProductState> VISIBLE = EnumSet.of(ProductState.ACTIVE, ProductState.PROMOTED);

	public ProductContext(Category category) {
		this(category, false, null, false);
	}

	public ProductContext(Category category, boolean subCategories, String name, boolean arrival) {
		this(category, subCategories, name, arrival, VISIBLE);
	}

	public ProductContext(Category category, boolean subCategories, String name, boolean arrival, EnumSet<ProductState> productStates) {
		this.name = name;
		this.productStates = productStates;
		this.arrival = arrival;
		this.category = category;
		this.subCategories = subCategories;
	}

	public String getName() {
		return name;
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

	public EnumSet<ProductState> getProductStates() {
		return productStates;
	}
}
