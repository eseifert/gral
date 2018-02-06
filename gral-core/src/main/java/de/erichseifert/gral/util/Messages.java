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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Singleton class that globally provides translated message texts.
 */
public abstract class Messages {
	/** Name of resource bundle that contains message texts. */
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

	/** Resource bundle that contains message texts. */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * Private constructor.
	 */
	private Messages() {
	}

	/**
	 * Returns a message text that is determined by the specified key.
	 * A replacement text generated from the key is returned if the message
	 * cannot be found.
	 * @param key Key string that identifies the message
	 * @return Translated message text, or default key if the message cannot
	 *         be found.
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
