package billiongoods.server.test.state.impl;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Ignore
public class SessionRegistryStateManagerTest {
	public SessionRegistryStateManagerTest() {
	}

	@Test
	public void test() {
/*
		final Player player1 = new DefaultVisitor(Language.RU);
        final Player player2 = new DefaultVisitor(Language.EN);

        final PlayerStateListener listener = createStrictMock(PlayerStateListener.class);

        final SessionRegistryStateManager stateManager = new SessionRegistryStateManager();
        stateManager.addPlayerStateListener(listener);

        listener.playerOnline(player1);
        listener.playerOnline(player2);
        listener.playerAlive(player1);
        listener.playerAlive(player1);
        listener.playerOffline(player2);
        listener.playerOffline(player1);
        replay(listener);

        assertFalse(stateManager.isPlayerOnline(player1));
        stateManager.registerNewSession("S1", new MemberDetails(player1, "asd", "qwe", false, false, Arrays.asList("mock")));
        assertTrue(stateManager.isPlayerOnline(player1));
        stateManager.registerNewSession("S2", new MemberDetails(player1, "asd", "qwe", false, false, Arrays.asList("mock")));
        assertFalse(stateManager.isPlayerOnline(player2));
        stateManager.registerNewSession("S3", new MemberDetails(player2, "asd", "qwe", false, false, Arrays.asList("mock")));
        assertTrue(stateManager.isPlayerOnline(player2));
        stateManager.registerNewSession("S4", new MemberDetails(player2, "asd", "qwe", false, false, Arrays.asList("mock")));
        stateManager.refreshLastRequest("S5");
        stateManager.refreshLastRequest("S1");
        stateManager.refreshLastRequest("S1");
        stateManager.removeSessionInformation("S5");
        assertTrue(stateManager.isPlayerOnline(player1));
        assertTrue(stateManager.isPlayerOnline(player2));
        stateManager.removeSessionInformation("S2");
        assertTrue(stateManager.isPlayerOnline(player1));
        stateManager.removeSessionInformation("S3");
        assertTrue(stateManager.isPlayerOnline(player2));
        stateManager.removeSessionInformation("S4");
        assertFalse(stateManager.isPlayerOnline(player2));
        stateManager.removeSessionInformation("S1");
        assertFalse(stateManager.isPlayerOnline(player1));

        verify(listener);
*/
		throw new UnsupportedOperationException("commented");
	}
}
