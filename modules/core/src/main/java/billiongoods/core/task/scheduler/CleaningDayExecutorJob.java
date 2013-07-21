package billiongoods.core.task.scheduler;


import billiongoods.core.task.CleaningDayListener;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CleaningDayExecutorJob implements Job {
    private static final Logger log = LoggerFactory.getLogger("billiongoods.scheduler.CleaningDayJob");

    public CleaningDayExecutorJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final Date scheduledFireTime = context.getScheduledFireTime();
        final ApplicationContext applicationContext = SchedulerContextHelper.getApplicationContext(context);
        final PlatformTransactionManager transactionManager = SchedulerContextHelper.getTransactionManager(applicationContext);

        final TransactionTemplate template = new TransactionTemplate(transactionManager, TransactionalExecutorJob.TRANSACTION_DEFINITION);
        if (applicationContext != null) {
            final Map<String, CleaningDayListener> listenerMap = applicationContext.getBeansOfType(CleaningDayListener.class);
            for (Map.Entry<String, CleaningDayListener> entry : listenerMap.entrySet()) {
                final CleaningDayListener value = entry.getValue();
                template.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        value.cleanup(scheduledFireTime);
                    }
                });
            }
        } else {
            log.error("No application context for SpringBreakingDayExecutorJob");
        }
    }
}
