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
package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;

/**
 * <p>Everything a {@link Drawable} needs in order to paint itself: the
 * {@code Graphics2D} to paint on, plus two hints about where the output is
 * going.</p>
 *
 * <p>The {@link Target} tells a renderer whether it is producing pixels or
 * vector geometry. That matters because some effects &mdash; gradients,
 * transparency, clipping against a complex shape &mdash; have to be rasterized
 * for a vector document, while for a bitmap target the {@code Graphics2D} can
 * do them directly:</p>
 * <pre>
 * public void draw(DrawingContext context) {
 *     if (context.getTarget() == DrawingContext.Target.VECTOR) {
 *         // Emit plain geometry that a PDF or SVG can represent.
 *     } else {
 *         // Free to use image-based effects.
 *     }
 * }
 * </pre>
 *
 * <p>The {@link Quality} hint says how much effort is wanted; a renderer may
 * use it to skip expensive detail while a plot is being dragged. Both hints are
 * advisory: ignoring them produces a correct, if less well adapted,
 * drawing.</p>
 *
 * <p>A context is immutable and cheap to create. The default constructor
 * assumes {@link Quality#NORMAL} and {@link Target#BITMAP}, which is what a
 * Swing component wants.</p>
 */
public class DrawingContext {
	/**
	 * How much effort should be spent on drawing. This is a hint; a renderer
	 * that ignores it still produces correct output.
	 */
	public enum Quality {
		/** Fast drawing mode. */
		DRAFT,
		/** Standard drawing mode. */
		NORMAL,
		/** High quality drawing mode. */
		QUALITY
	}

	/**
	 * What kind of output the drawing ends up in. Renderers check this to
	 * decide whether an effect has to be rasterized first.
	 */
	public enum Target {
		/** Bitmap drawing target consisting of pixels. */
		BITMAP,
		/** Vector drawing target consisting of lines and curves. */
		VECTOR
	}

	/** Graphics instance used for drawing. */
	private final Graphics2D graphics;
	/** Quality level used for drawing. */
	private final Quality quality;
	/** Target media. */
	private final Target target;

	/**
	 * Initializes a new context for drawing on a bitmap with normal quality.
	 * @param graphics Object for drawing geometry.
	 */
	public DrawingContext(Graphics2D graphics) {
		this(graphics, Quality.NORMAL, Target.BITMAP);
	}

	/**
	 * Initializes a new context with the specified quality and target.
	 * @param graphics Object for drawing geometry.
	 * @param quality Drawing quality.
	 * @param target Target media.
	 */
	public DrawingContext(Graphics2D graphics, Quality quality, Target target) {
		this.graphics = graphics;
		this.quality = quality;
		this.target = target;
	}

	/**
	 * Returns the object for drawing geometry.
	 * @return Graphics object.
	 */
	public Graphics2D getGraphics() {
		return graphics;
	}

	/**
	 * Returns the desired display quality.
	 * @return Display quality mode.
	 */
	public Quality getQuality() {
		return quality;
	}

	/**
	 * Returns the drawing target.
	 * @return Drawing target.
	 */
	public Target getTarget() {
		return target;
	}
}
