package openjchart.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import openjchart.DrawablePanel;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.data.filters.Convolution;
import openjchart.plots.XYPlot;
import openjchart.plots.lines.DefaultLineRenderer2D;
import openjchart.util.Insets2D;

public class ConvolutionExample extends JFrame {

	public ConvolutionExample() {
		super("OpenJChartTest");
		DataTable data = new DataTable(Double.class, Double.class);
		Random r = new Random();
		for (int i=0; i<200; i++) {
			double x = i/2.0/3.141;
			double y = 10.0*Math.sin(x/5.0) + 2.0*r.nextGaussian()*Math.cos(x/2.5);
			data.add(x, y);
		}
		DataSeries ds = new DataSeries(data, 0, 1);

		Convolution dataSmoothed = new Convolution(data, new double[] {0.0625, 0.2500, 0.3750, 0.2500, 0.0625}, Convolution.MODE_REPEAT, 1);
		DataSeries dsSmoothed = new DataSeries(dataSmoothed, 0, 1);

		Convolution dataHighpass = new Convolution(data, new double[] {-0.0625, -0.2500, 0.6250, -0.2500, -0.0625}, Convolution.MODE_REPEAT, 1);
		DataSeries dsHighpass = new DataSeries(dataHighpass, 0, 1);

		XYPlot plot = new XYPlot(dsHighpass, ds, dsSmoothed);

		plot.setShapeRenderer(ds, null);
		DefaultLineRenderer2D lr1 = new DefaultLineRenderer2D();
		lr1.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, Color.DARK_GRAY);
		plot.setLineRenderer(ds, lr1);

		plot.setShapeRenderer(dsSmoothed, null);
		DefaultLineRenderer2D lr2 = new DefaultLineRenderer2D();
		lr2.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, Color.RED);
		plot.setLineRenderer(dsSmoothed, lr2);

		plot.setShapeRenderer(dsHighpass, null);
		DefaultLineRenderer2D lr3 = new DefaultLineRenderer2D();
		lr3.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, Color.GRAY);
		plot.setLineRenderer(dsHighpass, lr3);

		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new DrawablePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		ConvolutionExample test = new ConvolutionExample();
		test.setVisible(true);
	}
}
