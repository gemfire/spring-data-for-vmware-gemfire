/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.process;

import java.util.EventListener;

/**
 * The {@link ProcessInputStreamListener} is a callback interface that gets called when input arrives from either a
 * {@link Process process's} standard output steam or standard error stream.
 *
 * @author John Blum
 * @see EventListener
 * @since 0.0.1
 */
public interface ProcessInputStreamListener extends EventListener {

	/**
	 * Callback method that gets called when the {@link Process} sends output from either its standard out
	 * or standard error streams.
	 *
	 * @param input {@link String} containing output from the {@link Process} that this listener is listening to.
	 * @see Process#getErrorStream()
	 * @see Process#getInputStream()
	 */
	void onInput(String input);

}
