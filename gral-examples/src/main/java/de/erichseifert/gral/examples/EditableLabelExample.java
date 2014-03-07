package de.erichseifert.gral.examples;

import de.erichseifert.gral.graphics.EditableLabel;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;


public class EditableLabelExample extends ExamplePanel {

	public EditableLabelExample() {
		EditableLabel label = new EditableLabel("Editable label");
		label.setFont(getFont().deriveFont(20f));
		label.setEdited(true);

		DrawablePanel panel = new InteractivePanel(label);
		add(panel);
	}

	@Override
	public String getTitle() {
		return "Editable label example";
	}

	@Override
	public String getDescription() {
		return "Label with editable content.";
	}

	public static void main(String[] args) {
		new EditableLabelExample().showInFrame();
	}
}
