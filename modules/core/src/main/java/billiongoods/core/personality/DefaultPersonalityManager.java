package billiongoods.core.personality;

import billiongoods.core.*;
import billiongoods.core.cache.NoOpCache;
import billiongoods.core.personality.player.account.Account;
import billiongoods.core.personality.player.account.AccountListener;
import billiongoods.core.personality.player.account.AccountManager;
import billiongoods.core.personality.player.membership.MembershipCard;
import billiongoods.core.personality.player.membership.MembershipListener;
import billiongoods.core.personality.player.membership.MembershipManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultPersonalityManager implements PersonalityManager, InitializingBean {
    private Cache playersCache = NoOpCache.INSTANCE;

    private AccountManager accountManager;
    private MembershipManager membershipManager;

    private final Map<Language, Visitor> visitorMap = new HashMap<>();

    private final Map<Long, Personality> predefinedPlayers = new HashMap<>();

    private final TheMemberPlayerListener memberPlayerListener = new TheMemberPlayerListener();
    private final Collection<PersonalityListener> listeners = new CopyOnWriteArraySet<>();

    public DefaultPersonalityManager() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Language language : Language.values()) {
            final Visitor visitor = new DefaultVisitor(language);
            visitorMap.put(language, visitor);
            predefinedPlayers.put(visitor.getId(), visitor);
        }
    }

    @Override
    public void addPersonalityListener(PersonalityListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removePersonalityListener(PersonalityListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Personality getPerson(Long id) {
        if (id < 1000) {
            return predefinedPlayers.get(id);
        } else {
            return getMember(id);
        }
    }

    @Override
    public Member getMember(Long id) {
        if (id < 1000) {
            return null;
        }

        final DefaultMember cachedMember = getCachedMember(id);
        if (cachedMember != null) {
            return cachedMember;
        }

        final Account account = accountManager.getAccount(id);
        if (account != null) {
            final DefaultMember memberPlayer = createMemberPlayer(account);
            playersCache.put(memberPlayer.getId(), memberPlayer);
            return memberPlayer;
        }
        return null;
    }

    @Override
    public Visitor getVisitor(Language language) {
        return visitorMap.get(language);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.playersCache = cacheManager.getCache("personality");

        if (this.playersCache == null) {
            throw new IllegalArgumentException("CacheManager doesn't have 'personality' cache");
        }
    }

    public void setAccountManager(AccountManager accountManager) {
        if (this.accountManager != null) {
            this.accountManager.removeAccountListener(memberPlayerListener);
        }

        this.accountManager = accountManager;

        if (this.accountManager != null) {
            this.accountManager.addAccountListener(memberPlayerListener);
        }
    }

    public void setMembershipManager(MembershipManager membershipManager) {
        if (this.membershipManager != null) {
            this.membershipManager.removeMembershipListener(memberPlayerListener);
        }

        this.membershipManager = membershipManager;

        if (this.membershipManager != null) {
            this.membershipManager.addMembershipListener(memberPlayerListener);
        }
    }

    private DefaultMember getCachedMember(Long id) {
        final Cache.ValueWrapper valueWrapper = playersCache.get(id);
        if (valueWrapper != null) {
            return (DefaultMember) valueWrapper.get();
        }
        return null;
    }

    private DefaultMember createMemberPlayer(Account account) {
        final Membership card = membershipManager.getMembership(account);
        return new DefaultMember(account, card);
    }

    private final class TheMemberPlayerListener implements AccountListener, MembershipListener {
        private TheMemberPlayerListener() {
        }

        @Override
        public void accountCreated(Account account) {
            for (PersonalityListener listener : listeners) {
                listener.playerRegistered(createMemberPlayer(account));
            }
        }

        @Override
        public void accountRemove(Account account) {
            Player remove = getCachedMember(account.getId());
            if (remove == null) {
                remove = createMemberPlayer(account);
            }
            for (PersonalityListener listener : listeners) {
                listener.playerUnregistered(remove);
            }
        }

        @Override
        public void accountUpdated(Account oldAccount, Account newAccount) {
            final DefaultMember personality = getCachedMember(newAccount.getId());
            if (personality != null) {
                personality.update(newAccount, membershipManager.getMembership(newAccount));
            }
        }

        @Override
        public void membershipCardUpdated(Account account, MembershipCard oldCard, MembershipCard newCard) {
            final DefaultMember personality = getCachedMember(account.getId());
            if (personality != null) {
                personality.update(account, newCard.getValidMembership());
            }
        }
    }
}
