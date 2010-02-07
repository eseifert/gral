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

package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.io.AbstractWriter;
import de.erichseifert.gral.io.WriterCapabilities;


/**
 * Class that writes a DataSource in a TSV-file.
 */
public class TSVWriter extends AbstractWriter implements DataWriter {
	static {
		WriterCapabilities CSV_CAPABILITIES = new WriterCapabilities(
			"CSV",
			"Comma separated value",
			TYPE_CSV,
			"csv", "txt"
		);
		addCapabilities(CSV_CAPABILITIES);
	}

	private final String mimeType;

	/**
	 * Creates a new TSVWrites object with the specified MIME-Type.
	 * @param mimeType MIME-Type of the output file.
	 */
	public TSVWriter(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public void write(DataSource data, OutputStream output) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output);
		for (Number[] row : data) {
			for (int col = 0; col < row.length; col++) {
				if (col > 0) {
					writer.write("\t");
				}
				writer.write(String.valueOf(row[col]));
			}
			// FIXME: Only works on *NIX systems
			writer.write("\n");
		}

		writer.close();
	}

	public String getMimeType() {
		return mimeType;
	}

}
