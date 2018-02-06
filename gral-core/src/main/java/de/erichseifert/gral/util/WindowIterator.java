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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WindowIterator<T> implements Iterator<List<T>> {
	private final Iterator<T> iterator;
	private final Deque<T> window;

	public WindowIterator(Iterator<T> iterator, int windowSize) {
		this.iterator = iterator;

		this.window = new LinkedList<>();
		// Before the first call of Iterator.next(), the window contains an empty slot
		window.add(null);
		// ... and the other cells of the window are filled with values from the source iterator
		for (int windowIndex = 0; windowIndex < windowSize - 1; windowIndex++) {
			window.add(iterator.next());
		}
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public List<T> next() {
		window.removeFirst();
		window.add(iterator.next());
		return new LinkedList<>(window);
	}

	@Override
	public void remove() {
	}
}
