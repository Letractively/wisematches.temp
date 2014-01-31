package billiongoods.server.web.servlet.view.freemarker;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class FreeMarkerEnumMap<E extends Enum<E>> implements Map<String, E> {
    private final Class<E> type;

    private final Map<String, E> keys = new LinkedHashMap<>();
    private final Collection<E> values = new LinkedList<>(); // required for ?reverse operation

    private static final Map<Class<?>, FreeMarkerEnumMap<?>> cache = new HashMap<>();

    private FreeMarkerEnumMap(Class<E> type) {
        this.type = type;

        final E[] enumConstants = type.getEnumConstants();
        for (E enumConstant : enumConstants) {
            keys.put(enumConstant.name(), enumConstant);
            values().add(enumConstant);
        }
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keys.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    @Override
    public E put(String key, E value) {
        throw new UnsupportedOperationException("Not changeable");
    }

    @Override
    public void putAll(Map<? extends String, ? extends E> m) {
        throw new UnsupportedOperationException("Not changeable");
    }

    @Override
    public E get(Object key) {
        return keys.get(key);
    }

    @Override
    public E remove(Object key) {
        throw new UnsupportedOperationException("Not changeable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not changeable");
    }

    @Override
    public Set<String> keySet() {
        return keys.keySet();
    }

    @Override
    public Collection<E> values() {
        return values;
    }

    @Override
    public Set<Entry<String, E>> entrySet() {
        return keys.entrySet();
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> FreeMarkerEnumMap<E> valueOf(Class<E> type) {
        FreeMarkerEnumMap<E> enumView = (FreeMarkerEnumMap<E>) cache.get(type);
        if (enumView == null) {
            enumView = new FreeMarkerEnumMap<>(type);
            cache.put(type, enumView);
        }
        return enumView;
    }
}
