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
package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.io.OutputStream;

import de.erichseifert.gral.data.DataSource;


/**
 * <p>Writes a {@link DataSource} to an output stream. Obtain an instance from
 * {@link DataWriterFactory} rather than constructing one, so that the format
 * stays exchangeable:</p>
 *
 * <pre>
 * DataWriter writer = DataWriterFactory.getInstance().get("text/csv");
 * try (OutputStream out = new FileOutputStream("values.csv")) {
 *     writer.write(data, out);
 * }
 * </pre>
 *
 * @see DataWriterFactory
 * @see DataReader
 */
public interface DataWriter {
	/**
	 * Writes the specified data source to the stream. Whether the stream is
	 * closed afterwards is left to the implementation, so callers should manage
	 * it themselves, for example with try-with-resources.
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException if writing the data failed
	 */
	void write(DataSource data, OutputStream output) throws IOException;

	/**
	 * Returns the value of a format-specific setting, for example
	 * {@link CSVWriter#SEPARATOR_CHAR}. The available keys are declared as
	 * constants on the concrete writer.
	 * @param <T> return type
	 * @param key key of the setting
	 * @return the value of the setting, or {@code null} if it is unknown
	 */
	<T> T getSetting(String key);

	/**
	 * Changes a format-specific setting. Settings have to be applied before
	 * {@link #write(DataSource, OutputStream)} is called.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value value of the setting
	 */
	<T> void setSetting(String key, T value);

}
