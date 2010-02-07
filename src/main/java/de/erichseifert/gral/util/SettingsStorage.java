/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.util;

/**
 * Interface providing functions to store and retrieve settings for an
 * object.
 */
public interface SettingsStorage {
	/**
	 * Returns the setting with the specified key.
	 * If no setting is available, the default setting will be returned.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @return Setting.
	 */
	<T> T getSetting(String key);

	/**
	 * Sets the setting with the specified key to the specified value.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 */
	<T> void setSetting(String key, T value);

	/**
	 * Removes the setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 */
	<T> void removeSetting(String key);

	/**
	 * Sets a default value for the setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 * @param value Value to be set.
	 */
	<T> void setSettingDefault(String key, T value);

	/**
	 * Removes the default setting with the specified key.
	 * @param <T> Type of setting.
	 * @param key Key.
	 */
	<T> void removeSettingDefault(String key);
}
