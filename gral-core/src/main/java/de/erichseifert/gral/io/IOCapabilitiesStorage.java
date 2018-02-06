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
package de.erichseifert.gral.io;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class that provides the basic functions to store capabilities of
 * a reader or a writer implementation.
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
