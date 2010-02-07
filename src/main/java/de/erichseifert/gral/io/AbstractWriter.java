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

package de.erichseifert.gral.io;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that implements the basic functions of a writer.
 */
public abstract class AbstractWriter {
	private static final Set<WriterCapabilities> capabilities = new HashSet<WriterCapabilities>();

	/**
	 * Creates a new AbstractWriter object.
	 */
	protected AbstractWriter() {
	}

	/**
	 * Returns a Set with WriterCapabilities for all supported formats.
	 * @return WriterCapabilities.
	 */
	public static Set<WriterCapabilities> getCapabilities() {
		return Collections.unmodifiableSet(capabilities);
	}

	/**
	 * Adds the specified WriterCapabilities to the Set of supported formats.
	 * @param capabilities WriterCapabilities to be added.
	 */
	protected final static void addCapabilities(WriterCapabilities capabilities) {
		AbstractWriter.capabilities.add(capabilities);
	}
}
