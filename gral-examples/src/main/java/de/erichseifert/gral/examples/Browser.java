/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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

import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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

public class Browser extends JFrame implements ListSelectionListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -3734045121668893200L;

	private static class ExamplesList extends JList {
		/** Version id for serialization. */
		private static final long serialVersionUID = -5904920699472899791L;

		public ExamplesList(ExamplePanel[] examples) {
			super(examples);
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			int index = locationToIndex(event.getPoint());
			ExamplePanel item = (ExamplePanel) getModel().getElementAt(index);
			return item.getDescription();
		}
	}

	private static final ExamplePanel[] examples = {
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
		new MultiplePointRenderers()
	};

	private final JList examplesList;
	private final JScrollPane exampleScrollPane;

	public Browser() {
		super("GRAL examples");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		examplesList = new ExamplesList(examples);
		examplesList.addListSelectionListener(this);
		exampleScrollPane = new JScrollPane();
		setExample(examples[0]);

		JSplitPane listExamplesSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		listExamplesSplitter.setLeftComponent(examplesList);
		listExamplesSplitter.setRightComponent(exampleScrollPane);
		listExamplesSplitter.setOneTouchExpandable(true);
		listExamplesSplitter.setContinuousLayout(true);
		getContentPane().add(listExamplesSplitter);

		pack();
		setLocationRelativeTo(null);
	}

	private void setExample(ExamplePanel example) {
		if (example == exampleScrollPane.getViewport().getView()) {
			return;
		}
		exampleScrollPane.getViewport().setView(example);
		examplesList.setSelectedValue(example, true);
	}

	public void valueChanged(ListSelectionEvent e) {
		Object source = e.getSource();
		if (source == examplesList) {
			setExample((ExamplePanel) examplesList.getSelectedValue());
		}
	}

	public static void main(String[] args) {
		JFrame frame = new Browser();
		frame.setVisible(true);
	}
}
