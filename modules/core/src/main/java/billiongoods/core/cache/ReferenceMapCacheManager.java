package billiongoods.core.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ReferenceMapCacheManager implements CacheManager {
    private boolean dynamic;
    private final ReferenceType referenceType;
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

    /**
     * Construct a dynamic ConcurrentMapCacheManager,
     * lazily creating cache instances as they are being requested.
     */
    public ReferenceMapCacheManager(ReferenceType referenceType) {
        dynamic = true;
        this.referenceType = referenceType;
    }

    /**
     * Construct a static ConcurrentMapCacheManager,
     * managing caches for the specified cache names only.
     */
    public ReferenceMapCacheManager(ReferenceType referenceType, String... cacheNames) {
        this.referenceType = referenceType;
        setCacheNames(referenceType, Arrays.asList(cacheNames));
    }

    /**
     * Specify the set of cache names for this CacheManager's 'static' mode.
     * <p>The number of caches and their names will be fixed after a call to this method,
     * with no creation of further cache regions at runtime.
     */
    public void setCacheNames(ReferenceType referenceType, Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, createReferenceMapCache(name));
            }
            this.dynamic = false;
        }
    }

    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = createReferenceMapCache(name);
                    this.cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    /**
     * Create a new ConcurrentMapCache instance for the specified cache name.
     *
     * @param name the name of the cache
     * @return the ConcurrentMapCache (or a decorator thereof)
     */
    protected Cache createReferenceMapCache(String name) {
        return new ReferenceMapCache(name, referenceType);
    }
}
