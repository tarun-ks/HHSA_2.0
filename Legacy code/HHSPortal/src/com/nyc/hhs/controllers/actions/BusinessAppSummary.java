package com.nyc.hhs.controllers.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;

import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.BusinessApplicationSummary;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.SubSectionBean;
import com.nyc.hhs.util.ApplicationSession;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;

/**
 * This class sets the required values in the Channel object, required to
 * execute the transaction to create common List of Beans to display sub section
 * and documents together. Also it sets the values, required in the in jsp, in
 * the request object.
 * 
 */

public class BusinessAppSummary extends BusinessApplication
{

	/**
	 * Gets the channel object for action
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Action request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, ActionRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		return null;
	}

	/**
	 * Gets the channel object for render
	 * 
	 * @param asSectionName - current section name
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppId - Business application id of the application
	 * @param asAppStatus - the current application status
	 * @param asAppDataForUpdate - data to be updated in application
	 * @param asAction - the action to be performed
	 * @param asUserRole - current user role
	 * @param aoRequest - Render request
	 * @param asTaxonomyName - taxonomy name to be used in factory
	 * @return the channel object to be used for further processing
	 * @throws ApplicationException
	 */
	@Override
	public Channel getChannelObject(String asSectionName, String asOrgId, String asAppId, String asAppStatus,
			String asAppDataForUpdate, String asAction, String asUserRole, RenderRequest aoRequest,
			String asTaxonomyName) throws ApplicationException
	{
		Channel loChannel = new Channel();
		loChannel.setData(ApplicationConstants.APPID, asAppId);
		loChannel.setData(ApplicationConstants.ORG_ID, asOrgId);
		loChannel.setData("abIsFinalView", true);
		return loChannel;
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			RenderRequest aoRequest) throws ApplicationException
	{
		Map<String, Object> loMapForRender = new HashMap<String, Object>();
		loMapForRender.put(ApplicationConstants.FILE_TO_INCLUDE,
				"/WEB-INF/jsp/businessapplication/businessapplicationsummary.jsp");
		String lsMenu = getSubMenu(asSectionName);
		loMapForRender.put(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU, lsMenu);
		List<SubSectionBean> loSubSectionList = (List<SubSectionBean>) aoChannel.getData("loSubSectionDetails");
		List<DocumentBean> loDocumentList = (List<DocumentBean>) aoChannel.getData("loDocDetails");
		ApplicationSession.setAttribute("false", aoRequest, "isActiveCeo");
		if (!CollectionUtils.isEmpty(loDocumentList))
		{
			Iterator loDocListItr = loDocumentList.iterator();
			while (loDocListItr.hasNext())
			{
				DocumentBean loDocument = (DocumentBean) loDocListItr.next();
				if ((loDocument != null && loDocument.getDocType() != null
						&& loDocument.getDocType().equalsIgnoreCase(ApplicationConstants.CEO_NAME) && loDocument
							.isMbIsCeoActive()))
				{
					ApplicationSession.setAttribute("true", aoRequest, "isActiveCeo");
				}
			}
		}
		Map<String, StringBuffer> loBusinessSummaryMap = (Map<String, StringBuffer>) aoChannel
				.getData("aoPrinterFriendlyComments");
		for (Entry<String, StringBuffer> loEntry : loBusinessSummaryMap.entrySet())
		{
			StringBuffer loPrintableHtmlContent = new StringBuffer();
			loPrintableHtmlContent.append("<div class='commentBox' id='").append(loEntry.getKey())
					.append("_comments' >").append(loEntry.getValue()).append("</div>");
			loBusinessSummaryMap.put(loEntry.getKey(), loPrintableHtmlContent);
		}
		loMapForRender.put("aoBusinessSummaryMap", loBusinessSummaryMap);
		List<BusinessApplicationSummary> loBASList = new ArrayList<BusinessApplicationSummary>();
		String[] lsSectionIDArray = ApplicationConstants.WORKFLOW_LAUNCH_SECTION_IDS_SEQUENCE;
		String[][] lsSubSectionIDArray = ApplicationConstants.SUB_SECTION_NAMES_MAPPING;
		// Logic to create common List of Beans to display sub section and
		// documents together
		// block of code to be executed if loSubSectionList is not empty
		if (!CollectionUtils.isEmpty(loSubSectionList))
		{
			createBeanToDisplayDocuments(loSubSectionList, loDocumentList, loBASList, lsSectionIDArray,
					lsSubSectionIDArray);
		}
		// Declaring 4 lists to contain the data for each section summary
		List<BusinessApplicationSummary> loBasicSummaryList = new ArrayList<BusinessApplicationSummary>();
		List<BusinessApplicationSummary> loBoardSummaryList = new ArrayList<BusinessApplicationSummary>();
		List<BusinessApplicationSummary> loFilingSummaryList = new ArrayList<BusinessApplicationSummary>();
		List<BusinessApplicationSummary> loPoliciesSummaryList = new ArrayList<BusinessApplicationSummary>();

		if (!CollectionUtils.isEmpty(loBASList))
		{
			Iterator loListItr = loBASList.iterator();
			while (loListItr.hasNext())
			{
				BusinessApplicationSummary loBAS = (BusinessApplicationSummary) loListItr.next();
				// To set data into 4 separate lists of individual sections
				// and set them in request object to display on GRID

				if (ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS.equalsIgnoreCase(loBAS.getMsSectionID()))
				{
					loBasicSummaryList.add(loBAS);
				}
				else if (ApplicationConstants.BUSINESS_APPLICATION_SECTION_BOARD.equalsIgnoreCase(loBAS
						.getMsSectionID()))
				{
					loBoardSummaryList.add(loBAS);
				}
				else if (ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS.equalsIgnoreCase(loBAS
						.getMsSectionID()))
				{
					loFilingSummaryList.add(loBAS);
				}
				else if (ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES.equalsIgnoreCase(loBAS
						.getMsSectionID()))
				{
					loPoliciesSummaryList.add(loBAS);
				}
			}
			// Setting section Summary lists in Request Object
			loMapForRender.put("loBasicSummaryList", loBasicSummaryList);
			loMapForRender.put("loBoardSummaryList", loBoardSummaryList);
			loMapForRender.put("loFilingSummaryList", loFilingSummaryList);
			loMapForRender.put("loPoliciesSummaryList", loPoliciesSummaryList);
		}
		return loMapForRender;
	}

	/**
	 * Iterating for all the sections
	 * 
	 * @param aoSubSectionList
	 * @param aoDocumentList
	 * @param aoBASList
	 * @param asSectionIDArray
	 * @param asSubSectionIDArray
	 * @throws ApplicationException
	 */
	private void createBeanToDisplayDocuments(List<SubSectionBean> aoSubSectionList, List<DocumentBean> aoDocumentList,
			List<BusinessApplicationSummary> aoBASList, String[] asSectionIDArray, String[][] asSubSectionIDArray)
			throws ApplicationException
	{
		boolean lbSecQues = true;
		// Iterating for all the sections
		for (int liSecCounter = 0; liSecCounter < asSectionIDArray.length; liSecCounter++)
		{
			for (int liSubSecCounter = 0; liSubSecCounter < asSubSectionIDArray[liSecCounter].length; liSubSecCounter++)
			{
				Iterator loListItr = aoSubSectionList.iterator();
				while (loListItr.hasNext())
				{
					SubSectionBean loSubSection = (SubSectionBean) loListItr.next();
					if (asSectionIDArray[liSecCounter].equalsIgnoreCase(loSubSection.getSectionId())
							&& asSubSectionIDArray[liSecCounter][liSubSecCounter].equalsIgnoreCase(loSubSection
									.getSubSectionID()))
					{
						if (loSubSection.getSubSectionID().equalsIgnoreCase(
								ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)
								|| loSubSection.getSubSectionID().equalsIgnoreCase(
										ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS))
						{
							createBeanForQuestion(aoDocumentList, aoBASList, loSubSection);
						}
						else if (loSubSection.getSubSectionID().equalsIgnoreCase(
								ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY)
								|| loSubSection.getSubSectionID().equalsIgnoreCase(
										ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES)
								|| loSubSection.getSubSectionID().equalsIgnoreCase(
										ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_POPULATIONS))
						{
							lbSecQues = createBeanForOthers(aoSubSectionList, aoBASList, lbSecQues, loSubSection);
						}
					}
				}
			}
		}
	}

	/**
	 * Block of code to be executed if lbSecQues is true
	 * 
	 * @param aoSubSectionList
	 * @param aoBASList
	 * @param abSecQues
	 * @param aoSubSection
	 * @return
	 * @throws ApplicationException
	 */
	private boolean createBeanForOthers(List<SubSectionBean> aoSubSectionList,
			List<BusinessApplicationSummary> aoBASList, boolean abSecQues, SubSectionBean aoSubSection)
			throws ApplicationException
	{
		// Block of code to be executed if lbSecQues is true
		if (abSecQues)
		{
			Iterator loListItrinr = aoSubSectionList.iterator();
			while (loListItrinr.hasNext())
			{
				SubSectionBean loSubSectionInr = (SubSectionBean) loListItrinr.next();
				if (null != loSubSectionInr.getSectionId()
						&& aoSubSection.getSectionId().equalsIgnoreCase(loSubSectionInr.getSectionId())
						&& !loSubSectionInr.getSubSectionID().contains(
								ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS)
						&& !loSubSectionInr.getSubSectionID().contains(
								ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION))
				{

					BusinessApplicationSummary loWFCBinr = new BusinessApplicationSummary(
							loSubSectionInr.getSectionId(), loSubSectionInr.getSubSectionID(),
							loSubSectionInr.getDocumentType(), loSubSectionInr.getSubSectionStatus(),
							DateUtil.getDateMMddYYYYFormat(loSubSectionInr.getModifiedDate()),
							FileNetOperationsUtils.getUserName(aoSubSection.getModifiedBy()),
							ApplicationConstants.EMPTY_STRING, loSubSectionInr.getFormId(),
							loSubSectionInr.getFormVersion(), loSubSectionInr.getFormName());
					aoBASList.add(loWFCBinr);
					abSecQues = true;
				}
			}
			abSecQues = false;
		}
		return abSecQues;
	}

	/**
	 * Block of code to be executed if loDocumentList is not empty
	 * 
	 * @param aoDocumentList
	 * @param aoBASList
	 * @param aoSubSection
	 * @throws ApplicationException
	 */
	private void createBeanForQuestion(List<DocumentBean> aoDocumentList, List<BusinessApplicationSummary> aoBASList,
			SubSectionBean aoSubSection) throws ApplicationException
	{
		if (aoSubSection.getSubSectionID().equalsIgnoreCase(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)
				|| (aoSubSection.getSubSectionID().equalsIgnoreCase(
						ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS)
						&& !ApplicationConstants.COMPLETED_STATE.equalsIgnoreCase(aoSubSection.getSubSectionStatus()) && !ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_IN_REVIEW
							.equalsIgnoreCase(aoSubSection.getSubSectionStatus())))
		{

			if (aoSubSection.getSubSectionStatus() != null
					&& aoSubSection.getSubSectionStatus().equalsIgnoreCase(ApplicationConstants.START_STATUS))
			{
				aoSubSection.setSubSectionStatus(ApplicationConstants.PARTIALLY_COMPLETE_STATE);
			}
			BusinessApplicationSummary loBAS = new BusinessApplicationSummary(aoSubSection.getSectionId(),
					aoSubSection.getSubSectionID(), aoSubSection.getDocumentType(), aoSubSection.getSubSectionStatus(),
					DateUtil.getDateMMddYYYYFormat(aoSubSection.getModifiedDate()),
					FileNetOperationsUtils.getUserName(aoSubSection.getModifiedBy()),
					ApplicationConstants.EMPTY_STRING, aoSubSection.getFormId(), aoSubSection.getFormVersion(),
					aoSubSection.getFormName());
			aoBASList.add(loBAS);
		}
		if (aoSubSection.getSubSectionID().equalsIgnoreCase(
				ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS))
		{
			// block of code to be executed if loDocumentList is not empty
			if (!CollectionUtils.isEmpty(aoDocumentList))
			{
				Iterator loDocListItr = aoDocumentList.iterator();
				while (loDocListItr.hasNext())
				{
					DocumentBean loDocument = (DocumentBean) loDocListItr.next();
					if (aoSubSection.getSectionId().equalsIgnoreCase(loDocument.getSectionID())
							&& aoSubSection.getFormId().equalsIgnoreCase(loDocument.getFormID())
							&& aoSubSection.getFormName().equalsIgnoreCase(loDocument.getFormName())
							&& aoSubSection.getFormVersion().equalsIgnoreCase(loDocument.getFormVersion()))
					{

						if ((loDocument.getDocTitle() == null || loDocument.getDocTitle().equalsIgnoreCase("null"))
								&& (loDocument.getDocType() != null && loDocument.getDocType().equalsIgnoreCase(
										ApplicationConstants.CEO_NAME)))
						{
							loDocument.setDocTitle(loDocument.getMsCeoName());
						}
						if ((loDocument.getDocTitle() == null || loDocument.getDocTitle().equalsIgnoreCase("null"))
								&& (loDocument.getDocType() != null && loDocument.getDocType().equalsIgnoreCase(
										ApplicationConstants.CFO_NAME)))
						{
							loDocument.setDocTitle(loDocument.getMsCfoName());
						}

						if (loDocument.getDocTitle() == null || loDocument.getDocTitle().equalsIgnoreCase("null"))
						{
							loDocument.setDocTitle("");
						}

						BusinessApplicationSummary loBAS = new BusinessApplicationSummary(loDocument.getSectionID(),
								loDocument.getDocTitle(), loDocument.getDocType(), loDocument.getDocStatus(),
								DateUtil.getDateMMddYYYYFormat(loDocument.getModifiedDate()),
								FileNetOperationsUtils.getUserName(aoSubSection.getModifiedBy()),
								loDocument.getDocID(), loDocument.getFormID(), loDocument.getFormVersion(),
								loDocument.getFormName());
						aoBASList.add(loBAS);
					}
				}
			}
		}
	}

	/**
	 * This method fetches the map to be rendered
	 * 
	 * @param asAction - the action to be performed
	 * @param asSectionName - current section name
	 * @param aoChannel - channel object with data, will be needed to be passed
	 *            to MapForRender
	 * @param aoRequest - Action Request
	 * @return the map to be rendered on front end
	 * @throws ApplicationException
	 */
	@Override
	public Map<String, Object> getMapForRender(String asAction, String asSectionName, Channel aoChannel,
			ActionRequest aoRequest) throws ApplicationException
	{
		return null;
	}

}
