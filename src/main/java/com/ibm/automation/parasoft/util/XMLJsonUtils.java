package com.ibm.automation.parasoft.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
	
	public static Map<String, Object> jsonString2MapForReponseCodeAssertions(String responseCode, String responseSchema, String prependKey, Element andAssertion, int andAssertionSize) throws JSONException, IOException {
	       Map<String, Object> keys = new HashMap<String, Object>();   
	        org.json.JSONObject jsonObject = new org.json.JSONObject(responseSchema); // HashMap
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
		            	jsonString2MapForReponseCodeAssertions(responseCode, value.toString(), actualKey, andAssertion, andAssertionSize);
		               // keys.put( actualKey, );
		            }else if ( value instanceof org.json.JSONArray) {
		                org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
		                
		                jsonArray2ListForReponseCodeAssertions( jsonArray, actualKey, responseCode, andAssertion, andAssertionSize );
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
		               // andAssertionSize = (andAssertionSize + jsonArray.length());
		                XMLElementBuilder.buildStringComparisonAssertions(jsonArray, actualKey, andAssertion);		               
						//System.out.println(jsonArray.length()+"=======assertionsSize+++++++++++++++++>"+andAssertionSize+"");
		                int andAssertionSizeLocal = 0;
						if(!andAssertion.getChild("assertionsSize").getContent().get(0).getValue().toString().contains("11")){
							andAssertionSizeLocal = Integer.parseInt(andAssertion.getChild("assertionsSize").getContent().get(0).getValue().toString());
						}
						 System.out.println("=======before assertionsSize+++++++++++++++++>"+(andAssertionSizeLocal+jsonArray.length())+"");
		        		andAssertion.getChild("assertionsSize").removeContent();
		        		andAssertion.getChild("assertionsSize").addContent((andAssertionSizeLocal+jsonArray.length())+"");   	
		            }
	            }
	        }
	        return keys;
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

	public static List<Object> jsonArray2ListForReponseCodeAssertions(JSONArray arrayOFKeys, String actualKey, String responseCode, Element jsonAssertionTool, int andAssertionSize) throws JSONException, IOException {
        List<Object> array2List = new ArrayList<Object>();
        for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
            if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
                Map<String, Object> subObj2Map = jsonString2MapForReponseCodeAssertions(responseCode, arrayOFKeys.opt(i).toString(), actualKey, jsonAssertionTool, andAssertionSize);
                array2List.add(subObj2Map);
            }else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
                List<Object> subarray2List = jsonArray2ListForReponseCodeAssertions((JSONArray) arrayOFKeys.opt(i), actualKey, responseCode, jsonAssertionTool, andAssertionSize);
                array2List.add(subarray2List);
            }else {
                array2List.add( arrayOFKeys.opt(i) );
            }
        }
        return array2List;  
       }	
	

}