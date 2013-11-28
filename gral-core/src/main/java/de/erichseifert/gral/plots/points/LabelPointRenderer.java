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
package de.erichseifert.gral.plots.points;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.util.GraphicsUtils;

/**
 * Class that provides {@code Drawable}s, which display a specified data
 * values.
 */
public class LabelPointRenderer extends DefaultPointRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2612520977245369774L;

	/** Key for specifying a {@link java.awt.Font} instance for the font of
	this label. */
	public static final Key FONT =
		new Key("labelPoint.font"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the horizontal alignment
	relative to the data point. 0 means left, 1 means right. */
	public static final Key ALIGNMENT_X =
		new Key("labelPoint.alignment.x"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the vertical alignment
    relative to the data point. 0 means top, 1 means bottom. */
	public static final Key ALIGNMENT_Y =
		new Key("labelPoint.alignment.y"); //$NON-NLS-1$
	/** Key for specifying the {@link de.erichseifert.gral.Location} value
	where the label will be aligned at. */

	private int column;
	private Format format;

	/**
	 * Initializes a new object.
	 */
	public LabelPointRenderer() {
		column = 1;
		format = NumberFormat.getInstance();
		setSettingDefault(FONT, Font.decode(null));
		setSettingDefault(ALIGNMENT_X, 0.5);
		setSettingDefault(ALIGNMENT_Y, 0.5);
	}

	/**
	 * Returns the index of the column which is used for the label.
	 * @return Index of the column which is used for the label.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the index of the column which will be used for the label.
	 * @param column Index of the column which will be used for the label.
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * Returns the format which specifies how the labels are displayed.
	 * @return {@code Format} instance which specifies how the labels are
	 * displayed.
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * Sets the format which specifies how the labels will be displayed.
	 * @param format {@code Format} instance which specifies how the labels will
	 * be displayed.
	 */
	public void setFormat(Format format) {
		this.format = format;
	}

	@Override
	public Shape getPointShape(PointData data) {
		Row row = data.row;
		int colLabel = getColumn();
		if (colLabel >= row.size()) {
			return null;
		}

		Comparable<?> labelValue = row.get(colLabel);
		if (labelValue == null) {
			return null;
		}

		Format format = getFormat();
		Font font = getSetting(FONT);
		String text = format.format(labelValue);
		double alignment = this.<Number>getSetting(ALIGNMENT_X).doubleValue();
		Shape shape = GraphicsUtils.getOutline(text, font, 0f, alignment);

		double alignX = this.<Number>getSetting(ALIGNMENT_X).doubleValue();
		double alignY = this.<Number>getSetting(ALIGNMENT_Y).doubleValue();
		Rectangle2D bounds = shape.getBounds2D();
		AffineTransform tx = AffineTransform.getTranslateInstance(
			-alignX*bounds.getWidth(), alignY*bounds.getHeight());
		shape = tx.createTransformedShape(shape);

		return shape;
	}
}
