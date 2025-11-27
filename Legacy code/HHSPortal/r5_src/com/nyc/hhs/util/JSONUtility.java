/**
 * 
 */
package com.nyc.hhs.util;

/**
 * This utility class provides the functionality to translate property Name and convert To String
 */
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;

public class JSONUtility extends PropertyNamingStrategyBase
{
	@Override
	public String translate(String propertyName)
	{
		return propertyName;
	}
	
/**
 * This method convert a text into String
 * @param obj an object
 * @return the converted string
 * @throws JsonProcessingException
 */
	public static String convertToString(Object obj) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.setPropertyNamingStrategy(new JSONUtility());
		return mapper.writeValueAsString(obj);
	}
}