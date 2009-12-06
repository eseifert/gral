package openjchart.examples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import openjchart.DrawablePanel;
import openjchart.Legend;
import openjchart.DrawableConstants.Location;
import openjchart.DrawableConstants.Orientation;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.data.filters.Convolution;
import openjchart.plots.Plot;
import openjchart.plots.XYPlot;
import openjchart.plots.lines.DefaultLineRenderer2D;
import openjchart.util.Insets2D;
import openjchart.util.MathUtils;

public class ConvolutionExample extends JFrame {

	public ConvolutionExample() {
		super("OpenJChartTest");
		DataTable data = new DataTable(Double.class, Double.class);
		Random r = new Random();
		for (int i=0; i<200; i++) {
			double x = i/2.0/3.141;
			double y = 10.0*Math.sin(x/5.0) + r.nextGaussian()*Math.cos(x/2.0);
			data.add(x, y);
		}
		DataSeries ds = new DataSeries(data, 0, 1);

		final double KERNEL_VARIANCE = 5.0;

		double[] kernel = MathUtils.normalize(MathUtils.getBinomial(KERNEL_VARIANCE));
		Convolution dataSmoothed = new Convolution(data, kernel, Convolution.Mode.MODE_REPEAT, 1);
		DataSeries dsSmoothed = new DataSeries(dataSmoothed, 0, 1);

		double[] kernel2 = MathUtils.negate(MathUtils.normalize(MathUtils.getBinomial(KERNEL_VARIANCE)));
		kernel2[kernel2.length/2] += 1.0;
		Convolution dataHighpass = new Convolution(data, kernel2, Convolution.Mode.MODE_REPEAT, 1);
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
		lr3.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(0f, 0.7f, 0f));
		plot.setLineRenderer(dsHighpass, lr3);

		plot.setSetting(Plot.KEY_LEGEND, true);
		plot.setSetting(Plot.KEY_LEGEND_POSITION, Location.SOUTH_WEST);
		Legend legend = plot.getLegend();
		legend.setSetting(Legend.KEY_BACKGROUND, Color.WHITE);
		legend.setSetting(Legend.KEY_BORDER, new BasicStroke(1f));
		legend.setSetting(Legend.KEY_ORIENTATION, Orientation.HORIZONTAL);
		legend.add(ds, "Data", plot.getShapeRenderer(ds), plot.getLineRenderer(ds));
		legend.add(dsSmoothed, "Lowpass", plot.getShapeRenderer(dsSmoothed), plot.getLineRenderer(dsSmoothed));
		legend.add(dsHighpass, "Highpass", plot.getShapeRenderer(dsHighpass), plot.getLineRenderer(dsHighpass));
		
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new DrawablePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(getContentPane().getMinimumSize());
		setSize(800, 600);
	}

	public static void main(String[] args) {
		ConvolutionExample test = new ConvolutionExample();
		test.setVisible(true);
	}
}
