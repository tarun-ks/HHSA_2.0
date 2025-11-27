package com.hhs.formbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdom.Document;
import org.jdom.Element;

import com.accenture.constants.ObjectModelConstants;
import com.accenture.factory.Validation;
import com.accenture.formtaglib.ErrorMessages;
import com.accenture.formtaglib.FileInformation;
import com.accenture.formtaglib.TLDUtill;
import com.accenture.util.FormBuilderUtill;
import com.accenture.util.LanguageRegex;
import com.accenture.util.XmlUtil;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.util.XMLUtil;

public class FormBuilderValidation implements Validation
{
	private static final LogInfo LOG_OBJECT = new LogInfo(FormBuilderValidation.class);

	/**
	 * This method validate the form's DOM object and returns the HashMap with
	 * error codes.
	 * 
	 * @param aoDFormtemplate - jdom form template
	 * @param aoDQuestiontemplate - jdom question template
	 * @param parameters - hashmap of form parameter, values
	 * @param asUserRoles - user role against which data will be validated
	 * @param asFormName - form name to be validated
	 * @param asFormVersion - form version to be validated
	 * @param asUserRoleForDocument - String showing the role of user
	 * @return - Array or error message bean
	 * @throws ApplicationException
	 */
	public ErrorMessages[] validate(Document aoDFormtemplate, Document aoDQuestiiontemplate,
			HashMap<String, Object> aoHParameters, String asUserRoles, String asFormName, String asFormVersion,
			String asUserRoleForDocument) throws ApplicationException
	{
		LOG_OBJECT.Debug("public validate start");
		List<ErrorMessages> loLError = new ArrayList<ErrorMessages>();
		String lsLanguageCode = (String) aoHParameters.get(ObjectModelConstants.VARIABLE_PREFIX + "languageCode");
		String lsRegexpForValidation = LanguageRegex.getRegExp(lsLanguageCode);
		boolean lbByPassValidation = true;
		List<String> loMandatoryList = getMandatoryObjects(aoHParameters, asFormName, asFormVersion);
		Iterator<String> loParameterItr = aoHParameters.keySet().iterator();
		lbByPassValidation = validateWhileLoop(aoDFormtemplate, aoHParameters, asUserRoles, asFormName, asFormVersion,
				asUserRoleForDocument, loLError, lsRegexpForValidation, lbByPassValidation, loMandatoryList,
				loParameterItr);
		LOG_OBJECT.Info("validaeWhileLoop : " + String.valueOf(lbByPassValidation));
		List<ErrorMessages> loErrMsg = CustomValidation.customValidation(aoDFormtemplate, aoHParameters, asFormName,
				asFormVersion, loMandatoryList);
		addErrorMsg(loLError, loErrMsg);
		ErrorMessages[] loAllErrorMsgArray = loLError.toArray(new ErrorMessages[loLError.size()]);
		LOG_OBJECT.Debug("public validate end");
		return loAllErrorMsgArray;
	}

	/**
	 * This method validate the form's DOM object validate's Method and returns
	 * the boolean with
	 * 
	 * @param aoDFormtemplate
	 * @param aoHParameters
	 * @param asUserRoles
	 * @param asFormName
	 * @param asFormVersion
	 * @param asUserRoleForDocument
	 * @param loLError
	 * @param lsRegexpForValidation
	 * @param lbByPassValidation
	 * @param loMandatoryList
	 * @param loParameterItr
	 * @return
	 * @throws ApplicationException
	 */
	private boolean validateWhileLoop(Document aoDFormtemplate, HashMap<String, Object> aoHParameters,
			String asUserRoles, String asFormName, String asFormVersion, String asUserRoleForDocument,
			List<ErrorMessages> loLError, String lsRegexpForValidation, boolean lbByPassValidation,
			List<String> loMandatoryList, Iterator<String> loParameterItr) throws ApplicationException
	{
		while (loParameterItr.hasNext())
		{
			String lsParameterName = loParameterItr.next();// iterate all divs
			String lsXPath = "//element[(@name='" + lsParameterName + "')]";
			Element loEle = XmlUtil.getElement(lsXPath, aoDFormtemplate);
			if (loEle != null)
			{
				String lsElementType = loEle.getAttributeValue("elementtype");
				String lsRowId = loEle.getAttributeValue("rowid");
				String lsSubmitedValue = null;
				String lsMandatory = loEle.getAttributeValue("mandatory");
				String lsSchemaName = loEle.getAttributeValue("schemaname");
				lsMandatory = checkForElementType(asUserRoleForDocument, loMandatoryList, lsParameterName,
						lsElementType, lsMandatory, lsSchemaName);
				if (null != lsElementType
						&& lsElementType.equalsIgnoreCase("property")
						&& (null != loEle.getAttributeValue("defaultpermission") && !loEle.getAttributeValue(
								"defaultpermission").isEmpty()))
				{
					String lsDefaultPermission = loEle.getAttributeValue("defaultpermission");
					if (ObjectModelConstants.PERMISSION_FOR_EDIT.equalsIgnoreCase(lsDefaultPermission))
					{
						lbByPassValidation = false;
					}
					else
					{
						lbByPassValidation = false;
					}
					// temporary giving edit permission as default permission
					continue;
				}
				String lsEleId = loEle.getAttributeValue("id");
				String lsCanContainsValue = loEle.getAttributeValue("typeofvalue");
				String lsErrorMsg = loEle.getAttributeValue("errorcode1");
				FileInformation loFileInf = null;
				String lsAction = null;
				if (asUserRoles != null && !asUserRoles.trim().isEmpty())
				{
					lbByPassValidation = checkUserRole(asUserRoles, lbByPassValidation, loEle, lsAction);
				}
				String[] loIdsArr = null;
				int liRowCount = 0;
				String lsRowParameterName = lsParameterName;
				String lsEleIdForRow = lsEleId;
				do
				{
					ErrorMessages loErrMsg = setErrMsg(lsParameterName, lsElementType, lsSchemaName);
					if (null != lsElementType && lsElementType.equalsIgnoreCase("file"))
					{
						try
						{
							loFileInf = (FileInformation) aoHParameters.get(lsParameterName);
						}
						catch (Exception aoExp)
						{
							lsSubmitedValue = (String) aoHParameters.get(lsParameterName);
						}
					}
					else
					{
						lsSubmitedValue = (String) aoHParameters.get(lsParameterName);
					}
					boolean lbIsErrorMsgSet = false;
					checkByPassValidation(asFormName, asFormVersion, lsRegexpForValidation, lbByPassValidation,
							lsParameterName, loEle, lsElementType, lsSubmitedValue, lsMandatory, lsCanContainsValue,
							lsErrorMsg, loFileInf, lsEleIdForRow, loErrMsg, lbIsErrorMsgSet);
					loLError.add(loErrMsg);
					if (null == loIdsArr && null != lsRowId && lsRowId.startsWith("lineElt")
							&& aoHParameters.containsKey(lsRowId))
					{
						String lsRowIds = (String) aoHParameters.get(lsRowId);
						loIdsArr = lsRowIds.split(",");
						if (null != loIdsArr && loIdsArr.length > 0)
						{
							lsParameterName = "add$" + lsRowParameterName + "$lin3$" + loIdsArr[liRowCount];
							lsEleIdForRow = lsEleId + loIdsArr[liRowCount++];
						}
					}
					else if (null != loIdsArr && liRowCount < loIdsArr.length)
					{
						lsParameterName = "add$" + lsRowParameterName + "$lin3$" + loIdsArr[liRowCount];
						lsEleIdForRow = lsEleId + loIdsArr[liRowCount++];
					}
					else
					{
						liRowCount = 0;
						loIdsArr = null;
					}
				}
				while (loIdsArr != null && loIdsArr.length >= liRowCount);
			}
		}
		return lbByPassValidation;
	}

	/**
	 * This method set ErrorMessages
	 * 
	 * @param lsParameterName
	 * @param lsElementType
	 * @param lsSchemaName
	 * @return loErrMsg
	 */
	private ErrorMessages setErrMsg(String lsParameterName, String lsElementType, String lsSchemaName)
	{
		ErrorMessages loErrMsg = new ErrorMessages();
		loErrMsg.setSchemaname(lsSchemaName);
		loErrMsg.setHtmlElementType(lsElementType);
		loErrMsg.setParameterName(lsParameterName);
		return loErrMsg;
	}

	/**
	 * This Method check for Element type
	 * @param asUserRoleForDocument
	 * @param loMandatoryList
	 * @param lsParameterName
	 * @param lsElementType
	 * @param lsMandatory
	 * @param lsSchemaName
	 * @return
	 */
	private String checkForElementType(String asUserRoleForDocument, List<String> loMandatoryList,
			String lsParameterName, String lsElementType, String lsMandatory, String lsSchemaName)
	{
		if (null != lsElementType && (!lsElementType.equalsIgnoreCase("checkbox"))
				&& loMandatoryList.contains(lsParameterName) && lsMandatory.equalsIgnoreCase("false"))
		{
			lsMandatory = "true";
		}
		if (!asUserRoleForDocument.equalsIgnoreCase("first_time")
				&& ApplicationConstants.BASIC_NO_UPDATE.contains(lsSchemaName))
		{
			lsMandatory = "false";
		}
		return lsMandatory;
	}

	/**
	 * @param asFormName
	 * @param asFormVersion
	 * @param lsRegexpForValidation
	 * @param lbByPassValidation
	 * @param lsParameterName
	 * @param loEle
	 * @param lsElementType
	 * @param lsSubmitedValue
	 * @param lsMandatory
	 * @param lsCanContainsValue
	 * @param lsErrorMsg
	 * @param loFileInf
	 * @param lsEleIdForRow
	 * @param loErrMsg
	 * @param isErrorMsgSet
	 * @throws ApplicationException
	 */
	private void checkByPassValidation(String asFormName, String asFormVersion, String lsRegexpForValidation,
			boolean lbByPassValidation, String lsParameterName, Element loEle, String lsElementType,
			String lsSubmitedValue, String lsMandatory, String lsCanContainsValue, String lsErrorMsg,
			FileInformation loFileInf, String lsEleIdForRow, ErrorMessages loErrMsg, boolean isErrorMsgSet)
			throws ApplicationException
	{
		if (!lbByPassValidation
				|| (null != loFileInf && null != lsElementType && lsElementType.equalsIgnoreCase("file") && null != loFileInf
						.getFileName()))
		{

			if (null != lsMandatory && lsMandatory.equalsIgnoreCase("true"))
			{
				isErrorMsgSet = setMandatoryCheck(lsParameterName, lsElementType, lsSubmitedValue, lsErrorMsg,
						loFileInf, lsEleIdForRow, loErrMsg, isErrorMsgSet);
			}
			if (!isErrorMsgSet)
			{
				setErrorMsgSet(asFormName, asFormVersion, lsRegexpForValidation, lsParameterName, loEle, lsElementType,
						lsSubmitedValue, lsCanContainsValue, lsEleIdForRow, loErrMsg);
			}
		}
	}

	/**
	 * This method add ErrorMsg
	 * 
	 * @param loLError
	 * @param loErrMsg
	 */
	private void addErrorMsg(List<ErrorMessages> loLError, List<ErrorMessages> loErrMsg)
	{
		if (loErrMsg != null && !loErrMsg.isEmpty())
		{
			for (ErrorMessages loErr : loErrMsg)
			{
				loLError.add(loErr);
			}
		}
	}

	/**
	 * This Method validate for mandatory check
	 * @param lsParameterName
	 * @param lsElementType
	 * @param lsSubmitedValue
	 * @param lsErrorMsg
	 * @param loFileInf
	 * @param lsEleIdForRow
	 * @param loErrMsg
	 * @param isErrorMsgSet
	 * @return
	 */
	private boolean setMandatoryCheck(String lsParameterName, String lsElementType, String lsSubmitedValue,
			String lsErrorMsg, FileInformation loFileInf, String lsEleIdForRow, ErrorMessages loErrMsg,
			boolean isErrorMsgSet)
	{
		if (null != lsElementType && !lsElementType.equalsIgnoreCase("file")
				&& (lsSubmitedValue == null || lsSubmitedValue.trim().isEmpty()))
		{
			loErrMsg.setParameterName(lsParameterName);
			loErrMsg.setCustomErrorMessage(lsEleIdForRow + "#_#" + lsErrorMsg);
			isErrorMsgSet = true;
		}
		else if (null != loFileInf && null != lsElementType && lsElementType.equalsIgnoreCase("file")
				&& (null == loFileInf.getFileName() || loFileInf.getFileName().trim().isEmpty()))
		{
			loErrMsg.setCustomErrorMessage(lsEleIdForRow + "#_#" + lsErrorMsg);
			isErrorMsgSet = true;
		}
		return isErrorMsgSet;
	}

	/**
	 * This Method set for ErrorMsgSet
	 * 
	 * @param asFormName
	 * @param asFormVersion
	 * @param lsRegexpForValidation
	 * @param lsParameterName
	 * @param loEle
	 * @param lsElementType
	 * @param lsSubmitedValue
	 * @param lsCanContainsValue
	 * @param lsEleIdForRow
	 * @param loErrMsg
	 * @throws ApplicationException
	 */
	private void setErrorMsgSet(String asFormName, String asFormVersion, String lsRegexpForValidation,
			String lsParameterName, Element loEle, String lsElementType, String lsSubmitedValue,
			String lsCanContainsValue, String lsEleIdForRow, ErrorMessages loErrMsg) throws ApplicationException
	{
		if (null != lsSubmitedValue && !lsSubmitedValue.trim().isEmpty() && null != lsElementType
				&& lsElementType.equalsIgnoreCase("text"))
		{

			if (null != lsCanContainsValue)
			{
				boolean lbIsErrorFails = isValidationFailsForTextElement(lsCanContainsValue, lsSubmitedValue,
						lsRegexpForValidation);
				if (lbIsErrorFails)
				{
					String lsError = loEle.getAttributeValue("errorcode2");
					loErrMsg.setCustomErrorMessage(lsEleIdForRow + "#_#" + lsError);
				}
				else
				{
					String lsError = CustomValidation.customValidation(lsParameterName, lsSubmitedValue, asFormName,
							asFormVersion);
					if (lsError != null && lsError.length() > 0)
					{
						loErrMsg.setCustomErrorMessage(lsEleIdForRow + "#_#" + lsError);
					}
					// invoke custom validation
				}
			}
		}
		else if (null != lsSubmitedValue && !lsSubmitedValue.trim().isEmpty() && null != lsElementType
				&& lsElementType.equalsIgnoreCase("textarea"))
		{
			String lsError = CustomValidation.customValidation(lsParameterName, lsSubmitedValue, asFormName,
					asFormVersion);
			if (lsError != null && lsError.length() > 0)
			{
				loErrMsg.setCustomErrorMessage(lsEleIdForRow + "#_#" + lsError);
			}
		}
	}

	/**
	 * This method validate for UserRole
	 * 
	 * @param asUserRoles
	 * @param lbByPassValidation
	 * @param loEle
	 * @param lsAction
	 * @return
	 */
	private boolean checkUserRole(String asUserRoles, boolean lbByPassValidation, Element loEle, String lsAction)
	{
		String[] loUserRoles;
		String lsViewOnly = loEle.getAttributeValue("viewrole");
		String lsForEdit = loEle.getAttributeValue("editrole");
		String lsNone = loEle.getAttributeValue("nonerole");
		loUserRoles = asUserRoles.split(",");

		for (String lsRole : loUserRoles)
		{

			if (lsForEdit != null && TLDUtill.isContainsRole(lsForEdit, lsRole))
			{
				lbByPassValidation = false;
				lsAction = ObjectModelConstants.PERMISSION_FOR_EDIT;
				break;
			}
			else if (lsViewOnly != null && TLDUtill.isContainsRole(lsViewOnly, lsRole))
			{
				lbByPassValidation = true;
				lsAction = ObjectModelConstants.PERMISSION_FOR_VIEW;
			}
			else if (lsNone != null && TLDUtill.isContainsRole(lsNone, lsRole) && lsAction == null)
			{
				lbByPassValidation = true;
				lsAction = ObjectModelConstants.PERMISSION_FOR_NONE;
			}
		}
		return lbByPassValidation;
	}

	/**
	 * Gets the list of mandatory objects
	 * 
	 * @param aoHParameters - hashmap of data
	 * @param asFormName - form name of the form to be validated
	 * @param asFormVersion - form version of the form to be validated
	 * @return list of mandatory element names
	 * @throws ApplicationException
	 */
	private List<String> getMandatoryObjects(HashMap<String, Object> aoHParameters, String asFormName,
			String asFormVersion) throws ApplicationException
	{
		LOG_OBJECT.Debug("private getMandatoryObjects start");
		Document loDependencyDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.DEPENDENCY_CACHE_REF);
		List<String> loMandatoryList = new ArrayList<String>();
		List<Element> loEltList = XMLUtil.getElementList("//" + ApplicationConstants.DEPENDENCY_RULE_NODE
				+ "[@formname=\"" + asFormName + "\" and @version=\"" + asFormVersion + "\"]/"
				+ ApplicationConstants.ELEMENT_NODE, loDependencyDoc);
		for (Element loElt : loEltList)
		{
			String lsElementName = loElt.getAttributeValue("name");
			List<Element> loDepEltList = loElt.getChildren(ApplicationConstants.DEPENDENT_NODE);
			if (!loDepEltList.isEmpty())
			{
				boolean lbDependencyPass = true;
				for (Element loDepElt : loDepEltList)
				{
					List<Element> loDepOnEltList = loDepElt.getChildren(ApplicationConstants.DEPENDENT_ON_NODE);
					for (Element loDepOnElt : loDepOnEltList)
					{
						lbDependencyPass = true;
						String lsDependentOn = loDepOnElt.getAttributeValue("on");
						String lsDependentValue = loDepOnElt.getAttributeValue("value");
						Object loSubmittedValue = aoHParameters.get(lsDependentOn);
						if (!((loSubmittedValue != null && ((String) loSubmittedValue)
								.equalsIgnoreCase(lsDependentValue)) || (loSubmittedValue == null && lsDependentValue
								.isEmpty())))
						{
							lbDependencyPass = false;
							break;
						}
					}
					if (lbDependencyPass)
					{
						break;
					}
				}
				if (lbDependencyPass)
				{
					loMandatoryList.add(lsElementName);
				}
				else
				{
					aoHParameters.remove(lsElementName);
				}
			}
			else
			{
				loMandatoryList.add(lsElementName);
			}
		}
		for (String lsName : loMandatoryList)
		{
			if (aoHParameters.get(lsName) == null)
			{
				aoHParameters.put(lsName, null);
			}
		}
		LOG_OBJECT.Debug("private getMandatoryObjects end");
		return loMandatoryList;
	}

	/**
	 * This method validate the form's DOM object and returns the HashMap with
	 * error codes.
	 * 
	 * @param aoDFormtemplate - jdom form template
	 * @param aoDQuestiontemplate - jdom question template
	 * @param aoReq - Request from which form data will be fetched
	 * @param asFormName - form name to be validated
	 * @param asFormVersion - form version to be validated
	 * @param asXMLPath - path of folder containing form template and question
	 *            template
	 * @param asUserRoleForDocument - String showing the role of user
	 * @return - Array or error message bean
	 * @throws ApplicationException
	 */
	public ErrorMessages[] validate(Document aoDFormtemplate, Document aoDQuestiontemplate, HttpServletRequest aoReq,
			String asFormName, String asFormVersion, String asXMLPath, String asUserRoleForDocument)
			throws ApplicationException
	{
		boolean lbIsMultipart = ServletFileUpload.isMultipartContent(aoReq);
		ErrorMessages[] loAllErrorMsgArray = null;
		String lsRoleKeyName = TLDUtill.getSessionVariableName("roles");
		HttpSession loSession = aoReq.getSession();
		String lsUserRoles = (String) TLDUtill.getAttributeValue(aoReq, loSession, lsRoleKeyName);
		if (lbIsMultipart)
		{
			HashMap<String, Object> loMapReq = FormBuilderUtill.getRequestMapIfFormContainsAnyFileTypeElement(aoReq);
			loAllErrorMsgArray = validate(aoDFormtemplate, aoDQuestiontemplate, loMapReq, lsUserRoles, asFormName,
					asFormVersion, asUserRoleForDocument);
		}
		else
		{
			HashMap<String, Object> loMapReq = FormBuilderUtill.getRequestMap(aoReq);
			loAllErrorMsgArray = validate(aoDFormtemplate, aoDQuestiontemplate, loMapReq, lsUserRoles, asFormName,
					asFormVersion, asUserRoleForDocument);
		}
		return loAllErrorMsgArray;
	}

	/**
	 * Checks if validation fails for text element(language dependent)
	 * 
	 * @param asCanContainsValue - type of value
	 * @param asValue - value to be checked
	 * @param asRegexpForValidation - reg ex of language
	 * @return flag if validation passed or failed
	 */
	public boolean isValidationFailsForTextElement(String asCanContainsValue, String asValue,
			String asRegexpForValidation)
	{
		if (asCanContainsValue.equalsIgnoreCase("i"))
		{
			if (!FormBuilderUtill.isNumeric(asValue))
			{
				return true;
			}
		}
		else if (asCanContainsValue.equalsIgnoreCase("s"))
		{
			if (!FormBuilderUtill.isInputValueAsPerFormLanguage(asValue, asRegexpForValidation))
			{
				return true;
			}
		}
		else if (asCanContainsValue.equalsIgnoreCase("em") && !FormBuilderUtill.isValidEmail(asValue))
		{
			return true;
		}
		return false;
	}
}
