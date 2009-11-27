package openjchart;

import java.awt.geom.Dimension2D;

public interface Layout {

	void layout(Container container);

	Dimension2D getPreferredSize(Container container);
}