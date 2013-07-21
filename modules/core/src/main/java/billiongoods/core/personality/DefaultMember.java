package billiongoods.core.personality;

import billiongoods.core.Language;
import billiongoods.core.Member;
import billiongoods.core.Membership;
import billiongoods.core.personality.player.account.Account;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class DefaultMember extends Member {
    private String email;
    private String nickname;
    private TimeZone timeZone;
    private Language language;
    private Membership membership;

    private static final long serialVersionUID = -3657252453631101842L;

    public DefaultMember(Account account, Membership membership) {
        super(account.getId());
        update(account, membership);
    }

    public DefaultMember(long id, String nickname, String email, TimeZone timeZone, Membership membership, Language language) {
        super(id);
        this.email = email;
        this.nickname = nickname;
        this.timeZone = timeZone;
        this.language = language;
        this.membership = membership;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public TimeZone getTimeZone() {
        return timeZone;
    }

    @Override
    public Membership getMembership() {
        return membership;
    }

    final void update(Account account, Membership membership) {
        if (!getId().equals(account.getId())) {
            throw new IllegalArgumentException("Incorrect account to be updated");
        }
        this.nickname = account.getNickname();
        this.email = account.getEmail();
        this.timeZone = account.getTimeZone();
        this.language = account.getLanguage();
        this.membership = membership;
    }
}
