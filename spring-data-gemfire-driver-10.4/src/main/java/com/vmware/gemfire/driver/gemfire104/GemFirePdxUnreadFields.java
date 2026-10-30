// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.DriverPdxUnreadFields;
import org.apache.geode.pdx.PdxUnreadFields;

final class GemFirePdxUnreadFields implements DriverPdxUnreadFields {
	final PdxUnreadFields value;
	GemFirePdxUnreadFields(PdxUnreadFields value) { this.value = value; }
}
