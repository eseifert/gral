package openjchart.examples.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import openjchart.data.DataTable;
import openjchart.plots.XYPlot;
import openjchart.plots.io.DrawableWriter;
import openjchart.plots.lines.DefaultLineRenderer2D;
import openjchart.util.Insets2D;

public class DrawableWriterTest {
	private final DataTable data;
	private final XYPlot plot;

	public DrawableWriterTest() {
		data = new DataTable(Double.class, Double.class);
		data.add(1.0, 4.5);
		data.add(1.5, 5.5);
		data.add(3.0, 3.5);
		data.add(4.0, 4.7);

		plot = new XYPlot(data);
		plot.setInsets(new Insets2D.Double(20, 50, 50, 20));
		plot.setLineRenderer(data, new DefaultLineRenderer2D());

	}

	public void save() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showSaveDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				DrawableWriter writer = new DrawableWriter(new FileOutputStream(file), DrawableWriter.FORMAT_SVG);
				writer.write(plot, 800, 600);
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
