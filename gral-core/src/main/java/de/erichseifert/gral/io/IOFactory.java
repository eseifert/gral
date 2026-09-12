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
package de.erichseifert.gral.io;

import java.util.List;

/**
 * <p>Looks up a reader or writer by MIME type. This is the entry point of
 * GRAL's format plug-in system: the mapping from MIME type to implementation
 * class is read from properties files on the classpath, so a separate JAR can
 * add a format without any change to GRAL.</p>
 *
 * <pre>
 * DataWriterFactory factory = DataWriterFactory.getInstance();
 * if (factory.isFormatSupported("text/csv")) {
 *     DataWriter writer = factory.get("text/csv");
 *     …
 * }
 * </pre>
 *
 * <p>The three implementations are
 * {@link de.erichseifert.gral.io.data.DataReaderFactory},
 * {@link de.erichseifert.gral.io.data.DataWriterFactory} and
 * {@link de.erichseifert.gral.io.plots.DrawableWriterFactory}, each a singleton
 * obtained through its own {@code getInstance()} method.</p>
 *
 * @param <T> Class of the objects produced by the factory.
 * @see AbstractIOFactory
 */
public interface IOFactory<T> {
	/**
	 * Returns a new object for reading or writing the specified format. Each
	 * call creates a fresh instance, so settings applied to one do not affect
	 * another.
	 * @param mimeType MIME type.
	 * @return Reader or writer for the specified MIME type.
	 */
	T get(String mimeType);

	/**
	 * Returns the capabilities for a specific format.
	 * @param mimeType MIME type of the format
	 * @return Capabilities for the specified format.
	 */
	IOCapabilities getCapabilities(String mimeType);

	/**
	 * Returns a list of capabilities for all supported formats.
	 * @return Supported capabilities.
	 */
	List<IOCapabilities> getCapabilities();

	/**
	 * Returns an array of Strings containing all supported formats.
	 * @return Supported formats.
	 */
	String[] getSupportedFormats();

	/**
	 * Returns whether the specified MIME type is supported.
	 * @param mimeType MIME type.
	 * @return {@code true} if supported, otherwise {@code false}.
	 */
	boolean isFormatSupported(String mimeType);
}
