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

import javax.swing.JFrame;

import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassDrawable;

public class UMLClassDiagram extends JFrame {
	private ClassDrawable clazz;

	public UMLClassDiagram() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		clazz = new ClassDrawable(UMLClassDiagram.class.getName());

		DrawablePanel panel = new DrawablePanel(clazz);
		getContentPane().add(panel, BorderLayout.CENTER);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paint(g2d);
	}

	public static void main(String[] args) {
		new UMLClassDiagram().setVisible(true);
	}
}
