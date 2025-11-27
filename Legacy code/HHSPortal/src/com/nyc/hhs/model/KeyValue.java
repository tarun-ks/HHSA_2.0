package com.nyc.hhs.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is a bean which maintains the Key Value information.
 *
 */

public class KeyValue {

	private String key;
	private String value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public static Map<String,Object> convertListToMap(List<KeyValue> aoKeyValue){
		Map<String,Object> loConvertedMap = new HashMap<String, Object>();
		if(aoKeyValue != null){
			
			Iterator<KeyValue> loItr = aoKeyValue.iterator();
			while(loItr.hasNext()){
				KeyValue loKeyValue = loItr.next();
				loConvertedMap.put(loKeyValue.getKey(), loKeyValue.getValue());
			}
		}
		return loConvertedMap;
	}
	@Override
	public String toString() {
		return "KeyValue [key=" + key + ", value=" + value + "]";
	}
}
