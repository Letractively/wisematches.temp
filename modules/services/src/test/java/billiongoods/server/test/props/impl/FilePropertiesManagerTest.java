package billiongoods.server.test.props.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FilePropertiesManagerTest {
	private File file;
	private FilePropertiesManager filePropertiesManager;

	public FilePropertiesManagerTest() {
	}

	@Before
	public void setUp() throws Exception {
		file = File.createTempFile("proposal-", "-unit-test");
		if (file.exists()) {
			file.delete();
		}

		filePropertiesManager = new FilePropertiesManager();
		filePropertiesManager.setPropertiesFile(file);
		filePropertiesManager.afterPropertiesSet();
	}

	@After
	public void cleanUp() {
		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void test() {
		final Date value = new Date();

		filePropertiesManager.setBoolean("mock.group", "p1", true);
		filePropertiesManager.setDate("mock.group", "p2", value);
		filePropertiesManager.setDouble("mock.group", "p3", 12.3d);
		filePropertiesManager.setFloat("mock.group", "p4", 13.2f);
		filePropertiesManager.setInt("mock.group", "p5", 10);
		filePropertiesManager.setLong("mock.group", "p6", 20L);
		filePropertiesManager.setString("mock.group", "p7", "mock");

		assertEquals(true, filePropertiesManager.getBoolean("mock.group", "p1", false));
		assertEquals(value, filePropertiesManager.getDate("mock.group", "p2", null));
		assertEquals(12.3d, filePropertiesManager.getDouble("mock.group", "p3", Double.NaN), 0.00001);
		assertEquals(13.2f, filePropertiesManager.getFloat("mock.group", "p4", Float.NaN), 0.00001);
		assertEquals(10, filePropertiesManager.getInt("mock.group", "p5", 0));
		assertEquals(20L, filePropertiesManager.getLong("mock.group", "p6", 0));
		assertEquals("mock", filePropertiesManager.getString("mock.group", "p7", null));
	}
}
