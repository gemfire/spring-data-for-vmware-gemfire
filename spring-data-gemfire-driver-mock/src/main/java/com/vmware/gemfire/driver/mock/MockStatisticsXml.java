/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverStatisticDescriptor;
import com.vmware.gemfire.driver.spi.DriverStatisticsType;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration;
import com.vmware.gemfire.driver.spi.StatisticDescriptorConfiguration.ValueType;

/** Local statistics XML reader. Resolves the statistics DTD in memory; never fetches external data. */
final class MockStatisticsXml {

	private static final String DTD = """
		<!ELEMENT statistics (type)+>
		<!ELEMENT type (description?, (stat)+)>
		<!ATTLIST type name CDATA #REQUIRED>
		<!ELEMENT stat (description?, unit?)>
		<!ATTLIST stat name CDATA #REQUIRED counter (true | false) #IMPLIED
		  largerBetter (true | false) #IMPLIED storage (int | long | double) #IMPLIED>
		<!ELEMENT description (#PCDATA)>
		<!ELEMENT unit (#PCDATA)>
		""";

	private final DriverStatisticsTypes types;

	MockStatisticsXml(DriverStatisticsTypes types) { this.types = types; }

	List<DriverStatisticsType> read(Reader reader) {
		try {
			var factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			var parser = factory.newDocumentBuilder();
			parser.setEntityResolver((publicId, systemId) -> {
				if ("-//GemStone Systems, Inc.//GemFire StatisticsType//EN".equals(publicId)
						|| (systemId != null && systemId.endsWith("statisticsType.dtd"))) {
					return new InputSource(new StringReader(DTD));
				}
				throw new SAXException("Unsupported statistics XML external identifier");
			});
			parser.setErrorHandler(new DefaultHandler() {
				@Override public void error(SAXParseException failure) throws SAXException { throw failure; }
				@Override public void fatalError(SAXParseException failure) throws SAXException { throw failure; }
			});
			var root = parser.parse(new InputSource(reader)).getDocumentElement();
			require(root.getTagName().equals("statistics"));
			var definitions = root.getElementsByTagName("type");
			var result = new ArrayList<DriverStatisticsType>();
			for (int i = 0; i < definitions.getLength(); i++) result.add(readType((Element) definitions.item(i)));
			return List.copyOf(result);
		}
		catch (IOException | SAXException | ParserConfigurationException failure) {
			throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "StatisticsTypes.createTypesFromXml failed", failure);
		}
		catch (ClassCastException failure) {
			throw new DriverOperationException(DriverFailureKind.UNKNOWN, "StatisticsTypes.createTypesFromXml failed", failure);
		}
	}

	private DriverStatisticsType readType(Element definition) {
		require(definition.hasAttribute("name"));
		var descriptors = new ArrayList<DriverStatisticDescriptor>();
		var statistics = definition.getElementsByTagName("stat");
		for (int i = 0; i < statistics.getLength(); i++) descriptors.add(readStatistic((Element) statistics.item(i)));
		String description = "";
		var descriptions = definition.getElementsByTagName("description");
		if (descriptions.getLength() > 0 && descriptions.item(0).getParentNode().getNodeName().equals("type")) {
			description = text((Element) descriptions.item(0));
		}
		return types.createType(definition.getAttribute("name"), description, descriptors);
	}

	private DriverStatisticDescriptor readStatistic(Element definition) {
		require(definition.hasAttribute("name"));
		boolean counter = flag(definition, "counter", true);
		boolean largerBetter = flag(definition, "largerBetter", counter);
		String storage = definition.hasAttribute("storage") ? definition.getAttribute("storage") : "int";
		require(storage.equalsIgnoreCase("int") || storage.equalsIgnoreCase("long") || storage.equalsIgnoreCase("double"));
		return types.createDescriptor(new StatisticDescriptorConfiguration(definition.getAttribute("name"),
			childText(definition, "description"), childText(definition, "unit"),
			storage.equalsIgnoreCase("double") ? ValueType.DOUBLE : ValueType.LONG, counter, largerBetter));
	}

	private boolean flag(Element definition, String name, boolean fallback) {
		if (!definition.hasAttribute(name)) return fallback;
		String value = definition.getAttribute(name);
		require(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
		return Boolean.parseBoolean(value);
	}

	private String childText(Element definition, String name) {
		var children = definition.getElementsByTagName(name);
		require(children.getLength() <= 1);
		return children.getLength() == 0 ? "" : text((Element) children.item(0));
	}

	private String text(Element element) {
		var first = (Text) element.getFirstChild();
		return first == null ? "" : first.getData();
	}

	private void require(boolean valid) {
		if (!valid) throw new AssertionError("Invalid statistics XML schema content");
	}
}
