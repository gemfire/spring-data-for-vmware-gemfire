/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.support;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * The {@link DataSourceAdapter} class is an implementation of the {@link DataSource} interface
 * with unsupported operations by default.
 *
 * @author John Blum
 * @see Connection
 * @see DataSource
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public abstract class DataSourceAdapter implements DataSource {

	private static final String UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE = "Not Implemented";

	@Override
	public Connection getConnection() throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public Connection getConnection(final String username, final String password) throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	//@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_EXCEPTION_MESSAGE);
	}
}
