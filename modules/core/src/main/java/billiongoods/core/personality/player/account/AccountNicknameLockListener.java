package billiongoods.core.personality.player.account;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface AccountNicknameLockListener {
    void usernameLocked(String username, String reason);

    void usernameUnlocked(String username);
}
