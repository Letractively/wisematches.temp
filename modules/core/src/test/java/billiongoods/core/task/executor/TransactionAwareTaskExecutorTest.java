package billiongoods.core.task.executor;

import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;

import static org.easymock.EasyMock.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TransactionAwareTaskExecutorTest {
    public TransactionAwareTaskExecutorTest() {
    }

    @Test
    public void test() {
        final Runnable runnable = createNiceMock(Runnable.class);
        runnable.run();
        replay(runnable);

        final TransactionStatus transactionStatus = new SimpleTransactionStatus();

        final PlatformTransactionManager transactionManager = createStrictMock(PlatformTransactionManager.class);
        expect(transactionManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW))).andReturn(transactionStatus);
        transactionManager.commit(transactionStatus);
        replay(transactionManager);

        final TransactionAwareExecutor executor = new TransactionAwareExecutor();
        executor.setTaskExecutor(new SyncTaskExecutor());
        executor.setTransactionManager(transactionManager);

        executor.execute(runnable);

        verify(runnable, transactionManager);
    }
}
