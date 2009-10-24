package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import openjchart.Drawable;
import openjchart.charts.axes.Axis;

public class Chart extends JPanel {
	private final Map<Axis, Drawable> axes;

	private Color backgroundColor;
	private boolean antialiasingEnabled;

	public Chart() {
		axes = new HashMap<Axis, Drawable>();
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
		for (Drawable axis : axes.values()) {
			g2d.translate(axis.getX(), axis.getY());
			axis.draw(g2d);
			g2d.setTransform(txOld);
		}
	}

	public void addAxis(Axis axis) {
		axes.put(axis, null);
	}

	public void addAxis(Axis axis, Drawable drawable) {
		axes.put(axis, drawable);
	}

	public void removeAxis(Axis axis) {
		axes.remove(axis);
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
