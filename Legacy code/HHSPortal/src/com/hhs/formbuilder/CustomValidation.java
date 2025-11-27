package com.hhs.formbuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.accenture.formtaglib.ErrorMessages;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class performs the role of validating a form to its corresponding rules.
 * 
 */

public class CustomValidation
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CustomValidation.class);

	/**
	 * This method validates a form to its corresponding rules
	 * 
	 * @param asFieldName - field name to be validated
	 * @param asFieldValue - field value against the field to be validated
	 * @param asFormName - form name for which validation to be done
	 * @param asFormVersion - form Version for which validation to be done
	 * @return blank for no error and the corresponding error message if error
	 *         exist
	 * @throws ApplicationException
	 */
	public static String customValidation(String asFieldName, String asFieldValue, String asFormName,
			String asFormVersion) throws ApplicationException
	{
		LOG_OBJECT.Debug("public customValidation1 start");
		String lsErrorMessage = "";
		Document loValidationDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.VALIDATION_CACHE_REF);
		if (asFieldValue != null)
		{
			Element loCorrRuleElt = XMLUtil.getElement("//" + ApplicationConstants.FORM_RULE_NODE + "[@formname=\""
					+ asFormName + "\" and @version=\"" + asFormVersion + "\"]/" + ApplicationConstants.ELEMENT_NODE
					+ "[@name=\"" + asFieldName + "\"]", loValidationDoc);
			if (loCorrRuleElt != null)
			{
				List<Element> loRuleEltList = loCorrRuleElt.getChildren();
				Boolean lbReturnedValue = false;
				for (Element loRule : loRuleEltList)
				{
					lbReturnedValue = invokeValidation(asFieldValue, loRule);
					if (!lbReturnedValue)
					{
						lsErrorMessage = loRule.getAttributeValue("errorMessage");
						break;
					}
				}
			}
		}
		LOG_OBJECT.Debug("public customValidation1 end");
		return lsErrorMessage;
	}

	/**
	 * Custom validation for mandatory on forms
	 * 
	 * @param aoXMLDom - jdom of form template
	 * @param aoHmRequest - complete hashmap of form
	 * @param asFormName - form name for which validation to be done
	 * @param asFormVersion - form Version for which validation to be done
	 * @param aoMandatoryList - List of mandatory elements
	 * @return List loErrList represents the list of errors.
	 * @throws ApplicationException
	 */
	public static List<ErrorMessages> customValidation(Document aoXMLDom, HashMap<String, Object> aoHmRequest,
			String asFormName, String asFormVersion, List<String> aoMandatoryList) throws ApplicationException
	{
		LOG_OBJECT.Debug("public customValidation2 start");
		String lsErrorMessage = "";
		List<ErrorMessages> loErrList = new ArrayList<ErrorMessages>();
		Document loValidationDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.VALIDATION_CACHE_REF);
		List<Element> loCorrRuleEltList = XMLUtil.getElementList("//" + ApplicationConstants.FORM_RULE_NODE
				+ "[@formname=\"" + asFormName + "\" and @version=\"" + asFormVersion + "\"]/"
				+ ApplicationConstants.MANDATORY_NODE, loValidationDoc);
		if (loCorrRuleEltList != null && !loCorrRuleEltList.isEmpty())
		{
			for (Element loMandatoryElt : loCorrRuleEltList)
			{
				Element loFirstMandatoryElt = null;
				boolean lbIsError = true;
				List<Element> loMandatoryChild = loMandatoryElt.getChildren();
				String lsValidateFieldName = loMandatoryElt.getAttributeValue("name");
				if (lsValidateFieldName != null && !lsValidateFieldName.trim().isEmpty()
						&& aoMandatoryList.contains(lsValidateFieldName))
				{
					if (loMandatoryChild != null)
					{
						loFirstMandatoryElt = loMandatoryChild.get(0);
						for (Element loMandatoryEltNode : loMandatoryChild)
						{
							String lsName = loMandatoryEltNode.getAttributeValue("name");
							if (aoHmRequest.containsKey(lsName) && aoHmRequest.get(lsName) != null)
							{
								lbIsError = false;
								break;
							}
						}
					}
					if (lbIsError && loFirstMandatoryElt != null)
					{
						lsErrorMessage = loMandatoryElt.getAttributeValue("errorMessage");
						ErrorMessages loErrMsg = new ErrorMessages();
						Element loInfo = XMLUtil.getElement(
								"//element[@name=\"" + loFirstMandatoryElt.getAttributeValue("name") + "\"]", aoXMLDom);
						loErrMsg.setSchemaname(loInfo.getAttributeValue("schemaname"));
						loErrMsg.setHtmlElementType(loInfo.getAttributeValue("elementtype"));
						loErrMsg.setParameterName(loInfo.getAttributeValue("name"));
						loErrMsg.setCustomErrorMessage(loInfo.getAttributeValue("id") + "#_#" + lsErrorMessage);
						loErrList.add(loErrMsg);
					}
				}
			}
		}
		LOG_OBJECT.Debug("public customValidation2 end");
		return loErrList;
	}

	/**
	 * Validates an attribute of validation xml for its value
	 * 
	 * @param asFieldValue - field value against the field to be validated
	 * @param aoRule - Element of validation rule
	 * @return boolean flag depicting whether an error has occured or not
	 * @throws ApplicationException
	 */
	public static Boolean invokeValidation(String asFieldValue, Element aoRule) throws ApplicationException
	{
		Boolean lbReturnedValue = false;
		try
		{
			if (ApplicationConstants.VALIDATION_METHOD_MAP.containsKey(aoRule.getName()))
			{
				String lsRuleValue = aoRule.getAttributeValue("value");
				String lsFunctionName = ApplicationConstants.VALIDATION_METHOD_MAP.get(aoRule.getName());
				Class loClassName = Class.forName(ApplicationConstants.VALIDATION_UTIL_CLASS);
				Method loMethod = loClassName
						.getMethod(lsFunctionName, lsRuleValue.getClass(), asFieldValue.getClass());
				Object loReturnedValue = loMethod.invoke(loClassName, lsRuleValue, asFieldValue);
				// method will return false if test is failed else true
				lbReturnedValue = (Boolean) loReturnedValue;
			}
		}
		catch (Exception aoEx)
		{
			throw new ApplicationException("Error occured while validating form " + aoEx);
		}
		return lbReturnedValue;
	}
}
