/*
 * GRAL: Vector export for Java(R) Graphics2D
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
import de.erichseifert.gral.DrawableConstants;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.SettingsStorage;
import de.erichseifert.gral.util.Settings.Key;


/**
 * Class that draws a label to a specific location.
 * A Label is able to manage its settings and to set and get the
 * displayed text, as well as calculating its bounds.
 */
public class Label extends AbstractDrawable implements SettingsStorage, SettingsListener {
	/** Key for specifying the horizontal alignment within the bounding rectangle. */
	public static final Key ALIGNMENT_X = new Key("label.alignment.x");
	/** Key for specifying the vertical alignment within the bounding rectangle. */
	public static final Key ALIGNMENT_Y = new Key("label.alignment.y");
	/** Key for specifying the {@link de.erichseifert.gral.DrawableConstants}
	value where the label will be aligned at. */
	public static final Key ANCHOR = new Key("label.anchor");
	/** Key for specifying the font of this label. */
	public static final Key FONT = new Key("label.font");
	/** Key for specifying the rotation of this label, */
	public static final Key ROTATION = new Key("label.rotation");
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	paint the label shape. */
	public static final Key COLOR = new Key("label.color");

	private final Settings settings;
	private String text;
	private TextLayout layout;

	/**
	 * Creates a new <code>Label</code> object with the specified text.
	 * @param text Text to be displayed.
	 */
	public Label(String text) {
		settings = new Settings(this);
		this.text = text;

		setSettingDefault(ALIGNMENT_X, 0.5);
		setSettingDefault(ALIGNMENT_Y, 0.5);
		setSettingDefault(ANCHOR, DrawableConstants.Location.CENTER);
		setSettingDefault(FONT, Font.decode(null));
		setSettingDefault(ROTATION, 0.0);
		setSettingDefault(COLOR, Color.BLACK);
	}

	@Override
	public void draw(Graphics2D g2d) {
		if (layout == null) {
			return;
		}
		Paint paintOld = g2d.getPaint();

		AffineTransform txLabel = AffineTransform.getTranslateInstance(
			getX() + getWidth()/2.0,
			getY() + getHeight()/2.0
		);

		Double rotation = getSetting(ROTATION);
		if (rotation != null && (rotation%360.0) != 0.0) {
			txLabel.rotate(-rotation/180.0*Math.PI);
		}

		Rectangle2D textBounds = layout.getBounds();
		double alignmentX = this.<Double>getSetting(ALIGNMENT_X);
		double alignmentY = this.<Double>getSetting(ALIGNMENT_Y);
		DrawableConstants.Location anchor = getSetting(ANCHOR);
		double anchorModifierX =  anchor.getAlignmentH() - 0.5;
		double anchorModifierY = -anchor.getAlignmentV() + 0.5;
		txLabel.translate(
			-textBounds.getX() - anchorModifierX*textBounds.getWidth() -
				alignmentX*textBounds.getWidth() + (alignmentX - 0.5)*getWidth(),
			-textBounds.getY() - anchorModifierY*textBounds.getHeight() -
				alignmentY*textBounds.getHeight() + (alignmentY - 0.5)*getHeight()
		);

		Shape labelShape = layout.getOutline(txLabel);

		/*
		// DEBUG:
		g2d.setPaint(new Color(1f, 0f, 0f, 0.2f));
		g2d.fill(labelShape.getBounds2D());
		//*/

		Paint paint = getSetting(COLOR);
		g2d.setPaint(paint);
		GraphicsUtils.fillPaintedShape(g2d, labelShape, paint, null);
		g2d.setPaint(paintOld);

	}

	@Override
	public Dimension2D getPreferredSize() {
		Dimension2D d = super.getPreferredSize();
		if (layout != null) {
			Shape shape = getTextRectangle();
			Rectangle2D bounds = shape.getBounds2D();
			Double rotation = getSetting(ROTATION);
			if (rotation != null && (rotation%360.0) != 0.0) {
				shape = AffineTransform.getRotateInstance(
					-rotation/180.0*Math.PI,
					bounds.getCenterX(),
					bounds.getCenterY()
				).createTransformedShape(shape);
			}
			d.setSize(
				shape.getBounds2D().getWidth(),
				shape.getBounds2D().getHeight()
			);
		}
		return d;
	}

	/**
	 * Returns the bounding rectangle of the text.
	 * @return Bounds.
	 */
	public Rectangle2D getTextRectangle() {
		return layout.getBounds();
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
		renewLayout();
	}

	/**
	 * Revalidates the text layout.
	 */
	private void renewLayout() {
		if (text != null && !text.isEmpty()) {
			layout = GraphicsUtils.getLayout(text, this.<Font>getSetting(FONT));
		}
	}

	@Override
	public <T> T getSetting(Key key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(Key key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(Key key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(Key key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(Key key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (FONT.equals(key)) {
			renewLayout();
		}
	}
}
