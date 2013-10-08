package billiongoods.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ReferenceMapCache implements Cache {
	private final String name;
	private final ReferenceType referenceType;

	private final Map<Object, ReferenceType.CacheValue> cache = new HashMap<>();
	private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.cache.ReferenceMapCache");

	public ReferenceMapCache(String name, ReferenceType referenceType) {
		this.name = name;
		this.referenceType = referenceType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return cache;
	}

	@Override
	public ValueWrapper get(Object key) {
		clearDeadReferences();
		return cache.get(key);
	}

	@Override
	public void put(Object key, Object value) {
		clearDeadReferences();
		cache.put(key, referenceType.createCacheReference(key, value, referenceQueue));
	}

	@Override
	public void evict(Object key) {
		clearDeadReferences();
		cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	private void clearDeadReferences() {
		int count = 0;
		ReferenceType.CacheValue reference = (ReferenceType.CacheValue) referenceQueue.poll();
		while (reference != null) {
			count++;
			cache.remove(reference.getKey());
			reference = (ReferenceType.CacheValue) referenceQueue.poll();
		}

		if (count > 0) {
			log.info("Cache '{}' was reduced by {} elements. Current size: {}", name, count, cache.size());
		}
	}
}
