/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.io;

/**
 * Class that stores information on a {@link de.erichseifert.gral.io.plots.DrawableWriter}.
 */
public class WriterCapabilities {
	private final String format;
	private final String name;
	private final String mimeType;
	private final String[] extensions;

	/**
	 * Creates a new <code>WriterCapabilities</code> object with the specified format,
	 * name, MIME-Type and filename extensions.
	 * @param format Format.
	 * @param name Name.
	 * @param mimeType MIME-Type
	 * @param extensions Extensions.
	 */
	public WriterCapabilities(String format, String name, String mimeType, String... extensions) {
		this.format = format;
		this.name = name;
		this.mimeType = mimeType;
		// TODO: Check that there is at least one filename extension
		this.extensions = extensions;
	}

	/**
	 * Returns the format.
	 * @return Format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Returns the name of the format.
	 * @return Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the MIME-Type of the format.
	 * @return Format.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Returns an array with Strings containing all possible filename
	 * extensions.
	 * @return Filename Extensions.
	 */
	public String[] getExtensions() {
		return extensions;
	}
}
