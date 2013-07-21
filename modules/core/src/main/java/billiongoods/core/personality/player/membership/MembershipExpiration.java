package billiongoods.core.personality.player.membership;

import billiongoods.core.expiration.ExpirationType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum MembershipExpiration implements ExpirationType {
    DAY("day", 24 * 60 * 60 * 1000);

    private final String code;
    private final long remainedMillis;

    MembershipExpiration(String code, long remainedMillis) {
        this.code = code;
        this.remainedMillis = remainedMillis;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public long getRemainedTime() {
        return remainedMillis;
    }

    @Override
    public long getTriggerTime(long extinctionTime) {
        return extinctionTime - remainedMillis;
    }
}
