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
package de.erichseifert.gral.examples.uml;

import javax.swing.JFrame;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassRenderer;
import metamodel.classes.kernel.Package;
import metamodel.examples.WindowClass;

public class ClassExample extends JFrame {

	public ClassExample() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		// Example taken from UML 2.4.1 superstructure
		Package defaultPackage = new Package("Default");
		WindowClass windowClass = new WindowClass(defaultPackage);

		ClassRenderer classRenderer = new ClassRenderer();
		Drawable classDrawable = classRenderer.getRendererComponent(windowClass);
		getContentPane().add(new DrawablePanel(classDrawable));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClassExample().setVisible(true);
	}
}
