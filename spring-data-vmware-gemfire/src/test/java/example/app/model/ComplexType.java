// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package example.app.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.Data;
import lombok.ToString;

/**
 * {@link ComplexType} class used for testing purposes.
 *
 * @author John Blum
 * @see org.springframework.data.annotation.Id
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 2.5.0
 */
@Data
@ToString
@Region("Examples")
public class ComplexType {

	private BigDecimal decimalValue;

	private BigInteger integerValue;

	@Id
	private Long id;

	private String name;

}
