/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
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

package openjchart.data.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import openjchart.data.DataSource;

/**
 * Class that writes a DataSource in a TSV-file.
 */
public class TSVWriter implements DataWriter {
	private final Writer output;

	/**
	 * Creates a new TSVWrites object with the specified Writer.
	 * @param output Writer used to export the DataSource.
	 */
	public TSVWriter(Writer output) {
		this.output = output;
	}

	@Override
	public void write(DataSource data) throws IOException {
		PrintWriter writer = new PrintWriter(output);
		for (Number[] row : data) {
			for (int col = 0; col < row.length; col++) {
				if (col > 0) {
					writer.print("\t");
				}
				writer.print(row[col]);
			}
			writer.println();
		}
	}

}
