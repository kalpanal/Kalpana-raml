package com.ibm.automation.parasoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.automation.excelOps.InputExcelFileVO;
import com.ibm.automation.parasoft.domain.ConfigurationTO;
import com.ibm.automation.parasoft.util.Util;
import com.ibm.automation.parasoft.util.XMLJsonUtils;

public class XMLElementBuilder {

	@SuppressWarnings("unused")
	private static void buildComplexValueUnderMessagingSchema(Element messagingSchema, String inputSampleString) {

		try {
			Element rootElementObjectType = new XMLElementBuilder().loadElementValueTemplateXML("rootElementValueTemplateXML.xml").getRootElement();
			messagingSchema.getChild("ElementValue").addContent(rootElementObjectType.detach());

			IteratorIterable<Content> descendantsOfChannel = messagingSchema.getDescendants();
			Element parentElement = null;
			for (Content descendant : descendantsOfChannel) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("CompositorValueSet")) {
						parentElement = child;

					}
				}
			}
			// Element parentElement = messagingSchema.getChild("ElementValue");
			if (inputSampleString == null) {

				rootElementObjectType.getChild("CompositorValue").getChild("CompositorValueSetCollectionSet").getChild("CompositorValueSet")
						.removeChild("valuesSize");
			} else {
				jsonString2MapForComplexValue(parentElement, inputSampleString, true, null);

			}
		} catch (IOException e) {
			System.out.println("Error inside buildComplexValueUnderMessagingSchema method" + e.getMessage());
		}

	}

	private static Element buildCompositorValueSet(Element child, String inputSampleStringJson) {
		// Element compositorValueSet = new Element("CompositorValueSet");
		try {
			if (inputSampleStringJson != null) {
				/*
				 * Element rootElementObjectType = new XMLElementBuilder()
				 * .loadElementValueTemplateXML(
				 * "rootElementTemplateXML.xml").getRootElement();
				 */

				IteratorIterable<Content> descendantsOfChannel = child.getDescendants();
				Element parentElement = null;
				for (Content descendant : descendantsOfChannel) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element child1 = (Element) descendant;
						if (child1.getName().equalsIgnoreCase("ElementValue")) {
							// Do nothing
							IteratorIterable<Content> descendantsOfChannel1 = child1.getDescendants();
							for (Content descendant1 : descendantsOfChannel1) {
								if (descendant1.getCType().equals(Content.CType.Element)) {
									Element element = (Element) descendant1;
									if (element.getName().equals("paramTypesSize")) {
										System.out.println("\n paramtypesize--------------->" + element.getText());
										parentElement = (Element) element.getParent();
									}
								}
							}

						}
					}
				}
				jsonString2Map(parentElement, inputSampleStringJson, true, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return child;
	}

	public static Element buildConfigurationEndPointElement(String endpoint, String chunking, String followRedirects, String connection, String method) {

		Element configurationEndPointElement = new Element("Configuration");
		configurationEndPointElement.setAttribute(new Attribute("endpoint", endpoint));
		configurationEndPointElement.setAttribute(new Attribute("chunking", chunking));
		configurationEndPointElement.setAttribute(new Attribute("followRedirects", followRedirects));
		configurationEndPointElement.setAttribute(new Attribute("connection", connection));
		configurationEndPointElement.setAttribute(new Attribute("method", method));
		return configurationEndPointElement;

	}

	public static Element buildConfigurationTypeElement(String type, String dataSource, String validRange, String url, String hTTPMethod,
			String resourceMode, String serviceDefinitionUrl) {

		Element configurationTypeElement = new Element("Configuration");
		configurationTypeElement.setAttribute(new Attribute("type", type));
		configurationTypeElement.setAttribute(new Attribute("dataSource", dataSource));
		configurationTypeElement.setAttribute(new Attribute("validRange", validRange));
		configurationTypeElement.setAttribute(new Attribute("Url", url));
		configurationTypeElement.setAttribute(new Attribute("HTTPMethod", hTTPMethod));
		configurationTypeElement.setAttribute(new Attribute("ResourceMode", resourceMode));
		configurationTypeElement.setAttribute(new Attribute("ServiceDefinitionUrl", serviceDefinitionUrl));

		return configurationTypeElement;

	}

	public static Element buildDataSourceConfigElement(String resource) {

		Element configurationElement = new Element("Configuration");
		Element resourceElement = new Element("Resource");
		resourceElement.addContent(resource);
		configurationElement.addContent(resourceElement);

		return configurationElement;

	}

	public static Element buildDataSourceElement(String name, String type) {

		Element dataSouceElement = new Element("DataSource");
		dataSouceElement.setAttribute(new Attribute("name", name));
		dataSouceElement.setAttribute(new Attribute("type", type));
		return dataSouceElement;

	}

	public static Element buildHeadersElement(HashMap<String, String> headerMap) {

		Element headersElement = new Element("Headers");

		headerMap.forEach((headerName, headerValue) -> {

			Element headerElement = new Element("Header");
			headerElement.setAttribute(new Attribute("name", headerName));
			headerElement.addContent(headerValue);
			headersElement.addContent(headerElement);
		});

		return headersElement;

	}

	public static Element buildHeadersElementByEnvVariable(String authorization, String messageId, String traceabilityId) {

		Element headersElement = new Element("Headers");
		headersElement.addContent(new Element("Header").setAttribute("name", authorization).addContent("${" + authorization.toUpperCase() + "}"));
		headersElement.addContent(new Element("Header").setAttribute("name", messageId).addContent("${" + messageId.toUpperCase() + "}"));
		headersElement.addContent(new Element("Header").setAttribute("name", traceabilityId).addContent("${" + traceabilityId.toUpperCase() + "}"));

		return headersElement;

	}

	public static Element buildHeadersElementParameterised(String authorization, String messageId, String traceabilityId) {

		Element headersElement = new Element("Headers");
		headersElement.addContent(new Element("Header").setAttribute("name", authorization).addContent("Parameterized: " + authorization));
		headersElement.addContent(new Element("Header").setAttribute("name", messageId).addContent("Parameterized: " + messageId));
		headersElement.addContent(new Element("Header").setAttribute("name", traceabilityId).addContent("Parameterized: " + traceabilityId));

		return headersElement;

	}

	private static void buildNameValuePropertiesForQueryParama(Element nameValuePropertiesForQueryParams, List<Parameter> queryParametersList)
			throws IOException {
		Element propertiesSize = new Element("propertiesSize");
		propertiesSize.addContent(queryParametersList.size() + "");

		nameValuePropertiesForQueryParams.addContent(propertiesSize);

		for (int i = 0; i < queryParametersList.size(); i++) {
			Parameter parameter = queryParametersList.get(i);

			Element nameValuePairElement = new XMLElementBuilder().loadElementValueTemplateXML("nameValuePairWithSelectedIndex.xml")
					.detachRootElement();

			IteratorIterable<Content> descendantsOfNameValuePair = nameValuePairElement.getDescendants();
			Element name = null, column = null;
			for (Content descendant : descendantsOfNameValuePair) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("name")) {
						name = child;

					} else if (child.getName().equalsIgnoreCase("column")) {
						column = child;

					}
				}
			}
			name.removeContent();
			name.addContent(parameter.displayName().toString());
			column.removeContent();
			column.addContent(parameter.displayName().toString());

			nameValuePropertiesForQueryParams.addContent(nameValuePairElement);

		}

	}

	@SuppressWarnings("unused")
	public static void buildNameValuePropertiesForTokenURL(Element nameValuePropertiesForQueryParams, Map<String, Object> queryParamsMap)
			throws IOException {
		// nameValuePropertiesForQueryParams.removeChild("propertiesSize");
		/*
		 * Element propertiesSize = new Element("propertiesSize");
		 * propertiesSize.addContent(queryParamsMap.size()+"");
		 */

		queryParamsMap.forEach((k, v) -> {

			System.out.println("key: " + k + ", value: " + v.toString());
			Element nameValuePairElement = null;
			try {
				nameValuePairElement = new XMLElementBuilder().loadElementValueTemplateXML("nameValuePair.xml").detachRootElement();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IteratorIterable<Content> descendantsOfNameValuePair = nameValuePairElement.getDescendants();
			Element name = null, column = null, value = null;
			for (Content descendant : descendantsOfNameValuePair) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("name")) {
						name = child;

					} else if (child.getName().equalsIgnoreCase("column")) {
						column = child;

					} else if (child.getName().equalsIgnoreCase("value")) {
						value = child;
					}
				}
			}
			name.removeContent();
			name.addContent(k.toString());
			column.removeContent();
			column.addContent("");
			value.removeContent();
			value.addContent(v.toString());
			nameValuePropertiesForQueryParams.addContent(nameValuePairElement);

		});

	}

	public static Element buildNegativeResponseConditionalAssertion(String responseCode, Element xmlAssertionTool) throws IOException {
		Element conditionAssertion = new XMLElementBuilder().loadElementValueTemplateXML("conditionalAssertionTemplate.xml").detachRootElement();
		IteratorIterable<Content> descendantsOfConditional = conditionAssertion.getDescendants();
		Element andAssertion = null;
		for (Content descendant : descendantsOfConditional) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("AndAssertion")) {
					andAssertion = child;
				} else if (child.getName().equalsIgnoreCase("assertionsSize")) {
				}
			}
		}

		xmlAssertionTool.addContent(9, conditionAssertion);

		return andAssertion;
	}

	private static void buildRESTClientToolTest(Element restClientToolTest, Element messagingSchema, ConfigurationTO configurationTO,
			boolean firstTime, AtomicInteger increment, Element urlPathParametersLiteralElement, Element genericAssertionTool,
			Element conditionalAssertionSize) throws Exception {
		Element restClientToolTestParent = (Element) restClientToolTest.getParent();

		AtomicInteger firstTimeRestClientToolSet = new AtomicInteger();
		System.out.println("for END POINT URL ------->" + configurationTO.getEndPointUrl() + "\n" + configurationTO.getResponseSchemaString());
		if (configurationTO.getInputSampleString() != null) {
			Map<String, Object> map = XMLJsonUtils.jsonString2MapForAssertionsWithXPath(configurationTO.getInputSampleString(), null);
			System.out.println("Done==========\n" + map);
		}
		
		
		/** set Assertions - START */
		HashMap<String, String> responseMap = configurationTO.getResponseSchemaMap();
		int genericAssertionSizeInt = 0;
		boolean firstAssertionToolSet = false;
		Element genericAssertionToolForPosOrNegScenario = null;
		Element genericAssertionToolCloned = null;
		Element genericAssertionToolOriginal = genericAssertionTool.clone();
		boolean negativeScenarioExecuted = false;
		/*SortedMap<String, Object> responseMapSorted = new TreeMap<String, Object>(responseMap);
		//SortedMap<whatever> myNewMap = new TreeMap<whatever>(responseMap);
		 * //for (Entry<String, String> entry : responseMapSorted.entrySet()) {*/
		for(String responseCode: new TreeSet<String>(responseMap.keySet())){		
			//String responseCode = entry.getKey();
			String reponseSchemaContent = responseMap.get(responseCode);
			// Just run assertions tag creation just twice not as many
			// response code as in responseMap
			if (genericAssertionSizeInt < 2) {				
				if(!firstAssertionToolSet){
					genericAssertionToolForPosOrNegScenario = genericAssertionTool.getChild("XMLAssertionTool");
						
				}else{			
					genericAssertionToolCloned = genericAssertionToolOriginal.clone();
					genericAssertionToolForPosOrNegScenario = genericAssertionToolCloned.getChild("XMLAssertionTool");					
				}
				
				if (reponseSchemaContent != null && !reponseSchemaContent.equals("") ) {
					if(reponseSchemaContent.contains("serverStatusCode") && (!negativeScenarioExecuted)){
						genericAssertionToolForPosOrNegScenario.getChild("assertionsSize").removeContent();
						genericAssertionToolForPosOrNegScenario.getChild("assertionsSize").addContent("1");
						Element andAssertion = buildNegativeResponseConditionalAssertion(responseCode, genericAssertionToolForPosOrNegScenario);
						genericAssertionToolForPosOrNegScenario.getParentElement().getChild("name").removeContent();
						genericAssertionToolForPosOrNegScenario.getParentElement().getChild("name").addContent("JSON Assertor_For_NegativeScenarios");
						XMLJsonUtils.jsonString2MapForReponseCodeAssertions(responseCode, reponseSchemaContent, null, andAssertion);
						//firstAssertionToolSet = true;
						//genericAssertionSizeInt++;
						negativeScenarioExecuted = true;
					
					} else if(!reponseSchemaContent.contains("serverStatusCode")) {
						genericAssertionToolForPosOrNegScenario.getParentElement().getChild("name").removeContent();
						genericAssertionToolForPosOrNegScenario.getParentElement().getChild("name").addContent("JSON Assertor_For_PositiveScenarios");
						XMLJsonUtils.jsonString2MapForReponseCodeAssertions(responseCode, reponseSchemaContent, null, genericAssertionToolForPosOrNegScenario);
						
					}
					
					if((!genericAssertionToolForPosOrNegScenario.getChild("assertionsSize").getText().equals("0")) && 
							(!genericAssertionToolForPosOrNegScenario.getChild("assertionsSize").getText().equals("1-template"))){
						if(firstAssertionToolSet){
							genericAssertionTool.getParentElement().addContent(40, genericAssertionToolCloned);							
						}else{
							firstAssertionToolSet = true;
						}
						genericAssertionSizeInt++;
					}
									
				}
				
			}
		}
		if ( genericAssertionTool.getParentElement().getChild("outputToolsSize") != null) {
			genericAssertionTool.getParentElement().getChild("outputToolsSize").removeContent();
			genericAssertionTool.getParentElement().getChild("outputToolsSize").addContent(""+genericAssertionSizeInt);
		}

		/** set Assertions - END */
		/*
		 * configurationTO.getDataSource().forEach( (dataSheetName) -> {
		 */
		/*
		 * for first time we already have RESTClientToolTest XML node inside
		 * testSuiteTemplate.xml, therefore this if loop should be called for
		 * first time
		 */
		if (firstTimeRestClientToolSet.get() == 0) {
			firstTimeRestClientToolSet.incrementAndGet();
			try {
				messagingSchema.addContent(0, new XMLElementBuilder().loadElementValueTemplateXML("rootElementTemplateXML.xml").getRootElement()
						.detach());

				// removeElementTypeIfQueryParamIsPresent();

				buildCompositorValueSet(messagingSchema, configurationTO.getInputSampleString());
				updatedHashTagValues(messagingSchema);
				buildComplexValueUnderMessagingSchema(messagingSchema, configurationTO.getInputSampleString());

				/**
				 * update messagingSchemaElement with empty <ElementType> and
				 * emtpy <ElementValue> nodes if incoming request json is empty,
				 * this should be called only for first time messaging schema(if
				 * loop) , not in else loop
				 */
				updateMessagingSchemaForEmptyRequest(messagingSchema, configurationTO.getInputSampleString());
				updateUrlPathParametersLiteral(urlPathParametersLiteralElement, configurationTO);
				// Map<String, Object> responseParamsMap =
				// buildAssertionsOnResponseParams(configurationTO.getResponseSchemaString());

			} catch (Exception e) {
				System.out.println("Error while update restclientoolToolTest XML node for first time" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			/*
			 * from second time onwards within a <testSuite> if more than one
			 * RESTClientToolTest xml node is to be built else loop to be
			 * called, to add RESTClientToolTest node afresh from it;s template
			 * xml *
			 */
			try {
				// restClientToolTestParent.addContent(new
				// XMLElementBuilder().loadElementValueTemplateXML("restClientToolTestTemplateXML.xml").detachRootElement());
				Element secondRestClientToolTestParent = new XMLElementBuilder().loadElementValueTemplateXML("restClientToolTestTemplateXML.xml")
						.detachRootElement();
				secondRestClientToolTestParent = updateRestToolTest(secondRestClientToolTestParent, increment, configurationTO);
				IteratorIterable<Content> descendantsOfChannel = secondRestClientToolTestParent.getDescendants();
				Element secondTimeMessaingSchema = null, outputToolsSize = null, jsonAssertionToolElse = null, secondTimeMessaingSchemaForElementVal = null;
				for (Content descendant : descendantsOfChannel) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element child = (Element) descendant;
						if (child.getName().equalsIgnoreCase("MessagingSchemaElement")) {
							if (child.getChildren().size() > 0) {
								if (child.getChildren().get(0).getName().equals("elementTypeName")) {
									// getChild("elementTypeName").getTextTrim().equalsIgnoreCase("template")){
									secondTimeMessaingSchema = child;
								}
								if (child.getChildren().get(0).getName().equals("ElementValue")) {
									// getChild("elementTypeName").getTextTrim().equalsIgnoreCase("template")){
									secondTimeMessaingSchemaForElementVal = child;
								}
							}

						} else if (child.getName().equalsIgnoreCase("outputToolsSize") && child.getText().equalsIgnoreCase("1-assertiontemplate")) {
							outputToolsSize = child;
						} else if (child.getName().equalsIgnoreCase("XMLAssertionTool")) {
							jsonAssertionToolElse = child;
						} else if (child.getName().equalsIgnoreCase("UrlPathParametersLiteral") && child.getTextTrim().equalsIgnoreCase("template")) {
							urlPathParametersLiteralElement = child;
						}
					}
				}
				secondTimeMessaingSchema.addContent(0, new XMLElementBuilder().loadElementValueTemplateXML("rootElementTemplateXML.xml")
						.getRootElement().detach());
				Element elementToAddElementValueXML = secondTimeMessaingSchema.getChild("ElementValue");

				// messagingSchema = secondTimeMessaingSchema;
				buildCompositorValueSet(secondTimeMessaingSchema, configurationTO.getInputSampleString());
				restClientToolTestParent.addContent(secondRestClientToolTestParent.detach());
				updatedHashTagValues(secondTimeMessaingSchema);
				buildComplexValueUnderMessagingSchema(secondTimeMessaingSchemaForElementVal, configurationTO.getInputSampleString());

				/**
				 * update messagingSchemaElement with empty <ElementType> and
				 * emtpy <ElementValue> nodes if incoming request json is empty
				 */
				// updateMessagingSchemaForEmptyRequest(secondTimeMessaingSchema,
				// configurationTO.getInputSampleString());
				updateAllOtherValueInsideRestClientToolTestTag(secondTimeMessaingSchema, configurationTO);
				updateUrlPathParametersLiteral(urlPathParametersLiteralElement, configurationTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static Element buildRowElements(InputExcelFileVO dataSourceFileContent, Element dataSource) {
		List<String> headerList = dataSourceFileContent.getHeaders();
		List<DataRow> allRowsContent = dataSourceFileContent.getContents();
		final AtomicInteger index = new AtomicInteger();

		try {
			allRowsContent.stream().forEach(dataRow -> {
				Element row1 = new Element("Row");
				row1.setAttribute("index", index.toString());
				index.incrementAndGet();
				System.out.println("dataRow-------->" + dataRow);

				headerList.stream().forEach(headerName -> {
					// System.out.println("headerNameheaderNameheaderNameheaderName->"+headerName);
						Element column1 = new Element("Column");
						column1.setAttribute(new Attribute("name", headerName));
						switch (headerName.toLowerCase()) {
						case "messageid":
							if (dataRow.get(headerName).isEmpty()) {
								column1.addContent("${MessageID}");
							} else {
								column1.addContent(dataRow.get(headerName));
							}
							break;
						case "tracebilityid":
							if (dataRow.get(headerName).isEmpty()) {
								column1.addContent("${TraceabilityID}");
							} else {
								column1.addContent(dataRow.get(headerName));
							}
							break;
						default:
							column1.addContent(dataRow.get(headerName));
							break;
						}

						row1.addContent(column1);
					});

				dataSource.addContent(row1);
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// System.out.println("row1row1row1row1row1row1"+dataSource);
		return dataSource;
	}

	public static Element buildStringComparisonAssertions(JSONArray jsonArray, String actualKey, Element andAssertion)
			throws IOException {
		Element assertionXPath = null, column = null, stringName1 = null;

		for (int i = 0; i < jsonArray.length(); i++) {
			Element stringAssertion = new XMLElementBuilder().loadElementValueTemplateXML("stringAssertionTemplate.xml").detachRootElement();
			IteratorIterable<Content> descendantsOfChannel1 = stringAssertion.getDescendants();
			for (Content descendant : descendantsOfChannel1) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("name")) {
						if (child.getText().equalsIgnoreCase("Key1")) {
							stringName1 = child;
						}
					} else if (child.getName().equalsIgnoreCase("Assertion_XPath")) {
						assertionXPath = child;

					} else if (child.getName().equalsIgnoreCase("column")) {
						column = child;

					}
				}
			}

			if (stringName1 != null) {
				stringName1.removeContent();
				stringName1.addContent(jsonArray.get(i) + "");
			}
			assertionXPath.removeContent();
			assertionXPath.addContent("/root/" + XMLJsonUtils.replaceDotWithSlashForAssertion_Xpath(actualKey)+"/"+jsonArray.get(i));
			column.removeContent();
			//System.out.println(actualKey.substring(0, actualKey.indexOf("/")));
			// column.addContent(jsonArray.get(i)+"");
			column.addContent(XMLJsonUtils.replaceDotWithUnderscore(actualKey)+"_"+jsonArray.get(i));
			if(andAssertion.getName().contains("XMLAssertionTool")){
				andAssertion.addContent(8,stringAssertion);
			}else{
				andAssertion.addContent(stringAssertion);
			}
			
			// System.out.println();

		}
		return andAssertion;
	}

	public static Element buildTestElement(String name, String testPath) {

		Element testElement = new Element("Test");
		testElement.setAttribute(new Attribute("testPath", testPath));
		testElement.setAttribute(new Attribute("name", name));
		return testElement;

	}

	public static Element buildTestSuiteElement(String name, String testPath) {

		Element testSuiteElement = new Element("TestSuite");
		testSuiteElement.setAttribute(new Attribute("name", name));
		testSuiteElement.setAttribute(new Attribute("runConcurrently", "false"));
		testSuiteElement.setAttribute(new Attribute("testPath", testPath));
		// mainElement.addContent(testSuiteElement);
		return testSuiteElement;

	}

	public static List<Object> jsonArray2List(String arrayOFKeys, Element parentElement, String actualKey) throws JSONException, IOException {
		System.out.println("Incoming value is of JSONArray : =========");
		// Element elementValueElement = new
		// XMLElementBuilder().loadElementValueTemplateXML();
		ObjectMapper mapper = new ObjectMapper();
		// mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// jsonString = jsonString.replace("'", "\\\\u0027");
		System.out.print("\n Incoming json string for reference : " + arrayOFKeys);
		TypeReference<ArrayList<?>> typeRef = new TypeReference<ArrayList<?>>() {
		};
		ArrayList<?> arrayOFKeysList = mapper.readValue(arrayOFKeys, typeRef);

		List<Object> array2List = new ArrayList<Object>();
		int arraySize;
		if (arrayOFKeysList.size() == 1) {
			arraySize = arrayOFKeysList.size();
		} else {
			arraySize = arrayOFKeysList.size() - 1;
		}

		/**
		 * always send one item from Array Not all items, therefore sending
		 * arraySize as 1
		 */
		arraySize = 1;
		for (int i = 0; i < arraySize; i++) {
			if (arrayOFKeysList.get(i) instanceof LinkedHashMap) {

				System.out.println("Incomin value is of JSONObject : " + arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				// Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				System.out.println(mapToJson);
				Map<String, Object> subObj2Map = jsonString2Map(parentElement, mapToJson, false, actualKey);
				array2List.add(subObj2Map);
			} else if (arrayOFKeysList.get(i) instanceof ArrayList) {

				System.out.println("Incomin value is of JSONObject : " + arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				// Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				System.out.println(mapToJson);

				List<Object> subarray2List = jsonArray2List(mapToJson, parentElement, actualKey);
				array2List.add(subarray2List);
			} else {
					//array2List.add(arrayOFKeysList.get(i));				
					//listChildrenForElementValue(parentElement, 0, actualKey, value.toString(), totalSize + "");
					listChildrenForArrayElementValue(parentElement, 0, actualKey, arrayOFKeysList.get(i).toString(), 1 + "");
				
					//listChildrenForElementValue(parentElement, 0, key, value.toString(), totalSize + "");
				
			}
		}
		return array2List;
	}

	private static Element listChildrenForArrayElementValue(Element parentElement, int depth, String columnName, String columnValue, String objectSize)
			throws IOException {
		Element complexType = parentElement.getParentElement();
		complexType.removeChild("attributesSize");
		complexType.removeChild("AttributeType");//get reference to complexType Element		
		complexType.removeChild("name");
		complexType.removeChild("compositor");
		complexType.removeChild("AllCompositor");
		
		return parentElement;
	}
	public static List<Object> jsonArray2ListForComplexValue(String arrayOFKeys, Element incomingElementValueElementForString, String actualKey)
			throws JSONException, IOException {
		System.out.println("Incoming value is of JSONArray : =========");
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + arrayOFKeys);
		TypeReference<ArrayList<?>> typeRef = new TypeReference<ArrayList<?>>() {
		};
		ArrayList<?> arrayOFKeysList = mapper.readValue(arrayOFKeys, typeRef);

		int arraySize;
		if (arrayOFKeysList.size() == 1) {
			arraySize = arrayOFKeysList.size();
		} else {
			arraySize = arrayOFKeysList.size() - 1;
		}

		List<Object> array2List = new ArrayList<Object>();
		for (int i = 0; i < arraySize; i++) {
			if (arrayOFKeysList.get(i) instanceof LinkedHashMap) {
				System.out.println("Incomin value is of JSONObject : " + arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				// Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				System.out.println(mapToJson);
				Map<String, Object> subObj2Map = jsonString2MapForComplexValue(incomingElementValueElementForString, mapToJson, false, actualKey);
				array2List.add(subObj2Map);
			} else if (arrayOFKeysList.get(i) instanceof ArrayList) {

				System.out.println("Incomin value is of JSONObject : " + arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				// Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				System.out.println(mapToJson);

				List<Object> subarray2List = jsonArray2ListForComplexValue(mapToJson, incomingElementValueElementForString, actualKey);
				array2List.add(subarray2List);
			} else {
				// keyNode( arrayOFKeys.opt(i) );
				array2List.add(arrayOFKeysList.get(i));
			}
		}
		return array2List;
	}

	public static Map<String, Object> jsonString2Map(Element parentElement, String jsonString, boolean firstTime, String prependKey)
			throws JSONException, IOException {
		LinkedHashMap<String, Object> keys = new LinkedHashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + jsonString);
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
		};

		LinkedHashMap<String, ?> o = mapper.readValue(jsonString, typeRef);
		int totalSize = o.size();
		parentElement.getChild("paramTypesSize").removeContent();
		parentElement.getChild("paramTypesSize").addContent(totalSize - 1 + "");
		int depth = 0;
		for (String key : o.keySet()) {
			String actualKey = key;
			if (null != prependKey) {
				actualKey = prependKey + "_" + key;
			}
			if (!key.equalsIgnoreCase("required")) {
				Object value = o.get(key);
				if (value instanceof LinkedHashMap) {
					System.out.println("Incomin value is of JSONObject : " + value);
					ObjectMapper objectMapper = new ObjectMapper();
					// Set pretty printing of json
					objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
					String mapToJson = objectMapper.writeValueAsString(value);
					System.out.println(mapToJson);
					// parentElement = updateWithObjectElements(parentElement,
					// depth, (((LinkedHashMap) value).size()-1), actualKey);
					keys.put(key, jsonString2Map(updateWithObjectElements(parentElement, depth, totalSize, actualKey), mapToJson, false, actualKey));
				} else if (value instanceof ArrayList) {
					System.out.println("Incomin value is of JSONArray : " + value);
					ObjectMapper objectMapper = new ObjectMapper();
					// Set pretty printing of json
					objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
					String mapToJson = objectMapper.writeValueAsString(value);
					System.out.println(mapToJson);
					// parentElement= updateWithArrayElements(parentElement,
					// depth, 1, actualKey);
					// JSONArray jsonArray = new JSONArray(value.toString());
					keys.put(key, jsonArray2List(mapToJson, updateWithArrayElements(parentElement, depth, 1, actualKey), actualKey));
				} else {
					if (firstTime) {
						firstTime = false;
						listChildrenForElementValue(parentElement, 0, key, value.toString(), totalSize + "");
					} else {
						listChildrenForElementValue(parentElement, 0, key, value.toString(), totalSize + "");
					}
				}
				depth++;
			}

		}
		return keys;
	}

	public static Map<String, Object> jsonString2MapForComplexValue(Element parentElement, String jsonString, boolean firstTime, String prependKey)
			throws JSONException, IOException {
		LinkedHashMap<String, Object> keys = new LinkedHashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + jsonString);
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
		};

		LinkedHashMap<String, ?> o = mapper.readValue(jsonString, typeRef);
		int totalSize = o.size();
		parentElement.getChild("valuesSize").removeContent();
		parentElement.getChild("valuesSize").addContent(totalSize - 1 + "");
		for (String key : o.keySet()) {
			String actualKey = key;
			if (null != prependKey) {
				actualKey = prependKey + "_" + key;
			}
			if (!key.equalsIgnoreCase("required")) {
				// String key = (String) keyset.next();
				Object value = o.get(key);
				if (value instanceof LinkedHashMap) {
					System.out.println("Incomin value is of JSONObject : " + value);
					ObjectMapper objectMapper = new ObjectMapper();
					// Set pretty printing of json
					objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
					String mapToJson = objectMapper.writeValueAsString(value);
					System.out.println(mapToJson);
					// updateWithObjectElementsForComplexValue(parentElement, 0,
					// (((LinkedHashMap) value).size()-1), actualKey);
					keys.put(
							key,
							jsonString2MapForComplexValue(
									updateWithObjectElementsForComplexValue(parentElement, 0, ((LinkedHashMap) value).size() - 1, actualKey),
									mapToJson, false, actualKey));
				} else if (value instanceof ArrayList) {
					System.out.println("Incomin value is of JSONArray : " + value);
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
					String mapToJson = objectMapper.writeValueAsString(value);
					System.out.println(mapToJson);
					/*
					 * updateWithArrayElementsForComplexValue(parentElement, 0,
					 * (((ArrayList) value).size()-1), actualKey);
					 */
					// JSONArray jsonArray = new JSONArray(value.toString());
					keys.put(
							key,
							jsonArray2ListForComplexValue(mapToJson,
									updateWithArrayElementsForComplexValue(parentElement, 0, ((ArrayList) value).size() - 1, actualKey), actualKey));
				} else {
					if (firstTime) {
						firstTime = false;
						listChildrenForComplexValue(parentElement, 0, actualKey, value.toString(), totalSize + "");
					} else {
						listChildrenForComplexValue(parentElement, 0, actualKey, value.toString(), totalSize + "");
					}
				}
			}
		}
		return keys;
	}

	private static Element listChildrenForComplexValue(Element parentElement, int depth, String columnName, String columnValue, String objectSize)
			throws IOException {

		Document elementForStringorInteger = null;

		if (Util.checkNumberOnly(columnValue)) {
			elementForStringorInteger = new XMLElementBuilder().loadElementValueTemplateXML("integerTypeValueTemplateXML.xml");
		} else {
			elementForStringorInteger = new XMLElementBuilder().loadElementValueTemplateXML("stringTypeValueTemplateXML.xml");
		}
		// innerMostElementForValueSize.setText((Integer.parseInt(objectSize)-1)+"");
		elementForStringorInteger.getRootElement().getChild("ComplexValue").getChild("StringValue").getChild("columnName").setText(columnName);
		parentElement.addContent(elementForStringorInteger.getRootElement().detach());
		return parentElement;
	}

	private static Element listChildrenForElementValue(Element parentElement, int depth, String columnName, String columnValue, String objectSize)
			throws IOException {
		Document elementForStringOrInteger = null;
		if (Util.checkNumberOnly(columnValue)) {
			elementForStringOrInteger = new XMLElementBuilder().loadElementValueTemplateXML("integerTypeTemplateXML.xml");
		} else {
			elementForStringOrInteger = new XMLElementBuilder().loadElementValueTemplateXML("stringTypeTemplateXML.xml");
		}

		// innerMostParamTypeSizeElementList.stream().forEach(paramTypeSize ->{
		// paramTypeSize.removeContent();
		// paramTypeSize.addContent(objectSize);});
		/*
		 * parentElement.getChild("paramTypesSize").removeContent();
		 * parentElement
		 * .getChild("paramTypesSize").addContent((Integer.parseInt(
		 * objectSize)-1)+"");
		 */
		elementForStringOrInteger.getRootElement().getChild("localName").setText(columnName);
		parentElement.addContent(elementForStringOrInteger.getRootElement().detach());

		return parentElement;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static Element listChildrenForTestSuite(Element current, int depth, AtomicInteger increment, ConfigurationTO configurationTO,
			boolean firstTime) throws Exception {

		Element testID = null, testID1 = null, httpClientEndPoint = null;
		Element docDelivery = null, ftpDeliveries = null, restClient = null, nameValuePair = null;
		Element restClientToolTest = null, messagingSchema = null, testsSize = null, profileMappingIDEle = null;
		Element fileWriterProperties = null, nameValuePropertiesForQueryParams = null;
		Element ftpDeliveriesRestClient = null, urlPathParametersLiteralElement = null;
		Element outputToolsSize = null, genericAssertionTool = null, conditionalAssertionSize = null, httpMethod = null;
		List<Element> commonHttpProperties = new ArrayList<Element>();
		IteratorIterable<Content> descendantsOfChannel = current.getDescendants();

		AtomicInteger profileMappingId = new AtomicInteger();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				// System.out.println(child.getName());
				if (child.getName().equalsIgnoreCase("testID")) {
					testID = child;
				} else if (child.getName().equalsIgnoreCase("name") && child.getTextNormalize().equals("DocDelivery.1.raml")) {
					docDelivery = child;

				} else if (child.getName().equalsIgnoreCase("name") && child.getTextNormalize().equals("ftpdeliveries-positive-testSuite")) {
					ftpDeliveries = child;

				} else if (child.getName().equalsIgnoreCase("name") && child.getTextNormalize().equals("ftpdeliveries-positive")) {
					ftpDeliveriesRestClient = child;

				} else if (child.getName().equalsIgnoreCase("testID") && child.getTextNormalize().equals("74")) {
					testID1 = child;
				} else if (child.getName().equalsIgnoreCase("name") && child.getTextNormalize().equals("REST Client")) {
					restClient = child;
				} /*
				 * else if (child.getName().equalsIgnoreCase("dataSourceName")
				 * && child.getTextNormalize().equals("Pos-Mailing")) { Element
				 * restClientElement = (Element) child.getParent() .getParent();
				 * dataSourceName = child;
				 * 
				 * }
				 */else if (child.getName().equalsIgnoreCase("HTTPClient_Endpoint")) {
					if (child.getText().contains("/ftpdeliveries-template")) {
						System.out.println("else if (child.getName().equalsIgnoreCase" + child.getText());
						httpClientEndPoint = child;
					}
				} else if (child.getName().equalsIgnoreCase("NameValuePair") && child.getChild("name").getTextTrim().equals("Authorization")) {
					nameValuePair = child;
					// Element restClientElement = (Element)
					// child.getParent().getParent();

				} else if (child.getName().equalsIgnoreCase("UrlPathParametersLiteral") && child.getTextTrim().equalsIgnoreCase("template")) {
					urlPathParametersLiteralElement = child;
				} else if (child.getName().equalsIgnoreCase("RESTClientToolTest")) {
					restClientToolTest = child;
				} else if (child.getName().equalsIgnoreCase("profileMappingID")) {
					profileMappingIDEle = child;
				} else if (child.getName().equalsIgnoreCase("FileStreamWriter")) {
				} else if (child.getName().equalsIgnoreCase("NameValueProperties")
						&& child.getTextTrim().equalsIgnoreCase("templateforNameValuePair")) {
					nameValuePropertiesForQueryParams = child;
				} else if (child.getName().equalsIgnoreCase("MessagingSchemaElement")) {
					if (child.getChildren().size() > 0) {
						System.out.println(child.getChildren());
						// System.out.println(child.getChild("elementTypeName").getTextTrim());
						if (child.getChildren().get(0).getName().equals("elementTypeName")) {

							// getChild("elementTypeName").getTextTrim().equalsIgnoreCase("template")){
							messagingSchema = child;
						}
						if (child.getChildren().get(0).getName().equals("ElementValue")) {
						}
					}

				} else if (child.getName().equalsIgnoreCase("testsSize")) {
					testsSize = child;

				} else if (child.getName().equalsIgnoreCase("outputToolsSize") && child.getText().equalsIgnoreCase("1-assertiontemplate")) {

					outputToolsSize = child;
				} else if (child.getName().equalsIgnoreCase("GenericAssertionTool")) {
					genericAssertionTool = child;
				} else if (child.getName().equalsIgnoreCase("assertionsSize") && child.getText().equalsIgnoreCase("1-template")) {
					conditionalAssertionSize = child;
				} else if (child.getName().equalsIgnoreCase("RESTResourceMethod")) {
					httpMethod = child.getChild("httpMethod");

				} else if (child.getName().equalsIgnoreCase("method") && child.getText().contains("POST-template")) {
					// System.out.println(child.getChild("method"));
					commonHttpProperties.add(child);

				}

				/*
				 * else if(child.getName().equalsIgnoreCase("testRunsSize") &&
				 * child.getText().equals("template")){ testRunsSize = child; }
				 */
			}
		}

		if (httpMethod != null) {
			httpMethod.removeContent();
			httpMethod.addContent(configurationTO.getMethod());
		}

		commonHttpProperties.forEach(commonProperty -> {
			commonProperty.removeContent();
			commonProperty.addContent(configurationTO.getMethod().toUpperCase());
		});

		if (testID != null) {
			testID.removeContent();
			testID.addContent(increment.getAndIncrement() + "");
		}
		if (docDelivery != null) {
			docDelivery.removeContent();
			docDelivery.addContent(configurationTO.getRamlFileName());
		}
		ftpDeliveries.removeContent();
		ftpDeliveries.addContent(configurationTO.getEndPointUrl() + " - " + configurationTO.getMethod());
		ftpDeliveriesRestClient.removeContent();
		ftpDeliveriesRestClient.addContent(configurationTO.getEndPointUrl());
		if (testID1 != null) {
			testID1.removeContent();
			testID1.addContent(increment.getAndIncrement() + "");
		}

		restClient.removeContent();
		restClient.addContent("Kalpana testing REST Client");

		profileMappingIDEle.removeContent();
		profileMappingIDEle.addContent(profileMappingId.getAndIncrement() + "");

		nameValuePair.getChild("MultiValue").getChild("StringTestValue").getChild("value").removeContent();
		nameValuePair.getChild("MultiValue").getChild("StringTestValue").getChild("value").addContent("${TOKEN}");
		if (httpClientEndPoint != null) {
			httpClientEndPoint.removeContent();
			String paramStr = "";
			if (configurationTO.getQueryParameters() != null) {

				for (int i = 0; i < configurationTO.getQueryParameters().size(); i++) {
					Parameter parameter = configurationTO.getQueryParameters().get(i);
					if (i == configurationTO.getQueryParameters().size() - 1) {
						paramStr = paramStr + parameter.displayName().toString() + "=${" + parameter.displayName().toString() + "}";
					} else {
						paramStr = paramStr + parameter.displayName().toString() + "=${" + parameter.displayName().toString() + "}&";
					}
				}
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl() + "?" + paramStr);
			} else {
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl());
			}
		}

		testsSize.removeContent();
		// testsSize.addContent(configurationTO.getDataSource().size()+"");
		testsSize.addContent("1");
		if (nameValuePropertiesForQueryParams != null) {

			if (configurationTO.getQueryParameters() != null) {
				nameValuePropertiesForQueryParams.removeContent();
				buildNameValuePropertiesForQueryParama(nameValuePropertiesForQueryParams, configurationTO.getQueryParameters());
			} else {
				nameValuePropertiesForQueryParams.removeContent();
			}
		}
		/*
		 * if(testRunsSize != null){ buildTestRuns(); }
		 */

		Element fileWriterPropertiesPath = null;
		if (fileWriterProperties != null) {
			IteratorIterable<Content> descendantsOfileWriter = fileWriterProperties.getDescendants();

			for (Content descendant : descendantsOfileWriter) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("path")) {
						fileWriterPropertiesPath = child;
					}
				}
			}
		}
		if (fileWriterPropertiesPath != null) {
			fileWriterPropertiesPath.removeContent();
			fileWriterPropertiesPath.addContent("$OUTPUT" + configurationTO.getEndPointUrl());
		}

		restClientToolTest.getChild("name").removeContent();
		restClientToolTest.getChild("name").addContent(configurationTO.getDataSourcePath() + " - " + configurationTO.getMethod());

		outputToolsSize.removeContent();
		outputToolsSize.addContent("1");

		/** Kalpana to be uncommented later */
		try {
			buildRESTClientToolTest(restClientToolTest, messagingSchema, configurationTO, firstTime, increment, urlPathParametersLiteralElement,
					genericAssertionTool, conditionalAssertionSize);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return current;
	}

	private static void updateAllOtherValueInsideRestClientToolTestTag(Element messagingSchema, ConfigurationTO configurationTO) {
		IteratorIterable<Content> descendantsOfChannel = messagingSchema.getDescendants();
		Element httpClientEndPoint = null;
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("HTTPClient_Endpoint")) {
					if (child.getText().contains("/ftpdeliveries-template")) {

						httpClientEndPoint = child;

					}
				}
			}
		}

		if (httpClientEndPoint != null) {
			httpClientEndPoint.removeContent();
			String paramStr = "";
			if (configurationTO.getQueryParameters() != null) {
				/** remove the elementTYpe xml tag under messaging schema */
				if (messagingSchema.getChildren().get(0).getName().equalsIgnoreCase("ElementType")) {
					messagingSchema.getChild("ElementValue").removeChild("ElementType");
					messagingSchema.getChild("ElementValue").addContent(new Element("hasReference").addContent("true"));
					messagingSchema.getChild("ElementValue").addContent(new Element("qnameAsString").addContent(":root"));
				}
				for (int i = 0; i < configurationTO.getQueryParameters().size(); i++) {
					Parameter parameter = configurationTO.getQueryParameters().get(i);
					if (i == configurationTO.getQueryParameters().size() - 1) {
						paramStr = paramStr + parameter.displayName().toString() + "=${" + parameter.displayName().toString() + "}";
					} else {
						paramStr = paramStr + parameter.displayName().toString() + "=${" + parameter.displayName().toString() + "}&amp;";
					}
				}
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl() + "?" + paramStr);
				System.out.println("paramStr============>" + paramStr);
			} else {
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl());
			}
		}

	}

	private static void updatedHashTagValues(Element messagingSchema) {
		IteratorIterable<Content> descendantsOfChannel = messagingSchema.getDescendants();
		AtomicInteger hashValue = new AtomicInteger();
		ArrayList<Element> hashValueElementList = new ArrayList<Element>();

		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element element = (Element) descendant;
				if (element.getName().equals("hash")) {
					// System.out.println("kalpana testing Hash value ===========>"+element.getText());
					// hashTag = element;
					hashValueElementList.add(element);
					// element.removeContent();
					// element.addContent(hashValue.getAndIncrement()+"");
				}
			}
		}

		hashValueElementList.stream().forEachOrdered(hashElement -> {
			hashElement.removeContent();
			hashElement.addContent(hashValue.getAndIncrement() + "");
		});
	}

	private static void updateMessagingSchemaForEmptyRequest(Element messagingSchema, String jsonString) throws JsonParseException,
			JsonMappingException, IOException {
		new LinkedHashMap<String, Object>();
		new JSONObject();
		new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + jsonString);
		new TypeReference<LinkedHashMap<String, Object>>() {
		};

		new ArrayList<String>();
		if (jsonString == null) {
			messagingSchema.getChild("ElementValue").getChild("ElementType").getChild("ComplexType").getChild("AllCompositor")
					.removeChild("paramTypesSize");
		}

	}

	@SuppressWarnings("unused")
	public static Element updateRestToolTest(Element current, AtomicInteger increment, ConfigurationTO configurationTO)
			throws ParserConfigurationException, SAXException, IOException {

		Element testID = null, testID1 = null, httpMethodTestValue = null, httpClientEndPoint = null, httpMethod = null;
		Element docDelivery = null, ftpDeliveries = null, restClient = null, nameValuePair = null;
		List<Element> commonHttpProperties = new ArrayList<Element>();

		IteratorIterable<Content> descendantsOfChannel = current.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("name") && child.getText().equalsIgnoreCase("ftpdeliveries-positive")) {
					ftpDeliveries = child;
					// child.removeContent();
					// child.addContent(configurationTO.getEndPointUrl());

				} else if (child.getName().equalsIgnoreCase("testID") && child.getText().equals("74")) {
					testID1 = child;
				} else if (child.getName().equalsIgnoreCase("name") && child.getText().equals("REST Client")) {
					restClient = child;
				} else if (child.getName().equalsIgnoreCase("HTTPClient_Endpoint")
						&& child.getTextTrim().contains("http://ocp.api.sys.td.com/com-td-ocp-delivery-api/ftpdeliveries-template")) {
					httpClientEndPoint = child;

				} else if (child.getName().equalsIgnoreCase("HTTPMethodTestValue")) {
					httpMethodTestValue = child;

				} else if (child.getName().equalsIgnoreCase("NameValuePair") && child.getChild("name").getTextTrim().equals("Authorization")) {
					nameValuePair = child;
					// Element restClientElement = (Element)
					// child.getParent().getParent();

				} else if (child.getName().equalsIgnoreCase("RESTClientToolTest")) {
				} else if (child.getName().equalsIgnoreCase("RESTResourceMethod")) {
					httpMethod = child.getChild("httpMethod");

				} else if (child.getName().equalsIgnoreCase("UrlPathParametersLiteral") && child.getText().equalsIgnoreCase("template")) {
				} else if (child.getName().equalsIgnoreCase("method") && child.getText().contains("POST-template")) {
					// System.out.println(child.getChild("method"));
					commonHttpProperties.add(child);

				}
				/*
				 * else
				 * if(child.getName().equalsIgnoreCase("CommonHTTPProperties")){
				 * commonHttpProperties =
				 * child.getChild("MultiValue").getChild("HTTPMethodTestValue"
				 * ).getChild("method");
				 * 
				 * }
				 */

			}
		}
		if (httpMethod != null) {
			httpMethod.removeContent();
			httpMethod.addContent(configurationTO.getMethod());
		}
		commonHttpProperties.forEach(commonProperty -> {
			commonProperty.removeContent();
			commonProperty.addContent(configurationTO.getMethod().toUpperCase());
		});
		if (testID != null) {
			testID.removeContent();
			testID.addContent(increment.getAndIncrement() + "");
		}
		if (docDelivery != null) {
			docDelivery.removeContent();
			docDelivery.addContent(configurationTO.getRamlFileName());
		}
		if (ftpDeliveries != null) {
			ftpDeliveries.removeContent();
			ftpDeliveries.addContent(configurationTO.getEndPointUrl() + " - " + configurationTO.getMethod());
		}
		if (testID1 != null) {
			testID1.removeContent();
			testID1.addContent(increment.getAndIncrement() + "");
		}

		restClient.removeContent();
		restClient.addContent("Kalpana testing REST Client");

		// dataSourceName.removeContent();
		// dataSourceName.addContent(configurationTO.getDataSource());
		if (httpMethodTestValue != null) {
			httpMethodTestValue.getChild("method").removeContent();
			httpMethodTestValue.getChild("method").addContent(configurationTO.getMethod());
		}

		nameValuePair.getChild("MultiValue").getChild("StringTestValue").getChild("value").removeContent();
		nameValuePair.getChild("MultiValue").getChild("StringTestValue").getChild("value").addContent("${TOKEN}");

		/*
		 * if(httpClientEndPoint !=null){ httpClientEndPoint.removeContent();
		 * httpClientEndPoint.addContent(configurationTO.getEndPointUrl());
		 * 
		 * }
		 */

		if (httpClientEndPoint != null) {
			httpClientEndPoint.removeContent();
			String paramStr = null;
			if (configurationTO.getQueryParameters() != null) {
				for (int i = 0; i < configurationTO.getQueryParameters().size(); i++) {
					Parameter parameter = configurationTO.getQueryParameters().get(i);
					if (i == configurationTO.getQueryParameters().size() - 1) {
						paramStr = paramStr + parameter.toString() + "=${" + parameter.toString() + "}";
					} else {
						paramStr = paramStr + parameter.toString() + "=${" + parameter.toString() + "}&amp;";
					}
				}
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl() + "?" + paramStr);
			} else {
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl());
			}
		}
		return current;

	}

	public static Element updateTemplateXMLForTestSuite(Element testSuiteMain, AtomicInteger incrementerForTestID, ConfigurationTO configurationTO,
			boolean firstTime) {

		try {
			listChildrenForTestSuite(testSuiteMain, 0, incrementerForTestID, configurationTO, firstTime);
		} catch (ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(testSuiteMain);
		return testSuiteMain;
	}

	/**
	 * This method create the URL path parameter in XML form for example if the
	 * endpoint url is /locators/streetaddresses/{streetAddressId} it would
	 * create three multivalue elements
	 *
	 * @param urlPathParametersLiteralElement
	 *            Parent element where urlpath needs to be attached to
	 * @param configurationTO
	 * @throws IOException
	 * @see IOException
	 */
	public static void updateUrlPathParametersLiteral(Element urlPathParametersLiteralElement, ConfigurationTO configurationTO) throws IOException {
		Element UrlPathParamsMultiValue = null;
		if (urlPathParametersLiteralElement != null) {
			urlPathParametersLiteralElement.removeContent();
			String[] urlPaths = Util.tokenizePathURLEndpoint(configurationTO.getEndPointUrl());
			urlPathParametersLiteralElement.addContent(new Element("pathElementss").setAttribute("size", urlPaths.length - 1 + ""));

			for (int i = 1; i < urlPaths.length; i++) {

				System.out.println("paths::" + i + " " + urlPaths[i] + "\n");
				if (urlPaths[i].startsWith("{")) {
					UrlPathParamsMultiValue = new XMLElementBuilder().loadElementValueTemplateXML("urlPathParamsMultiValue.xml").detachRootElement();
					// UrlPathParamsMultiValue = document.getRootElement();
					UrlPathParamsMultiValue.getChild("StringTestValue").getChild("value").removeContent();
					UrlPathParamsMultiValue.getChild("StringTestValue").getChild("value").addContent(XMLJsonUtils.removebraces(urlPaths[i]));
					UrlPathParamsMultiValue.getChild("ParameterizedTestValue").addContent(
							new Element("column").addContent(XMLJsonUtils.removebraces(urlPaths[i])));
					UrlPathParamsMultiValue.addContent(4, new Element("selectedIndex").addContent("-2"));

					urlPathParametersLiteralElement.getChild("pathElementss").addContent(UrlPathParamsMultiValue);

				} else {
					UrlPathParamsMultiValue = new XMLElementBuilder().loadElementValueTemplateXML("urlPathParamsMultiValue.xml").detachRootElement();
					// UrlPathParamsMultiValue = document.getRootElement();
					UrlPathParamsMultiValue.getChild("StringTestValue").getChild("value").removeContent();
					UrlPathParamsMultiValue.getChild("StringTestValue").getChild("value").addContent(urlPaths[i]);
					urlPathParametersLiteralElement.getChild("pathElementss").addContent(UrlPathParamsMultiValue);

				}
			}
		}
	}

	private static Element updateWithArrayElements(Element parentElement, int depth, int valueSize, String columnName) throws IOException {
		Document elementForArray = new XMLElementBuilder().loadElementValueTemplateXML("arrayTypeTemplateXML.xml");
		elementForArray.getRootElement().getChild("localName").setText(columnName);
		elementForArray.getRootElement().getChild("ComplexType").getChild("SequenceCompositor").getChild("paramTypesSize").removeContent();
		elementForArray.getRootElement().getChild("ComplexType").getChild("SequenceCompositor").getChild("paramTypesSize").addContent(valueSize + "");
		elementForArray.getRootElement().getChild("localName").removeContent();
		elementForArray.getRootElement().getChild("localName").addContent(columnName);
		Element parentElementToReturn = null;
		IteratorIterable<Content> descendantsOfChannel = elementForArray.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element element = (Element) descendant;
				if (element.getName().equals("AllCompositor")) {
					parentElementToReturn = element;
				}
			}
		}
		parentElement.addContent(elementForArray.getRootElement().detach());

		return parentElementToReturn;
	}

	private static Element updateWithArrayElementsForComplexValue(Element parentElement, int depth, int valueSize, String columnName)
			throws IOException {
		// printSpaces(depth);
		/*
		 * Element innerMostChildForComplexType = null;
		 * IteratorIterable<Content> descendantsOfChannel = current
		 * .getDescendants(); for (Content descendant : descendantsOfChannel) {
		 * if (descendant.getCType().equals(Content.CType.Element)) { Element
		 * child = (Element) descendant; if
		 * (child.getName().equalsIgnoreCase("ComplexValue")) { //Do nothing
		 * IteratorIterable<Content> descendantsOfChannel1 =
		 * child.getDescendants(); for (Content descendant1 :
		 * descendantsOfChannel1) { if
		 * (descendant1.getCType().equals(Content.CType.Element)) { Element
		 * element = (Element) descendant1; if
		 * (element.getName().equals("valuesSize") ) { System.out
		 * .println("\n valuesSize--------------->" + element.getText()); //
		 * innerMostChildForComplexType = (Element) element.getParent(); //
		 * prints all urls of all thumbnails within the // 'media' namespace } }
		 * }
		 * 
		 * } } }
		 */
		Document elementForArray = new XMLElementBuilder().loadElementValueTemplateXML("arrayTypeValueTemplateXML.xml");
		Element parentElementToReturn = null, valueSizeEle = null;
		IteratorIterable<Content> descendantsOfChannel = elementForArray.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element element = (Element) descendant;
				if (element.getName().equals("CompositorValueSet")) {
					parentElementToReturn = element;
				} else if (element.getName().equals("valuesSize") && element.getText().equalsIgnoreCase("template")) {
					valueSizeEle = element;
				}
			}
		}
		valueSizeEle.removeContent();
		valueSizeEle.addContent(valueSize + "");
		parentElement.addContent(elementForArray.getRootElement().detach());

		return parentElementToReturn;
	}

	private static Element updateWithObjectElements(Element parentElement, int depth, int valueSize, String columnName) throws IOException {
		Document elementForObject = new XMLElementBuilder().loadElementValueTemplateXML("objectTypeTemplateXML.xml");
		elementForObject.getRootElement().getChild("localName").removeContent();
		elementForObject.getRootElement().getChild("localName").addContent(columnName);

		Element elementToReturn = elementForObject.getRootElement().getChild("ComplexType").getChild("AllCompositor");
		parentElement.getChild("paramTypesSize").removeContent();
		parentElement.getChild("paramTypesSize").addContent(valueSize - 1 + "");
		parentElement.addContent(elementForObject.detachRootElement());
		return elementToReturn;
	}

	private static Element updateWithObjectElementsForComplexValue(Element parentElement, int depth, int valueSize, String columnName)
			throws IOException {

		Document elementForObject = new XMLElementBuilder().loadElementValueTemplateXML("objectTypeValueTemplateXML.xml");
		// parentElement.getChild("valuesSize").removeContent();
		// parentElement.getChild("valuesSize").addContent(valueSize + "");
		Element elementToReturn = elementForObject.getRootElement().getChild("ComplexValue").getChild("CompositorValue")
				.getChild("CompositorValueSetCollectionSet").getChild("CompositorValueSet");
		parentElement.addContent(elementForObject.detachRootElement());
		return elementToReturn;
	}

	public Document loadElementValueTemplateXML(String templateName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		//File xmlFile = new File(classLoader.getResourceAsStream(templateName));
		//File xmlFile = new File(templateName);
		// File xmlFile = new
		// File("C:/Kalpana/TD Bank/InputOutput/inputTSTFileTemplate.xml");
		Document document = null;
		//if (xmlFile.exists()) {
			// try to load document from xml file if it exist
			// create a file input stream
			//FileInputStream fis = new FileInputStream(classLoader.getResourceAsStream(templateName));
			SAXBuilder builder = new SAXBuilder();
			try {
				//document = builder.build(xmlFile);
				document = builder.build(classLoader.getResourceAsStream(templateName));
			}
			// indicates a well-formedness error
			catch (JDOMException e) {
				System.out.println(" is not well-formed.");
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e);
			}

			//fis.close();
		//}
		return document;
	}

	public Document loadElementValueTemplateXML_DOM(String templateName) throws ParserConfigurationException, SAXException, IOException {
		new File("C:/Users/xxx/Desktop/ff.xml");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(templateName);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = (Document) documentBuilder.parse(is);
		return document;
	}

	public Document loadIncomingTSTFile(String incomingTstFile) throws IOException {
		File xmlFile = new File(incomingTstFile);

		Document document = null;
		if (xmlFile.exists()) {

			FileInputStream fis = new FileInputStream(xmlFile);
			SAXBuilder builder = new SAXBuilder();
			try {
				document = builder.build(xmlFile);

			}
			// indicates a well-formedness error
			catch (JDOMException e) {
				System.out.println(" is not well-formed.");
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e);
			}

			fis.close();
		}
		return document;
	}

	public Element loadTestSuiteTemplateXML() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File xmlFile = new File(classLoader.getResource("testSuiteTemplate.xml").getFile());
		// File xmlFile = new
		// File("C:/Kalpana/TD Bank/InputOutput/inputTSTFileTemplate.xml");
		Document document = null;
	//	if (xmlFile.exists()) {
			// try to load document from xml file if it exist
			// create a file input stream
			//FileInputStream fis = new FileInputStream(xmlFile);
			SAXBuilder builder = new SAXBuilder();
			try {
				document = builder.build(classLoader.getResourceAsStream("testSuiteTemplate.xml"));

			}
			// indicates a well-formedness error
			catch (JDOMException e) {
				System.out.println(" is not well-formed.");
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e);
			}

			//fis.close();
		//}
		return document.getRootElement();
	}

}
