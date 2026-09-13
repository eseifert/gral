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
package de.erichseifert.gral.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Lookup of the user-facing texts of the Swing components, translated
 * according to the default locale. It mirrors
 * {@link de.erichseifert.gral.util.Messages} of the library core, but reads
 * its own bundle: a resource bundle is resolved by name on the class path, so
 * two modules cannot both contribute to {@code messages.properties} &mdash;
 * whichever archive comes first would hide the other. The texts of this module
 * therefore live in {@code messages.properties} next to this class, which
 * makes the bundle name {@code de.erichseifert.gral.ui.messages}.</p>
 *
 * <p>A key that is not in the bundle yields the key itself between exclamation
 * marks rather than an exception, so a missing translation shows up in the
 * interface instead of breaking it.</p>
 */
final class UiMessages {
	/** Name of the resource bundle that contains the message texts. */
	private static final String BUNDLE_NAME =
			"de.erichseifert.gral.ui.messages"; //$NON-NLS-1$

	/** Resource bundle that contains the message texts. */
	private static final ResourceBundle RESOURCE_BUNDLE =
			ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Private constructor.
	 */
	private UiMessages() {
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
