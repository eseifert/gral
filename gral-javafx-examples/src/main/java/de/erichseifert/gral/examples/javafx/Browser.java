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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import de.erichseifert.gral.examples.Example;
import de.erichseifert.gral.examples.Examples;

/**
 * <p>A window that lists every example on the left and shows the selected one
 * on the right, the same way the Swing browser of {@code gral-examples} does.
 * It is started by {@link Launcher}, which is what
 * {@code ./gradlew :gral-javafx-examples:run} runs.</p>
 *
 * <p>The examples are the very ones the Swing browser shows: they come from
 * {@link Examples} and know nothing about either toolkit. Only the two classes
 * in this package are JavaFX, and only {@link ExampleView} is more than a
 * list.</p>
 *
 * <p>The examples are all constructed up front, which means starting the
 * browser also serves as a rough check that none of them is broken.</p>
 */
public class Browser extends Application {
	/** Width of the list of examples. */
	private static final double LIST_WIDTH = 220.0;

	/** View of each example, in the order they are listed in. */
	private final Map<Example, ExampleView> views = new LinkedHashMap<>();

	/** View that is on display. */
	private ExampleView current;

	@Override
	public void start(Stage stage) {
		List<Example> examples = Examples.createAll();
		for (Example example : examples) {
			views.put(example, new ExampleView(example));
		}
		Example selected = Examples.find(examples, getSelectedName());

		var examplesList = new ListView<>(FXCollections.observableArrayList(examples));
		examplesList.setCellFactory(view -> new ExampleCell());
		examplesList.setPrefWidth(LIST_WIDTH);

		var splitter = new SplitPane(examplesList, views.get(selected));
		splitter.setDividerPositions(LIST_WIDTH/(LIST_WIDTH + selected.getPreferredSize().getWidth()));
		SplitPane.setResizableWithParent(examplesList, Boolean.FALSE);

		examplesList.getSelectionModel().selectedItemProperty().addListener(
			(observable, oldExample, newExample) -> setExample(splitter, newExample));
		examplesList.getSelectionModel().select(selected);
		setExample(splitter, selected);

		stage.setTitle("GRAL examples");
		stage.setScene(new Scene(splitter,
			LIST_WIDTH + selected.getPreferredSize().getWidth(),
			selected.getPreferredSize().getHeight()));
		stage.show();
	}

	/**
	 * Returns the name of the example that was named on the command line.
	 * @return The name, or {@code null} when none was given or the browser was
	 *         not started through {@link Launcher}.
	 */
	private String getSelectedName() {
		Parameters parameters = getParameters();
		if ((parameters == null) || parameters.getRaw().isEmpty()) {
			return null;
		}
		return parameters.getRaw().get(0);
	}

	@Override
	public void stop() {
		if (current != null) {
			current.setRunning(false);
		}
	}

	/**
	 * Shows the specified example and leaves the one before it alone. Only the
	 * example on display is refreshed, so a browser that has been left on a
	 * static plot does no work at all.
	 * @param splitter Pane that holds the view.
	 * @param example Example to show.
	 */
	private void setExample(SplitPane splitter, Example example) {
		ExampleView view = views.get(example);
		if ((view == null) || (view == current)) {
			return;
		}
		if (current != null) {
			current.setRunning(false);
		}
		current = view;
		splitter.getItems().set(1, view);
		view.setRunning(true);
	}

	/**
	 * An entry of the list, showing the title of an example and its
	 * description as a tooltip.
	 */
	private static class ExampleCell extends ListCell<Example> {
		@Override
		protected void updateItem(Example example, boolean empty) {
			super.updateItem(example, empty);
			if (empty || (example == null)) {
				setText(null);
				setTooltip(null);
			} else {
				setText(example.getTitle());
				setTooltip(new Tooltip(example.getDescription()));
			}
		}
	}
}
