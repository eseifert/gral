package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.Label;

public class ClassDrawableNavigator extends DrawableContainerNavigator {
	// TODO: Allow access to stored DrawableContainer in DrawableContainerNavigator
	// TODO: Make DrawableContainerNavigator generic
	private final ClassDrawable classDrawable;
	private final Map<Label, Font> defaultFontSizesByLabel;
	private Stroke defaultBorderStroke;

	public ClassDrawableNavigator(ClassDrawable classDrawable) {
		super(classDrawable);
		this.classDrawable = classDrawable;
		defaultFontSizesByLabel = new HashMap<Label, Font>();
	}

	@Override
	public void setZoom(double zoom) {
		super.setZoom(zoom);

		// Scale font of name label
		Label classLabel = classDrawable.getClassName();
		zoomLabel(classLabel, zoom);

		for (Drawable drawable : classDrawable.getDrawables()) {
			if (drawable instanceof Label) {
				zoomLabel((Label) drawable, zoom);
			}
		}

		// Scale font of border stroke
		Stroke classBorderStroke = defaultBorderStroke;
		if (defaultBorderStroke == null) {
			defaultBorderStroke = classDrawable.getBorderStroke();
			classBorderStroke = defaultBorderStroke;
		}
		if (classBorderStroke instanceof BasicStroke) {
			BasicStroke basicStroke = (BasicStroke) classBorderStroke;
			float lineWidth = basicStroke.getLineWidth();
			Stroke strokeNew = new BasicStroke((float) (lineWidth*zoom));
			classDrawable.setBorderStroke(strokeNew);
		}
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
