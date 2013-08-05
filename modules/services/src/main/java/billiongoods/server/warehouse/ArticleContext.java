package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleContext {
	private final boolean arrival;
	private final Category category;
	private final boolean subCategories;

	public ArticleContext(Category category) {
		this(category, false, false);
	}

	public ArticleContext(Category category, boolean subCategories) {
		this(category, subCategories, false);
	}

	public ArticleContext(Category category, boolean subCategories, boolean arrival) {
		this.category = category;
		this.subCategories = subCategories;
		this.arrival = arrival;
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
