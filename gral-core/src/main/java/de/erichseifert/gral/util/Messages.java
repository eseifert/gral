/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
 * <p>Lookup of the user-facing texts of the library, translated according to
 * the default locale. The texts live in {@code messages.properties} and its
 * per-language variants such as {@code messages_de.properties} in the
 * resources of the library.</p>
 *
 * <pre>
 * String description = Messages.getString("DataIO.csvDescription");
 * </pre>
 *
 * <p>A key that is not in the bundle yields the key itself between exclamation
 * marks rather than an exception, so a missing translation shows up in the
 * interface instead of breaking it. The bundle is resolved once, when the class
 * is loaded, so changing the default locale afterwards has no effect.</p>
 *
 * <p>Strings that are deliberately not translated &mdash; MIME types, format
 * names, property keys &mdash; are marked in the source with a
 * {@code //$NON-NLS-1$} comment.</p>
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
