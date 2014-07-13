package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderDiscount {
    double getAmount();

    String getCoupon();
}
