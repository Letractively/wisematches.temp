package billiongoods.core.security.authentication;

import billiongoods.core.security.userdetails.PlayerDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PlayerAuthentication extends AbstractAuthenticationToken {
    private PlayerDetails personality;

    public PlayerAuthentication(PlayerDetails personality) {
        super(personality.getAuthorities());
        setAuthenticated(true);
        this.personality = personality;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public PlayerDetails getPrincipal() {
        return personality;
    }
}
