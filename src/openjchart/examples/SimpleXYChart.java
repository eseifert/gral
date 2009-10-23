package openjchart.examples;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;

import openjchart.charts.ScatterPlot;
import openjchart.data.DataMapper;
import openjchart.data.DataTable;

public class SimpleXYChart extends JFrame {

	public SimpleXYChart() {
		super("OpenJChartTest");
		final DataTable data = new DataTable(Integer.class, Integer.class);
		data.add(1, 1);
		data.add(2, 3);
		data.add(3, 2);
		data.add(4, 6);
		data.add(5, 4);
		data.add(6, 8);
		data.add(7, 9);
		data.add(8, 11);
		DataMapper mapper = new DataMapper();
		mapper.put(DataMapper.X, 0);
		mapper.put(DataMapper.Y, 1);

		ScatterPlot chart = new ScatterPlot();
		// Custom shape
		//chart.setShape(new Ellipse2D.Double(-5.0, -5.0, 10, 10));
		// Custom shape coloring
		//chart.setShapeColor(Color.RED);
		// Grid disabled
		//chart.setGridEnabled(false);
		final JComponent plotArea = chart.getChartRenderer(data, mapper);
		//plotArea.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(plotArea, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleXYChart test = new SimpleXYChart();
		test.setVisible(true);
	}
}
