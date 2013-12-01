package de.erichseifert.gral.plots.areas;

import static org.junit.Assert.assertEquals;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;

public class AbstractAreaRendererTest {
	private static final double DELTA = TestUtils.DELTA;

	private static class MockAbstractAreaRenderer extends AbstractAreaRenderer {
		public MockAbstractAreaRenderer() {
		}

		@Override
		public Shape getAreaShape(List<DataPoint> points) {
			return new Rectangle2D.Float(0, 0, 10, 10);
		}

		@Override
		public Drawable getArea(List<DataPoint> points, final Shape shape) {
			return new AbstractDrawable() {
				@Override
				public void draw(DrawingContext context) {
					context.getGraphics().draw(shape);
				}
			};
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AreaRenderer original = new MockAbstractAreaRenderer();
		AreaRenderer deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getGap(), deserialized.getGap(), DELTA);
		assertEquals(original.isGapRounded(), deserialized.isGapRounded());
		assertEquals(original.getColor(), deserialized.getColor());
	}
}
