package billiongoods.core.personality.player.account;

import java.util.Date;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface RecoveryToken {
    String getToken();

    Date getGenerated();
}
