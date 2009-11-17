package openjchart.tests.util;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import openjchart.util.Settings;

import org.junit.Before;
import org.junit.Test;

public class SettingsTest {
	private Settings settings;

	@Before
	public void setUp() throws Exception {
		settings = new Settings();
		settings.set("1", "v1");
		settings.setDefault("1", "v1Default");
		settings.setDefault("2", "v2Default");
		settings.set("3", "v3");
	}

	@Test
	public void testGet() {
		assertEquals("v1", settings.get("1"));
		assertEquals("v2Default", settings.get("2"));
		assertEquals("v3", settings.get("3"));
	}

	@Test
	public void testGetDefaults() {
		Map<String, Object> defaults = settings.getDefaults();
		assertEquals(2, defaults.size());
		assertEquals("v1Default", defaults.get("1"));
		assertEquals("v2Default", defaults.get("2"));
		assertEquals(null, defaults.get("3"));
	}

	@Test
	public void testGetSettings() {
		Map<String, Object> settingsMap = settings.getSettings();
		assertEquals(2, settingsMap.size());
		assertEquals("v1", settingsMap.get("1"));
		assertEquals(null, settingsMap.get("2"));
		assertEquals("v3", settingsMap.get("3"));
	}

	@Test
	public void testClearDefaults() {
		settings.clearDefaults();
		assertEquals(true, settings.getDefaults().isEmpty());
	}

	@Test
	public void testClearSettings() {
		settings.clearSettings();
		assertEquals(true, settings.getSettings().isEmpty());
	}

	@Test
	public void testHasDefault() {
		assertEquals(true, settings.hasDefault("1"));
		assertEquals(true, settings.hasDefault("2"));
		assertEquals(false, settings.hasDefault("3"));
	}

	@Test
	public void testHasSetting() {
		assertEquals(true, settings.hasSetting("1"));
		assertEquals(false, settings.hasSetting("2"));
		assertEquals(true, settings.hasSetting("3"));
	}

	@Test
	public void testHasKey() {
		assertEquals(true, settings.hasKey("1"));
		assertEquals(true, settings.hasKey("2"));
		assertEquals(true, settings.hasKey("3"));
		assertEquals(false, settings.hasKey("4"));
	}

	@Test
	public void testKeySet() {
		Collection<String> keys = new HashSet<String>(3);
		keys.add("1");
		keys.add("2");
		keys.add("3");
		Set<String> keysToTest = settings.keySet();
		assertEquals(true, keysToTest.containsAll(keys));
		assertEquals(keys.size(), keysToTest.size());
	}

	@Test
	public void testSet() {
		settings.set("3", "v3_2");
		settings.set("4", "v4");
		assertEquals("v3_2", settings.get("3"));
		assertEquals("v4", settings.get("4"));
	}

	@Test
	public void testSetDefault() {
		settings.setDefault("3", "v3Default");
		settings.setDefault("4", "v4Default");
		assertEquals("v3", settings.get("3"));
		assertEquals("v4Default", settings.get("4"));
	}

	@Test
	public void testRemove() {
		settings.remove("3");
		settings.remove("4");
		assertEquals(null, settings.get("3"));
		assertEquals(null, settings.get("4"));
	}

	@Test
	public void testRemoveDefault() {
		settings.removeDefault("2");
		settings.removeDefault("3");
		assertEquals(null, settings.get("2"));
		assertEquals("v3", settings.get("3"));
	}

	@Test
	public void testValues() {
		Collection<Object> values = new HashSet<Object>();
		values.add("v1");
		values.add("v2Default");
		values.add("v3");
		Collection<Object> valuesToTest = settings.values();
		assertEquals(true, valuesToTest.containsAll(values));
		assertEquals(values.size(), valuesToTest.size());
	}

}
