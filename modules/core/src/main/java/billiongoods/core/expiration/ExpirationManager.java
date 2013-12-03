package billiongoods.core.expiration;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ExpirationManager<ID, T extends Enum<? extends ExpirationType>> {
    void addExpirationListener(ExpirationListener<ID, T> l);

    void removeExpirationListener(ExpirationListener<ID, T> l);


    T[] getExpirationPoints();
}
