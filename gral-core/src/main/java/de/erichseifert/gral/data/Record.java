/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2016 Erich Seifert <dev[at]erichseifert.de>,
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Record implements Iterable<Comparable<?>> {
	private Comparable[] values;

	public Record(List<Comparable<?>> values) {
		this.values = values.toArray(new Comparable[0]);
	}

	public Record(Comparable<?>... values) {
		this.values = Arrays.copyOf(values, values.length);
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
		List<Comparable<?>> list = new ArrayList<Comparable<?>>(values.length);
		for (Comparable value : values) {
			list.add(value);
		}
		return list.iterator();
	}
}
