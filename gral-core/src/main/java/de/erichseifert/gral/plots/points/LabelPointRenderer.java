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
package de.erichseifert.gral.plots.points;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.util.GraphicsUtils;


/**
 * Class that provides {@code Drawable}s, which display a specified data
 * values.
 */
public class LabelPointRenderer extends DefaultPointRenderer {
	/** Key for specifying the {@link java.lang.Integer} which specifies the
	index of the column that is used for point sizes. */
	public static final Key COLUMN = new Key("labelPoint.column"); //$NON-NLS-1$
	/** Key for specifying the {@link java.text.Format} instance which specifies
	how the labels will be displayed. */
	public static final Key FORMAT = new Key("labelPoint.format"); //$NON-NLS-1$
	/** Key for specifying the font of this label. */
	public static final Key FONT = new Key("labelPoint.font"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the horizontal alignment
	relative to the data point. 0 means left, 1 means right. */
	public static final Key ALIGNMENT_X = new Key("labelPoint.alignment.x"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the vertical alignment
    relative to the data point. 0 means top, 1 means bottom. */
	public static final Key ALIGNMENT_Y = new Key("labelPoint.alignment.y"); //$NON-NLS-1$
	/** Key for specifying the {@link de.erichseifert.gral.Location}
	value where the label will be aligned at. */

	/**
	 * Initializes a new object.
	 */
	public LabelPointRenderer() {
		setSettingDefault(COLUMN, 1);
		setSettingDefault(FORMAT, NumberFormat.getInstance());
		setSettingDefault(FONT, Font.decode(null));
		setSettingDefault(ALIGNMENT_X, 0.5);
		setSettingDefault(ALIGNMENT_Y, 0.5);
	}

	@Override
	public Shape getPointPath(Row row) {
		int colLabel = this.<Number>getSetting(COLUMN).intValue();
		if (colLabel >= row.size()) {
			return null;
		}

		Comparable<?> labelValue = row.get(colLabel);
		if (labelValue == null) {
			return null;
		}

		Format format = getSetting(FORMAT);
		String text = format.format(labelValue);
		TextLayout layout = GraphicsUtils.getLayout(
				text, this.<Font>getSetting(FONT));
		Shape shape = layout.getOutline(null);

		double alignX = this.<Number>getSetting(ALIGNMENT_X).doubleValue();
		double alignY = this.<Number>getSetting(ALIGNMENT_Y).doubleValue();
		Rectangle2D bounds = shape.getBounds2D();
		AffineTransform tx = AffineTransform.getTranslateInstance(
			-alignX*bounds.getWidth(), alignY*bounds.getHeight());
		shape = tx.createTransformedShape(shape);

		return shape;
	}
}
