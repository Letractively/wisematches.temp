package billiongoods.core.personality.player.membership;

import billiongoods.core.personality.player.account.Account;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface MembershipListener {
    /**
     * Indicates that account's membership has been changed.
     *
     * @param account the account who's membership has been changed.
     * @param oldCard the old player's membership.
     * @param newCard the new player's membership.
     */
    void membershipCardUpdated(Account account, MembershipCard oldCard, MembershipCard newCard);
}
