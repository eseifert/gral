package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EditableLabelTest {
	private static EditableLabel label;
	private static DrawingContext drawingContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		Image image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		drawingContext = new DrawingContext((Graphics2D) image.getGraphics());
	}

	@Before
	public void setUp() throws Exception {
		label = new EditableLabel();
	}

	@Test
	public void testCreation() {
		EditableLabel label = new EditableLabel();
	}

	@Test
	public void testDrawEmptyLabel() {
		label.setText("");

		// Ensure that the label can be drawn while being edited
		label.setEdited(true);
		label.draw(drawingContext);
	}

	@Test
	public void testEdited() throws Exception {
		label.setEdited(true);
		assertTrue(label.isEdited());

		label.setEdited(false);
		assertFalse(label.isEdited());
	}
}
