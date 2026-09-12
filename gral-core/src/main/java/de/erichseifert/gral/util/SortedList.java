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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>A {@code List} that keeps its elements in ascending order. Each
 * {@link #add(Comparable)} inserts the element at the position its natural
 * ordering dictates, found by binary search, so the list is sorted at all times
 * rather than only after an explicit sort.</p>
 *
 * <p>Because the position of an element is decided by its value,
 * {@code add(int, T)} and {@code set(int, T)} are not supported and the
 * inherited implementations throw. The main use in GRAL is
 * {@link de.erichseifert.gral.data.statistics.Statistics}, which needs sorted
 * values in order to compute quantiles.</p>
 *
 * @param <T> Data type of stored elements.
 */
public class SortedList<T extends Comparable<T>> extends AbstractList<T> {
	private final List<T> elements;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * @param initialCapacity Initial capacity of the list.
	 */
	public SortedList(int initialCapacity) {
		elements = new ArrayList<>(initialCapacity);
	}

	/**
	 * Constructs a list containing the elements of the specified collection.
	 * @param c Collection whose elements are to be added.
	 */
	public SortedList(Collection<? extends T> c) {
		this(c.size());
		for (T e : c) {
			add(e);
		}
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public SortedList() {
		this(10);
	}

	@Override
	public T get(int index) {
		return elements.get(index);
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public boolean add(T e) {
		if (elements.isEmpty()) {
			elements.add(e);
			return true;
		}
		int index = Collections.binarySearch(elements, e);
		if (index < 0) {
			index = -index - 1;
		}
		elements.add(index, e);
		return true;
	}

	@Override
	public T remove(int index) {
		return elements.remove(index);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int indexOf(Object o) {
		int index;
		try {
			index = Collections.binarySearch(elements, (T) o);
		} catch (NullPointerException | ClassCastException e) {
			return -1;
		}
		if (index < 0) {
			return -1;
		}
		// A binary search may return any of several equal elements, but the
		// contract of List.indexOf demands the lowest index
		while (index > 0 && elements.get(index - 1).equals(o)) {
			index--;
		}
		return index;
	}
}
