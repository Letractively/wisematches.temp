package billiongoods.server.services.state;

import billiongoods.core.Personality;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PersonalityStateListener {
	void playerOnline(Personality person);

	void playerAlive(Personality person);

	void playerOffline(Personality person);
}
