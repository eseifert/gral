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
package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import de.erichseifert.gral.io.AbstractIOFactory;

/**
 * <p>A factory class that produces {@code DataReader} instances for a
 * specified format. The produced readers can be used to retrieve data from
 * an {@code InputStream} and to get a {@code DataSource} instance.</p>
 * <p>Example usage:</p>
 * <pre>
 * DataReaderFactory factory = DataReaderFactory.getInstance();
 * DataReader reader = factory.get("text/csv");
 * DataSource data = reader.read(
 *     new FileInputStream(filename), Double.class, Double.class);
 * </pre>
 * <p>The supported MIME types are {@code text/csv},
 * {@code text/tab-separated-values}, the bitmap types {@code image/png},
 * {@code image/jpeg}, {@code image/bmp}, {@code image/gif} and
 * {@code image/vnd.wap.wbmp}, and {@code audio/wav}. Asking for an unsupported
 * type throws {@code IllegalArgumentException}, which
 * {@link #isFormatSupported(String)} can be used to avoid.</p>
 */
public final class DataReaderFactory extends AbstractIOFactory<DataReader> {
	/** Singleton instance. */
	private static DataReaderFactory instance;

	/**
	 * Constructor that initializes the factory.
	 * @throws IOException if the properties file could not be found.
	 */
	private DataReaderFactory() throws IOException {
		super("datareaders.properties"); //$NON-NLS-1$
	}

	/**
	 * Returns the instance of the factory.
	 * @return Instance of the factory.
	 */
	public static DataReaderFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DataReaderFactory();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	@Override
	public DataReader get(String mimeType) {
		DataReader reader = null;
		Class<? extends DataReader> clazz = getTypeClass(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DataReader> constructor =
					clazz.getDeclaredConstructor(String.class);
				reader = constructor.newInstance(mimeType);
			}
		} catch (SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException | IllegalArgumentException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (reader == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unsupported MIME type: {0}", mimeType)); //$NON-NLS-1$
		}

		return reader;
	}
}
