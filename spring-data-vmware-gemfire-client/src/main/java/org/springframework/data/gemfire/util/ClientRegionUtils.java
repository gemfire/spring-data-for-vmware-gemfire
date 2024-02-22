/*
 * Copyright (c) VMware, Inc. 2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.data.gemfire.client.ClientRegionShortcutWrapper;
import org.springframework.util.Assert;

public class ClientRegionUtils {
  /**
   * Assert that the configuration settings for {@link ClientRegionShortcut} and the {@literal persistent} attribute
   * in &lt;gfe:*-region&gt; elements are compatible.
   *
   * @param clientRegionShortcut {@link ClientRegionShortcut} resolved from the SDG XML namespace.
   * @param persistent boolean indicating the value of the {@literal persistent} configuration attribute.
   * @see ClientRegionShortcutWrapper
   * @see ClientRegionShortcut
   */
  public static void assertClientRegionShortcutAndPersistentAttributeAreCompatible(
      ClientRegionShortcut clientRegionShortcut, Boolean persistent) {

    boolean persistentUnspecified = persistent == null;

    if (ClientRegionShortcutWrapper.valueOf(clientRegionShortcut).isPersistent()) {
      Assert.isTrue(persistentUnspecified || Boolean.TRUE.equals(persistent),
          String.format("Client Region Shortcut [%s] is not valid when persistent is false", clientRegionShortcut));
    }
    else {
      Assert.isTrue(persistentUnspecified || Boolean.FALSE.equals(persistent),
          String.format("Client Region Shortcut [%s] is not valid when persistent is true", clientRegionShortcut));
    }
  }
}
