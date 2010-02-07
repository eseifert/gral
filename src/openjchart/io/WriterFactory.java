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

package openjchart.io;


public interface WriterFactory<T> {

	/**
	 * Returns a DrawableWriter for the specified format.
	 * @param mimeType Output MIME-Type.
	 * @return Writer for the specified MIME-Type.
	 */
	public abstract T getWriter(String mimeType);

	public abstract WriterCapabilities getCapabilities(String mimeType);

	/**
	 * Returns an array of capabilities for all supported output formats.
	 * @return Supported capabilities.
	 */
	public abstract WriterCapabilities[] getCapabilities();

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