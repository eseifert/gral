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
import java.io.OutputStreamWriter;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.Messages;


/**
 * <p>Class that writes all values of a {@code DataSource} to a character
 * separated file. The file then stores the values separated by a certain
 * delimiter character. The delimiter is chosen based on the file type but can
 * also be set manually. By default the comma character will be used as a
 * delimiter for separating columns. Lines end with a carriage return and a
 * line feed character.</p>
 * <p>{@code CSVWriter} instances should be obtained by the
 * {@link DataWriterFactory} rather than being created manually:</p>
 * <pre>
 * DataWriterFactory factory = DataWriterFactory.getInstance();
 * DataWriter writer = factory.get("text/csv");
 * writer.write(data, new FileOutputStream(filename));
 * </pre>
 * @see <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
 */
public class CSVWriter extends AbstractDataWriter {
	/** Key for specifying a {@link Character} value that defines the
	delimiting character used to separate columns. */
	public static final String SEPARATOR_CHAR = CSVReader.SEPARATOR_CHAR;

	static {
		addCapabilities(new IOCapabilities(
			"CSV", //$NON-NLS-1$
			Messages.getString("DataIO.csvDescription"), //$NON-NLS-1$
			"text/csv", //$NON-NLS-1$
			new String[] {"csv", "txt"} //$NON-NLS-1$ //$NON-NLS-2$
		));

		addCapabilities(new IOCapabilities(
			"TSV", //$NON-NLS-1$
			Messages.getString("DataIO.tsvDescription"), //$NON-NLS-1$
			"text/tab-separated-values", //$NON-NLS-1$
			new String[] {
				"tsv", "tab", "txt"} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		));
	}

	/**
	 * Creates a new instance with the specified MIME-Type. The delimiter is
	 * set depending on the MIME type parameter. By default a comma is used as
	 * a delimiter.
	 * @param mimeType MIME-Type of the output file.
	 */
	public CSVWriter(String mimeType) {
		super(mimeType);
		if ("text/tab-separated-values".equals(mimeType)) { //$NON-NLS-1$
			setDefault(SEPARATOR_CHAR, '\t'); //$NON-NLS-1$
		} else {
			setDefault(SEPARATOR_CHAR, ','); //$NON-NLS-1$
		}
	}

	/**
	 * Stores the specified data source.
	 * @param data DataSource to be stored.
	 * @param output OutputStream to be written to.
	 * @throws IOException if writing the data failed
	 */
	public void write(DataSource data, OutputStream output) throws IOException {
		Character separator = getSetting(SEPARATOR_CHAR);
		OutputStreamWriter writer = new OutputStreamWriter(output);

		int i = 0;
		int colCount = data.getColumnCount();
		for (Comparable<?> cell : data) {
			writer.write(String.valueOf(cell));

			int col = i % colCount;
			if (col < colCount - 1) {
				writer.write(separator);
			} else {
				writer.write("\r\n"); //$NON-NLS-1$
			}
			i++;
		}

		writer.close();
	}

}
