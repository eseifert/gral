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
package de.erichseifert.gral.javafx;

import static org.junit.Assume.assumeFalse;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * <p>Starts the JavaFX toolkit for the tests and runs code on its application
 * thread.</p>
 *
 * <p>Every JavaFX node has to be created and used on that thread, and the
 * toolkit needs a display, so the tests skip themselves when there is none in
 * the same way {@code ExportDialogTest} of the Swing module does.</p>
 */
final class JavaFxToolkit {
	/** How long a test waits for the toolkit and for the thread. */
	private static final int TIMEOUT_SECONDS = 30;

	/** Whether the toolkit has been started already. */
	private static boolean started;

	/**
	 * Private constructor.
	 */
	private JavaFxToolkit() {
	}

	/**
	 * Skips the calling test when no display is available, and starts the
	 * JavaFX toolkit otherwise. Starting it more than once is a no-op.
	 * @throws InterruptedException when the wait for the toolkit is
	 *         interrupted.
	 */
	public static synchronized void start() throws InterruptedException {
		assumeFalse(GraphicsEnvironment.isHeadless());
		if (started) {
			return;
		}
		var startup = new CountDownLatch(1);
		Platform.startup(startup::countDown);
		startup.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		// Keep the toolkit alive after the last window has been closed.
		Platform.setImplicitExit(false);
		started = true;
	}

	/**
	 * Runs the specified code on the JavaFX application thread and returns its
	 * result.
	 * @param <T> Type of the result.
	 * @param callable Code to run.
	 * @return The result of the code.
	 * @throws Exception when the code fails, or the wait for it does.
	 */
	public static <T> T call(Callable<T> callable) throws Exception {
		var task = new FutureTask<>(callable);
		Platform.runLater(task);
		return task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
	}
}
