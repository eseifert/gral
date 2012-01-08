/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that draws a label to a specific location.
 * A Label is able to manage its settings and to set and get the
 * displayed text, as well as calculating its bounds.
 */
public class Label extends StylableDrawable {
	/** Key for specifying a {@link Number} value for the horizontal alignment
	within the bounding rectangle. 0 means left, 1 means right. */
	public static final Key ALIGNMENT_X =
		new Key("label.alignment.x"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the vertical alignment
	within the bounding rectangle. 0 means top, 1 means bottom. */
	public static final Key ALIGNMENT_Y =
		new Key("label.alignment.y"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Font} instance used to display
	the text of this label. */
	public static final Key FONT =
		new Key("label.font"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the rotation of this
	label in degrees. The rotation will be counterclockwise. */
	public static final Key ROTATION =
		new Key("label.rotation"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the label shape. */
	public static final Key COLOR =
		new Key("label.color"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value for the alignment of text
	with multiple lines. 0 means left, 1 means right. */
	public static final Key ALIGNMENT_TEXT =
		new Key("label.alignment.text"); //$NON-NLS-1$
	/** Key for specifying a {@link Boolean} value whether the words of the
	text should be wrapped to fit the size of the label. */
	public static final Key WORD_WRAP =
		new Key("label.wordWrap"); //$NON-NLS-1$

	/** Text for this label. */
	private String text;

	/** Cached outline of the label text with word wrapping. */
	private Shape outlineWrapped;
	/** Cached outline of the label text without word wrapping. */
	private Shape outlineUnwrapped;

	/**
	 * Initializes a new empty {@code Label} instance.
	 */
	public Label() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * Initializes a new {@code Label} instance with the specified text.
	 * @param text Text to be displayed.
	 */
	public Label(String text) {
		this.text = text;

		setSettingDefault(ALIGNMENT_X, 0.5);
		setSettingDefault(ALIGNMENT_Y, 0.5);
		setSettingDefault(FONT, Font.decode(null));
		setSettingDefault(ROTATION, 0.0);
		setSettingDefault(COLOR, Color.BLACK);
		setSettingDefault(ALIGNMENT_TEXT, 0.5);
		setSettingDefault(WORD_WRAP, Boolean.FALSE);
	}

	/**
	 * Draws the object with the specified drawing context.
	 * @param context Environment used for drawing
	 */
	public void draw(DrawingContext context) {
		boolean wordWrap = this.<Boolean>getSetting(WORD_WRAP);
		Shape labelShape = getCachedOutline(wordWrap);

		if (labelShape == null) {
			return;
		}

		Rectangle2D textBounds = labelShape.getBounds2D();

		// Rotate label text around its center point
		Number rotationObj = this.<Number>getSetting(ROTATION);

		if (MathUtils.isCalculatable(rotationObj)) {
			double rotation =
				MathUtils.normalizeDegrees(rotationObj.doubleValue());
			if (rotation != 0.0) {
				AffineTransform txLabelText =
					AffineTransform.getRotateInstance(
						Math.toRadians(-rotation),
						textBounds.getCenterX(),
						textBounds.getCenterY()
					);
				labelShape = txLabelText.createTransformedShape(labelShape);
				textBounds = labelShape.getBounds2D();
			}
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
		if (getCachedOutline(false) != null) {
			Shape shape = getTextRectangle();
			Rectangle2D bounds = shape.getBounds2D();
			Number rotationObj = this.<Number>getSetting(ROTATION);
			if (MathUtils.isCalculatable(rotationObj)) {
				double rotation =
					MathUtils.normalizeDegrees(rotationObj.doubleValue());
				if (rotation != 0.0) {
					AffineTransform txLabelText =
						AffineTransform.getRotateInstance(
							Math.toRadians(-rotation),
							bounds.getCenterX(),
							bounds.getCenterY()
						);
					shape = txLabelText.createTransformedShape(shape);
				}
			}
			d.setSize(
				shape.getBounds2D().getWidth(),
				shape.getBounds2D().getHeight()
			);
		}
		return d;
	}

	/**
	 * Returns an outline shape for this label.
	 * @param wordWrap Wrap the words of the text to fit the current size.
	 * @return Outline for this label.
	 */
	protected Shape getOutline(boolean wordWrap) {
		Font font = this.<Font>getSetting(FONT);
		float wrappingWidth = 0f;
		if (wordWrap) {
			double rotation = Math.toRadians(this.<Number>getSetting(
				ROTATION).doubleValue());
			wrappingWidth = (float) (
				Math.abs(Math.cos(rotation))*getWidth() +
				Math.abs(Math.sin(rotation))*getHeight());
		}
		double alignment = this.<Number>getSetting(
			ALIGNMENT_TEXT).doubleValue();
		Shape outline = GraphicsUtils.getOutline(
			getText(), font, wrappingWidth, alignment);
		return outline;
	}

	/**
	 * Returns a cached instance of the outline shape for this label.
	 * @param wordWrap Flag, whether to wrap lines to fit the current size.
	 * @return An instance of the outline shape for this label.
	 */
	protected Shape getCachedOutline(boolean wordWrap) {
		if (!isValid() && getText() != null && !getText().isEmpty()) {
			outlineWrapped = getOutline(true);
			outlineUnwrapped = getOutline(false);
		}
		if (wordWrap) {
			return outlineWrapped;
		} else {
			return outlineUnwrapped;
		}
	}

	/**
	 * Returns the bounding rectangle of the text without rotation or word
	 * wrapping.
	 * @return Bounding rectangle.
	 */
	public Rectangle2D getTextRectangle() {
		return getCachedOutline(false).getBounds();
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
	 * Marks the text layout as invalid. It has to be refreshed the next time.
	 */
	protected void invalidate() {
		outlineWrapped = null;
		outlineUnwrapped = null;
	}

	/**
	 * Returns whether the cached values in this label are valid.
	 * @return {@code true} if all cached values are valid,
	 *         otherwise {@code false}.
	 */
	protected boolean isValid() {
		boolean wordWrap = this.<Boolean>getSetting(WORD_WRAP);
		if (wordWrap) {
			return outlineWrapped != null;
		} else {
			return outlineUnwrapped != null;
		}
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (ROTATION.equals(key) || FONT.equals(key) ||
				ALIGNMENT_TEXT.equals(key) || WORD_WRAP.equals(key)) {
			invalidate();
		}
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		double widthOld = getWidth();
		double heightOld = getHeight();
		super.setBounds(x, y, width, height);
		if (width != widthOld || height != heightOld) {
			invalidate();
		}
	}
}
