/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import java.util.Arrays;

import org.w3c.dom.Element;

import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.gemfire.function.config.TypeFilterParser.Type;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author David Turanski
 *
 */
public class XmlFunctionExecutionConfigurationSource extends AbstractFunctionExecutionConfigurationSource {

	private static final String BASE_PACKAGE = "base-package";

	private final Element element;

	private final Iterable<TypeFilter> includeFilters;
	private final Iterable<TypeFilter> excludeFilters;

	private final ParserContext parserContext;

	public XmlFunctionExecutionConfigurationSource(Element element, ParserContext parserContext) {

		Assert.notNull(element, "Element must not be null");
		Assert.notNull(parserContext, "ParserContext must not be null");

		this.element = element;
		this.parserContext = parserContext;

		TypeFilterParser parser = new TypeFilterParser(parserContext.getReaderContext());

		this.includeFilters = parser.parseTypeFilters(element, Type.INCLUDE);
		this.excludeFilters = parser.parseTypeFilters(element, Type.EXCLUDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.FunctionExecutionConfigurationSource#getSource()
	 */
	@Override
	public Object getSource() {
		return this.parserContext.extractSource(this.element);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.FunctionExecutionConfigurationSource#getBasePackages()
	 */
	@Override
	public Iterable<String> getBasePackages() {

		String attribute = this.element.getAttribute(BASE_PACKAGE);

		return Arrays.asList(StringUtils.delimitedListToStringArray(attribute, ",", " "));
	}


	/* (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.FunctionExecutionConfigurationSource#getIncludeFilters()
	 */
	@Override
	public Iterable<TypeFilter> getIncludeFilters() {
		return this.includeFilters;
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.FunctionExecutionConfigurationSource#getExcludeFilters()
	 */
	@Override
	public Iterable<TypeFilter> getExcludeFilters() {
		return this.excludeFilters;
	}
}
