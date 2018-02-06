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
package de.erichseifert.gral.data;

import static java.util.Arrays.copyOf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Record implements Iterable<Comparable<?>>, Serializable {
	private Comparable[] values;

	public Record(List<? extends Comparable<?>> values) {
		this.values = values.toArray(new Comparable[0]);
	}

	public Record(Comparable<?>... values) {
		this.values = copyOf(values, values.length);
	}

	public <T extends Comparable<?>> T get(int index) {
		return (T) values[index];
	}

	public int size() {
		return values.length;
	}

	@Override
	public Iterator<Comparable<?>> iterator() {
		// More readable version using Arrays.asList is prevented by broken Generics system
		List<Comparable<?>> list = new ArrayList<>(values.length);
		for (Comparable value : values) {
			list.add(value);
		}
		return list.iterator();
	}

	public boolean isNumeric(int index) {
		return values[index] instanceof Number;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Record)) {
			return false;
		}
		Record record = (Record) obj;
		return size() == record.size() && Arrays.equals(this.values, record.values);
	}

	@Override
	public String toString() {
		StringBuilder representation = new StringBuilder("(");
		for (int elementIndex = 0; elementIndex < values.length; elementIndex++) {
			Comparable element = values[elementIndex];
			representation.append(element);
			if (elementIndex != values.length - 1) {
				representation.append(", ");
			}
		}
		representation.append(")");
		return representation.toString();
	}

	public Record insert(Comparable<?> value, int position) {
		List<Comparable<?>> recordCopyAsList = new ArrayList<>(values.length + 1);
		for (Comparable<?> v : values) {
			recordCopyAsList.add(v);
		}
		recordCopyAsList.add(position, value);
		return new Record(recordCopyAsList);
	}
}
