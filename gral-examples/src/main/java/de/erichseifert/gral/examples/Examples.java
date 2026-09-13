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
package de.erichseifert.gral.examples;

import java.util.Arrays;
import java.util.List;

import de.erichseifert.gral.examples.barplot.HistogramPlot;
import de.erichseifert.gral.examples.barplot.SimpleBarPlot;
import de.erichseifert.gral.examples.boxplot.SimpleBoxPlot;
import de.erichseifert.gral.examples.pieplot.DynamicPiePlot;
import de.erichseifert.gral.examples.pieplot.SimplePiePlot;
import de.erichseifert.gral.examples.rasterplot.SimpleRasterPlot;
import de.erichseifert.gral.examples.xyplot.AreaPlot;
import de.erichseifert.gral.examples.xyplot.ConvolutionExample;
import de.erichseifert.gral.examples.xyplot.MemoryUsage;
import de.erichseifert.gral.examples.xyplot.MultiplePointRenderers;
import de.erichseifert.gral.examples.xyplot.ScatterPlot;
import de.erichseifert.gral.examples.xyplot.SimpleXYPlot;
import de.erichseifert.gral.examples.xyplot.SpiralPlot;
import de.erichseifert.gral.examples.xyplot.StackedPlots;

/**
 * <p>The list of examples that the browsers show. It is kept here rather than
 * in one of them, so that the Swing browser and the JavaFX browser cannot
 * drift apart.</p>
 *
 * <p>The class is not meant to be instantiated or extended.</p>
 */
public abstract class Examples {
	/**
	 * Creates one instance of every example, in the order they are listed in.
	 * Building them all is also a rough check that none of them is broken.
	 * @return The examples.
	 */
	public static List<Example> createAll() {
		return Arrays.asList(
			new HistogramPlot(),
			new SimpleBarPlot(),
			new SimpleBoxPlot(),
			new DynamicPiePlot(),
			new SimplePiePlot(),
			new SimpleRasterPlot(),
			new AreaPlot(),
			new ConvolutionExample(),
			new MemoryUsage(),
			new ScatterPlot(),
			new SimpleXYPlot(),
			new SpiralPlot(),
			new StackedPlots(),
			new MultiplePointRenderers(),
			new LabelExample()
		);
	}

	/**
	 * Returns the example whose class is named as specified, so that a browser
	 * can be started on one of them. The comparison ignores the case and the
	 * package.
	 * @param examples Examples to search.
	 * @param name Simple name of the example class, or {@code null}.
	 * @return The matching example, or the first one when there is no match.
	 */
	public static Example find(List<Example> examples, String name) {
		if (name != null) {
			for (Example example : examples) {
				if (example.getClass().getSimpleName().equalsIgnoreCase(name)) {
					return example;
				}
			}
		}
		return examples.get(0);
	}
}
