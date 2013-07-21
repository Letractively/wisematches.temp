package billiongoods.core.expiration.impl;

import billiongoods.core.expiration.ExpirationListener;
import billiongoods.core.expiration.ExpirationManager;
import billiongoods.core.expiration.ExpirationType;
import org.slf4j.Logger;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class AbstractExpirationManager<ID, T extends Enum<? extends ExpirationType>> implements ExpirationManager<ID, T> {
    protected final Lock lock = new ReentrantLock();
    private final Logger log;
    private final T[] expirationPoints;
    private final Map<ID, ScheduledFuture> scheduledExpirations = new HashMap<>();
    private final Collection<ExpirationListener<ID, T>> listeners = new CopyOnWriteArraySet<>();
    protected TransactionTemplate transactionTemplate;
    private TaskScheduler taskScheduler;

    protected AbstractExpirationManager(Class<T> typesClass, Logger log) {
        if (typesClass == null) {
            throw new NullPointerException("Types class can't be null");
        }
        this.log = log;
        expirationPoints = typesClass.getEnumConstants();
    }

    @Override
    public void addExpirationListener(ExpirationListener<ID, T> l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    @Override
    public void removeExpirationListener(ExpirationListener<ID, T> l) {
        listeners.remove(l);
    }

    @Override
    public T[] getExpirationPoints() {
        return expirationPoints;
    }

    protected final void scheduleTermination(final ID id, Date extinctionTime) {
        lock.lock();
        try {
            ScheduledFuture scheduledFuture = scheduledExpirations.get(id);
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }

            final ScheduledFuture schedule;
            final T type = nextExpiringPoint(extinctionTime);
            final ExpirationTask task = new ExpirationTask(id, extinctionTime, type);
            if (type == null) { // expired
                log.info("Entity is expired and will be terminated: {}", id);
                schedule = taskScheduler.schedule(task, extinctionTime);
            } else {
                final Date triggerTime = new Date(((ExpirationType) type).getTriggerTime(extinctionTime.getTime()));
                log.info("Start expiration scheduler for {}  to {} ({})", id, triggerTime, type);
                schedule = taskScheduler.schedule(task, triggerTime);
            }
            scheduledExpirations.put(id, schedule);
        } finally {
            lock.unlock();
        }
    }

    protected final void cancelTermination(final ID id) {
        lock.lock();
        try {
            log.info("Cancel entity termination: {}", id);

            ScheduledFuture scheduledFuture = scheduledExpirations.get(id);
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract boolean executeTermination(ID id);

    T nextExpiringPoint(Date extinctionTime) {
        final long currentTime = System.currentTimeMillis();
        for (T type : expirationPoints) {
            if (((ExpirationType) type).getTriggerTime(extinctionTime.getTime()) >= currentTime) {
                return type;
            }
        }
        return null;
    }

    private boolean terminateOrNotify(final ID id, final T type) {
        if (type == null) {
            final boolean b = executeTermination(id);
            log.info("Terminate entity {}: {}", id, b);
            return false;
        } else {
            log.info("Notify about expiration for entity {}: {}", id, type);
            for (ExpirationListener<ID, T> listener : listeners) {
                listener.expirationTriggered(id, type);
            }
            return true;
        }
    }

    private void processExpiration(final ID id, final Date extinctionTime, final T type) {
        final ScheduledFuture scheduledFuture;

        lock.lock();
        try {
            log.info("Process entity expiration: {} at {} ({})", id, extinctionTime, type);
            scheduledFuture = scheduledExpirations.get(id);
        } finally {
            lock.unlock();
        }

        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            final boolean reschedulingRequired;
            if (transactionTemplate != null) {
                reschedulingRequired = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean doInTransaction(TransactionStatus status) {
                        return terminateOrNotify(id, type);
                    }
                });
            } else {
                reschedulingRequired = terminateOrNotify(id, type);
            }

            if (reschedulingRequired) {
                scheduleTermination(id, extinctionTime);
            }
        }
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void destroy() {
        lock.lock();
        try {
            log.info("Destroy and cancel expirations for: {}", scheduledExpirations.keySet());
            for (ScheduledFuture scheduledFuture : scheduledExpirations.values()) {
                scheduledFuture.cancel(false);
            }
        } finally {
            lock.unlock();
        }
    }

    private class ExpirationTask implements Runnable {
        private final ID id;
        private final Date extinctionTime;
        private final T type;

        private ExpirationTask(ID id, Date extinctionTime, T type) {
            this.id = id;
            this.extinctionTime = extinctionTime;
            this.type = type;
        }

        @Override
        public void run() {
            processExpiration(id, extinctionTime, type);
        }
    }
}
