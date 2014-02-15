package de.erichseifert.gral.uml;

import java.awt.Stroke;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.Label;

public class PackageDrawableNavigator extends DrawableContainerNavigator<PackageDrawable> {
	private Stroke defaultBorderStroke;

	public PackageDrawableNavigator(PackageDrawable packageDrawable) {
		super(packageDrawable);
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
}
