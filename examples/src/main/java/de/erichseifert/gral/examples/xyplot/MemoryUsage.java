/*
 * GRAL: GRAphing Library for Java(R)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.filters.Convolution;
import de.erichseifert.gral.data.filters.Filter;
import de.erichseifert.gral.data.filters.Kernel;
import de.erichseifert.gral.data.filters.KernelUtils;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

final class UpdateTask implements ActionListener {
	private final DataTable data;
	private final Plot plot;
	private final JComponent component;

	public UpdateTask(DataTable data, XYPlot plot, JComponent comp) {
		this.data = data;
		this.plot = plot;
		this.component = comp;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!component.isVisible()) {
			return;
		}
		long time = System.currentTimeMillis();
		long memTotal = Runtime.getRuntime().totalMemory();
		long memFree = Runtime.getRuntime().freeMemory();
		double memUsedPercentage = 1.0 - memFree/(double)memTotal;

		data.add(time, memUsedPercentage);
		data.remove(0);

		Column col1 = data.getColumn(0);
		plot.getAxis(XYPlot.AXIS_X).setRange(
				col1.getStatistics(Statistics.MIN),
				col1.getStatistics(Statistics.MAX));
		Column col2 = data.getColumn(1);
		plot.getAxis(XYPlot.AXIS_Y).setRange(
				0.0,
				col2.getStatistics(Statistics.MAX));

		component.repaint();
	}
}

public class MemoryUsage extends JFrame {
	private static final int BUFFER_SIZE = 400;
	private static final int INTERVAL = 80;

	public MemoryUsage() {
		super("GRALTest");
		getContentPane().setBackground(new Color(1.0f, 1.0f, 1.0f));

		DataTable data = new DataTable(Long.class, Double.class);
		long time = System.currentTimeMillis();
		for (int i=BUFFER_SIZE - 1; i>=0; i--) {
			data.add(time - i*INTERVAL, Double.NaN);
		}

		Kernel kernel1 = KernelUtils.getUniform(BUFFER_SIZE/8, BUFFER_SIZE/8/2, 1.0).normalize();
		Filter average = new Convolution(data, kernel1, Filter.Mode.OMIT, 1);
		Kernel kernel2 = KernelUtils.getBinomial(BUFFER_SIZE/8/2).normalize();
		Filter smoothed = new Convolution(average, kernel2, Filter.Mode.REPEAT, 1);

		XYPlot plot = new XYPlot(data, smoothed);
		plot.getAxis(XYPlot.AXIS_Y).setRange(0.0, 1.0);
		AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
		AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererX.setSetting(AxisRenderer.TICKS_SPACING, BUFFER_SIZE*INTERVAL/10.0);
		axisRendererY.setSetting(AxisRenderer.TICKS_SPACING, 0.1);
		axisRendererX.setSetting(AxisRenderer.TICK_LABELS_FORMAT, DateFormat.getTimeInstance());
		axisRendererY.setSetting(AxisRenderer.TICK_LABELS_FORMAT, NumberFormat.getPercentInstance());

		plot.setPointRenderer(data, null);
		LineRenderer line1 = new DefaultLineRenderer2D();
		line1.setSetting(LineRenderer.COLOR, new Color(1.0f, 0.0f, 0.0f, 0.3f));
		plot.setLineRenderer(data, line1);

		plot.setPointRenderer(smoothed, null);
		LineRenderer line2 = new DefaultLineRenderer2D();
		line2.setSetting(LineRenderer.COLOR, new Color(0.0f, 0.3f, 1.0f));
		plot.setLineRenderer(smoothed, line2);

		plot.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		InteractivePanel plotPanel = new InteractivePanel(plot);
		getContentPane().add(plotPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(900, 300);

		UpdateTask updateTask = new UpdateTask(data, plot, plotPanel);
		Timer updateTimer = new Timer(INTERVAL, updateTask);
		updateTimer.setCoalesce(false);
		updateTimer.start();
	}

	public static void main(String[] args) {
		MemoryUsage test = new MemoryUsage();
		test.setVisible(true);
	}
}
