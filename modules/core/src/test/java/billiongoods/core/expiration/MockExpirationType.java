package billiongoods.core.expiration;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum MockExpirationType implements ExpirationType {
    ONE("mock1", 50000),
    TWO("mock2", 10000);

    private final String code;
    private final long diff;

    MockExpirationType(String code, long diff) {
        this.code = code;
        this.diff = diff;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public long getRemainedTime() {
        return diff;
    }

    @Override
    public long getTriggerTime(long extinctionTime) {
        return extinctionTime - diff;
    }
}
