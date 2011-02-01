/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
 * Resizable implementation of the List interface that automatically sorts all
 * values. It implements the methods <code>get</code>, <code>size</code>,
 * <code>add</code>, and <code>size</code>. The stored elements must implement
 * the interface <code>Comparable</code>.
 * @param <T> Data type of stored elements.
 */
public class SortedList<T extends Comparable<T>> extends AbstractList<T> {
	private final List<T> elements;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * @param initialCapacity Initial capacity of the list.
	 */
	public SortedList(int initialCapacity) {
		elements = new ArrayList<T>(initialCapacity);
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
	public int indexOf(Object o) {
		try {
			return Collections.binarySearch(elements, (T) o);
		} catch (NullPointerException e) {
			return -1;
		} catch (ClassCastException e) {
			return -1;
		}
	}
}
