package billiongoods.core.expiration.impl;

import billiongoods.core.expiration.ExpirationListener;
import billiongoods.core.expiration.MockExpirationType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AbstractExpirationManagerTest {
    private MockExpirationManager expirationManager;

    public AbstractExpirationManagerTest() {
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        TransactionTemplate transactionTemplate = new TransactionTemplate() {
            @Override
            public <T> T execute(TransactionCallback<T> action) throws TransactionException {
                return action.doInTransaction(null);
            }
        };

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();

        expirationManager = new MockExpirationManager();
        expirationManager.setTaskScheduler(taskScheduler);
        expirationManager.setTransactionTemplate(transactionTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTerminatorInitialization() throws InterruptedException {
        final ExpirationListener<Long, MockExpirationType> l = createMock(ExpirationListener.class);
        l.expirationTriggered(12L, MockExpirationType.ONE);
        l.expirationTriggered(13L, MockExpirationType.TWO);
        replay(l);

        expirationManager.addExpirationListener(l);

        final long time = System.currentTimeMillis();

        expirationManager.scheduleTermination(12L, new Date(time + MockExpirationType.ONE.getRemainedTime() + 200)); // expired in first tick
        expirationManager.scheduleTermination(13L, new Date(time + MockExpirationType.TWO.getRemainedTime() + 200)); // expired in second tick
        expirationManager.scheduleTermination(14L, new Date(time - 200)); // already expired
        expirationManager.scheduleTermination(15L, new Date(time + 200)); // will expire in a mig

        Thread.sleep(500);

        verify(l);
    }

    @Test
    public void test_nextExpiringType() {
        assertEquals(MockExpirationType.ONE, expirationManager.nextExpiringPoint(new Date(System.currentTimeMillis() + 2 * MockExpirationType.ONE.getRemainedTime())));
        assertEquals(MockExpirationType.ONE, expirationManager.nextExpiringPoint(new Date(System.currentTimeMillis() + MockExpirationType.ONE.getRemainedTime())));
        assertEquals(MockExpirationType.TWO, expirationManager.nextExpiringPoint(new Date(System.currentTimeMillis() + MockExpirationType.TWO.getRemainedTime() + 100)));
        assertNull(expirationManager.nextExpiringPoint(new Date(System.currentTimeMillis() + MockExpirationType.TWO.getRemainedTime() / 2)));
        assertNull(expirationManager.nextExpiringPoint(new Date(System.currentTimeMillis() - 1)));
    }
}
