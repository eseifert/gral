package openjchart.examples;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import openjchart.charts.PieChart;
import openjchart.data.DataTable;

public class SimplePieChart extends JFrame {

	public SimplePieChart() {
		super("OpenJChartTest");
		final DataTable data = new DataTable(Integer.class);
		/*
		data.add(1, 1);
		data.add(2, 3);
		data.add(3, 2);
		data.add(4, 6);
		data.add(5, 4);
		data.add(6, 8);
		data.add(7, 9);
		data.add(8, 11);//*/
		//*
		Random r = new Random();
		for (int i = 0; i < 15; i++) {
			data.add(Math.abs(r.nextInt()) % 10 + 1);
		}
		PieChart chart = new PieChart(data);
		// Change rotation
		//chart.setSetting(PieChart.KEY_CLOCKWISE, false);
		// Custom start angle
		//chart.setSetting(PieChart.KEY_START, 70.0);
		// Custom colors
		//chart.setSetting(PieChart.KEY_COLORS, new RainbowColors());
		chart.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(chart, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimplePieChart test = new SimplePieChart();
		test.setVisible(true);
	}
}
