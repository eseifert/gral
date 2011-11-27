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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.Location;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.BasicSettingsStorage;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;


/**
 * Abstract class implementing functions for the administration of settings.
 */
public abstract class AbstractPointRenderer extends BasicSettingsStorage
		implements PointRenderer, SettingsListener {
	/**
	 * Creates a new AbstractPointRenderer object with default shape and
	 * color.
	 */
	public AbstractPointRenderer() {
		addSettingsListener(this);

		setSettingDefault(SHAPE, new Rectangle2D.Double(-2.5, -2.5, 5.0, 5.0));
		setSettingDefault(COLOR, Color.BLACK);

		setSettingDefault(VALUE_DISPLAYED, Boolean.FALSE);
		setSettingDefault(VALUE_COLUMN, 1);
		setSettingDefault(VALUE_LOCATION, Location.CENTER);
		setSettingDefault(VALUE_ALIGNMENT_X, 0.5);
		setSettingDefault(VALUE_ALIGNMENT_Y, 0.5);
		setSettingDefault(VALUE_DISTANCE, 1.0);
		setSettingDefault(VALUE_COLOR, Color.BLACK);
		setSettingDefault(VALUE_FONT, Font.decode(null));

		setSettingDefault(ERROR_DISPLAYED, Boolean.FALSE);
		setSettingDefault(ERROR_COLUMN_TOP, 2);
		setSettingDefault(ERROR_COLUMN_BOTTOM, 3);
		setSettingDefault(ERROR_COLOR, Color.BLACK);
		setSettingDefault(ERROR_SHAPE, new Line2D.Double(-2.0, 0.0, 2.0, 0.0));
		setSettingDefault(ERROR_STROKE, new BasicStroke(1f));
	}

	/**
	 * Draws the specified value label for the specified shape.
	 * @param context Environment used for drawing.
	 * @param point Point to draw into.
	 * @param value Value to be displayed.
	 */
	protected void drawValue(DrawingContext context,
			Shape point, Object value) {

		// Formatting
		Format format = getSetting(VALUE_FORMAT);
		if ((format == null) && (value instanceof Number)) {
			format = NumberFormat.getInstance();
		}

		// Text to display
		String text = (format != null) ? format.format(value) : value.toString();

		// Visual settings
		Color color = getSetting(VALUE_COLOR);
		Font font = getSetting(VALUE_FONT);
		double fontSize = font.getSize2D();

		// Layout settings
		Location location = getSetting(VALUE_LOCATION);
		Number alignX = this.<Number>getSetting(VALUE_ALIGNMENT_X);
		Number alignY = this.<Number>getSetting(VALUE_ALIGNMENT_Y);
		Number rotation = this.<Number>getSetting(VALUE_ROTATION);
		Number distanceObj = getSetting(VALUE_DISTANCE);
		double distance = 0.0;
		if (MathUtils.isCalculatable(distanceObj)) {
			distance = distanceObj.doubleValue()*fontSize;
		}

		// Create a label with the settings
		Label label = new Label(text);
		label.setSetting(Label.ALIGNMENT_X, alignX);
		label.setSetting(Label.ALIGNMENT_Y, alignY);
		label.setSetting(Label.ROTATION, rotation);
		label.setSetting(Label.COLOR, color);
		label.setSetting(Label.FONT, font);

		Rectangle2D boundsPoint = point.getBounds2D();
		Dimension2D boundsLabel = label.getPreferredSize();

		// Horizontal layout
		double x, w = boundsLabel.getWidth();
		if (location == Location.NORTH_EAST || location == Location.EAST || location == Location.SOUTH_EAST) {
			x = boundsPoint.getMinX() - distance - boundsLabel.getWidth();
		} else if (location == Location.NORTH_WEST || location == Location.WEST || location == Location.SOUTH_WEST) {
			x = boundsPoint.getMaxX() + distance + boundsLabel.getWidth();
		} else {
			x = boundsPoint.getX() + distance;
			w = boundsPoint.getWidth() - 2.0*distance;
		}
		// Vertical layout
		double y, h = boundsLabel.getHeight();
		if (location == Location.NORTH_EAST || location == Location.NORTH || location == Location.NORTH_WEST) {
			y = boundsPoint.getMinY() - distance - boundsLabel.getHeight();
		} else if (location == Location.SOUTH_EAST || location == Location.SOUTH || location == Location.SOUTH_WEST) {
			y = boundsPoint.getMaxY() + distance + boundsLabel.getHeight();
		} else {
			y = boundsPoint.getY() + distance;
			h = boundsPoint.getHeight() - 2.0*distance;
		}

		label.setBounds(x, y, w, h);
		label.draw(context);
	}

	/**
	 * Draws an error bar.
	 * @param context Environment used for drawing.
	 * @param point Shape of the point.
	 * @param value Position of the current point in axis units.
	 * @param lengthTop Upper value of the error bar in axis units.
	 * @param lengthBottom Lower value of the error bar in axis units.
	 * @param axis Axis.
	 * @param axisRenderer Axis renderer.
	 */
	protected void drawError(DrawingContext context, Shape point,
			double value, double lengthTop, double lengthBottom,
			Axis axis, AxisRenderer axisRenderer) {
		if (axisRenderer == null) {
			return;
		}
		Graphics2D graphics = context.getGraphics();
		AffineTransform txOld = graphics.getTransform();

		// Calculate positions
		PointND<Double> pointValue = axisRenderer.getPosition(
			axis, value, true, false);
		double posY = pointValue.get(PointND.Y);

		PointND<Double> pointTop = axisRenderer.getPosition(
			axis, value + lengthTop, true, false);
		double posYTop = pointTop.get(PointND.Y) - posY;

		PointND<Double> pointBottom = axisRenderer.getPosition(
			axis, value - lengthBottom, true, false);
		double posYBottom = pointBottom.get(PointND.Y) - posY;

		// Draw the error bar
		Line2D errorBar = new Line2D.Double(0.0, posYTop, 0.0, posYBottom);
		Paint errorPaint = getSetting(ERROR_COLOR);
		Stroke errorStroke = getSetting(ERROR_STROKE);
		GraphicsUtils.drawPaintedShape(
			graphics, errorBar, errorPaint, null, errorStroke);

		// Draw the shapes at the end of the error bars
		Shape endShape = getSetting(ERROR_SHAPE);
		graphics.translate(0.0, posYTop);
		Stroke endShapeStroke = new BasicStroke(1f);
		GraphicsUtils.drawPaintedShape(
			graphics, endShape, errorPaint, null, endShapeStroke);
		graphics.setTransform(txOld);
		graphics.translate(0.0, posYBottom);
		GraphicsUtils.drawPaintedShape(
			graphics, endShape, errorPaint, null, endShapeStroke);
		graphics.setTransform(txOld);
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
	}
}
