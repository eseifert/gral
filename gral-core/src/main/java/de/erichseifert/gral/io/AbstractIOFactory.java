/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
package de.erichseifert.gral.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Abstract implementation of <code>IOFactory</code> which provides basic
 * functionality.
 *
 * @param <T> The type of objects which should be produced by this factory
 */
public abstract class AbstractIOFactory<T> implements IOFactory<T> {
	private final Map<String, Class<? extends T>> entries;

	/**
	 * Constructor that creates a new instance and initializes it with the name
	 * of the corresponding properties file(s).
	 * @param propFileName File name of the properties file(s)
	 * @throws IOException if reading the properties file(s) failed
	 */
	protected AbstractIOFactory(String propFileName) throws IOException {
		entries = new HashMap<String, Class<? extends T>>();

		// Retrieve property-files
		Enumeration<URL> propFiles = null;
		propFiles = getClass().getClassLoader().getResources(propFileName);
		if (!propFiles.hasMoreElements()) {
			throw new IOException(MessageFormat.format(
				"Property file not found: {0}", propFileName)); //$NON-NLS-1$
		}
		Properties props = new Properties();
		while (propFiles.hasMoreElements()) {
			URL propURL = propFiles.nextElement();
			InputStream stream = null;
			try {
				stream = propURL.openStream();
				props.load(stream);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
			// Parse property files and register entries as items
			for (Map.Entry<Object, Object> prop : props.entrySet()) {
				String mimeType = (String) prop.getKey();
				String className = (String) prop.getValue();
				Class<?> clazz = null;
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new IOException(e);
				}
				// FIXME Missing type safety check
				entries.put(mimeType, (Class<? extends T>) clazz);
			}
		}
	}

	@Override
	public IOCapabilities getCapabilities(String mimeType) {
		Class<? extends T> clazz = entries.get(mimeType);
		try {
			Method capabilitiesGetter =
				clazz.getMethod("getCapabilities"); //$NON-NLS-1$
			Set<IOCapabilities> capabilities =
				(Set<IOCapabilities>) capabilitiesGetter.invoke(clazz);
			for (IOCapabilities c : capabilities) {
				if (c.getMimeType().equals(mimeType)) {
					return c;
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<IOCapabilities> getCapabilities() {
		List<IOCapabilities> caps =
			new ArrayList<IOCapabilities>(entries.size());
		for (String mimeType : entries.keySet()) {
			IOCapabilities capability = getCapabilities(mimeType);
			if (capability != null) {
				caps.add(capability);
			}
		}
		return caps;
	}

	@Override
	public String[] getSupportedFormats() {
		String[] formats = new String[entries.size()];
		entries.keySet().toArray(formats);
		return formats;
	}

	@Override
	public boolean isFormatSupported(String mimeType) {
		return entries.containsKey(mimeType);
	}

	/**
	 * Returns the type of factory products for a specified format.
	 * @param type Format.
	 * @return Class type to create new instances.
	 */
	protected Class<? extends T> getTypeClass(String type) {
		return entries.get(type);
	}
}
