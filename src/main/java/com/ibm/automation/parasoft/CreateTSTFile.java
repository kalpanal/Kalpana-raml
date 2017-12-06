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

		boolean foundUPC = false;
		try {
			String FILE_NAME = "C:/Kalpana/TD Bank/datasheets/DocDelivery.xlsx";
			FileInputStream spreadsheet = new FileInputStream(new File(
					FILE_NAME));
			// Create workbook instance to hold file reference to .xlsxfile
			XSSFWorkbook workbook = new XSSFWorkbook(spreadsheet);

			// Get first/desire sheed from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each row one by one
			Iterator<Row> rowIterator = sheet.iterator();

			List<String> headers = new ArrayList<>();
			Map<String, Integer> headersMap = new HashMap<>();
			if (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				for (Cell cell : row) {
					headers.add(cell.getStringCellValue());
					headersMap.put(cell.getStringCellValue(),
							cell.getColumnIndex());
				}
			}

			List<DataRow> contents = new ArrayList<>();
			HashMap<String, List<Object>> assertionsMap = new HashMap<String, List<Object>>();
			String[] assertionColumns = { "dataSourceName",
					"Assertor String Name", "Assertion_Xpath", "timestamp",
					"Parameterized columnName" };
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				String assertionDetailsInput = row.getCell(
						headersMap.get("Input needed from user"))
						.getStringCellValue();
				String assertionType = row.getCell(
						headersMap.get("Assertion Type")).getStringCellValue();
				String assertionLines[] = assertionDetailsInput
						.split("\\r?\\n");

				switch (assertionType) {
				case "String Comparison Assertion": {
					// assertionsMap.get("String Comparison Assertion");
					StringComparisonAsssertionDetails stringComparisonAsssertionDetails = new StringComparisonAsssertionDetails();
					int i;
					for (String s : assertionLines) {
						int assertionTypePos = contains(assertionColumns, s);
						switch (assertionTypePos) {
						case 0:
							// it is datasource
							stringComparisonAsssertionDetails
									.setDataSourceName(s.substring(s
											.indexOf("=")));
							break;
						case 1:
							// It is Assertor String Name
							stringComparisonAsssertionDetails
									.setAssertorStringName(s.substring(s
											.indexOf("=")));
							break;
						case 2:
							// It is Assertion_Xpath
							stringComparisonAsssertionDetails
									.setAssertion_Xpath(s.substring(s
											.indexOf("=")));
							break;
						case 3:
							// It is timestamp
							stringComparisonAsssertionDetails.setTimestamp(s
									.substring(s.indexOf("=")));
							break;
						case 4:
							// It is Parameterized column Name
							stringComparisonAsssertionDetails
									.setParameterizedColumnName(s.substring(s
											.indexOf("=")));
							break;

						}

					}
					addAssertionsToMap("String Comparison Assertion",
							assertionsMap, stringComparisonAsssertionDetails);
					break;
				}
				}
			}

			//writeFileUsingJDOM(assertionsMap);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	public static void writeFileUsingJDOM(ArrayList<ConfigurationTO> configurationTOEndPointList)
			throws Exception {

		Document document = null;
		Element testSuite = null;
		
			try {
				
				if(Util.loadProperties("UPDATE_FLAG", configurationTOEndPointList.get(0).getAppConfigPath()).equals("N")){
					
					String inputTstFile = Util.loadProperties("INPUT_TST_FILE", configurationTOEndPointList.get(0).getAppConfigPath());
					document = new XMLElementBuilder().loadIncomingTSTFile(inputTstFile);
					testSuite = document.getRootElement();
				}else{
					document = new XMLElementBuilder().loadElementValueTemplateXML_DOM("inputTSTFileTemplate.xml");
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
			int depth, ArrayList<ConfigurationTO> configurationTOEndPointList) throws IOException {

		//printSpaces(depth);
		//System.out.println(current.getName());
		List children = current.getChildren();
		Element testSuiteMain = null;
		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				//System.out.println("element" + child.getName() + child);
				/*
				 * if (element.getName().equals("MessagingSchemaElement")) {
				 * System
				 * .out.println("\n paramtypesize--------------->"+element.
				 * getText()); // // innerMostElement = element; // prints all
				 * urls of all thumbnails within the // 'media' namespace }
				 */
				// while (iteratorTemplateXML.hasNext()) {
				// Element child = (Element) iteratorTemplateXML.next();
				if (child.getName().equalsIgnoreCase("testsSize") && child.getTextTrim().contains("4")) {	
					testSuiteMain = child;
					
				}
			}
		}
		Element testSuiteXML = new XMLElementBuilder().loadTestSuiteTemplateXML();
		testSuiteMain.getParent().addContent(testSuiteXML.detach());
	/*	while (iteratorTemplateXML.hasNext()) {
			Element child = (Element) iteratorTemplateXML.next();
			if (child.getName().equalsIgnoreCase("testsSize") && child.getTextTrim().contains("4")) {	
					Element testSuiteXML = new XMLElementBuilder().loadTestSuiteTemplateXML();
					child.getParent().addContent(testSuiteXML.detach());
					//break;

			} else {
				listChildren(child,  depth + 1, configurationTOEndPointList);
				
			}

		}*/
		return current;
	}

	private static void printSpaces(int n) {

		for (int i = 0; i < n; i++) {
			System.out.print(' ');
		}

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
						listChildren(testSuiteMain, 0, configurationTOEndPointList);
						XMLElementBuilder.updateTemplateXMLForTestSuite(testSuiteMain, incrementerForTestID, configurationTO, true);
					} else{
						testSuiteXML = new XMLElementBuilder().loadTestSuiteTemplateXML();					
						listChildren(testSuiteMain, 0, configurationTOEndPointList);
						XMLElementBuilder.updateTemplateXMLForTestSuite(testSuiteMain, incrementerForTestID, configurationTO, false);
						
					}
					
					
					/*XMLOutputter outputter = new XMLOutputter();
					outputter.setFormat(Format.getPrettyFormat());*/
					//outputter.output((Document) document, writer);
					//outputter.output((Document) testSuiteMain.getDocument(), System.out);
					//testSuiteMain.addContent(updatedTestSuiteXML.detach());	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		
			});
		
		try {
			ReadExcelDataSheet.buildDataSources(testSuiteMain, configurationTOEndPointList.get(0).getAppConfigPath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
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