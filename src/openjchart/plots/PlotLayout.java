package openjchart.plots;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import openjchart.Drawable;

public class PlotLayout {
	public static final String NORTH = "plotlayout.north";
	public static final String NORTH_EAST = "plotlayout.northeast";
	public static final String EAST = "plotlayout.east";
	public static final String SOUTH_EAST = "plotlayout.southeast";
	public static final String SOUTH = "plotlayout.south";
	public static final String SOUTH_WEST = "plotlayout.southwest";
	public static final String WEST = "plotlayout.west";
	public static final String NORTH_WEST = "plotlayout.northwest";
	public static final String CENTER = "plotlayout.center";

	private final Map<String, Drawable> drawables;

	public PlotLayout() {
		drawables = new HashMap<String, Drawable>(9);
	}

	public void add(Drawable drawable, String anchor) {
		drawables.put(anchor, drawable);
	}

	public void remove(Drawable drawable) {
		for (Map.Entry<String, Drawable> entry : drawables.entrySet()) {
			if (entry.getValue() == drawable) {
				drawables.remove(entry.getKey());
				break;
			}
		}
	}

	public void layout(Rectangle2D bounds, Insets insets) {
		if (insets == null) {
			insets = new Insets(0, 0, 0, 0);
		}

		// Calculate height north
		Drawable dNW = drawables.get(NORTH_WEST);
		Drawable dN = drawables.get(NORTH);
		Drawable dNE = drawables.get(NORTH_EAST);
		double heightNorth = getMaxHeight(dNW, dN, dNE);

		// Calculate width east
		Drawable dE = drawables.get(EAST);
		Drawable dSE = drawables.get(SOUTH_EAST);
		double widthEast = getMaxWidth(dNE, dE, dSE);

		// Calculate height south
		Drawable dS = drawables.get(SOUTH);
		Drawable dSW = drawables.get(SOUTH_WEST);
		double heightSouth = getMaxHeight(dSE, dS, dSW);

		// Calculate width east
		Drawable dW = drawables.get(WEST);
		double widthWest = getMaxWidth(dSW, dW, dNW);

		double yNorth = bounds.getMinY() + insets.top;
		double xEast = bounds.getMaxX() - widthEast - insets.right;
		double ySouth = bounds.getMaxY() - heightSouth - insets.bottom;
		double xWest = bounds.getMinX() + insets.left;

		layoutComponent(dNW,
			xWest, yNorth,
			widthWest, heightNorth
		);

		layoutComponent(dN,
			xWest + widthWest, yNorth,
			bounds.getWidth() - insets.left - widthWest - widthEast - insets.right,
			heightNorth
		);

		layoutComponent(dNE,
			xEast, yNorth,
			widthEast, heightNorth
		);

		layoutComponent(dE,
			xEast, yNorth + heightNorth,
			widthEast,
			bounds.getHeight() - insets.top - heightSouth - insets.bottom
		);

		layoutComponent(dSE,
			xEast, ySouth,
			widthEast,
			heightSouth
		);

		layoutComponent(dS,
			xWest + widthWest, ySouth,
			bounds.getWidth() - insets.left - widthWest - widthEast - insets.right,
			heightSouth
		);

		layoutComponent(dSW,
			xWest, ySouth,
			widthWest,
			heightSouth
		);

		layoutComponent(dW,
			xWest, yNorth + heightNorth,
			widthWest,
			bounds.getHeight() - insets.top - heightNorth - heightSouth - insets.bottom
		);

		Drawable dC = drawables.get(CENTER);
		layoutComponent(dC,
			xWest + widthWest, yNorth + heightNorth,
			bounds.getWidth() - insets.left - widthWest - widthEast - insets.right,
			bounds.getHeight() - insets.top - heightNorth - heightSouth - insets.bottom
		);
	}

	private static double getMaxWidth(Drawable... drawables) {
		double width = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			width = Math.max(width, d.getPreferredSize().getWidth());
		}

		return width;
	}

	private static double getMaxHeight(Drawable... drawables) {
		double height = 0.0;
		for (Drawable d : drawables) {
			if (d == null) {
				continue;
			}
			height = Math.max(height, d.getPreferredSize().getHeight());
		}

		return height;
	}

	private static void layoutComponent(Drawable d, double x, double y, double w, double h) {
		if (d == null) {
			return;
		}
		d.setBounds(x, y, w, h);
	}
}
