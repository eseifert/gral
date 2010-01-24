/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.util;

import java.util.EventObject;

/**
 * Class for handling event data of settings.
 * @see SettingsListener
 */
public class SettingChangeEvent extends EventObject {
	private final String key;
	private final Object valOld;
	private final Object valNew;
	private final boolean defaultSetting;

	/**
	 * Creates a new event object with the specified values.
	 * @param source The object on which the Event initially occurred.
	 * @param key Key of the setting.
	 * @param valOld Old value.
	 * @param valNew New value.
	 * @param defaultSetting <code>true</code> if a default setting has changed.
	 */
	public SettingChangeEvent(Object source, String key, Object valOld, Object valNew, boolean defaultSetting) {
		super(source);
		this.key = key;
		this.valOld = valOld;
		this.valNew = valNew;
		this.defaultSetting = defaultSetting;
	}

	/**
	 * Returns the key of the changed setting.
	 * @return Key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns the old value.
	 * @return Old value
	 */
	public Object getValOld() {
		return valOld;
	}

	/**
	 * Returns the new value.
	 * @return New value
	 */
	public Object getValNew() {
		return valNew;
	}

	/**
	 * Returns whether the setting is a default setting.
	 * @return <code>true</code> or <code>false</code>
	 */
	public boolean isDefaultSetting() {
		return defaultSetting;
	}

}
