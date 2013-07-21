package billiongoods.server.services.props.impl;

import billiongoods.server.services.props.ReliablePropertiesManager;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemoryPropertiesManager implements ReliablePropertiesManager {
    private final Map<String, Serializable> cache = new ConcurrentHashMap<>();

    public MemoryPropertiesManager() {
    }

    @Override
    public int getInt(String group, String name, int def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (Integer) serializable;
    }

    @Override
    public long getLong(String group, String name, long def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (Long) serializable;
    }

    @Override
    public Date getDate(String group, String name, Date def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (Date) serializable;
    }

    @Override
    public float getFloat(String group, String name, float def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (Float) serializable;
    }

    @Override
    public double getDouble(String group, String name, double def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (Double) serializable;
    }

    @Override
    public String getString(String group, String name, String def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (String) serializable;
    }

    @Override
    public boolean getBoolean(String group, String name, boolean def) {
        final Serializable serializable = cache.get(group + "." + name);
        if (serializable == null) {
            return def;
        }
        return (Boolean) serializable;
    }

    @Override
    public void setInt(String group, String name, int value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void setLong(String group, String name, long value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void setDate(String group, String name, Date value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void setFloat(String group, String name, float value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void setDouble(String group, String name, double value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void setString(String group, String name, String value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void setBoolean(String group, String name, boolean value) {
        cache.put(group + "." + name, value);
    }

    @Override
    public void removeProperty(String group, String name) {
        cache.remove(group + "." + name);
    }

    @Override
    public boolean hasProperty(String group, String name) {
        return cache.containsKey(group + "." + name);
    }
}
