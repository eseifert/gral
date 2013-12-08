/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.data;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class DataTableConcurrencyTest {
	private DataTable table;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		table = new DataTable(Integer.class, Integer.class);
	}

	private static class DataTableReader extends Thread {
		private final DataTable table;
		public final AtomicInteger read;

		public DataTableReader(DataTable table) {
			this.table = table;
			read = new AtomicInteger();
		}

		@Override
		public void run() {
			int cols = table.getColumnCount();
			int rows = table.getRowCount();

			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					table.get(col, row);
					read.incrementAndGet();
				}
			}
		}
	}

	private static class DataTableProducer extends Thread {
		private static final Random random = new Random();
		private final DataTable table;

		public DataTableProducer(DataTable table) {
			this.table = table;
		}

		@Override
		public void run() {
			// Add row
			int value1 = random.nextInt();
			int value2 = random.nextInt();
			int row = table.add(value1, value2);

			// Verify
			assertEquals(value1, table.get(0, row));
			assertEquals(value2, table.get(1, row));
		}
	}

	private static class DataTableConsumer extends Thread {
		private final MutableDataSource table;

		public DataTableConsumer(MutableDataSource table) {
			this.table = table;
		}

		@Override
		public void run() {
			// Remove the first row
			table.remove(0);
		}
	}

	@Test
	public void testConcurrentRead() throws InterruptedException {
		table.add(1,  1); // 0
		table.add(2,  3); // 1
		table.add(3,  2); // 2
		table.add(4,  6); // 3
		table.add(5,  4); // 4
		table.add(6,  8); // 5
		table.add(7,  9); // 6
		table.add(8, 11); // 7

		DataTableReader[] readers = new DataTableReader[100];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = new DataTableReader(table);
		}
		for (DataTableReader reader : readers) {
			reader.start();
		}
		for (DataTableReader reader : readers) {
			reader.join();
		}
	}

	@Test
	public void testConcurrentWrite() throws InterruptedException {
		DataTableProducer[] producers = new DataTableProducer[100];
		for (int i = 0; i < producers.length; i++) {
			producers[i] = new DataTableProducer(table);
		}
		for (DataTableProducer producer : producers) {
			producer.start();
		}
		for (DataTableProducer producer : producers) {
			producer.join();
		}

		assertEquals(producers.length, table.getRowCount());
	}

	@Test
	public void testConcurrentReadWrite() throws InterruptedException {
		DataTableProducer[] producers = new DataTableProducer[100];
		for (int i = 0; i < producers.length; i++) {
			producers[i] = new DataTableProducer(table);
		}

		DataTableReader[] readers = new DataTableReader[100];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = new DataTableReader(table);
		}

		for (DataTableProducer producer : producers) {
			producer.start();
		}
		for (DataTableReader reader : readers) {
			reader.start();
		}

		for (DataTableProducer producer : producers) {
			producer.join();
		}
		for (DataTableReader reader : readers) {
			reader.join();
		}

		assertEquals(producers.length, table.getRowCount());
	}

	@Test
	public void testConcurrentProduceConsume() throws InterruptedException {
		DataTableProducer[] producers = new DataTableProducer[100];
		for (int i = 0; i < producers.length; i++) {
			producers[i] = new DataTableProducer(table);
		}

		DataTableConsumer[] consumers = new DataTableConsumer[100];
		for (int i = 0; i < consumers.length; i++) {
			consumers[i] = new DataTableConsumer(table);
		}

		for (DataTableProducer producer : producers) {
			producer.start();
		}
		for (DataTableConsumer consumer : consumers) {
			consumer.start();
		}

		for (DataTableProducer producer : producers) {
			producer.join();
		}
		for (DataTableConsumer consumer : consumers) {
			consumer.join();
		}

		assertEquals(0, table.getRowCount());
	}
}
