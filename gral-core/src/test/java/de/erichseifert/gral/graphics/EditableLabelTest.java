package de.erichseifert.gral.graphics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EditableLabelTest {
	private static EditableLabel label;

	@Before
	public void setUp() throws Exception {
		label = new EditableLabel();
	}

	@Test
	public void testEdited() throws Exception {
		label.setEdited(true);
		assertTrue(label.isEdited());

		label.setEdited(false);
		assertFalse(label.isEdited());
	}
}
