package com.ibm.automation.parasoft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.automation.parasoft.domain.AppConfigurationPropertiesForDataSheet;
import com.ibm.automation.parasoft.domain.ConfigurationTO;
import com.ibm.automation.parasoft.util.JsonGeneratorFromSchema;
import com.ibm.automation.parasoft.util.Util;

/**
 * This class program is the main program which should be called with app.config
 * file path as argument
 * 
 * @author kalpana
 * @version 1.0
 * @since 2017-10-31
 *
 */
public class ParasoftTstGeneratorMain {

	static String appConfigPath;
	static String inputTstFile;

	public static ConfigurationTO copyRAMLDataToDTO(Resource urlEndPointsSubNode, int index) throws Exception {
		// System.out.println("EndPoints are ==>"+urlEndPointsNode.displayName()+urlEndPointsSubNode.displayName());
		System.out.println("Method type is GET/PUT/POST/DELETE ==>" + urlEndPointsSubNode.methods().get(index).method());

		System.out.println("securedBy ==>" + urlEndPointsSubNode.methods().get(index).securedBy().get(0).name());
		// System.out.println("responses schema content ==>" +
		// urlEndPointsSubNode.methods().get(index).responses().get(0).body().get(0));

		ConfigurationTO configurationTO = new ConfigurationTO();

		new AtomicInteger();
		HashMap<String, String> responseSchemaMap = new HashMap<String, String>();

		List<Response> res1 = urlEndPointsSubNode.methods().get(index).responses();
		for (int k = 0; k < res1.size(); k++) {
			// response.get(i);
			Response response = res1.get(k);
			if (response.body() != null && !response.body().isEmpty()) {
				if (response.body().get(0).schemaContent() != null) {
					// System.out.println(response.body().get(0).schemaContent());
					try {
						// System.out.println(response.description().value());
						System.out.println("endpoint URL is  ==>" + urlEndPointsSubNode.resourcePath());
						responseSchemaMap.put(response.code().value(), JsonGeneratorFromSchema.generateInputSampleString(response.body().get(0).schemaContent().toString(), Util.loadProperties("RAML_LOCATION", appConfigPath) + "\\jsd\\" + response.body().get(0).schema().value().toString() + ".1.schema.json").toString());
						// configurationTO.setResponseSchemaString(JsonGeneratorFromSchema.generateInputSampleString(response.body().get(0).schemaContent()).toString());
					} catch (Exception e) {
						System.out.println("Error while reading response body schema content" + e.getMessage());
						e.printStackTrace();
					}
				}
			} else {
				// System.out.println(response.description().value());
				responseSchemaMap.put(response.code().value(), "");
			}

		}
		configurationTO.setResponseSchemaMap(responseSchemaMap);
		String inputMethodType = urlEndPointsSubNode.methods().get(index).method();
		configurationTO.setMethod(inputMethodType.toUpperCase());
		configurationTO.setDataSourcePath(Util.loadProperties("DATA_SOURCE_PATH", appConfigPath));
		configurationTO.setEndPointUrl(Util.loadProperties("BASE_URL", appConfigPath) + urlEndPointsSubNode.resourcePath());
		configurationTO.setTestPathUrl(Util.loadProperties("BASE_URL", appConfigPath) + urlEndPointsSubNode.resourcePath());
		configurationTO.setSecuredBy(urlEndPointsSubNode.methods().get(index).securedBy().get(0).name());
		configurationTO.setValidRange(urlEndPointsSubNode.methods().get(index).responses().get(0).code().toString());
		new ObjectMapper();

		try {

			switch (inputMethodType.toUpperCase()) {
			case "POST":

				if (urlEndPointsSubNode.methods().get(index).body().size() > 0) {
					configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode.methods().get(index).body().get(0).schemaContent(), Util.loadProperties("RAML_LOCATION", appConfigPath) + "\\jsd\\" + urlEndPointsSubNode.methods().get(index).body().get(0).schema().value().toString() + ".1.schema.json").toString());

				}

				break;
			case "GET":
				if (urlEndPointsSubNode.methods().get(index).body().size() > 0) {
					configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode.methods().get(index).body().get(0).schemaContent(), Util.loadProperties("RAML_LOCATION", appConfigPath) + "\\jsd\\" + urlEndPointsSubNode.methods().get(index).body().get(0).schema().value().toString() + ".1.schema.json").toString());
				}

				// configurationTO.setResponseSchemaString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
				// .methods().get(index).responses().get(0).body().get(0).schemaContent()).toString());
			default:

				if (urlEndPointsSubNode.methods().get(index).body().size() > 0) {
					configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode.methods().get(index).body().get(0).schemaContent(), Util.loadProperties("RAML_LOCATION", appConfigPath) + "\\jsd\\" + urlEndPointsSubNode.methods().get(index).body().get(0).schema().value().toString() + ".1.schema.json").toString());
				}
				// configurationTO.setResponseSchemaString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
				// .methods().get(index).responses().get(0).body().get(0).schemaContent()).toString());

			}
			System.out.println(configurationTO.getInputSampleString());

		} catch (Exception e) {
			System.out.println("Error inside swtich case of method type in method copyRAMLDataToDTO()" + e.getMessage());
		}
		System.out.println(configurationTO.getInputSampleString());
		return configurationTO;
	}

	public static void main(String[] args) throws Exception {

		appConfigPath = args[0];

		ArrayList<AppConfigurationPropertiesForDataSheet> appConfigPropertiesForDatasheets = Util.loadPropertiesForDataSheets(appConfigPath);

		List<String> filesListWithFullPath = Util.ramlFilesWithFullPath(appConfigPath);
		List<String> filesList = Util.ramlFiles(appConfigPath);

		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(filesListWithFullPath.get(0));
		ramlModelResult.getApiV08();
		ramlModelResult.getSecurityScheme();

		Api api = ramlModelResult.getApiV08();
		// START***************get all endpoint URLs from RAML
		// file***********************/
		ArrayList<ConfigurationTO> configurationTOEndPointList = new ArrayList<ConfigurationTO>();

		AtomicInteger profileMappingID = new AtomicInteger();
		// ArrayList<AppConfigurationProperties> datasheetListAndEndpoints =
		// Util.loadProperties();
		api.resources().forEach((urlEndPointsNode) -> {
			System.out.println("EndPoints are ==>" + urlEndPointsNode.displayName() + "===>resources size====>" + urlEndPointsNode.resources().size());
			AtomicInteger methodPosition = new AtomicInteger();
			List<Method> methodTypes = urlEndPointsNode.methods();
			for (Method methodName : methodTypes) {
				List<AppConfigurationPropertiesForDataSheet> dataSheetsListNodeLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsNode.resourcePath())).collect(Collectors.toList());
				ConfigurationTO configurationTO = null;
				try {
					configurationTO = copyRAMLDataToDTO(urlEndPointsNode, methodPosition.get());
					configurationTO.setDataSource(dataSheetsListNodeLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
					configurationTO.setProfileMappingID(profileMappingID.getAndIncrement() + "");
					configurationTO.setRamlFileName(filesList.get(0));
					configurationTO.setAppConfigPath(appConfigPath);
					configurationTO.setInputTstFile(inputTstFile);
					configurationTO.setQueryParameters(urlEndPointsNode.methods().get(methodPosition.get()).queryParameters());
					// methodPosition1.incrementAndGet();
					methodPosition.incrementAndGet();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				configurationTOEndPointList.add(configurationTO);

			}
			urlEndPointsNode.resources().forEach((urlEndPointsSubNode) -> {
				List<AppConfigurationPropertiesForDataSheet> dataSheetsListSubNodeLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsSubNode.resourcePath())).collect(Collectors.toList());
				System.out.println(dataSheetsListSubNodeLevel);
				System.out.println("EndPoints are ==>" + urlEndPointsSubNode.displayName() + "===>resources size====>" + urlEndPointsSubNode.resources().size());
				AtomicInteger methodPosition1 = new AtomicInteger();
				List<Method> methodTypes1 = urlEndPointsSubNode.methods();
				for (Method methodName : methodTypes1) {
					ConfigurationTO configurationTOSubNode = null;
					try {
						configurationTOSubNode = copyRAMLDataToDTO(urlEndPointsSubNode, methodPosition1.get());
						configurationTOSubNode.setDataSource(dataSheetsListSubNodeLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
						configurationTOSubNode.setRamlFileName(filesList.get(0));
						configurationTOSubNode.setAppConfigPath(appConfigPath);
						configurationTOSubNode.setInputTstFile(inputTstFile);
						configurationTOSubNode.setQueryParameters(urlEndPointsSubNode.methods().get(methodPosition1.get()).queryParameters());
						methodPosition1.incrementAndGet();
						// configurationTOSubNodeLevelDSList.add(configurationTOSubNode);
					} catch (Exception e) {
						e.printStackTrace();
					}
					configurationTOEndPointList.add(configurationTOSubNode);
				}
				
				urlEndPointsSubNode.resources().forEach((urlEndPointsSubSecondLevelNode) -> {
					List<AppConfigurationPropertiesForDataSheet> dataSheetsListSubNodeSecondLevelLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsSubSecondLevelNode.resourcePath())).collect(Collectors.toList());
					System.out.println(dataSheetsListSubNodeLevel);
					System.out.println("EndPoints are ==>" + urlEndPointsSubSecondLevelNode.displayName() + "===>resources size====>" + urlEndPointsSubSecondLevelNode.resources().size());
					AtomicInteger methodPosition2 = new AtomicInteger();
					List<Method> methodTypes2 = urlEndPointsSubSecondLevelNode.methods();
					for (Method methodName : methodTypes2) {
						ConfigurationTO configurationTOSubNode = null;
						try {
							configurationTOSubNode = copyRAMLDataToDTO(urlEndPointsSubSecondLevelNode, methodPosition2.get());
							configurationTOSubNode.setDataSource(dataSheetsListSubNodeSecondLevelLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
							configurationTOSubNode.setRamlFileName(filesList.get(0));
							configurationTOSubNode.setAppConfigPath(appConfigPath);
							configurationTOSubNode.setInputTstFile(inputTstFile);
							configurationTOSubNode.setQueryParameters(urlEndPointsSubSecondLevelNode.methods().get(methodPosition2.get()).queryParameters());
							methodPosition1.incrementAndGet();
							// configurationTOSubNodeLevelDSList.add(configurationTOSubNode);
						} catch (Exception e) {
							e.printStackTrace();
						}
						configurationTOEndPointList.add(configurationTOSubNode);
					}

				});

			}	);

			// }

		}	);

		try {
			System.out.println("configurationTOEndPointList" + configurationTOEndPointList);
			CreateTSTFile.writeFileUsingJDOM(configurationTOEndPointList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// END***************get all endpoint URLs from RAML

		if (ramlModelResult.hasErrors()) {
			for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
				System.out.println(validationResult.getMessage());
			}
		} else {
			ramlModelResult.getApiV10();

		}
	}

}
