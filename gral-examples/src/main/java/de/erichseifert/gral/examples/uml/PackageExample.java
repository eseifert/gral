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
package de.erichseifert.gral.examples.uml;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import metamodel.classes.kernel.Package;
import metamodel.examples.TypesPackage;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassDiagram;
import de.erichseifert.gral.uml.PackageDrawable;

public class PackageExample extends JFrame {

	public PackageExample() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		Package types = new TypesPackage();

		DrawableContainer diagram = new ClassDiagram();
		PackageDrawable typesDrawableCompactView = new PackageDrawable(types);
		typesDrawableCompactView.setBounds(30, 30, 150, 100);
		diagram.add(typesDrawableCompactView);
		PackageDrawable typesDrawableContainerView = new PackageDrawable(types);
		typesDrawableContainerView.setBounds(300, 100, 150, 400);
		typesDrawableContainerView.getTab().setNameVisible(true);
		typesDrawableContainerView.setMembersDisplayed(true);
		diagram.add(typesDrawableContainerView);

		DrawablePanel panel = new DrawablePanel(diagram);
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		new PackageExample().setVisible(true);
	}
}
