package openjchart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;

import javax.swing.JPanel;

/**
 * A class that represents an adapter between the components of this library and swing.
 * It displays a single Drawable in a JPanel.
 */
public class DrawablePanel extends JPanel {
	private final Drawable drawable;

	/**
	 * Creates a new DrawablePanel with the specified Drawable.
	 * @param drawable Drawable to be displayer
	 */
	public DrawablePanel(Drawable drawable) {
		this.drawable = drawable;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawable.draw((Graphics2D)g);
	}

	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		drawable.setBounds(bounds);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		drawable.setBounds(x, y, width, height);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dims = super.getPreferredSize();
		Dimension2D dimsPlot = drawable.getPreferredSize();
		dims.setSize(dimsPlot);
		return dims;
	}

	public Dimension getMinimalSize() {
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}
}
