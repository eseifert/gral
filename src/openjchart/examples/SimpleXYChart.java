package openjchart.examples;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import openjchart.charts.ScatterPlot;
import openjchart.charts.axes.LogarithmicRenderer2D;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class SimpleXYChart extends JFrame {

	public SimpleXYChart() {
		super("OpenJChartTest");
		/*final DataTable data = new DataTable(Integer.class, Integer.class);
		data.add(1, 1);
		data.add(2, 3);
		data.add(3, 2);
		data.add(4, 6);
		data.add(5, 4);
		data.add(6, 8);
		data.add(7, 9);
		data.add(8, 11);*/
		final DataTable data = new DataTable(Double.class, Double.class, Double.class);
		/*data.add(-8.0, 2.07944154168);
		data.add(-7.0, 1.94591014906);
		data.add(-6.0, 1.79175946923);
		data.add(-5.0, 1.60943791243);
		data.add(-4.0, 1.38629436112);
		data.add(-3.0, 1.09861228867);
		data.add(-2.0, 0.69314718056);
		data.add(-1.0, 0.0);*/
		data.add(1.0, 0.0, 3.0);
		data.add(2.0, 0.69314718056, 3.0);
		data.add(3.0, 1.09861228867, 1.0);
		data.add(4.0, 1.38629436112, 5.0);
		data.add(5.0, 1.60943791243, 3.0);
		data.add(6.0, 1.79175946923, 7.0);
		data.add(7.0, 1.94591014906, 3.0);
		data.add(8.0, 2.07944154168, 4.0);
		DataSeries seriesLog = new DataSeries();
		seriesLog.put(DataSeries.X, 0);
		seriesLog.put(DataSeries.Y, 1);
		DataSeries seriesLin = new DataSeries();
		seriesLin.put(DataSeries.X, 0);
		seriesLin.put(DataSeries.Y, 0);
		seriesLin.put(DataSeries.SIZE, 2);
		ScatterPlot chart = new ScatterPlot(data, seriesLog, seriesLin);
		// Custom shape bounds
		//chart.getShapeRenderer().setBounds(new Rectangle2D.Double(-10.0, -5.0, 20.0, 5.0));
		// Custom shape coloring
		//chart.getShapeRenderer().setColor(Color.RED);
		// Grid disabled
		//chart.setGridEnabled(false);
		// Custom axis renderers
		chart.setAxisXRenderer(new LogarithmicRenderer2D());
		//chart.setAxisYRenderer(new LogarithmicRenderer2D());
		chart.getAxisXRenderer().setTickSpacing(0.67);
		chart.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(chart, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleXYChart test = new SimpleXYChart();
		test.setVisible(true);
	}
}
