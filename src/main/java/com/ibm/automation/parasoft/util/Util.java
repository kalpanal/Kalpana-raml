package com.ibm.automation.parasoft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.automation.common.StringUtilsCustom;
import com.ibm.automation.parasoft.domain.AppConfigurationPropertiesForDataSheet;

public class Util {

	public static ArrayList<AppConfigurationPropertiesForDataSheet> loadPropertiesForDataSheets(String appConfigPath) throws Exception {

		ArrayList<AppConfigurationPropertiesForDataSheet> endPointURLAndDataSheetName = new ArrayList<AppConfigurationPropertiesForDataSheet>();
		Scanner scanAppConfigFile = null;
		try{
		scanAppConfigFile = new Scanner(new File(appConfigPath));
		while (scanAppConfigFile.hasNextLine()) {
			// System.out.println(scan.nextLine());
			String datasheetAndTestSuiteName[] = scanAppConfigFile.nextLine()
					.trim().split("=");
			if (datasheetAndTestSuiteName.length == 2) {
				if(datasheetAndTestSuiteName[0].indexOf("/") == 0){
					endPointURLAndDataSheetName.add(new AppConfigurationPropertiesForDataSheet(datasheetAndTestSuiteName[0]
						, datasheetAndTestSuiteName[1]));
				}
				// v.add(datasheetAndTestSuiteName[1]);
				// System.out.println("pair " + datasheetAndTestSuiteName[0] +
				// ":" + datasheetAndTestSuiteName[1]);
			}
			// System.out.println("a");*/
		}
		}
		finally {
			scanAppConfigFile.close();
	    }
		return endPointURLAndDataSheetName;
	}
	
	public static List<String> ramlFiles(String appConfigPath) throws Exception {
		  List<String> ramlFiles = new ArrayList<String>();
		  File dir = new File(loadProperties("RAML_LOCATION", appConfigPath));
		  for (File file : dir.listFiles()) {
		    if (file.getName().endsWith((".raml"))) {
		      ramlFiles.add(file.getName());
		    }
		  }
		  return ramlFiles;
		}
	public static List<String> ramlFilesWithFullPath(String appConfigPath) throws Exception {
		  List<String> ramlFiles = new ArrayList<String>();
		  File dir = new File(loadProperties("RAML_LOCATION", appConfigPath));
		  for (File file : dir.listFiles()) {
		    if (file.getName().endsWith((".raml"))) {
		      ramlFiles.add(loadProperties("RAML_LOCATION", appConfigPath)+"/"+file.getName());
		    }
		  }
		  return ramlFiles;
		}
	public static String loadProperties(String propertyKey, String appConfigPath) throws Exception {
		Properties prop = new Properties();
		InputStream input = null;
		String propertyValue = null;

		try {

			input = new FileInputStream(appConfigPath);
			// load a properties file
			prop.load(input);
			propertyValue = prop.getProperty(propertyKey);
			// get the property value and print it out
			System.out.println(prop.getProperty(propertyKey));
			

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return propertyValue;
	}

	public static String[] tokenizePathURLEndpoint(String tokenizePathURLEndpoint){
		
		ArrayList<Integer> listofPositionsOfColon = StringUtilsCustom.occurrencesPos(tokenizePathURLEndpoint, "/");
		
		//tokenizePathURLEndpoint.substring(tokenizePathURLEndpoint.indexOf(":", listofPositionsOfColon.get(1))+1);
		//tokenizePathURLEndpoint = tokenizePathURLEndpoint.substring(tokenizePathURLEndpoint.indexOf("/"));
		System.out.println("splitting of sprint starts \n");
        String[] urlPathTokens = tokenizePathURLEndpoint.substring(tokenizePathURLEndpoint.indexOf("/", listofPositionsOfColon.get(1))+1).split("/");
		return urlPathTokens;
		
	}
/*	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		ArrayList<DataSheetEndpoint> list = loadProperties();
		List<DataSheetEndpoint> filteredlist = list.stream().filter(p -> p.getEndpointUrl().equals("/printdeliveries")).collect(Collectors.toList());
		System.out.println(filteredlist);
	}*/

	public static ArrayList<String> loadDataSheets(String appConfigPath) throws Exception {
		ArrayList<String> dataSheetsList = new ArrayList<String>();
		  File dir = new File(loadProperties("DATA_SOURCE_PATH", appConfigPath));
		  for (File file : dir.listFiles()) {
		    if (file.getName().endsWith((".xlsx"))) {
		    	dataSheetsList.add(file.getName());
		    }
		  }
		return dataSheetsList;
	}
	
	public static Map<String, Object> jsonString2Map( String jsonString ) throws JSONException{
        Map<String, Object> keys = new HashMap<String, Object>(); 

        org.json.JSONObject jsonObject = new org.json.JSONObject( jsonString ); // HashMap
        Iterator<?> keyset = jsonObject.keys(); // HM

        while (keyset.hasNext()) {
            String key =  (String) keyset.next();
            Object value = jsonObject.get(key);
            System.out.print("\n Key : "+key);
            if ( value instanceof org.json.JSONObject ) {
                System.out.println("Incomin value is of JSONObject : ");
                keys.put( key, jsonString2Map( value.toString() ));
            }else if ( value instanceof org.json.JSONArray) {
                org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
                //JSONArray jsonArray = new JSONArray(value.toString());
                keys.put( key, jsonArray2List( jsonArray ));
            } else {
               // keyNode( value);
                keys.put( key, value );
            }
        }
        return keys;
    }
	
	public static List<Object> jsonArray2List( JSONArray arrayOFKeys ) throws JSONException{
        System.out.println("Incoming value is of JSONArray : =========");
        List<Object> array2List = new ArrayList<Object>();
        for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
            if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
                Map<String, Object> subObj2Map = jsonString2Map(arrayOFKeys.opt(i).toString());
                array2List.add(subObj2Map);
            }else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
                List<Object> subarray2List = jsonArray2List((JSONArray) arrayOFKeys.opt(i));
                array2List.add(subarray2List);
            }else {
                //keyNode( arrayOFKeys.opt(i) );
                array2List.add( arrayOFKeys.opt(i) );
            }
        }
        return array2List;      
    }
	
	public static boolean checkNumberOnly(String incomingStr){
		return incomingStr.matches("[0-9]+");
	}
}
