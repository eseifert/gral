package de.erichseifert.gral.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.erichseifert.gral.util.GraphicsUtils;

public class EditableLabel extends Label implements KeyListener {
	private boolean edited;
	private int caretPosition;
	private int markPosition;
	private Paint selectionBackground;
	private StringBuilder text;
	private PropertyChangeSupport propertyChangeSupport;

	// TODO: Should superclass use StringBuilder?
	public EditableLabel() {
		this("");
	}

	public EditableLabel(String text) {
		super(text);
		this.text = new StringBuilder(text);
		selectionBackground = new Color(100, 199, 233, 120);
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public boolean isEdited() {
		return edited;
	}

	public void setEdited(boolean edited) {
		this.edited = edited;
		setCaretPosition(text.length());
		setMarkPosition(text.length());
	}

	protected int getCaretPosition() {
		return caretPosition;
	}

	protected void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}

	protected int getMarkPosition() {
		return markPosition;
	}

	protected void setMarkPosition(int markPosition) {
		this.markPosition = markPosition;
	}

	public Paint getSelectionBackground() {
		return selectionBackground;
	}

	public void setSelectionBackground(Paint selectionBackground) {
		this.selectionBackground = selectionBackground;
	}

	@Override
	public void drawComponents(DrawingContext context) {
		if (isEdited()) {
			drawCaret(context);
		}
		super.drawComponents(context);
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
		Shape selectionShape = layout.getLogicalHighlightShape(getMarkPosition(), getCaretPosition());
		Shape[] caretShapes = layout.getCaretShapes(caretPosition);

		// Apply positioning
		g2d.translate(0, layout.getAscent());

		GraphicsUtils.fillPaintedShape(g2d, selectionShape, getSelectionBackground(), null);
		g2d.draw(caretShapes[0]);
		if (caretShapes[1] != null) {
			g2d.draw(caretShapes[1]);
		}
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
		int markPosition = getMarkPosition();
		/*
		 * Bring start and end in correct order. Otherwise StringBuilder.delete will throw
		 * an ArrayIndexOutOfBoundsException.
		 */
		int startIndex = Math.min(markPosition, caretPosition);
		int endIndex = Math.max(markPosition, caretPosition);
		if (key == KeyEvent.VK_RIGHT) {
			caretPosition = Math.min(caretPosition + 1, text.length());
			if (!e.isShiftDown()) {
				markPosition = caretPosition;
			}
		} else if (key == KeyEvent.VK_LEFT) {
			caretPosition = Math.max(caretPosition - 1, 0);
			if (!e.isShiftDown()) {
				markPosition = caretPosition;
			}
		} else if (key == KeyEvent.VK_BACK_SPACE) {
			if (!getText().isEmpty()) {
				String textOld = getText();
				if (startIndex != endIndex) {
					// Delete multiple characters
					text.delete(startIndex, endIndex);
					caretPosition = startIndex;
					propertyChangeSupport.firePropertyChange("text", textOld, getText());
					invalidate();
				} else if (startIndex > 0) {
					// Delete single character
					text.deleteCharAt(caretPosition - 1);
					caretPosition--;
					propertyChangeSupport.firePropertyChange("text", textOld, getText());
					invalidate();
				}
				markPosition = caretPosition;
			}
		} else if (key == KeyEvent.VK_HOME) {
			caretPosition = 0;
			if (!e.isShiftDown()) {
				markPosition = caretPosition;
			}
		} else if (key == KeyEvent.VK_END) {
			caretPosition = text.length();
			if (!e.isShiftDown()) {
				markPosition = caretPosition;
			}
		} else if (key == KeyEvent.VK_A && e.isControlDown()) {
			markPosition = 0;
			caretPosition = text.length();
		} else {
			char keyChar = e.getKeyChar();
			if (getFont().canDisplay(keyChar)) {
				String textOld = getText();
				text.replace(startIndex, endIndex, String.valueOf(keyChar));
				caretPosition = startIndex + 1;
				markPosition = caretPosition;
				invalidate();
				propertyChangeSupport.firePropertyChange("text", textOld, getText());
			}
		}
		setCaretPosition(caretPosition);
		setMarkPosition(markPosition);
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
		String textOld = getText();
		this.text = new StringBuilder(text);
		propertyChangeSupport.firePropertyChange("text", textOld, getText());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
