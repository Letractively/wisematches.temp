package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleContext {
	private final Category category;

	public ArticleContext() {
		this(null);
	}

	public ArticleContext(Category category) {
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}
}
