/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.examples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;

import javax.swing.JFrame;

import openjchart.DrawablePanel;
import openjchart.PlotArea2D;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.plots.XYPlot;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.axes.LogarithmicRenderer2D;
import openjchart.plots.lines.DiscreteLineRenderer2D;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.plots.lines.SplineLineRenderer2D;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.plots.shapes.SizeableShapeRenderer;
import openjchart.util.Insets2D;

public class SimpleXYPlot extends JFrame {

	public SimpleXYPlot() {
		super("OpenJChartTest");
		getContentPane().setBackground(new Color(1.0f, 0.99f, 0.95f));
		
		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		data.add(-1.5,  1.0,  Math.log( 1.0),  0.30);
		data.add( 0.0,  2.0,  Math.log( 2.0),  0.81);
		data.add( 1.5,  3.0,  Math.log( 3.0),  3.50);
		data.add( 4.0,  4.0,  Math.log( 4.0),  0.50);
		data.add( 5.0,  5.0,  Math.log( 5.0),  1.80);
		data.add( 6.0,  6.0,  Math.log( 6.0),  1.02);
		data.add( 7.0,  7.0,  Math.log( 7.0),  0.38);
		data.add( 8.0,  8.0,  Math.log( 8.0),  2.55);
		data.add( 9.0,  9.0,  Math.log( 9.0),  1.50);
		data.add(10.0, 10.0,  Math.log(10.0),  2.00);

		DataSeries seriesLog = new DataSeries(data, 1, 2);
		DataSeries seriesLin = new DataSeries(data, 1, 0, 3);

		XYPlot plot = new XYPlot(seriesLog, seriesLin);
		// Custom plot area formatting
		plot.getPlotArea().setSetting(PlotArea2D.KEY_BACKGROUND,
				new LinearGradientPaint(0f,0f, 1f,0f, new float[] {0.00f, 0.05f}, new Color[] {new Color(0.15f,0.05f,0.00f,0.15f), new Color(0.15f,0.05f,0.00f,0.00f)}));
		plot.getPlotArea().setSetting(PlotArea2D.KEY_BORDER, null);
		// Setting the title
		plot.setSetting(XYPlot.KEY_TITLE, "A Sample XY Plot");
		// Custom title alignment
		//plot.getTitle().setSetting(Label.KEY_ALIGNMENT, 0.3);
		// Custom shape renderer
		ShapeRenderer sizeableShapeRenderer = new SizeableShapeRenderer();
		plot.setShapeRenderer(seriesLin, sizeableShapeRenderer);
		// Custom shape bounds
		//plot.getShapeRenderer().setBounds(new Rectangle2D.Double(-10.0, -5.0, 20.0, 5.0));
		// Custom shape coloring
		//plot.getShapeRenderer().setColor(Color.RED);
		// Custom grid color
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.KEY_GRID_COLOR, Color.BLUE);
		// Grid disabled
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.KEY_GRID_X, false);
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.KEY_GRID_Y, false);
		// Custom line renderer
		LineRenderer2D discreteRenderer = new SplineLineRenderer2D();
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_COLOR, new Color(0.5f, 0.2f, 0.0f, 0.7f));
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_STROKE, new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {3f, 6f}, 0.0f));
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom gaps for shapes
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_GAP, 2.0);
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_GAP_ROUNDED, true);
		// Custom ascending point
		discreteRenderer.setSetting(DiscreteLineRenderer2D.KEY_ASCENDING_POINT, 0.5);
		// Custom axis renderers
		AxisRenderer2D logRendererX = new LogarithmicRenderer2D();
		logRendererX.setSetting(AxisRenderer2D.KEY_LABEL, "Logarithmic axis");
		plot.setSetting(XYPlot.KEY_AXIS_X_RENDERER, logRendererX);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(2f);
		logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_STROKE, stroke);
		((AxisRenderer2D) plot.getSetting(XYPlot.KEY_AXIS_Y_RENDERER)).setSetting(AxisRenderer2D.KEY_LABEL, "Linear axis");
		// Custom stroke for the ticks
		//logRendererX.setSetting(AxisRenderer2D.KEY_TICK_STROKE, stroke);
		// Swap axis direction
		//logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_DIRECTION_SWAPPED, true);
		//plot.setAxisYRenderer(new LogarithmicRenderer2D());
		plot.<AxisRenderer2D>getSetting(XYPlot.KEY_AXIS_X_RENDERER).setSetting(AxisRenderer2D.KEY_TICK_SPACING, 0.5);
		plot.setInsets(new Insets2D.Double(20.0, 80.0, 40.0, 40.0));
		getContentPane().add(new DrawablePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleXYPlot test = new SimpleXYPlot();
		test.setVisible(true);
	}
}
