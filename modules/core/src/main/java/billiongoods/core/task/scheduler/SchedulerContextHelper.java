package billiongoods.core.task.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
class SchedulerContextHelper {
    private SchedulerContextHelper() {
    }

    static ApplicationContext getApplicationContext(JobExecutionContext context) throws JobExecutionException {
        ApplicationContext applicationContext = (ApplicationContext) context.get("SpringApplicationContext");
        if (applicationContext == null) {
            try {
                applicationContext = (ApplicationContext) context.getScheduler().getContext().get("SpringApplicationContext");
            } catch (SchedulerException ex) {
                throw new JobExecutionException("Scheduler context can't be received", ex);
            }
        }
        return applicationContext;
    }

    static PlatformTransactionManager getTransactionManager(ApplicationContext context) throws JobExecutionException {
        if (context == null) {
            throw new JobExecutionException("No application context");
        }
        try {
            return context.getBean(PlatformTransactionManager.class);
        } catch (BeansException ex) {
            throw new JobExecutionException("Not single transaction manager", ex);
        }
    }
}
