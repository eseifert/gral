/**
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.examples.xyplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;

import de.erichseifert.gral.InteractivePanel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer2D;
import de.erichseifert.gral.util.Insets2D;

final class UpdateTask extends TimerTask {
	private final DataTable data;
	private final Plot plot;
	private final JComponent component;

	public UpdateTask(DataTable data, XYPlot plot, JComponent comp) {
		this.data = data;
		this.plot = plot;
		this.component = comp;
	}

	@Override
	public void run() {
		if (!component.isVisible()) {
			return;
		}
		long time = System.currentTimeMillis();
		long memTotal = Runtime.getRuntime().totalMemory();
		long memFree = Runtime.getRuntime().freeMemory();
		double memUsedPercentage = 1.0 - memFree/(double)memTotal;

		data.add(time, memUsedPercentage);
		data.remove(0);

		Statistics stats = data.getStatistics();
		plot.getAxis(Axis.X).setRange(stats.get(Statistics.MIN, 0), stats.get(Statistics.MAX, 0));
		//plot.getAxis(Axis.Y).setRange(stats.get(Statistics.MIN, 1), stats.get(Statistics.MAX, 1));

		component.repaint();
	}
}

public class MemoryUsage extends JFrame {
	private static final int BUFFER_SIZE = 200;
	private static final int INTERVAL = 100;

	public MemoryUsage() {
		super("GRALTest");
		getContentPane().setBackground(new Color(1.0f, 0.92f, 0.90f));

		DataTable data = new DataTable(Long.class, Double.class);
		long time = System.currentTimeMillis();
		for (int i=BUFFER_SIZE - 1; i>=0; i--) {
			data.add(time - i*INTERVAL, (i == BUFFER_SIZE - 1) ? 0.0 : Double.NaN);
		}

		XYPlot plot = new XYPlot(data);
		plot.getAxis(Axis.Y).setRange(0.0, 1.0);
		plot.<AxisRenderer>getSetting(XYPlot.AXIS_X_RENDERER).setSetting(AxisRenderer.TICKS_SPACING, BUFFER_SIZE*INTERVAL/10.0);
		plot.<AxisRenderer>getSetting(XYPlot.AXIS_Y_RENDERER).setSetting(AxisRenderer.TICKS_SPACING, 0.1);
		plot.<AxisRenderer>getSetting(XYPlot.AXIS_X_RENDERER).setSetting(AxisRenderer.TICK_LABELS_FORMAT, DateFormat.getTimeInstance());
		plot.<AxisRenderer>getSetting(XYPlot.AXIS_Y_RENDERER).setSetting(AxisRenderer.TICK_LABELS_FORMAT, NumberFormat.getPercentInstance());

		plot.setPointRenderer(data, null);
		LineRenderer2D line = new DefaultLineRenderer2D();
		line.setSetting(LineRenderer2D.COLOR, new Color(0.9f, 0.3f, 0.2f));
		plot.setLineRenderer(data, line);

		plot.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		InteractivePanel plotPanel = new InteractivePanel(plot);
		getContentPane().add(plotPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(900, 300);

		Timer updateTimer = new Timer();
		UpdateTask updateTask = new UpdateTask(data, plot, plotPanel);
		updateTimer.scheduleAtFixedRate(updateTask, INTERVAL, INTERVAL);
	}

	public static void main(String[] args) {
		MemoryUsage test = new MemoryUsage();
		test.setVisible(true);
	}
}
