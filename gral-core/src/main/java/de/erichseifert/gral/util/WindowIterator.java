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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>An iterator that turns a sequence of values into a sequence of
 * overlapping windows. Each call of {@link #next()} shifts the window by one
 * value and returns a new list of the current contents.</p>
 *
 * <pre>
 * Iterator&lt;List&lt;Integer&gt;&gt; windows =
 *     new WindowIterator&lt;&gt;(Arrays.asList(1, 2, 3, 4).iterator(), 2);
 * // yields [1, 2], then [2, 3], then [3, 4]
 * </pre>
 *
 * <p>There is no padding at the ends: <i>m</i> input values and a window of
 * <i>n</i> produce <i>m&nbsp;&minus;&nbsp;n&nbsp;+&nbsp;1</i> windows, and no
 * window at all if the source is shorter than <i>n</i>. This is what the
 * window-based filters in {@link de.erichseifert.gral.data.filters} are built
 * on.</p>
 *
 * <p>Each returned list is a fresh copy, so it may be kept and modified. As
 * with {@link ConcatenationIterator}, {@link #remove()} is a no-op for
 * backwards compatibility rather than an exception.</p>
 *
 * @param <T> Type of the values.
 */
public class WindowIterator<T> implements Iterator<List<T>> {
	/** Source of the values. */
	private final Iterator<T> iterator;
	/** Values of the window that {@link #next()} will return. */
	private final Deque<T> window;

	/**
	 * Initializes a new instance that returns sliding windows of the specified
	 * size. If the source iterator provides fewer values than the window size,
	 * no window can be built and the new instance is empty.
	 * @param iterator Source iterator.
	 * @param windowSize Number of values in a window.
	 */
	public WindowIterator(Iterator<T> iterator, int windowSize) {
		this.iterator = iterator;

		this.window = new LinkedList<>();
		// Before the first call of Iterator.next(), the window contains an empty slot
		window.add(null);
		// ... and the other cells of the window are filled with values from the source iterator
		for (int windowIndex = 0; windowIndex < windowSize - 1; windowIndex++) {
			if (!iterator.hasNext()) {
				// The source iterator provides too few values for a complete
				// window
				window.clear();
				break;
			}
			window.add(iterator.next());
		}
	}

	@Override
	public boolean hasNext() {
		return !window.isEmpty() && iterator.hasNext();
	}

	@Override
	public List<T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		window.removeFirst();
		window.add(iterator.next());
		return new LinkedList<>(window);
	}

	/**
	 * Does nothing. See the class documentation for why this is not an
	 * {@code UnsupportedOperationException}.
	 */
	@Override
	public void remove() {
	}
}
