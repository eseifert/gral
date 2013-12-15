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

		Package kernel = new Package("Kernel");
		Package associationClasses = new Package("AssociationClasses");
		kernel.merge(associationClasses);
		Package dependencies = new Package("Dependencies");
		kernel.merge(dependencies);
		Package powerTypes = new Package("PowerTypes");
		kernel.merge(powerTypes);
		Package interfaces = new Package("Interfaces");
		dependencies.merge(interfaces);
		Package basicBehaviours = new Package("BasicBehaviors");
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
