package com.nyc.hhs.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.portlet.ActionRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.Population;
import com.nyc.hhs.model.PrintContentBean;
import com.nyc.hhs.model.ServiceQuestions;
import com.nyc.hhs.model.ServiceSettingBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.TaxonomyTree;

/**
 * This class has utility function for service and business application which
 * includes fetching the doc types, taxonomy child elements, parent service
 * name, creating taxonomy tree class object from given taxonomy DOM, converting
 * status and section status, sub-section status corresponding to non started,
 * draft, complete, creating printable view... etc
 * 
 */

public class BusinessApplicationUtil
{
	private static final LogInfo LOG_OBJECT = new LogInfo(BusinessApplicationUtil.class);

	/**
	 * Get all doc type and category corresponding to form
	 * 
	 * @param aoQuestion - jdom of the question template
	 * @param aoRuleDoc - jdom of the Rule template
	 * @param aoParameterMap - Hashmap of data
	 * @param asFormName - the form name of the current form
	 * @param asFormVersion - the form version of the current form
	 * @param abSelectStatus - boolean flag if cfo to be checked
	 * @param asCorpStr - Corp Str value for filing form
	 * @return Map of doctype and doccategory
	 * @throws ApplicationException
	 */
	public static Map<String, String> getAllDocTypes(Document aoQuestion, Document aoRuleDoc,
			HashMap<String, String> aoParameterMap, String asFormName, String asFormVersion, Boolean abSelectStatus,
			String asCorpStr) throws ApplicationException
	{
		LOG_OBJECT.Debug("public getAllDocTypes start");
		Map<String, String> loDocMap = new TreeMap<String, String>();
		Element loTriggerElement = XMLUtil.getElement(ApplicationConstants.FORM_RULES_NODE + "/"
				+ ApplicationConstants.FORM_RULE_NODE + "[@formname=\"" + asFormName + "\" and @version=\""
				+ asFormVersion + "\"]/" + ApplicationConstants.TRIGGER_NODE, aoRuleDoc);
		String lsTriggerIds = loTriggerElement.getAttributeValue("ids");
		if (lsTriggerIds != null)
		{
			String loTriggerArray[] = lsTriggerIds.split(",");
			String lsTriggerId;
			for (int liCounter = 0; liCounter < loTriggerArray.length; liCounter++)
			{
				lsTriggerId = loTriggerArray[liCounter];
				List<Element> loRuleEltList = XMLUtil.getElementList(ApplicationConstants.FORM_RULES_NODE + "/"
						+ ApplicationConstants.FORM_RULE_NODE + "[@formname=\"" + asFormName + "\" and @version=\""
						+ asFormVersion + "\"]/" + ApplicationConstants.RULE_NODE + "["
						+ ApplicationConstants.QUESTION_NODE + "[@quesid=\"" + lsTriggerId + "\"]]", aoRuleDoc);
				getDocTypes(aoQuestion, aoRuleDoc, aoParameterMap, asFormName, asFormVersion, abSelectStatus, loDocMap,
						loRuleEltList, asCorpStr);
			}
		}
		LOG_OBJECT.Debug("public getAllDocTypes end");
		return loDocMap;
	}

	/**
	 * Get all doc type and category corresponding to form
	 * 
	 * @param aoQuestion - jdom of the question template
	 * @param aoRuleDoc - jdom of the Rule template
	 * @param aoParameterMap - Hashmap of data
	 * @param asFormName - the form name of the current form
	 * @param asFormVersion - the form version of the current form
	 * @param abSelectStatus - boolean flag if cfo to be checked
	 * @param aoDocMap - Map of doctype and doccategory
	 * @param aoRuleEltList - List of rules corresponding to form
	 * @param asCorpStr - Corp Str value for filing form
	 * @throws ApplicationException
	 */
	private static void getDocTypes(Document aoQuestion, Document aoRuleDoc, HashMap<String, String> aoParameterMap,
			String asFormName, String asFormVersion, Boolean abSelectStatus, Map<String, String> aoDocMap,
			List<Element> aoRuleEltList, String asCorpStr) throws ApplicationException
	{
		boolean lbRuleSuccessFull = false;
		String lsRuleId = "";
		for (Element loRuleElt : aoRuleEltList)
		{
			List loQuesList = loRuleElt.getChildren(ApplicationConstants.QUESTION_NODE);
			Iterator loQuesIterator = loQuesList.iterator();
			while (loQuesIterator.hasNext())
			{
				Element loQuestionElt = (Element) loQuesIterator.next();
				String lsQuestionId = loQuestionElt.getAttributeValue("quesid");
				String lsValue = loQuestionElt.getAttributeValue("value");
				Element loQuestionEltTemplate = XMLUtil.getElement("//" + ApplicationConstants.QUESTION_NODE
						+ "[@quesid=\"" + lsQuestionId + "\"]", aoQuestion);
				boolean lbValueNotFound = false;
				if (loQuestionEltTemplate != null)
				{
					lbValueNotFound = checkRuleFromXML(aoParameterMap, lsValue, loQuestionEltTemplate);
				}
				else if (lsQuestionId.equalsIgnoreCase("all"))
				{
					lbValueNotFound = false;
				}
				else if (lsQuestionId.equalsIgnoreCase("cfo"))
				{
					lbValueNotFound = !abSelectStatus;
				}
				else if (lsQuestionId.equalsIgnoreCase("corpStr") && !asCorpStr.equals(lsValue))
				{
					lbValueNotFound = true;
				}
				if (!lbValueNotFound)
				{
					lbRuleSuccessFull = true;
					lsRuleId = loRuleElt.getAttributeValue("ruleid");
				}
				else
				{
					lbRuleSuccessFull = false;
					break;
				}
			}
			if (lbRuleSuccessFull)
			{
				getDocTypeCatForRule(aoRuleDoc, asFormName, asFormVersion, aoDocMap, lsRuleId);
			}
		}
	}

	/**
	 * Check if rule matches the value
	 * 
	 * @param aoParameterMap - Hashmap of data
	 * @param asValue - value across which rule to be validated
	 * @param aoQuestionEltTemplate - jdom Element of form question element
	 * @return boolean flag showing if value is found or not
	 */
	private static boolean checkRuleFromXML(HashMap<String, String> aoParameterMap, String asValue,
			Element aoQuestionEltTemplate)
	{
		boolean lbValueNotFound = false;
		String lsColumnNames = aoQuestionEltTemplate.getAttributeValue("columnname");
		for (String lsColumnName : lsColumnNames.split(","))
		{
			if (null == aoParameterMap.get(lsColumnName)
					|| (!asValue.isEmpty() && null != aoParameterMap.get(lsColumnName) && (!asValue
							.equals((String) aoParameterMap.get(lsColumnName)))))
			{
				lbValueNotFound = true;
				break;
			}
			else if (asValue.isEmpty())
			{
				if (!(((String) aoParameterMap.get(lsColumnName)).length() > 0))
				{
					lbValueNotFound = true;
					break;
				}
			}
		}
		return lbValueNotFound;
	}

	/**
	 * Gets doc type and category for a rule id
	 * 
	 * @param aoRuleDoc - jdom of the Rule template
	 * @param asFormName - the form name of the current form
	 * @param asFormVersion - the form version of the current form
	 * @param aoDocMap - Map of doctype and doccategory
	 * @param asRuleId - Rule id for which doctype and category needs to be
	 *            fetched
	 * @throws ApplicationException
	 */

	private static void getDocTypeCatForRule(Document aoRuleDoc, String asFormName, String asFormVersion,
			Map<String, String> aoDocMap, String asRuleId) throws ApplicationException
	{
		if (!asRuleId.isEmpty())
		{
			List<Element> loDocEltList = XMLUtil.getElementList("//" + ApplicationConstants.FORM_RULE_NODE
					+ "[@formname=\"" + asFormName + "\" and @version=\"" + asFormVersion + "\"]" + "/"
					+ ApplicationConstants.DOC_MAPPING_NODE + "/" + ApplicationConstants.RULES_NODE + "["
					+ ApplicationConstants.RULE_NODE + "[@ruleid=\"" + asRuleId + "\"]]" + "/"
					+ ApplicationConstants.DOCS_TYPE_NODE + "/" + ApplicationConstants.DOC_TYPE_NODE, aoRuleDoc);
			Iterator<Element> loDocIterator = loDocEltList.iterator();
			while (loDocIterator.hasNext())
			{
				Element loDocElt = (Element) loDocIterator.next();
				String lsDocType = loDocElt.getText();
				String lsCategory = loDocElt.getAttributeValue("category");
				if (!aoDocMap.containsKey(lsDocType))
				{
					aoDocMap.put(lsDocType, lsCategory);
				}
			}
		}
	}

	/**
	 * Crates tree structure for Service Specialization and Add services
	 * 
	 * @param aoDoc - jdom of Taxonomy
	 * @param asId - Id of element from which tree starts(root of tree)
	 * @param asType - contains value -> button/checkbox to differentiate
	 *            between type for which function is called
	 * @return Tree Structure as String
	 * @throws Exception
	 */
	public static String getTree(Document aoDoc, String asId, String asType) throws Exception
	{
		Element loEltService = XMLUtil.getElement("//element[(@id='" + asId + "')]", aoDoc);
		Element loRoot = new Element("ul");
		String lsReturn = "";
		if (loEltService != null)
		{
			loRoot.setAttribute("id", loEltService.getAttributeValue("id"));
			loRoot.setAttribute("class", "ulTreeClass");
			Document loHTMLDoc = new Document(loRoot);
			convertChildren(loEltService.getChildren(), loHTMLDoc, asType, null);
			lsReturn = XMLUtil.getXMLAsString(loHTMLDoc);
		}
		else
		{
			lsReturn = "No Data found";
		}
		return lsReturn;
	}

	/**
	 * @param aoElement - current jdom element from taxonomy
	 * @param aoHTMLDoc - jdom of new tree
	 * @param asType - contains value -> button/checkbox to differentiate
	 *            between type for which function is called
	 * @param asParentId - level 1 parent id
	 * @throws ApplicationException
	 */
	private static void convertChildren(List<Element> aoList, Document aoHTMLDoc, String asType, String asParentId)
			throws ApplicationException
	{

		for (Element loElt : aoList)
		{
			boolean lbToPass = false;
			// Check for eliminating Inactive Taxonomy
			if (null != loElt.getAttributeValue("activeflag")
					&& loElt.getAttributeValue("activeflag").equalsIgnoreCase("1"))
			{
				lbToPass = true;
			}
			if (lbToPass)
			{
				String lsParentId = loElt.getAttributeValue("parentid");
				if (asParentId != null)
				{
					lsParentId = asParentId;
				}
				String lsId = loElt.getAttributeValue("id");
				Element loLi = new Element("li");
				loLi.setAttribute("id", lsId);
				loLi.setAttribute("class", "liTreeClass");
				Element loTitle = new Element("div");
				loTitle.setAttribute("class", "titleClass");
				loTitle.setText(loElt.getAttributeValue("name"));
				loLi.addContent(loTitle);
				Element loDescriptionNode = loElt.getChild("description");
				if (loDescriptionNode != null)
				{
					Element loDescription = new Element("div");
					loDescription.setAttribute("class", "descriptionTreeClass");
					loDescription.setText(loDescriptionNode.getText());
					if (asType != null)
					{
						Element loInput = new Element("input");
						loInput.setAttribute("class", "inputClass");
						loInput.setAttribute("id", "input" + lsId);
						loInput.setAttribute("name", "taxonomyList");
						loInput.setAttribute("type", asType);
						if (asType.equalsIgnoreCase("button"))
						{
							loInput.setAttribute("value", "+ Add");
							loInput.setAttribute("title", "+ Add");
							loInput.setAttribute("onclick", "addRemoveService('" + loElt.getAttributeValue("name")
									+ "',this,'" + lsId + "')");
						}
						else
						{
							loInput.setAttribute("value", lsId);
						}
						loDescription.addContent(loInput);
					}
					loLi.addContent(loDescription);
				}

				Element loRequiredParent = XMLUtil.getElement("//ul[(@id='" + lsParentId + "')]", aoHTMLDoc);

				if (loRequiredParent == null)
				{
					loRequiredParent = new Element("ul");
					loRequiredParent.setAttribute("id", lsParentId);
					loRequiredParent.setAttribute("class", "ulTreeClass");
					Element loParent = XMLUtil.getElement("//li[(@id='" + lsParentId + "')]", aoHTMLDoc);
					if (asType != null)
					{
						Element loInput = ((Element) loParent.getChildren().get(1)).getChild("input");
						if (asType.equalsIgnoreCase("button"))
						{
							loInput.setAttribute("value", "Continue");
							loInput.setAttribute("title", "Continue");
							loInput.setAttribute("onclick", "clickButton(this)");
						}
						else
						{
							loInput.detach();
						}
					}
					loParent.addContent(loRequiredParent);
				}
				loRequiredParent.addContent(loLi);
				if ((asType != null && !asType.equalsIgnoreCase("button"))
						|| loElt.getAttributeValue("evidencerequiredflag").equalsIgnoreCase("0"))
				{
					convertChildren(loElt.getChildren(), aoHTMLDoc, asType, null);
				}
			}
		}
	}

	/**
	 * Gets Taxonomy name for an element id
	 * 
	 * @param asElementId - element id for which name to be fetched
	 * @param aoTaxonomyDom - jdom of Taxonomy
	 * @return Taxonomy name for the provided id
	 * @throws ApplicationException
	 */
	public static String getTaxonomyName(String asElementId, Document aoTaxonomyDom) throws ApplicationException
	{
		StringBuffer loSBServiceName = new StringBuffer();
		List<String> loLNames = new ArrayList<String>();
		getTaxonomyName(asElementId, aoTaxonomyDom, loLNames);
		ListIterator<String> loItr = loLNames.listIterator(loLNames.size());
		while (loItr.hasPrevious())
		{
			String lsName = loItr.previous();
			loSBServiceName.append(lsName);
			if (loItr.hasPrevious())
			{
				loSBServiceName.append(" > ");
			}
		}
		return loSBServiceName.toString();
	}

	/**
	 * Gets Taxonomy name for an element id
	 * 
	 * @param asElementId - element id for which name to be fetched
	 * @param aoTaxonomyDom - jdom of Taxonomy
	 * @param aoLNames - Heirarchy of element names
	 * @throws ApplicationException
	 */
	public static void getTaxonomyName(String asElementId, Document aoTaxonomyDom, List<String> aoLNames)
			throws ApplicationException
	{
		String lsXPath = "//element[(@id=\"" + asElementId + "\")]";
		Element loElement = XMLUtil.getElement(lsXPath, aoTaxonomyDom);
		if (loElement != null)
		{
			String lsEleType = loElement.getAttributeValue("type");
			if (!"TAXONOMY".equalsIgnoreCase(lsEleType))
			{
				aoLNames.add(loElement.getAttributeValue("name"));
				// getTaxonomyName(lsParentId, aoTaxonomyDom, aoLNames); // for
				// defect 1715
			}
		}
	}

	/**
	 * Joins parent name with child
	 * 
	 * @param aoLChild - List of taxonomy tree
	 * @param aoTaxonomyDom - jdom of taxonomy
	 * @throws ApplicationException
	 */
	public static void joinParentNameWithChild(List<TaxonomyTree> aoLChild, Document aoTaxonomyDom)
			throws ApplicationException
	{
		if (aoLChild != null && !aoLChild.isEmpty())
		{
			Iterator<TaxonomyTree> loItr = aoLChild.iterator();
			while (loItr.hasNext())
			{
				TaxonomyTree loElement = loItr.next();
				String lsEleId = loElement.getMsElementid();
				String lsName = getTaxonomyName(lsEleId, aoTaxonomyDom);
				loElement.setMsDisplayName(lsName);
			}
		}
	}

	/**
	 * Converts taxonomy element to TaxonomyTree
	 * 
	 * @param aoTaxonomyElement - jdome taxonomy element
	 * @param aoTaxonomyDom - jdom of taxonomy
	 * @return TaxonomyTree bean for taxonomy element
	 * @throws ApplicationException
	 */
	public static TaxonomyTree convertToTaxonomyObject(Element aoTaxonomyElement, Document aoTaxonomyDom)
			throws ApplicationException
	{
		TaxonomyTree loTaxonomyTree = new TaxonomyTree();
		String loDescription = aoTaxonomyElement.getChild("description").getText();
		loTaxonomyTree.setMsElementid(aoTaxonomyElement.getAttributeValue("id"));
		loTaxonomyTree.setMsElementName(aoTaxonomyElement.getAttributeValue("name"));
		loTaxonomyTree.setMsElementType(aoTaxonomyElement.getAttributeValue("type"));
		loTaxonomyTree.setMsBranchid(aoTaxonomyElement.getAttributeValue("branchid"));
		loTaxonomyTree.setMsParentid(aoTaxonomyElement.getAttributeValue("parentid"));
		loTaxonomyTree.setMsEvidenceReqd(aoTaxonomyElement.getAttributeValue("evidencerequiredflag"));
		loTaxonomyTree.setMsActiveFlag(aoTaxonomyElement.getAttributeValue("activeflag"));
		loTaxonomyTree.setMsSelectionFlag(aoTaxonomyElement.getAttributeValue("selectionflag"));
		loTaxonomyTree.setMsElementDescription(loDescription);
		String lsXPath = "//element[(@parentid=\"" + aoTaxonomyElement.getAttributeValue("id") + "\")]";
		List<Element> loAllChilds = XMLUtil.getElementList(lsXPath, aoTaxonomyDom);
		if (loAllChilds != null && !loAllChilds.isEmpty())
		{
			loTaxonomyTree.setChildId("true");
		}
		else
		{
			loTaxonomyTree.setChildId("false");
		}
		return loTaxonomyTree;
	}

	/**
	 * @param aoStatusBeanMap - StatusMap without not started statuses
	 * @throws ApplicationException
	 */
	public static void getCompleteStatusMap(Map<String, StatusBean> aoStatusBeanMap) throws ApplicationException
	{
		String lsNotStartedStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replace(" ", "");
		for (int liCounter = 0; liCounter < ApplicationConstants.SECTION_NAMES.length; liCounter++)
		{
			String lsSectionName = ApplicationConstants.SECTION_NAMES[liCounter];
			if (aoStatusBeanMap.keySet().contains(lsSectionName))
			{
				StatusBean loStatusBean = aoStatusBeanMap.get(lsSectionName);
				Map<String, String> loHMMapStatus = loStatusBean.getMoHMSubSectionDetails();
				Map<String, String> loHMMapStatusToDisplay = loStatusBean.getMoHMSubSectionDetailsToDisplay();
				for (String lsSubSecName : ApplicationConstants.SUB_SECTION_NAMES_MAPPING[liCounter])
				{
					if (!loHMMapStatus.containsKey(lsSubSecName))
					{
						loHMMapStatus.put(lsSubSecName, lsNotStartedStatus);
						loHMMapStatusToDisplay.put(lsSubSecName, ApplicationConstants.NOT_STARTED_STATE);
					}
				}
				loStatusBean.setMsSectionStatus(getSectionStatus(loHMMapStatus, liCounter));
			}
			else
			{
				StatusBean loStatusBean = new StatusBean();
				Map<String, String> loHMMapStatus = new HashMap<String, String>();
				Map<String, String> loHMMapStatusToDisplay = new HashMap<String, String>();
				for (String lsSubSecName : ApplicationConstants.SUB_SECTION_NAMES_MAPPING[liCounter])
				{
					String lsSubSectionStatus = lsNotStartedStatus;
					loHMMapStatus.put(lsSubSecName, lsSubSectionStatus);
					loHMMapStatusToDisplay.put(lsSubSecName, ApplicationConstants.NOT_STARTED_STATE);
				}
				loStatusBean.setMoHMSubSectionDetails(loHMMapStatus);
				loStatusBean.setMoHMSubSectionDetailsToDisplay(loHMMapStatusToDisplay);
				loStatusBean.setMsSectionStatus(getSectionStatus(loHMMapStatus, liCounter));
				aoStatusBeanMap.put(lsSectionName, loStatusBean);
			}
		}
	}

	/**
	 * @param aoMap - SubSection status map
	 * @param aiSectionIndex - Section index
	 * @return section status
	 * @throws ApplicationException
	 */
	private static String getSectionStatus(Map<String, String> aoMap, int aiSectionIndex) throws ApplicationException
	{
		String lsNotStartedStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replace(" ", "");
		String lsDraftStatus = ApplicationConstants.DRAFT_STATE.toLowerCase().replace(" ", "");
		String lsCompleteStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase().replace(" ", "");
		String lsStatus = "";
		try
		{
			for (String lsSubSection : ApplicationConstants.SUB_SECTION_NAMES_MAPPING[aiSectionIndex])
			{
				String lsSubSectionStatus = aoMap.get(lsSubSection);
				if (lsSubSectionStatus == null)
				{
					lsStatus = lsNotStartedStatus;
				}
				else
				{
					if (lsSubSectionStatus.equalsIgnoreCase(lsDraftStatus)
							|| (lsSubSectionStatus.equalsIgnoreCase(lsCompleteStatus) && lsStatus
									.equalsIgnoreCase(lsNotStartedStatus))
							|| (lsSubSectionStatus.equalsIgnoreCase(lsNotStartedStatus) && lsStatus
									.equalsIgnoreCase(lsCompleteStatus)))
					{
						lsStatus = lsDraftStatus;
						break;
					}
					else
					{
						lsStatus = lsSubSectionStatus;
					}
				}
			}
			if (lsStatus.isEmpty())
			{
				lsStatus = lsNotStartedStatus;
			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Some error occured while creating status map for section", aoExp);
		}
		return lsStatus;
	}

	/**
	 * Create printable view for business application
	 * 
	 * @param aoPrintBeanMap - Map with key as Section name and value as content
	 * @param aoPopulationBeanList - List of selected population
	 * @param aoLangList - List of selected Language
	 * @param aoGeoList - List of selected Geographies
	 * @param asWebContentPath - Web Content Path
	 * @param aoPrinterFriendlyComments - Map with key as Section name and value
	 *            as comments
	 * @param abIsFinalView - Flag depicting whether to generate final view or
	 *            normal view
	 * @return content to be displayed
	 * @throws ApplicationException
	 */
	public static String createPrintableView(Map<String, PrintContentBean> aoPrintBeanMap,
			List<Population> aoPopulationBeanList, List<Map<String, String>> aoLangList,
			List<Map<String, String>> aoGeoList, String asWebContentPath,
			Map<String, StringBuffer> aoPrinterFriendlyComments, Boolean abIsFinalView) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: createPrintableView :: start");
		StringBuffer loPrintableContent = new StringBuffer();
		try
		{
			for (int liCounter = 0; liCounter < ApplicationConstants.SECTION_NAMES.length; liCounter++)
			{
				String lsSectionName = ApplicationConstants.SECTION_NAMES[liCounter];
				boolean lbIsComment = false;
				if (aoPrinterFriendlyComments.containsKey(lsSectionName.toLowerCase()) && abIsFinalView)
				{
					lbIsComment = true;
					loPrintableContent.append("<div class='commentBox' id='").append(lsSectionName)
							.append("_comments' >").append(aoPrinterFriendlyComments.get(lsSectionName.toLowerCase()))
							.append("</div>");
				}
				String lsDisplayName = ApplicationConstants.SECTION_NAMES_DISPLAY_PRINT[liCounter];
				PrintContentBean loPrintBean = aoPrintBeanMap.get(lsSectionName);
				if (loPrintBean != null)
				{
					Map<String, Object> loFormContent = loPrintBean.getMoFormContent();
					List<DocumentBean> loDocContent = loPrintBean.getMoDocContent();
					loPrintableContent.append("<div class='headingText'><font class='headingFont'>")
							.append(lsDisplayName).append(": </font><font class='sectionHeading'>")
							.append(ApplicationConstants.QUESTION_DISPLAY).append("</font>")
							.append(showCommentLink(lsSectionName, lbIsComment)).append("</div>");
					LOG_OBJECT.Debug("Inside :: createPrintableView :: create form view");
					if (loFormContent != null && !loFormContent.isEmpty())
					{
						loPrintableContent.append(getFormHTML(loFormContent, asWebContentPath, false));
					}
					else
					{
						loPrintableContent.append(getNoContentData());
					}
					if (!lsSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES))
					{
						loPrintableContent.append("<div class='headingText'><font class='headingFont'>")
								.append(lsDisplayName).append(": </font><font class='sectionHeading'>")
								.append(ApplicationConstants.DOCUMENTS_DISPLAY).append("</font>")
								.append(showCommentLink(lsSectionName, lbIsComment)).append("</div>");
						LOG_OBJECT.Debug("Inside :: createPrintableView :: create document view");
						if (loDocContent != null)
						{
							loPrintableContent.append(getFormDocument(loDocContent, abIsFinalView));
						}
						else
						{
							loPrintableContent.append(getNoContentData());
						}
					}
					if (lsSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS))
					{
						loPrintableContent.append("<div class='headingText'><font class='headingFont'>")
								.append(lsDisplayName).append(": </font><font class='sectionHeading'>")
								.append(ApplicationConstants.GEOGRAPHY_DISPLAY).append("</font>")
								.append(showCommentLink(lsSectionName, lbIsComment)).append("</div>");
						loPrintableContent.append(generateGeoLangTables(aoGeoList, false));
						loPrintableContent.append("<div class='headingText'><font class='headingFont'>")
								.append(lsDisplayName).append(": </font><font class='sectionHeading'>")
								.append(ApplicationConstants.LANGUAGES_DISPLAY).append("</font>")
								.append(showCommentLink(lsSectionName, lbIsComment)).append("</div>");
						loPrintableContent.append(generateGeoLangTables(aoLangList, true));
						loPrintableContent.append("<div class='headingText'><font class='headingFont'>")
								.append(lsDisplayName).append(": </font><font class='sectionHeading'>")
								.append(ApplicationConstants.POPULATIONS_DISPLAY).append("</font>")
								.append(showCommentLink(lsSectionName, lbIsComment)).append("</div>");
						loPrintableContent.append(generatePopulationTable(aoPopulationBeanList));
					}
				}
			}
		}
		catch (ApplicationException aoExp)
		{
			throw new ApplicationException("Error occured while creating printer friendly view", aoExp);
		}
		LOG_OBJECT.Debug("Inside :: createPrintableView :: end");
		return loPrintableContent.toString();
	}

	/**
	 * Generates Comment link for final view
	 * 
	 * @param asSectionName - Section name
	 * @param abIsComment - flag showing if comment is available or not for the
	 *            section
	 * @return Comment link
	 * @throws ApplicationException
	 */
	private static String showCommentLink(String asSectionName, boolean abIsComment) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: showCommentLink :: start");
		StringBuffer loLink = new StringBuffer();
		if (abIsComment)
		{
			loLink.append("<div class='commentLinkClass' toshow='").append(asSectionName).append("'>Show ")
					.append(toTitleCase(asSectionName)).append(" Comments</div>");
		}
		LOG_OBJECT.Debug("Inside :: showCommentLink :: end");
		return loLink.toString();
	}

	/**
	 * Generates Data for Geography/Language for print
	 * 
	 * @param aoList - List of selected Geographies/Languages
	 * @param abIsLanguage - flag showing if language or geography
	 * @return StringBuffer of Geography/Language content for print
	 * @throws ApplicationException
	 */
	private static StringBuffer generateGeoLangTables(List<Map<String, String>> aoList, boolean abIsLanguage)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: generateGeoLangTables :: start");
		final org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		StringBuffer loSBPrintableContent = new StringBuffer();
		boolean lbHasAdditionLang = false;
		boolean lbAnotherGeo = false;
		if (aoList != null && !aoList.isEmpty())
		{
			printGeographyMessages(aoList, abIsLanguage, loDoc, loSBPrintableContent, lbHasAdditionLang, lbAnotherGeo);
		}
		else
		{
			loSBPrintableContent.append(getNoContentData());
		}
		LOG_OBJECT.Debug("Inside :: generateGeoLangTables :: end");
		return loSBPrintableContent;
	}

	/**
	 * @param aoList
	 * @param abIsLanguage
	 * @param aoDoc
	 * @param asbPrintableContent
	 * @param abHasAdditionLang
	 * @param abAnotherGeo
	 * @throws ApplicationException
	 */
	private static void printGeographyMessages(List<Map<String, String>> aoList, boolean abIsLanguage,
			final org.jdom.Document aoDoc, StringBuffer asbPrintableContent, boolean abHasAdditionLang,
			boolean abAnotherGeo) throws ApplicationException
	{
		asbPrintableContent.append("<table><tr>");
		if (abIsLanguage)
		{
			asbPrintableContent.append("<td class='questions'>").append(ApplicationConstants.PRINT_LANGUAGE_MESSAGE)
					.append("</td>");
		}
		else
		{
			asbPrintableContent.append("<td class='questions'>").append(ApplicationConstants.PRINT_GEOGRAPHY_MESSAGE)
					.append("</td>");
		}
		asbPrintableContent.append("<td>");
		List<String> loLangGeoList = new ArrayList<String>();
		List<String> loLangListOther = new ArrayList<String>();
		StringBuffer loOtherContent = new StringBuffer();
		if (!abIsLanguage)
		{
			sortGeography(aoList, aoDoc);
		}
		for (Map<String, String> loMap : aoList)
		{
			String lsEltId = loMap.get("ELEMENT_ID");
			if (abIsLanguage)
			{
				abHasAdditionLang = displayLanguage(aoDoc, abHasAdditionLang, loLangGeoList, loLangListOther, loMap,
						lsEltId);
			}
			else
			{
				abAnotherGeo = anotherGeographyLangurage(aoDoc, abAnotherGeo, loLangGeoList, loMap, lsEltId);
			}
		}
		if (abIsLanguage)
		{
			java.util.Collections.sort(loLangGeoList);
		}
		for (String lsName : loLangGeoList)
		{
			asbPrintableContent.append("- ").append(lsName.trim()).append("<br />");
		}
		if (abIsLanguage)
		{
			java.util.Collections.sort(loLangListOther);
			for (String lsName : loLangListOther)
			{
				loOtherContent.append(lsName).append(", ");
			}
			if (loOtherContent.length() > 0)
			{
				asbPrintableContent.append("- ").append("Other: ")
						.append(loOtherContent.substring(0, loOtherContent.length() - 2)).append("<br />");
			}
		}
		if (abIsLanguage && abHasAdditionLang)
		{
			asbPrintableContent.append("- ").append(ApplicationConstants.PRINT_LANGUAGE_MORE_MESSAGE).append("<br />");
		}
		else if (abAnotherGeo)
		{
			asbPrintableContent.append("- ").append(ApplicationConstants.PRINT_GEOGRAPHY_NO_MESSAGE).append("<br />");
		}
		asbPrintableContent.append("</td></tr></table>");
	}

	/**
	 * This method returns Geography Language
	 * @param aoDoc
	 * @param abHasAdditionLang
	 * @param aoLangGeoList
	 * @param aoLangListOther
	 * @param aoMap
	 * @param asEltId
	 * @return
	 * @throws ApplicationException
	 */
	private static boolean displayLanguage(final org.jdom.Document aoDoc, boolean abHasAdditionLang,
			List<String> aoLangGeoList, List<String> aoLangListOther, Map<String, String> aoMap, String asEltId)
			throws ApplicationException
	{
		String lsName;
		Element loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + asEltId + "\" and @type=\""
				+ ApplicationConstants.LANGUAGES_DISPLAY + "\"]", aoDoc);
		lsName = loCorrRuleElt.getAttributeValue("name");
		if (loCorrRuleElt.getParentElement().getAttributeValue("name").equalsIgnoreCase("Other")
				&& loCorrRuleElt.getParentElement().getAttributeValue("type")
						.equalsIgnoreCase(ApplicationConstants.LANGUAGES_DISPLAY))
		{
			aoLangListOther.add(lsName);
		}
		else
		{
			aoLangGeoList.add(lsName);
		}
		if (!abHasAdditionLang)
		{
			String lsHasAddLang = aoMap.get("LANGUAGE_INTERPRETATION");
			if (lsHasAddLang != null && !lsHasAddLang.equalsIgnoreCase("null") && lsHasAddLang.equalsIgnoreCase("true"))
			{
				abHasAdditionLang = true;
			}
		}
		return abHasAdditionLang;
	}

	/**
	 * This method returns another Geography Language.
	 * @param aoDoc
	 * @param abAnotherGeo
	 * @param aoLangGeoList
	 * @param aoMap
	 * @param asEltId
	 * @return
	 * @throws ApplicationException
	 */
	private static boolean anotherGeographyLangurage(final org.jdom.Document aoDoc, boolean abAnotherGeo,
			List<String> aoLangGeoList, Map<String, String> aoMap, String asEltId) throws ApplicationException
	{
		String lsName;
		if (asEltId != null)
		{
			Element loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + asEltId + "\" and @type=\""
					+ ApplicationConstants.GEOGRAPHY_DISPLAY + "\"]", aoDoc);
			if (loCorrRuleElt != null)
			{
				lsName = loCorrRuleElt.getAttributeValue("name") + " "
						+ loCorrRuleElt.getChild("description").getText();
				aoLangGeoList.add(lsName);
			}
		}
		String lsAnotherGeo = aoMap.get("ANOTHER_GEOGRAPHY");
		if (lsAnotherGeo != null && lsAnotherGeo.equalsIgnoreCase("on"))
		{
			abAnotherGeo = true;
		}
		return abAnotherGeo;
	}

	/**
	 * Sorts the geography for print view
	 * 
	 * @param aoList - list of geography
	 * @param aoDoc - taxonomy dom
	 */
	private static void sortGeography(List<Map<String, String>> aoList, final org.jdom.Document aoDoc)
	{
		Collections.sort(aoList, new Comparator<Map<String, String>>()
		{
			@Override
			public int compare(Map<String, String> aoObj1, Map<String, String> aoObj2)
			{
				String lsEltId1 = aoObj1.get("ELEMENT_ID");
				String lsEltId2 = aoObj2.get("ELEMENT_ID");
				String lsAnotherGeo1 = aoObj1.get("ANOTHER_GEOGRAPHY");
				String lsAnotherGeo2 = aoObj2.get("ANOTHER_GEOGRAPHY");
				if ((lsAnotherGeo1 != null && lsAnotherGeo1.equalsIgnoreCase("on"))
						|| (lsAnotherGeo2 != null && lsAnotherGeo2.equalsIgnoreCase("on")))
				{
					return 0;
				}
				if (lsEltId1 != null && lsEltId2 != null)
				{
					try
					{
						Element loCorrRuleElt1 = XMLUtil.getElement("//element[@id=\"" + lsEltId1 + "\" and @type=\""
								+ ApplicationConstants.GEOGRAPHY_DISPLAY + "\"]", aoDoc);
						Element loCorrRuleElt2 = XMLUtil.getElement("//element[@id=\"" + lsEltId2 + "\" and @type=\""
								+ ApplicationConstants.GEOGRAPHY_DISPLAY + "\"]", aoDoc);
						if (loCorrRuleElt1 != null && loCorrRuleElt2 != null)
						{
							Element loParentRuleElt1 = loCorrRuleElt1.getParentElement();
							Element loParentRuleElt2 = loCorrRuleElt2.getParentElement();
							if (loParentRuleElt1.getAttributeValue("name").equals(
									loParentRuleElt2.getAttributeValue("name")))
							{
								String lsName1 = loCorrRuleElt1.getAttributeValue("name");
								String lsName2 = loCorrRuleElt2.getAttributeValue("name");
								if (lsName1.equals(lsName2))
								{
									return loCorrRuleElt1.getChild("description").getText()
											.compareTo(loCorrRuleElt2.getChild("description").getText());
								}
								else
								{
									try
									{
										return (Integer.valueOf(lsName1)).compareTo(Integer.valueOf(lsName2));
									}
									catch (NumberFormatException aoExp)
									{
										return (lsName1).compareTo(lsName2);
									}
								}
							}
							else
							{
								return loParentRuleElt1.getAttributeValue("name").compareTo(
										loParentRuleElt2.getAttributeValue("name"));
							}
						}
					}
					catch (ApplicationException loEx)
					{
						LOG_OBJECT.Error("An Error occured while sorting geography list for print", loEx);
					}
				}
				return 0;
			}
		});
	}

	/**
	 * Generates Data for Population for print
	 * 
	 * @param aoPopulationBeanList - List of selected Population
	 * @return StringBuffer of Population content for print
	 * @throws ApplicationException
	 */
	private static StringBuffer generatePopulationTable(List<Population> aoPopulationBeanList)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: generatePopulationTable :: start");
		StringBuffer loSBPrintable = new StringBuffer();
		org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		if (aoPopulationBeanList != null && !aoPopulationBeanList.isEmpty())
		{
			loSBPrintable.append("<table><tr>");
			loSBPrintable.append("<td class='questions'>").append(ApplicationConstants.PRINT_POPULATION_MESSAGE)
					.append("</td>");
			loSBPrintable.append("<td>");
			String lsName = "";
			Collections.sort(aoPopulationBeanList, new Comparator<Population>()
			{
				@Override
				public int compare(Population aoObject1, Population aoObject2)
				{
					org.jdom.Document loDoc;
					try
					{
						loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
								ApplicationConstants.TAXONOMY_ELEMENT);
						String lsEltId1 = aoObject1.getMsElementid();
						String lsEltId2 = aoObject2.getMsElementid();
						if (!(lsEltId1.equalsIgnoreCase("-1") || lsEltId1.equalsIgnoreCase("-2")))
						{
							return -1;
						}
						if (!(lsEltId2.equalsIgnoreCase("-1") || lsEltId2.equalsIgnoreCase("-2")))
						{
							return 1;
						}
						Element loCorrRuleElt1 = XMLUtil.getElement("//element[@id=\"" + lsEltId1 + "\" and @type=\""
								+ ApplicationConstants.POPULATIONS_DISPLAY + "\"]", loDoc);
						Element loCorrRuleElt2 = XMLUtil.getElement("//element[@id=\"" + lsEltId2 + "\" and @type=\""
								+ ApplicationConstants.POPULATIONS_DISPLAY + "\"]", loDoc);
						return (loCorrRuleElt1.getAttributeValue("name").compareTo(loCorrRuleElt2
								.getAttributeValue("name")));

					}
					catch (ApplicationException aoExp)
					{
						LOG_OBJECT.Error("Error in generatePopulationTable method ", aoExp);
					}
					return 0;
				}
			});
			for (Population loPopulationBean : aoPopulationBeanList)
			{
				String lsEltId = loPopulationBean.getMsElementid();
				StringBuffer loSBValue = new StringBuffer();
				if (lsEltId.equalsIgnoreCase("-1"))
				{
					lsName = "Other:";
					loSBValue.append(loPopulationBean.getMsOther());
				}
				else if (lsEltId.equalsIgnoreCase("-2"))
				{
					lsName = "";
					loSBValue.append(ApplicationConstants.PRINT_POPULATION_NO_SPECIFIC_MESSAGE);
				}
				else
				{
					Element loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + lsEltId + "\" and @type=\""
							+ ApplicationConstants.POPULATIONS_DISPLAY + "\"]", loDoc);
					lsName = loCorrRuleElt.getAttributeValue("name");
					String lsAgeFrom = loPopulationBean.getMsAgeFrom();
					String lsAgeTo = loPopulationBean.getMsAgeTo();
					if (lsAgeFrom != null && lsAgeFrom.length() > 0)
					{
						loSBValue.append(" (");
						loSBValue.append(lsAgeFrom);
						loSBValue.append(" to ");
						loSBValue.append(lsAgeTo);
						loSBValue.append(")");
					}
				}
				loSBPrintable.append("- ");
				loSBPrintable.append(lsName);
				loSBPrintable.append(loSBValue);
				loSBPrintable.append("<br />");
			}
			loSBPrintable.append("</td>");
			loSBPrintable.append("</tr></table>");
		}
		else
		{
			loSBPrintable.append(getNoContentData());
		}
		LOG_OBJECT.Debug("Inside :: generatePopulationTable :: end");
		return loSBPrintable;
	}

	/**
	 * Generates Data for Forms for print
	 * 
	 * @param aoFormContent - Map of form data
	 * @param asWebContentPath - Web Content Path
	 * @param abShowBlank - flag depecting if blank(not filled) data is to be
	 *            displayed
	 * @return StringBuffer of Form content for print
	 * @throws ApplicationException
	 */
	public static StringBuffer getFormHTML(Map<String, Object> aoFormContent, String asWebContentPath,
			boolean abShowBlank) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: getFormHTML :: start");
		StringBuffer loPrintableFormContent = new StringBuffer();
		String lsFormName = (String) aoFormContent.get(ApplicationConstants.FORM_NAME);
		String lsFormVersion = (String) aoFormContent.get(ApplicationConstants.FORMVERSION_STRING);
		String lsElementXMLPath = PropertyUtil.getDeployedFormLocation(lsFormName, lsFormVersion, false);
		String lsQuestionXMLPath = PropertyUtil.getDeployedFormLocation(lsFormName, lsFormVersion, true);
		Document loQuestionDoc = XMLUtil.getDomObj(asWebContentPath + lsQuestionXMLPath);
		Document loElementDoc = XMLUtil.getDomObj(asWebContentPath + lsElementXMLPath);
		List<Element> loQuestionList = XMLUtil.getElementList("//question", loQuestionDoc);
		for (Element loQuestionElt : loQuestionList)
		{
			String lsQuestion = loQuestionElt.getAttributeValue("text");
			String lsHeading = loQuestionElt.getAttributeValue("heading");
			StringBuffer loAnswer = new StringBuffer();
			String lsColumnNames = loQuestionElt.getAttributeValue("columnname");
			String loColumnNamesArray[] = lsColumnNames.split(",");
			if (lsHeading != null && !lsHeading.trim().equalsIgnoreCase("null") && !lsHeading.isEmpty())
			{
				loPrintableFormContent.append("<tr><td colspan='2' class='subheading'>");
				loPrintableFormContent.append(lsHeading);
				loPrintableFormContent.append("</td></tr>");
			}
			for (String lsColumnName : loColumnNamesArray)
			{
				getFormAnswerForColumn(aoFormContent, loElementDoc, loAnswer, lsColumnNames, lsColumnName);
			}
			if (loAnswer.toString().contains("<br />- "))
			{
				loAnswer.insert(0, "- ");
			}
			if (loAnswer.length() > 0 && !abShowBlank)
			{
				if ("PHONE_NUMBER".equalsIgnoreCase(loColumnNamesArray[0]))
				{
					loPrintableFormContent.append("<tr><td class='questions'>");
					loPrintableFormContent.append(lsQuestion);
					loPrintableFormContent.append("</td><td class='answers'><label class='printerFriendlyPhone'>");
					loPrintableFormContent.append(loAnswer);
					loPrintableFormContent.append("</label></td></tr>");
				}
				else
				{
					loPrintableFormContent.append("<tr><td class='questions'>");
					loPrintableFormContent.append(lsQuestion);
					loPrintableFormContent.append("</td><td class='answers'>");
					loPrintableFormContent.append(loAnswer);
					loPrintableFormContent.append("</td></tr>");
				}
			}
			else if (abShowBlank)
			{
				loPrintableFormContent.append("<tr><td class='questions'>");
				loPrintableFormContent.append(lsQuestion);
				loPrintableFormContent.append("</td><td class='answers'>");
				loPrintableFormContent.append("");
				loPrintableFormContent.append("</td></tr>");
			}
		}
		if (loPrintableFormContent.length() > 0)
		{
			loPrintableFormContent.insert(0, "<table>").append("</table>");
		}
		else
		{
			loPrintableFormContent.append(getNoContentData());
		}
		LOG_OBJECT.Debug("Inside :: getFormHTML :: end");
		return loPrintableFormContent;
	}

	/**
	 * Gets form answer data for a field
	 * 
	 * @param aoFormContent - Map of form data
	 * @param aoElementDoc - jdom of form template
	 * @param asbAnswer - answer data to be generated for question data
	 * @param asColumnNames - Current column names
	 * @param asColumnName - Current column name
	 * @throws ApplicationException
	 */
	private static void getFormAnswerForColumn(Map<String, Object> aoFormContent, Document aoElementDoc,
			StringBuffer asbAnswer, String asColumnNames, String asColumnName) throws ApplicationException
	{
		String lsColumnValue = (String) aoFormContent.get(asColumnName);
		if (lsColumnValue != null && !lsColumnValue.trim().equalsIgnoreCase("null") && !lsColumnValue.isEmpty())
		{
			lsColumnValue = StringEscapeUtils.escapeHtml(lsColumnValue);
			if (asColumnNames.equalsIgnoreCase("ACCOUNTING_PERIOD_START_MONTH,ACCOUNTING_PERIOD_END_MONTH"))
			{
				if (asbAnswer.length() == 0)
				{
					asbAnswer.append(lsColumnValue);
				}
				else
				{
					asbAnswer.append(" to ");
					asbAnswer.append(lsColumnValue);
				}
			}
			else
			{
				Element loElt = XMLUtil.getElement("//element[@schemaname='" + asColumnName + "']", aoElementDoc);
				if (loElt != null && loElt.getAttributeValue("elementtype").equalsIgnoreCase("radio"))
				{
					String lsToDisplay = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE,
							lsColumnValue);
					if (lsToDisplay != null)
					{
						lsColumnValue = lsToDisplay;
					}
				}
				else if (loElt != null && loElt.getAttributeValue("elementtype").equalsIgnoreCase("checkbox"))
				{
					Element loElement = XMLUtil.getElement("//element[@schemaname='" + asColumnName
							+ "' and @todisplay]", aoElementDoc);
					if (loElement != null)
					{
						lsColumnValue = loElement.getAttributeValue("todisplay");
					}
				}
				if (asbAnswer.length() == 0)
				{
					asbAnswer.append(lsColumnValue);
				}
				else
				{
					asbAnswer.append("<br />- ");
					asbAnswer.append(lsColumnValue);
				}
			}
		}
	}

	/**
	 * Gets form document data for print
	 * 
	 * @param aoDocContent - list of document
	 * @param abIsFinalView - Flag depicting whether to generate final view or
	 *            normal view
	 * @return StringBuffer of document content
	 * @throws ApplicationException
	 */
	private static StringBuffer getFormDocument(List<DocumentBean> aoDocContent, Boolean abIsFinalView)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: getFormDocument :: start");
		StringBuffer loPrintableDocContent = new StringBuffer();
		String lsClass = "";
		for (int liCounter = 0; liCounter < aoDocContent.size(); liCounter++)
		{
			DocumentBean loDocBean = aoDocContent.get(liCounter);
			if (loDocBean != null)
			{
				if (liCounter % 2 != 0)
				{
					lsClass = "tableRowOddPrint";
				}
				else
				{
					lsClass = "tableRowEvenPrint";
				}
				loPrintableDocContent.append("<tr class='");
				loPrintableDocContent.append(lsClass);
				loPrintableDocContent.append("'>");
				createDocTd(loDocBean.getDocTitle(), loDocBean, loPrintableDocContent, abIsFinalView,
						ApplicationConstants.HEADER_DOC_NAME);
				if (loDocBean.getDocStatus() != null
						&& !loDocBean.getDocStatus().equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE))
				{
					createDocTd("Info", loDocBean, loPrintableDocContent, abIsFinalView,
							ApplicationConstants.HEADER_DOC_INFO);
				}
				else
				{
					createDocTd("N/A", loDocBean, loPrintableDocContent, abIsFinalView,
							ApplicationConstants.HEADER_DOC_INFO);
				}
				createDocTd(loDocBean.getDocType(), loDocBean, loPrintableDocContent, null,
						ApplicationConstants.HEADER_DOC_TYPE);
				createDocTd(loDocBean.getDocStatus(), loDocBean, loPrintableDocContent, null,
						ApplicationConstants.HEADER_DOC_STATUS);
				if (loDocBean.getDocStatus() != null
						&& !loDocBean.getDocStatus().equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE))
				{
					getDocTDdata(loPrintableDocContent, loDocBean);
				}
				else
				{
					createDocTd(null, loDocBean, loPrintableDocContent, null, ApplicationConstants.HEADER_DOC_MODIFIED);
					createDocTd(null, loDocBean, loPrintableDocContent, null,
							ApplicationConstants.HEADER_DOC_MODIFIED_BY);
				}
				loPrintableDocContent.append("</tr>");
			}
		}
		if (loPrintableDocContent.length() > 0)
		{
			setTableHeader(loPrintableDocContent);
		}
		else
		{
			loPrintableDocContent.append(getNoContentData());
		}
		LOG_OBJECT.Debug("Inside :: getFormDocument :: end");
		return loPrintableDocContent;
	}

	/**
	 * Method is used to get Doc TD Data.
	 * @param asbPrintableDocContent
	 * @param aoDocBean
	 * @throws ApplicationException
	 */
	private static void getDocTDdata(StringBuffer asbPrintableDocContent, DocumentBean aoDocBean)
			throws ApplicationException
	{
		Date loModifiedDate = aoDocBean.getModifiedDate();
		String lsModifiedDate = null;
		if (loModifiedDate != null)
		{
			java.util.Date loUtilDate = loModifiedDate;
			lsModifiedDate = DateUtil.getDateMMddYYYYFormat(loUtilDate);
		}
		createDocTd(lsModifiedDate, aoDocBean, asbPrintableDocContent, null, ApplicationConstants.HEADER_DOC_MODIFIED);
		String lsModifiedBy = aoDocBean.getModifiedBy();
		if (lsModifiedBy != null && !lsModifiedBy.isEmpty())
		{
			createDocTd(FileNetOperationsUtils.getUserName(lsModifiedBy), aoDocBean, asbPrintableDocContent, null,
					ApplicationConstants.HEADER_DOC_MODIFIED_BY);
		}
		else
		{
			createDocTd(null, aoDocBean, asbPrintableDocContent, null, ApplicationConstants.HEADER_DOC_MODIFIED_BY);
		}
	}

	/**
	 * Method is used to set the header data.
	 * @param asbPrintableDocContent
	 */
	private static void setTableHeader(StringBuffer asbPrintableDocContent)
	{
		StringBuffer lsTableHeader = new StringBuffer();
		lsTableHeader.append("<table class='documentTablePrint'><tr class='tableHeaderPrint'>");
		lsTableHeader.append("<th>");
		lsTableHeader.append(ApplicationConstants.HEADER_DOC_NAME);
		lsTableHeader.append("</th>");
		lsTableHeader.append("<th>");
		lsTableHeader.append(ApplicationConstants.HEADER_DOC_INFO);
		lsTableHeader.append("</th>");
		lsTableHeader.append("<th>");
		lsTableHeader.append(ApplicationConstants.HEADER_DOC_TYPE);
		lsTableHeader.append("</th>");
		lsTableHeader.append("<th>");
		lsTableHeader.append(ApplicationConstants.HEADER_DOC_STATUS);
		lsTableHeader.append("</th>");
		lsTableHeader.append("<th>");
		lsTableHeader.append(ApplicationConstants.HEADER_DOC_MODIFIED);
		lsTableHeader.append("</th>");
		lsTableHeader.append("<th>");
		lsTableHeader.append(ApplicationConstants.HEADER_DOC_MODIFIED_BY);
		lsTableHeader.append("</th>");
		lsTableHeader.append("</tr>");
		asbPrintableDocContent.insert(0, lsTableHeader).append("</table>");
	}

	/**
	 * Gets form document td data for print
	 * 
	 * @param aoContent - Content to display
	 * @param aoDocBean - Document bean containing data
	 * @param asbPrintableDocContent - StringBuffer of printable content
	 * @param abIsFinalView - Flag depicting whether to generate final view or
	 *            normal view
	 * @param asColumnName - Header name
	 * @throws ApplicationException
	 */
	private static void createDocTd(Object aoContent, DocumentBean aoDocBean, StringBuffer asbPrintableDocContent,
			Boolean abIsFinalView, String asColumnName) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: createDocTd :: start");
		if (aoContent != null && (abIsFinalView == null || !abIsFinalView))
		{
			asbPrintableDocContent.append("<td>");
			asbPrintableDocContent.append(aoContent);
			asbPrintableDocContent.append("</td>");
		}
		else if (aoContent != null && abIsFinalView)
		{
			if (ApplicationConstants.HEADER_DOC_NAME.equalsIgnoreCase(asColumnName))
			{
				asbPrintableDocContent.append("<td><a href='#' onclick=\"javascript: viewDocument('");
				asbPrintableDocContent.append(aoDocBean.getDocID());
				asbPrintableDocContent.append("', '");
				asbPrintableDocContent.append(aoContent);
				asbPrintableDocContent.append("');\" >");
				asbPrintableDocContent.append(aoContent);
				asbPrintableDocContent.append("</a></td>");
			}

			else if (ApplicationConstants.HEADER_DOC_INFO.equalsIgnoreCase(asColumnName))
			{
				if (abIsFinalView)
				{
					asbPrintableDocContent.append("<td><a href='#' name='docInfo'");
					asbPrintableDocContent.append(" docid='");
					asbPrintableDocContent.append(aoDocBean.getDocID());
					asbPrintableDocContent.append("'");
					asbPrintableDocContent.append(" doctype='");
					asbPrintableDocContent.append(aoDocBean.getDocType());
					asbPrintableDocContent.append("'");
					asbPrintableDocContent.append(" doccat='");
					asbPrintableDocContent.append(aoDocBean.getDocCategory());
					asbPrintableDocContent.append("'");
					asbPrintableDocContent.append(">");
					asbPrintableDocContent.append(aoContent);
					asbPrintableDocContent.append("</a></td>");
				}
				else
				{
					asbPrintableDocContent.append("<td>");
					asbPrintableDocContent.append(aoContent);
					asbPrintableDocContent.append("</td>");
				}
			}
		}
		else
		{
			asbPrintableDocContent.append("<td></td>");
		}
		LOG_OBJECT.Debug("Inside :: createDocTd :: end");
	}

	/**
	 * Creates No Content block
	 * 
	 * @return StringBuffer of No Content block
	 * @throws ApplicationException
	 */
	private static StringBuffer getNoContentData() throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: getNoContentData :: start");
		StringBuffer loNoContent = new StringBuffer();
		loNoContent.append("<div class='noContent'>");
		loNoContent.append(ApplicationConstants.PRINT_NO_CONTENT_MESSAGE);
		loNoContent.append("</div>");
		LOG_OBJECT.Debug("Inside :: getNoContentData :: end");
		return loNoContent;
	}

	/**
	 * @return returns dynamic Service application id
	 */
	public static String generateRandomNumber()
	{
		long lsTimeStamp = System.currentTimeMillis();
		StringBuffer loSbBrAppId = new StringBuffer("sr_");
		loSbBrAppId.append(lsTimeStamp);
		return loSbBrAppId.toString();
	}

	/**
	 * @returns dynamic Service application id
	 */
	public static String generateBusinessAppId()
	{
		long lsTimeStamp = System.currentTimeMillis();
		StringBuffer loSbBrAppId = new StringBuffer("br_");
		loSbBrAppId.append(lsTimeStamp);
		return loSbBrAppId.toString();
	}

	/**
	 * Creates printable view of the service
	 * 
	 * @param aoServiceQuestionsBean - Service Question Bean of the service
	 * @param aoServiceDocumentList - Document list corresponding to service
	 * @param aoStaffDetailList - List of Staff details mapped for the service
	 * @param aoContractDetailsList - List of contract details mapped for the
	 *            service
	 * @param aoServiceSettingList - List of service setting/Specialization
	 *            selected for the service
	 * @param asServiceName - Service Name
	 * @param abIsFinalView - Flag depicting whether to generate final view or
	 *            normal view
	 * @param aoDoc - jdom of cached taxonomy
	 * @return content to be displayed
	 * @throws ApplicationException
	 */
	public static StringBuffer createServicePrintableView(ServiceQuestions aoServiceQuestionsBean,
			List<DocumentBean> aoServiceDocumentList, List<StaffDetails> aoStaffDetailList,
			List<ContractDetails> aoContractDetailsList, List<ServiceSettingBean> aoServiceSettingList,
			String asServiceName, Boolean abIsFinalView, Document aoDoc) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: createServicePrintableView :: start");
		StringBuffer loPrintableContent = new StringBuffer();
		try
		{
			loPrintableContent.append("<div class='headingTextMainHeader'><font class='headingFont mainHeader'>");
			loPrintableContent.append("Service");
			loPrintableContent.append(": </font><font class='sectionHeading'>");
			loPrintableContent.append(asServiceName);
			loPrintableContent.append("</font></div>");
			loPrintableContent.append("<div class='headingText'><font class='headingFont'>");
			loPrintableContent.append(ApplicationConstants.QUESTION_DISPLAY);
			loPrintableContent.append("</font></div>");
			if (aoServiceQuestionsBean != null)
			{
				loPrintableContent.append(getQuestionsContractStaffService(aoServiceQuestionsBean, aoStaffDetailList,
						aoContractDetailsList));
			}
			else
			{
				loPrintableContent.append(getNoContentData());
			}
			boolean lbShowDocumentSection = true;
			lbShowDocumentSection = isContractTypeSingleNYCGovernment(aoContractDetailsList);
			if (!lbShowDocumentSection)
			{
				loPrintableContent.append("<div class='headingText'><font class='headingFont'>");
				loPrintableContent.append(ApplicationConstants.DOCUMENTS_DISPLAY);
				loPrintableContent.append("</font></div>");

				if (aoServiceDocumentList != null)
				{
					loPrintableContent.append(getFormDocument(aoServiceDocumentList, abIsFinalView));
				}
				else
				{
					loPrintableContent.append(getNoContentData());
				}
			}
			if (aoServiceSettingList != null)
			{
				getServiceSettingSpecializationContent(aoServiceSettingList, loPrintableContent, aoDoc);
			}
			else
			{
				loPrintableContent.append("<div class='headingText'><font class='headingFont'>");
				loPrintableContent.append(ApplicationConstants.SPECIALIZATION_DISPLAY);
				loPrintableContent.append("</font></div>");
				loPrintableContent.append(getNoContentData());
				loPrintableContent.append("<div class='headingText'><font class='headingFont'>");
				loPrintableContent.append(ApplicationConstants.SERVICE_SETTING_DISPLAY);
				loPrintableContent.append("</font></div>");
				loPrintableContent.append(getNoContentData());
			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Error occured while creating printer friendly view for services", aoExp);
		}
		LOG_OBJECT.Debug("Inside :: createServicePrintableView :: end");
		return loPrintableContent;
	}

	/**
	 * This method returns a flag to identify whether or not Contract added in
	 * the Service Application is of type NYC Government The flag returned will
	 * be true of there is only a single contract added and its type is
	 * "NYC Government"
	 * @param aoContractDetailsList
	 * @return
	 */
	public static Boolean isContractTypeSingleNYCGovernment(List<ContractDetails> aoContractDetailsList)
	{
		int liNYCGovtCounter = 0;
		Iterator<ContractDetails> loContractListIterator = aoContractDetailsList.iterator();
		while (loContractListIterator.hasNext())
		{
			ContractDetails loContract = (ContractDetails) loContractListIterator.next();
			if (null != loContract.getMsContractType() && loContract.getMsContractType().trim().length() > 0
					&& loContract.getMsContractType().equalsIgnoreCase("NYC Government"))
			{
				liNYCGovtCounter++;
			}
		}
		// Added for release 5
		return (liNYCGovtCounter >= 1 && aoContractDetailsList.size() == liNYCGovtCounter);
	}// Added for release 5

	/**
	 * Gets the service setting/Specialization content to be displayed
	 * 
	 * @param aoServiceSettingList - List of service setting/Specialization
	 *            selected for the service
	 * @param asbPrintableContent - StringBuffer of printable content
	 * @param aoDoc - jdom of cached taxonomy
	 * @throws ApplicationException
	 */
	private static void getServiceSettingSpecializationContent(List<ServiceSettingBean> aoServiceSettingList,
			StringBuffer asbPrintableContent, Document aoDoc) throws ApplicationException
	{
		StringBuffer loSpecialization = new StringBuffer();
		StringBuffer loServiceSetting = new StringBuffer();
		StringBuffer loSpecializationAnswer = new StringBuffer();
		StringBuffer loServiceSettingAnswer = new StringBuffer();
		for (ServiceSettingBean loBean : aoServiceSettingList)
		{
			String lsElementId = loBean.getElementId();
			if (lsElementId != null)
			{
				if (loBean.getMsElementType().equalsIgnoreCase(ApplicationConstants.SPECIALIZATION))
				{
					loSpecializationAnswer.append("- ");
					loSpecializationAnswer.append(getTaxonomyName(lsElementId, aoDoc));
					loSpecializationAnswer.append("<br />");
				}
				else
				{
					loServiceSettingAnswer.append("- ");
					loServiceSettingAnswer.append(getTaxonomyName(lsElementId, aoDoc));
					loServiceSettingAnswer.append("<br />");
				}
			}
			else
			{
				String lsNoSetting = loBean.getNoSettingFlag();
				if (lsNoSetting.equalsIgnoreCase("Y"))
				{
					if (loBean.getMsElementType().equalsIgnoreCase(ApplicationConstants.SPECIALIZATION))
					{
						loSpecializationAnswer.append("- ");
						loSpecializationAnswer.append(ApplicationConstants.PRINT_SPECIALIZATION_NO_MESSAGE);
					}
					else
					{
						loServiceSettingAnswer.append("- ");
						loSpecializationAnswer.append(ApplicationConstants.PRINT_SERVICE_SETTING_NO_MESSAGE);
					}
				}
			}
		}
		if (loSpecializationAnswer.length() > 0)
		{
			String lsQuestion = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE,
					ApplicationConstants.SPECIALIZATION);
			loSpecialization.append("<div class='headingText'><font class='headingFont'>");
			loSpecialization.append(ApplicationConstants.SPECIALIZATION_DISPLAY);
			loSpecialization.append("</font></div>");
			loSpecialization.append("<table><tr><td class='questions'>");
			loSpecialization.append(lsQuestion);
			loSpecialization.append("</td><td>");
			loSpecialization.append(loSpecializationAnswer);
			loSpecialization.append("</td></tr></table>");
		}
		else
		{
			loSpecialization.append("<div class='headingText'><font class='headingFont'>");
			loSpecialization.append(ApplicationConstants.SPECIALIZATION_DISPLAY);
			loSpecialization.append("</font></div>");
			loSpecialization.append(getNoContentData());
		}
		if (loServiceSettingAnswer.length() > 0)
		{
			String lsQuestion = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE,
					ApplicationConstants.SERVICE_SETTING);
			loServiceSetting.append("<div class='headingText'><font class='headingFont'>");
			loServiceSetting.append(ApplicationConstants.SERVICE_SETTING_DISPLAY);
			loServiceSetting.append("</font></div>");
			loServiceSetting.append("<table><tr><td class='questions'>");
			loServiceSetting.append(lsQuestion);
			loServiceSetting.append("</td><td>");
			loServiceSetting.append(loServiceSettingAnswer);
			loServiceSetting.append("</td></tr></table>");
		}
		else
		{
			loServiceSetting.append("<div class='headingText'><font class='headingFont'>");
			loServiceSetting.append(ApplicationConstants.SERVICE_SETTING_DISPLAY);
			loServiceSetting.append("</font></div>");
			loServiceSetting.append(getNoContentData());
		}
		asbPrintableContent.append(loSpecialization);
		asbPrintableContent.append(loServiceSetting);
	}

	/**
	 * Gets Questions/Contract/Staff for print
	 * 
	 * @param aoServiceQuestionsBean - Service Question Bean of the service
	 * @param aoStaffDetailList - List of Staff details mapped for the service
	 * @param aoContractDetailsList - List of contract details mapped for the
	 *            service
	 * @return content of Questions/Contract/Staff for print
	 * @throws ApplicationException
	 */
	private static StringBuffer getQuestionsContractStaffService(ServiceQuestions aoServiceQuestionsBean,
			List<StaffDetails> aoStaffDetailList, List<ContractDetails> aoContractDetailsList)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: getQuestionsContractStaffService :: start");
		StringBuffer loPrintableContent = new StringBuffer();
		StringBuffer loPrintableContentContractStaff = new StringBuffer();
		ResourceBundle loRBPrint = PropertyLoader.getProperties(ApplicationConstants.SERVICE_PRINT_PROP_FILE);
		try
		{
			if (aoServiceQuestionsBean.getMsQuestion1() != null)
			{
				String lsAnswer1 = aoServiceQuestionsBean.getMsQuestion1();
				String lsQuestion1 = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE,
						"msQuestion1");
				loPrintableContent.append("<tr><td class='questions'>");
				loPrintableContent.append(lsQuestion1);
				loPrintableContent.append("</td><td>");
				loPrintableContent.append(toTitleCase(lsAnswer1));
				loPrintableContent.append("</td></tr>");
				if (lsAnswer1 != null && lsAnswer1.equalsIgnoreCase("yes"))
				{
					if (aoContractDetailsList != null && !aoContractDetailsList.isEmpty())
					{
						getContractData(aoContractDetailsList, loPrintableContentContractStaff, loRBPrint);
					}
					else
					{
						loPrintableContentContractStaff.append("<div class='headingText'><font class='headingFont'>");
						loPrintableContentContractStaff.append(ApplicationConstants.CONTRACT_GRANT_DISPLAY);
						loPrintableContentContractStaff.append("</font></div>");
						loPrintableContentContractStaff.append(getNoContentData());
					}
				}
				else
				{
					if (aoServiceQuestionsBean.getMsQuestion2() != null
							&& aoServiceQuestionsBean.getMsQuestion2().equalsIgnoreCase("yes"))
					{
						getServiceQuestionData(aoServiceQuestionsBean, aoStaffDetailList, loPrintableContent,
								loPrintableContentContractStaff, loRBPrint);
					}
					else
					{
						String lsAnswer2 = aoServiceQuestionsBean.getMsQuestion1();
						String lsQuestion2 = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE,
								"msQuestion2");
						loPrintableContent.append("<tr><td class='questions'>");
						loPrintableContent.append(lsQuestion2);
						loPrintableContent.append("</td><td>");
						loPrintableContent.append(toTitleCase(lsAnswer2));
						loPrintableContent.append("</td></tr>");
						if (aoServiceQuestionsBean.getMsQuestion3() != null
								&& !aoServiceQuestionsBean.getMsQuestion3().equalsIgnoreCase("0")
								&& !aoServiceQuestionsBean.getMsQuestion3().equalsIgnoreCase("nothing"))
						{
							String lsAnswer3 = aoServiceQuestionsBean.getMsQuestion3();
							String lsQuestion3 = PropertyLoader.getProperty(
									ApplicationConstants.SERVICE_PRINT_PROP_FILE, "msQuestion3");
							loPrintableContent.append("<tr><td class='questions'>");
							loPrintableContent.append(lsQuestion3);
							loPrintableContent.append("</td><td>");
							loPrintableContent.append(toTitleCase(lsAnswer3));
							loPrintableContent.append("</td></tr>");
						}
					}
				}
			}
			else
			{
				loPrintableContent.append(getNoContentData());
			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(
					"Error occured while creating printer friendly view for services - Questions", aoExp);
		}
		if (loPrintableContent.length() > 0)
		{
			loPrintableContent.insert(0, "<table>").append("</table>");
		}
		else
		{
			loPrintableContent.append(getNoContentData());
		}
		LOG_OBJECT.Debug("Inside :: getQuestionsContractStaffService :: end");
		return loPrintableContent.append(loPrintableContentContractStaff);
	}

	/**
	 * Gets Questions data for print
	 * 
	 * @param aoServiceQuestionsBean - Service Question Bean of the service
	 * @param aoStaffDetailList - List of Staff details mapped for the service
	 * @param aoPrintableContent - StringBuffer of printable content
	 * @param aoPrintableContentContractStaff - StringBuffer of printable
	 *            content contract/staff
	 * @param aoRBPrint - Resource Bundle of Service Headers and question labels
	 * @throws ApplicationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 */

	private static void getServiceQuestionData(ServiceQuestions aoServiceQuestionsBean,
			List<StaffDetails> aoStaffDetailList, StringBuffer aoPrintableContent,
			StringBuffer aoPrintableContentContractStaff, ResourceBundle aoRBPrint) throws ApplicationException,
			IllegalAccessException, InvocationTargetException, IntrospectionException
	{
		String lsAnswer2 = aoServiceQuestionsBean.getMsQuestion2();
		String lsQuestion2 = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE, "msQuestion2");
		aoPrintableContent.append("<tr><td class='questions'>");
		aoPrintableContent.append(lsQuestion2);
		aoPrintableContent.append("</td><td>");
		aoPrintableContent.append(toTitleCase(lsAnswer2));
		aoPrintableContent.append("</td></tr>");
		if (lsAnswer2.equalsIgnoreCase("yes"))
		{
			if (aoStaffDetailList != null && !aoStaffDetailList.isEmpty())
			{
				for (StaffDetails loStaffDetail : aoStaffDetailList)
				{
					Object loFirstName = new PropertyDescriptor("msStaffFirstName", loStaffDetail.getClass())
							.getReadMethod().invoke(loStaffDetail);
					Object loMiddleName = new PropertyDescriptor("msStaffMidInitial", loStaffDetail.getClass())
							.getReadMethod().invoke(loStaffDetail);
					Object loLastName = new PropertyDescriptor("msStaffLastName", loStaffDetail.getClass())
							.getReadMethod().invoke(loStaffDetail);
					StringBuffer loName = new StringBuffer();
					if (loFirstName != null)
					{
						loName.append(loFirstName);
						loName.append(" ");
					}
					if (loMiddleName != null)
					{
						loName.append(loMiddleName);
						loName.append(" ");
					}
					if (loLastName != null)
					{
						loName.append(loLastName);
						loName.append(" ");
					}
					StringBuffer loPrintContentStaff = new StringBuffer();
					loPrintContentStaff.append(getColumnData(aoRBPrint, "msStaffFirstName", loStaffDetail));
					loPrintContentStaff.append(getColumnData(aoRBPrint, "msStaffMidInitial", loStaffDetail));
					loPrintContentStaff.append(getColumnData(aoRBPrint, "msStaffLastName", loStaffDetail));
					loPrintContentStaff.append(getColumnData(aoRBPrint, "msStaffTitle", loStaffDetail));
					loPrintContentStaff.append(getColumnData(aoRBPrint, "msStaffPhone", loStaffDetail));
					loPrintContentStaff.append(getColumnData(aoRBPrint, "msStaffEmail", loStaffDetail));
					loPrintContentStaff.insert(0, "<table>").append("</table>");
					StringBuffer loPrintContentStaffHeader = new StringBuffer();
					loPrintContentStaffHeader.append("<div class='headingText'><font class='headingFont'>");
					loPrintContentStaffHeader.append(ApplicationConstants.KEY_STAFF_DISPLAY);
					loPrintContentStaffHeader.append(": </font><font class='sectionHeading'>");
					loPrintContentStaffHeader.append(loName);
					loPrintContentStaffHeader.append("</font></div>");
					loPrintContentStaff.insert(0, loPrintContentStaffHeader);
					aoPrintableContentContractStaff.append(loPrintContentStaff);
				}
			}
			else
			{
				aoPrintableContentContractStaff.append("<div class='headingText'><font class='headingFont'>");
				aoPrintableContentContractStaff.append(ApplicationConstants.KEY_STAFF_DISPLAY);
				aoPrintableContentContractStaff.append("</font></div>");
				aoPrintableContentContractStaff.append(getNoContentData());
			}
		}
	}

	/**
	 * Generates Contract data for print
	 * 
	 * @param aoContractDetailsList - List of contract details mapped for the
	 *            service
	 * @param asbPrintableContentContractStaff - StringBuffer of printable
	 *            content contract/staff
	 * @param aoRBPrint - Resource Bundle of Service Headers and question labels
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IntrospectionException
	 * @throws ApplicationException
	 */
	private static void getContractData(List<ContractDetails> aoContractDetailsList,
			StringBuffer asbPrintableContentContractStaff, ResourceBundle aoRBPrint) throws IllegalAccessException,
			InvocationTargetException, IntrospectionException, ApplicationException
	{
		for (ContractDetails loContractDetail : aoContractDetailsList)
		{
			Object loNYCName = new PropertyDescriptor("msContractNYCAgency", loContractDetail.getClass())
					.getReadMethod().invoke(loContractDetail);
			Object loFunderName = new PropertyDescriptor("msContractFunderName", loContractDetail.getClass())
					.getReadMethod().invoke(loContractDetail);
			Object loContractId = new PropertyDescriptor("msContractID", loContractDetail.getClass()).getReadMethod()
					.invoke(loContractDetail);
			Object loDescription = new PropertyDescriptor("msContractDescription", loContractDetail.getClass())
					.getReadMethod().invoke(loContractDetail);
			StringBuffer loSBHeading = new StringBuffer();
			if (loNYCName != null && !loNYCName.toString().isEmpty())
			{
				loSBHeading.append(loNYCName);
			}
			else if (loFunderName != null && !loFunderName.toString().isEmpty())
			{
				loSBHeading.append(loFunderName);
			}
			if (loContractId != null && !loContractId.toString().isEmpty())
			{
				loSBHeading.append(" - ");
				loSBHeading.append(loContractId);
			}
			if (loDescription != null && !loDescription.toString().isEmpty())
			{
				loSBHeading.append(" - ");
				loSBHeading.append(loDescription);
			}
			StringBuffer loPrintContentContract = new StringBuffer();
			Object loObject = new PropertyDescriptor("msContractType", loContractDetail.getClass()).getReadMethod()
					.invoke(loContractDetail);
			loPrintContentContract.append(getColumnData(aoRBPrint, "msContractType", loContractDetail));
			if (loObject != null && ((String) loObject).equalsIgnoreCase("NYC Government"))
			{
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractNYCAgency", loContractDetail));
			}
			if (loObject != null && !((String) loObject).equalsIgnoreCase("NYC Government"))
			{
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractFunderName", loContractDetail));
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractRefFirstName", loContractDetail));
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractRefMidName", loContractDetail));
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractRefLastName", loContractDetail));
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractRefTitle", loContractDetail));
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractRefPhone", loContractDetail));
				loPrintContentContract.append(getColumnData(aoRBPrint, "msContractRefEmail", loContractDetail));
			}
			loPrintContentContract.append(getColumnData(aoRBPrint, "msContractID", loContractDetail));
			loPrintContentContract.append(getColumnData(aoRBPrint, "msContractDescription", loContractDetail));
			loPrintContentContract.append(getColumnData(aoRBPrint, "msContractStartDate", loContractDetail));
			loPrintContentContract.append(getColumnData(aoRBPrint, "msContractEndDate", loContractDetail));
			loPrintContentContract.append(getColumnData(aoRBPrint, "msContractBudget", loContractDetail));
			loPrintContentContract.insert(0, "<table>").append("</table>");
			StringBuffer loPrintContentContractHeader = new StringBuffer();
			loPrintContentContractHeader.append("<div class='headingText'><font class='headingFont'>");
			loPrintContentContractHeader.append(ApplicationConstants.CONTRACT_GRANT_DISPLAY);
			loPrintContentContractHeader.append(": </font><font class='sectionHeading'>");
			loPrintContentContractHeader.append(loSBHeading.toString());
			loPrintContentContractHeader.append("</font></div>");
			loPrintContentContract.insert(0, loPrintContentContractHeader);
			asbPrintableContentContractStaff.append(loPrintContentContract);
		}
	}

	/**
	 * Generates Column data for Contract/Staff
	 * 
	 * @param aoRBPrint - Resource Bundle of Service Headers and question labels
	 * @param asColumneName - Key in property file
	 * @param aoObject - Data to be added
	 * @return StringBuffer corresponding to Column
	 * @throws ApplicationException
	 */
	private static StringBuffer getColumnData(ResourceBundle aoRBPrint, String asColumneName, Object aoObject)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: getColumnData :: start");
		StringBuffer loSbObj = new StringBuffer();
		try
		{
			Object loObject = new PropertyDescriptor(asColumneName, aoObject.getClass()).getReadMethod().invoke(
					aoObject);
			String lsQuestion = PropertyLoader.getProperty(ApplicationConstants.SERVICE_PRINT_PROP_FILE, asColumneName);
			if (loObject != null)
			{
				if (loObject.getClass().toString().contains("java.sql.Date"))
				{
					loObject = DateUtil.getDateMMddYYYYFormat((Date) loObject);
				}
				String lsHeading = null;
				if (aoRBPrint.containsKey(asColumneName + "_heading"))
				{
					lsHeading = aoRBPrint.getString(asColumneName + "_heading");
				}
				if (lsHeading != null && lsHeading.length() > 0)
				{
					loSbObj.append("<tr><td colspan='2' class='subheading'>");
					loSbObj.append(lsHeading);
					loSbObj.append("</td></tr>");
				}
				if (null != lsQuestion && "Phone Number:".equals(lsQuestion))
				{
					loSbObj.append("<tr><td class='questions'>");
					loSbObj.append(lsQuestion);
					loSbObj.append("</td><td>");
					loSbObj.append("<div class='printerFriendlyPhone'>");
					loSbObj.append(loObject);
					loSbObj.append("</div>");
					loSbObj.append("</td></tr>");
				}
				else
				{
					loSbObj.append("<tr><td class='questions'>");
					loSbObj.append(lsQuestion);
					loSbObj.append("</td><td>");
					loSbObj.append(loObject);
					loSbObj.append("</td></tr>");
				}

			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(
					"Error occured while creating printer friendly view for services - contract/staff", aoExp);
		}
		LOG_OBJECT.Debug("Inside :: getColumnData :: end");
		return loSbObj;
	}

	/**
	 * Gets complete status map for service
	 * 
	 * @param aoStatusBeanMap - Map of Section Status
	 * @throws ApplicationException
	 */
	public static void getCompleteStatusMapService(Map<String, StatusBean> aoStatusBeanMap) throws ApplicationException
	{
		for (Entry<String, StatusBean> loEntry : aoStatusBeanMap.entrySet())
		{
			StatusBean loStatusBean = loEntry.getValue();
			Map<String, String> loHMMapStatus = loStatusBean.getMoHMSubSectionDetails();
			Map<String, String> loHMMapStatusToDisplay = loStatusBean.getMoHMSubSectionDetailsToDisplay();
			for (String lsSubSecName : ApplicationConstants.SERVICE_SUB_SECTION_NAMES_MAPPING)
			{
				String lsSubSectionStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replace(" ", "");
				loHMMapStatus.remove(null);
				loHMMapStatusToDisplay.remove(null);
				if (!loHMMapStatus.containsKey(lsSubSecName) && null != lsSubSecName
						&& !"null".equalsIgnoreCase(lsSubSecName))
				{
					loHMMapStatus.put(lsSubSecName, lsSubSectionStatus);
					loHMMapStatusToDisplay.put(lsSubSecName, ApplicationConstants.NOT_STARTED_STATE);
				}
			}
			loStatusBean.setMsSectionStatus(getSectionStatusService(loHMMapStatus));
		}
	}

	/**
	 * Get Section status for service
	 * 
	 * @param aoHMMapStatus - Map of Sub section name and its status
	 * @return Section status for service
	 * @throws ApplicationException
	 */
	private static String getSectionStatusService(Map<String, String> aoHMMapStatus) throws ApplicationException
	{
		String lsNotStartedStatus = ApplicationConstants.NOT_STARTED_STATE.toLowerCase().replace(" ", "");
		String lsDraftStatus = ApplicationConstants.DRAFT_STATE.toLowerCase().replace(" ", "");
		String lsCompleteStatus = ApplicationConstants.COMPLETED_STATE.toLowerCase().replace(" ", "");
		String lsStatus = "";
		try
		{
			for (String lsSubSection : ApplicationConstants.SERVICE_SUB_SECTION_NAMES_MAPPING)
			{
				String lsSubSectionStatus = aoHMMapStatus.get(lsSubSection);
				if (lsSubSectionStatus == null)
				{
					lsStatus = lsNotStartedStatus;
				}
				else
				{
					if (lsSubSectionStatus.equalsIgnoreCase(lsDraftStatus)
							|| (lsSubSectionStatus.equalsIgnoreCase(lsCompleteStatus) && lsStatus
									.equalsIgnoreCase(lsNotStartedStatus))
							|| (lsSubSectionStatus.equalsIgnoreCase(lsNotStartedStatus) && lsStatus
									.equalsIgnoreCase(lsCompleteStatus)))
					{
						lsStatus = lsDraftStatus;
						break;
					}
					else
					{
						lsStatus = lsSubSectionStatus;
					}
				}

			}
			if (lsStatus.isEmpty())
			{
				lsStatus = lsNotStartedStatus;
			}
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Some error occured while creating status map for section", aoExp);
		}
		return lsStatus;
	}

	/**
	 * Check if application/service is readonly based on there statuses
	 * 
	 * @param asBusinessApplicationId - Business Application Id
	 * @param asStatus - application/service status
	 * @param asSectionId - Section Id
	 * @param asServiceApplicationId - Service Application Id
	 * @param asApplicationType - Application Type(business/service)
	 * @param asOrgnizationType - Organization Type
	 * @param asPermissionType - Permission Type
	 * @return
	 */
	// R5 code start
	public static Boolean doCheckReadOnly(final String asBusinessApplicationId, final String asStatus,
			final String asSectionId, final String asServiceApplicationId, final String asApplicationType,
			final String asOrgnizationType, String asPermissionType)
	{
		// Added : R5 Condition for Agency user
		if (asOrgnizationType != null
				&& (asOrgnizationType.equalsIgnoreCase("city_org")
						|| asOrgnizationType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG) || (asPermissionType != null
						&& asOrgnizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG) && asPermissionType
							.equalsIgnoreCase(ApplicationConstants.ROLE_READ_ONLY))))
		{
			return true;
		}
		else
		{
			if (asApplicationType != null
					&& (asApplicationType.equalsIgnoreCase("business")
							|| asApplicationType
									.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
							|| asApplicationType
									.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS)
							|| asApplicationType
									.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BOARD) || asApplicationType
								.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES))
					|| asServiceApplicationId != null)
			{
				if (asStatus != null && !asStatus.equalsIgnoreCase("null"))
				{
					if (asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.PARTIALLY_COMPLETE_STATE)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)
							|| (ApplicationConstants.FINAL_VIEW_STATUSES.contains(asStatus.toLowerCase()) && (asApplicationType
									.equalsIgnoreCase("business") || asApplicationType.equalsIgnoreCase("service"))))
					{
						return false;
					}
					else if (asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED))
					{
						return BusinessApplicationUtil.doCheckReadOnly(asBusinessApplicationId, asSectionId, null,
								null, "", asOrgnizationType, asPermissionType);
					}
					else
					{
						return true;
					}
				}
			}
			else
			{
				if (asStatus != null && !asStatus.equalsIgnoreCase("null"))
				{
					if (asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.PARTIALLY_COMPLETE_STATE)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE))
					{
						return false;
					}
					else if (asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN)
							|| asStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED))
					{
						return BusinessApplicationUtil.doCheckReadOnly(asBusinessApplicationId, asSectionId, null,
								null, "", asOrgnizationType, asPermissionType);
					}
					else
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	// R5 code ends
	/**
	 * @return application id
	 */
	public static String generatAppId()
	{
		long lsTimeStamp = System.currentTimeMillis();
		StringBuffer loSbBrAppId = new StringBuffer("app_");
		loSbBrAppId.append(lsTimeStamp);
		return loSbBrAppId.toString();
	}

	/**
	 * @return org name change id
	 */
	public static String generateReqId()
	{
		long lsTimeStamp = System.currentTimeMillis();
		StringBuffer loSbBrAppId = new StringBuffer("onc_");
		loSbBrAppId.append(lsTimeStamp);
		return loSbBrAppId.toString();
	}

	/**
	 * Convert String to Title Case
	 * 
	 * @param asInput - String to be converted to title case
	 * @return String converted to title case
	 */
	public static String toTitleCase(String asInput)
	{
		StringBuilder loTitleCase = new StringBuilder();
		boolean lbNextTitleCase = true;
		for (char loChar : asInput.toCharArray())
		{
			if (Character.isSpaceChar(loChar))
			{
				lbNextTitleCase = true;
			}
			else if (lbNextTitleCase)
			{
				loChar = Character.toTitleCase(loChar);
				lbNextTitleCase = false;
			}
			loTitleCase.append(loChar);
		}
		return loTitleCase.toString();
	}

	/**
	 * Checks if business Application is completely filled
	 * 
	 * @param aoBusinessAppM - Map of Business Application Section Statuses
	 * @return boolean flag indicating if application is complete
	 */
	public static boolean isBusinessApplicationComplete(Map<String, StatusBean> aoBusinessAppM)
	{
		List<String> loSectionStatusList = new ArrayList<String>();
		loSectionStatusList.add(ApplicationConstants.NOT_STARTED_STATE.replace(" ", "").toLowerCase());
		loSectionStatusList.add(ApplicationConstants.DRAFT_STATE.replace(" ", "").toLowerCase());
		for (Entry<String, StatusBean> loEntry : aoBusinessAppM.entrySet())
		{
			String lsSectionStatus = loEntry.getValue().getMsSectionStatus();
			if (loSectionStatusList.contains(lsSectionStatus))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the readonly status
	 * 
	 * @param asBussAppStatus - Current Buss App status
	 * @return boolean flag indicating if application has to be readonly
	 */
	public static boolean getBusinessApplicationReadOnlyStatus(String asBussAppStatus)
	{
		Boolean lbBussAppStatus = false;
		if (null != asBussAppStatus
				&& !(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(asBussAppStatus)
						|| ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(asBussAppStatus) || ApplicationConstants.STATUS_DEFFERED
							.equalsIgnoreCase(asBussAppStatus)))
		{
			lbBussAppStatus = true;
		}
		return lbBussAppStatus;
	}

	/**
	 * Gets the service application Status
	 * 
	 * @param asBussAppStatus - Current Buss App status
	 * @return boolean flag indicating if service has to be skipped
	 */
	public static boolean getServiceApplicationStatus(String asBussAppStatus)
	{
		Boolean lbBussAppStatus = false;
		if (null == asBussAppStatus)
		{
			return true;
		}
		if (asBussAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT)
				|| asBussAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
				|| asBussAppStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)
				|| asBussAppStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)
				|| asBussAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
				|| asBussAppStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
		{
			lbBussAppStatus = true;
		}
		return lbBussAppStatus;
	}

	/**
	 * Converts Map to String
	 * 
	 * @param aoMap - Map to be converted to String
	 * @return String converted from Map
	 */
	public static String convertMapToString(Map<String, Object> aoMap)
	{
		StringBuffer loSbOutput = new StringBuffer();
		for (Entry<String, Object> lsKey : aoMap.entrySet())
		{
			loSbOutput.append(lsKey.getKey());
			loSbOutput.append(ApplicationConstants.KEY_SEPARATOR);
			loSbOutput.append(lsKey.getValue());
			loSbOutput.append("d!ffv@lu3S3p@r@t0r");
		}
		return loSbOutput.toString();
	}

	/**
	 * Converts String to Map
	 * 
	 * @param asData - String to be converted to Map
	 * @return Map converted from String
	 */
	public static Map<String, Object> convertStringToMap(String asData)
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		String loArrayRow[] = asData.split("d!ffv@lu3S3p@r@t0r");
		for (String lsRow : loArrayRow)
		{
			String loArrayKeyValue[] = lsRow.split(ApplicationConstants.KEY_SEPARATOR);
			if (loArrayKeyValue.length == 2)
			{
				loMap.put(loArrayKeyValue[0], loArrayKeyValue[1]);
			}
		}
		return loMap;
	}

	/**
	 * Converts Input Stream to string
	 * 
	 * @param aoInputStream - Input Stream of content
	 * @return Converted Stream to string
	 */
	public static String convertStreamToString(java.io.InputStream aoInputStream)
	{
		try
		{
			return new java.util.Scanner(aoInputStream).useDelimiter("\\A").next();
		}
		catch (java.util.NoSuchElementException aoExp)
		{
			return "";
		}
	}

	/**
	 * Copies a map to other
	 * 
	 * @param aoMapToRender - copy map from
	 * @param aoReturnMapToRender - copy map to
	 */
	public static void setIntoMapForRender(Map<String, Object> aoMapToRender, Map<String, Object> aoReturnMapToRender)
	{
		if (aoMapToRender != null && !aoMapToRender.isEmpty())
		{
			for (Map.Entry<String, Object> loEntry : aoMapToRender.entrySet())
			{
				aoReturnMapToRender.put(loEntry.getKey(), loEntry.getValue());
			}
		}
	}

	/**
	 * Crates tree structure for Service Specialization and Add services
	 * 
	 * @param aoDoc - jdom of Taxonomy
	 * @param aoList - List of element
	 * @param asType - contains value -> button/checkbox to differentiate
	 *            between type for which function is called
	 * @return Tree Structure as String
	 * @throws Exception
	 */
	public static String getTree(Document aoDoc, List<Element> aoList, String asType) throws Exception
	{
		String lsParentId = null;
		Element loNewElt = null;
		List<Element> loSortedList = null;
		if (!aoList.isEmpty())
		{
			loSortedList = new ArrayList<Element>();
			loNewElt = (Element) aoList.get(0).clone();
			lsParentId = loNewElt.getAttributeValue("id");
			List<Element> loMainEltChildList = loNewElt.getChildren("element");
			loSortedList.addAll(loMainEltChildList);
			for (int liChildCount = 1; liChildCount < aoList.size(); liChildCount++)
			{
				Element loElt = aoList.get(liChildCount);
				List<Element> loChildren = loElt.getChildren("element");
				loSortedList.addAll(loChildren);
			}
			Collections.sort(loSortedList, new ElementComparator());

		}
		Element loRoot = new Element("ul");
		String lsReturn = "";
		if (lsParentId != null)
		{
			loRoot.setAttribute("id", lsParentId);
			loRoot.setAttribute("class", "ulTreeClass");
			Document loHTMLDoc = new Document(loRoot);
			convertChildren(loSortedList, loHTMLDoc, asType, lsParentId);
			lsReturn = XMLUtil.getXMLAsString(loHTMLDoc);
		}
		else
		{
			lsReturn = "No Data found";
		}
		return lsReturn;
	}

	/**
	 * Converts history to XML
	 * 
	 * @param asBusAppId - Business application id
	 * @throws ApplicationException
	 */
	public static String convertHistoryToXML(String asBusAppId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData("asBusAppId", asBusAppId);
		TransactionManager.executeTransaction(loChannel, "fetchAppAllSectionStatus");
		String lsFilePath = "/com/nyc/hhs/config/HistoryStructure.xml";
		Document loDom = XMLUtil.getDomObj(BusinessApplicationUtil.class.getResourceAsStream(lsFilePath));
		List<Map<String, Object>> loSectionList = (List<Map<String, Object>>) loChannel.getData("loSectionStatusMap");
		for (Map<String, Object> loSectionMap : loSectionList)
		{
			String lsSectionName = (String) loSectionMap.get("SECTION_ID");
			Element loRootElt = loDom.getRootElement();
			if (loRootElt.getAttributeValue("status") != null && loRootElt.getAttributeValue("status").length() <= 0)
			{
				loRootElt.setAttribute("status", convertToString(loSectionMap.get("APPLICATION_STATUS")));
				loRootElt.setAttribute("updateBy", ApplicationConstants.ACCELERATOR);
				loRootElt.setAttribute("date", convertToString(loSectionMap.get("MODIFIED_DATE")));
				loRootElt.setAttribute("comment", convertToString(loSectionMap.get("BUSINESS_PROVIDER_COMMENT")));
			}
			Element loCurrentSectionElt = loRootElt.getChild(lsSectionName);
			if (loSectionMap.get("TYPE") != null
					&& convertToString(loSectionMap.get("TYPE")).equalsIgnoreCase("SUBSECTION"))
			{
				if (loCurrentSectionElt.getAttributeValue("status") != null
						&& !loCurrentSectionElt.getAttributeValue("status").isEmpty())
				{
					loCurrentSectionElt.setAttribute("status", convertToString(loSectionMap.get("SECTION_STATUS")));
					loCurrentSectionElt.setAttribute("updateBy", ApplicationConstants.ACCELERATOR);
					loCurrentSectionElt.setAttribute("date", convertToString(loSectionMap.get("MODIFIED_DATE")));
					loCurrentSectionElt.setAttribute("comment",
							convertToString(loSectionMap.get("SECTION_PROVIDER_COMMENT")));
				}
				if (loSectionMap.get("SUB_SECTION_ID") != null
						&& !convertToString(loSectionMap.get("SUB_SECTION_ID")).equalsIgnoreCase(
								ApplicationConstants.DOCUMENT_LIST)
						&& loSectionMap.get("SUB_SECTION_STATUS") != null
						&& convertToString(loSectionMap.get("SUB_SECTION_STATUS")).equalsIgnoreCase(
								ApplicationConstants.STATUS_RETURNED))
				{
					appendReturnedDocs(loCurrentSectionElt,
							toTitleCase(convertToString(loSectionMap.get("SUB_SECTION_ID"))));
				}
			}
			else if (loSectionMap.get("TYPE") != null
					&& convertToString(loSectionMap.get("TYPE")).equalsIgnoreCase("DOCUMENT"))
			{
				if (loSectionMap.get("DOCUMENT_STATUS") != null
						&& convertToString(loSectionMap.get("DOCUMENT_STATUS")).equalsIgnoreCase(
								ApplicationConstants.STATUS_RETURNED))
				{
					appendReturnedDocs(loCurrentSectionElt, convertToString(loSectionMap.get("DOCUMENT_TITLE")));
				}
			}

		}
		return XMLUtil.getXMLAsString(loDom);
	}

	/**
	 * Convert Object to string
	 * 
	 * @param aoObj - object to be converted to String
	 * @return
	 */
	private static String convertToString(Object aoObj)
	{
		if (aoObj != null)
		{
			return (String) aoObj;
		}
		else
		{
			return "";
		}
	}

	/**
	 * Appends data to returned
	 * 
	 * @param aoObj - object to be converted to String
	 * @return
	 */
	private static void appendReturnedDocs(Element aoCurrentSectionElt, String asValueToAppend)
	{
		String lsValue = aoCurrentSectionElt.getAttributeValue("returnedDocuments");
		String lsAttributeValue = "";
		if (lsValue.equalsIgnoreCase("N/A"))
		{
			lsAttributeValue = asValueToAppend;
		}
		else if (asValueToAppend.equalsIgnoreCase(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
		{
			lsAttributeValue = asValueToAppend + ", " + lsValue;
		}
		else
		{
			lsAttributeValue = lsValue + ", " + asValueToAppend;
		}
		aoCurrentSectionElt.setAttribute("returnedDocuments", lsAttributeValue);
	}

	/**
	 * This is a comparator of type Element. Used to sort List of Elements in
	 * getTree method for Service Selection screen.
	 */
	static class ElementComparator implements Comparator<Element>
	{

		@Override
		public int compare(Element o1, Element o2)
		{
			return o1.getAttributeValue("name").trim().compareTo(o2.getAttributeValue("name").trim());
		}

	}

	/**
	 * This method perform the back action from the second overlay while
	 * uploading document on second overlay. Moved from
	 * BusinessApplicationController for Release 3.3.0, Defect 6451
	 * @param aoRequest - Action Request
	 */
	public static void backRequestAction(ActionRequest aoRequest)
	{
		String lsDoccategory = aoRequest.getParameter("docCategory");
		String lsDocType = aoRequest.getParameter("docType");
		String lsFormName = aoRequest.getParameter("formName");
		String lsFormVer = aoRequest.getParameter("formVersion");
		String lsSerAppId = aoRequest.getParameter("service_app_id");
		String lsSecId = aoRequest.getParameter("sectionId");
		ApplicationSession.setAttribute(lsFormName, aoRequest, "form_name");
		ApplicationSession.setAttribute(lsFormVer, aoRequest, "form_version");
		ApplicationSession.setAttribute(lsDoccategory, aoRequest, "document_category");
		ApplicationSession.setAttribute(lsDocType, aoRequest, "document_type");
		ApplicationSession.setAttribute(lsSerAppId, aoRequest, "service_app_id");
		ApplicationSession.setAttribute(lsSecId, aoRequest, "section_id");
	}

}
