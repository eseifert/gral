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
package de.erichseifert.gral.javafx;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Optional;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

import de.erichseifert.gral.graphics.Drawable;

/**
 * <p>A dialog that asks for the bounds an export should be written at,
 * initialized with the bounds the drawable currently has. It is the JavaFX
 * counterpart of {@code de.erichseifert.gral.ui.ExportDialog} and is used by
 * the export action of {@link InteractiveCanvas}.</p>
 *
 * <p>The size of the export is independent of the size on screen, which is the
 * point of asking: a figure for print is written at its final size while the
 * view stays as large as the window.</p>
 */
class ExportDialog extends Dialog<Rectangle2D> {
	/** Number of columns in the input fields. */
	private static final int COLUMNS = 10;

	/**
	 * Creates a dialog for exporting the specified drawable.
	 * @param owner Window the dialog belongs to, or {@code null}.
	 * @param drawable Drawable that will be exported.
	 */
	ExportDialog(Window owner, Drawable drawable) {
		initOwner(owner);
		setTitle(FxMessages.getString("ExportDialog.exportOptionsTitle")); //$NON-NLS-1$

		Rectangle2D bounds = drawable.getBounds();
		TextField inputX = createInputField(bounds.getX());
		TextField inputY = createInputField(bounds.getY());
		TextField inputWidth = createInputField(bounds.getWidth());
		TextField inputHeight = createInputField(bounds.getHeight());

		var fields = new GridPane();
		fields.setHgap(8.0);
		fields.setVgap(8.0);
		addField(fields, 0, FxMessages.getString("ExportDialog.left"), inputX); //$NON-NLS-1$
		addField(fields, 1, FxMessages.getString("ExportDialog.top"), inputY); //$NON-NLS-1$
		addField(fields, 2, FxMessages.getString("ExportDialog.width"), inputWidth); //$NON-NLS-1$
		addField(fields, 3, FxMessages.getString("ExportDialog.height"), inputHeight); //$NON-NLS-1$
		getDialogPane().setContent(fields);

		/*
		 * The dialog pane is given the roles rather than the texts, so that the
		 * buttons end up in the order of the platform; their labels are then
		 * replaced by the translated ones.
		 */
		var confirm = new ButtonType(FxMessages.getString("ExportDialog.confirm"), //$NON-NLS-1$
				ButtonBar.ButtonData.OK_DONE);
		var abort = new ButtonType(FxMessages.getString("ExportDialog.abort"), //$NON-NLS-1$
				ButtonBar.ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(confirm, abort);

		setResultConverter(button -> {
			if (button != confirm) {
				return null;
			}
			return new Rectangle2D.Double(
				getValue(inputX), getValue(inputY),
				getValue(inputWidth), getValue(inputHeight));
		});
	}

	/**
	 * Shows the dialog and returns the bounds the user confirmed.
	 * @return The document bounds, or an empty result when the export was
	 *         cancelled.
	 */
	Optional<Rectangle2D> getDocumentBounds() {
		return showAndWait();
	}

	/**
	 * Creates a field that accepts a decimal number.
	 * @param value Value to start with.
	 * @return An input field.
	 */
	private static TextField createInputField(double value) {
		var field = new TextField();
		field.setPrefColumnCount(COLUMNS);
		field.setTextFormatter(new TextFormatter<>(
				new NumberStringConverter(new DecimalFormat()), value));
		return field;
	}

	/**
	 * Adds a labelled input field to the specified grid.
	 * @param fields Grid to add to.
	 * @param row Row of the grid.
	 * @param label Text describing the field.
	 * @param field Field to add.
	 */
	private static void addField(GridPane fields, int row, String label, TextField field) {
		fields.add(new Label(label), 0, row);
		fields.add(field, 1, row);
	}

	/**
	 * Returns the number that was entered in the specified field.
	 * @param field Field to read.
	 * @return The value of the field, or zero when it is empty.
	 */
	private static double getValue(TextField field) {
		Object value = field.getTextFormatter().getValue();
		return (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
	}
}
