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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.plots.XYPlot;
import openjchart.plots.io.DrawableWriter;
import openjchart.plots.io.DrawableWriterFactory;
import openjchart.plots.lines.DefaultLineRenderer2D;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.util.Insets2D;

public class DrawableWriterTest {
	private final DataTable data;
	private final XYPlot plot;

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

		LineRenderer2D lr1 = new DefaultLineRenderer2D();
		lr1.setSetting(LineRenderer2D.KEY_COLOR, Color.RED);
		plot.setLineRenderer(s1, lr1);

		LineRenderer2D lr2 = new DefaultLineRenderer2D();
		lr2.setSetting(LineRenderer2D.KEY_COLOR, Color.GREEN);
		plot.setLineRenderer(s2, lr2);

		LineRenderer2D lr3 = new DefaultLineRenderer2D();
		lr3.setSetting(LineRenderer2D.KEY_COLOR, Color.BLUE);
		plot.setLineRenderer(s3, lr3);
	}

	public void save() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				DrawableWriter writer = DrawableWriterFactory.getInstance().getDrawableWriter(DrawableWriter.TYPE_EPS);
				writer.write(plot, new FileOutputStream(file), 800, 600);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DrawableWriterTest test = new DrawableWriterTest();
		test.save();
	}

}
