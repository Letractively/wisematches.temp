package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Deprecated
public enum Membership {
    /**
     * Indicates that it's basic registered player.
     */
    BASIC,

    /**
     * Silver player. It's more well than the <i>GUEST<i> player.
     */
    SILVER,

    /**
     * Gold player. It's more well than the <i>GOLD<i> player.
     */
    GOLD,

    /**
     * o
     * Platinum player. A player with this membership has full access.
     */
    PLATINUM;

    public boolean isBasic() {
        return this == BASIC;
    }

    public boolean isSilver() {
        return this == SILVER;
    }

    public boolean isGold() {
        return this == GOLD;
    }

    public boolean isPlatinum() {
        return this == PLATINUM;
    }
}
