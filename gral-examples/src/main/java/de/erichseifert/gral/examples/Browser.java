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

import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * <p>A window that lists every example on the left and shows the selected one
 * on the right. This is the {@code Main-Class} of the examples JAR, so it is
 * what {@code ./gradlew :gral-examples:run} starts.</p>
 *
 * <p>The examples themselves are in {@link Examples} and know nothing about
 * Swing, so the JavaFX browser of {@code gral-javafx-examples} shows the same
 * ones. Naming an example on the command line opens the browser on it, for
 * example {@code ScatterPlot}.</p>
 *
 * <p>The examples are all constructed up front, which means starting the
 * browser also serves as a rough check that none of them is broken.</p>
 */
public class Browser extends JFrame implements ListSelectionListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -3734045121668893200L;

	private static class ExamplesList extends JList<Example> {
		/** Version id for serialization. */
		private static final long serialVersionUID = -5904920699472899791L;

		public ExamplesList(List<Example> examples) {
			super(examples.toArray(new Example[0]));
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			int index = locationToIndex(event.getPoint());
			return getModel().getElementAt(index).getDescription();
		}
	}

	private final transient Map<Example, ExamplePanel> panels;
	private final ExamplesList examplesList;
	private final JScrollPane exampleScrollPane;

	/**
	 * Creates the browser window and instantiates every example.
	 * @param selected Example to show first.
	 */
	public Browser(Example selected) {
		super("GRAL examples");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		List<Example> examples = Examples.createAll();
		panels = new LinkedHashMap<>();
		for (Example example : examples) {
			panels.put(example, new ExamplePanel(example));
		}

		examplesList = new ExamplesList(examples);
		examplesList.addListSelectionListener(this);
		exampleScrollPane = new JScrollPane();
		setExample(selected);

		var listExamplesSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		listExamplesSplitter.setLeftComponent(examplesList);
		listExamplesSplitter.setRightComponent(exampleScrollPane);
		listExamplesSplitter.setOneTouchExpandable(true);
		listExamplesSplitter.setContinuousLayout(true);
		getContentPane().add(listExamplesSplitter);

		pack();
		setLocationRelativeTo(null);
	}

	private void setExample(Example example) {
		ExamplePanel panel = panels.get(example);
		if (panel == exampleScrollPane.getViewport().getView()) {
			return;
		}
		exampleScrollPane.getViewport().setView(panel);
		examplesList.setSelectedValue(example, true);
	}

	/**
	 * Reacts to a selection in the list by showing the corresponding example.
	 * @param e Event describing the selection.
	 */
	public void valueChanged(ListSelectionEvent e) {
		Object source = e.getSource();
		if (source == examplesList) {
			setExample(examplesList.getSelectedValue());
		}
	}

	/**
	 * Opens the example browser.
	 * @param args Command line arguments. The first one, if given, is the
	 *        simple class name of the example to open, like
	 *        {@code ScatterPlot}.
	 */
	public static void main(String[] args) {
		String name = (args.length > 0) ? args[0] : null;
		var frame = new Browser(Examples.find(Examples.createAll(), name));
		frame.setVisible(true);
	}
}
