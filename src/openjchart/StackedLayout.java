package openjchart;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import openjchart.util.Insets2D;


public class StackedLayout implements Layout {
	public static enum Orientation { HORIZONTAL, VERTICAL };

	private Orientation orientation;
	private double minGap;

	public StackedLayout(Orientation orientation) {
		this(orientation, 0.0);
	}

	public StackedLayout(Orientation orientation, double minGap) {
		this.orientation = orientation;
		this.minGap = minGap;
	}

	@Override
	public Dimension2D getPreferredSize(Container container) {
		Insets2D insets = container.getInsets();

		double width = insets.getLeft();
		double height = insets.getTop();
		if (Orientation.HORIZONTAL.equals(orientation)) {
			for (Drawable component : container) {
				Dimension2D itemBounds = component.getPreferredSize();
				width += itemBounds.getWidth();
				height = Math.max(height, itemBounds.getHeight());
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			for (Drawable component : container) {
				Dimension2D itemBounds = component.getPreferredSize();
				width = Math.max(width, itemBounds.getWidth());
				height += itemBounds.getHeight();
			}
		}
		width += insets.getRight();
		height += insets.getBottom();

		Dimension2D bounds = new openjchart.util.Dimension2D.Double(width, height);
		return bounds;
	}

	@Override
	public void layout(Container container) {
		Dimension2D size = getPreferredSize(container);
		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();

		double x = insets.getLeft();
		double y = insets.getTop();
		double width = bounds.getWidth() - insets.getLeft() - insets.getRight();
		double height = bounds.getHeight() - insets.getTop() - insets.getBottom();
		int count = 0;
		if (Orientation.HORIZONTAL.equals(orientation)) {
			double gap = Math.max(bounds.getWidth() - size.getWidth(), minGap)/(double)(container.size() - 1);
			for (Drawable component : container) {
				if (count++ > 0) {
					x += gap;
				}
				Dimension2D compBounds = component.getPreferredSize();
				component.setBounds(x, y, compBounds.getWidth(), height);
				x += compBounds.getWidth();
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			double gap = Math.max(bounds.getHeight() - size.getHeight(), minGap)/(double)(container.size() - 1);
			for (Drawable component : container) {
				if (count++ > 0) {
					y += gap;
				}
				Dimension2D compBounds = component.getPreferredSize();
				component.setBounds(x, y, width, compBounds.getHeight());
				y += compBounds.getHeight();
			}
		}
	}

}
