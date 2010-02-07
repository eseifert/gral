/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.io.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import openjchart.io.AbstractWriterFactory;

public class DataWriterFactory extends AbstractWriterFactory<DataWriter> {
	private static DataWriterFactory instance;

	private DataWriterFactory() {
		super("datawriters.properties");
	}

	public static DataWriterFactory getInstance() {
		if (instance == null) {
			instance = new DataWriterFactory();
		}
		return instance;
	}

	@Override
	public DataWriter getWriter(String mimeType) {
		DataWriter writer = null;
		Class<? extends DataWriter> clazz = writers.get(mimeType);
		//WriterCapabilities capabilities = getCapabilities(mimeType);
		try {
			Constructor<? extends DataWriter> constructor = clazz.getDeclaredConstructor(String.class);
			writer = constructor.newInstance(mimeType);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (writer == null) {
			throw new IllegalArgumentException("Unsupported MIME-Type: "+mimeType);
		}

		return writer;
	}
}
