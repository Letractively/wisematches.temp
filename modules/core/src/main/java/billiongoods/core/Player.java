package billiongoods.core;

/**
 * The {@code Player} represents real player.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class Player extends Personality {
    Player(long id) {
        super(id);
    }

    public abstract Language getLanguage();
}
