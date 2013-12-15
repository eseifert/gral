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
import metamodel.classes.kernel.VisibilityKind;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassDiagram;
import de.erichseifert.gral.uml.PackageDrawable;
import de.erichseifert.gral.util.Insets2D;

public class UMLClassDiagram extends JFrame {

	public UMLClassDiagram() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		Package kernel = new Package(null, "Kernel", VisibilityKind.PUBLIC, null);
		Package associationClasses = new Package(null, "AssociationClasses", VisibilityKind.PUBLIC, null);
		kernel.merge(associationClasses);
		Package dependencies = new Package(null, "Dependencies", VisibilityKind.PUBLIC, null);
		kernel.merge(dependencies);
		Package powerTypes = new Package(null, "PowerTypes", VisibilityKind.PUBLIC, null);
		kernel.merge(powerTypes);
		Package interfaces = new Package(null, "Interfaces", VisibilityKind.PUBLIC, null);
		dependencies.merge(interfaces);
		Package basicBehaviours = new Package(null, "BasicBehaviors", VisibilityKind.PUBLIC, null);
		basicBehaviours.merge(interfaces);

		DrawableContainer diagram = new ClassDiagram();
		diagram.setInsets(new Insets2D.Double(10d));
		Drawable kernelDrawable = new PackageDrawable(kernel);
		diagram.add(kernelDrawable);
		Drawable depsDrawable = new PackageDrawable(dependencies);
		diagram.add(depsDrawable);

		DrawablePanel panel = new DrawablePanel(diagram);
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		new UMLClassDiagram().setVisible(true);
	}
}
