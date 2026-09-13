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

import java.awt.Color;
import java.awt.Dimension;

import de.erichseifert.gral.graphics.Drawable;

/**
 * <p>Base class of the visual examples. An example builds the
 * {@link Drawable} it is meant to demonstrate and says what to call it; it
 * knows nothing about the window it ends up in, so the same example can be
 * shown by the Swing browser and by the JavaFX one.</p>
 *
 * <p>A subclass builds its drawable in the constructor, hands it to
 * {@link #setDrawable(Drawable)}, and returns a title and a description that
 * the browsers list it under. The two colors the examples share are here as
 * well, so that they look like one set.</p>
 *
 * <p>An example that is more than a static picture implements one of the
 * nested interfaces, which describe the interaction without naming a toolkit:
 * {@link Adjustable} for a single number the viewer can change, {@link Animated}
 * for an example that refreshes itself. A browser renders those with its own
 * controls.</p>
 */
public abstract class Example {
	/** First corporate color used for normal coloring.*/
	protected static final Color COLOR1 = new Color( 55, 170, 200);
	/** Second corporate color used as signal color */
	protected static final Color COLOR2 = new Color(200,  80,  75);

	/** Drawable that this example demonstrates. */
	private Drawable drawable;

	/** Size that the example looks best at. */
	private Dimension preferredSize = new Dimension(800, 600);

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
	 * Returns the drawable that this example demonstrates.
	 * @return The drawable of the example.
	 */
	public Drawable getDrawable() {
		return drawable;
	}

	/**
	 * Sets the drawable that this example demonstrates. A subclass calls this
	 * at the end of its constructor.
	 * @param drawable The drawable of the example.
	 */
	protected void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	/**
	 * Returns the size that the example looks best at, which a browser uses
	 * as the initial size of the view.
	 * @return The preferred size of the view.
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	/**
	 * Sets the size that the example looks best at.
	 * @param preferredSize The preferred size of the view.
	 */
	protected void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}

	/**
	 * Returns whether the viewer may zoom and pan the drawable. An example
	 * that moves its own axes says no.
	 * @return {@code true} if zooming and panning are useful here,
	 *         {@code false} otherwise.
	 */
	public boolean isNavigable() {
		return true;
	}

	@Override
	public String toString() {
		return getTitle();
	}

	/**
	 * An example that is driven by a single number, which a browser offers as
	 * a slider.
	 */
	public interface Adjustable {
		/**
		 * Returns the lowest value that may be set.
		 * @return The lower bound of the range.
		 */
		int getMinimum();

		/**
		 * Returns the highest value that may be set.
		 * @return The upper bound of the range.
		 */
		int getMaximum();

		/**
		 * Returns the value that is currently set.
		 * @return The current value.
		 */
		int getValue();

		/**
		 * Sets a new value and updates the drawable accordingly.
		 * @param value The new value.
		 */
		void setValue(int value);

		/**
		 * Returns the distance between two labelled steps of the range.
		 * @return The spacing of the major ticks.
		 */
		int getTickSpacing();
	}

	/**
	 * An example that refreshes itself, which a browser drives with a timer of
	 * its own.
	 */
	public interface Animated {
		/**
		 * Returns how long to wait between two updates.
		 * @return The interval in milliseconds.
		 */
		int getUpdateInterval();

		/**
		 * Brings the example up to date. Called on the thread of the user
		 * interface, so the drawable may be changed directly.
		 */
		void update();
	}
}
