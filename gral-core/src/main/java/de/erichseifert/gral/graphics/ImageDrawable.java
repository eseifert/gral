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
package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;
import java.awt.Image;

public class ImageDrawable extends AbstractDrawable {
	/** Image to be drawn */
	private final Image image;

	public ImageDrawable(Image image) {
		this.image = image;
	}

	@Override
	public void draw(DrawingContext context) {
		int x = (int) Math.round(getX());
		int y = (int) Math.round(getY());
		int width = (int) Math.round(getWidth());
		int height = (int) Math.round(getHeight());
		Graphics2D g2d = context.getGraphics();
		g2d.drawImage(
			image,
			// Destination rectangle
			x, y, x+width, y+height,
			// Source rectangle
			0, 0, image.getWidth(null), image.getHeight(null),
			null
		);
	}

	public Image getImage() {
		return image;
	}
}
