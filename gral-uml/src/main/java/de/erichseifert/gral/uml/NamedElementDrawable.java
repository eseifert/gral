package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EditableLabel;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.util.Orientation;
import metamodel.classes.kernel.NamedElement;

public abstract class NamedElementDrawable extends DrawableContainer {
	private final EditableLabel name;
	private boolean nameVisible;

	public NamedElementDrawable(NamedElement namedElement, boolean nameVisible) {
		super(new StackedLayout(Orientation.HORIZONTAL, 0, 0));
		name = new EditableLabel(namedElement.getName());
		add(name);
		this.nameVisible = nameVisible;
	}

	public NamedElementDrawable(NamedElement namedElement) {
		this(namedElement, true);
	}

	@Override
	public void drawComponents(DrawingContext context) {
		for (Drawable d : this) {
			if (d.equals(name) && !isNameVisible()) {
				continue;
			}
			d.draw(context);
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
}
