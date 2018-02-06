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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;


/**
 * Class that draws a label to a specific location.
 * A Label is able to manage its settings and to set and get the
 * displayed text, as well as calculating its bounds.
 */
public class Label extends AbstractDrawable {
	/** Version id for serialization. */
	private static final long serialVersionUID = 374045708533704103L;

	/** Text for this label. */
	private String text;
	/** Horizontal label alignment. */
	private double alignmentX;
	/** Vertical label alignment. */
	private double alignmentY;
	/** Font used to display the text of this label. */
	private Font font;
	/** Rotation angle in degrees. */
	private double rotation;
	/** Paint used to draw the shape. */
	private Paint color;
	/** Relative text alignment. */
	private double textAlignment;
	/** Decides whether the text should be wrapped. */
	private boolean wordWrapEnabled;
	/** Paint used to display the background. */
	private Paint background;

	/** Cached outline of the label text with word wrapping. */
	private transient Shape outlineWrapped;
	/** Cached outline of the label text without word wrapping. */
	private transient Shape outlineUnwrapped;

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

		alignmentX = 0.5;
		alignmentY = 0.5;
		font = Font.decode(null);
		rotation = 0.0;
		color = Color.BLACK;
		textAlignment = 0.5;
		wordWrapEnabled = false;
	}

	/**
	 * Draws the object with the specified drawing context.
	 * @param context Environment used for drawing
	 */
	public void draw(DrawingContext context) {
		boolean wordWrap = isWordWrapEnabled();
		Shape labelShape = getCachedOutline(wordWrap);

		if (labelShape == null) {
			return;
		}

		Rectangle2D textBounds = labelShape.getBounds2D();

		// Rotate label text around its center point
		double rotation = getRotation();
		if (MathUtils.isCalculatable(rotation) && rotation != 0.0) {
			AffineTransform txLabelText =
				AffineTransform.getRotateInstance(
					Math.toRadians(-rotation),
					textBounds.getCenterX(),
					textBounds.getCenterY()
				);
			labelShape = txLabelText.createTransformedShape(labelShape);
			textBounds = labelShape.getBounds2D();
		}

		// Get graphics instance and store state information
		Graphics2D graphics = context.getGraphics();
		AffineTransform txOld = graphics.getTransform();

		// Draw background
		Paint background = getBackground();
		if (background != null) {
			GraphicsUtils.fillPaintedShape(graphics, getBounds(), background, null);
		}

		// Calculate absolute text position:
		// First, move the text to the upper left of the bounding rectangle
		double shapePosX = getX() - textBounds.getX();
		double shapePosY = getY() - textBounds.getY();
		// Position the text inside the bounding rectangle using the alignment
		// settings
		double alignmentX = getAlignmentX();
		double alignmentY = getAlignmentY();
		shapePosX += alignmentX*(getWidth() - textBounds.getWidth());
		shapePosY += alignmentY*(getHeight() - textBounds.getHeight());
		// Apply positioning
		graphics.translate(shapePosX, shapePosY);

		// Paint the shape with the color from settings
		Paint paint = getColor();
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
			double rotation = getRotation();
			if (MathUtils.isCalculatable(rotation) && rotation != 0.0) {
				AffineTransform txLabelText =
					AffineTransform.getRotateInstance(
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
	 * Returns an outline shape for this label.
	 * @param wordWrap Wrap the words of the text to fit the current size.
	 * @return Outline for this label.
	 */
	protected Shape getOutline(boolean wordWrap) {
		Font font = getFont();
		float wrappingWidth = 0f;
		if (wordWrap) {
			double rotation = Math.toRadians(getRotation());
			wrappingWidth = (float) (
				Math.abs(Math.cos(rotation))*getWidth() +
				Math.abs(Math.sin(rotation))*getHeight());
		}
		double alignment = getTextAlignment();
		return GraphicsUtils.getOutline(
			getText(), font, wrappingWidth, alignment);
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
		boolean wordWrap = isWordWrapEnabled();
		if (wordWrap) {
			return outlineWrapped != null;
		} else {
			return outlineUnwrapped != null;
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

	/**
	 * Returns the horizontal alignment within the bounding rectangle.
	 * 0.0 means left, 1.0 means right.
	 * @return Horizontal label alignment.
	 */
	public double getAlignmentX() {
		return alignmentX;
	}

	/**
	 * Sets the horizontal alignment within the bounding rectangle.
	 * 0.0 means left, 1.0 means right.
	 * @param alignmentX Horizontal label alignment.
	 */
	public void setAlignmentX(double alignmentX) {
		this.alignmentX = alignmentX;
	}

	/**
	 * Returns the vertical alignment within the bounding rectangle.
	 * 0.0 means top, 1.0 means bottom.
	 * @return Vertical label alignment.
	 */
	public double getAlignmentY() {
		return alignmentY;
	}

	/**
	 * Sets the vertical alignment within the bounding rectangle.
	 * 0.0 means top, 1.0 means bottom.
	 * @param alignmentY Vertical label alignment.
	 */
	public void setAlignmentY(double alignmentY) {
		this.alignmentY = alignmentY;
	}

	/**
	 * Returns the font used to display the text of this label.
	 * @return Font used for text display.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the font used to display the text of this label.
	 * @param font Font used for text display.
	 */
	public void setFont(Font font) {
		this.font = font;
		invalidate();
	}

	/**
	 * Returns the rotation of this label.
	 * The rotation will be counterclockwise.
	 * @return Rotation in degrees.
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Sets the rotation of this label.
	 * The rotation will be counterclockwise.
	 * @param angle Rotation in degrees.
	 */
	public void setRotation(double angle) {
		this.rotation = angle;
		invalidate();
	}

	/**
	 * Returns the paint used to draw the label shape.
	 * @return Paint for shape drawing.
	 */
	public Paint getColor() {
		return color;
	}

	/**
	 * Sets the paint used to draw the label shape.
	 * @param color Paint for shape drawing.
	 */
	public void setColor(Paint color) {
		this.color = color;
	}

	/**
	 * Returns the alignment of text with multiple lines.
	 * 0.0 means left, 1.0 means right.
	 * @return Relative text alignment.
	 */
	public double getTextAlignment() {
		return textAlignment;
	}

	/**
	 * Sets the alignment of text with multiple lines.
	 * 0.0 means left, 1.0 means right.
	 * @param textAlignment Relative text alignment.
	 */
	public void setTextAlignment(double textAlignment) {
		this.textAlignment = textAlignment;
		invalidate();
	}

	/**
	 * Returns whether words of the text should be wrapped to fit the size of the label.
	 * @return {@code true} if the text should be wrapped, {@code false} otherwise.
	 */
	public boolean isWordWrapEnabled() {
		return wordWrapEnabled;
	}

	/**
	 * Sets whether words of the text should be wrapped to fit the size of the label.
	 * @param wordWrapEnabled {@code true} if the text should be wrapped, {@code false} otherwise.
	 */
	public void setWordWrapEnabled(boolean wordWrapEnabled) {
		this.wordWrapEnabled = wordWrapEnabled;
		invalidate();
	}

	/**
	 * Returns the background color.
	 * @return Background color or {@code null}, if no background is defined.
	 */
	public Paint getBackground() {
		return background;
	}

	/**
	 * Sets the background color to the specified value.
	 * @param background Background color.
	 */
	public void setBackground(Paint background) {
		this.background = background;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Label)) {
			return false;
		}
		Label label = (Label) obj;
		return ((getText() == null && label.getText() == null) || getText().equals(label.getText()))
				&& (getAlignmentX() == label.getAlignmentX())
				&& (getAlignmentY() == label.getAlignmentY())
				&& ((getFont() == null && label.getFont() == null) || getFont().equals(label.getFont()))
				&& (getRotation() == label.getRotation())
				&& ((getColor() == null && label.getColor() == null) || getColor().equals(label.getColor()))
				&& (getTextAlignment() == label.getTextAlignment())
				&& (isWordWrapEnabled() == label.isWordWrapEnabled())
				&& ((getBackground() == null && label.getBackground() == null) || getBackground().equals(label.getBackground()));
	}

	// TODO: Override Object.hashCode()
}
