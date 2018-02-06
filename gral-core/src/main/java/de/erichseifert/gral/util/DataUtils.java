/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class that contains utility functions for creating data structures
 * and for working with data sources and values.
 */
public abstract class DataUtils {
	/**
	 * Default constructor that prevents creation of class.
	 */
	private DataUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a mapping from two arrays, one with keys, one with values.
	 * @param <K> Data type of the keys.
	 * @param <V> Data type of the values.
	 * @param keys Array containing the keys.
	 * @param values Array containing the values.
	 * @return Map with keys and values from the specified arrays.
	 */
	public static <K, V> Map<K, V> map(K[] keys, V[] values) {
		// Check for valid parameters
		if (keys.length != values.length) {
			throw new IllegalArgumentException(
				"Could not create the map because the number of keys and values differs.");
		}
		// Fill map with keys and values
		Map<K, V> map = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			K key = keys[i];
			V value = values[i];
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Returns the double value of the {@code Number} object or the specified
	 * default value if the object is {@code null}.
	 * @param n Number object.
	 * @param defaultValue Default value.
	 * @return Double value of the {@code Number} object or the default value
	 *         if the object is {@code null}
	 */
	public static double getValueOrDefault(Number n, double defaultValue) {
		if (n == null) {
			return defaultValue;
		}
		return n.doubleValue();
	}
}
