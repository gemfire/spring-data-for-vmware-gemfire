/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudLogWriter interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire LogWriter.
 */
public interface GudLogWriter {

    boolean severeEnabled();
    void severe(String message);
    void severe(Throwable ex);
    void severe(String message, Throwable ex);
    
    boolean errorEnabled();
    void error(String message);
    void error(Throwable ex);
    void error(String message, Throwable ex);
    
    boolean warningEnabled();
    void warning(String message);
    void warning(Throwable ex);
    void warning(String message, Throwable ex);
    
    boolean infoEnabled();
    void info(String message);
    void info(Throwable ex);
    void info(String message, Throwable ex);
    
    boolean configEnabled();
    void config(String message);
    void config(Throwable ex);
    void config(String message, Throwable ex);
    
    boolean fineEnabled();
    void fine(String message);
    void fine(Throwable ex);
    void fine(String message, Throwable ex);
    
    boolean finerEnabled();
    void finer(String message);
    void finer(Throwable ex);
    void finer(String message, Throwable ex);
    
    boolean finestEnabled();
    void finest(String message);
    void finest(Throwable ex);
    void finest(String message, Throwable ex);
}
