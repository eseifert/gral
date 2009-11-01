package openjchart.plots;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.util.Dimension2D;
import openjchart.util.Settings;
import openjchart.util.SettingsStorage;

public class Label extends AbstractDrawable implements SettingsStorage {
	public static final String KEY_ALIGNMENT = "label.alignment";
	public static final String KEY_FONT = "label.font";
	public static final String KEY_FONT_RENDER_CONTEXT = "label.fontrendercontext";

	private final Settings settings;
	private String text;
	private TextLayout layout;

	public Label(String text) {
		this.settings = new Settings();
		this.text = text;

		setSettingDefault(KEY_ALIGNMENT, 0.5);
		setSettingDefault(KEY_FONT, new Font("Arial", Font.PLAIN, 12));
		setSettingDefault(KEY_FONT_RENDER_CONTEXT, new FontRenderContext(null, true, true));
		if (text != null && !text.isEmpty()) {
			this.layout = new TextLayout(text, this.<Font>getSetting(KEY_FONT), this.<FontRenderContext>getSetting(KEY_FONT_RENDER_CONTEXT));
		}
	}

	@Override
	public void draw(Graphics2D g2d) {
		Font fontOld = g2d.getFont();

		g2d.setFont(Label.this.<Font>getSetting(KEY_FONT));
		FontMetrics metrics = g2d.getFontMetrics();
		int titleWidth = metrics.stringWidth(text);
		float x = (float) (getX() + (getWidth() - titleWidth)*Label.this.<Double>getSetting(KEY_ALIGNMENT));
		float y = (float) (getY() + getHeight()/2.0);

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
		if (KEY_FONT.equals(key) || KEY_FONT_RENDER_CONTEXT.equals(key)) {
			if (text != null && !text.isEmpty()) {
				this.layout = new TextLayout(text, this.<Font>getSetting(KEY_FONT), this.<FontRenderContext>getSetting(KEY_FONT_RENDER_CONTEXT));
			}
		}
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
		if (KEY_FONT.equals(key) || KEY_FONT_RENDER_CONTEXT.equals(key)) {
			if (text != null && !text.isEmpty()) {
				this.layout = new TextLayout(text, this.<Font>getSetting(KEY_FONT), this.<FontRenderContext>getSetting(KEY_FONT_RENDER_CONTEXT));
			}
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		if (text != null && !text.isEmpty()) {
			this.layout = new TextLayout(text, this.<Font>getSetting(KEY_FONT), this.<FontRenderContext>getSetting(KEY_FONT_RENDER_CONTEXT));
		}
	}
}
