package de.erichseifert.gral.uml;

import java.awt.geom.Dimension2D;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Label;
import metamodel.classes.kernel.NamedElement;

public abstract class NamedElementDrawable extends AbstractDrawable {
	private final Label name;
	private boolean nameVisible;

	public NamedElementDrawable(NamedElement namedElement, boolean nameVisible) {
		name = new Label(namedElement.getName());
		this.nameVisible = nameVisible;
	}

	public NamedElementDrawable(NamedElement namedElement) {
		this(namedElement, true);
	}

	@Override
	public void draw(DrawingContext context) {
		if (nameVisible) {
			name.draw(context);
		}
	}

	public boolean isNameVisible() {
		return nameVisible;
	}

	public void setNameVisible(boolean nameVisible) {
		this.nameVisible = nameVisible;
	}

	public Label getName() {
		return name;
	}

	@Override
	public Dimension2D getPreferredSize() {
		return name.getPreferredSize();
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		super.setBounds(x, y, width, height);
		name.setBounds(x, y, width, height);
	}
}
