/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.eviction;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;

/**
 * Simple utility class used for defining nested factory-method like definitions w/o polluting the container with useless beans.
 *
 * @author Costin Leau
 * @author John Blum
 * @see FactoryBean
 * @see InitializingBean
 * @see GudEvictionAttributes
 * @see GudObjectSizer
 */
@SuppressWarnings("unused")
public class EvictionAttributesFactoryBean implements FactoryBean<GudEvictionAttributes>, InitializingBean {

	protected static final int DEFAULT_LRU_MAXIMUM_ENTRIES = GudEvictionAttributes.DEFAULT_ENTRIES_MAXIMUM;

	protected static final int DEFAULT_MEMORY_MAXIMUM_SIZE = GudEvictionAttributes.DEFAULT_MEMORY_MAXIMUM;

	private GudEvictionAction action = null;

	private GudEvictionAttributes evictionAttributes;

	private EvictionPolicyType type = EvictionPolicyType.ENTRY_COUNT;

	private Integer threshold = null;

	private GudObjectSizer objectSizer = null;

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() {
		evictionAttributes = createAttributes();
	}

	GudEvictionAttributes createAttributes() {
		switch (type) {
			case HEAP_PERCENTAGE:
				if (threshold != null) {
					throw new IllegalArgumentException("HEAP_PERCENTAGE (LRU_HEAP algorithm) does not support threshold (a.k.a. maximum)");
				}

				return GudEvictionAttributes.createLRUHeapAttributes(getObjectSizer(), getAction());
			case MEMORY_SIZE:
				return (threshold != null ? GudEvictionAttributes.createLRUMemoryAttributes(getThreshold(), getObjectSizer(), getAction())
					: GudEvictionAttributes.createLRUMemoryAttributes(getObjectSizer(), getAction()));
			case ENTRY_COUNT:
			default:
				return (threshold != null ? GudEvictionAttributes.createLRUEntryAttributes(getThreshold(), getAction())
					: GudEvictionAttributes.createLRUEntryAttributes(DEFAULT_LRU_MAXIMUM_ENTRIES, getAction()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public GudEvictionAttributes getObject() {
		return evictionAttributes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?> getObjectType() {
		return (evictionAttributes != null ? evictionAttributes.getClass() : GudEvictionAttributes.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Sets the action to perform on the Region when Eviction occurs.
	 *
	 * @param action the specified EvictionAction taken on the Region.
	 * @see GudEvictionAction
	 */
	public void setAction(final GudEvictionAction action) {
		this.action = action;
	}

	/**
	 * Gets the action performed on the Region when Eviction occurs.
	 *
	 * @return the EvictionAction taken on the Region.
	 * @see GudEvictionAction
	 */
	public GudEvictionAction getAction() {
		return (action != null ? action : GudEvictionAction.DEFAULT_EVICTION_ACTION);
	}

	/**
	 * Sets the GemFire ObjectSizer used in determining object sizes of data stored in the Cache.
	 *
	 * @param objectSizer the ObjectSizer used in sizing object data stored in the Cache.
	 * @see GudObjectSizer
	 */
	public void setObjectSizer(final GudObjectSizer objectSizer) {
		this.objectSizer = objectSizer;
	}

	/**
	 * Gets the GemFire ObjectSizer used in determining object sizes of data stored in the Cache.
	 *
	 * @return the ObjectSizer used in sizing object data stored in the Cache.
	 * @see GudObjectSizer
	 */
	public GudObjectSizer getObjectSizer() {
		return objectSizer;
	}

	/**
	 * Set the threshold used by the LRU algorithm in ENTRY_COUNT and MEMORY_SIZE eviction policy.
	 *
	 * @param threshold an Integer value specifying the threshold used by the LRU algorithm
	 * when enforcing the eviction policy.
	 */
	public void setThreshold(final Integer threshold) {
		this.threshold = threshold;
	}

	/**
	 * Get the threshold used by the LRU algorithm in ENTRY_COUNT and MEMORY_SIZE eviction policy.
	 *
	 * @return an Integer value specifying the threshold used by the LRU algorithm when enforcing the eviction policy.
	 */
	public Integer getThreshold() {
		return threshold;
	}

	/**
	 * Sets the type of eviction policy and algorithm (e.g. LRU on Entry Count, Heap % or Memory Size)
	 * to implement on the Region.
	 *
	 * @param type the type of eviction policy/algorithm to implement on the Region.
	 * @see EvictionPolicyType
	 */
	public void setType(final EvictionPolicyType type) {
		this.type = type;
	}

	/**
	 * Gets the eviction policy and algorithm used by the Region.
	 *
	 * @return the eviction policy and algorithm in use by the Region.
	 * @see EvictionPolicyType
	 */
	public EvictionPolicyType getType() {
		return type;
	}
}
