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
import java.io.InputStream;

import de.erichseifert.gral.data.DataSource;


/**
 * <p>Reads a {@link DataSource} from an input stream. Obtain an instance from
 * {@link DataReaderFactory} rather than constructing one, so that the format
 * stays exchangeable:</p>
 *
 * <pre>
 * DataReader reader = DataReaderFactory.getInstance().get("text/csv");
 * reader.setSetting(CSVReader.SEPARATOR_CHAR, ';');
 * try (InputStream in = new FileInputStream("values.csv")) {
 *     DataSource data = reader.read(in, Double.class, Double.class);
 * }
 * </pre>
 *
 * <p>The column types have to be supplied by the caller, since a text file
 * does not carry them. How the text of a cell is converted to a value of that
 * type is up to the reader; {@link CSVReader} looks for a static
 * {@code parse…(String)} method on the type, which the wrapper classes of the
 * primitives and {@code String} all provide.</p>
 *
 * @see DataReaderFactory
 * @see DataWriter
 */
public interface DataReader {
	/**
	 * Reads a data source from the specified stream. The stream is not closed
	 * by this method.
	 * @param input Input to be read.
	 * @param types Types for the columns of the data source, in column order.
	 * @return Imported data.
	 * @throws IOException when the file format is not valid or when
	 *         experiencing an error during file operations.
	 */
	DataSource read(InputStream input, Class<? extends Comparable<?>>... types)
		throws IOException;

	/**
	 * Returns the value of a format-specific setting, for example
	 * {@link CSVReader#SEPARATOR_CHAR}. The available keys are declared as
	 * constants on the concrete reader.
	 * @param <T> return type
	 * @param key key of the setting
	 * @return the value of the setting, or {@code null} if it is unknown
	 */
	<T> T getSetting(String key);

	/**
	 * Changes a format-specific setting. Settings have to be applied before
	 * {@link #read(InputStream, Class...)} is called.
	 * @param <T> value type
	 * @param key key of the setting
	 * @param value value of the setting
	 */
	<T> void setSetting(String key, T value);

}
