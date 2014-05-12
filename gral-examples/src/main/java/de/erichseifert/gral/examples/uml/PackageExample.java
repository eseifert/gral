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

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassDiagramRenderer;
import de.erichseifert.gral.uml.PackageRenderer;
import metamodel.classes.kernel.Package;
import metamodel.examples.TypesPackage;

public class PackageExample extends JFrame {

	public PackageExample() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		Package types = new TypesPackage();

		DrawableContainer diagram = (DrawableContainer) new ClassDiagramRenderer().getRendererComponent();
		PackageRenderer packageRenderer = new PackageRenderer();
		Drawable typesDrawableCompactView = packageRenderer.getRendererComponent(types);
		typesDrawableCompactView.setBounds(30, 30, 150, 100);
		diagram.add(typesDrawableCompactView);
		packageRenderer.setMembersVisible(true);
		packageRenderer.setNameVisible(true);
		Drawable typesDrawableContainerView = packageRenderer.getRendererComponent(types);
		typesDrawableContainerView.setBounds(300, 100, 150, 400);
		diagram.add(typesDrawableContainerView);

		DrawablePanel panel = new DrawablePanel(diagram);
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		new PackageExample().setVisible(true);
	}
}
