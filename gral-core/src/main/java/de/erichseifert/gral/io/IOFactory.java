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
package de.erichseifert.gral.io;

import java.util.List;

/**
 * Interface for factories producing input (reader) or output (writer) classes.
 * This is be used to create a extensible plug-in system for reading or writing
 * data.
 * @param <T> Class of the objects produced by the factory.
 */
public interface IOFactory<T> {
	/**
	 * Returns an object for reading or writing the specified format.
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
