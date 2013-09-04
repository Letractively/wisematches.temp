package billiongoods.server.warehouse;

import java.util.EnumSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleContext {
	private final String name;
	private final boolean arrival;
	private final Category category;
	private final boolean subCategories;
	private final EnumSet<ArticleState> articleStates;

	public static final EnumSet<ArticleState> ACTIVE_ONLY = EnumSet.of(ArticleState.ACTIVE);
	public static final EnumSet<ArticleState> PROMOTED_ONLY = EnumSet.of(ArticleState.PROMOTED);
	public static final EnumSet<ArticleState> NOT_REMOVED = EnumSet.of(ArticleState.DISABLED, ArticleState.ACTIVE, ArticleState.PROMOTED);

	public static final EnumSet<ArticleState> VISIBLE = EnumSet.of(ArticleState.ACTIVE, ArticleState.PROMOTED);

	public ArticleContext(Category category) {
		this(category, false, null, false);
	}

	public ArticleContext(Category category, boolean subCategories, String name, boolean arrival) {
		this(category, subCategories, name, arrival, VISIBLE);
	}

	public ArticleContext(Category category, boolean subCategories, String name, boolean arrival, EnumSet<ArticleState> articleStates) {
		this.name = name;
		this.articleStates = articleStates;
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

	public EnumSet<ArticleState> getArticleStates() {
		return articleStates;
	}
}
