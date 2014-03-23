package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class EditableLabel extends Label implements KeyListener {
	private boolean edited;
	private int caretPosition;
	private StringBuilder text;

	// TODO: Should superclass use StringBuilder?
	public EditableLabel() {
		this("");
	}

	public EditableLabel(String text) {
		super(text);
		this.text = new StringBuilder(text);
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
		setCaretPosition(text.length());
	}

	protected int getCaretPosition() {
		return caretPosition;
	}

	protected void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
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
		AffineTransform txOld = g2d.getTransform();
		FontRenderContext fontRenderContext = g2d.getFontRenderContext();

		String outlineText = getText();
		int caretPosition = getCaretPosition();
		if (outlineText.isEmpty()) {
			outlineText = EMPTY_LABEL_OUTLINE_STRING;
			caretPosition = 0;
		}

		TextLayout layout = new TextLayout(outlineText, getFont(), fontRenderContext);
		Shape[] caretShapes = layout.getCaretShapes(caretPosition);
		Shape caretShape = caretShapes[0];
		Rectangle2D textBounds = getCachedOutline(isWordWrapEnabled()).getBounds2D();

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

		g2d.draw(caretShape);
		g2d.setTransform(txOld);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!isEdited()) {
			return;
		}

		int key = e.getKeyCode();
		int caretPosition = getCaretPosition();
		if (key == KeyEvent.VK_RIGHT) {
			caretPosition = Math.min(caretPosition + 1, text.length());
		} else if (key == KeyEvent.VK_LEFT) {
			caretPosition = Math.max(caretPosition - 1, 0);
		} else if (key == KeyEvent.VK_BACK_SPACE) {
			if (!getText().isEmpty() && caretPosition > 0) {
				text.deleteCharAt(caretPosition - 1);
				invalidate();
				caretPosition--;
			}
		} else if (key == KeyEvent.VK_HOME) {
			caretPosition = 0;
		} else if (key == KeyEvent.VK_END) {
			caretPosition = text.length();
		} else {
			char keyChar = e.getKeyChar();
			if (getFont().canDisplay(keyChar)) {
				text.insert(caretPosition, keyChar);
				caretPosition++;
				invalidate();
			}
		}
		setCaretPosition(caretPosition);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public String getText() {
		return text.toString();
	}

	@Override
	public void setText(String text) {
		this.text = new StringBuilder(text);
	}
}
