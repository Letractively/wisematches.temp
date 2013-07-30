package billiongoods.core;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class Member extends Player {
    private static final long serialVersionUID = -5921468650108338917L;

    protected Member(long id) {
        super(id);
    }

    public abstract String getEmail();

    public abstract String getNickname();

    public abstract TimeZone getTimeZone();

    public abstract Membership getMembership();

    @Override
    public final PersonalityType getType() {
        return PersonalityType.MEMBER;
    }
}
