/*
 * Copyright 2023-2026 Broadcom. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vmware.gemfire.driver.spi;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Native-free layout adapted from GemFireFormatter and LogWriterImpl.formatText.
 * The date formatter captures the default locale/time zone at construction. Full formatting
 * preserves the raw message; inherited formatMessage resolves bundles and parameters for publish.
 */
final class DriverLogFormatter extends Formatter {

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS z");

	@Override public synchronized String format(LogRecord record) {
		var output = new StringWriter();
		try (var writer = new PrintWriter(output)) {
			writer.println();
			writer.print('['); writer.print(record.getLevel().getName()); writer.print(' ');
			writer.print(dateFormat.format(new Date(record.getMillis())));
			var thread = Thread.currentThread();
			if (thread.getName() != null) { writer.print(' '); writer.print(thread.getName()); }
			writer.print(" tid=0x"); writer.print(Long.toHexString(thread.getId()));
			writer.print("] (msgTID="); writer.print(record.getThreadID());
			writer.print(" msgSN="); writer.print(record.getSequenceNumber()); writer.print(") ");
			String message = record.getMessage();
			if (message == null) writer.println();
			else {
				try { formatText(writer, message); }
				catch (RuntimeException failure) {
					writer.println(message);
					writer.println("Ignoring the following exception:");
					failure.printStackTrace(writer);
				}
			}
			if (record.getThrown() != null) record.getThrown().printStackTrace(writer);
		}
		return output.toString();
	}

	private void formatText(PrintWriter writer, String message) {
		BreakIterator boundary = BreakIterator.getLineInstance();
		boundary.setText(message);
		int start = boundary.first();
		int end = boundary.next();
		int lineLength = 40;
		while (end != BreakIterator.DONE) {
			char endChar = message.charAt(end - 1);
			while (!Character.isWhitespace(endChar)) {
				int lastEnd = end;
				end = boundary.next();
				if (end == BreakIterator.DONE) { end = lastEnd; break; }
				endChar = message.charAt(end - 1);
			}
			int wordEnd = end;
			if (endChar == '\n') {
				wordEnd--;
				if (wordEnd > 0 && message.charAt(wordEnd - 1) == '\r') wordEnd--;
			} else if (endChar == '\t') lineLength += 7;
			String word = message.substring(start, wordEnd);
			lineLength += word.length();
			writer.print(word);
			if (endChar == '\n' || endChar == '\r') {
				writer.println(); writer.print("  "); lineLength = 2;
			}
			start = end;
			end = boundary.next();
		}
		if (lineLength != 0) writer.println();
	}
}
