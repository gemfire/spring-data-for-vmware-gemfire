/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.listener.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudOperation;
import org.springframework.data.gemfire.gud.api.GudCqEvent;
import org.springframework.data.gemfire.gud.api.GudCqQuery;

import org.springframework.data.gemfire.listener.ContinuousQueryListener;

/**
 * Unit Tests for {@link ContinuousQueryListenerAdapter}.
 *
 * @author Costin Leau
 * @author Oliver Gierke
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter
 */
public class QueryListenerAdapterUnitTests {

	private ContinuousQueryListenerAdapter adapter;

	@SuppressWarnings("unused")
	interface Delegate {

		void handleEvent(GudCqEvent event);

		void handleQuery(GudCqQuery query);

		void handleOperation(GudOperation op);

		void handleArray(byte[] ba);

		void handleKey(Object key);

		void handleKeyValue(Object key, Object value);

		void handleError(Throwable th);

		void handleOperations(GudOperation base, GudOperation query);

		void handleAll(GudCqEvent event, GudCqQuery query, byte[] data, Object key, GudOperation op, Throwable error, GudOperation queryOp, Object value);

		void handleInvalid(Object o1, Object o2, Object o3);

	}

	static class SampleListener implements ContinuousQueryListener {

		int count;

		@Override
		public void onEvent(GudCqEvent event) {
			count++;
		}
	}

	static GudCqEvent event() {

		return new GudCqEvent() {

			final byte[] deltaValue = new byte[0];
			final GudCqQuery cq = mock(GudCqQuery.class);
			final Exception exception = new Exception();
			final Object key = new Object();
			final Object value = new Object();

			public GudOperation getBaseOperation() {
				return GudOperation.CACHE_CLOSE;
			}

			public GudCqQuery getCq() {
				return cq;
			}

			public byte[] getDeltaValue() {
				return deltaValue;
			}

			public Object getKey() {
				return key;
			}

			public Object getNewValue() {
				return value;
			}

			public GudOperation getQueryOperation() {
				return GudOperation.CACHE_CREATE;
			}

			public Throwable getThrowable() {
				return exception;
			}
		};
	}

	@Before
	public void setUp() {
		this.adapter = new ContinuousQueryListenerAdapter();
	}

	@Test
	public void testThatWhenNoDelegateIsSuppliedTheDelegateIsAssumedToBeTheListenerAdapterItself() {
		assertThat(adapter.getDelegate()).isSameAs(adapter);
	}

	@Test
	public void testThatTheDefaultHandlingMethodNameIsTheConstantDefault() {
		assertThat(adapter.getDefaultListenerMethod())
			.isEqualTo(ContinuousQueryListenerAdapter.DEFAULT_LISTENER_METHOD_NAME);
	}

	@Test
	public void adapterWithListenerAndDefaultMessageOnEventIsCorrect() {

		ContinuousQueryListener mockCqListener = mock(ContinuousQueryListener.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockCqListener);

		GudCqEvent event = event();

		cqListenerAdapter.onEvent(event);

		verify(mockCqListener, times(1)).onEvent(same(event));
	}

	@Test
	public void handlesAll() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.setDefaultListenerMethod("handleAll");
		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1))
			.handleAll(eq(event), eq(event.getCq()), eq(event.getDeltaValue()), eq(event.getKey()),
				eq(event.getBaseOperation()), eq(event.getThrowable()), eq(event.getQueryOperation()),
				eq(event.getNewValue()));
	}

	@Test
	public void handlesArray() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.setDefaultListenerMethod("handleArray");
		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1)).handleArray(eq(event.getDeltaValue()));
	}

	@Test
	public void handlesCqEvent() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1)).handleEvent(same(event));
	}

	@Test
	public void handlesCqQuery() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		cqListenerAdapter.setDefaultListenerMethod("handleQuery");

		GudCqEvent event = event();

		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1)).handleQuery(same(event.getCq()));
	}

	@Test
	public void handlesError() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.setDefaultListenerMethod("handleError");
		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1)).handleError(eq(event.getThrowable()));
	}

	@Test
	public void handlesKey() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.setDefaultListenerMethod("handleKey");
		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1)).handleKey(eq(event.getKey()));
	}

	@Test
	public void handlesKeyValue() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.setDefaultListenerMethod("handleKeyValue");
		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1))
			.handleKeyValue(eq(event.getKey()), eq(event.getNewValue()));
	}

	@Test
	public void handlesOperations() {

		Delegate mockDelegate = mock(Delegate.class);

		ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

		GudCqEvent event = event();

		cqListenerAdapter.setDefaultListenerMethod("handleOperations");
		cqListenerAdapter.onEvent(event);

		verify(mockDelegate, times(1))
			.handleOperations(eq(event.getBaseOperation()), eq(event.getQueryOperation()));
	}

	@Test
	public void handlesInvalid() {

		Delegate mockDelegate = mock(Delegate.class);

		try {

			ContinuousQueryListenerAdapter cqListenerAdapter = new ContinuousQueryListenerAdapter(mockDelegate);

			cqListenerAdapter.setDefaultListenerMethod("handleInvalid");
			cqListenerAdapter.onEvent(event());
		}
		finally {
			verify(mockDelegate, never()).handleInvalid(any(), any(), any());
		}
	}

	/**
	 * @link https://jira.spring.io/browse/SGF-89
	 */
	@Test
	public void triggersListenerImplementingInterfaceCorrectly() {

		SampleListener listener = new SampleListener();

		ContinuousQueryListenerAdapter listenerAdapter = spy(new ContinuousQueryListenerAdapter(listener));

		doAnswer(invocation -> { throw new RuntimeException(invocation.<Throwable>getArgument(0)); })
			.when(listenerAdapter).handleListenerException(isA(Throwable.class));

		listenerAdapter.onEvent(event());
		assertThat(listener.count).isEqualTo(1);
	}
}
