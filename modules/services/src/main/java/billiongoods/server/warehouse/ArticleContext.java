package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleContext {
	private final String name;
	private final boolean arrival;
	private final boolean inactive;
	private final Category category;
	private final boolean subCategories;

	public ArticleContext(Category category) {
		this(category, false, null, false);
	}

	public ArticleContext(Category category, boolean subCategories, String name, boolean arrival) {
		this(category, subCategories, name, arrival, false);
	}

	public ArticleContext(Category category, boolean subCategories, String name, boolean arrival, boolean inactive) {
		this.name = name;
		this.inactive = inactive;
		this.arrival = arrival;
		this.category = category;
		this.subCategories = subCategories;
	}

	public String getName() {
		return name;
	}

	public boolean isInactive() {
		return inactive;
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
}
