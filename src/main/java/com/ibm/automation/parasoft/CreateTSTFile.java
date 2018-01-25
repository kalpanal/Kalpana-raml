package com.ibm.automation.parasoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.ibm.automation.excelOps.ReadExcelDataSheet;
import com.ibm.automation.parasoft.domain.ConfigurationTO;
import com.ibm.automation.parasoft.util.Util;

public class CreateTSTFile {


	public static void createTstFile() {
	}

	public static void writeFileUsingJDOM(ArrayList<ConfigurationTO> configurationTOEndPointList)
			throws Exception {

		Document document = null, document1 = null;
		Element testSuite = null, testSuite1=null;

		try {

			if(Util.loadProperties("UPDATE_FLAG", configurationTOEndPointList.get(0).getAppConfigPath()).equals("Y")){

				String inputTstFile = Util.loadProperties("INPUT_TST_FILE", configurationTOEndPointList.get(0).getAppConfigPath());
				document = new XMLElementBuilder().loadIncomingTSTFile(inputTstFile);
				testSuite = document.getRootElement();

				document1 = new XMLElementBuilder().loadElementValueTemplateXML_DOM("inputTSTFileTemplateForUpdate.xml");
				testSuite1 = document1.getRootElement();
				testSuite.addContent(testSuite1);

			}else{
				document = new XMLElementBuilder().loadElementValueTemplateXML("inputTSTFileTemplate.xml");
				testSuite = document.getRootElement();
			}

			buildTestPathXML(configurationTOEndPointList, testSuite);	
		}
		catch (IOException e) {
			System.out.println(e);
		}

		try {
			String outputDir = Util.loadProperties("OUTPUT_DIRECTORY", configurationTOEndPointList.get(0).getAppConfigPath());
			FileWriter writer = new FileWriter(outputDir+"/UpdatedtstFile.xml");
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output((Document) document, writer);
			writer.close(); // close writer
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Element listChildren(Element current,
			int depth, ArrayList<ConfigurationTO> configurationTOEndPointList) throws Exception {
		Element testSuiteMain = null, tokenPathURL = null;
		IteratorIterable<Content> descendantsOfChannel = current.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("testsSize") && child.getTextTrim().contains("4")) {	
					testSuiteMain = child;					

				}else if(child.getName().equalsIgnoreCase("HTTPClient_Endpoint") ){
					if(child.getText().contains("https://pingfederate.sys.td.com:9031/as/token.oauth2?client_id")){
						tokenPathURL = child;
					}
				}
			}
		}
		if(tokenPathURL!= null){
			tokenPathURL.removeContent();
			tokenPathURL.addContent(Util.loadProperties("TOKEN_URL", configurationTOEndPointList.get(0).getAppConfigPath()));
		}
		Element testSuiteXML = new XMLElementBuilder().loadTestSuiteTemplateXML();
		testSuiteMain.getParent().addContent(testSuiteXML.detach());

		return testSuiteMain.getParentElement();
	}

	@SuppressWarnings({ })
	private static Element buildTestPathXML(
			ArrayList<ConfigurationTO> configurationTOEndPointList, Element testSuiteMain) throws IOException {
		AtomicReference<Element> updateTestSuiteXML  = new AtomicReference<Element>();
		AtomicInteger incrementerForTestID = new AtomicInteger(0);
		AtomicInteger incrementerFirstTime = new AtomicInteger(0);
		configurationTOEndPointList.stream().forEach(configurationTO ->{
			incrementerForTestID.getAndIncrement();
			Element testSuiteXML;
			try {
				incrementerFirstTime.getAndIncrement();
				if(incrementerFirstTime.get() == 1){
					testSuiteXML = new XMLElementBuilder().loadTestSuiteTemplateXML();					
					Element testSuiteMain1 = listChildren(testSuiteMain, 0, configurationTOEndPointList);
					XMLElementBuilder.updateTemplateXMLForTestSuite(testSuiteMain1, incrementerForTestID, configurationTO, true);
				} else{
					testSuiteXML = new XMLElementBuilder().loadTestSuiteTemplateXML();					
					Element testSuiteMain1 = listChildren(testSuiteMain, 0, configurationTOEndPointList);
					XMLElementBuilder.updateTemplateXMLForTestSuite(testSuiteMain1, incrementerForTestID, configurationTO, false);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}				

		});

		try {
			ReadExcelDataSheet.buildDataSources(testSuiteMain, configurationTOEndPointList.get(0).getAppConfigPath(), configurationTOEndPointList.size());
		} catch (Exception e) {
			System.out.println("Error building Data source XML nodes"+e.getMessage());
			e.printStackTrace();
		}

		return testSuiteMain;

	}

	public static void addAssertionsToMap(String key,
			HashMap<String, List<Object>> assertionListMap,
			StringComparisonAsssertionDetails stringComparisonDetails) {
		if (!assertionListMap.containsKey(key)) {
			ArrayList<Object> stringComparisonList = new ArrayList<Object>();
			stringComparisonList.add(stringComparisonDetails);
			assertionListMap.put(key, stringComparisonList);
		} else {

			List<Object> stringComparisonList = assertionListMap.get(key);
			stringComparisonList.add(stringComparisonDetails);
			assertionListMap.put(key, stringComparisonList);

		}

	}

	public static int contains(String[] arr, String item) {
		for (int i = 0; i < arr.length; i++) {
			if (item.contains(arr[i])) {
				return i;
			}
		}
		return 0;
	}

}