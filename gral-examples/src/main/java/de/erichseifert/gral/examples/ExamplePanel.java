/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import de.erichseifert.gral.ui.InteractivePanel;

/**
 * <p>The Swing view of an {@link Example}: a white 800x600 panel showing the
 * drawable of the example in an {@link InteractivePanel}, so that it can be
 * zoomed, panned, exported and printed.</p>
 *
 * <p>The interaction that an example declares without naming a toolkit is
 * rendered here in Swing terms: {@link Example.Adjustable} becomes a
 * {@code JSlider} below the plot, and {@link Example.Animated} a
 * {@code javax.swing.Timer} that ticks while the panel is showing.</p>
 */
public class ExamplePanel extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 8221256658243821951L;

	/** Example that is displayed. */
	private final transient Example example;

	/** Component showing the drawable of the example. */
	private final InteractivePanel plotPanel;

	/**
	 * Creates a panel showing the specified example.
	 * @param example Example to display.
	 */
	public ExamplePanel(Example example) {
		super(new BorderLayout());
		setBackground(Color.WHITE);

		this.example = example;
		setPreferredSize(example.getPreferredSize());

		plotPanel = new InteractivePanel(example.getDrawable());
		plotPanel.setZoomable(example.isNavigable());
		plotPanel.setPannable(example.isNavigable());
		add(plotPanel, BorderLayout.CENTER);

		if (example instanceof Example.Adjustable) {
			add(createSlider((Example.Adjustable) example), BorderLayout.SOUTH);
		}
		if (example instanceof Example.Animated) {
			startTimer((Example.Animated) example);
		}
	}

	/**
	 * Returns the example that is displayed by this panel.
	 * @return The example.
	 */
	public Example getExample() {
		return example;
	}

	/**
	 * Creates the slider that drives an adjustable example.
	 * @param adjustable Example to drive.
	 * @return A slider bound to the example.
	 */
	private JSlider createSlider(Example.Adjustable adjustable) {
		var slider = new JSlider(adjustable.getMinimum(), adjustable.getMaximum(),
				adjustable.getValue());
		slider.setBorder(new EmptyBorder(15, 15, 5, 15));
		slider.setMajorTickSpacing(adjustable.getTickSpacing());
		slider.setMinorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.addChangeListener(e -> {
			adjustable.setValue(slider.getValue());
			repaint();
		});
		return slider;
	}

	/**
	 * Starts the timer that refreshes an animated example. It keeps running
	 * for the lifetime of the panel, but does nothing while the panel is
	 * hidden, for example because another example is selected.
	 * @param animated Example to refresh.
	 */
	private void startTimer(Example.Animated animated) {
		var timer = new Timer(animated.getUpdateInterval(), e -> {
			if (!isVisible()) {
				return;
			}
			animated.update();
			repaint();
		});
		timer.setCoalesce(false);
		timer.start();
	}

	/**
	 * Opens a frame and shows this panel in it.
	 * @return the frame instance used for displaying the example.
	 */
	public JFrame showInFrame() {
		var frame = new JFrame(getExample().getTitle());
		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(getPreferredSize());
		frame.setVisible(true);
		return frame;
	}

	@Override
	public String toString() {
		return getExample().getTitle();
	}
}
