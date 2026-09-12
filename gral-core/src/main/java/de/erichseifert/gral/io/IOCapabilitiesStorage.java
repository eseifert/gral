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
package de.erichseifert.gral.io;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Base class of every reader and writer, and the registry of what formats
 * exist. An implementation announces the formats it handles from a static
 * initializer:</p>
 *
 * <pre>
 * static {
 *     addCapabilities(new IOCapabilities(
 *         "CSV", "Comma separated values", "text/csv",
 *         new String[] {"csv", "txt"}));
 * }
 * </pre>
 *
 * <p>The registry is static and shared by all readers and writers, so
 * {@link #getCapabilities()} only reports the formats whose classes have
 * already been loaded. That is why an {@link IOFactory} calls this method
 * reflectively on each class it knows about, rather than reading the set
 * directly.</p>
 */
public abstract class IOCapabilitiesStorage {
	/** Set of all registered capabilities. */
	private static final Set<IOCapabilities> capabilities
		= new HashSet<>();

	/**
	 * Initializes a new storage instance.
	 */
	protected IOCapabilitiesStorage() {
	}

	/**
	 * Returns a {@code Set} with capabilities for all supported formats.
	 * @return Capabilities.
	 */
	public static Set<IOCapabilities> getCapabilities() {
		return Collections.unmodifiableSet(capabilities);
	}

	/**
	 * Adds the specified capabilities to the Set of supported formats.
	 * @param capabilities Capabilities to be added.
	 */
	protected static void addCapabilities(IOCapabilities capabilities) {
		IOCapabilitiesStorage.capabilities.add(capabilities);
	}
}
