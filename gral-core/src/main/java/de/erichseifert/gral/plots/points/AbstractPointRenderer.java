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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import de.erichseifert.gral.DrawingContext;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.BasicSettingsStorage;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Placement;
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

		setSettingDefault(VALUE_DISPLAYED, false);
		setSettingDefault(VALUE_FORMAT, NumberFormat.getInstance());
		setSettingDefault(VALUE_ALIGNMENT_X, 0.5);
		setSettingDefault(VALUE_ALIGNMENT_Y, 0.5);
		setSettingDefault(VALUE_PLACEMENT, Placement.INSIDE);
		setSettingDefault(VALUE_DISTANCE, 1.0);
		setSettingDefault(VALUE_COLOR, Color.BLACK);
		setSettingDefault(VALUE_FONT, Font.decode(null));

		setSettingDefault(ERROR_DISPLAYED, false);
		setSettingDefault(ERROR_COLOR, Color.BLACK);
		setSettingDefault(ERROR_SHAPE, new Line2D.Double(-2.0, 0.0, 2.0, 0.0));
		setSettingDefault(ERROR_STROKE, new BasicStroke(1f));
	}

	/**
	 * Draws the specified value for the specified shape.
	 * @param context Environment used for drawing.
	 * @param point Point to draw into.
	 * @param value Value to be displayed.
	 */
	protected void drawValue(DrawingContext context,
			Shape point, Object value) {
		Format format = getSetting(VALUE_FORMAT);
		String text = format.format(value);
		Label valueLabel = new Label(text);
		valueLabel.setSetting(Label.ALIGNMENT_X, getSetting(VALUE_ALIGNMENT_X));
		valueLabel.setSetting(Label.ALIGNMENT_Y, getSetting(VALUE_ALIGNMENT_Y));
		valueLabel.setSetting(Label.COLOR, getSetting(VALUE_COLOR));
		valueLabel.setSetting(Label.FONT, getSetting(VALUE_FONT));

		double fontSize = this.<Font>getSetting(VALUE_FONT).getSize2D();
		Placement placement = getSetting(VALUE_PLACEMENT);
		Number valueDistanceObj = getSetting(VALUE_DISTANCE);
		double valueDistance = MathUtils.isCalculatable(valueDistanceObj)
			? valueDistanceObj.doubleValue()*fontSize : 0.0;
		Rectangle2D boundsPoint = point.getBounds2D();
		if (placement == Placement.OUTSIDE) {
			Dimension2D boundsValue = valueLabel.getPreferredSize();
			double padX = valueDistance + boundsValue.getWidth();
			double padY = valueDistance + boundsValue.getHeight();
			valueLabel.setBounds(
				boundsPoint.getX() - padX,
				boundsPoint.getY() - padY,
				boundsPoint.getWidth() + 2.0*padX,
				boundsPoint.getHeight() + 2.0*padY
			);
		} else if (placement == Placement.INSIDE) {
			double padX = valueDistance;
			double padY = valueDistance;
			valueLabel.setBounds(
				boundsPoint.getX() + padX,
				boundsPoint.getY() + padY,
				boundsPoint.getWidth() - 2.0*padX,
				boundsPoint.getHeight() - 2.0*padY
			);
		}
		valueLabel.draw(context);
	}

	/**
	 * Draws an error bar.
	 * @param context Environment used for drawing.
	 * @param point Point to draw error bar for.
	 * @param value Value of the data point.
	 * @param errorTop Upper value of the error bar.
	 * @param errorBottom Lower value of the error bar.
	 * @param axis Axis.
	 * @param axisRenderer Axis renderer.
	 */
	protected void drawError(DrawingContext context, Shape point,
			double value, double errorTop, double errorBottom,
			Axis axis, AxisRenderer axisRenderer) {
		if (axisRenderer == null) {
			return;
		}
		double posX = point.getBounds2D().getCenterX();
		double valueTop = value + errorTop;
		double valueBottom = value - errorBottom;
		double posY = axisRenderer.getPosition(
			axis, value, true, false).get(PointND.Y);
		double posYTop = axisRenderer.getPosition(
			axis, valueTop, true, false).get(PointND.Y) - posY;
		double posYBottom = axisRenderer.getPosition(
			axis, valueBottom, true, false).get(PointND.Y) - posY;
		Point2D pointTop = new Point2D.Double(posX, posYTop);
		Point2D pointBottom = new Point2D.Double(posX, posYBottom);
		Line2D errorBar = new Line2D.Double(pointTop, pointBottom);

		// Draw the error bar
		Paint errorPaint = getSetting(ERROR_COLOR);
		Stroke errorStroke = getSetting(ERROR_STROKE);
		Graphics2D graphics = context.getGraphics();
		GraphicsUtils.drawPaintedShape(
			graphics, errorBar, errorPaint, null, errorStroke);

		// Draw the shapes at the end of the error bars
		Shape endShape = getSetting(ERROR_SHAPE);
		AffineTransform txOld = graphics.getTransform();
		graphics.translate(posX, posYTop);
		Stroke endShapeStroke = new BasicStroke(1f);
		GraphicsUtils.drawPaintedShape(
			graphics, endShape, errorPaint, null, endShapeStroke);
		graphics.setTransform(txOld);
		graphics.translate(posX, posYBottom);
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
