package billiongoods.core.task;

import org.springframework.core.task.TaskExecutor;

/**
 * Wrapper under {@code TaskExecutor} spring interface that process any work inside Hibernate
 * transaction with {@code PROPAGATION_REQUIRES_NEW} propagation.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface TransactionalExecutor extends TaskExecutor {
    /**
     * Executes specified task in new transaction.
     *
     * @param task the task that must be executed in new transaction.
     */
    @Override
    void execute(Runnable task);

    /**
     * Executes specified tasks outside transaction.
     *
     * @param task the task to be executed outside transaction.
     */
    void executeStraight(Runnable task);
}
