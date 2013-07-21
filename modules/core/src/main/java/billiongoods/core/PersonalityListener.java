package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PersonalityListener {
    /**
     * Indicates that new account has been created.
     *
     * @param player the created player account.
     */
    void playerRegistered(Personality player);

    /**
     * Indicates that account has been removed.
     *
     * @param player the removed account.
     */
    void playerUnregistered(Personality player);
}
