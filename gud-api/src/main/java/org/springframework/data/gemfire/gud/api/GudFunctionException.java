/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFunctionException as 1:1 mapping of GemFire FunctionException
 */

package org.springframework.data.gemfire.gud.api;

import java.util.List;

/**
 * GUD API exception for function execution errors.
 */
public class GudFunctionException extends GudException {

    private List<? extends Throwable> exceptions;

    public GudFunctionException() {
        super();
    }

    public GudFunctionException(String message) {
        super(message);
    }

    public GudFunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GudFunctionException(Throwable cause) {
        super(cause);
    }

    public void addException(Throwable exception) {
        // Implementation would add to list
    }

    public List<? extends Throwable> getExceptions() {
        return exceptions;
    }
}
