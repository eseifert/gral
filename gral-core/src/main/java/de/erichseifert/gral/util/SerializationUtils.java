/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.util;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * <p>Helpers for serializing the AWT types that are not themselves
 * serializable. Plots and data sources are fully serializable, and this is how
 * they keep their strokes, shapes and points across a round trip.</p>
 *
 * <p>A field of such a type is declared {@code transient} and handled by hand
 * in a pair of methods:</p>
 * <pre>
 * private transient Stroke borderStroke;
 *
 * private void writeObject(ObjectOutputStream out) throws IOException {
 *     out.defaultWriteObject();
 *     out.writeObject(SerializationUtils.wrap(borderStroke));
 * }
 *
 * private void readObject(ObjectInputStream in)
 *         throws ClassNotFoundException, IOException {
 *     in.defaultReadObject();
 *     borderStroke = (Stroke) SerializationUtils.unwrap(
 *         (Serializable) in.readObject());
 * }
 * </pre>
 *
 * <p>{@link #wrap(Object)} passes objects that are already serializable
 * through unchanged and substitutes a stand-in for the others, so it is safe to
 * apply to anything; {@link #unwrap(Serializable)} reverses it. Wrappers exist
 * for {@code BasicStroke}, {@code Shape}, {@code Area} and {@code Point2D}; an
 * object of some other non-serializable type is returned as it is and will
 * still fail to serialize.</p>
 *
 * <p>Forgetting this for a newly added field is easy and fails silently until
 * something is actually serialized, which is why every serializable class in
 * GRAL has a round-trip test.</p>
 */
public abstract class SerializationUtils {
	/**
	 * Default constructor that prevents creation of class.
	 */
	private SerializationUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Makes sure an object is serializable, otherwise a serializable wrapper
	 * will be returned.
	 * @param o Object to be serialized.
	 * @return A serializable object, or a serializable wrapper.
	 */
	public static Serializable wrap(Object o) {
		if (o == null || o instanceof Serializable) {
			return (Serializable) o;
		}

		// See Java bug 4305099:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4305099
		if (o instanceof BasicStroke) {
			BasicStroke stroke = (BasicStroke) o;
			return new SerializableBasicStroke(stroke);
		}

		// See Java bug 4263142 until Java 1.6:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4263142
		if ((o instanceof Point2D.Double) || (o instanceof Point2D.Float)) {
			Point2D point = (Point2D) o;
			return new SerializablePoint2D(point);
		}

		if (o instanceof Area) {
			Area area = (Area) o;
			return new SerializableArea(area);
		}

		if (o instanceof Shape) {
			Shape shape = (Shape) o;
			return new SerializableShape(shape);
		}

		throw new IllegalArgumentException(String.format(
			"Failed to make value of type %s serializable.",
			o.getClass().getName()
		));
	}

	/**
	 * Makes sure a regular object is returned, wrappers for serialization will
	 * be removed.
	 * @param o Deserialized object.
	 * @return A regular (unwrapped) object.
	 */
	public static Object unwrap(Serializable o) {
		if (o instanceof SerializationWrapper<?>) {
			SerializationWrapper<?> wrapper = (SerializationWrapper<?>) o;
			return wrapper.unwrap();
		}
		return o;
	}
}
