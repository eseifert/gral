package de.erichseifert.gral.uml;

import java.awt.Font;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.Label;

public class PackageDrawableNavigator extends DrawableContainerNavigator<PackageDrawable> {
	private final Map<Label, Font> defaultFontSizesByLabel;
	private Stroke defaultBorderStroke;

	public PackageDrawableNavigator(PackageDrawable packageDrawable) {
		super(packageDrawable);
		defaultFontSizesByLabel = new HashMap<Label, Font>();
	}

	@Override
	public void setZoom(double zoom) {
		super.setZoom(zoom);

		// Scale font of name label
		Label packageLabel = getDrawableContainer().getTab().getName();
		zoomLabel(packageLabel, zoom);

		for (Drawable drawable : getDrawableContainer().getDrawables()) {
			if (drawable instanceof Label) {
				zoomLabel((Label) drawable, zoom);
			}
		}

		// Scale font of border stroke
		/*Stroke packageBorderStroke = defaultBorderStroke;
		if (defaultBorderStroke == null) {
			defaultBorderStroke = getDrawableContainer().getBorderStroke();
			packageBorderStroke = defaultBorderStroke;
		}
		if (packageBorderStroke instanceof BasicStroke) {
			BasicStroke basicStroke = (BasicStroke) packageBorderStroke;
			float lineWidth = basicStroke.getLineWidth();
			Stroke strokeNew = new BasicStroke((float) (lineWidth*zoom));
			getDrawableContainer().setBorderStroke(strokeNew);
		}*/
	}

	private void zoomLabel(Label label, double zoom) {
		Font classLabelFont = defaultFontSizesByLabel.get(label);
		if (classLabelFont == null) {
			classLabelFont = label.getFont();
			defaultFontSizesByLabel.put(label, classLabelFont);
		}
		label.setFont(classLabelFont.deriveFont((float) (classLabelFont.getSize2D()*zoom)));
	}
}
