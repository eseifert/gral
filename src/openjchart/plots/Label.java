package openjchart.plots;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.util.*;

public class Label extends AbstractDrawable implements SettingsStorage, SettingsListener {
	public static final String KEY_ALIGNMENT_X = "label.alignment.x";
	public static final String KEY_ALIGNMENT_Y = "label.alignment.x";
	public static final String KEY_FONT = "label.font";
	public static final String KEY_FONT_RENDER_CONTEXT = "label.fontrendercontext";

	private final Settings settings;
	private String text;
	private TextLayout layout;

	public Label(String text) {
		this.settings = new Settings();
		this.text = text;

		setSettingDefault(KEY_ALIGNMENT_X, 0.5);
		setSettingDefault(KEY_ALIGNMENT_Y, 0.5);
		this.settings.setDefault(KEY_FONT, new Font("Arial", Font.PLAIN, 12));
		this.settings.setDefault(KEY_FONT_RENDER_CONTEXT, new FontRenderContext(null, true, true));
		renewLayout();
	}

	@Override
	public void draw(Graphics2D g2d) {
		Font fontOld = g2d.getFont();

		g2d.setFont(Label.this.<Font>getSetting(KEY_FONT));
		FontMetrics metrics = g2d.getFontMetrics();
		int textWidth = metrics.stringWidth(text);
		int textHeight = metrics.getHeight();
		float x = (float) (getX() + (getWidth() - textWidth)*Label.this.<Double>getSetting(KEY_ALIGNMENT_X));
		float y = (float) (getY() + (getHeight() - textHeight/2.0)*Label.this.<Double>getSetting(KEY_ALIGNMENT_Y));

		g2d.drawString(text, x, y);

		g2d.setFont(fontOld);
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
			this.layout = new TextLayout(text, this.<Font>getSetting(KEY_FONT), this.<FontRenderContext>getSetting(KEY_FONT_RENDER_CONTEXT));

			// Set width and height
			Rectangle2D layoutBounds = this.layout.getBounds();
			setBounds(getX(), getY(), layoutBounds.getWidth(), layoutBounds.getHeight());
		}
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		String key = event.getKey();
		if (KEY_FONT.equals(key) || KEY_FONT_RENDER_CONTEXT.equals(key)) {
			renewLayout();
		}
	}
}
