package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;
import java.awt.Image;

public class ImageDrawable extends AbstractDrawable {
	/** Image to be drawn */
	private final Image image;

	public ImageDrawable(Image image) {
		this.image = image;
	}

	@Override
	public void draw(DrawingContext context) {
		int x = (int) Math.round(getX());
		int y = (int) Math.round(getY());
		int width = (int) Math.round(getWidth());
		int height = (int) Math.round(getHeight());
		Graphics2D g2d = context.getGraphics();
		g2d.drawImage(
			image,
			// Destination rectangle
			x, y, x+width, y+height,
			// Source rectangle
			0, 0, image.getWidth(null), image.getHeight(null),
			null
		);
	}

	public Image getImage() {
		return image;
	}
}
