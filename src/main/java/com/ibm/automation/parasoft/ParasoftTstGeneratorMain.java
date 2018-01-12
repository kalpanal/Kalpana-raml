package com.ibm.automation.parasoft;

import java.io.IOException;
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
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v08.methods.Method;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.automation.common.StringUtilsCustom;
import com.ibm.automation.parasoft.domain.AppConfigurationPropertiesForDataSheet;
import com.ibm.automation.parasoft.domain.ConfigurationTO;
import com.ibm.automation.parasoft.util.JsonGeneratorFromSchema;
import com.ibm.automation.parasoft.util.Util;


/**
 * Hello world!
 *
 */
public class ParasoftTstGeneratorMain {

	static String appConfigPath;
	static String inputTstFile;
	public static void main(String[] args) throws Exception {

		appConfigPath = args[0];

		ArrayList<AppConfigurationPropertiesForDataSheet> appConfigPropertiesForDatasheets = Util.loadPropertiesForDataSheets(appConfigPath);


		List<String> filesListWithFullPath  = Util.ramlFilesWithFullPath(appConfigPath);
		List<String> filesList  = Util.ramlFiles(appConfigPath);



		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(filesListWithFullPath.get(0));
		ramlModelResult.getApiV08();
		ramlModelResult.getSecurityScheme();

		Api api = (Api) ramlModelResult.getApiV08();
		// START***************get all endpoint URLs from RAML
		// file***********************/
		ArrayList<ConfigurationTO> configurationTOEndPointList = new ArrayList<ConfigurationTO>();

		AtomicInteger profileMappingID = new AtomicInteger();
		//ArrayList<AppConfigurationProperties> datasheetListAndEndpoints = Util.loadProperties();
		api.resources().forEach(
				(urlEndPointsNode) -> {
					System.out.println("EndPoints are ==>"
							+ urlEndPointsNode.displayName()
							+ "===>resources size====>"
							+ urlEndPointsNode.resources().size());
					AtomicInteger methodPosition = new AtomicInteger();
					List<Method> methodTypes = urlEndPointsNode.methods();
					for(Method methodName : methodTypes)  {						
						List<AppConfigurationPropertiesForDataSheet> dataSheetsListNodeLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsNode.resourcePath())).collect(Collectors.toList());
						ConfigurationTO configurationTO = null;
						try {
							configurationTO = copyRAMLDataToDTO(urlEndPointsNode, methodPosition.get());							
							configurationTO.setDataSource(dataSheetsListNodeLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
							configurationTO.setProfileMappingID(profileMappingID.getAndIncrement()+"");
							configurationTO.setRamlFileName(filesList.get(0));
							configurationTO.setAppConfigPath(appConfigPath);
							configurationTO.setInputTstFile(inputTstFile);
							methodPosition.incrementAndGet();

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						configurationTOEndPointList.add(configurationTO);

					}
					/* below code is to iterate over sub-node URLs under main endpoint URL in RAML file */
					// if(urlEndPointsNode.resources().size() > 0){
					int methodPos = 0;
					urlEndPointsNode.resources().forEach(
							(urlEndPointsSubNode) -> {						
								List<AppConfigurationPropertiesForDataSheet> dataSheetsListSubNodeLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsSubNode.resourcePath())).collect(Collectors.toList());
								System.out.println(dataSheetsListSubNodeLevel);	
								AtomicInteger methodPosition1 = new AtomicInteger();
								List<Method> methodTypes1 = urlEndPointsSubNode.methods();
								for(Method methodName : methodTypes1)  {
									ConfigurationTO configurationTOSubNode = null;
									try {
										configurationTOSubNode = copyRAMLDataToDTO(urlEndPointsSubNode,methodPosition1.get());	
										configurationTOSubNode.setDataSource(dataSheetsListSubNodeLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
										configurationTOSubNode.setRamlFileName(filesList.get(0));
										configurationTOSubNode.setAppConfigPath(appConfigPath);
										configurationTOSubNode.setInputTstFile(inputTstFile);
										configurationTOSubNode.setQueryParameters(urlEndPointsSubNode.methods().get(methodPosition1.get()).queryParameters());
										methodPosition1.incrementAndGet();
										//configurationTOSubNodeLevelDSList.add(configurationTOSubNode);
									} catch (Exception e) {
										e.printStackTrace();
									}
									configurationTOEndPointList.add(configurationTOSubNode);
								}

							});

					// }


				});

		try {
			System.out.println("configurationTOEndPointList"+configurationTOEndPointList);
			CreateTSTFile.writeFileUsingJDOM( configurationTOEndPointList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// END***************get all endpoint URLs from RAML

		if (ramlModelResult.hasErrors()) {
			for (ValidationResult validationResult : ramlModelResult
					.getValidationResults()) {
				System.out.println(validationResult.getMessage());
			}
		} else {
			Api api1 = (Api) ramlModelResult.getApiV10();

		}
	}

	public static ConfigurationTO copyRAMLDataToDTO(Resource urlEndPointsSubNode, int index) throws Exception {
		// System.out.println("EndPoints are ==>"+urlEndPointsNode.displayName()+urlEndPointsSubNode.displayName());
		System.out.println("Method type is GET/PUT/POST/DELETE ==>"	+ urlEndPointsSubNode.methods().get(index).method());
		System.out.println("endpoint URL is  ==>"	+ urlEndPointsSubNode.resourcePath());
		System.out.println("securedBy ==>"	+ urlEndPointsSubNode.methods().get(index).securedBy().get(0).name());
		//System.out.println("responses schema content ==>"	+ urlEndPointsSubNode.methods().get(index).responses().get(0).body().get(0));


		ConfigurationTO configurationTO = new ConfigurationTO();
		
		// set the response schema content
		AtomicInteger j = new AtomicInteger();
		HashMap<String, String> responseSchemaMap = new HashMap<String, String>();
		
		List<Response> res1 = urlEndPointsSubNode.methods().get(index).responses();
		for(int k=0; k<2; k++){
			//response.get(i);
			Response response = res1.get(k);
			if(response.body() != null && (!response.body().isEmpty())){
				//System.out.println(response.body().get(0).schemaContent());
				try {
					//System.out.println(response.description().value());
					responseSchemaMap.put(response.code().value(), JsonGeneratorFromSchema.generateInputSampleString(response.body().get(0).schemaContent()).toString());
					//configurationTO.setResponseSchemaString(JsonGeneratorFromSchema.generateInputSampleString(response.body().get(0).schemaContent()).toString());
				} catch (Exception e) {
					System.out.println("Error while reading response body schema content"+e.getMessage());
					e.printStackTrace();
				}
			}else{
				System.out.println(response.description().value());
				responseSchemaMap.put(response.code().value(), "");
			}
			
			
		}
 		configurationTO.setResponseSchemaMap(responseSchemaMap);
		String inputMethodType = urlEndPointsSubNode.methods().get(index).method();
		configurationTO.setMethod(inputMethodType);
		configurationTO.setDataSourcePath(Util.loadProperties("DATA_SOURCE_PATH", appConfigPath));
		configurationTO.setEndPointUrl(Util.loadProperties("BASE_URL", appConfigPath)
				+ urlEndPointsSubNode.resourcePath());
		configurationTO.setTestPathUrl(Util.loadProperties("BASE_URL", appConfigPath)+urlEndPointsSubNode.resourcePath());
		configurationTO.setSecuredBy(urlEndPointsSubNode.methods().get(index)
				.securedBy().get(0).name());
		configurationTO.setValidRange(urlEndPointsSubNode.methods().get(index).responses().get(0).code().toString());
		ObjectMapper mapper = new ObjectMapper();

		try {

			switch (inputMethodType.toUpperCase()) {
			case "POST":
				
				if(urlEndPointsSubNode.methods().get(index).body().size() >0){					
					configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
							.methods().get(index).body().get(0).schemaContent()).toString());
					
				}
				
				
				
				break;
			case "GET":
				if(urlEndPointsSubNode.methods().get(index).body().size() >0){
					configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
							.methods().get(index).body().get(0).schemaContent()).toString());
				}
				

			//	configurationTO.setResponseSchemaString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
				//		.methods().get(index).responses().get(0).body().get(0).schemaContent()).toString());
			default:

				if(urlEndPointsSubNode.methods().get(index).body().size() >0){
					configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
							.methods().get(index).body().get(0).schemaContent()).toString());
				}
			//	configurationTO.setResponseSchemaString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
				//		.methods().get(index).responses().get(0).body().get(0).schemaContent()).toString());


			}

		} catch (Exception e) {
			System.out.println("Error inside swtich case of method type in method copyRAMLDataToDTO()"+e.getMessage());
		}
		System.out.println(configurationTO.getInputSampleString());
		return configurationTO;
	}


}
