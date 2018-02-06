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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;

import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;


public class LabelExample extends ExamplePanel {

	public LabelExample() {
		Label label = new Label("TestLabel");
		label.setFont(getFont().deriveFont(20f));
		label.setBackground(new GradientPaint(
			new Point2D.Double(0.0, 0.0), Color.BLACK,
			new Point2D.Double(1.0, 1.0), Color.WHITE
		));

		DrawablePanel panel = new InteractivePanel(label);
		add(panel);
	}

	@Override
	public String getTitle() {
		return "Label example";
	}

	@Override
	public String getDescription() {
		return "Label with colored background";
	}

	public static void main(String[] args) {
		new LabelExample().showInFrame();
	}
}
