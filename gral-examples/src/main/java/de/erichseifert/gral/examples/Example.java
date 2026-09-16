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
import java.util.Random;

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
 *
 * <p>Most of the examples invent their data, and two of them read the clock,
 * so no two runs look alike. That is what an example gallery wants and what a
 * golden-image test cannot have, so both go through {@link #createRandom()}
 * and {@link #currentTimeMillis()} rather than through {@code new Random()} and
 * {@code System.currentTimeMillis()}. Setting the system property
 * {@value #REPRODUCIBLE_PROPERTY} pins them, and every example then builds the
 * same drawable every time.</p>
 */
public abstract class Example {
	/**
	 * Name of the system property that pins the values which would otherwise
	 * differ from run to run. Its value is the seed to draw random data from;
	 * anything that is not a number selects a default seed. When the property
	 * is absent, the examples are as random as they have always been.
	 */
	public static final String REPRODUCIBLE_PROPERTY =
		"de.erichseifert.gral.examples.reproducible"; //$NON-NLS-1$

	/** Seed used when the property above is set to something unparseable. */
	private static final long DEFAULT_SEED = 20260916L;

	/**
	 * Instant the examples pretend it is while they are pinned, in milliseconds
	 * since the epoch: 2026-01-01T00:00:00Z. Any fixed value would do; this one
	 * gives readable tick labels.
	 */
	private static final long FIXED_TIME = 1767225600000L;

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
	 * Returns whether the examples have been pinned to fixed data and a fixed
	 * clock, which the system property {@value #REPRODUCIBLE_PROPERTY} does.
	 * @return {@code true} when every run builds the same drawable.
	 */
	protected static boolean isReproducible() {
		return System.getProperty(REPRODUCIBLE_PROPERTY) != null;
	}

	/**
	 * Returns the source of random numbers an example should invent its data
	 * from. It is seeded while the examples are pinned, so that the same
	 * example always produces the same values, and unseeded otherwise.
	 * @return A new random number generator.
	 */
	protected static Random createRandom() {
		String value = System.getProperty(REPRODUCIBLE_PROPERTY);
		if (value == null) {
			return new Random();
		}
		long seed;
		try {
			seed = Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			seed = DEFAULT_SEED;
		}
		return new Random(seed);
	}

	/**
	 * Returns the current time in milliseconds since the epoch, or a fixed
	 * instant while the examples are pinned. An example that puts the clock on
	 * an axis reads it from here, so that its tick labels can be compared.
	 * @return The time an example should call now.
	 */
	protected static long currentTimeMillis() {
		return isReproducible() ? FIXED_TIME : System.currentTimeMillis();
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
