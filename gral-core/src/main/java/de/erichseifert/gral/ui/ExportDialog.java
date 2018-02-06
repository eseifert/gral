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
package de.erichseifert.gral.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.util.Messages;

/**
 * A dialog implementation for exporting plots. It allows the user to
 * specify the document dimensions.
 */
public class ExportDialog extends JDialog {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1344719157074981540L;

	/** Type of user feedback. */
	public enum UserAction {
		/** User confirmed dialog. */
		APPROVE,
		/** User canceled or closed dialog. */
		CANCEL
	}

	/** Bounding rectangle for document. */
	private final Rectangle2D documentBounds;
	/** Action that was used to close this dialog. */
	private UserAction userAction;

	/** Input component for horizontal document offset. */
	private final JFormattedTextField inputX;
	/** Input component for vertical document offset. */
	private final JFormattedTextField inputY;
	/** Input component for document width. */
	private final JFormattedTextField inputW;
	/** Input component for document height. */
	private final JFormattedTextField inputH;

	/**
	 * Creates a new instance and initializes it with a parent and a
	 * drawable component.
	 * @param parent Parent component.
	 * @param drawable Drawable component.
	 */
	public ExportDialog(Component parent, Drawable drawable) {
		super(JOptionPane.getFrameForComponent(parent), true);
		setTitle(Messages.getString("ExportDialog.exportOptionsTitle")); //$NON-NLS-1$

		documentBounds = new Rectangle2D.Double();
		documentBounds.setFrame(drawable.getBounds());
		setUserAction(UserAction.CANCEL);

		JPanel cp = new JPanel(new BorderLayout());
		cp.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(cp);

		DecimalFormat formatMm = new DecimalFormat();
		formatMm.setMinimumFractionDigits(2);

		JPanel options = new JPanel(new GridLayout(4, 2, 10, 2));
		getContentPane().add(options, BorderLayout.NORTH);

		PropertyChangeListener docBoundsListener =
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					setDocumentBounds(
						((Number) inputX.getValue()).doubleValue(),
						((Number) inputY.getValue()).doubleValue(),
						((Number) inputW.getValue()).doubleValue(),
						((Number) inputH.getValue()).doubleValue());
				}
			};
		inputX = new JFormattedTextField(formatMm);
		addInputField(inputX, Messages.getString("ExportDialog.left"), //$NON-NLS-1$
				options, documentBounds.getX(), docBoundsListener);
		inputY = new JFormattedTextField(formatMm);
		addInputField(inputY, Messages.getString("ExportDialog.top"), //$NON-NLS-1$
				options, documentBounds.getY(), docBoundsListener);
		inputW = new JFormattedTextField(formatMm);
		addInputField(inputW, Messages.getString("ExportDialog.width"), //$NON-NLS-1$
				options, documentBounds.getWidth(), docBoundsListener);
		inputH = new JFormattedTextField(formatMm);
		addInputField(inputH, Messages.getString("ExportDialog.height"), //$NON-NLS-1$
				options, documentBounds.getHeight(), docBoundsListener);

		JPanel controls = new JPanel(new FlowLayout());
		cp.add(controls, BorderLayout.SOUTH);

		JButton buttonConfirm = new JButton(
				Messages.getString("ExportDialog.confirm")); //$NON-NLS-1$
		buttonConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUserAction(UserAction.APPROVE);
				dispose();
			}
		});
		controls.add(buttonConfirm);

		JButton buttonCancel = new JButton(
				Messages.getString("ExportDialog.abort")); //$NON-NLS-1$
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUserAction(UserAction.CANCEL);
				dispose();
			}
		});
		controls.add(buttonCancel);

		pack();
		setLocationRelativeTo(parent);
	}

	/**
	 * Utility method that adds a new label and a new input field to the
	 * dialog.
	 * @param input Input field.
	 * @param labelText Text for label.
	 * @param cont Container.
	 * @param initialValue Initial value for the input field.
	 * @param pcl Property change listener that should be associated with the
	 *        input field.
	 */
	private static void addInputField(JFormattedTextField input,
			String labelText, java.awt.Container cont, Object initialValue,
			PropertyChangeListener pcl) {
		JLabel label = new JLabel(labelText);
		label.setHorizontalAlignment(JLabel.RIGHT);
		cont.add(label);
		input.setValue(initialValue);
		input.setHorizontalAlignment(JFormattedTextField.RIGHT);
		input.addPropertyChangeListener("value", pcl); //$NON-NLS-1$
		cont.add(input);
		label.setLabelFor(input);
	}

	/**
	 * Returns the bounds entered by the user.
	 * @return Document bounds that should be used to export the plot
	 */
	public Rectangle2D getDocumentBounds() {
		Rectangle2D bounds = new Rectangle2D.Double();
		bounds.setFrame(documentBounds);
		return bounds;
	}

	/**
	 * Sets new bounds for the document.
	 * @param x Top-left corner
	 * @param y Bottom-right corner
	 * @param w Width.
	 * @param h Height.
	 */
	protected void setDocumentBounds(double x, double y, double w, double h)  {
		if ((documentBounds.getX() == x)
				&& (documentBounds.getY() == y)
				&& (documentBounds.getWidth() == w)
				&& (documentBounds.getHeight() == h)) {
			return;
		}
		documentBounds.setFrame(x, y, w, h);
		inputX.setValue(x);
		inputY.setValue(y);
		inputW.setValue(w);
		inputH.setValue(h);
	}

	/**
	 * Returns the last action by the user. The return value can be used to
	 * determine whether the user approved or canceled the dialog.
	 * @return Type of user action.
	 */
	public UserAction getUserAction() {
		return userAction;
	}

	/**
	 * Sets the type of action the user executed. The value can later be used
	 * to determine whether the user approved or canceled the dialog.
	 * @param userAction Type of user action.
	 */
	private void setUserAction(UserAction userAction) {
		this.userAction = userAction;
	}
}
