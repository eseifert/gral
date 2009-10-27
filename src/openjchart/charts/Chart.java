package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.Border;

import openjchart.Drawable;
import openjchart.charts.axes.Axis;

public class Chart extends JPanel {
	private final Map<String, Axis> axes;
	private final Map<String, Drawable> axisDrawables;

	private Color backgroundColor;
	private boolean antialiasingEnabled;

	public Chart() {
		axes = new HashMap<String, Axis>();
		axisDrawables = new HashMap<String, Drawable>();
		backgroundColor = Color.WHITE;
		setBackground(backgroundColor);
		antialiasingEnabled = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		if (antialiasingEnabled) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		else {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		AffineTransform txOld = g2d.getTransform();

		// Draw axes
		for (Drawable axis : axisDrawables.values()) {
			g2d.translate(axis.getX(), axis.getY());
			axis.draw(g2d);
			g2d.setTransform(txOld);
		}
	}

	@Override
	public Insets getInsets() {
		Border border = getBorder();
		if (border != null) {
			return border.getBorderInsets(this);
		}

		return new Insets(0, 0, 0, 0);
	}

	public Axis getAxis(String name) {
		return axes.get(name);
	}

	public void setAxis(String name, Axis axis) {
		if (axis == null) {
			removeAxis(name);
		}
		axes.put(name, axis);
		axisDrawables.put(name, null);
	}

	public void setAxis(String name, Axis axis, Drawable drawable) {
		if (axis == null) {
			removeAxis(name);
		}
		axes.put(name, axis);
		axisDrawables.put(name, drawable);
	}

	public void removeAxis(String name) {
		axes.remove(name);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean isAntialiasingEnabled() {
		return antialiasingEnabled;
	}

	public void setAntialiasingEnabled(boolean antialiasingEnabled) {
		this.antialiasingEnabled = antialiasingEnabled;
	}
}
