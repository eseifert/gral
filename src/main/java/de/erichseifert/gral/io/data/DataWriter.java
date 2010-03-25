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
import java.io.OutputStream;

import de.erichseifert.gral.data.DataSource;


/**
 * Interface that provides a function to store a DataSource.
 */
public interface DataWriter {
	/** Use the BMP bitmap format for saving. */
	static final String TYPE_CSV = "text/csv";

	/**
	 * Stores the specified DataSource
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException
	 */
	void write(DataSource data, OutputStream output) throws IOException;
}
