package billiongoods.core.expiration.impl;

import billiongoods.core.expiration.MockExpirationType;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
class MockExpirationManager extends AbstractExpirationManager<Long, MockExpirationType> {
    private List<Long> terminated = new ArrayList<>();

    protected MockExpirationManager() {
        super(MockExpirationType.class, LoggerFactory.getLogger("billiongoods.expiration.MockExpirationManager"));
    }

    @Override
    protected boolean executeTermination(Long id) {
        terminated.add(id);
        return true;
    }

    public List<Long> getTerminated() {
        return terminated;
    }
}
