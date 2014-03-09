package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class EditableLabel extends Label {
	private boolean edited;

	public EditableLabel() {
	}

	public EditableLabel(String text) {
		super(text);
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	@Override
	public void draw(DrawingContext context) {
		super.draw(context);
		if (isEdited()) {
			drawCaret(context);
		}
	}

	protected void drawCaret(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();
		FontRenderContext fontContext = g2d.getFontRenderContext();
		TextLayout layout = new TextLayout(getText(), getFont(), fontContext);
		Shape[] caretShapes = layout.getCaretShapes(layout.getCharacterCount());
		Shape weakShape = caretShapes[0];
		AffineTransform txOld = g2d.getTransform();
		Rectangle2D textBounds = getCachedOutline(isWordWrapEnabled()).getBounds2D();
		g2d.draw(weakShape.getBounds2D());

		// FIXME: Code copied from superclass
		// Calculate absolute text position:
		// First, move the text to the upper left of the bounding rectangle
		double shapePosX = getX() - textBounds.getX();
		double shapePosY = getY() - textBounds.getY();
		// Position the text inside the bounding rectangle using the alignment
		// settings
		double alignmentX = getAlignmentX();
		double alignmentY = getAlignmentY();
		shapePosX += alignmentX*(getWidth() - textBounds.getWidth());
		shapePosY += alignmentY*(getHeight() - textBounds.getHeight()) + layout.getAscent();
		// Apply positioning
		g2d.translate(shapePosX, shapePosY);

		g2d.draw(weakShape);
		g2d.setTransform(txOld);
	}
}
