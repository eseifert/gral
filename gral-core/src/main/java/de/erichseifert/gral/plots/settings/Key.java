/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
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

import java.io.Serializable;

/**
 * A settings key storing a name.
 */
public final class Key implements Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2236083523816239316L;

	/** Path-like formatted name to identify the setting. */
	private final String name;
	/** Number used for hashing. */
	private final int hashCode;

	/**
	 * Constructor that initializes the instance with a name.
	 * @param name Name associated with this key.
	 */
	public Key(String name) {
		this.name = name;
		this.hashCode = name.hashCode();
	}

	/**
	 * Returns the name associated with this key.
	 * @return Name of the settings key.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Key)) {
			return false;
		}
		return ((Key) obj).hashCode == hashCode;
	}
}
