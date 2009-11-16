package openjchart.tests.util;

import static org.junit.Assert.assertEquals;
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
	public void testSet() {
		settings.set("3", "v3_2");
		settings.set("4", "v4");
		assertEquals("v3_2", settings.get("3"));
		assertEquals("v4", settings.get("4"));
	}

	@Test
	public void testRemove() {
		settings.remove("3");
		settings.remove("4");
		assertEquals(null, settings.get("3"));
		assertEquals(null, settings.get("4"));
	}

	@Test
	public void testSetDefault() {
		settings.setDefault("3", "v3Default");
		settings.setDefault("4", "v4Default");
		assertEquals("v3", settings.get("3"));
		assertEquals("v4Default", settings.get("4"));
	}

	@Test
	public void testRemoveDefault() {
		settings.removeDefault("2");
		settings.removeDefault("3");
		assertEquals(null, settings.get("2"));
		assertEquals("v3", settings.get("3"));
	}

}
