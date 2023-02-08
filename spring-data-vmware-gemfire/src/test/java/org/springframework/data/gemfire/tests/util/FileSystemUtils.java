/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.util;

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Abstract utility class encapsulating functionality to process file system directories and files collectively.
 *
 * @author John Blum
 * @see File
 * @see FileFilter
 * @see FileUtils
 * @see IOUtils
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class FileSystemUtils extends FileUtils {

	public static final File JAVA_HOME = new File(System.getProperty("java.home"));
	public static final File JAVA_EXE = new File(new File(JAVA_HOME, "bin"), "java");
	public static final File TEMPORARY_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
	public static final File USER_HOME = new File(System.getProperty("user.home"));
	public static final File WORKING_DIRECTORY = new File(System.getProperty("user.dir"));

	public static final File[] NO_FILES = new File[0];

	public static boolean deleteRecursive(@Nullable File path) {
		return deleteRecursive(path, AllFilesFilter.INSTANCE);
	}

	public static boolean deleteRecursive(@Nullable File path, @Nullable FileFilter fileFilter) {

		boolean success = true;

		if (isDirectory(path)) {
			for (File file : safeListFiles(path, fileFilter)) {
				success &= deleteRecursive(file);
			}
		}

		return ((!exists(path) || path.delete()) && success);
	}

	public static boolean exists(@Nullable File path) {
		return path != null && path.exists();
	}

	// returns subdirectory just below working directory
	public static @Nullable File getRootRelativeToWorkingDirectoryOrPath(@Nullable File path) {

		File localPath = path;

		if (isDirectory(localPath)) {
			while (localPath != null && !WORKING_DIRECTORY.equals(localPath.getParentFile())) {
				localPath = localPath.getParentFile();
			}
		}

		return localPath != null ? localPath : path;
	}

	public static boolean isEmpty(@Nullable File path) {

		return isDirectory(path)
			? ArrayUtils.isEmpty(path.listFiles())
			: nullSafeLength(path) == 0;
	}

	public static @NonNull File[] listFiles(@NonNull File directory, @NonNull FileFilter fileFilter) {

		Assert.isTrue(isDirectory(directory),
			String.format("File [%s] does not refer to a valid directory", directory));

		List<File> results = new ArrayList<>();

		for (File file : safeListFiles(directory, fileFilter)) {
			if (isDirectory(file)) {
				results.addAll(Arrays.asList(listFiles(file, fileFilter)));
			}
			else {
				results.add(file);
			}
		}

		return results.toArray(new File[0]);
	}

	public static @NonNull File[] safeListFiles(@Nullable File directory) {
		return safeListFiles(directory, AllFilesFilter.INSTANCE);
	}

	public static @NonNull File[] safeListFiles(@Nullable File directory, @Nullable FileFilter fileFilter) {

		FileFilter resolvedFileFilter = fileFilter != null ? fileFilter : AllFilesFilter.INSTANCE;

		File[] files = isDirectory(directory) ? directory.listFiles(resolvedFileFilter) : null;

		return files != null
			? files
			: NO_FILES;
	}

	public static class AllFilesFilter implements FileFilter {

		public static final AllFilesFilter INSTANCE = new AllFilesFilter();

		@Override
		public boolean accept(File pathname) {
			return true;
		}
	}

	public static class CompositeFileFilter implements FileFilter {

		private final FileFilter fileFilterOne;
		private final FileFilter fileFilterTwo;

		private final LogicalOperator logicalOperator;

		private CompositeFileFilter(FileFilter fileFilterOne, LogicalOperator operator, FileFilter fileFilterTwo) {
			this.fileFilterOne = fileFilterOne;
			this.logicalOperator = operator;
			this.fileFilterTwo = fileFilterTwo;
		}

		protected static FileFilter compose(FileFilter fileFilterOne, LogicalOperator operator, FileFilter fileFilterTwo) {
			return (fileFilterOne == null ? fileFilterTwo : (fileFilterTwo == null ? fileFilterOne
				: new CompositeFileFilter(fileFilterOne, operator, fileFilterTwo)));
		}

		public static FileFilter and(FileFilter... fileFilters) {
			return and(Arrays.asList(nullSafeArray(fileFilters, FileFilter.class)));
		}

		public static FileFilter and(Iterable<FileFilter> fileFilters) {
			FileFilter current = null;

			for (FileFilter fileFilter : nullSafeIterable(fileFilters)) {
				current = compose(current, LogicalOperator.AND, fileFilter);
			}

			return current;
		}

		public static FileFilter or(FileFilter... fileFilters) {
			return or(Arrays.asList(nullSafeArray(fileFilters, FileFilter.class)));
		}

		public static FileFilter or(Iterable<FileFilter> fileFilters) {
			FileFilter current = null;

			for (FileFilter fileFilter : nullSafeIterable(fileFilters)) {
				current = compose(current, LogicalOperator.OR, fileFilter);
			}

			return current;
		}

		@Override
		public boolean accept(File pathname) {

			switch (this.logicalOperator) {
				case AND:
					return (fileFilterOne.accept(pathname) && fileFilterTwo.accept(pathname));
				case OR:
					return (fileFilterOne.accept(pathname) || fileFilterTwo.accept(pathname));
				default:
					throw new UnsupportedOperationException(String.format(
						"Logical operator [%s] is unsupported", this.logicalOperator));
			}
		}

		enum LogicalOperator {
			AND, OR
		}
	}

	public static class DirectoryOnlyFilter implements FileFilter {

		public static final DirectoryOnlyFilter INSTANCE = new DirectoryOnlyFilter();

		@Override
		public boolean accept(File pathname) {
			return isDirectory(pathname);
		}
	}

	public static final class FileExtensionFilter extends FileOnlyFilter {

		private final String fileExtension;

		public static FileExtensionFilter newFileExtensionFilter(String fileExtension) {
			return new FileExtensionFilter(fileExtension);
		}

		public FileExtensionFilter(String fileExtension) {
			Assert.hasText(fileExtension, String.format("File extension [%s] must be specified", fileExtension));
			this.fileExtension = fileExtension;
		}

		@Override
		public boolean accept(File pathname) {
			return super.accept(pathname) && pathname.getAbsolutePath().toLowerCase().endsWith(this.fileExtension);
		}
	}

	public static class FileOnlyFilter implements FileFilter {

		public static final FileOnlyFilter INSTANCE = new FileOnlyFilter();

		@Override
		public boolean accept(File pathname) {
			return isFile(pathname);
		}
	}

	public static class NegatingFileFilter implements FileFilter {

		private final FileFilter delegate;

		public static NegatingFileFilter newNegatingFileFilter(FileFilter delegate) {
			return new NegatingFileFilter(delegate);
		}

		public NegatingFileFilter(FileFilter delegate) {
			Assert.notNull(delegate, "FileFilter must not be null");
			this.delegate = delegate;
		}

		@Override
		public boolean accept(File pathname) {
			return !this.delegate.accept(pathname);
		}
	}
}
