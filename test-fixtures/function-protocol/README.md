# Client/server function test protocol

This test-only source directory contains JDK-only wire DTOs and function IDs. Both
Spring's test source set and the 10.4 driver's isolated `functionProtocol` source
set compile these same sources. It is not a published library or a dependency on
the native driver. Keep native APIs, Spring APIs, server bodies and annotation
processors out of this directory.

`Metric` retains its wire class name and public constructor/getters. The server
deployment JAR embeds the protocol output; the Spring tests compile it locally.
`FunctionFixtureIds` lets client tests refer to the echo operation without loading
its server implementation.

The native driver's `serverFixtures` source set owns the function implementations
and PDX server XML. Spring's integration tests receive that JAR only as a file to
deploy. `verifyFunctionFixturePackaging` rejects server classes in either client
test classpath or the transitional Spring test JAR.
