package billiongoods.server.test.props.impl;


import billiongoods.server.test.props.ReliablePropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FilePropertiesManager implements ReliablePropertiesManager, InitializingBean, Closeable {
	private FileChannel propertiesFile;
	private final Lock lock = new ReentrantLock();
	private final Map<String, Serializable> cache = new ConcurrentHashMap<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.properties.FileManager");

	public FilePropertiesManager() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		lock.lock();
		try {
			if (!propertiesFile.isOpen() || propertiesFile.size() == 0) {
				return;
			}
			propertiesFile.position(0);
			final ObjectInputStream inputStream = new ObjectInputStream(Channels.newInputStream(propertiesFile));
			int count = inputStream.readInt();
			if (count == 0) {
				return;
			}

			for (int i = 0; i < count; i++) {
				final String name = inputStream.readUTF();
				final Serializable value = (Serializable) inputStream.readObject();
				cache.put(name, value);
			}
		} catch (IOException | ClassNotFoundException ex) {
			log.error("File proposal can't be loaded", ex);
		} finally {
			lock.unlock();
		}
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
		saveAllProperties();
	}

	@Override
	public void setLong(String group, String name, long value) {
		cache.put(group + "." + name, value);
		saveAllProperties();
	}

	@Override
	public void setDate(String group, String name, Date value) {
		cache.put(group + "." + name, value);
		saveAllProperties();
	}

	@Override
	public void setFloat(String group, String name, float value) {
		cache.put(group + "." + name, value);
		saveAllProperties();
	}

	@Override
	public void setDouble(String group, String name, double value) {
		cache.put(group + "." + name, value);
		saveAllProperties();
	}

	@Override
	public void setString(String group, String name, String value) {
		cache.put(group + "." + name, value);
		saveAllProperties();
	}

	@Override
	public void setBoolean(String group, String name, boolean value) {
		cache.put(group + "." + name, value);
		saveAllProperties();
	}

	@Override
	public void removeProperty(String group, String name) {
		cache.remove(group + "." + name);
		saveAllProperties();
	}

	@Override
	public boolean hasProperty(String group, String name) {
		return cache.containsKey(group + "." + name);
	}

	private void saveAllProperties() {
		lock.lock();
		try {
			propertiesFile.position(0);
			final ObjectOutputStream outputStream = new ObjectOutputStream(Channels.newOutputStream(this.propertiesFile));
			outputStream.writeInt(cache.size());
			for (Map.Entry<String, Serializable> entry : cache.entrySet()) {
				outputStream.writeUTF(entry.getKey());
				outputStream.writeObject(entry.getValue());
			}
			outputStream.flush();
		} catch (IOException ex) {
			log.error("File proposal can't be stored", ex);
		} finally {
			lock.unlock();
		}
	}

	public void setPropertiesFile(File propertiesFile) throws IOException {
		lock.lock();
		try {
			if (!propertiesFile.exists()) {
				propertiesFile.createNewFile();
			}
			this.propertiesFile = new RandomAccessFile(propertiesFile, "rw").getChannel();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void close() throws IOException {
		lock.lock();
		try {
			if (propertiesFile != null) {
				propertiesFile.close();
			}
		} finally {
			lock.unlock();
		}
	}
}
