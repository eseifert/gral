/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
 * Class that stores an object for drawing and additional context information
 * that may be necessary to determine how to draw the object. This includes
 * information on drawing quality and the target media (screen, paper, etc.).
 */
public class DrawingContext {
	/**
	 * Data type that describes the quality mode of drawing operations.
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
	 * Data type that describes the type of the drawing target.
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
	 * Initializes a new context with a {@code Graphics2D} object.
	 * @param graphics Object for drawing geometry.
	 */
	public DrawingContext(Graphics2D graphics) {
		this(graphics, Quality.NORMAL, Target.BITMAP);
	}

	/**
	 * Initializes a new context with a {@code Graphics2D} object.
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
