package billiongoods.server.web.servlet.mvc.warehouse.form;

import java.util.Arrays;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BasketItemForm {
    private int quantity;
    private Integer article;

    private Integer[] optionIds;
    private String[] optionValues;

    public BasketItemForm() {
    }

    public Integer getArticle() {
        return article;
    }

    public void setArticle(Integer article) {
        this.article = article;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer[] getOptionIds() {
        return optionIds;
    }

    public void setOptionIds(Integer[] optionIds) {
        this.optionIds = optionIds;
    }

    public String[] getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(String[] optionValues) {
        this.optionValues = optionValues;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BasketItemForm{");
        sb.append("article=").append(article);
        sb.append(", quantity=").append(quantity);
        sb.append(", optionIds=").append(Arrays.toString(optionIds));
        sb.append(", optionValues=").append(Arrays.toString(optionValues));
        sb.append('}');
        return sb.toString();
    }
}
