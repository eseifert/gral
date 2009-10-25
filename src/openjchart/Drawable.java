package openjchart;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public interface Drawable {
	Rectangle2D getBounds();
	void setBounds(Rectangle2D bounds);

	double getX();
	double getY();

	double getWidth();
	double getHeight();

	Dimension2D getPreferredSize();

	void draw(Graphics2D graphics);
}
