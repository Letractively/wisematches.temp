package billiongoods.core.cache;

import org.springframework.cache.Cache;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class NoOpCache implements Cache {
    public static final NoOpCache INSTANCE = new NoOpCache();

    private NoOpCache() {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
    }

    @Override
    public void evict(Object key) {
    }

    @Override
    public void clear() {
    }
}
