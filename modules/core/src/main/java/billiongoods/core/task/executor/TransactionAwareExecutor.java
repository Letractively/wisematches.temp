package billiongoods.core.task.executor;

import billiongoods.core.task.TransactionalExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionAwareExecutor implements TransactionalExecutor {
    private TaskExecutor taskExecutor;
    private TransactionTemplate transactionTemplate;

    private static final DefaultTransactionDefinition TRANSACTION_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    public TransactionAwareExecutor() {
    }

    @Override
    public void execute(final Runnable task) {
        if (transactionTemplate == null) {
            throw new IllegalStateException("TransactionManager is not set");
        }

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (transactionTemplate == null) {
                    throw new IllegalStateException("TransactionManager is not set");
                }

                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        task.run();
                    }
                });
            }
        });
    }

    @Override
    public void executeStraight(Runnable task) {
        taskExecutor.execute(task);
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        if (transactionManager != null) {
            transactionTemplate = new TransactionTemplate(transactionManager, TRANSACTION_DEFINITION);
        } else {
            transactionTemplate = null;
        }
    }
}