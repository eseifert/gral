/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2014 Erich Seifert <dev[at]erichseifert.de>,
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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import de.erichseifert.gral.graphics.EditableLabel;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;


public class EditableLabelExample extends ExamplePanel {

	public EditableLabelExample() {
		EditableLabel label = new EditableLabel("Editable label");
		label.setFont(getFont().deriveFont(20f));
		label.setEdited(true);

		final DrawablePanel panel = new InteractivePanel(label);
		panel.setFocusable(true);
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				panel.repaint();
			}
		});
		panel.addKeyListener(label);
		add(panel);
	}

	@Override
	public String getTitle() {
		return "Editable label example";
	}

	@Override
	public String getDescription() {
		return "Label with editable content.";
	}

	public static void main(String[] args) {
		new EditableLabelExample().showInFrame();
	}
}
