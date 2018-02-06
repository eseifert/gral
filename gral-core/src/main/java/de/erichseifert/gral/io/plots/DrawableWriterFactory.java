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
package de.erichseifert.gral.io.plots;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import de.erichseifert.gral.io.AbstractIOFactory;


/**
 * <p>Class that provides {@code DrawableWriter} implementations for
 * different file formats.</p>
 *
 * <p>Example Usage:</p>
 * <pre>
 * DrawableWriterFactory factory = DrawableWriterFactory.getInstance();
 * DrawableWriter writer = factory.get("application/pdf");
 * writer.write(plot, new FileOutputStream(filename));
 * </pre>
 *
 * @see DrawableWriter
 */
public final class DrawableWriterFactory extends AbstractIOFactory<DrawableWriter> {
	/** Singleton instance. */
	private static DrawableWriterFactory instance;

	/**
	 * Constructor that initializes the factory.
	 * @throws IOException if the properties file could not be found.
	 */
	private DrawableWriterFactory() throws IOException {
		super("drawablewriters.properties"); //$NON-NLS-1$
	}

	/**
	 * Returns an instance of this DrawableWriterFactory.
	 * @return Instance.
	 */
	public static DrawableWriterFactory getInstance() {
		if (instance == null) {
			try {
				instance = new DrawableWriterFactory();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	@Override
	public DrawableWriter get(String mimeType) {
		DrawableWriter writer = null;
		Class<? extends DrawableWriter> clazz = getTypeClass(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			if (clazz != null) {
				Constructor<? extends DrawableWriter> constructor =
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
