package com.ibm.automation.parasoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.ibm.automation.parasoft.util.XMLJsonUtils;
import com.ibm.automation.parasoft.util.Util;

public class XMLElementBuilder {

	public static Element buildTestSuiteElement(String name, String testPath) {

		Element testSuiteElement = new Element("TestSuite");
		testSuiteElement.setAttribute(new Attribute("name", name));
		testSuiteElement
		.setAttribute(new Attribute("runConcurrently", "false"));
		testSuiteElement.setAttribute(new Attribute("testPath", testPath));
		// mainElement.addContent(testSuiteElement);
		return testSuiteElement;

	}

	public static Element buildTestElement(String name, String testPath) {

		Element testElement = new Element("Test");
		testElement.setAttribute(new Attribute("testPath", testPath));
		testElement.setAttribute(new Attribute("name", name));
		return testElement;

	}

	public static Element buildDataSourceElement(String name, String type) {

		Element dataSouceElement = new Element("DataSource");
		dataSouceElement.setAttribute(new Attribute("name", name));
		dataSouceElement.setAttribute(new Attribute("type", type));
		return dataSouceElement;

	}

	public static Element buildHeadersElementByEnvVariable(
			String authorization, String messageId, String traceabilityId) {

		Element headersElement = new Element("Headers");
		headersElement.addContent(new Element("Header").setAttribute("name",
				authorization).addContent(
						"${" + authorization.toUpperCase() + "}"));
		headersElement.addContent(new Element("Header").setAttribute("name",
				messageId).addContent("${" + messageId.toUpperCase() + "}"));
		headersElement.addContent(new Element("Header").setAttribute("name",
				traceabilityId).addContent(
						"${" + traceabilityId.toUpperCase() + "}"));

		return headersElement;

	}

	public static Element buildHeadersElementParameterised(
			String authorization, String messageId, String traceabilityId) {

		Element headersElement = new Element("Headers");
		headersElement.addContent(new Element("Header").setAttribute("name",
				authorization).addContent("Parameterized: " + authorization));
		headersElement.addContent(new Element("Header").setAttribute("name",
				messageId).addContent("Parameterized: " + messageId));
		headersElement.addContent(new Element("Header").setAttribute("name",
				traceabilityId).addContent("Parameterized: " + traceabilityId));

		return headersElement;

	}

	public static Element buildDataSourceConfigElement(String resource) {

		Element configurationElement = new Element("Configuration");
		Element resourceElement = new Element("Resource");
		resourceElement.addContent(resource);
		configurationElement.addContent(resourceElement);

		return configurationElement;

	}

	public static Element buildConfigurationTypeElement(String type,
			String dataSource, String validRange, String url,
			String hTTPMethod, String resourceMode, String serviceDefinitionUrl) {

		Element configurationTypeElement = new Element("Configuration");
		configurationTypeElement.setAttribute(new Attribute("type", type));
		configurationTypeElement.setAttribute(new Attribute("dataSource",
				dataSource));
		configurationTypeElement.setAttribute(new Attribute("validRange",
				validRange));
		configurationTypeElement.setAttribute(new Attribute("Url", url));
		configurationTypeElement.setAttribute(new Attribute("HTTPMethod",
				hTTPMethod));
		configurationTypeElement.setAttribute(new Attribute("ResourceMode",
				resourceMode));
		configurationTypeElement.setAttribute(new Attribute(
				"ServiceDefinitionUrl", serviceDefinitionUrl));

		return configurationTypeElement;

	}

	public static Element buildConfigurationEndPointElement(String endpoint,
			String chunking, String followRedirects, String connection,
			String method) {

		Element configurationEndPointElement = new Element("Configuration");
		configurationEndPointElement.setAttribute(new Attribute("endpoint",
				endpoint));
		configurationEndPointElement.setAttribute(new Attribute("chunking",
				chunking));
		configurationEndPointElement.setAttribute(new Attribute(
				"followRedirects", followRedirects));
		configurationEndPointElement.setAttribute(new Attribute("connection",
				connection));
		configurationEndPointElement.setAttribute(new Attribute("method",
				method));
		return configurationEndPointElement;

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

	public static Element buildRowElements(
			InputExcelFileVO dataSourceFileContent, Element dataSource) {
		List<String> headerList = dataSourceFileContent.getHeaders();
		List<DataRow> allRowsContent = dataSourceFileContent.getContents();
		final AtomicInteger index = new AtomicInteger();

		try {
			allRowsContent
			.stream()
			.forEach(
					dataRow -> {
						Element row1 = new Element("Row");
						row1.setAttribute("index", index.toString());
						index.incrementAndGet();
						System.out
						.println("dataRow-------->" + dataRow);

						headerList
						.stream()
						.forEach(headerName -> {
							// System.out.println("headerNameheaderNameheaderNameheaderName->"+headerName);
							Element column1 = new Element(
									"Column");
							column1.setAttribute(new Attribute(
									"name", headerName));
							switch (headerName
									.toLowerCase()) {
									case "messageid":
										if (dataRow.get(headerName)
												.isEmpty()) {
											column1.addContent("${MessageID}");
										} else {
											column1.addContent(dataRow
													.get(headerName));
										}
										break;
									case "tracebilityid":
										if (dataRow.get(headerName)
												.isEmpty()) {
											column1.addContent("${TraceabilityID}");
										} else {
											column1.addContent(dataRow
													.get(headerName));
										}
										break;
									default:
										column1.addContent(dataRow
												.get(headerName));
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

	public Element loadTestSuiteTemplateXML() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File xmlFile = new File(classLoader
				.getResource("testSuiteTemplate.xml").getFile());
		// File xmlFile = new
		// File("C:/Kalpana/TD Bank/InputOutput/inputTSTFileTemplate.xml");
		Document document = null;
		if (xmlFile.exists()) {
			// try to load document from xml file if it exist
			// create a file input stream
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
		return document.getRootElement();
	}

	public Document loadIncomingTSTFile(String incomingTstFile)
			throws IOException {
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

	public Document loadElementValueTemplateXML(String templateName)
			throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File xmlFile = new File(classLoader.getResource(templateName).getFile());
		// File xmlFile = new
		// File("C:/Kalpana/TD Bank/InputOutput/inputTSTFileTemplate.xml");
		Document document = null;
		if (xmlFile.exists()) {
			// try to load document from xml file if it exist
			// create a file input stream
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

	public Document loadElementValueTemplateXML_DOM(String templateName) throws ParserConfigurationException, SAXException, IOException{
		File file = new File("C:/Users/xxx/Desktop/ff.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = (Document) documentBuilder.parse(templateName);
		return document;

	}
	public static Element updateTemplateXMLForTestSuite(Element testSuiteMain,
			AtomicInteger incrementerForTestID, ConfigurationTO configurationTO, boolean firstTime) {

		// Element testSuite = doc1.getRootElement();
		try {
			try {
				listChildrenForTestSuite(testSuiteMain, 0, incrementerForTestID,
						configurationTO, firstTime);
			} catch (ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(testSuiteMain);
		return testSuiteMain;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static Element listChildrenForTestSuite(Element current, int depth,
			AtomicInteger increment, ConfigurationTO configurationTO, boolean firstTime)
					throws IOException, ParserConfigurationException, SAXException {

		Element testID = null, testID1 = null, name1 = null, httpMethodTestValue = null, httpClientEndPoint = null;
		Element docDelivery = null, ftpDeliveries = null, restClient = null, dataSourceName = null, nameValuePair = null;
		Element restClientToolTest = null, messagingSchema = null, testsSize=null, profileMappingIDEle=null;
		Element dataSourcesSize = null, fileWriterProperties = null, fileStreamWriter =null, nameValuePropertiesForQueryParams = null;
		Element ftpDeliveriesRestClient = null, pathElementss = null, UrlPathParamsMultiValue=null, urlPathParametersLiteralElement = null;
		Element outputToolsSize = null, jsonAssertionTool=null, conditionalAssertionSize=null;
		IteratorIterable<Content> descendantsOfChannel = current.getDescendants();

		AtomicInteger profileMappingId = new AtomicInteger();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("testID")) {
					testID = child;
				} else if (child.getName().equalsIgnoreCase("name")
						&& child.getTextNormalize()
						.equals("DocDelivery.1.raml")) {
					docDelivery = child;

				} else if (child.getName().equalsIgnoreCase("name")
						&& child.getTextNormalize().equals(
								"ftpdeliveries-positive-testSuite")) {
					ftpDeliveries = child;

				}else if (child.getName().equalsIgnoreCase("name")
						&& child.getTextNormalize().equals(
								"ftpdeliveries-positive")) {
					ftpDeliveriesRestClient = child;

				} else if (child.getName().equalsIgnoreCase("testID")
						&& child.getTextNormalize().equals("74")) {
					testID1 = child;
				} else if (child.getName().equalsIgnoreCase("name")
						&& child.getTextNormalize().equals("REST Client")) {
					restClient = child;
				} /*else if (child.getName().equalsIgnoreCase("dataSourceName")
						&& child.getTextNormalize().equals("Pos-Mailing")) {
					Element restClientElement = (Element) child.getParent()
							.getParent();
					dataSourceName = child;

				}*/ else if (child.getName().equalsIgnoreCase("HTTPClient_Endpoint")){
					if(child.getText().contains("/ftpdeliveries-template")){
						System.out.println("else if (child.getName().equalsIgnoreCase"+child.getText());
						httpClientEndPoint = child;
					}
				} else if (child.getName().equalsIgnoreCase(
						"HTTPMethodTestValue")) {
					httpMethodTestValue = child;

				} else if (child.getName().equalsIgnoreCase("NameValuePair")
						&& child.getChild("name").getTextTrim()
						.equals("Authorization")) {
					nameValuePair = child;
					// Element restClientElement = (Element)
					// child.getParent().getParent();

				}else if(child.getName().equalsIgnoreCase("UrlPathParametersLiteral") && child.getTextTrim().equalsIgnoreCase("template")){ 
					urlPathParametersLiteralElement = child;
				}
				else if (child.getName().equalsIgnoreCase("RESTClientToolTest")) {
					restClientToolTest = child;
				}else if(child.getName().equalsIgnoreCase("profileMappingID")){
					profileMappingIDEle = child;
				}
				else if(child.getName().equalsIgnoreCase("FileStreamWriter")){
					fileStreamWriter = child;
				}else if(child.getName().equalsIgnoreCase("NameValueProperties") && child.getTextTrim().equalsIgnoreCase("templateforNameValuePair")){
					nameValuePropertiesForQueryParams = child;
				}
				else if (child.getName().equalsIgnoreCase(
						"MessagingSchemaElement")) {
					if(child.getChildren().size()>0){
						System.out.println(child.getChildren());
						//System.out.println(child.getChild("elementTypeName").getTextTrim());
						if(child.getChildren().get(0).getName().equals("elementTypeName")){

							//getChild("elementTypeName").getTextTrim().equalsIgnoreCase("template")){
							messagingSchema = child;
						}}

				}else if(child.getName().equalsIgnoreCase("testsSize")){
					testsSize = child;

				}else if(child.getName().equalsIgnoreCase("outputToolsSize") && child.getText().equalsIgnoreCase("1-assertiontemplate")){

					outputToolsSize = child;
				}
				else if(child.getName().equalsIgnoreCase("JSONAssertionTool")){					
					jsonAssertionTool = child;
				}else if(child.getName().equalsIgnoreCase("assertionsSize")&& child.getText().equalsIgnoreCase("1-template")){
					conditionalAssertionSize = child;
				}
				/*else if(child.getName().equalsIgnoreCase("testRunsSize") && child.getText().equals("template")){
					testRunsSize = child;
				}*/
			}}

		if(conditionalAssertionSize != null){
			conditionalAssertionSize.removeContent();
			conditionalAssertionSize.addContent(configurationTO.getResponseSchemaMap().size()+"");
		}

		if (testID != null) {
			testID.removeContent();
			testID.addContent(increment.getAndIncrement() + "");
		}
		if (docDelivery != null) {
			docDelivery.removeContent();
			docDelivery.addContent(configurationTO.getRamlFileName());
		}
		ftpDeliveries.removeContent();
		ftpDeliveries.addContent(configurationTO.getEndPointUrl());
		ftpDeliveriesRestClient.removeContent();
		ftpDeliveriesRestClient.addContent(configurationTO.getEndPointUrl());
		if(testID1 != null){
			testID1.removeContent();
			testID1.addContent(increment.getAndIncrement() + "");
		}

		restClient.removeContent();
		restClient.addContent("Kalpana testing REST Client");

		profileMappingIDEle.removeContent();
		profileMappingIDEle.addContent(profileMappingId.getAndIncrement()+"");
		//dataSourceName.removeContent();
		//dataSourceName.addContent(configurationTO.getDataSource());
		if(httpMethodTestValue != null){
			httpMethodTestValue.getChild("method").removeContent();
			httpMethodTestValue.getChild("method").addContent(
					configurationTO.getMethod());
		}

		nameValuePair.getChild("MultiValue").getChild("StringTestValue")
		.getChild("value").removeContent();
		nameValuePair.getChild("MultiValue").getChild("StringTestValue")
		.getChild("value")
		.addContent("${TOKEN}");
		if(httpClientEndPoint != null){
			httpClientEndPoint.removeContent();
			String paramStr = null;
			if(configurationTO.getQueryParameters() != null){
				for(int i=0; i<  configurationTO.getQueryParameters().size(); i++){
					Parameter parameter = configurationTO.getQueryParameters().get(i);
					if(i == (configurationTO.getQueryParameters().size()-1)){
						paramStr = paramStr+parameter.toString()+"=${"+parameter.toString()+"}";
					}else{
						paramStr = paramStr+parameter.toString()+"=${"+parameter.toString()+"}&amp;";
					}
				}
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl()+"?"+paramStr);
			}else{
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl());
			}
		}

		testsSize.removeContent();
		//testsSize.addContent(configurationTO.getDataSource().size()+"");
		testsSize.addContent("1");
		if(nameValuePropertiesForQueryParams != null ){

			if(configurationTO.getQueryParameters() != null){
				nameValuePropertiesForQueryParams.removeContent();
				buildNameValuePropertiesForQueryParama(nameValuePropertiesForQueryParams, configurationTO.getQueryParameters());
			}else{
				nameValuePropertiesForQueryParams.removeContent();
			}
		}
		/*if(testRunsSize != null){
			buildTestRuns();
		}*/

		Element fileWriterPropertiesPath = null;
		if(fileWriterProperties != null){
			IteratorIterable<Content> descendantsOfileWriter = fileWriterProperties.getDescendants();			

			for (Content descendant : descendantsOfileWriter) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("path")) {
						fileWriterPropertiesPath = child;
					}}}
		}
		if(fileWriterPropertiesPath != null){
			fileWriterPropertiesPath.removeContent();
			fileWriterPropertiesPath.addContent("$OUTPUT"+configurationTO.getEndPointUrl());
		}

		restClientToolTest.getChild("name").removeContent();
		restClientToolTest.getChild("name").addContent(
				configurationTO.getDataSourcePath() + " - "
						+ configurationTO.getMethod());
		
		outputToolsSize.removeContent();
		outputToolsSize.addContent("1");

		/** Kalpana to be uncommented later*/
		try {
			buildRESTClientToolTest(restClientToolTest, messagingSchema, configurationTO, firstTime, increment, urlPathParametersLiteralElement, jsonAssertionTool);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return current;
	}


	private static void buildNameValuePropertiesForQueryParama(Element nameValuePropertiesForQueryParams,
			List<Parameter> queryParametersList) throws IOException {
		Element propertiesSize = new Element("propertiesSize");
		propertiesSize.addContent(queryParametersList.size()+"");

		nameValuePropertiesForQueryParams.addContent(propertiesSize);

		for(int i=0; i<  queryParametersList.size(); i++){
			Parameter parameter = queryParametersList.get(i);

			Element nameValuePairElement = new XMLElementBuilder().loadElementValueTemplateXML("nameValuePair.xml").detachRootElement();

			IteratorIterable<Content> descendantsOfNameValuePair = nameValuePairElement.getDescendants();
			Element name = null, column = null;
			Element secondTimeMessaingSchema = null;
			for (Content descendant : descendantsOfNameValuePair) {
				if (descendant.getCType().equals(Content.CType.Element)) {
					Element child = (Element) descendant;
					if (child.getName().equalsIgnoreCase("name")) {
						name = child;

					}else if(child.getName().equalsIgnoreCase("column")){
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

	private static void buildRESTClientToolTest(Element restClientToolTest, Element messagingSchema,
			ConfigurationTO configurationTO, boolean firstTime, AtomicInteger increment, Element urlPathParametersLiteralElement, Element jsonAssertionTool) throws Exception {
		Element restClientToolTestParent = (Element) restClientToolTest.getParent();

		AtomicInteger firstTimeRestClientToolSet = new AtomicInteger();
		System.out.println("for END POINT URL ------->"+configurationTO.getEndPointUrl()+"\n"+configurationTO.getResponseSchemaString());
		if(configurationTO.getInputSampleString() != null){
		  		Map<String,Object> map = XMLJsonUtils.jsonString2MapForAssertionsWithXPath(configurationTO.getInputSampleString(), null);
					System.out.println("Done==========\n"+map);
		}
		/*		configurationTO.getDataSource().forEach(
				(dataSheetName) -> {*/
		/* for first time we already have RESTClientToolTest 
		 * XML node inside testSuiteTemplate.xml, therefore this if 
		 * loop should be called for first time */
		if(firstTimeRestClientToolSet.get() == 0){
			firstTimeRestClientToolSet.incrementAndGet();
			try {
				messagingSchema.addContent(0, new XMLElementBuilder().loadElementValueTemplateXML("rootElementTemplateXML.xml")
						.getRootElement().detach());

				buildCompositorValueSet(messagingSchema, configurationTO.getInputSampleString());
				updatedHashTagValues(messagingSchema);
				buildComplexValueUnderMessagingSchema(messagingSchema, configurationTO.getInputSampleString());

				/** update messagingSchemaElement with empty <ElementType> and emtpy <ElementValue>
				 * nodes if incoming request 	json is empty,
				 * this should be called only for first time messaging schema(if loop)
				 * , not in else loop
				 */						
				updateMessagingSchemaForEmptyRequest(messagingSchema, configurationTO.getInputSampleString());
				updateUrlPathParametersLiteral(urlPathParametersLiteralElement, configurationTO);
				//Map<String, Object> responseParamsMap = buildAssertionsOnResponseParams(configurationTO.getResponseSchemaString());
				/** set Assertions - START*/
				HashMap<String, String> responseMap = configurationTO.getResponseSchemaMap();
				for (Entry<String, String> entry : responseMap.entrySet()) {
				    String responseCode = entry.getKey();
				    String reponseSchemaContent = entry.getValue();
				    int andAssertionSize = 0;
				    if(reponseSchemaContent!= null && (!reponseSchemaContent.equals(""))){
				    	Element andAssertion = buildResponseConditionalAssertion(responseCode, jsonAssertionTool);
				    	XMLJsonUtils.jsonString2MapForReponseCodeAssertions(responseCode, reponseSchemaContent, null, andAssertion, andAssertionSize);
				    }
				   
				}
/*				if(configurationTO.getResponseSchemaString() !=null){
					
					Map<String,Object> map1 = XMLJsonUtils.jsonString2MapForAssertionsRequiredParams(configurationTO.getResponseSchemaString(), null, andAssertion);					
					//Map<String,Object> map = XMLJsonUtils.jsonString2MapForAssertionsRequiredParams(configurationTO.getResponseSchemaString(), null );
					System.out.println("Done==========\n"+map1);	
					jsonAssertionTool.addContent(andAssertion.detach());
				}*/
				/** set Assertions - END*/
			} catch (Exception e) {
				System.out.println("Error while update restclientoolToolTest XML node for first time"+e.getMessage());
				e.printStackTrace();
			}
		}else {
			/* from second time onwards within a <testSuite> if more than one  RESTClientToolTest xml node is to be built
			 * else loop to be called, to add RESTClientToolTest node afresh from it;s template xml						 * 
			 */
			try {
				//restClientToolTestParent.addContent(new XMLElementBuilder().loadElementValueTemplateXML("restClientToolTestTemplateXML.xml").detachRootElement());
				Element secondRestClientToolTestParent = new XMLElementBuilder().loadElementValueTemplateXML("restClientToolTestTemplateXML.xml").detachRootElement();
				secondRestClientToolTestParent = updateRestToolTest(secondRestClientToolTestParent, increment, configurationTO);
				IteratorIterable<Content> descendantsOfChannel = secondRestClientToolTestParent.getDescendants();
				Element secondTimeMessaingSchema = null, outputToolsSize= null, jsonAssertionToolElse=null;
				for (Content descendant : descendantsOfChannel) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element child = (Element) descendant;
						if (child.getName().equalsIgnoreCase("MessagingSchemaElement")) {
							if(child.getChildren().size()>0){
								if(child.getChildren().get(0).getName().equals("elementTypeName")){
									//getChild("elementTypeName").getTextTrim().equalsIgnoreCase("template")){
									secondTimeMessaingSchema = child;
								}}

						}else if(child.getName().equalsIgnoreCase("outputToolsSize") && child.getText().equalsIgnoreCase("1-assertiontemplate")){
							outputToolsSize = child;
						}
						else if(child.getName().equalsIgnoreCase("JSONAssertionTool")){					
							jsonAssertionToolElse = child;
						}else if(child.getName().equalsIgnoreCase("UrlPathParametersLiteral") && child.getTextTrim().equalsIgnoreCase("template")){ 
							urlPathParametersLiteralElement = child;
						}
					}
				}
				secondTimeMessaingSchema.addContent(0, new XMLElementBuilder().loadElementValueTemplateXML("rootElementTemplateXML.xml").getRootElement().detach());
				Element elementToAddElementValueXML = secondTimeMessaingSchema.getChild("ElementValue");

				//messagingSchema = secondTimeMessaingSchema;
				buildCompositorValueSet(secondTimeMessaingSchema, configurationTO.getInputSampleString());
				restClientToolTestParent.addContent(secondRestClientToolTestParent.detach());
				updatedHashTagValues(secondTimeMessaingSchema);
				buildComplexValueUnderMessagingSchema(secondTimeMessaingSchema,	configurationTO.getInputSampleString());

				/** update messagingSchemaElement with empty <ElementType> and emtpy <ElementValue>
				 * nodes if incoming request json is empty
				 */
				//updateMessagingSchemaForEmptyRequest(secondTimeMessaingSchema, configurationTO.getInputSampleString());
				updateAllOtherValueInsideRestClientToolTestTag(secondTimeMessaingSchema, configurationTO);
				updateUrlPathParametersLiteral(urlPathParametersLiteralElement, configurationTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		//restClientToolTestParent.
		//});


	}

	private static void buildConditionalAssertions() {
		// TODO Auto-generated method stub
		
	}

	private static void updateAllOtherValueInsideRestClientToolTestTag(Element messagingSchema, ConfigurationTO configurationTO) {
		IteratorIterable<Content> descendantsOfChannel = messagingSchema.getDescendants();
		Element httpClientEndPoint = null;
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("HTTPClient_Endpoint")) { 
					if(child.getText().contains("/ftpdeliveries-template")){

						httpClientEndPoint = child;

					} }
			}
		}

		if(httpClientEndPoint != null){
			httpClientEndPoint.removeContent();
			String paramStr = null;
			if(configurationTO.getQueryParameters() != null){
				for(int i=0; i<  configurationTO.getQueryParameters().size(); i++){
					Parameter parameter = configurationTO.getQueryParameters().get(i);
					if(i == (configurationTO.getQueryParameters().size()-1)){
						paramStr = paramStr+parameter.toString()+"=${"+parameter.toString()+"}";
					}else{
						paramStr = paramStr+parameter.toString()+"=${"+parameter.toString()+"}&amp;";
					}
				}
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl()+"?"+paramStr);
			}else{
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
					//System.out.println("kalpana testing Hash value ===========>"+element.getText());
					//hashTag = element;
					hashValueElementList.add(element);
					//element.removeContent();
					//element.addContent(hashValue.getAndIncrement()+"");
				}
			}			
		}

		hashValueElementList.stream().forEachOrdered(hashElement -> {
			hashElement.removeContent();
			hashElement.addContent(hashValue.getAndIncrement()+"");
		});
	}

	@SuppressWarnings("unused")
	private static void buildComplexValueUnderMessagingSchema(Element messagingSchema, String inputSampleString) {

		try {
			Element rootElementObjectType = new XMLElementBuilder()
			.loadElementValueTemplateXML("rootElementValueTemplateXML.xml").getRootElement();
			messagingSchema.getChild("ElementValue").addContent(rootElementObjectType.detach());
			Element elementToAddElementValueXML = messagingSchema.getChild("ElementValue");
			if(inputSampleString == null){

				rootElementObjectType.getChild("CompositorValue").getChild("CompositorValueSetCollectionSet")
				.getChild("CompositorValueSet").removeChild("valuesSize");
			}else {
				Map<String, Object> results = jsonString2MapForComplexValue(elementToAddElementValueXML,
						inputSampleString, true);				

			}
		} catch (IOException e) {
			System.out.println("Error inside buildComplexValueUnderMessagingSchema method"+e.getMessage());
		}

	}

	private static void updateMessagingSchemaForEmptyRequest(
			Element messagingSchema, String jsonString) throws JsonParseException, JsonMappingException, IOException {
		LinkedHashMap<String, Object> keys = new LinkedHashMap<String, Object>();
		JSONObject json = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + jsonString);
		TypeReference<LinkedHashMap<String, Object>> typeRef 
		= new TypeReference<LinkedHashMap<String,Object>>() {};

		// LinkedHashMap<String,?> o = mapper.readValue(jsonString, typeRef); 
		List<String> sortKey = new ArrayList<String>();
		if(jsonString == null){
			messagingSchema.getChild("ElementValue").getChild("ElementType").getChild("ComplexType").getChild("AllCompositor").removeChild("paramTypesSize");
		}


	}

	private static Element buildCompositorValueSet(Element child,
			String inputSampleStringJson) {
		// Element compositorValueSet = new Element("CompositorValueSet");
		try {
			if (inputSampleStringJson != null) {
				Element rootElementObjectType = new XMLElementBuilder()
				.loadElementValueTemplateXML(
						"rootElementTemplateXML.xml").getRootElement();
				Map<String, Object> results = jsonString2Map(child,
						inputSampleStringJson, true);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return child;
	}

	public static Map<String, Object> jsonString2Map(Element incomingElementValueElementForString, String jsonString,
			boolean firstTime) throws JSONException, IOException {
		LinkedHashMap<String, Object> keys = new LinkedHashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + jsonString);
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String,Object>>() {};

		LinkedHashMap<String,?> o = mapper.readValue(jsonString, typeRef); 
		int totalSize = o.size();

		for (String key : o.keySet()){
			if(!key.equalsIgnoreCase("required")){
			//String key = (String) keyset.next();
			Object value = o.get(key);
			if (value instanceof LinkedHashMap) {
				System.out.println("Incomin value is of JSONObject : "+value);
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(value);
				System.out.println(mapToJson);
				updateWithObjectElements(incomingElementValueElementForString,	0, ((LinkedHashMap) value).size(), key);
				keys.put(
						key,
						jsonString2Map(incomingElementValueElementForString,
								mapToJson, false));
			} else if (value instanceof ArrayList) {
				System.out.println("Incomin value is of JSONArray : "+value);
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(value);
				System.out.println(mapToJson);
				updateWithArrayElements(incomingElementValueElementForString,
						0, 1, key);
				// JSONArray jsonArray = new JSONArray(value.toString());
				keys.put(
						key,
						jsonArray2List(mapToJson,
								incomingElementValueElementForString));
			} else {
				// keyNode( value);
				// listChildrenForElementValue(incomingElementValueElementForString,
				// 0, key, value.toString());
				if (firstTime) {
					// incomingElementValueElementForString = new
					// XMLElementBuilder().loadElementValueTemplateXML("elementValueForObject.xml").getRootElement();
					firstTime = false;
					incomingElementValueElementForString = listChildrenForElementValue(
							incomingElementValueElementForString, 0, key,
							value.toString(), totalSize + "");
				} else {
					incomingElementValueElementForString = listChildrenForElementValue(
							incomingElementValueElementForString, 0, key,
							value.toString(), totalSize + "");
				}
				// compositorValueSet.addContent(incomingElementValueElementForString.detach());
				// keys.put( key, value );
			}
			}
		}
		return keys;
	}



	
	public static Map<String, Object> jsonString2MapForComplexValue(
			Element incomingElementValueElementForString, String jsonString,
			boolean firstTime) throws JSONException, IOException {
		LinkedHashMap<String, Object> keys = new LinkedHashMap<String, Object>();
		JSONObject json = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + jsonString);
		TypeReference<LinkedHashMap<String, Object>> typeRef 
		= new TypeReference<LinkedHashMap<String,Object>>() {};

		LinkedHashMap<String,?> o = mapper.readValue(jsonString, typeRef); 
		List<String> sortKey = new ArrayList<String>();
		int totalSize = o.size();

		for (String key : o.keySet()){
			if(!key.equalsIgnoreCase("required")){
			//String key = (String) keyset.next();
			Object value = o.get(key);
			if (value instanceof LinkedHashMap) {
				// incomingElementValueElementForString =
				// loadElementValueTemplateXMLForObject();
				System.out.println("Incomin value is of JSONObject : "+value);
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(value);
				System.out.println(mapToJson);
				updateWithObjectElementsForComplexValue(incomingElementValueElementForString,
						0, ((LinkedHashMap) value).size(), key);
				keys.put(
						key,
						jsonString2MapForComplexValue(incomingElementValueElementForString,
								mapToJson, false));
			} else if (value instanceof ArrayList) {
				System.out.println("Incomin value is of JSONArray : "+value);
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(value);
				System.out.println(mapToJson);
				updateWithArrayElementsForComplexValue(incomingElementValueElementForString,
						0, ((ArrayList) value).size(), key);
				// JSONArray jsonArray = new JSONArray(value.toString());
				keys.put(
						key,
						jsonArray2ListForComplexValue(mapToJson,
								incomingElementValueElementForString));
			} else {
				// keyNode( value);
				// listChildrenForElementValue(incomingElementValueElementForString,
				// 0, key, value.toString());
				if (firstTime) {
					// incomingElementValueElementForString = new
					// XMLElementBuilder().loadElementValueTemplateXML("elementValueForObject.xml").getRootElement();
					firstTime = false;
					incomingElementValueElementForString = listChildrenForComplexValue(
							incomingElementValueElementForString, 0, key,
							value.toString(), totalSize + "");
				} else {
					incomingElementValueElementForString = listChildrenForComplexValue(
							incomingElementValueElementForString, 0, key,
							value.toString(), totalSize + "");
				}
				// compositorValueSet.addContent(incomingElementValueElementForString.detach());
				// keys.put( key, value );
			}
			}
		}
		return keys;
	}

	private static int getJSONObjectSize(org.json.JSONObject firstJSONObject) {
		Iterator keysToCopyIterator = firstJSONObject.keys();
		List<String> keysList = new ArrayList<String>();
		while (keysToCopyIterator.hasNext()) {
			String key = (String) keysToCopyIterator.next();
			keysList.add(key);
		}
		String[] kesyArray = keysList.toArray(new String[keysList.size()]);

		return kesyArray.length;
		// JSONObject secondJSONObject = new JSONObject(firstJSONObject, );

	}

	private static Element listChildrenForElementValue(Element current,
			int depth, String columnName, String columnValue, String objectSize)
					throws IOException {
		// printSpaces(depth);

		List<Element> children = current.getChildren();
		Iterator<Element> iteratorTemplateXML = children.iterator();
		Element innerMostElement = null;
		Element innerMostParamTypeSizeElement = null;
		ArrayList<Element> innerMostParamTypeSizeElementList = new ArrayList<Element>();
		while (iteratorTemplateXML.hasNext()) {
			Element child = (Element) iteratorTemplateXML.next();
			if (child.getName().equalsIgnoreCase("ElementValue")) {
				// innerMostElement =
				// child.getChild("ElementType").getChild("ComplexType").getChild("AllCompositor").getChild("paramTypesSize");

				IteratorIterable<Content> descendantsOfChannel = child
						.getDescendants();
				for (Content descendant : descendantsOfChannel) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element element = (Element) descendant;
						if (element.getName().equals("paramTypesSize")) {
							System.out
							.println("\n paramtypesize--------------->"
									+ element.getText()); //
							innerMostElement = (Element) element.getParent();
							innerMostParamTypeSizeElement = element;
							innerMostParamTypeSizeElementList.add(element);
							// prints all urls of all thumbnails within the
							// 'media' namespace
						}
					}
				}

			} else if (child.getChildren().size() != 0) {

				listChildrenForElementValue(child, depth + 1, columnName,
						columnValue, objectSize);
			}
		}
		Document elementForString = new XMLElementBuilder()
		.loadElementValueTemplateXML("stringTypeTemplateXML.xml");
		//innerMostParamTypeSizeElementList.stream().forEach(paramTypeSize ->{ paramTypeSize.removeContent(); paramTypeSize.addContent(objectSize);});
		innerMostParamTypeSizeElement.removeContent();
		innerMostParamTypeSizeElement.addContent(objectSize);
		elementForString.getRootElement().getChild("localName").setText(columnName);
		innerMostElement.addContent(
				elementForString.getRootElement().detach());

		return current;
	}
	private static Element listChildrenForComplexValue(Element current,
			int depth, String columnName, String columnValue, String objectSize) throws IOException {

		List<Element> children = current.getChildren();
		Iterator<Element> iteratorTemplateXML = children.iterator();
		Element innerMostElement = null;
		Element innerMostElementForValueSize = null;
		while (iteratorTemplateXML.hasNext()) {
			Element child = (Element) iteratorTemplateXML.next();
			if (child.getName().equalsIgnoreCase("ComplexValue")) {
				IteratorIterable<Content> descendantsOfChannel = child.getDescendants();
				for (Content descendant : descendantsOfChannel) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element element = (Element) descendant;
						if (element.getName().equals("valuesSize")) {
							System.out.println("\n valuesSize--------------->"+ element.getText());
							innerMostElement = (Element) element.getParent();
							innerMostElementForValueSize = element;
						}
					}
				}

			} 
		}
		Document elementForString = new XMLElementBuilder().loadElementValueTemplateXML("stringTypeValueTemplateXML.xml");
		innerMostElementForValueSize.setText(objectSize);
		elementForString.getRootElement().getChild("ComplexValue").getChild("StringValue").getChild("columnName").setText(columnName);
		innerMostElement.addContent(elementForString.getRootElement().detach());
		return current;
	}

	private static Element updateWithArrayElements(Element current, int depth,
			int valueSize, String columnName) throws IOException {
		// printSpaces(depth);
		Element innerMostChildForComplexType = null;
		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("ElementValue")) {
					//Do nothing
					IteratorIterable<Content> descendantsOfChannel1 = child.getDescendants();
					for (Content descendant1 : descendantsOfChannel1) {
						if (descendant1.getCType().equals(Content.CType.Element)) {
							Element element = (Element) descendant1;
							if (element.getName().equals("paramTypesSize") ) {
								System.out
								.println("\n paramtypesize--------------->"
										+ element.getText()); //
								innerMostChildForComplexType = (Element) element.getParent();
								// prints all urls of all thumbnails within the
								// 'media' namespace
							}
						}
					}

				}
			}
		}

		Document elementForArray = new XMLElementBuilder()
		.loadElementValueTemplateXML("arrayTypeTemplateXML.xml");
		elementForArray.getRootElement().getChild("localName").setText(columnName);
		elementForArray.getRootElement().getChild("ComplexType").getChild("SequenceCompositor").getChild("paramTypesSize").removeContent();
		elementForArray.getRootElement().getChild("ComplexType").getChild("SequenceCompositor").getChild("paramTypesSize").addContent(valueSize + "");
		elementForArray.getRootElement().getChild("localName").removeContent();
		elementForArray.getRootElement().getChild("localName").addContent(columnName);
		innerMostChildForComplexType.addContent(elementForArray.getRootElement().detach());
		/*
		List children = current.getChildren();
		Iterator iteratorTemplateXML = children.iterator();
		Element innerMostElement = null;*/
		/*	while (iteratorTemplateXML.hasNext()) {
			Element child = (Element) iteratorTemplateXML.next();

			if (child.getName().equalsIgnoreCase("ElementValue")) {

				IteratorIterable<Content> descendantsOfChannel = child
						.getDescendants();
				for (Content descendant : descendantsOfChannel) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element element = (Element) descendant;
						if (element.getName().equals("paramTypesSize")) {
							System.out.println("paramtypesize--------------->"
									+ element.getText()); //
							innerMostElement = element;
							// prints all urls of all thumbnails within the
							// 'media' namespace
						}
					}
				}

			} else if (child.getChildren().size() != 0) {
				updateWithArrayElements(child, depth + 1, valueSize, columnName);
			}

		}*/
		// lastChildForCompositorValueSet.addContent(elementForString.detachRootElement());
		/**
		 * All elementvalue for string to inner most CompositorValueSet tag END
		 * //child.addContent(increment.getAndIncrement()+"");
		 */

		return current;
	}

	private static Element updateWithArrayElementsForComplexValue(Element current, int depth,
			int valueSize, String columnName) throws IOException {
		// printSpaces(depth);
		Element innerMostChildForComplexType = null;
		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("ComplexValue")) {
					//Do nothing
					IteratorIterable<Content> descendantsOfChannel1 = child.getDescendants();
					for (Content descendant1 : descendantsOfChannel1) {
						if (descendant1.getCType().equals(Content.CType.Element)) {
							Element element = (Element) descendant1;
							if (element.getName().equals("valuesSize") ) {
								System.out
								.println("\n valuesSize--------------->"
										+ element.getText()); //
								innerMostChildForComplexType = (Element) element.getParent();
								// prints all urls of all thumbnails within the
								// 'media' namespace
							}
						}
					}

				}
			}
		}
		Document elementForArray = new XMLElementBuilder()
		.loadElementValueTemplateXML("arrayTypeValueTemplateXML.xml");

		innerMostChildForComplexType.addContent(
				elementForArray.getRootElement().detach());

		return current;
	}

	private static Element updateWithObjectElementsForComplexValue(Element current, int depth,
			int valueSize, String columnName) throws IOException {

		Element innerMostChildForComplexType = null;		

		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("ComplexValue")) {
					//Do nothing
					IteratorIterable<Content> descendantsOfChannel1 = child.getDescendants();
					for (Content descendant1 : descendantsOfChannel1) {
						if (descendant1.getCType().equals(Content.CType.Element)) {
							Element element = (Element) descendant1;
							if (element.getName().equals("valuesSize") ) {
								System.out
								.println("\n valuesSize--------------->"
										+ element.getText()); //
								innerMostChildForComplexType = (Element) element.getParent();
								// prints all urls of all thumbnails within the
								// 'media' namespace
							}
						}
					}

				}
			}
		}

		// lastChildForCompositorValueSet.addContent(elementForString.detachRootElement());
		Document elementForObject = new XMLElementBuilder()
		.loadElementValueTemplateXML("objectTypeValueTemplateXML.xml");
		//elementForObject.getRootElement().getChild("localName").setText(columnName);
		//System.out.println(innerMostChildForComplexType);
		if(innerMostChildForComplexType.getChild("valuesSize").getText().equals("template")){
			innerMostChildForComplexType.getChild("valuesSize").removeContent();
			innerMostChildForComplexType.getChild("valuesSize").addContent("1");
		}else{
			innerMostChildForComplexType.getChild("valuesSize").removeContent();
			innerMostChildForComplexType.getChild("valuesSize").addContent(valueSize + "");
		}

		innerMostChildForComplexType.addContent(
				elementForObject.detachRootElement());
		return current;
	}
	
	
	private static Element updateWithObjectElements(Element current, int depth, int valueSize,
			String columnName) throws IOException {
		// printSpaces(depth);

		List children = current.getChildren();
		Iterator iteratorTemplateXML = children.iterator();
		Element innerMostChildForComplexType = null;		

		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("ElementValue")) {
					//Do nothing
					IteratorIterable<Content> descendantsOfChannel1 = child.getDescendants();
					for (Content descendant1 : descendantsOfChannel1) {
						if (descendant1.getCType().equals(Content.CType.Element)) {
							Element element = (Element) descendant1;
							if (element.getName().equals("paramTypesSize") ) {
								System.out
								.println("\n paramtypesize--------------->"	+ element.getText());
								innerMostChildForComplexType = (Element) element.getParent();
							}
						}
					}

				}
			}
		}

		// lastChildForCompositorValueSet.addContent(elementForString.detachRootElement());
		Document elementForObject = new XMLElementBuilder()
		.loadElementValueTemplateXML("objectTypeTemplateXML.xml");
		//elementForObject.getRootElement().getChild("localName").setText(columnName);
		//elementForObject.getRootElement().getChild("ComplexType").getChild("AllCompositor").getChild("paramTypesSize").removeContent();
		//elementForObject.getRootElement().getChild("ComplexType").getChild("AllCompositor").getChild("paramTypesSize").addContent(valueSize + "");
		elementForObject.getRootElement().getChild("localName").removeContent();
		elementForObject.getRootElement().getChild("localName").addContent(columnName);
		//elementForObject.getRootElement().getChild("localName").setText(columnName);
		System.out.println(innerMostChildForComplexType);
		if(innerMostChildForComplexType.getChild("paramTypesSize").getText().equals("template")){
			innerMostChildForComplexType.getChild("paramTypesSize").removeContent();
			innerMostChildForComplexType.getChild("paramTypesSize").addContent("1");
		}else{
			innerMostChildForComplexType.getChild("paramTypesSize").removeContent();
			innerMostChildForComplexType.getChild("paramTypesSize").addContent(valueSize + "");
		}
		innerMostChildForComplexType.addContent(
				elementForObject.detachRootElement());
		return current;
	}

	public static List<Object> jsonArray2List(String arrayOFKeys,
			Element incomingElementValueElementForString) throws JSONException,
			IOException {
		System.out.println("Incoming value is of JSONArray : =========");
		// Element elementValueElement = new
		// XMLElementBuilder().loadElementValueTemplateXML();
		ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		//jsonString = jsonString.replace("'", "\\\\u0027");
		System.out.print("\n Incoming json string for reference : " + arrayOFKeys);
		TypeReference<ArrayList<?>> typeRef 
		= new TypeReference<ArrayList<?>>() {};        
		ArrayList<?> arrayOFKeysList = mapper.readValue(arrayOFKeys, typeRef); 


		List<Object> array2List = new ArrayList<Object>();
		int arraySize;
		if(arrayOFKeysList.size() ==1){
			arraySize = arrayOFKeysList.size();
		}else{
			arraySize = arrayOFKeysList.size()-1;
		}

		/** always send one item from Array 
		 * Not all items, therefore sending arraySize as 1
		 */
		arraySize = 1;
		for (int i = 0; i < arraySize; i++) {
			if (arrayOFKeysList.get(i) instanceof LinkedHashMap) {

				System.out.println("Incomin value is of JSONObject : "+arrayOFKeysList
						.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList
						.get(i));
				System.out.println(mapToJson);
				Map<String, Object> subObj2Map = jsonString2Map(
						incomingElementValueElementForString, mapToJson, false);
				array2List.add(subObj2Map);
			} else if (arrayOFKeysList.get(i) instanceof ArrayList) {

				System.out.println("Incomin value is of JSONObject : "+arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList
						.get(i));
				System.out.println(mapToJson);

				List<Object> subarray2List = jsonArray2List(mapToJson,
						incomingElementValueElementForString);
				array2List.add(subarray2List);
			} else {
				// keyNode( arrayOFKeys.opt(i) );
				array2List.add(arrayOFKeysList.get(i));
			}
		}
		return array2List;
	}


	public static List<Object> jsonArray2ListForComplexValue(String arrayOFKeys,
			Element incomingElementValueElementForString) throws JSONException,
			IOException {
		System.out.println("Incoming value is of JSONArray : =========");
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + arrayOFKeys);
		TypeReference<ArrayList<?>> typeRef = new TypeReference<ArrayList<?>>() {};        
		ArrayList<?> arrayOFKeysList = mapper.readValue(arrayOFKeys, typeRef); 	     

		int arraySize;
		if(arrayOFKeysList.size() ==1){
			arraySize = arrayOFKeysList.size();
		}else{
			arraySize = arrayOFKeysList.size()-1;
		}

		List<Object> array2List = new ArrayList<Object>();
		for (int i = 0; i < arraySize; i++) {
			if (arrayOFKeysList.get(i) instanceof LinkedHashMap) {				
				System.out.println("Incomin value is of JSONObject : "+arrayOFKeysList
						.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList
						.get(i));
				System.out.println(mapToJson);
				Map<String, Object> subObj2Map = jsonString2MapForComplexValue(
						incomingElementValueElementForString, mapToJson, false);
				array2List.add(subObj2Map);
			} else if (arrayOFKeysList.get(i) instanceof ArrayList) {

				System.out.println("Incomin value is of JSONObject : "+arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				//Set pretty printing of json
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				System.out.println(mapToJson);

				List<Object> subarray2List = jsonArray2ListForComplexValue(mapToJson,
						incomingElementValueElementForString);
				array2List.add(subarray2List);
			} else {
				// keyNode( arrayOFKeys.opt(i) );
				array2List.add(arrayOFKeysList.get(i));
			}
		}
		return array2List;
	}


	@SuppressWarnings("unused")
	public static Element updateRestToolTest(Element current, AtomicInteger increment, ConfigurationTO configurationTO) throws ParserConfigurationException, SAXException, IOException{

		Element testID = null, testID1 = null, name1 = null, httpMethodTestValue = null, httpClientEndPoint = null;
		Element docDelivery = null, ftpDeliveries = null, restClient = null, dataSourceName = null, nameValuePair = null;
		Element restClientToolTest = null, messagingSchema = null, pathElementss = null, UrlPathParamsMultiValue=null;
		Element urlPathParametersLiteralElement = null;

		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("name")
						&& child.getText().equalsIgnoreCase(
								"ftpdeliveries-positive")) {
					ftpDeliveries = child;
					//child.removeContent();
					//child.addContent(configurationTO.getEndPointUrl());

				} else if (child.getName().equalsIgnoreCase("testID")
						&& child.getText().equals("74")) {
					testID1 = child;
				} else if (child.getName().equalsIgnoreCase("name")
						&& child.getText().equals("REST Client")) {
					restClient = child;
				} else if (child.getName().equalsIgnoreCase(
						"HTTPClient_Endpoint")
						&& child.getTextTrim()
						.contains("http://ocp.api.sys.td.com/com-td-ocp-delivery-api/ftpdeliveries-template")) {
					httpClientEndPoint = child;

				} else if (child.getName().equalsIgnoreCase(
						"HTTPMethodTestValue")) {
					httpMethodTestValue = child;

				} else if (child.getName().equalsIgnoreCase("NameValuePair")
						&& child.getChild("name").getTextTrim()
						.equals("Authorization")) {
					nameValuePair = child;
					// Element restClientElement = (Element)
					// child.getParent().getParent();

				} else if (child.getName().equalsIgnoreCase(
						"RESTClientToolTest")) {
					restClientToolTest = child;
				}
				else if(child.getName().equalsIgnoreCase("UrlPathParametersLiteral") && child.getText().equalsIgnoreCase("template")){ 
					urlPathParametersLiteralElement = child;
				}

			}
		}

		if (testID != null) {
			testID.removeContent();
			testID.addContent(increment.getAndIncrement() + "");
		}
		if (docDelivery != null) {
			docDelivery.removeContent();
			docDelivery.addContent(configurationTO.getRamlFileName());
		}
		if(ftpDeliveries != null){
			ftpDeliveries.removeContent();
			ftpDeliveries.addContent(configurationTO.getEndPointUrl());
		}
		if(testID1 != null){
			testID1.removeContent();
			testID1.addContent(increment.getAndIncrement() + "");
		}

		restClient.removeContent();
		restClient.addContent("Kalpana testing REST Client");

		//dataSourceName.removeContent();
		//dataSourceName.addContent(configurationTO.getDataSource());
		if(httpMethodTestValue != null){
			httpMethodTestValue.getChild("method").removeContent();
			httpMethodTestValue.getChild("method").addContent(
					configurationTO.getMethod());
		}

		nameValuePair.getChild("MultiValue").getChild("StringTestValue")
		.getChild("value").removeContent();
		nameValuePair.getChild("MultiValue").getChild("StringTestValue")
		.getChild("value")
		.addContent("${TOKEN}");

		/*if(httpClientEndPoint !=null){
		httpClientEndPoint.removeContent();
		httpClientEndPoint.addContent(configurationTO.getEndPointUrl());

	}*/

		if(httpClientEndPoint != null){
			httpClientEndPoint.removeContent();
			String paramStr = null;
			if(configurationTO.getQueryParameters() != null){
				for(int i=0; i<  configurationTO.getQueryParameters().size(); i++){
					Parameter parameter = configurationTO.getQueryParameters().get(i);
					if(i == (configurationTO.getQueryParameters().size()-1)){
						paramStr = paramStr+parameter.toString()+"=${"+parameter.toString()+"}";
					}else{
						paramStr = paramStr+parameter.toString()+"=${"+parameter.toString()+"}&amp;";
					}
				}
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl()+"?"+paramStr);
			}else{
				httpClientEndPoint.addContent(configurationTO.getEndPointUrl());
			}
		}
		return current;	

	}

	public static void updateUrlPathParametersLiteral(Element urlPathParametersLiteralElement, ConfigurationTO configurationTO) throws IOException{
		Element UrlPathParamsMultiValue = null;
		if(urlPathParametersLiteralElement != null){
			urlPathParametersLiteralElement.removeContent();
			String[] urlPaths = Util.tokenizePathURLEndpoint(configurationTO.getEndPointUrl());
			urlPathParametersLiteralElement.addContent(new Element("pathElementss").setAttribute("size", (urlPaths.length-1)+""));			

			for (int i = 1; i < urlPaths.length; i++) {

				System.out.println("paths::"+i+" "+urlPaths[i]+"\n");
				UrlPathParamsMultiValue = new XMLElementBuilder().loadElementValueTemplateXML("urlPathParamsMultiValue.xml").detachRootElement();
				//UrlPathParamsMultiValue = document.getRootElement();

				UrlPathParamsMultiValue.getChild("StringTestValue").getChild("value").removeContent();
				UrlPathParamsMultiValue.getChild("StringTestValue").getChild("value").addContent(urlPaths[i]);

				urlPathParametersLiteralElement.getChild("pathElementss").addContent(UrlPathParamsMultiValue);
			}
		}
	}

	public static Element buildStringComparisonAssertions(JSONArray jsonArray, String actualKey, Element andAssertion) throws IOException {		
		Element name =null, assertionsSize=null;
		Element stringName=null, assertionXPath=null,column=null;		

		
		 for ( int i = 0; i < jsonArray.length(); i++ )  {
			 Element stringAssertion = new XMLElementBuilder().loadElementValueTemplateXML("stringAssertionTemplate.xml").detachRootElement();
			 IteratorIterable<Content> descendantsOfChannel1 = stringAssertion.getDescendants();
			 for (Content descendant : descendantsOfChannel1) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element child = (Element) descendant;
						if (child.getName().equalsIgnoreCase("name")) {
							stringName = child;
						} else if (child.getName().equalsIgnoreCase("Assertion_XPath")
								) {
							assertionXPath = child;

						} else if (child.getName().equalsIgnoreCase("column")
								) {
							column = child;

						}
					}
				}
			 stringName.removeContent();
			 stringName.addContent(jsonArray.get(i)+"");
			 assertionXPath.removeContent();
			 assertionXPath.addContent(actualKey+"[0];"+jsonArray.get(i));
			 column.removeContent();
			 column.addContent(jsonArray.get(i)+"");
			 andAssertion.addContent(stringAssertion);
			 //System.out.println();			
			 
         }	
		 return andAssertion;
	}
	
public static Element buildResponseConditionalAssertion(String responseCode, Element jsonAssertionTool) throws IOException{
	Element conditionAssertion = new XMLElementBuilder().loadElementValueTemplateXML("conditionalAssertionTemplate.xml").detachRootElement();
	IteratorIterable<Content> descendantsOfConditional = conditionAssertion.getDescendants();
	Element name =null, stringComparision = null, andAssertion =null, nameAndAssertion =null, andAssertionSize=null;
	for (Content descendant : descendantsOfConditional) {
		if (descendant.getCType().equals(Content.CType.Element)) {
			Element child = (Element) descendant;
			if (child.getName().equalsIgnoreCase("value")&&child.getText().equalsIgnoreCase("404")) {				
				name = child;
			}else if(child.getName().equalsIgnoreCase("AndAssertion")){
				andAssertion = child;
			}else if(child.getName().equalsIgnoreCase("name") && child.getText().equalsIgnoreCase("FirstResponseAND")){
				nameAndAssertion = child;
			}else if(child.getName().equalsIgnoreCase("assertionsSize")){
				andAssertionSize = child;
			}
		}
	}
	
	name.removeContent();
	name.addContent(responseCode);
	
/*	andAssertionSize.removeContent();
	andAssertionSize.addContent();*/
	
	/*nameAndAssertion.removeContent();
	nameAndAssertion.addContent();*/
	jsonAssertionTool.addContent(4, conditionAssertion);
	
	return andAssertion;
}

}
