package billiongoods.core.task.scheduler;

import billiongoods.core.task.BreakingDayListener;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BreakingDayExecutorJob implements Job {
    private static final Logger log = LoggerFactory.getLogger("billiongoods.scheduler.BreakingDayJob");

    public BreakingDayExecutorJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final Date scheduledFireTime = context.getScheduledFireTime();
        ApplicationContext applicationContext = (ApplicationContext) context.get("SpringApplicationContext");
        if (applicationContext == null) {
            try {
                applicationContext = (ApplicationContext) context.getScheduler().getContext().get("SpringApplicationContext");
            } catch (SchedulerException ex) {
                log.error("Scheduler context can't be received", ex);
            }
        }


        if (applicationContext != null) {
            final Map<String, BreakingDayListener> listenerMap = applicationContext.getBeansOfType(BreakingDayListener.class);
            for (Map.Entry<String, BreakingDayListener> entry : listenerMap.entrySet()) {
                entry.getValue().breakingDayTime(scheduledFireTime);
            }
        } else {
            log.error("No application context for SpringBreakingDayExecutorJob");
        }
    }
}
