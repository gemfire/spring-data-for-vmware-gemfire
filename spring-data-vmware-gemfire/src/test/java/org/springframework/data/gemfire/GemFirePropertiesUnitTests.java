/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;

import org.apache.geode.distributed.ConfigurationProperties;

import org.springframework.util.ReflectionUtils;
import org.testcontainers.shaded.com.google.common.collect.Lists;

/**
 * Unit Tests for {@link GemFireProperties}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.distributed.ConfigurationProperties
 * @see org.springframework.data.gemfire.GemFireProperties
 * @since 2.3.0
 */
public class GemFirePropertiesUnitTests {

    private static final Set<String> deprecatedGemFireProperties = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "disable-tcp",
            "security-udp-dhalgo",
            "udp-fragment-size",
            "udp-recv-buffer-size",
            "udp-send-buffer-size",
            "cluster-ssl-ciphers", // all 'cluster-ssl-*' properties replaced by 'ssl-*' properties
            "cluster-ssl-enabled",
            "cluster-ssl-keystore",
            "cluster-ssl-keystore-password",
            "cluster-ssl-keystore-type",
            "cluster-ssl-protocols",
            "cluster-ssl-require-authentication",
            "cluster-ssl-truststore",
            "cluster-ssl-truststore-password",
            "jmx-manager-http-port", // replaced by 'http-service-port' property
            "roles",
            "security-client-accessor", // replaced by SecurityManager
            "security-client-accessor-pp", // replaced by SecurityManager
            "security-client-authenticator", // replaced by SecurityManager
            "security-client-dhalgo", // use SSL instead
            "security-peer-authenticator" // replaced by SecurityManager
    )));

    private Set<String> resolveActualGemFirePropertyNames() {

        return Arrays.stream(GemFireProperties.values())
                .map(GemFireProperties::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> resolveExpectedNonDeprecatedGemFirePropertyNames() {

        List<Field> nonDeprecatedPublicFields = Arrays.stream(ConfigurationProperties.class.getFields())
                .filter(field -> !field.isAnnotationPresent(Deprecated.class))
                .filter(field -> !field.getName().endsWith("PREFIX"))
                .collect(Collectors.toList());

        return nonDeprecatedPublicFields.stream()
                .map(field -> ReflectionUtils.getField(field, null))
                .map(String::valueOf)
                .filter(propertyName -> !deprecatedGemFireProperties.contains(propertyName))
                .collect(Collectors.toSet());
    }

    @Test
    public void enumeratedGemFirePropertiesContainAllConfigurationProperties() {

        Set<String> actualGemFireProperties = resolveActualGemFirePropertyNames();
        Set<String> unsupportedGemFireProperties = Set.of(
            "async-distribution-timeout", "async-max-queue-size", "async-queue-timeout",
            "enable-management-rest-service", "http-service-bind-address", "http-service-port", "start-dev-rest-api",
            "memcached-bind-address", "memcached-port", "memcached-protocol");
        Set<String> expectedGemFireProperties = resolveExpectedNonDeprecatedGemFirePropertyNames();
        Set<String> missingGemFireProperties = new TreeSet<>(expectedGemFireProperties);

        missingGemFireProperties.removeAll(actualGemFireProperties);
        missingGemFireProperties.removeAll(unsupportedGemFireProperties);

        assertThat(missingGemFireProperties)
                .describedAs("Expected properties in [%s] not in [%s] include (%s)",
                        ConfigurationProperties.class.getName(), GemFireProperties.class.getName(), missingGemFireProperties)
                .isEmpty();
    }

    @Test
    public void enumeratedGemFirePropertiesMatchConfigurationProperties() {

        Set<String> actualGemFireProperties = resolveActualGemFirePropertyNames();
        Set<String> expectedGemFireProperties = resolveExpectedNonDeprecatedGemFirePropertyNames();
        Set<String> missingGemFireProperties = new TreeSet<>(actualGemFireProperties);

        missingGemFireProperties.removeAll(expectedGemFireProperties);
        missingGemFireProperties.removeAll(deprecatedGemFireProperties);

        assertThat(missingGemFireProperties)
                .describedAs("Unexpected properties in [%s] not in [%s] include (%s)",
                        GemFireProperties.class.getName(), ConfigurationProperties.class.getName(), missingGemFireProperties)
                .isEmpty();
    }

    @Test
    public void fromValidGemFireProperty() {

        assertThat(GemFireProperties.from("cache-xml-file")).isEqualTo(GemFireProperties.CACHE_XML_FILE);
        assertThat(GemFireProperties.from("gemfire.locators")).isEqualTo(GemFireProperties.LOCATORS);
        assertThat(GemFireProperties.from("  gemfire.remote-locators ")).isEqualTo(GemFireProperties.REMOTE_LOCATORS);
    }

    private void testFromInvalidGemFireProperty(String propertyName) {

        try {
            GemFireProperties.from(propertyName);
        } catch (IllegalArgumentException expected) {

            assertThat(expected).hasMessage("[%s] is not a valid Apache Geode property", propertyName);
            assertThat(expected).hasNoCause();

            throw expected;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromInvalidGemFireDotPrefixedProperty() {
        testFromInvalidGemFireProperty("gemfire.invalid-property");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromInvalidGemFireProperty() {
        testFromInvalidGemFireProperty("non-existing-property");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromValidGeodeDotPrefixedProperty() {
        testFromInvalidGemFireProperty("geode.log-level");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromBlankProperty() {
        testFromInvalidGemFireProperty("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromEmptyProperty() {
        testFromInvalidGemFireProperty("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromNullProperty() {
        testFromInvalidGemFireProperty(null);
    }
}
