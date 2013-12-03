package billiongoods.server.services.state.impl;

import billiongoods.core.Visitor;
import billiongoods.server.services.state.PersonalityStateListener;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernatePersonalityStateManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernatePersonalityStateManagerTest() {
	}

	@Test
	public void test() throws InterruptedException {
		final Visitor player1 = new Visitor(System.currentTimeMillis());
		final Visitor player2 = new Visitor(System.currentTimeMillis() + 1);

		final PersonalityStateListener listener = createStrictMock(PersonalityStateListener.class);

		final HibernatePersonalityStateManager stateManager = new HibernatePersonalityStateManager();
		stateManager.setSessionFactory(sessionFactory);

		stateManager.addPersonalityStateListener(listener);

		listener.playerOnline(player1);
		listener.playerOnline(player2);
		listener.playerAlive(player1);
		listener.playerAlive(player1);
		listener.playerOffline(player2);
		listener.playerOffline(player1);
		replay(listener);

		Date lastActivity1 = stateManager.getLastActivityDate(player1);
		if (lastActivity1 == null) {
			lastActivity1 = new Date();
		}

		Date lastActivity2 = stateManager.getLastActivityDate(player2);
		if (lastActivity2 == null) {
			lastActivity2 = new Date();
		}

		assertFalse(stateManager.isPersonalityOnline(player1));

		Thread.sleep(100);
		stateManager.registerNewSession("S1", player1);
		assertTrue(stateManager.isPersonalityOnline(player1));
		assertTrue(lastActivity1.before(lastActivity1 = stateManager.getLastActivityDate(player1)));

		Thread.sleep(100);
		stateManager.registerNewSession("S2", player1);
		assertFalse(stateManager.isPersonalityOnline(player2));
		assertTrue(lastActivity1.before(lastActivity1 = stateManager.getLastActivityDate(player1)));

		Thread.sleep(100);
		stateManager.registerNewSession("S3", player2);
		assertTrue(stateManager.isPersonalityOnline(player2));
		assertTrue(lastActivity2.before(lastActivity2 = stateManager.getLastActivityDate(player2)));

		Thread.sleep(100);
		stateManager.registerNewSession("S4", player2);
		assertTrue(lastActivity2.before(lastActivity2 = stateManager.getLastActivityDate(player2)));

		Thread.sleep(100);
		stateManager.refreshLastRequest("S5"); // what is it?
		assertEquals(lastActivity1, stateManager.getLastActivityDate(player1));
		assertEquals(lastActivity2, stateManager.getLastActivityDate(player2));

		Thread.sleep(100);
		stateManager.refreshLastRequest("S1");
		assertTrue(lastActivity1.before(lastActivity1 = stateManager.getLastActivityDate(player1)));

		Thread.sleep(100);
		stateManager.refreshLastRequest("S1");
		assertTrue(lastActivity1.before(lastActivity1 = stateManager.getLastActivityDate(player1)));

		Thread.sleep(100);
		stateManager.removeSessionInformation("S5");
		assertTrue(stateManager.isPersonalityOnline(player1));
		assertTrue(stateManager.isPersonalityOnline(player2));
		assertEquals(lastActivity1, stateManager.getLastActivityDate(player1));
		assertEquals(lastActivity2, stateManager.getLastActivityDate(player2));

		Thread.sleep(100);
		stateManager.removeSessionInformation("S2");
		assertTrue(stateManager.isPersonalityOnline(player1));
		assertEquals(lastActivity1, stateManager.getLastActivityDate(player1));
		assertEquals(lastActivity2, stateManager.getLastActivityDate(player2));

		Thread.sleep(100);
		stateManager.removeSessionInformation("S3");
		assertTrue(stateManager.isPersonalityOnline(player2));
		assertEquals(lastActivity1, stateManager.getLastActivityDate(player1));
		assertEquals(lastActivity2, stateManager.getLastActivityDate(player2));

		Thread.sleep(100);
		stateManager.removeSessionInformation("S4");
		assertFalse(stateManager.isPersonalityOnline(player2));
		assertEquals(lastActivity1, stateManager.getLastActivityDate(player1));
		assertNull(stateManager.getLastActivityDate(player2));

		Thread.sleep(100);
		stateManager.removeSessionInformation("S1");
		assertFalse(stateManager.isPersonalityOnline(player1));
		assertNull(stateManager.getLastActivityDate(player1));
		assertNull(stateManager.getLastActivityDate(player2));

		verify(listener);
	}
}
