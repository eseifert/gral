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
package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EditableLabel;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
import metamodel.classes.interfaces.Property;
import metamodel.classes.kernel.Class;
import metamodel.classes.kernel.Operation;
import metamodel.classes.kernel.Parameter;

public class ClassRenderer {
	public static class ClassDrawable extends DrawableContainer {
		private final EditableLabel className;
		private final metamodel.classes.kernel.Class clazz;
		private Paint background;

		private static class PropertyLabel extends DrawableContainer {
			private final Label visibility;
			private final EditableLabel propertyName;
			private final Label type;
			private boolean visibilityDisplayed;
			private boolean typeDisplayed;

			public PropertyLabel(Property property) {
				super(new StackedLayout(Orientation.HORIZONTAL));

				visibility = new Label(property.getVisibility().getLiteral()+" ");
				add(visibility);
				propertyName = new EditableLabel(property.getName());
				propertyName.addPropertyChangeListener("text", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						layout();
					}
				});
				add(propertyName);
				type = new Label(": "+property.getType().getName());
				add(type);
			}

			@Override
			protected void drawComponents(DrawingContext context) {
				// TODO: Drawable should support a visibility flag
				for (Drawable d : this) {
					if (d.equals(visibility) && !isVisibilityDisplayed()) {
						continue;
					} else if (d.equals(type) && !isTypeDisplayed()) {
						continue;
					}
					d.draw(context);
				}
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

		private static class OperationLabel extends DrawableContainer {
			private final Label visibility;
			private final EditableLabel operationName;
			private final Label parameters;
			private boolean visibilityDisplayed;

			public OperationLabel(Operation operation) {
				super(new StackedLayout(Orientation.HORIZONTAL));
				visibility = new Label(operation.getVisibility().getLiteral()+" ");
				add(visibility);
				operationName = new EditableLabel(operation.getName());
				operationName.addPropertyChangeListener("text", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						layout();
					}
				});
				add(operationName);

				StringBuilder parametersText = new StringBuilder();
				parametersText.append("(");
				Iterator<Parameter> parameterIterator = operation.getOwnedParameters().iterator();
				while (parameterIterator.hasNext()) {
					Parameter parameter = parameterIterator.next();
					parametersText.append(parameter.getName());
					parametersText.append(": ");
					parametersText.append(parameter.getType().getName());
					if (parameterIterator.hasNext()) {
						parametersText.append(", ");
					}
				}
				parametersText.append(")");
				parameters = new Label(parametersText.toString());
				add(parameters);
			}

			@Override
			protected void drawComponents(DrawingContext context) {
				// TODO: Drawable should support a visibility flag
				for (Drawable d : this) {
					if (d.equals(visibility) && !isVisibilityDisplayed()) {
						continue;
					}
					d.draw(context);
				}
			}

			public boolean isVisibilityDisplayed() {
				return visibilityDisplayed;
			}

			public void setVisibilityDisplayed(boolean visibilityDisplayed) {
				this.visibilityDisplayed = visibilityDisplayed;
			}
		}

		private Stroke borderStroke;

		protected ClassDrawable(metamodel.classes.kernel.Class clazz, Stroke borderStroke) {
			super(new StackedLayout(Orientation.VERTICAL, 0.0, 7.0));
			this.clazz = clazz;
			this.borderStroke = borderStroke;
			className = new EditableLabel(clazz.getQualifiedName());
			Font classNameFont = className.getFont().deriveFont(Font.BOLD);
			if (clazz.isAbstract()) {
				classNameFont = classNameFont.deriveFont(Font.ITALIC);
			}
			className.setFont(classNameFont);
			add(className);

			StackedLayout.Constraints constraints = new StackedLayout.Constraints(false, 0.0, 0.5);
			for (Property property : clazz.getOwnedAttributes()) {
				PropertyLabel propertyLabel = new PropertyLabel(property);
				propertyLabel.setVisibilityDisplayed(true);
				propertyLabel.setTypeDisplayed(true);
				add(propertyLabel, constraints);
			}
			for (Operation operation : clazz.getOwnedOperations()) {
				OperationLabel operationLabel = new OperationLabel(operation);
				operationLabel.setVisibilityDisplayed(true);
				add(operationLabel, constraints);
			}

			double textHeight = className.getTextRectangle().getHeight();
			Insets2D insets = new Insets2D.Double(textHeight);
			setInsets(insets);
			borderStroke = new BasicStroke(1f);
		}

		@Override
		public void draw(DrawingContext context) {
			if (getBackground() != null) {
				GraphicsUtils.fillPaintedShape(context.getGraphics(), getBounds(), getBackground(), null);
			}
			drawBorder(context);
			drawComponents(context);
		}

		protected void drawBorder(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();

			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);

			g2d.draw(getBounds());

			g2d.setStroke(strokeOld);
		}

		public EditableLabel getClassName() {
			return className;
		}

		public Class getClazz() {
			return clazz;
		}

		public Paint getBackground() {
			return background;
		}

		public void setBackground(Paint background) {
			this.background = background;
		}
	}

	/** Stroke used to paint the border of the class. */
	private Stroke borderStroke;

	public ClassRenderer() {
		borderStroke = new BasicStroke(1f);
	}

	public Drawable getRendererComponent(metamodel.classes.kernel.Class clazz) {
		return new ClassDrawable(clazz, getBorderStroke());
	}

	public Stroke getBorderStroke() {
		return borderStroke;
	}

	public void setBorderStroke(Stroke borderStroke) {
		this.borderStroke = borderStroke;
	}
}
