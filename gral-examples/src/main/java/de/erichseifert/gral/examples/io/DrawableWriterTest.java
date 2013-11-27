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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.util.Insets2D;


public class DrawableWriterTest {
	private final DataTable data;
	private final XYPlot plot;

	@SuppressWarnings("unchecked")
	public DrawableWriterTest() {
		data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		data.add(1.0, 4.5, 4.3, 4.0);
		data.add(1.5, 5.5, 5.3, 5.0);
		data.add(3.0, 3.5, 3.7, 4.0);
		data.add(4.0, 4.7, 4.5, 4.3);

		DataSeries s1 = new DataSeries(data, 0, 1);
		DataSeries s2 = new DataSeries(data, 0, 2);
		DataSeries s3 = new DataSeries(data, 0, 3);

		plot = new XYPlot(s1, s2, s3);
		plot.setInsets(new Insets2D.Double(20, 50, 50, 20));

		LineRenderer lr1 = new DefaultLineRenderer2D();
		lr1.setColor(Color.RED);
		plot.setLineRenderer(s1, lr1);

		LineRenderer lr2 = new DefaultLineRenderer2D();
		lr2.setColor(Color.GREEN);
		plot.setLineRenderer(s2, lr2);

		LineRenderer lr3 = new DefaultLineRenderer2D();
		lr3.setColor(Color.BLUE);
		plot.setLineRenderer(s3, lr3);
	}

	public void save() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				DrawableWriter writer = DrawableWriterFactory.getInstance().get("application/postscript");
				writer.write(plot, new FileOutputStream(file), 800, 600);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DrawableWriterTest test = new DrawableWriterTest();
		test.save();
	}

}
