package billiongoods.core.task;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BreakingDayListener {
    void breakingDayTime(Date midnight);
}
