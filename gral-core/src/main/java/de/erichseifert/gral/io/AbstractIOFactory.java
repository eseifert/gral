/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
 * Abstract implementation of {@code IOFactory} which provides basic
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
	@SuppressWarnings("unchecked")
	protected AbstractIOFactory(String propFileName) throws IOException {
		entries = new HashMap<>();

		// Retrieve property-files
		Enumeration<URL> propFiles;
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
				Class<?> clazz;
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

	/**
	 * Returns the capabilities for a specific format.
	 * @param mimeType MIME type of the format
	 * @return Capabilities for the specified format.
	 */
	@SuppressWarnings("unchecked")
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
		} catch (SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns a list of capabilities for all supported formats.
	 * @return Supported capabilities.
	 */
	public List<IOCapabilities> getCapabilities() {
		List<IOCapabilities> caps =
				new ArrayList<>(entries.size());
		for (String mimeType : entries.keySet()) {
			IOCapabilities capability = getCapabilities(mimeType);
			if (capability != null) {
				caps.add(capability);
			}
		}
		return caps;
	}

	/**
	 * Returns an array of Strings containing all supported formats.
	 * @return Supported formats.
	 */
	public String[] getSupportedFormats() {
		String[] formats = new String[entries.size()];
		entries.keySet().toArray(formats);
		return formats;
	}

	/**
	 * Returns whether the specified MIME type is supported.
	 * @param mimeType MIME type.
	 * @return {@code true} if supported, otherwise {@code false}.
	 */
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

	/**
	 * Returns an object for reading or writing the specified format.
	 * @param mimeType MIME type.
	 * @return Reader or writer for the specified MIME type.
	 */
	public T get(String mimeType) {
		// TODO Auto-generated method stub
		return null;
	}
}
