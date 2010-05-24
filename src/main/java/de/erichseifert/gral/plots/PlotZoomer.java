package de.erichseifert.gral.plots;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.SettingsStorage;

/**
 * Class that controls the zoom of a Plot.
 */
public class PlotZoomer implements SettingsStorage, SettingsListener {
	/** Boolean value that specified whether zooming the x-axis is enabled or not. */
	public static final String KEY_ZOOM_X_ENABLED = "plotzoomer.zoomxenabled";
	/** Boolean value that specified whether zooming the y-axis is enabled or not. */
	public static final String KEY_ZOOM_Y_ENABLED = "plotzoomer.zoomyenabled";

	private final Settings settings;

	private final Plot plot;
	private final Number minX;
	private final Number maxX;
	private final Number minY;
	private final Number maxY;

	private double zoomStepX;
	private double zoomStepY;
	private Point2D center;
	private int zoomFactorX;
	private int zoomFactorY;
	private double thresholdMin;
	private double thresholdMax;

	/**
	 * Creates a new <code>PlotZoomer</code> object that is responsible for the specified plot.
	 * @param plot Plot to be zoomed.
	 */
	public PlotZoomer(Plot plot) {
		settings = new Settings(this);
		setSettingDefault(KEY_ZOOM_X_ENABLED, true);
		setSettingDefault(KEY_ZOOM_Y_ENABLED, true);

		this.plot = plot;
		Axis axisX = plot.getAxis(Axis.X);
		minX = axisX.getMin();
		maxX = axisX.getMax();
		Axis axisY = plot.getAxis(Axis.Y);
		minY = axisY.getMin();
		maxY = axisY.getMax();

		zoomStepX = axisX.getRange() * 0.125;
		zoomStepY = axisY.getRange() * 0.125;
		thresholdMin = 1e-2;
		thresholdMax = 1e2;
		Rectangle2D plotAreaBounds = plot.getPlotArea().getBounds();
		center = new Point2D.Double(plotAreaBounds.getCenterX(), plotAreaBounds.getCenterY());
	}

	/**
	 * Sets the plot's zoom to the specified center and zooms it according to the specified values.
	 * @param center Center of the zoom.
	 * @param stepsX Number of steps to zoom on the x-axis.
	 * @param stepsY Number of steps to zoom on the y-axis.
	 */
	public void zoom(Point2D center, int stepsX, int stepsY) {
		double posRelX = center.getX() / plot.getPlotArea().getWidth();
		double posRelY = center.getY() / plot.getPlotArea().getHeight();

		int zoomDiffX = zoomFactorX + stepsX;
		int zoomDiffY = zoomFactorY + stepsY;

		if (getSetting(KEY_ZOOM_X_ENABLED)) {
			Number minXNew = minX.doubleValue() + zoomDiffX * zoomStepX * posRelX;
			Number maxXNew = maxX.doubleValue() - zoomDiffX * zoomStepX * (1-posRelX);
			double rangeNew = maxXNew.doubleValue() - minXNew.doubleValue();
			if (rangeNew < thresholdMin || rangeNew > thresholdMax) {
				return;
			}
			Axis axisX = plot.getAxis(Axis.X);
			axisX.setRange(minXNew, maxXNew);
			zoomFactorX += stepsX;
			zoomStepX = axisX.getRange() * 0.125;
		}
		if (getSetting(KEY_ZOOM_Y_ENABLED)) {
			Number minYNew = minY.doubleValue() + zoomDiffY * zoomStepY * (1-posRelY);
			Number maxYNew = maxY.doubleValue() - zoomDiffY * zoomStepY * posRelY;
			double rangeNew = maxYNew.doubleValue() - minYNew.doubleValue();
			if (rangeNew < thresholdMin || rangeNew > thresholdMax) {
				return;
			}
			Axis axisY = plot.getAxis(Axis.Y);
			axisY.setRange(minYNew, maxYNew);
			zoomFactorY += stepsY;
			zoomStepY = axisY.getRange() * 0.125;
		}

		this.center = center;
	}

	/**
	 * Resets the plot's zoom to the original value.
	 */
	public void reset() {
		Axis axisX = plot.getAxis(Axis.X);
		axisX.setRange(minX, maxX);
		Axis axisY = plot.getAxis(Axis.Y);
		axisY.setRange(minY, maxY);

		zoomStepX = axisX.getRange() * 0.125;
		zoomStepY = axisY.getRange() * 0.125;
		zoomFactorX = 0;
		zoomFactorY = 0;
		Rectangle2D plotAreaBounds = plot.getPlotArea().getBounds();
		center.setLocation(plotAreaBounds.getCenterX(), plotAreaBounds.getCenterY());
	}

	public double getZoomStepX() {
		return zoomStepX;
	}

	public double getZoomStepY() {
		return zoomStepY;
	}

	public int getZoomFactorX() {
		return zoomFactorX;
	}

	public int getZoomFactorY() {
		return zoomFactorY;
	}

	public double getThresholdMin() {
		return thresholdMin;
	}

	public void setZoomStepX(double zoomStepX) {
		this.zoomStepX = zoomStepX;
	}

	public void setZoomStepY(double zoomStepY) {
		this.zoomStepY = zoomStepY;
	}

	public void setThresholdMin(double thresholdMin) {
		this.thresholdMin = thresholdMin;
	}

	public Point2D getCenter() {
		return center;
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
		settings.set(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}

	public double getThresholdMax() {
		return thresholdMax;
	}

	public void setThresholdMax(double thresholdMax) {
		this.thresholdMax = thresholdMax;
	}
}