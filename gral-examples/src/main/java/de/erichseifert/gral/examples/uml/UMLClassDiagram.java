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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;

import metamodel.classes.NamedElement.VisibilityKind;
import metamodel.classes.Package;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.PackageDrawable;

public class UMLClassDiagram extends JFrame {

	public UMLClassDiagram() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		Package kernel = new Package(null, VisibilityKind.PUBLIC, "Kernel", null);
		Package associationClasses = new Package(null, VisibilityKind.PUBLIC, "AssociationClasses", null);
		kernel.merge(associationClasses);
		Package dependencies = new Package(null, VisibilityKind.PUBLIC, "Dependencies", null);
		kernel.merge(dependencies);
		Package powerTypes = new Package(null, VisibilityKind.PUBLIC, "PowerTypes", null);
		kernel.merge(powerTypes);
		Package interfaces = new Package(null, VisibilityKind.PUBLIC, "Interfaces", null);
		dependencies.merge(interfaces);
		Package basicBehaviours = new Package(null, VisibilityKind.PUBLIC, "BasicBehaviors", null);
		basicBehaviours.merge(interfaces);

		Drawable pkg = new PackageDrawable(kernel);

		DrawablePanel panel = new DrawablePanel(pkg);
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2d);
	}

	public static void main(String[] args) {
		new UMLClassDiagram().setVisible(true);
	}
}
