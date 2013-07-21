package billiongoods.core.personality.player.membership;

import billiongoods.core.Membership;
import billiongoods.core.expiration.ExpirationManager;
import billiongoods.core.personality.player.account.Account;

import java.util.Date;

/**
 * {@code MembershipManager} tracks current membership status and provides ability to
 * update player's membership.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface MembershipManager extends ExpirationManager<Account, MembershipExpiration> {
    void addMembershipListener(MembershipListener l);

    void removeMembershipListener(MembershipListener l);


    /**
     * Returns current player's membership.
     * <p/>
     * This method checks membership expiration before
     * return and fires {@link MembershipListener#membershipCardUpdated(Account, MembershipCard, MembershipCard)}
     * event if it's expired. The event can be fire in the same or in separate thread.
     * <p/>
     * If person is unknown {@link billiongoods.core.Membership#BASIC} will be returned.
     *
     * @param personality the personality who's membership should be returned.
     * @return the person's membership.
     * @see MembershipListener#membershipCardUpdated(Account, MembershipCard, MembershipCard)
     */
    Membership getMembership(Account personality);

    /**
     * Returns original membership card associated with the person.
     *
     * @param person the person who's membership card must be returned.
     * @return the person's membership card or {@code null} if person doesn't have
     * any membership at this moment.
     */
    MembershipCard getPlayerMembership(Account person);

    /**
     * Removes membership from specified player.
     *
     * @param person the person who's membership should be removed.
     * @return old membership card associated with the person.
     */
    MembershipCard removePlayerMembership(Account person);

    /**
     * Changes current the person's membership.
     *
     * @param person     the person who's membership must be changed.
     * @param membership the new person's membership.
     * @param expiration the expiration date.
     * @return registered membership card.
     */
    MembershipCard updatePlayerMembership(Account person, Membership membership, Date expiration);
}
