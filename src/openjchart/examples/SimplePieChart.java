package openjchart.examples;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;

import openjchart.charts.PieChart;
import openjchart.data.DataMapper;
import openjchart.data.DataTable;

public class SimplePieChart extends JFrame {

	public SimplePieChart() {
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

		PieChart chart = new PieChart();
		// Change rotation
		//chart.setClockwise(false);
		// Custom start angle
		//chart.setStart(70);
		final JComponent plotArea = chart.getChartRenderer(data, mapper);
		getContentPane().add(plotArea, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimplePieChart test = new SimplePieChart();
		test.setVisible(true);
	}
}
