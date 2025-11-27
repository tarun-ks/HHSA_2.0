package com.nyc.hhs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.accenture.formtaglib.ErrorMessages;
import com.accenture.formtaglib.FileInformation;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaxonomyTree;

/**
 * This utility class provides the functionality like retrieving deployed form
 * location of Question label xml or Question, the configuration XML file path
 * of latest version of deployed form, actual path of the form when the form
 * name is given as input, form configuration path, transaction name to execute
 * services, setting the Taxonomy DOM in cache and prepare Dom object to
 * populate form created by form builder
 * 
 */

public class PropertyUtil
{
	/**
	 * This method gets Deployed Form Location.
	 * @param asFormName - form name
	 * @param asVersion - version
	 * @param abIsQuestionTemplate - question template flag
	 * @return - form path
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getDeployedFormLocation(String asFormName, String asVersion, boolean abIsQuestionTemplate)
			throws ApplicationException
	{
		StringBuffer loSbPath = new StringBuffer();
		Document loFormConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.FORM_CONFIG_FILE_PATH));
		String lsXPath = "//form[(@name=\"" + asFormName + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loFormConfiguration);
		lsXPath = "//version[(@no=\"" + asVersion + "\")]";
		Element loVersion = XMLUtil.getElement(lsXPath, loFormEle);
		loSbPath.append(loVersion.getAttributeValue(ApplicationConstants.FORM_DEPLOYED_LOCATION));
		if (abIsQuestionTemplate)
		{
			loSbPath.append("/");
			loSbPath.append(asFormName);
			loSbPath.append("_");
			loSbPath.append(asVersion);
			loSbPath.append("/data/");
			loSbPath.append(asFormName);
			loSbPath.append("_FormTable.xml");
		}
		else
		{
			loSbPath.append("/");
			loSbPath.append(asFormName);
			loSbPath.append("_");
			loSbPath.append(asVersion);
			loSbPath.append("/data/");
			loSbPath.append(asFormName);
			loSbPath.append(".xml");
		}
		return loSbPath.toString();
	}

	/**
	 * This method gets Deployed Form Location.
	 * @param asFormId - Form id
	 * @param abIsQuestionTemplate - question template flag
	 * @return lsPath - form path
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getDeployedFormLocation(String asFormId, boolean abIsQuestionTemplate)
			throws ApplicationException
	{
		Document loFormConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.FORM_CONFIG_FILE_PATH));
		String lsXPath = "//form[(@formid=\"" + asFormId + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loFormConfiguration);
		String lsFormName = loFormEle.getAttributeValue("name");
		String lsVersion = loFormEle.getAttributeValue(ApplicationConstants.LIVE_VERSION);
		String lsPath = getDeployedFormLocation(lsFormName, lsVersion, abIsQuestionTemplate);
		return lsPath;
	}

	/**
	 * This method gets Live Form Location.
	 * @param asFormId - Form id
	 * @return - form path
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getLiveFormLocation(String asFormId) throws ApplicationException
	{
		StringBuffer loSbPath = new StringBuffer();
		Document loFormConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.FORM_CONFIG_FILE_PATH));
		String lsXPath = "//form[(@formid=\"" + asFormId + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loFormConfiguration);
		String lsFormName = loFormEle.getAttributeValue("name");
		String lsVersion = loFormEle.getAttributeValue(ApplicationConstants.LIVE_VERSION);
		lsXPath = "//version[(@no=\"" + lsVersion + "\")]";
		Element loVersion = XMLUtil.getElement(lsXPath, loFormEle);
		loSbPath.append(loVersion.getAttributeValue(ApplicationConstants.FORM_DEPLOYED_LOCATION));
		loSbPath.append("/");
		loSbPath.append(lsFormName);
		loSbPath.append("_");
		loSbPath.append(lsVersion);
		loSbPath.append("/html/");
		loSbPath.append(lsFormName);
		loSbPath.append(".jsp");
		return loSbPath.toString();
	}

	/**
	 * This method gets Form Name Version Map.
	 * @param asFormId - Form id
	 * @return - loHMFormInformation
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static HashMap<String, Object> getFormNameVersionMap(String asFormId) throws ApplicationException
	{
		// String lsPath = "";
		Document loFormConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.FORM_CONFIG_FILE_PATH));
		String lsXPath = "//form[(@formid=\"" + asFormId + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loFormConfiguration);
		String lsFormName = loFormEle.getAttributeValue("name");
		String lsVersion = loFormEle.getAttributeValue(ApplicationConstants.LIVE_VERSION);

		HashMap<String, Object> loHMFormInformation = new HashMap<String, Object>();
		loHMFormInformation.put(ApplicationConstants.FORM_NAME, lsFormName);
		loHMFormInformation.put(ApplicationConstants.FORMVERSION_STRING, lsVersion);
		return loHMFormInformation;
	}

	/**
	 * This method gets Table Name.
	 * @param asFormId - form id
	 * @return lsTableName - table name
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getTableName(String asFormId) throws ApplicationException
	{
		Document loFormConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.FORM_CONFIG_FILE_PATH));
		String lsXPath = "//form[(@formid=\"" + asFormId + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loFormConfiguration);
		String lsTableName = loFormEle.getAttributeValue("tablename");
		return lsTableName;
	}

	/**
	 * This method gets File Path To Render.
	 * @param asFormName - form name
	 * @param asVersion - version
	 * @return - form path
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getFilePathToRender(String asFormName, String asVersion) throws ApplicationException
	{
		StringBuffer loSbPath = new StringBuffer();
		Document loFormConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.FORM_CONFIG_FILE_PATH));
		String lsXPath = "//form[(@name=\"" + asFormName + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loFormConfiguration);
		lsXPath = "//version[(@no=\"" + asVersion + "\")]";
		Element loVersion = XMLUtil.getElement(lsXPath, loFormEle);
		loSbPath.append(loVersion.getAttributeValue(ApplicationConstants.FORM_DEPLOYED_LOCATION));
		loSbPath.append("/");
		loSbPath.append(asFormName);
		loSbPath.append("_");
		loSbPath.append(asVersion);
		loSbPath.append("/html/");
		loSbPath.append(asFormName);
		loSbPath.append(".jsp");
		return loSbPath.toString();
	}

	/**
	 * This method gets Action Path.
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param asAction - action to be performed
	 * @return - lsPath
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getActionPath(String asSectionName, String asSubSectionName, String asAction)
			throws ApplicationException
	{
		String lsPath = "";
		Document loNavigationConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.NAVIGATION_FILE_PATH));
		String lsXPath = "//menu[(@name=\"" + asSectionName + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loNavigationConfiguration);
		lsXPath = "//menu[(@name=\"" + asSubSectionName + "\")]";
		Element loMenu = XMLUtil.getElement(lsXPath, loFormEle);
		lsPath = loMenu.getAttributeValue(asAction);
		return lsPath;
	}

	/**
	 * This method gets Service Name.
	 * @param asSectionName - Section Name
	 * @param asSubSectionName - Sub Section Name
	 * @param asAttributeName - Attribute Name
	 * @return - lsTransactionName
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getServiceName(String asSectionName, String asSubSectionName, String asAttributeName)
			throws ApplicationException
	{
		Document loNavigationConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.NAVIGATION_FILE_PATH));
		String lsXPath = "//menu[(@name=\"" + asSectionName + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loNavigationConfiguration);
		String lsTransactionName = null;
		if (loFormEle != null)
		{
			List<Element> loAllChildElement = loFormEle.getChildren();
			if (loAllChildElement != null)
			{
				Iterator<Element> loItr = loAllChildElement.iterator();
				while (loItr.hasNext())
				{
					Element loEle = loItr.next();
					String lsMenuName = loEle.getAttributeValue("name");
					if (lsMenuName.equalsIgnoreCase(asSubSectionName))
					{
						lsTransactionName = loEle.getAttributeValue(asAttributeName);
						break;
					}
				}
			}
		}
		else
		{
			throw new ApplicationException("Unable to find element for xpath: " + lsXPath);
		}

		return lsTransactionName;
	}

	/**
	 * This method gets Default Sub Section.
	 * @param asSectionName - Section Name
	 * @return - lsDefaultMenu
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static String getDefaultSubSection(String asSectionName) throws ApplicationException
	{
		Document loNavigationConfiguration = XMLUtil.getDomObj(PropertyUtil.class
				.getResourceAsStream(ApplicationConstants.NAVIGATION_FILE_PATH));
		String lsXPath = "//menu[(@name=\"" + asSectionName + "\")]";
		Element loFormEle = XMLUtil.getElement(lsXPath, loNavigationConfiguration);
		String lsDefaultMenu = null;
		if (loFormEle != null)
		{
			lsDefaultMenu = loFormEle.getAttributeValue("defaultmenu");
		}
		else
		{
			throw new ApplicationException("Unable to find element for xpath: " + lsXPath);
		}
		return lsDefaultMenu;
	}

	/**
	 * This method sets Taxonomy In Cache.
	 * @param aoCacheManager - ICacheManager
	 * @param asKey - key for taXONOMY
	 * @throws ApplicationException - throws ApplicationException
	 */
	public void setTaxonomyInCache(ICacheManager aoCacheManager, String asKey) throws ApplicationException
	{

		try
		{

			if (asKey == null)
			{
				asKey = ApplicationConstants.TAXONOMY_ELEMENT;
			}
			Channel loChannelObj = new Channel();
			// Fetch Taxonomy data from DB
			TransactionManager.executeTransaction(loChannelObj, ApplicationConstants.RETRIEVE_FROM_TAXONOMY);
			List<TaxonomyTree> loTaxonomyList = (List<TaxonomyTree>) loChannelObj.getData("loTaxonomyList");

			// Instantiating TaxonomyDOM to generate DOM Tree for Taxonomy
			TaxonomyDOMUtil loTaxonomyDOM = new TaxonomyDOMUtil();
			Document loTaxonomyDom = loTaxonomyDOM.createTaxonomyDOMObj(loTaxonomyList);
			// Caching Taxonomy DOM
			aoCacheManager.putCacheObject(asKey, loTaxonomyDom);

		}
		catch (ApplicationException aoError)
		{
			throw new ApplicationException("Error occured while creating Taxonomy DOM Object Cache", aoError);
		}
	}

	/**
	 * tHIS METHOD converts Request Map To Dom.
	 * @param aDFormtemplatePath - Form template Path
	 * @param aoParameters - map containing required values
	 * @return - loDomObj
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static Document converRequestMapToDom(String aoFormtemplatePath, HashMap<String, Object> aoParameters)
			throws ApplicationException
	{
		Document loDFormtemplate = XMLUtil.getDomObj(aoFormtemplatePath);
		List<ErrorMessages> loLError = new ArrayList<ErrorMessages>();
		Element loParentElement = loDFormtemplate.getRootElement();
		List<Element> loAllDivs = loParentElement.getChildren();
		Iterator<Element> loItrDiv = loAllDivs.iterator();
		while (loItrDiv.hasNext())
		{// iterate all divs
			Element loEle = loItrDiv.next();
			String lsElementType = loEle.getAttributeValue("elementtype");
			if (null != lsElementType && lsElementType.equalsIgnoreCase("property"))
			{
				continue;
			}
			String lsRowId = loEle.getAttributeValue("rowid");
			String lsParameterName = loEle.getAttributeValue("name");
			String lsSchemaName = loEle.getAttributeValue("schemaname");
			String[] loIdsArr = null;
			int liRowCount = 0;
			String lsRowParameterName = lsParameterName;
			do
			{
				ErrorMessages loErrMsg = new ErrorMessages();
				loErrMsg.setSchemaname(lsSchemaName);
				loErrMsg.setHtmlElementType(lsElementType);
				loErrMsg.setParameterName(lsParameterName);
				boolean lbIsFieldReadOnlyAsBR = filedIsReadOnly(lsParameterName);
				if (lbIsFieldReadOnlyAsBR)
				{
					loErrMsg.setMakeFieldReadOnly("true");
				}
				else
				{
					loErrMsg.setMakeFieldReadOnly("false");
				}
				loLError.add(loErrMsg);

				if (null == loIdsArr && null != lsRowId && lsRowId.startsWith("lineElt")
						&& aoParameters.containsKey(lsRowId))
				{
					String lsRowIds = (String) aoParameters.get(lsRowId);
					loIdsArr = lsRowIds.split(",");
					if (null != loIdsArr && loIdsArr.length > 0)
					{
						lsParameterName = "add$" + lsRowParameterName + "$lin3$" + loIdsArr[liRowCount];
					}
				}
				else if (null != loIdsArr && liRowCount < loIdsArr.length)
				{
					lsParameterName = "add$" + lsRowParameterName + "$lin3$" + loIdsArr[liRowCount];
				}
				else
				{
					liRowCount = 0;
					loIdsArr = null;
				}
			}
			while (loIdsArr != null && loIdsArr.length >= liRowCount);
		}
		ErrorMessages[] loAllErrorMsgArray = loLError.toArray(new ErrorMessages[loLError.size()]);
		Element loRootElement = new Element("formtemplate");
		Document loDomObj = new Document(loRootElement);
		List<FileInformation> loFilesToUpload = new ArrayList<FileInformation>();
		Element loEle = loDomObj.getRootElement();
		boolean lbErrorOnPage = false;
		for (ErrorMessages loError : loAllErrorMsgArray)
		{
			setElementData(aoParameters, loFilesToUpload, loEle, lbErrorOnPage, loError);
		}
		return loDomObj;
	}

	/**
	 * @param aoParameters
	 * @param loFilesToUpload
	 * @param loe
	 * @param lbErrorOnPage
	 * @param loError
	 */
	private static void setElementData(HashMap<String, Object> aoParameters, List<FileInformation> aoFilesToUpload,
			Element aoe, boolean abErrorOnPage, ErrorMessages aoError)
	{
		Element loElementtoWrite = new Element(aoError.getSchemaname());
		loElementtoWrite.setAttribute("name", aoError.getParameterName());
		loElementtoWrite.setAttribute("makeFieldReadOnly", aoError.getMakeFieldReadOnly());
		String lsSubmitedValue = null;
		FileInformation loFileInf = null;
		if (null != aoError.getHtmlElementType() && aoError.getHtmlElementType().equalsIgnoreCase("file"))
		{
			try
			{
				loFileInf = (FileInformation) aoParameters.get(aoError.getSchemaname());
				aoFilesToUpload.add(loFileInf);
			}
			catch (Exception aoEx)
			{
				lsSubmitedValue = (String) aoParameters.get(aoError.getSchemaname());
			}
		}
		else
		{
			lsSubmitedValue = (String) aoParameters.get(aoError.getSchemaname());
		}

		if (null != lsSubmitedValue && !lsSubmitedValue.isEmpty())
		{
			loElementtoWrite.setAttribute("value", lsSubmitedValue);
		}
		else if (!abErrorOnPage && null != loFileInf && null != aoError.getHtmlElementType()
				&& aoError.getHtmlElementType().equalsIgnoreCase("file") && loFileInf.getFileName() != null)
		{
			loElementtoWrite.setAttribute("value", loFileInf.getFileName());
		}
		else
		{
			loElementtoWrite.setAttribute("value", "");
		}

		aoe.addContent(loElementtoWrite);
	}

	/**
	 * This method checks for the read only fields.
	 * @param asFieldName - field name
	 * @return - read only flag
	 */
	public static boolean filedIsReadOnly(String asFieldName)
	{

		return false;
	}
}
