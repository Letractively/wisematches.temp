package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderItem {
    String getName();

    Integer getArticle();


    int getQuantity();

    double getAmount();


    double getWeight();

    String getOptions();
}
