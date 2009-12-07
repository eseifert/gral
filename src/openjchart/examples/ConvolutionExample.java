package openjchart.examples;

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
		DataSeries ds = new DataSeries("Data", data, 0, 1);

		final double KERNEL_VARIANCE = 5.0;

		double[] kernelLowpass = MathUtils.normalize(MathUtils.getBinomial(KERNEL_VARIANCE));
		Convolution dataLowpass = new Convolution(data, kernelLowpass, Convolution.Mode.MODE_REPEAT, 1);
		DataSeries dsLowpass = new DataSeries("Lowpass", dataLowpass, 0, 1);

		double[] kernelHighpass = MathUtils.negate(MathUtils.normalize(MathUtils.getBinomial(KERNEL_VARIANCE)));
		kernelHighpass[kernelHighpass.length/2] += 1.0;
		Convolution dataHighpass = new Convolution(data, kernelHighpass, Convolution.Mode.MODE_REPEAT, 1);
		DataSeries dsHighpass = new DataSeries("Highpass", dataHighpass, 0, 1);

		XYPlot plot = new XYPlot(dsHighpass, ds, dsLowpass);

		plot.setShapeRenderer(ds, null);
		DefaultLineRenderer2D lineData = new DefaultLineRenderer2D();
		lineData.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, Color.DARK_GRAY);
		plot.setLineRenderer(ds, lineData);

		plot.setShapeRenderer(dsLowpass, null);
		DefaultLineRenderer2D lineLowpass = new DefaultLineRenderer2D();
		lineLowpass.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, Color.RED);
		plot.setLineRenderer(dsLowpass, lineLowpass);

		plot.setShapeRenderer(dsHighpass, null);
		DefaultLineRenderer2D lineHighpass = new DefaultLineRenderer2D();
		lineHighpass.setSetting(DefaultLineRenderer2D.KEY_LINE_COLOR, new Color(0f, 0.7f, 0f));
		plot.setLineRenderer(dsHighpass, lineHighpass);

		// TODO: Put better default styles to standard constructor of Legend
		plot.setSetting(Plot.KEY_LEGEND, true);
		plot.setSetting(Plot.KEY_LEGEND_POSITION, Location.SOUTH_WEST);
		Legend legend = plot.getLegend();
		legend.setSetting(Legend.KEY_ORIENTATION, Orientation.HORIZONTAL);
		legend.add(ds);
		legend.add(dsLowpass);
		legend.add(dsHighpass);

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
