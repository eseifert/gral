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

package de.erichseifert.gral.io.plots;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.erichseifert.gral.io.AbstractIOFactory;


/**
 * Class that provides <code>DrawableWriter</code> implementations for
 * different file formats.
 * @see DrawableWriter
 */
public class DrawableWriterFactory extends AbstractIOFactory<DrawableWriter> {
	private static DrawableWriterFactory instance;

	private DrawableWriterFactory() {
		super("drawablewriters.properties");
	}

	/**
	 * Returns an instance of this DrawableWriterFactory.
	 * @return Instance.
	 */
	public static DrawableWriterFactory getInstance() {
		if (instance == null) {
			instance = new DrawableWriterFactory();
		}
		return instance;
	}

	@Override
	public DrawableWriter get(String mimeType) {
		DrawableWriter writer = null;
		Class<? extends DrawableWriter> clazz = entries.get(mimeType);
		//IOCapabilities capabilities = getCapabilities(mimeType);
		try {
			Constructor<? extends DrawableWriter> constructor = clazz.getDeclaredConstructor(String.class);
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
