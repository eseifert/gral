package de.erichseifert.gral.examples;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;

import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;


public class LabelExample extends ExamplePanel {

	public LabelExample() {
		Label label = new Label("TestLabel");
		label.setFont(getFont().deriveFont(20f));
		label.setBackground(new GradientPaint(
			new Point2D.Double(0.0, 0.0), Color.BLACK,
			new Point2D.Double(1.0, 1.0), Color.WHITE
		));

		DrawablePanel panel = new InteractivePanel(label);
		add(panel);
	}

	@Override
	public String getTitle() {
		return "Label example";
	}

	@Override
	public String getDescription() {
		return "Label with colored background";
	}

	public static void main(String[] args) {
		new LabelExample().showInFrame();
	}
}
