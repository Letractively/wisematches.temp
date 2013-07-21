package billiongoods.core.personality.player.membership;

import billiongoods.core.Membership;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface MembershipCard {
    /**
     * Checks is this membership card expired or not based on current timestamp.
     * <p/>
     * The {@link System#currentTimeMillis()} method is used to get current time for validation.
     *
     * @return {@code true} if membership is valid for this moment; {@code false} - otherwise.
     * @see System#currentTimeMillis()
     */
    boolean isExpired();

    /**
     * Returns expiration date for this membership card or {@code null} if it's permanent membership.
     *
     * @return the expiration date for this membership card or {@code null} if it's permanent membership.
     */
    Date getExpiration();

    /**
     * Returns current membership for this card.
     *
     * @return the current membership for this card.
     */
    Membership getMembership();

    /**
     * Returns valid membership for current moment. This method checks current time.
     *
     * @return valid membership for current moment. This method checks current time.
     */
    Membership getValidMembership();
}