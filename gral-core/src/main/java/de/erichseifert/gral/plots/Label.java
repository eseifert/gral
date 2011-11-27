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
package de.erichseifert.gral.plots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;


/**
 * Class that draws a label to a specific location.
 * A Label is able to manage its settings and to set and get the
 * displayed text, as well as calculating its bounds.
 */
public class Label extends AbstractDrawable implements SettingsListener {
	/** Key for specifying a {@link java.lang.Number} value for the horizontal
	alignment within the bounding rectangle. 0 means left, 1 means right. */
	public static final Key ALIGNMENT_X = new Key("label.alignment.x"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the vertical
	alignment within the bounding rectangle. 0 means top, 1 means bottom. */
	public static final Key ALIGNMENT_Y = new Key("label.alignment.y"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Font} instance used to display
	the text of this label. */
	public static final Key FONT = new Key("label.font"); //$NON-NLS-1$
	/** Key for specifying a {@link java.lang.Number} value for the rotation of
	this label in degrees. The rotation will be counterclockwise. */
	public static final Key ROTATION = new Key("label.rotation"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the label shape. */
	public static final Key COLOR = new Key("label.color"); //$NON-NLS-1$

	/** Text for this label. */
	private String text;
	/** Cached text layout. */
	private TextLayout layout;
	/** Cached outline of the label text. */
	private Shape outline;
	/** Flag describing whether cached values are still valid. */
	private boolean valid;

	/**
	 * Initializes a new empty {@code Label} instances.
	 */
	public Label() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * Initializes a new {@code Label} instance with the specified text.
	 * @param text Text to be displayed.
	 */
	public Label(String text) {
		addSettingsListener(this);
		this.text = text;

		setSettingDefault(ALIGNMENT_X, 0.5);
		setSettingDefault(ALIGNMENT_Y, 0.5);
		setSettingDefault(FONT, Font.decode(null));
		setSettingDefault(ROTATION, 0.0);
		setSettingDefault(COLOR, Color.BLACK);
	}

	/**
	 * Draws the {@code Drawable} with the specified
	 * {@code Graphics2D} object.
	 * @param context Environment used for drawing
	 */
	public void draw(DrawingContext context) {
		if (getLayout() == null) {
			return;
		}

		Shape labelShape = outline;
		Rectangle2D textBounds = outline.getBounds2D();

		// Rotate label text around its center point
		double rotation = this.<Number>getSetting(ROTATION).doubleValue();
		if (MathUtils.isCalculatable(rotation) && (rotation%360.0 != 0.0)) {
			AffineTransform txLabelText = AffineTransform.getRotateInstance(
				Math.toRadians(-rotation),
				textBounds.getCenterX(),
				textBounds.getCenterY()
			);
			labelShape = txLabelText.createTransformedShape(outline);
			textBounds = labelShape.getBounds2D();
		}

		// Get graphics instance and store state information
		Graphics2D graphics = context.getGraphics();
		AffineTransform txOld = graphics.getTransform();

		// Calculate absolute text position:
		// First, move the text to the upper left of the bounding rectangle
		double shapePosX = getX() - textBounds.getX();
		double shapePosY = getY() - textBounds.getY();
		// Position the text inside the bounding rectangle using the alignment
		// settings
		double alignmentX = this.<Number>getSetting(ALIGNMENT_X).doubleValue();
		double alignmentY = this.<Number>getSetting(ALIGNMENT_Y).doubleValue();
		shapePosX += alignmentX*(getWidth() - textBounds.getWidth());
		shapePosY += alignmentY*(getHeight() - textBounds.getHeight());
		// Apply positioning
		graphics.translate(shapePosX, shapePosY);

		// Paint the shape with the color from settings
		Paint paint = getSetting(COLOR);
		GraphicsUtils.fillPaintedShape(graphics, labelShape, paint, null);

		// Restore previous state
		graphics.setTransform(txOld);
	}

	@Override
	public Dimension2D getPreferredSize() {
		Dimension2D d = super.getPreferredSize();
		if (getLayout() != null) {
			Shape shape = getTextRectangle();
			Rectangle2D bounds = shape.getBounds2D();
			double rotation = this.<Number>getSetting(ROTATION).doubleValue();
			if (MathUtils.isCalculatable(rotation) && (rotation%360.0 != 0.0)) {
				AffineTransform txLabelText = AffineTransform.getRotateInstance(
					Math.toRadians(-rotation),
					bounds.getCenterX(),
					bounds.getCenterY()
				);
				shape = txLabelText.createTransformedShape(shape);
			}
			d.setSize(
				shape.getBounds2D().getWidth(),
				shape.getBounds2D().getHeight()
			);
		}
		return d;
	}

	/**
	 * Returns the {@code de TextLayout} instance for this label.
	 * @return {@code TextLayout} instance for this label.
	 */
	protected TextLayout getLayout() {
		if (!valid && text != null && !text.isEmpty()) {
			layout = GraphicsUtils.getLayout(
					text, this.<Font>getSetting(FONT));
			outline = layout.getOutline(null);
			valid = true;
		}
		return layout;
	}

	/**
	 * Returns the bounding rectangle of the text without rotation.
	 * @return Bounds.
	 */
	public Rectangle2D getTextRectangle() {
		return getLayout().getBounds();
	}

	/**
	 * Returns the text of this label.
	 * @return Text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the displayed text to the specified value.
	 * @param text Text to be displayed.
	 */
	public void setText(String text) {
		this.text = text;
		invalidate();
	}

	/**
	 * Revalidates the text layout.
	 */
	protected void invalidate() {
		layout = null;
		outline = null;
		valid = false;
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (FONT.equals(key)) {
			invalidate();
		}
	}

}
