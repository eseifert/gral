package openjchart;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import openjchart.DrawableConstants.Orientation;
import openjchart.util.Insets2D;


public class StackedLayout implements Layout {
	private Orientation orientation;
	private double gap;
	private double alignment;

	public StackedLayout(Orientation orientation) {
		this(orientation, 0.0);
	}

	public StackedLayout(Orientation orientation, double gap) {
		this.orientation = orientation;
		this.gap = gap;
		this.alignment = 0.5;
	}

	@Override
	public Dimension2D getPreferredSize(Container container) {
		Insets2D insets = container.getInsets();

		double width = insets.getLeft();
		double height = insets.getTop();
		int count = 0;
		if (Orientation.HORIZONTAL.equals(orientation)) {
			for (Drawable component : container) {
				if (count++ > 0) {
					width += gap;
				}
				Dimension2D itemBounds = component.getPreferredSize();
				width += itemBounds.getWidth();
				height = Math.max(height, itemBounds.getHeight());
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			for (Drawable component : container) {
				if (count++ > 0) {
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
		Dimension2D size = getPreferredSize(container);
		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();

		double x = insets.getLeft();
		double y = insets.getTop();
		double width = bounds.getWidth() - insets.getLeft() - insets.getRight();
		double height = bounds.getHeight() - insets.getTop() - insets.getBottom();
		int count = 0;
		if (Orientation.HORIZONTAL.equals(orientation)) {
			x += Math.max(bounds.getWidth() - size.getWidth(), 0.0)*alignment;
			for (Drawable component : container) {
				if (count++ > 0) {
					x += gap;
				}
				Dimension2D compBounds = component.getPreferredSize();
				component.setBounds(x, y, compBounds.getWidth(), height);
				x += compBounds.getWidth();
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			y += Math.max(bounds.getHeight() - size.getHeight(), 0.0)*alignment;
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
