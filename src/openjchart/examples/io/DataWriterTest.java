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

package openjchart.examples.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;

import openjchart.data.DataTable;
import openjchart.io.data.DataWriter;
import openjchart.io.data.DataWriterFactory;

public class DataWriterTest {
	private final DataTable data;

	public DataWriterTest() {
		data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		data.add(1.0, 4.5, 4.3, 4.0);
		data.add(1.5, 5.5, 5.3, 5.0);
		data.add(3.0, 3.5, 3.7, 4.0);
		data.add(4.0, 4.7, 4.5, 4.3);
	}

	public void save() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				OutputStream output = new FileOutputStream(file);
				DataWriter writer = DataWriterFactory.getInstance().getWriter(DataWriter.TYPE_CSV);
				writer.write(data, output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DataWriterTest test = new DataWriterTest();
		test.save();
	}

}
