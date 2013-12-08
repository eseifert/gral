/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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

import java.io.Serializable;
import java.util.Arrays;

/**
 * Immutable class for storing n-dimensional tuples.
 */
public class Tuple implements Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -4946415830302551257L;

	/** Elements of the tuple. **/
	private final Object[] elements;

	/**
	 * Constructor that initializes the tuple with a list of elements.
	 * @param elements Element values.
	 */
	public Tuple(Object... elements) {
		this.elements = Arrays.copyOf(elements, elements.length);
	}

	/**
	 * Returns the number of elements.
	 * @return Number of elements.
	 */
	public int size() {
		return elements.length;
	}

	/**
	 * Returns the value of a specified element.
	 * @param i element.
	 * @return Element value.
	 */
	public Object get(int i) {
		return elements[i];
	}

	@Override
	public String toString() {
		return getClass().getName() + Arrays.deepToString(elements);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tuple)) {
			return false;
		}
		Tuple t = (Tuple) obj;
		if (size() != t.size()) {
			return false;
		}
		for (int i = 0; i < elements.length; i++) {
			if (!get(i).equals(t.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (Object i : elements) {
			hashCode ^= i.hashCode();
		}
		return hashCode;
	}

}
