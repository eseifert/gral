package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.util.Dimension2D;

public class ClassDiagram extends DrawableContainer implements Navigable {
	private final Navigator navigator;

	public ClassDiagram() {
		navigator = new DrawableContainerNavigator(this);
	}

	@Override
	public Dimension2D getPreferredSize() {
		return new Dimension2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}
}
