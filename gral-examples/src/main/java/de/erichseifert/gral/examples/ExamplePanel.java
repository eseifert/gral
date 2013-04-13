/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Abstract base class for all visual examples.
 */
public abstract class ExamplePanel extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 8221256658243821951L;

	/** First corporate color used for normal coloring.*/
	protected static final Color COLOR1 = new Color( 55, 170, 200);
	/** Second corporate color used as signal color */
	protected static final Color COLOR2 = new Color(200,  80,  75);

	/**
	 * Performs basic initialization of an example,
	 * like setting a default size.
	 */
	public ExamplePanel() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.WHITE);
	}

	/**
	 * Returns a short title for the example.
	 * @return A title text.
	 */
	public abstract String getTitle();

	/**
	 * Returns a more detailed description of the example contents.
	 * @return A description of the example.
	 */
	public abstract String getDescription();

	/**
	 * Opens a frame and shows the example in it.
	 * @return the frame instance used for displaying the example.
	 */
	protected JFrame showInFrame() {
		JFrame frame = new JFrame(getTitle());
		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(getPreferredSize());
		frame.setVisible(true);
		return frame;
	}

	@Override
	public String toString() {
		return getTitle();
	}
}
