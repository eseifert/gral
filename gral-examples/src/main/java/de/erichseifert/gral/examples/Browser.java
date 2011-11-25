/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.erichseifert.gral.examples.barplot.HistogramPlot;
import de.erichseifert.gral.examples.barplot.SimpleBarPlot;
import de.erichseifert.gral.examples.boxplot.SimpleBoxPlot;
import de.erichseifert.gral.examples.pieplot.SimplePiePlot;
import de.erichseifert.gral.examples.rasterplot.SimpleRasterPlot;
import de.erichseifert.gral.examples.xyplot.AreaPlot;
import de.erichseifert.gral.examples.xyplot.ConvolutionExample;
import de.erichseifert.gral.examples.xyplot.MemoryUsage;
import de.erichseifert.gral.examples.xyplot.ScatterPlot;
import de.erichseifert.gral.examples.xyplot.SimpleXYPlot;
import de.erichseifert.gral.examples.xyplot.SpiralPlot;
import de.erichseifert.gral.examples.xyplot.StackedPlots;

public class Browser extends JFrame implements ListSelectionListener {
	private static final ExamplePanel[] examples = {
		new HistogramPlot(),
		new SimpleBarPlot(),
		new SimpleBoxPlot(),
		new SimplePiePlot(),
		new SimpleRasterPlot(),
		new AreaPlot(),
		new ConvolutionExample(),
		new MemoryUsage(),
		new ScatterPlot(),
		new SimpleXYPlot(),
		new SpiralPlot(),
		new StackedPlots()
	};
	private final JList examplesList;
	private final JScrollPane exampleScrollPane;

	public Browser() {
		super("GRAL Examples");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 800);
		setLocationRelativeTo(null);

		examplesList = new JList(examples);
		examplesList.addListSelectionListener(this);
		exampleScrollPane = new JScrollPane();
		setExample(examples[0]);

		JSplitPane listExamplesSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		listExamplesSplitter.setLeftComponent(examplesList);
		listExamplesSplitter.setRightComponent(exampleScrollPane);
		listExamplesSplitter.setOneTouchExpandable(true);
		listExamplesSplitter.setContinuousLayout(true);
		getContentPane().add(listExamplesSplitter);
	}

	private void setExample(ExamplePanel example) {
		exampleScrollPane.getViewport().setView(example);
	}

	public static void main(String[] args) {
		JFrame frame = new Browser();
		frame.setVisible(true);
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		Object source = e.getSource();
		if (source == examplesList) {
			setExample((ExamplePanel) examplesList.getSelectedValue());
		}
	}
}
