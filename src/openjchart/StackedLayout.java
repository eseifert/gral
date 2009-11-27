package openjchart;
import java.awt.geom.Dimension2D;

import openjchart.util.Insets2D;


public class StackedLayout implements Layout {
	public static enum Orientation { HORIZONTAL, VERTICAL };
	
	private Orientation orientation;
	private double gap;

	public StackedLayout(Orientation orientation) {
		this(orientation, 0.0);
	}

	public StackedLayout(Orientation orientation, double gap) {
		this.orientation = orientation;
		this.gap = gap;
	}

	@Override
	public Dimension2D getPreferredSize(Container container) {
		Insets2D insets = container.getInsets();

		double width = insets.getLeft();
		double height = insets.getTop();
		if (Orientation.HORIZONTAL.equals(orientation)) {
			for (Drawable component : container) {
				if (width > insets.getLeft()) {
					width += gap;
				}
				Dimension2D itemBounds = component.getPreferredSize();
				width += itemBounds.getWidth();
				height = Math.max(height, itemBounds.getHeight());
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			for (Drawable component : container) {
				if (height > insets.getTop()) {
					height += gap;
				}
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
		Insets2D insets = container.getInsets();

		if (Orientation.HORIZONTAL.equals(orientation)) {
			double heightMax = getMaxHeight(container);
			double x = insets.getLeft();
			double y = insets.getTop();
			for (Drawable component : container) {
				Dimension2D itemBounds = component.getPreferredSize();
				component.setBounds(x, y, itemBounds.getWidth(), heightMax);
				if (x > insets.getLeft()) {
					x += gap;
				}
				x += itemBounds.getWidth();
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			double x = insets.getLeft();
			double y = insets.getTop();
			double widthMax = getMaxWidth(container);
			for (Drawable component : container) {
				Dimension2D itemBounds = component.getPreferredSize();
				component.setBounds(x, y, widthMax, itemBounds.getHeight());
				if (y > insets.getTop()) {
					y += gap;
				}
				y += itemBounds.getHeight();
			}
		}
	}

	private static double getMaxWidth(Container container) {
		double widthMax = 0.0;
		for (Drawable component : container) {
			Dimension2D itemBounds = component.getPreferredSize();
			widthMax = Math.max(widthMax, itemBounds.getWidth());
		}
		return widthMax;
	}

	private static double getMaxHeight(Container container) {
		double heightMax = 0.0;
		for (Drawable component : container) {
			Dimension2D itemBounds = component.getPreferredSize();
			heightMax = Math.max(heightMax, itemBounds.getHeight());
		}
		return heightMax;
	}
}
