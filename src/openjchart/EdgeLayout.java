package openjchart;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import openjchart.util.Insets2D;

public class EdgeLayout implements Layout {
	public static final String NORTH = "north";
	public static final String NORTH_EAST = "northeast";
	public static final String EAST = "east";
	public static final String SOUTH_EAST = "southeast";
	public static final String SOUTH = "south";
	public static final String SOUTH_WEST = "southwest";
	public static final String WEST = "west";
	public static final String NORTH_WEST = "northwest";
	public static final String CENTER = "center";

	/* (non-Javadoc)
	 * @see openjchart.plots.LayoutManager2D#layout(java.awt.geom.Rectangle2D, java.awt.Insets)
	 */
	public void layout(Container container) {
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}

		Rectangle2D bounds = container.getBounds();

		// Fetch components
		Drawable north = null, northEast = null, east = null, southEast = null,
		         south = null, southWest = null, west = null, northWest = null,
		         center = null;
		for (Drawable d: container) {
			Object constraints = container.getConstraints(d);
			if (NORTH.equals(constraints)) {
				north = d;
			} else if (NORTH_EAST.equals(constraints)) {
				northEast = d;
			} else if (EAST.equals(constraints)) {
				east = d;
			} else if (SOUTH_EAST.equals(constraints)) {
				southEast = d;
			} else if (SOUTH.equals(constraints)) {
				south = d;
			} else if (SOUTH_WEST.equals(constraints)) {
				southWest = d;
			} else if (WEST.equals(constraints)) {
				west = d;
			} else if (NORTH_WEST.equals(constraints)) {
				northWest = d;
			} else if (CENTER.equals(constraints)) {
				center = d;
			}
		}

		// Calculate maximum widths and heights
		double widthWest    = getMaxWidth(northWest,  west,   southWest);
		double widthEast    = getMaxWidth(northEast,  east,   southEast);
		double heightNorth  = getMaxHeight(northWest, north,  northEast);
		double heightSouth  = getMaxHeight(southWest, south,  southEast);

		double xWest = bounds.getMinX() + insets.getLeft();
		double xEast = bounds.getMaxX() - insets.getRight() - widthEast;
		double yNorth = bounds.getMinY() + insets.getTop();
		double ySouth = bounds.getMaxY() - insets.getBottom() - heightSouth;

		layoutComponent(northWest,
			xWest, yNorth,
			widthWest, heightNorth
		);

		layoutComponent(north,
			xWest + widthWest, yNorth,
			bounds.getWidth() - insets.getLeft() - widthWest - widthEast - insets.getRight(),
			heightNorth
		);

		layoutComponent(northEast,
			xEast, yNorth,
			widthEast, heightNorth
		);

		layoutComponent(east,
			xEast, yNorth + heightNorth,
			widthEast,
			bounds.getHeight() - insets.getTop() - heightSouth - insets.getBottom()
		);

		layoutComponent(southEast,
			xEast, ySouth,
			widthEast,
			heightSouth
		);

		layoutComponent(south,
			xWest + widthWest, ySouth,
			bounds.getWidth() - insets.getLeft() - widthWest - widthEast - insets.getRight(),
			heightSouth
		);

		layoutComponent(southWest,
			xWest, ySouth,
			widthWest,
			heightSouth
		);

		layoutComponent(west,
			xWest, yNorth + heightNorth,
			widthWest,
			bounds.getHeight() - insets.getTop() - heightNorth - heightSouth - insets.getBottom()
		);

		layoutComponent(center,
			xWest + widthWest, yNorth + heightNorth,
			bounds.getWidth() - insets.getLeft() - widthWest - widthEast - insets.getRight(),
			bounds.getHeight() - insets.getTop() - heightNorth - heightSouth - insets.getBottom()
		);
	}

	@Override
	public Dimension2D getPreferredSize(Container container) {
		// Fetch components
		Drawable north = null, northEast = null, east = null, southEast = null,
		         south = null, southWest = null, west = null, northWest = null,
		         center = null;
		for (Drawable d: container) {
			Object constraints = container.getConstraints(d);
			if (NORTH.equals(constraints)) {
				north = d;
			} else if (NORTH_EAST.equals(constraints)) {
				northEast = d;
			} else if (EAST.equals(constraints)) {
				east = d;
			} else if (SOUTH_EAST.equals(constraints)) {
				southEast = d;
			} else if (SOUTH.equals(constraints)) {
				south = d;
			} else if (SOUTH_WEST.equals(constraints)) {
				southWest = d;
			} else if (WEST.equals(constraints)) {
				west = d;
			} else if (NORTH_WEST.equals(constraints)) {
				northWest = d;
			} else if (CENTER.equals(constraints)) {
				center = d;
			}
		}

		// Calculate maximum widths and heights
		double widthWest    = getMaxWidth(northWest,  west,   southWest);
		double widthCenter  = getMaxWidth(north,      center, south);
		double widthEast    = getMaxWidth(northEast,  east,   southEast);
		double heightNorth  = getMaxHeight(northWest, north,  northEast);
		double heightCenter = getMaxHeight(west,      center, east);
		double heightSouth  = getMaxHeight(southWest, south,  southEast);

		// Calculate preferred dimensions
		Insets2D insets = container.getInsets();
		return new openjchart.util.Dimension2D.Double(
			insets.getLeft() + widthEast + widthCenter + widthWest + insets.getRight(),
			insets.getTop() + heightNorth + heightCenter + heightSouth + insets.getBottom()
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
