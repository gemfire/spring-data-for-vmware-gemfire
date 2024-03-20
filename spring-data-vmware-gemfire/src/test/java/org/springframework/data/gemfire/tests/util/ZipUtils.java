/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * The {@link ZipUtils} class is an abstract utility class for working with JAR and ZIP archives.
 *
 * @author John Blum
 * @see File
 * @see ZipFile
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class ZipUtils {

	public static void unzip(final Resource zipResource, final File directory) throws IOException {

		Assert.notNull(zipResource, "ZIP Resource is required");

		Assert.isTrue(directory != null && directory.isDirectory(),
			String.format("The file system pathname (%1$s) is not a valid directory!", directory));

		ZipFile zipFile = new ZipFile(zipResource.getFile(), ZipFile.OPEN_READ);

		for (ZipEntry entry : CollectionUtils.iterable(zipFile.entries())) {

			if (entry.isDirectory()) {
				new File(directory, entry.getName()).mkdirs();
			}
			else {

				DataInputStream entryInputStream = new DataInputStream(zipFile.getInputStream(entry));

				DataOutputStream entryOutputStream = new DataOutputStream(new FileOutputStream(
					new File(directory, entry.getName())));

				try {
					FileCopyUtils.copy(entryInputStream, entryOutputStream);
				}
				finally {
					IOUtils.close(entryInputStream);
					IOUtils.close(entryOutputStream);
				}
			}
		}
	}
}
