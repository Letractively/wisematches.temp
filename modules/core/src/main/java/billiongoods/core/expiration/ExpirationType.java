package billiongoods.core.expiration;

/**
 * The {@code ExpirationEntity} is a base interface for any objects which have some lifetime and can be
 * expired and terminated.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ExpirationType {
    /**
     * Returns code name for the entity.
     *
     * @return the code name for the entity.
     */
    String getCode();

    /**
     * Returns remained time in milliseconds.
     *
     * @return the remained time in milliseconds.
     */
    long getRemainedTime();

    /**
     * Returns time when this type of expiration should be triggered.
     *
     * @param extinctionTime time when object will be removed.
     * @return the time before {@code dueTime} when this type of expiration should be triggered or date in the future
     * if trigger is not valid anymore.
     */
    long getTriggerTime(long extinctionTime);
}
