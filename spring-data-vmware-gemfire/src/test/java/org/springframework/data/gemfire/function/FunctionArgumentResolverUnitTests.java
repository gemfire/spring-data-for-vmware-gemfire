/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;

import org.springframework.data.gemfire.function.annotation.Filter;
import org.springframework.data.gemfire.function.annotation.RegionData;

/**
 * Unit Tests for {@link FunctionArgumentResolver}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 */
@SuppressWarnings("rawtypes")
public class FunctionArgumentResolverUnitTests {

	@Test
	public void testDefaultFunctionArgumentResolverWithNoArguments() {

		FunctionArgumentResolver functionArgumentResolver = new DefaultFunctionArgumentResolver();
		FunctionContext mockFunctionContext = mock(FunctionContext.class);

		when(mockFunctionContext.getArguments()).thenReturn(null);

		Object[] args = functionArgumentResolver.resolveFunctionArguments(mockFunctionContext);

		assertThat(args).isNotNull();
		assertThat(args.length).isEqualTo(0);
	}

	@Test
	public void testDefaultFunctionArgumentResolverWithArgumentArray() {

		FunctionArgumentResolver functionArgumentResolver = new DefaultFunctionArgumentResolver();
		FunctionContext functionContext = mock(FunctionContext.class);

		when(functionContext.getArguments()).thenReturn(new String[] { "one", "two", "three" });

		Object[] args = functionArgumentResolver.resolveFunctionArguments(functionContext);

		assertThat(args).isNotNull();
		assertThat(args instanceof String[]).isFalse();
		assertThat(args.length).isEqualTo(3);
		assertThat(args[0]).isEqualTo("one");
		assertThat(args[1]).isEqualTo("two");
		assertThat(args[2]).isEqualTo("three");
	}

	@Test
    public void testDefaultFunctionArgumentResolverWithSingleArgument() {

        FunctionArgumentResolver functionArgumentResolver = new DefaultFunctionArgumentResolver();
        FunctionContext functionContext = mock(FunctionContext.class);

        when(functionContext.getArguments()).thenReturn("test");

        Object[] args = functionArgumentResolver.resolveFunctionArguments(functionContext);

		assertThat(args).isNotNull();
        assertThat(args.length).isEqualTo(1);
        assertThat(args[0]).isEqualTo("test");
    }

    @Test
    public void testMethodWithNoSpecialArgs() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithNoSpecialArgs", String.class, int.class,
                boolean.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{"hello", 0, false};
        when(functionContext.getArguments()).thenReturn(originalArgs);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(originalArgs.length);

        int index = 0;

        for (Object arg : args) {
            assertThat(arg).isSameAs(originalArgs[index++]);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodWithRegionType() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);
        Region<Object, Object> region = mock(Region.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithRegionType", String.class, Region.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{"hello"};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getDataSet()).thenReturn(region);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(originalArgs.length + 1);

        int i = 0;
        for (Object arg : args) {
            if (i != 1) {
                assertThat(arg).isSameAs(originalArgs[i++]);
            } else {
                assertThat(arg).isSameAs(region);
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodWithOneArgRegionType() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);
        Region<Object, Object> region = mock(Region.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithOneArgRegionType", Region.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getDataSet()).thenReturn(region);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(1);
        assertThat(args[0]).isSameAs(region);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodWithAnnotatedRegion() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);
        Region<Object, Object> region = mock(Region.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithAnnotatedRegion", Region.class, String.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{"hello"};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getDataSet()).thenReturn(region);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(2);
        assertThat(args[0]).isSameAs(region);
        assertThat(args[1]).isSameAs(originalArgs[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodWithFunctionContext() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);
        Region<Object, Object> region = mock(Region.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithFunctionContext", Map.class, String.class,
                FunctionContext.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{"hello"};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getDataSet()).thenReturn(region);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(3);
        assertThat(args[0]).isSameAs(region);
        assertThat(args[1]).isSameAs(originalArgs[0]);
        assertThat(args[2]).isSameAs(functionContext);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testMethodWithResultSender() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);
        ResultSender resultSender = mock(ResultSender.class);
        Region<Object, Object> region = mock(Region.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithResultSender", Map.class, ResultSender.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getDataSet()).thenReturn(region);
        when(functionContext.getResultSender()).thenReturn(resultSender);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(2);
        assertThat(args[0]).isSameAs(region);
        assertThat(args[1]).isSameAs(resultSender);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testMethodWithFilterAndRegion() throws SecurityException, NoSuchMethodException {

        RegionFunctionContext functionContext = mock(RegionFunctionContext.class);
        Region<Object, Object> region = mock(Region.class);

        Method method = TestFunction.class.getDeclaredMethod("methodWithFilterAndRegion", Map.class, Set.class,
                Object.class);
        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{new Object()};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getDataSet()).thenReturn(region);
        @SuppressWarnings("rawtypes")
        Set keys = new HashSet<String>();
        when(functionContext.getFilter()).thenReturn(keys);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(3);
        assertThat(args[0]).isSameAs(region);
        assertThat(args[2]).isSameAs(originalArgs[0]);
        assertThat(args[1]).isSameAs(keys);
    }

	@Test(expected = IllegalStateException.class)
	public void testMethodWithMultipleRegionData() throws SecurityException, NoSuchMethodException {

		Method method = TestFunction.class.getDeclaredMethod("methodWithMultipleRegionData", Map.class, Map.class);

		new FunctionContextInjectingArgumentResolver(method);
	}

	@Test(expected = IllegalArgumentException.class)
    public void testMethodWithMultipleRegions() throws SecurityException, NoSuchMethodException {

        Method method = TestFunction.class.getDeclaredMethod("methodWithMultipleRegions", Region.class, Map.class);

		new FunctionContextInjectingArgumentResolver(method);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodWithInvalidTypeForAnnotation() throws SecurityException, NoSuchMethodException {

        Method method = TestFunction.class.getDeclaredMethod("methodWithInvalidTypeForAnnotation", Region.class);

		new FunctionContextInjectingArgumentResolver(method);
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodWithMultipleFunctionContext() throws SecurityException, NoSuchMethodException {

        Method method = TestFunction.class.getDeclaredMethod("methodWithMultipleFunctionContext",
			FunctionContext.class, FunctionContext.class);

		new FunctionContextInjectingArgumentResolver(method);
    }

    @Test
    public void testMethodWithFunctionContextAndResultSender() throws NoSuchMethodException {

        FunctionContext functionContext = mock(FunctionContext.class);
        ResultSender resultSender = mock(ResultSender.class);
        Method method = TestFunction.class.getDeclaredMethod("methodWithFunctionContextAndResultSender",
			FunctionContext.class, ResultSender.class);

        FunctionArgumentResolver far = new FunctionContextInjectingArgumentResolver(method);

        Object[] originalArgs = new Object[]{};
        when(functionContext.getArguments()).thenReturn(originalArgs);
        when(functionContext.getResultSender()).thenReturn(resultSender);

        Object[] args = far.resolveFunctionArguments(functionContext);

        assertThat(args.length).isEqualTo(2);
        assertThat(args[0]).isSameAs(functionContext);
        assertThat(args[1]).isSameAs(resultSender);
    }

	@SuppressWarnings("unused")
    static class TestFunction {

        public void methodWithNoSpecialArgs(String s1, int i1, boolean b1) { }

        public void methodWithRegionType(String s1, Region<?, ?> region) { }

        public void methodWithOneArgRegionType(Region<?, ?> region) { }

        public void methodWithAnnotatedRegion(@RegionData Region<?, ?> data, String s1) { }

        public void methodWithFunctionContext(@RegionData Map<?, ?> data, String s1, FunctionContext fc) { }

        public void methodWithResultSender(@RegionData Map<?, ?> data, ResultSender<?> resultSender) { }

        public void methodWithFilterAndRegion(@RegionData Map<String, Object> region, @Filter Set<String> keys, Object arg) { }

        //Invalid Method Signatures
        public void methodWithMultipleRegionData(@RegionData Map<?, ?> r1, @RegionData Map<?, ?> r2) { }

        public void methodWithMultipleRegions(Region<?, ?> r1, @RegionData Map<?, ?> r2) { }

        public void methodWithInvalidTypeForAnnotation(@Filter Region<?, ?> r1) { }

        public void methodWithMultipleFunctionContext(FunctionContext fc1, FunctionContext fc2) { }

        public void methodWithFunctionContextAndResultSender(FunctionContext fc1, ResultSender<?> rs) {  }
    }
}
