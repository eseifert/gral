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
import java.io.OutputStream;

import de.erichseifert.gral.data.DataSource;


/**
 * Interface that provides a function to store a data source.
 */
public interface DataWriter {
	/**
	 * Stores the specified data source.
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException if writing the data failed
	 */
	void write(DataSource data, OutputStream output) throws IOException;

	/**
	 * Returns the setting for the specified key.
	 * @param <T> return type
	 * @param key key of the setting
	 * @return the value of the setting
	 */
	<T> T getSetting(String key);

	/**
	 * Sets the setting for the specified key.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value value of the setting
	 */
	<T> void setSetting(String key, T value);

}
