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
package de.erichseifert.gral.io.data;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.IOCapabilities;
import de.erichseifert.gral.util.Messages;


/**
 * Class that reads a data source from a binary image file. This class
 * shouldn't be used directly but using the {@link DataReaderFactory}.
 */
public class AudioReader extends AbstractDataReader {
	static {
		addCapabilities(new IOCapabilities(
			"WAV", //$NON-NLS-1$
			Messages.getString("DataIO.wavDescription"), //$NON-NLS-1$
			"audio/wav", //$NON-NLS-1$
			new String[] {"wav"} //$NON-NLS-1$
		));
	}

	/**
	 * Creates a new instance with the specified MIME type.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public AudioReader(String mimeType) {
		super(mimeType);
		setDefault("factor", 1.0); //$NON-NLS-1$
		setDefault("offset", 0.0); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataSource read(InputStream input, Class<? extends Number>... types)
			throws IOException {
		AudioInputStream audio;
		try {
			audio = AudioSystem.getAudioInputStream(input);
		} catch (UnsupportedAudioFileException e) {
			throw new IOException(e);
		}

		// FIXME Should the types parameter be used?
		DataTable data = new DataTable(Double.class);

		double factor = this.<Number>getSetting("factor") //$NON-NLS-1$
			.doubleValue();
		double offset = this.<Number>getSetting("offset") //$NON-NLS-1$
			.doubleValue();

		int sampleSize = audio.getFormat().getSampleSizeInBits();
		byte[] samples = new byte[sampleSize/8];

		// see: http://www.jsresources.org/faq_audio.html#reconstruct_samples
		while (audio.read(samples) >= 0) {
			int b = samples[0];
			if (samples.length == 1) {
				b = b << 8;
			} else if (samples.length == 2) {
				b = (b & 0xFF) | (samples[1] << 8);
			}
			double v = factor*b + offset;
			data.add(v);
		}

		return data;
	}

}
