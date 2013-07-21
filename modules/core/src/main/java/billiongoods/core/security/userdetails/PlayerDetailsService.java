package billiongoods.core.security.userdetails;

import billiongoods.core.Language;
import billiongoods.core.Member;
import billiongoods.core.PersonalityManager;
import billiongoods.core.Visitor;
import billiongoods.core.personality.player.account.Account;
import billiongoods.core.personality.player.account.AccountLockManager;
import billiongoods.core.personality.player.account.AccountManager;
import billiongoods.core.personality.player.account.AccountRecoveryManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PlayerDetailsService implements UserDetailsService {
    private AccountManager accountManager;
    private AccountLockManager accountLockManager;
    private AccountRecoveryManager accountRecoveryManager;

    private PersonalityManager personalityManager;

    private final Set<Long> moderators = new HashSet<>();
    private final Set<Long> administrators = new HashSet<>();

    public PlayerDetailsService() {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadMemberByEmail(username);
    }

    public PlayerDetails loadMemberByEmail(String email) throws UsernameNotFoundException {
        final Account account = accountManager.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("Account with email " + email + " not found in the system");
        }

        final Member member = personalityManager.getMember(account.getId());
        if (member == null) {
            throw new UsernameNotFoundException("Player for account " + account + " can't be created");
        }
        final boolean locked = accountLockManager.isAccountLocked(account);
        final boolean expired = (accountRecoveryManager.getToken(account) != null);

        final Collection<String> authorities = new HashSet<>();
        if (administrators.contains(account.getId())) {
            authorities.add("admin");
        }
        if (moderators.contains(account.getId())) {
            authorities.add("moderator");
        }
        authorities.add("player");
        authorities.add(member.getMembership().name().toLowerCase());
        return new PlayerDetails(member, account.getEmail(), null, locked, expired, authorities);
    }

    public PlayerDetails loadVisitorByLanguage(Language language) throws UsernameNotFoundException {
        if (language == null) {
            throw new UsernameNotFoundException("Unsupported guest language: " + language);
        }

        final Visitor visitor = personalityManager.getVisitor(language);
        if (visitor == null) {
            throw new UsernameNotFoundException("Unsupported guest language: " + language);
        }
        return new PlayerDetails(visitor, "guest", null, false, false, Arrays.asList("player", "guest"));
    }

    public boolean isPasswordValid(PlayerDetails details, String rawPass) {
        return accountManager.checkAccountCredentials(details.getPlayer().getId(), rawPass);
    }

    public void setModerators(String moderators) {
        this.moderators.clear();

        if (moderators != null) {
            for (String moderator : moderators.split(",")) {
                this.moderators.add(Long.valueOf(moderator));
            }
        }
    }

    public void setAdministrators(String administrators) {
        this.administrators.clear();

        if (administrators != null) {
            for (String administrator : administrators.split(",")) {
                this.administrators.add(Long.valueOf(administrator));
            }
        }
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void setAccountLockManager(AccountLockManager accountLockManager) {
        this.accountLockManager = accountLockManager;
    }

    public void setPersonalityManager(PersonalityManager personalityManager) {
        this.personalityManager = personalityManager;
    }

    public void setAccountRecoveryManager(AccountRecoveryManager accountRecoveryManager) {
        this.accountRecoveryManager = accountRecoveryManager;
    }
}
