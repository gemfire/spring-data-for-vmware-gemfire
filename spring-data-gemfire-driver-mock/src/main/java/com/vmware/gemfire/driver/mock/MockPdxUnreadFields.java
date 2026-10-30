// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

/** Fields a reader had not consumed; an empty carrier when unread fields are ignored. */
record MockPdxUnreadFields(Map<String, MockPdxField> fields) implements DriverPdxUnreadFields {
	MockPdxUnreadFields { fields = new LinkedHashMap<>(fields); }
}
