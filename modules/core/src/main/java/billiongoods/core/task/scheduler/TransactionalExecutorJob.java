package billiongoods.core.task.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class TransactionalExecutorJob implements Job {
    protected static final DefaultTransactionDefinition TRANSACTION_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    protected TransactionalExecutorJob() {
    }

    @Override
    public final void execute(final JobExecutionContext context) throws JobExecutionException {
        final ApplicationContext applicationContext = SchedulerContextHelper.getApplicationContext(context);
        if (applicationContext == null) {
            throw new JobExecutionException("No application context");
        }
        final PlatformTransactionManager transactionManager = applicationContext.getBean(PlatformTransactionManager.class);
        final TransactionTemplate template = new TransactionTemplate(transactionManager, TRANSACTION_DEFINITION);
        final JobExecutionException execute = template.execute(new TransactionCallback<JobExecutionException>() {
            @Override
            public JobExecutionException doInTransaction(TransactionStatus status) {
                try {
                    executeInTransaction(applicationContext, context, status);
                    return null;
                } catch (JobExecutionException ex) {
                    return ex;
                }
            }
        });
        if (execute != null) {
            throw execute;
        }
    }

    protected abstract void executeInTransaction(ApplicationContext context, JobExecutionContext job, TransactionStatus status) throws JobExecutionException;
}
