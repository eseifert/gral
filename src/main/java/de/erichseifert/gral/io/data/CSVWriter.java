/*
 * GRAL: GRAphing Library for Java(R)
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
import java.io.OutputStreamWriter;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.io.IOCapabilities;


/**
 * Class that writes a DataSource to a CSV file. By default the semicolon
 * character will be used for separating columns.
 * @see <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
 */
public class CSVWriter extends AbstractDataWriter {
	static {
		addCapabilities(new IOCapabilities(
			"CSV",
			"Comma separated values",
			"text/csv",
			"csv", "txt"
		));

		addCapabilities(new IOCapabilities(
			"TSV",
			"Tab separated values",
			"text/tab-separated-values",
			"tsv", "txt"
		));
	}

	/**
	 * Creates a new CSVWriter object with the specified MIME-Type.
	 * @param mimeType MIME-Type of the output file.
	 */
	public CSVWriter(String mimeType) {
		super(mimeType);
		setDefault("separator", ";");
	}

	@Override
	public void write(DataSource data, OutputStream output) throws IOException {
		String separator = getSetting("separator");
		OutputStreamWriter writer = new OutputStreamWriter(output);
		for (Row row : data) {
			for (int col = 0; col < row.size(); col++) {
				if (col > 0) {
					writer.write(separator);
				}
				writer.write(String.valueOf(row.get(col)));
			}
			// FIXME: Only works on *NIX systems
			writer.write("\r\n");
		}

		writer.close();
	}

}
