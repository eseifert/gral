package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Stroke;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.Label;

public class ClassDrawableNavigator extends DrawableContainerNavigator<ClassDrawable> {
	private Stroke defaultBorderStroke;

	public ClassDrawableNavigator(ClassDrawable classDrawable) {
		super(classDrawable);
	}

	@Override
	public void setZoom(double zoom) {
		super.setZoom(zoom);

		// Scale font of name label
		Label classLabel = getDrawableContainer().getClassName();
		zoomLabel(classLabel, zoom);

		for (Drawable drawable : getDrawableContainer().getDrawables()) {
			if (drawable instanceof Label) {
				zoomLabel((Label) drawable, zoom);
			}
		}

		// Scale font of border stroke
		Stroke classBorderStroke = defaultBorderStroke;
		if (defaultBorderStroke == null) {
			defaultBorderStroke = getDrawableContainer().getBorderStroke();
			classBorderStroke = defaultBorderStroke;
		}
		if (classBorderStroke instanceof BasicStroke) {
			BasicStroke basicStroke = (BasicStroke) classBorderStroke;
			float lineWidth = basicStroke.getLineWidth();
			Stroke strokeNew = new BasicStroke((float) (lineWidth*zoom));
			getDrawableContainer().setBorderStroke(strokeNew);
		}
	}
}
