package openjchart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

import openjchart.plots.Plot;

public class PlotPanel extends JPanel {
	private Plot plot;

	public PlotPanel(Plot plot) {
		this.plot = plot;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		plot.draw((Graphics2D)g);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		plot.setBounds(x, y, width, height);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dims = super.getPreferredSize();
		Dimension2D dimsPlot = plot.getPreferredSize();
		dims.setSize(dimsPlot);
		return dims;
	}
}
