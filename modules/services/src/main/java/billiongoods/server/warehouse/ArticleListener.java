package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleListener {
	void articleCreated(Article article);

	void articleUpdated(Article article);

	void articleRemoved(Article article);
}
