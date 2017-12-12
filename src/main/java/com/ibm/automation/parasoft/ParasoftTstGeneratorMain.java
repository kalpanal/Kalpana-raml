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
import org.raml.v2.api.model.v08.resources.Resource;

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
					System.out.println(appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsNode.resourcePath())));
					
					List<AppConfigurationPropertiesForDataSheet> dataSheetsListNodeLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsNode.resourcePath())).collect(Collectors.toList());
					
					System.out.println(dataSheetsListNodeLevel);

						ConfigurationTO configurationTO = null;
						try {
							configurationTO = copyRAMLDataToDTO(urlEndPointsNode);							
							configurationTO.setDataSource(dataSheetsListNodeLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
							configurationTO.setProfileMappingID(profileMappingID.getAndIncrement()+"");
							configurationTO.setRamlFileName(filesList.get(0));
							configurationTO.setAppConfigPath(appConfigPath);
							configurationTO.setInputTstFile(inputTstFile);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					configurationTOEndPointList.add(configurationTO);
					
					
					// if(urlEndPointsNode.resources().size() > 0){
					urlEndPointsNode.resources().forEach(
							(urlEndPointsSubNode) -> {						
								//System.out.println(urlEndPointsSubNode.methods().get(0).queryParameters());
								ArrayList<ConfigurationTO> configurationTOSubNodeLevelDSList = new ArrayList<ConfigurationTO>();
								List<AppConfigurationPropertiesForDataSheet> dataSheetsListSubNodeLevel = appConfigPropertiesForDatasheets.stream().filter(p -> p.getEndpointUrl().equals(urlEndPointsSubNode.resourcePath())).collect(Collectors.toList());
								System.out.println(dataSheetsListSubNodeLevel);							
									ConfigurationTO configurationTOSubNode = null;
									try {
										configurationTOSubNode = copyRAMLDataToDTO(urlEndPointsSubNode);	
										configurationTOSubNode.setDataSource(dataSheetsListSubNodeLevel.stream().map(i -> i.getDatasheetName()).collect(Collectors.toList()));
										configurationTOSubNode.setRamlFileName(filesList.get(0));
										configurationTOSubNode.setAppConfigPath(appConfigPath);
										configurationTOSubNode.setInputTstFile(inputTstFile);
										configurationTOSubNode.setQueryParameters(urlEndPointsSubNode.methods().get(0).queryParameters());
										//configurationTOSubNodeLevelDSList.add(configurationTOSubNode);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								configurationTOEndPointList.add(configurationTOSubNode);
								
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

	public static ConfigurationTO copyRAMLDataToDTO(Resource urlEndPointsSubNode) throws Exception {
		// System.out.println("EndPoints are ==>"+urlEndPointsNode.displayName()+urlEndPointsSubNode.displayName());
		System.out.println("Method type is GET/PUT/POST/DELETE ==>"
				+ urlEndPointsSubNode.methods().get(0).method());
		System.out.println("endpoint URL is  ==>"
				+ urlEndPointsSubNode.resourcePath());
		System.out.println("securedBy ==>"
				+ urlEndPointsSubNode.methods().get(0).securedBy().get(0)
						.name());
		System.out.println("responses code ==>"
				+ urlEndPointsSubNode.methods().get(0).responses().get(0)
						.code().value());
		// System.out.println("schema sample string ---->"+urlEndPointsSubNode
		// .methods().get(0).body().get(0).example().value());

		ConfigurationTO configurationTO = new ConfigurationTO();

		String inputMethodType = urlEndPointsSubNode.methods().get(0).method();
		configurationTO.setMethod(inputMethodType);
		configurationTO.setDataSourcePath(Util.loadProperties("DATA_SOURCE_PATH", appConfigPath));
		configurationTO.setEndPointUrl(Util.loadProperties("BASE_URL", appConfigPath)
				+ urlEndPointsSubNode.resourcePath());
		configurationTO.setTestPathUrl(Util.loadProperties("BASE_URL", appConfigPath)+urlEndPointsSubNode.resourcePath());
		configurationTO.setSecuredBy(urlEndPointsSubNode.methods().get(0)
				.securedBy().get(0).name());
		configurationTO.setValidRange(urlEndPointsSubNode.methods().get(0).responses().get(0).code().toString());
		//if(urlEndPointsSubNode.methods().get(0).method().equalsIgnoreCase("POST"))
		if(urlEndPointsSubNode.methods().get(0).body().size() > 0){
			configurationTO.setInputSampleString(urlEndPointsSubNode.methods()
					.get(0).body().get(0).example().value());
		}
		//configurationTO.setDataSource(dataSheetName);
		
		// configurationTO.setHeadersList(headersList);

		switch (inputMethodType.toUpperCase()) {
		case "POST":
			ObjectMapper mapper = new ObjectMapper();
			JsonNode schemaPropertiesArray = null,
			schemaRequiredArray = null,
			schemadefinitionsArray = null;
			try {
				schemaPropertiesArray = mapper.readTree(urlEndPointsSubNode
						.methods().get(0).body().get(0).schemaContent()
						.toString());
				/*System.out.println(urlEndPointsSubNode
						.methods().get(0).body().get(0).schemaContent().replaceAll("$ref", "type"));
				SchemaV4 schema1 =  new SchemaV4().wrap((JsonObject) JsonElement.readFrom( urlEndPointsSubNode
						.methods().get(0).body().get(0).schemaContent().replaceAll("$ref", "type")
					)); */
				
				//System.out.println("generation without settings"+new JsonGenerator(schema1, null).generate());
				//System.out.println("generation with settings"+generateWithSettings(schema1));
				if(urlEndPointsSubNode.methods().get(0).body().size() >0){
					System.out.println(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
						.methods().get(0).body().get(0).schemaContent()).toString());
				configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
						.methods().get(0).body().get(0).schemaContent()).toString());
				}
				schemaRequiredArray = mapper.readTree(
						urlEndPointsSubNode.methods().get(0).body().get(0)
								.schemaContent().getBytes()).get("required");
				schemadefinitionsArray = mapper.readTree(
						urlEndPointsSubNode.methods().get(0).body().get(0)
								.schemaContent().toString()).get("definitions");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case "GET":
			if(urlEndPointsSubNode.methods().get(0).body().size() >0){
			configurationTO.setInputSampleString(JsonGeneratorFromSchema.generateInputSampleString(urlEndPointsSubNode
					.methods().get(0).body().get(0).schemaContent()).toString());
			}


		}
		return configurationTO;
	}


}
