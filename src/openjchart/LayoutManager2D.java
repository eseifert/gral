package openjchart;

import openjchart.util.Dimension2D;

public interface LayoutManager2D {

	void layout(Container container);

	Dimension2D getPreferredSize(Container container);
}