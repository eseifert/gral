/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.erichseifert.gral.io.AbstractIOFactory;


public class DataReaderFactory extends AbstractIOFactory<DataReader> {
	private static DataReaderFactory instance;

	private DataReaderFactory() throws IOException {
		super("datareaders.properties");
	}

	public static DataReaderFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DataReaderFactory();
			} catch (IOException e) {
			}
		}
		return instance;
	}

	@Override
	public DataReader get(String mimeType) {
		DataReader reader = null;
		Class<? extends DataReader> clazz = entries.get(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DataReader> constructor = clazz.getDeclaredConstructor(String.class);
				reader = constructor.newInstance(mimeType);
			}
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

		if (reader == null) {
			throw new IllegalArgumentException("Unsupported MIME type: "+mimeType);
		}

		return reader;
	}
}
