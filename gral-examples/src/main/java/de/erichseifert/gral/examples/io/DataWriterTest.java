/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.examples.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.data.DataWriter;
import de.erichseifert.gral.io.data.DataWriterFactory;


public class DataWriterTest {
	private final DataTable data;

	@SuppressWarnings("unchecked")
	public DataWriterTest() {
		data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		data.add(1.0, 4.5, 4.3, 4.0);
		data.add(1.5, 5.5, 5.3, 5.0);
		data.add(3.0, 3.5, 3.7, 4.0);
		data.add(4.0, 4.7, 4.5, 4.3);
	}

	public void save() throws IOException {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			 OutputStream output = new FileOutputStream(file);
			DataWriter writer = DataWriterFactory.getInstance().get("text/csv");
			writer.write(data, output);
		}
	}

	public static void main(String[] args) {
		DataWriterTest test = new DataWriterTest();
		try {
			test.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
