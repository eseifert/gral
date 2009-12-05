package openjchart;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import openjchart.DrawableConstants.Location;
import openjchart.util.Insets2D;

public class EdgeLayout implements Layout {
	private double hgap;
	private double vgap;

	public EdgeLayout(double hgap, double vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

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
			Location constraints = (Location)container.getConstraints(d);
			if (Location.NORTH.equals(constraints)) {
				north = d;
			} else if (Location.NORTH_EAST.equals(constraints)) {
				northEast = d;
			} else if (Location.EAST.equals(constraints)) {
				east = d;
			} else if (Location.SOUTH_EAST.equals(constraints)) {
				southEast = d;
			} else if (Location.SOUTH.equals(constraints)) {
				south = d;
			} else if (Location.SOUTH_WEST.equals(constraints)) {
				southWest = d;
			} else if (Location.WEST.equals(constraints)) {
				west = d;
			} else if (Location.NORTH_WEST.equals(constraints)) {
				northWest = d;
			} else if (Location.CENTER.equals(constraints)) {
				center = d;
			}
		}

		// Calculate maximum widths and heights
		double widthWest    = getMaxWidth(northWest,  west,   southWest);
		double widthEast    = getMaxWidth(northEast,  east,   southEast);
		double heightNorth  = getMaxHeight(northWest, north,  northEast);
		double heightSouth  = getMaxHeight(southWest, south,  southEast);

		double hgapEast  = (widthWest>0.0 && center!=null) ? hgap : 0.0;
		double hgapWest  = (widthEast>0.0 && center!=null) ? hgap : 0.0;
		double vgapNorth = (heightNorth>0.0 && center!=null) ? vgap : 0.0;
		double vgapSouth = (heightSouth>0.0 && center!=null) ? vgap : 0.0;

		double xWest   = bounds.getMinX() + insets.getLeft();
		double xCenter = xWest + widthWest + hgapEast;
		double xEast   = bounds.getMaxX() - insets.getRight() - widthEast;
		double yNorth  = bounds.getMinY() + insets.getTop();
		double yCenter = yNorth + heightNorth + vgapNorth;
		double ySouth  = bounds.getMaxY() - insets.getBottom() - heightSouth;

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
			xCenter, yCenter,
			bounds.getWidth() - insets.getLeft() - widthWest - widthEast - insets.getRight() - hgapEast - hgapWest,
			bounds.getHeight() - insets.getTop() - heightNorth - heightSouth - insets.getBottom() - vgapNorth - vgapSouth
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
			if (Location.NORTH.equals(constraints)) {
				north = d;
			} else if (Location.NORTH_EAST.equals(constraints)) {
				northEast = d;
			} else if (Location.EAST.equals(constraints)) {
				east = d;
			} else if (Location.SOUTH_EAST.equals(constraints)) {
				southEast = d;
			} else if (Location.SOUTH.equals(constraints)) {
				south = d;
			} else if (Location.SOUTH_WEST.equals(constraints)) {
				southWest = d;
			} else if (Location.WEST.equals(constraints)) {
				west = d;
			} else if (Location.NORTH_WEST.equals(constraints)) {
				northWest = d;
			} else if (Location.CENTER.equals(constraints)) {
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

		double hgapEast  = (widthWest>0.0 && center!=null) ? hgap : 0.0;
		double hgapWest  = (widthEast>0.0 && center!=null) ? hgap : 0.0;
		double vgapNorth = (heightNorth>0.0 && center!=null) ? vgap : 0.0;
		double vgapSouth = (heightSouth>0.0 && center!=null) ? vgap : 0.0;

		// Calculate preferred dimensions
		Insets2D insets = container.getInsets();
		return new openjchart.util.Dimension2D.Double(
			insets.getLeft() + widthEast + hgapEast + widthCenter + hgapWest + widthWest + insets.getRight(),
			insets.getTop() + heightNorth + vgapNorth + heightCenter + vgapSouth + heightSouth + insets.getBottom()
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
