package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Iterator;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EditableLabel;
import de.erichseifert.gral.graphics.StackedLayout;
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

		private static class PropertyLabel extends EditableLabel {
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

		private static class OperationLabel extends EditableLabel {
			private final Operation operation;
			private boolean visibilityDisplayed;

			public OperationLabel(Operation operation) {
				this.operation = operation;
			}

			@Override
			public String getText() {
				StringBuilder text = new StringBuilder();
				if (isVisibilityDisplayed()) {
					text.append(operation.getVisibility().getLiteral());
					text.append(" ");
				}
				text.append(operation.getName());
				text.append("(");
				Iterator<Parameter> parameterIterator = operation.getOwnedParameters().iterator();
				while (parameterIterator.hasNext()) {
					Parameter parameter = parameterIterator.next();
					text.append(parameter.getName());
					text.append(": ");
					text.append(parameter.getType().getName());
					if (parameterIterator.hasNext()) {
						text.append(", ");
					}
				}
				text.append(")");
				return text.toString();
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

			for (Property property : clazz.getOwnedAttributes()) {
				PropertyLabel propertyLabel = new PropertyLabel(property);
				propertyLabel.setVisibilityDisplayed(true);
				propertyLabel.setTypeDisplayed(true);
				propertyLabel.setAlignmentX(0.0);
				add(propertyLabel);
			}
			for (Operation operation : clazz.getOwnedOperations()) {
				OperationLabel operationLabel = new OperationLabel(operation);
				operationLabel.setVisibilityDisplayed(true);
				operationLabel.setAlignmentX(0.0);
				add(operationLabel);
			}

			double textHeight = className.getTextRectangle().getHeight();
			Insets2D insets = new Insets2D.Double(textHeight);
			setInsets(insets);
			borderStroke = new BasicStroke(1f);
		}

		@Override
		public void draw(DrawingContext context) {
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
