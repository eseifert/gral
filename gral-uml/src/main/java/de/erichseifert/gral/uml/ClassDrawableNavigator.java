package de.erichseifert.gral.uml;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.plots.Label;

public class ClassDrawableNavigator extends DrawableContainerNavigator {
	// TODO: Allow access to stored DrawableContainer in DrawableContainerNavigator
	// TODO: Make DrawableContainerNavigator generic
	private final ClassDrawable classDrawable;
	private final Map<Label, Font> defaultFontSizesByLabel;

	public ClassDrawableNavigator(ClassDrawable classDrawable) {
		super(classDrawable);
		this.classDrawable = classDrawable;
		defaultFontSizesByLabel = new HashMap<Label, Font>();
	}

	@Override
	public void setZoom(double zoom) {
		super.setZoom(zoom);
		Label classLabel = classDrawable.getClassName();
		Font classLabelFont = defaultFontSizesByLabel.get(classLabel);
		if (classLabelFont == null) {
			classLabelFont = classLabel.getFont();
			defaultFontSizesByLabel.put(classLabel, classLabelFont);
		}
		classLabel.setFont(classLabelFont.deriveFont((float) (classLabelFont.getSize2D()*zoom)));
	}
}
