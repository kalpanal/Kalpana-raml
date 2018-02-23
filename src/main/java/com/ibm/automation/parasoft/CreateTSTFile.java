package com.ibm.automation.parasoft;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.ibm.automation.excelOps.ReadExcelDataSheet;
import com.ibm.automation.parasoft.domain.ConfigurationTO;
import com.ibm.automation.parasoft.util.Util;

/**
 * This CreateTSTFile program implements logic to create a tst file in XML
 * format. The input for this program is app,config file
 * 
 * @author kalpana
 * @version 1.0
 * @since 2017-10-31
 *
 */
public class CreateTSTFile {

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

	/**
	 * @param configurationTOEndPointList
	 * @param testSuiteMain
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({})
	private static Element buildTestPathXML(
			ArrayList<ConfigurationTO> configurationTOEndPointList,
			Element testSuiteMain) throws IOException {
		AtomicInteger incrementerForTestID = new AtomicInteger(0);
		AtomicInteger incrementerFirstTime = new AtomicInteger(0);
		configurationTOEndPointList.stream().forEach(
				configurationTO -> {
					incrementerForTestID.getAndIncrement();
					try {
						incrementerFirstTime.getAndIncrement();
						if (incrementerFirstTime.get() == 1) {
							Element testSuiteMain1 = listChildren(
									testSuiteMain, 0,
									configurationTOEndPointList);
							XMLElementBuilder.updateTemplateXMLForTestSuite(
									testSuiteMain1, incrementerForTestID,
									configurationTO, true);
						} else {
							Element testSuiteMain1 = listChildren(
									testSuiteMain, 0,
									configurationTOEndPointList);
							XMLElementBuilder.updateTemplateXMLForTestSuite(
									testSuiteMain1, incrementerForTestID,
									configurationTO, false);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				});

		try {
			ReadExcelDataSheet.buildDataSources(testSuiteMain,
					configurationTOEndPointList.get(0).getAppConfigPath(),
					configurationTOEndPointList.size());
		} catch (Exception e) {
			System.out.println("Error building Data source XML nodes"
					+ e.getMessage());
			e.printStackTrace();
		}

		return testSuiteMain;

	}

	public static int contains(String[] arr, String item) {
		for (int i = 0; i < arr.length; i++) {
			if (item.contains(arr[i])) {
				return i;
			}
		}
		return 0;
	}

	public static void createTstFile() {
	}

	/**
	 * This method iterates through all child elements of a given element
	 * 
	 * @param current
	 *            This is the input Element
	 * @param depth
	 *            This param is used to defined the depth until which it needs
	 *            to iterate through
	 * @param configurationTOEndPointList
	 *            This is an array of all resource's information from RAML file
	 * @return This returns an Element
	 * @throws Exception
	 */
	public static Element listChildren(Element current, int depth,
			ArrayList<ConfigurationTO> configurationTOEndPointList)
			throws Exception {
		Element testSuiteMain = null, tokenPathURL = null, nameValuePropertiesForToken = null, nameValuePropertiesSizeForToken = null;
		IteratorIterable<Content> descendantsOfChannel = current
				.getDescendants();
		for (Content descendant : descendantsOfChannel) {
			if (descendant.getCType().equals(Content.CType.Element)) {
				Element child = (Element) descendant;
				if (child.getName().equalsIgnoreCase("testsSize")
						&& child.getTextTrim().contains("4")) {
					testSuiteMain = child;

				} else if (child.getName().equalsIgnoreCase(
						"HTTPClient_Endpoint")) {
					if (child
							.getText()
							.contains(
									"https://pingfederate.sys.td.com:9031/as/token.oauth2?client_id")) {
						tokenPathURL = child;
					}
				} else if (child.getName().equalsIgnoreCase("propertiesSize")
						&& child.getText().contains("1-tokenURL")) {
					nameValuePropertiesForToken = child.getParentElement();
					nameValuePropertiesSizeForToken = child;
				}
			}
		}

		String tokenURLFromPropertiesFile = Util.loadProperties("TOKEN_URL",
				configurationTOEndPointList.get(0).getAppConfigPath());
		tokenURLFromPropertiesFile = tokenURLFromPropertiesFile.replaceAll(
				"&amp;", "&");

		Map<String, Object> queryParamsMap = Util
				.convertQueryStringToMap(tokenURLFromPropertiesFile);
		if (nameValuePropertiesSizeForToken != null) {
			nameValuePropertiesSizeForToken.removeContent();
			nameValuePropertiesSizeForToken.addContent(queryParamsMap.size()
					+ "");
			XMLElementBuilder.buildNameValuePropertiesForTokenURL(
					nameValuePropertiesForToken, queryParamsMap);
		}
		if (tokenPathURL != null) {
			tokenPathURL.removeContent();
			tokenPathURL.addContent(Util.loadProperties("TOKEN_URL",
					configurationTOEndPointList.get(0).getAppConfigPath()));
		}

		Element testSuiteXML = new XMLElementBuilder()
				.loadTestSuiteTemplateXML();
		testSuiteMain.getParent().addContent(testSuiteXML.detach());

		return testSuiteMain.getParentElement();
	}

	/**
	 * This method is used to check if the update_flag is Y or N If update_flag
	 * is Y, it takes INPUT_tst_file to make updates to tst file If update_flag
	 * is N, it generates new tst file
	 * 
	 * @param configurationTOEndPointList
	 *            This is an array of all resources in RAML file
	 * @throws Exception
	 * @see Exception
	 */
	public static void writeFileUsingJDOM(
			ArrayList<ConfigurationTO> configurationTOEndPointList)
			throws Exception {

		Document document = null, document1 = null;
		Element testSuite = null, testSuite1 = null;

		try {

			if (Util.loadProperties("UPDATE_FLAG",
					configurationTOEndPointList.get(0).getAppConfigPath())
					.equals("Y")) {

				String inputTstFile = Util.loadProperties("INPUT_TST_FILE",
						configurationTOEndPointList.get(0).getAppConfigPath());
				document = new XMLElementBuilder()
						.loadIncomingTSTFile(inputTstFile);
				testSuite = document.getRootElement();
				IteratorIterable<Content> descendantsOfMainTestSuite = testSuite
						.getDescendants();
				Element firstTestsSize = null;

				for (Content descendant : descendantsOfMainTestSuite) {
					if (descendant.getCType().equals(Content.CType.Element)) {
						Element child = (Element) descendant;
						if (child.getName().equalsIgnoreCase("testsSize")) {
							firstTestsSize = child;
							break;
						}
					}
				}

				int firstTestsSizeInt = Integer.parseInt(firstTestsSize
						.getContent().get(0).getValue().toString());
				firstTestsSize.removeContent();
				firstTestsSize.addContent(firstTestsSizeInt + 1 + "");

				document1 = new XMLElementBuilder()
						.loadElementValueTemplateXML("inputTSTFileTemplateForUpdate.xml");
				testSuite1 = document1.getRootElement();
				buildTestPathXML(configurationTOEndPointList, testSuite1);
				firstTestsSize.getParent().addContent(testSuite1.detach());

			} else {
				document = new XMLElementBuilder()
						.loadElementValueTemplateXML("inputTSTFileTemplate.xml");
				testSuite = document.getRootElement();
				buildTestPathXML(configurationTOEndPointList, testSuite);
			}

		} catch (IOException e) {
			System.out.println(e);
		}

		try {
			String outputDir = Util.loadProperties("OUTPUT_DIRECTORY",
					configurationTOEndPointList.get(0).getAppConfigPath());
			FileWriter writer = new FileWriter(outputDir
					+ "/UpdatedtstFile.xml");
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(document, writer);
			writer.close(); // close writer
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}