package billiongoods.core.expiration;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ExpirationListener<ID, T extends Enum<? extends ExpirationType>> {
    void expirationTriggered(ID entity, T type);
}
