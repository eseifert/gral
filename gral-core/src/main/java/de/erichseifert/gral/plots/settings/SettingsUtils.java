/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.plots.settings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;


/**
 * Abstract class that contains utility functions for working with managing and
 * storing settings.
 */
public abstract class SettingsUtils {
	/**
	 * Default constructor that prevents creation of class.
	 */
	private SettingsUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns all static fields of a specified class that represent settings
	 * keys as pairs of (name, key).
	 * @param cls Class to inspect.
	 * @return A map storing pairs with key name, and key object, or a empty
	 *         map if the class was {@code null} no keys were found.
	 */
	public static Map<String, Key> getKeys(Class<?> cls) {
		Map<String, Key> keys = new TreeMap<String, Key>();

		if (cls == null) {
			return keys;
		}

		Field[] fields = cls.getFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) &&
					Key.class.isAssignableFrom(field.getType())) {
				try {
					keys.put(field.getName(), (Key) field.get(null));
				} catch (IllegalArgumentException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				}
			}
		}

		return keys;
	}
}
