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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConcatenationIterator<T> implements Iterator<T> {
	private final Iterator<T>[] inputIterators;

	public ConcatenationIterator(Iterator<T>... inputIterators) {
		this.inputIterators = Arrays.copyOf(inputIterators, inputIterators.length);
	}

	@Override
	public boolean hasNext() {
		for (Iterator<T> inputIterator : inputIterators) {
			if (inputIterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public T next() {
		for (Iterator<T> inputIterator : inputIterators) {
			if (inputIterator.hasNext()) {
				return inputIterator.next();
			}
		}
		throw new NoSuchElementException("No elements left in concatenated iterator.");
	}

	@Override
	public void remove() {
	}
}
