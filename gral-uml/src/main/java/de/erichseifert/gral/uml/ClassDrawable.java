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
package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import metamodel.classes.interfaces.Property;
import metamodel.classes.kernel.Operation;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.Dimension2D;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;

public class ClassDrawable extends DrawableContainer {
	private final Label className;
	/** Stroke used to paint the border of the plot area. */
	private Stroke borderStroke;

	private static class PropertyLabel extends Label {
		private final Property property;
		private boolean visibilityDisplayed;
		private boolean typeDisplayed;

		public PropertyLabel(Property property) {
			this.property = property;
		}

		@Override
		public String getText() {
			StringBuilder text = new StringBuilder();
			if (isVisibilityDisplayed()) {
				text.append(property.getVisibility().getLiteral());
				text.append(" ");
			}
			text.append(property.getName());
			if (isTypeDisplayed()) {
				text.append(": ");
				text.append(property.getType().getName());
			}
			return text.toString();
		}

		public boolean isVisibilityDisplayed() {
			return visibilityDisplayed;
		}

		public void setVisibilityDisplayed(boolean visibilityDisplayed) {
			this.visibilityDisplayed = visibilityDisplayed;
		}

		public boolean isTypeDisplayed() {
			return typeDisplayed;
		}

		public void setTypeDisplayed(boolean typeDisplayed) {
			this.typeDisplayed = typeDisplayed;
		}
	}

	public ClassDrawable(metamodel.classes.kernel.Class clazz) {
		super(new StackedLayout(Orientation.VERTICAL, new Dimension2D.Double(0.0, 7.0)));
		className = new Label(clazz.getQualifiedName());
		Font classNameFont = className.getFont().deriveFont(Font.BOLD);
		if (clazz.isAbstract()) {
			classNameFont = classNameFont.deriveFont(Font.ITALIC);
		}
		className.setFont(classNameFont);
		add(className);

		setInsets(new Insets2D.Double(12.0, 6.0, 6.0, 6.0));

		for (Property property : clazz.getOwnedAttributes()) {
			PropertyLabel propertyLabel = new PropertyLabel(property);
			propertyLabel.setVisibilityDisplayed(true);
			propertyLabel.setTypeDisplayed(true);
			propertyLabel.setAlignmentX(0.0);
			add(propertyLabel);
		}
		for (Operation operation : clazz.getOwnedOperations()) {
			Label operationLabel = new Label(operation.getName());
			operationLabel.setAlignmentX(0.0);
			add(operationLabel);
		}

		double textHeight = className.getTextRectangle().getHeight();
		double textWidth = className.getTextRectangle().getWidth();
		Insets2D insets = new Insets2D.Double(textHeight);
		setInsets(insets);
		Rectangle2D bounds = new Rectangle2D.Double(
			0.0, 0.0,
			insets.getLeft() + textWidth + insets.getRight(),
			insets.getTop() + textHeight + insets.getBottom()
		);
		setBounds(bounds);
		borderStroke = new BasicStroke(5f);
	}

	@Override
	public void draw(DrawingContext context) {
		drawBorder(context);
		super.drawComponents(context);
	}

	protected void drawBorder(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();

		Stroke strokeOld = g2d.getStroke();
		//Stroke borderStroke = getBorderStroke();
		//g2d.setStroke(borderStroke);

		g2d.draw(getBounds());

		g2d.setStroke(strokeOld);
	}

	public Stroke getBorderStroke() {
		return borderStroke;
	}

	public void setBorderStroke(Stroke borderStroke) {
		this.borderStroke = borderStroke;
	}
}
