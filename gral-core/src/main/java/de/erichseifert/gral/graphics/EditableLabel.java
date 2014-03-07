package de.erichseifert.gral.graphics;

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
}
