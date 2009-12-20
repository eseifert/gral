package openjchart;

/**
 * Interface that solely stores constants concerning Drawables.
 * Stored constants include location and orientation.
 */
public interface DrawableConstants {
	static enum Location {
		CENTER,
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST
	};

	static enum Orientation {
		HORIZONTAL,
		VERTICAL
	};

}
