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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.io;


public interface IOFactory<T> {

	/**
	 * Returns an object for reading or writing the specified format.
	 * @param mimeType Output MIME-Type.
	 * @return Reader or writer for the specified MIME-Type.
	 */
	public abstract T get(String mimeType);

	public abstract IOCapabilities getCapabilities(String mimeType);

	/**
	 * Returns an array of capabilities for all supported output formats.
	 * @return Supported capabilities.
	 */
	public abstract IOCapabilities[] getCapabilities();

	/**
	 * Returns an array of Strings containing all supported output formats.
	 * @return Supported formats.
	 */
	public abstract String[] getSupportedFormats();

	/**
	 * Returns true if the specified MIME-Type is supported.
	 * @param mimeType MIME-Type.
	 * @return True if supported.
	 */
	public abstract boolean isFormatSupported(String mimeType);

}