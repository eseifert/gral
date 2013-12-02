/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.Timer;

import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.XYPlot.XYPlotArea2D;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;

final class UpdateTask implements ActionListener {
	private final DataTable data;
	private final Plot plot;
	private final JComponent component;
	private Method getTotalPhysicalMemorySize;
	private Method getFreePhysicalMemorySize;

	public UpdateTask(DataTable data, XYPlot plot, JComponent comp) {
		this.data = data;
		this.plot = plot;
		this.component = comp;

		// Check for VM specific methods getTotalPhysicalMemorySize() and
		// getFreePhysicalMemorySize()
		OperatingSystemMXBean osBean =
			ManagementFactory.getOperatingSystemMXBean();
		try {
			getTotalPhysicalMemorySize = osBean.getClass()
				.getMethod("getTotalPhysicalMemorySize");
			getTotalPhysicalMemorySize.setAccessible(true);
			getFreePhysicalMemorySize = osBean.getClass()
				.getMethod("getFreePhysicalMemorySize");
			getFreePhysicalMemorySize.setAccessible(true);
		} catch (SecurityException ex) {
		} catch (NoSuchMethodException ex) {
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (!component.isVisible()) {
			return;
		}
		double time = System.currentTimeMillis();

		// Physical system memory
		long memSysTotal = 0L;
		long memSysFree = 0L;
		long memSysUsed = 0L;

		// We can only display system memory if there are the corresponding
		// methods
		if ((getTotalPhysicalMemorySize != null) &&
				(getFreePhysicalMemorySize != null)) {
			OperatingSystemMXBean osBean =
				ManagementFactory.getOperatingSystemMXBean();
			try {
				memSysTotal = (Long) getTotalPhysicalMemorySize.invoke(osBean);
				memSysFree = (Long) getFreePhysicalMemorySize.invoke(osBean);
				memSysUsed = memSysTotal - memSysFree;
			} catch (IllegalArgumentException ex) {
			} catch (IllegalAccessException ex) {
			} catch (InvocationTargetException ex) {
			}
		}

		// JVM memory
		long memVmTotal = Runtime.getRuntime().totalMemory();
		long memVmFree = Runtime.getRuntime().freeMemory();
		long memVmUsed = memVmTotal - memVmFree;

		data.add(time, memSysUsed/1024L/1024L, memVmTotal/1024L/1024L, memVmUsed/1024L/1024L);
		data.remove(0);

		Column col1 = data.getColumn(0);
		plot.getAxis(XYPlot.AXIS_X).setRange(
			col1.getStatistics(Statistics.MIN),
			col1.getStatistics(Statistics.MAX)
		);

		Column col3 = data.getColumn(2);
		plot.getAxis(XYPlot.AXIS_Y).setRange(
			0, Math.max(
				memSysTotal/1024L/1024L,
				col3.getStatistics(Statistics.MAX)
			)
		);

		component.repaint();
	}
}

public class MemoryUsage extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5914124874301980251L;

	/** Size of the data buffer in no. of element. */
	private static final int BUFFER_SIZE = 400;
	/** Update interval in milliseconds */
	private static final int INTERVAL = 100;

	@SuppressWarnings("unchecked")
	public MemoryUsage() {
		DataTable data = new DataTable(Double.class, Long.class, Long.class, Long.class);
		double time = System.currentTimeMillis();
		for (int i=BUFFER_SIZE - 1; i>=0; i--) {
			data.add(time - i*INTERVAL, null, null, null);
		}

		// Use columns 0 and 1 for physical system memory
		DataSource memSysUsage = new DataSeries("Used by system", data, 0, 1);
		// Use columns 0 and 2 for JVM memory
		DataSource memVm = new DataSeries("Allocated by Java VM", data, 0, 2);
		// Use columns 0 and 2 for JVM memory usage
		DataSource memVmUsage = new DataSeries("Used by Java VM", data, 0, 3);

		// Create new xy-plot
		XYPlot plot = new XYPlot(memSysUsage, memVm, memVmUsage);

		// Format  plot
		plot.setInsets(new Insets2D.Double(20.0, 90.0, 40.0, 20.0));
		plot.getTitle().setText("Memory Usage");
		plot.setLegendVisible(true);

		// Format legend
		plot.getLegend().setOrientation(Orientation.HORIZONTAL);

		// Format plot area
		((XYPlotArea2D) plot.getPlotArea()).setMajorGridX(false);
		((XYPlotArea2D) plot.getPlotArea()).setMinorGridY(true);

		// Format axes (set scale and spacings)
		plot.getAxis(XYPlot.AXIS_Y).setRange(0.0, 1.0);
		AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
		axisRendererX.setTickSpacing(BUFFER_SIZE*INTERVAL/10.0);
		axisRendererX.setTickLabelFormat(DateFormat.getTimeInstance());
		AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererY.setMinorTicksCount(4);
		axisRendererY.setTickLabelFormat(new DecimalFormat("0 MiB"));

		Color color1Dark = GraphicsUtils.deriveDarker(COLOR1);

		// Format first data series
		plot.setPointRenderer(memSysUsage, null);
		AreaRenderer area1 = new DefaultAreaRenderer2D();
		area1.setColor(new LinearGradientPaint(
			0f, 0f, 0f, 1f,
			new float[] {0f, 1f},
			new Color[] {
				GraphicsUtils.deriveWithAlpha(COLOR1, 128),
				GraphicsUtils.deriveWithAlpha(COLOR1, 24)
			}
		));
		plot.setAreaRenderer(memSysUsage, area1);

		// Format second data series
		plot.setPointRenderer(memVm, null);
		LineRenderer line2 = new DefaultLineRenderer2D();
		line2.setColor(GraphicsUtils.deriveWithAlpha(color1Dark, 128));
		plot.setLineRenderer(memVm, line2);

		// Format third data series
		plot.setPointRenderer(memVmUsage, null);
		AreaRenderer area3 = new DefaultAreaRenderer2D();
		area3.setColor(new LinearGradientPaint(
				0f, 0f, 0f, 1f,
				new float[] {0f, 1f},
				new Color[] {
					GraphicsUtils.deriveWithAlpha(COLOR2, 128),
					GraphicsUtils.deriveWithAlpha(COLOR2, 24)
				}
		));
		plot.setAreaRenderer(memVmUsage, area3);

		// Add plot to frame
		InteractivePanel plotPanel = new InteractivePanel(plot);
		plotPanel.setPannable(false);
		plotPanel.setZoomable(false);
		add(plotPanel, BorderLayout.CENTER);

		// Start watching memory
		UpdateTask updateTask = new UpdateTask(data, plot, plotPanel);
		Timer updateTimer = new Timer(INTERVAL, updateTask);
		updateTimer.setCoalesce(false);
		updateTimer.start();
	}

	@Override
	public String getTitle() {
		return "Memory usage";
	}

	@Override
	public String getDescription() {
		return "Area plot of the system's current memory usage. This example " +
			"works best with Oracle VM, but it can show VM memory usage on " +
			"other VMs too.";
	}

	public static void main(String[] args) {
		new MemoryUsage().showInFrame();
	}
}
