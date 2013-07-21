package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Article extends ArticleSummary, ArticleReference {
    /**
     * Returns number of sold articles.
     *
     * @return number of sold articles.
     */
    int getSoldCount();

    /**
     * Returns available options for this article which customer can choice.
     *
     * @return available options for this article which customer can choice or {@code null} if there is no one.
     */
    List<Option> geOptions();

    /**
     * Get accessories for this articles or {@code null} ir there is no one.
     *
     * @return accessories for this articles or {@code null} ir there is no one.
     */
    List<Article> getAccessories();

    /**
     * Returns characters for this article.
     *
     * @return the characters for this article.
     */
    List<Character> getCharacters();
}