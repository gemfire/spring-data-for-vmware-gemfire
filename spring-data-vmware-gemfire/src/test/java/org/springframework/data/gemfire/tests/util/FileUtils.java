/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract utility class for processing file system files by using {@link File} objects.
 *
 * @author John Blum
 * @see File
 * @see IOUtils
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class FileUtils extends IOUtils {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static boolean isDirectory(File path) {
		return path != null && path.isDirectory();
	}

	public static boolean isFile(File path) {
		return path != null && path.isFile();
	}

	public static @NonNull File newFile(String pathname) {
		return new File(pathname);
	}

	public static @NonNull File newFile(File parent, String pathname) {
		return new File(parent, pathname);
	}

	public static long nullSafeLength(File path) {
		return path != null ? path.length() : 0L;
	}

	public static @NonNull String read(@NonNull File file) throws IOException {

		Assert.isTrue(isFile(file), String.format("The File [%s] to read the contents from is not a valid file", file));

		try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {

			StringBuilder buffer = new StringBuilder();

			for (String line = fileReader.readLine(); line != null; line = fileReader.readLine()) {
				buffer.append(line);
				buffer.append(LINE_SEPARATOR);
			}

			return buffer.toString().trim();
		}
	}

	public static void write(@NonNull File file, @NonNull String contents) throws IOException {

		Assert.notNull(file, "File is required");

		Assert.isTrue(StringUtils.hasText(contents),
			String.format("The contents for File [%1$s] cannot be null or empty", file));

		BufferedWriter fileWriter = null;

		try {
			fileWriter = new BufferedWriter(new FileWriter(file));
			fileWriter.write(contents);
			fileWriter.flush();
		}
		finally {
			IOUtils.close(fileWriter);
		}
	}
}
