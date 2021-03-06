package com.ibm.automation.parasoft.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.automation.parasoft.XMLElementBuilder;

public class XMLJsonUtils {

	public static Map<String, Object> jsonToMap(JSONObject json) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		if(json != null) {
			retMap = toMap(json);
		}
		return retMap;
	}

	public static Map<String, Object> toMap(JSONObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keySet().iterator();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if(value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) {
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < ((Map<String, Object>) array).size(); i++) {
			Object value = array.get(i);
			if(value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if(value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}


	public static Map<String, Object> jsonString2MapForAssertionsWithXPath( String jsonString, String prependKey ) throws JSONException{
		Map<String, Object> keys = new HashMap<String, Object>();         

		org.json.JSONObject jsonObject = new org.json.JSONObject( jsonString ); // HashMap
		Iterator<?> keyset = jsonObject.keys(); // HM

		while (keyset.hasNext()) {
			String key =  (String) keyset.next();
			if(!key.equalsIgnoreCase("required")){
				Object value = jsonObject.get(key);
				//System.out.print("\n Key : "+key);
				String actualKey=key;
				if(null!= prependKey){
					actualKey=prependKey+"."+key;
				}
				if ( value instanceof org.json.JSONObject ) {
					// System.out.println("Incomin value is of JSONObject : ");
					keys.put( actualKey, jsonString2MapForAssertionsWithXPath( value.toString(), actualKey));
				}else if ( value instanceof org.json.JSONArray) {
					org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
					//JSONArray jsonArray = new JSONArray(value.toString());
					actualKey = actualKey+"[0]";
					keys.put( actualKey, jsonArray2ListForAssertionsWithXpath( jsonArray, actualKey ));
				} else {
					//keyNode( value);
					keys.put( actualKey, value );
				}
			}
		}
		return keys;
	}

	public static List<Object> jsonArray2ListForAssertionsWithXpath( JSONArray arrayOFKeys, String actualKey ) throws JSONException{
		//System.out.println("Incoming value is of JSONArray : =========");
		List<Object> array2List = new ArrayList<Object>();
		for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
			if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
				Map<String, Object> subObj2Map = jsonString2MapForAssertionsWithXPath(arrayOFKeys.opt(i).toString(), actualKey);
				array2List.add(subObj2Map);
			}else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
				List<Object> subarray2List = jsonArray2ListForAssertionsWithXpath((JSONArray) arrayOFKeys.opt(i), actualKey);
				array2List.add(subarray2List);
			}else {
				// keyNode( arrayOFKeys.opt(i) );
				array2List.add( arrayOFKeys.opt(i) );
			}
		}
		return array2List;      
	}

	//Convert JSON to HashMap, so as to put everything into parameters
	public static Map<String, Object> jsonString2Map1( JSONObject jsonObject ) throws JSONException{
		Map<String, Object> keys = new HashMap<String, Object>(); 


		Iterator<?> keyset = jsonObject.keys(); // HM

		while (keyset.hasNext()) {
			String key =  (String) keyset.next();
			Object value = jsonObject.get(key);
			if ( value instanceof JSONObject ) {
				keys.put( key, jsonString2Map1( (JSONObject)value ));
			}else if ( value instanceof JSONArray) {
				JSONArray jsonArray = jsonObject.getJSONArray(key);
				keys.put( key, jsonArray2List1( jsonArray ));
			} else {
				keys.put( key, value!=null?value.toString():"" );
			}
		}
		return keys;
	}
	public static List<Object> jsonArray2List1( JSONArray arrayOFKeys ) throws JSONException{
		System.out.println("Incoming value is of JSONArray : =========");
		List<Object> array2List = new ArrayList<Object>();
		for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
			if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
				Map<String, Object> subObj2Map = jsonString2Map1(arrayOFKeys.opt(i).toString());
				array2List.add(subObj2Map);
			}else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
				List<Object> subarray2List = jsonArray2List1((JSONArray) arrayOFKeys.opt(i));
				array2List.add(subarray2List);
			}else {
				// keyNode( arrayOFKeys.opt(i) );
				array2List.add( arrayOFKeys.opt(i) );
			}
		}
		return array2List;      
	}

	public static Map<String, Object> jsonString2Map1( String jsonString ) throws JSONException{
		Map<String, Object> keys = new HashMap<String, Object>(); 

		org.json.JSONObject jsonObject = new org.json.JSONObject( jsonString ); // HashMap
		Iterator<?> keyset = jsonObject.keys(); // HM

		while (keyset.hasNext()) {
			String key =  (String) keyset.next();
			Object value = jsonObject.get(key);
			System.out.print("\n Key : "+key);
			if ( value instanceof org.json.JSONObject ) {
				System.out.println("Incomin value is of JSONObject : ");
				keys.put( key, jsonString2Map1( value.toString() ));
			}else if ( value instanceof org.json.JSONArray) {
				org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
				//JSONArray jsonArray = new JSONArray(value.toString());
				keys.put( key, jsonArray2List1( jsonArray ));
			} else {
				//keyNode( value);
				keys.put( key, value );
			}
		}
		return keys;
	}
	@SuppressWarnings("unchecked")
	public static void displayJSONMAP( Map<String, Object> allKeys, boolean isArray ) throws Exception{
		Set<String> keyset = allKeys.keySet(); // HM$keyset
		if (! keyset.isEmpty()) {
			Iterator<String> keys = keyset.iterator(); // HM$keysIterator
			while (keys.hasNext()) {
				String key = keys.next();

				Object value = allKeys.get( key );
				if ( value instanceof Map ) {
					System.out.println("\n Object Key : "+key);
					displayJSONMAP((Map<String,Object>)value, false);                   
				}else if ( value instanceof List ) {
					System.out.println("\n Array Key : "+key);
					key = key+"[0]";
					//JSONArray jsonArray = new JSONArray(value.toString());                    
					displayJSONMAP((Map<String, Object>)((List)value).get(0), true);
				}else {

					System.out.println("key : "+key);
				}
			}
		}   

	}

	public static Map<String, Object> jsonString2MapForAssertionsRequiredParams(String jsonString, String prependKey, Element andAssertion ) throws JSONException, IOException {
		Map<String, Object> keys = new HashMap<String, Object>();     
		// Element andAssertion = null;

		org.json.JSONObject jsonObject = new org.json.JSONObject( jsonString ); // HashMap
		Iterator<?> keyset = jsonObject.keys(); // HM

		while (keyset.hasNext()) {
			String key =  (String) keyset.next();
			if(!key.equalsIgnoreCase("required")){
				Object value = jsonObject.get(key);
				//System.out.print("\n Key : "+key);
				String actualKey=key;
				if(null!= prependKey){
					actualKey=prependKey+"."+key;
				}
				if ( value instanceof org.json.JSONObject ) {
					// System.out.println("Incomin value is of JSONObject : ");
					jsonString2MapForAssertionsRequiredParams( value.toString(), actualKey, andAssertion);
					// keys.put( actualKey, );
				}else if ( value instanceof org.json.JSONArray) {
					org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
					jsonArray2ListForAssertionsRequiredParams( jsonArray, actualKey, andAssertion );
				} else {
					//keyNode( value);
					// keys.put( actualKey, value );
				}
			}else{//build assertions for required params	            	
				Object value = jsonObject.get(key);
				String actualKey=key;
				if(null!= prependKey){
					actualKey=prependKey;
				}
				if ( value instanceof org.json.JSONArray) {
					org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
					// XMLElementBuilder.buildStringComparisonAssertions(jsonArray, actualKey, andAssertion);


				}
			}
		}
		return keys;
	}


	public static Map<String, Object> jsonString2MapForReponseCodeAssertions(String responseCode, String responseSchema, String prependKey, Element xmlorAndAssertionTool) throws JSONException, IOException {
		Map<String, Object> keys = new HashMap<String, Object>();   
		org.json.JSONObject jsonObject = new org.json.JSONObject(responseSchema); // HashMap
		Iterator<?> keyset = jsonObject.keys(); // HM

		// if(prependKey == null){	        	
		LinkedHashMap<String, Object> keys1 = new LinkedHashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		System.out.print("\n Incoming json string for reference : " + responseSchema);
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String,Object>>() {};
		LinkedHashMap<String,?> responseSchemaMap = mapper.readValue(responseSchema, typeRef); 
		boolean andAssertionAdded = false;
		for (String key : responseSchemaMap.keySet()){
			String actualKey=key;
			

		if(!key.equalsIgnoreCase("required")){
			if(null!= prependKey ){			
				actualKey=prependKey+"/"+key;
			}
			Object value = responseSchemaMap.get(key);
			if ( value instanceof LinkedHashMap ) {
				System.out.println("Incomin value is of JSONObject : ");
				String mapToJson = mapper.writeValueAsString(value);
				//actualKey=prependKey+"/";
				jsonString2MapForReponseCodeAssertions( responseCode, mapToJson, actualKey, xmlorAndAssertionTool);
			}else if ( value instanceof ArrayList) {
				System.out.println("Incomin value is of JSONArray : ");
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(value);
				actualKey=actualKey+"/item";
				jsonArray2ListForReponseCodeAssertions( mapToJson, actualKey, responseCode, xmlorAndAssertionTool);
			}else{
				//actualKey=prependKey;				
				if(!andAssertionAdded){
					buildAndAssertionsForRequiredFields(key, responseSchemaMap, xmlorAndAssertionTool, prependKey);
				}
				andAssertionAdded=true;
			}
			//System.out.println(actualKey);
		}


		}
		return keys;
	}

	private static org.json.JSONArray compareRequiredAttribitesAgainstValueList(org.json.JSONObject jsonObject, org.json.JSONArray jsonArray) {

		for(int i=0; i<jsonArray.length(); i++){
			if(jsonObject.get(jsonArray.getString(i)) instanceof org.json.JSONObject || jsonObject.get(jsonArray.getString(i)) instanceof org.json.JSONArray) {				
				jsonArray.remove(i);
			}
		}
		return jsonArray;
	}

	public static List<Object> jsonArray2ListForAssertionsRequiredParams(JSONArray arrayOFKeys, String actualKey, Element andAssertion) throws JSONException, IOException {
		List<Object> array2List = new ArrayList<Object>();
		for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
			if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
				Map<String, Object> subObj2Map = jsonString2MapForAssertionsRequiredParams(arrayOFKeys.opt(i).toString(), actualKey, andAssertion);
				array2List.add(subObj2Map);
			}else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
				List<Object> subarray2List = jsonArray2ListForAssertionsRequiredParams((JSONArray) arrayOFKeys.opt(i), actualKey, andAssertion);
				array2List.add(subarray2List);
			}else {
				array2List.add( arrayOFKeys.opt(i) );
			}
		}
		return array2List;  
	}

	public static List<Object> jsonArray2ListForReponseCodeAssertions(String arrayOFKeys, String actualKey, String responseCode, Element xmlAssertionTool) throws JSONException, IOException {

		ObjectMapper mapper = new ObjectMapper();		
		TypeReference<ArrayList<?>> typeRef = new TypeReference<ArrayList<?>>() {};        
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
		for ( int i = 0; i < arraySize; i++ )  {
			if ( arrayOFKeysList.get(i) instanceof LinkedHashMap ) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				Map<String, Object> subObj2Map = jsonString2MapForReponseCodeAssertions(responseCode, mapToJson, actualKey, xmlAssertionTool);
				array2List.add(subObj2Map);
			}else if ( arrayOFKeysList.get(i) instanceof ArrayList ) {
				System.out.println("Incomin value is of JSONObject : "+arrayOFKeysList.get(i).toString());
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				String mapToJson = objectMapper.writeValueAsString(arrayOFKeysList.get(i));
				List<Object> subarray2List = jsonArray2ListForReponseCodeAssertions(mapToJson, actualKey, responseCode, xmlAssertionTool);
				array2List.add(subarray2List);
			}else {
				array2List.add( arrayOFKeysList.get(i) );
			}
		}
		return array2List;  
	}	

	public static String replaceDotWithSlashForAssertion_Xpath(String incomingXpath){
		//String replacementStr= "\"][\"";
		String replacementStr= "/";
		incomingXpath = StringUtils.replace(incomingXpath, ".", replacementStr);
		return incomingXpath;

	}

	public static String replaceDotWithUnderscore(String incomingXpath){
		String replacementStr= "_";
		incomingXpath = StringUtils.replace(incomingXpath, ".", replacementStr);
		incomingXpath = StringUtils.replace(incomingXpath, "/", replacementStr);
		incomingXpath = StringUtils.replace(incomingXpath, "_item", "");
		System.out.println("incomingXpath"+incomingXpath);
		return incomingXpath;

	}
	
	public static String removebraces(String incomingXpath){
		String replacementStr= "";
		incomingXpath = StringUtils.replace(incomingXpath, "{", replacementStr);
		incomingXpath = StringUtils.replace(incomingXpath, "}", replacementStr);
		return incomingXpath;

	}

	/**
	 * This method build AND assertion for negative scenarios
	 * or set of string comparison assertion for positive scenarios 
	 * for each required field in response json
	 * @param key
	 * @param responseMap
	 * @param xmlorAndAssertionTool
	 * @param actualKey
	 * @throws IOException
	 */
	public static void buildAndAssertionsForRequiredFields(String key, LinkedHashMap<String,?> responseMap, Element xmlorAndAssertionTool, String actualKey) throws IOException{
			@SuppressWarnings("unchecked")
			org.json.JSONArray jsonArray =  new org.json.JSONArray((ArrayList<String>)responseMap.get("required"));
			/** check if the required attribute is not an JSON Object, 
 			            if it is a Object, we cannot create assertions for that attribute
			 */
			XMLElementBuilder.buildStringComparisonAssertions(jsonArray, actualKey, xmlorAndAssertionTool);		               
			int andAssertionSizeLocal = 0;
			if(!xmlorAndAssertionTool.getChild("assertionsSize").getContent().get(0).getValue().toString().contains("1-template")){
				andAssertionSizeLocal = Integer.parseInt(xmlorAndAssertionTool.getChild("assertionsSize").getContent().get(0).getValue().toString());
			}
			System.out.println("=======total size of assertions===========>"+(andAssertionSizeLocal+jsonArray.length())+"");
			xmlorAndAssertionTool.getChild("assertionsSize").removeContent();
			xmlorAndAssertionTool.getChild("assertionsSize").addContent((andAssertionSizeLocal+jsonArray.length())+"");			

	}
	

	}