/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral;

import java.awt.Graphics2D;

/**
 * Class that encapsulates an object for drawing and context information.
 */
public class DrawingContext {
	/**
	 * Data type that describes the quality mode of drawing operations.
	 */
	public static enum Quality {
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
	public static enum Target {
		/** Bitmap drawing target consisting of pixels. */
		BITMAP,
		/** Vector drawing target consisting of lines and curves. */
		VECTOR
	}

	private final Graphics2D graphics;
	private final Quality quality;
	private final Target target;

	/**
	 * Initializes a new context with a <code>Graphics2D</code> object.
	 * @param graphics Object for drawing geometry.
	 */
	public DrawingContext(Graphics2D graphics) {
		this(graphics, Quality.NORMAL, Target.BITMAP);
	}

	/**
	 * Initializes a new context with a <code>Graphics2D</code> object.
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
