package billiongoods.server.services.state.impl;

import billiongoods.core.Personality;
import billiongoods.core.Visitor;
import billiongoods.server.services.state.PersonalityStateListener;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SessionRegistryStateManagerTest {
	public SessionRegistryStateManagerTest() {
	}

	@Test
	public void test() {
		final Personality player1 = new Visitor(System.currentTimeMillis());
		final Personality player2 = new Visitor(System.currentTimeMillis() + 1);

		final PersonalityStateListener listener = createStrictMock(PersonalityStateListener.class);

		final SessionRegistryStateManager stateManager = new SessionRegistryStateManager();
		stateManager.addPersonalityStateListener(listener);

		listener.playerOnline(player1);
		listener.playerOnline(player2);
		listener.playerAlive(player1);
		listener.playerAlive(player1);
		listener.playerOffline(player2);
		listener.playerOffline(player1);
		replay(listener);

		assertFalse(stateManager.isPersonalityOnline(player1));
		stateManager.registerNewSession("S1", player1);
		assertTrue(stateManager.isPersonalityOnline(player1));
		stateManager.registerNewSession("S2", player1);
		assertFalse(stateManager.isPersonalityOnline(player2));
		stateManager.registerNewSession("S3", player2);
		assertTrue(stateManager.isPersonalityOnline(player2));
		stateManager.registerNewSession("S4", player2);
		stateManager.refreshLastRequest("S5");
		stateManager.refreshLastRequest("S1");
		stateManager.refreshLastRequest("S1");
		stateManager.removeSessionInformation("S5");
		assertTrue(stateManager.isPersonalityOnline(player1));
		assertTrue(stateManager.isPersonalityOnline(player2));
		stateManager.removeSessionInformation("S2");
		assertTrue(stateManager.isPersonalityOnline(player1));
		stateManager.removeSessionInformation("S3");
		assertTrue(stateManager.isPersonalityOnline(player2));
		stateManager.removeSessionInformation("S4");
		assertFalse(stateManager.isPersonalityOnline(player2));
		stateManager.removeSessionInformation("S1");
		assertFalse(stateManager.isPersonalityOnline(player1));

		verify(listener);
	}
}
