/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.examples.javafx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.javafx.InteractiveCanvas;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;

/**
 * <p>An xy-plot in a JavaFX window. Dragging pans the view, scrolling and a
 * double click zoom it.</p>
 *
 * <p>The plot is built exactly as it would be for a Swing window, for a PNG or
 * for a PDF; only the last two lines differ, where it is handed to an
 * {@link InteractiveCanvas} instead of an {@code InteractivePanel}.</p>
 */
public class ScatterPlotExample extends Application {
	/** Instance to generate random data values. */
	private static final Random random = new Random();

	@Override
	@SuppressWarnings("unchecked")
	public void start(Stage stage) {
		// Generate data
		var data = new DataTable(Double.class, Double.class);
		for (double x = -5.0; x <= 5.0; x += 0.05) {
			data.add(x, Math.sin(x) + 0.1*random.nextGaussian());
		}

		// Create data series
		var series = new DataSeries("sin(x) + noise", data, 0, 1);

		// Create and format the plot
		var plot = new XYPlot(series);
		plot.setInsets(new Insets2D.Double(20.0, 60.0, 60.0, 40.0));
		plot.setBackground(Color.WHITE);
		plot.getTitle().setText("Drag to pan, scroll or double click to zoom");
		plot.setLegendVisible(true);

		var pointRenderer = new DefaultPointRenderer2D();
		pointRenderer.setColor(new Color(55, 170, 200));
		plot.setPointRenderers(series, pointRenderer);

		var lineRenderer = new DefaultLineRenderer2D();
		lineRenderer.setColor(new Color(55, 170, 200, 128));
		lineRenderer.setStroke(new BasicStroke(2f));
		plot.setLineRenderers(series, lineRenderer);

		// Display the plot in a JavaFX window
		var root = new StackPane(new InteractiveCanvas(plot));
		stage.setTitle("GRAL JavaFX example");
		stage.setScene(new Scene(root, 800.0, 600.0));
		stage.show();
	}
}
