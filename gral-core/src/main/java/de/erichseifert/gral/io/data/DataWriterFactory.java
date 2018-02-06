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
package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import de.erichseifert.gral.io.AbstractIOFactory;

/**
 * <p>A factory class that produces {@code DataWriter} instances for a
 * specified format. The produced writers can be used to output a
 * {@code DataSource} to a data sink.</p>
 * <p>Example usage:</p>
 * <pre>
 * DataWriterFactory factory = DataWriterFactory.getInstance();
 * DataWriter writer = factory.get("image/png");
 * writer.write(data);
 * </pre>
 */
public final class DataWriterFactory extends AbstractIOFactory<DataWriter> {
	/** Singleton instance. */
	private static DataWriterFactory instance;

	/**
	 * Constructor that initializes the factory.
	 * @throws IOException if the properties file could not be found.
	 */
	private DataWriterFactory() throws IOException {
		super("datawriters.properties"); //$NON-NLS-1$
	}

	/**
	 * Returns the instance of the factory.
	 * @return Instance of the factory.
	 */
	public static DataWriterFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DataWriterFactory();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	@Override
	public DataWriter get(String mimeType) {
		DataWriter writer = null;
		Class<? extends DataWriter> clazz = getTypeClass(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DataWriter> constructor =
					clazz.getDeclaredConstructor(String.class);
				writer = constructor.newInstance(mimeType);
			}
		} catch (SecurityException | InvocationTargetException | IllegalAccessException | InstantiationException | IllegalArgumentException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (writer == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unsupported MIME type: {0}", mimeType)); //$NON-NLS-1$
		}

		return writer;
	}
}
