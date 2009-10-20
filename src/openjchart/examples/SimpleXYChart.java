package openjchart.examples;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		/*Random r = new Random();
		for (int i = 0; i < 30; i++) {
			data.add(r.nextDouble()*10, r.nextDouble()*20);
		}*/
		DataMapper mapper = new DataMapper();
		mapper.put(DataMapper.X, 0);
		mapper.put(DataMapper.Y, 1);

		ScatterPlot chart = new ScatterPlot();
		final JComponent plotArea = chart.getChartRenderer(data, mapper);
		plotArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				/*data.clear();
				Random r = new Random();
				for (int i = 0; i < 30000; i++) {
					data.add(r.nextDouble()*10, r.nextDouble()*20);
				}

				plotArea.repaint();*/
			}
		});
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
