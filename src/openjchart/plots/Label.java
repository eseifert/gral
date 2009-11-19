package openjchart.plots;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.util.Dimension2D;
import openjchart.util.GraphicsUtils;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

public class Label extends AbstractDrawable implements SettingsStorage, SettingsListener {
	public static final String KEY_ALIGNMENT_X = "label.alignment.x";
	public static final String KEY_ALIGNMENT_Y = "label.alignment.x";
	public static final String KEY_FONT = "label.font";

	private final Settings settings;
	private String text;
	private TextLayout layout;

	public Label(String text) {
		settings = new Settings();
		settings.addSettingsListener(this);
		this.text = text;

		setSettingDefault(KEY_ALIGNMENT_X, 0.5);
		setSettingDefault(KEY_ALIGNMENT_Y, 0.5);
		setSettingDefault(KEY_FONT, new Font("Arial", Font.PLAIN, 12));
	}

	@Override
	public void draw(Graphics2D g2d) {
		if (layout == null) {
			return;
		}
		Rectangle2D textBounds = layout.getBounds();
		double alignmentX = getSetting(KEY_ALIGNMENT_X);
		double alignmentY = getSetting(KEY_ALIGNMENT_Y);
		double x = getX() + (getWidth() - textBounds.getWidth())*alignmentX;
		double y = getY() + (getHeight() - textBounds.getHeight())*alignmentY;

		layout.draw(g2d, (float)x, (float)y);

		/*
		// DEBUG:
		AffineTransform txOld = g2d.getTransform();
		g2d.translate(0, -getHeight());
		g2d.draw(getBounds());
		g2d.setTransform(txOld);
		//*/
	}

	@Override
	public Dimension2D getPreferredSize() {
		Dimension2D d = super.getPreferredSize();
		if (layout != null) {
			Rectangle2D bounds = layout.getBounds();
			d.setSize(bounds.getWidth(), bounds.getHeight());
		}
		return d;
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		renewLayout();
	}

	private void renewLayout() {
		if (text != null && !text.isEmpty()) {
			layout = GraphicsUtils.getLayout(text, this.<Font>getSetting(KEY_FONT));
			Rectangle2D layoutBounds = layout.getBounds();
			setBounds(getX(), getY(), layoutBounds.getWidth(), layoutBounds.getHeight());
		}
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		String key = event.getKey();
		if (KEY_FONT.equals(key)) {
			renewLayout();
		}
	}
}
