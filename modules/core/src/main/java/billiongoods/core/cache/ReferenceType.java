package billiongoods.core.cache;

import org.springframework.cache.Cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ReferenceType {
    SOFT {
        @Override
        CacheValue createCacheReference(Object key, Object value, ReferenceQueue<Object> q) {
            return new SoftCacheValue(key, value, q);
        }
    },

    WEAK {
        @Override
        CacheValue createCacheReference(Object key, Object value, ReferenceQueue<Object> q) {
            return new WeakCacheValue(key, value, q);
        }
    };

    abstract CacheValue createCacheReference(Object key, Object value, ReferenceQueue<Object> q);

    interface CacheValue extends Cache.ValueWrapper {
        Object getKey();
    }

    class SoftCacheValue extends SoftReference<Object> implements CacheValue {
        private final Object key;

        public SoftCacheValue(Object key, Object value, ReferenceQueue<Object> q) {
            super(value, q);
            this.key = key;
        }

        @Override
        public Object getKey() {
            return key;
        }
    }

    class WeakCacheValue extends WeakReference<Object> implements CacheValue {
        private final Object key;

        public WeakCacheValue(Object key, Object value, ReferenceQueue<Object> q) {
            super(value, q);
            this.key = key;
        }

        @Override
        public Object getKey() {
            return key;
        }
    }
}
