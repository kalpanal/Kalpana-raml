package com.ibm.automation.parasoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

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
    
    
    public static Map<String, Object> jsonString2Map( String jsonString, String prependKey ) throws JSONException{
        Map<String, Object> keys = new HashMap<String, Object>(); 

        org.json.JSONObject jsonObject = new org.json.JSONObject( jsonString ); // HashMap
        Iterator<?> keyset = jsonObject.keys(); // HM

        while (keyset.hasNext()) {
            String key =  (String) keyset.next();
            Object value = jsonObject.get(key);
            //System.out.print("\n Key : "+key);
            String actualKey=key;
            if(null!= prependKey){
				actualKey=prependKey+"."+key;
            }
            if ( value instanceof org.json.JSONObject ) {
               // System.out.println("Incomin value is of JSONObject : ");
                keys.put( actualKey, jsonString2Map( value.toString(), actualKey));
            }else if ( value instanceof org.json.JSONArray) {
                org.json.JSONArray jsonArray = jsonObject.getJSONArray(key);
                //JSONArray jsonArray = new JSONArray(value.toString());
                keys.put( actualKey, jsonArray2List( jsonArray, actualKey ));
            } else {
                //keyNode( value);
                keys.put( actualKey, value );
            }
        }
        return keys;
    }
    
    public static List<Object> jsonArray2List( JSONArray arrayOFKeys, String actualKey ) throws JSONException{
        //System.out.println("Incoming value is of JSONArray : =========");
        List<Object> array2List = new ArrayList<Object>();
        for ( int i = 0; i < arrayOFKeys.length(); i++ )  {
            if ( arrayOFKeys.opt(i) instanceof JSONObject ) {
                Map<String, Object> subObj2Map = jsonString2Map(arrayOFKeys.opt(i).toString(), actualKey);
                array2List.add(subObj2Map);
            }else if ( arrayOFKeys.opt(i) instanceof JSONArray ) {
                List<Object> subarray2List = jsonArray2List((JSONArray) arrayOFKeys.opt(i), actualKey);
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
    
}