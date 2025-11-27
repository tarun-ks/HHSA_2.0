package com.nyc.hhs.frameworks.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.util.HHSUtil;

/**
 * This is a custom tag class which is executed by the JSTL to generate the R2 Navigation
 */

/**
 * @author megha.singhal
 * 
 */
public class CommonSolicitationTag extends BodyTagSupport
{
	private static final LogInfo LOG_OBJECT = new LogInfo(CommonSolicitationTag.class);

	private static final long serialVersionUID = 1L;
	private int procurementId;
	private String level;
	private String procurementStatus;
	private String topLevelStatus;
	private String providerId;
	private String bottomLevelStatus;
	private String screenName;

	/**
	 * @return the screenName
	 */
	public String getScreenName()
	{
		return screenName;
	}

	/**
	 * @param screenName the screenName to set
	 */
	public void setScreenName(String screenName)
	{
		this.screenName = screenName;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId()
	{
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	/**
	 * This method is invoked by the JSP page implementation object It process
	 * the end tag for this instance.
	 * 
	 * @returns EVAL_BODY_AGAIN if screen name doesnt matches, EVAL_BODY_INCLUDE
	 *          if screen name matches
	 */
	public int doStartTag()
	{
		try
		{
			JspWriter loOut = pageContext.getOut();
			if (HHSConstants.PROC_WIDGET.equalsIgnoreCase(level))
			{
				loOut.print(fetchProcurementDetails(procurementId, procurementStatus));
				return (SKIP_BODY);
			}
			if (HHSConstants.TRUE.equalsIgnoreCase(topLevelStatus))
			{
				loOut.print(fetchProviderStatus(providerId, procurementId, pageContext.getRequest()));
				return (SKIP_BODY);
			}

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error in common solicitation", aoExp);
		}
		return (EVAL_BODY_AGAIN);
	}

	/**
	 * @param screenNumber the screenNumber to set
	 */
	public void setLevel(String level)
	{
		this.level = level;
	}

	/**
	 * @return the screenNumber
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * @param screenNumber the screenNumber to set
	 */
	public void setProcurementId(int procurementId)
	{
		this.procurementId = procurementId;
	}

	/**
	 * @return the screenNumber
	 */
	public int getProcurementId()
	{
		return procurementId;
	}

	/**
	 * @return the procurementStatus
	 */
	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	/**
	 * @param procurementStatus the procurementStatus to set
	 */
	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	/**
	 * @return the topLevelStatus
	 */
	public String getTopLevelStatus()
	{
		return topLevelStatus;
	}

	/**
	 * @param topLevelStatus the topLevelStatus to set
	 */
	public void setTopLevelStatus(String topLevelStatus)
	{
		this.topLevelStatus = topLevelStatus;
	}

	/**
	 * This method fetches details (procurement status and provider status) that
	 * has to be rendered on provider screens at top most level
	 * <ul>
	 * <li>1. Retrieve provider Id and procurement Id and set them in the
	 * Channel object</li>
	 * <li>2. Call transaction <b>fetchProviderWidgetDetails</b> to fetch the
	 * map populated with procurement status and provider status</li>
	 * <li>3. Call getStatusName method to corresponding to process type
	 * "procurement" and "provider" and status Ids so as to retrieve the status
	 * values</li>
	 * <li>4. Append the retrieved status in the string buffer and return the
	 * same</li>
	 * </ul>
	 * 
	 * @param providerId - string representation of provider Id
	 * @param aiProcurementId - string representation of procurement Id
	 * @return String - procurement and provider status
	 * @throws ApplicationException
	 * @throws ParseException
	 */
	private String fetchProviderStatus(String asProviderId, Integer aoProcurementId, ServletRequest aoRequest)
			throws ApplicationException, ParseException
	{
		StringBuffer loProviderWidget = new StringBuffer();
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.PROCUREMENT_ID_KEY, aoProcurementId.toString());
		loChannel.setData(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROVIDER_WIDGET_DET);
		Map<Object, Object> loProviderWidgetData = (Map<Object, Object>) loChannel
				.getData(HHSConstants.LO_PROVIDER_MAP);

		if (loProviderWidgetData != null && !loProviderWidgetData.isEmpty())
		{
			String lsProcurementStatus = HHSUtil.getStatusName(HHSConstants.PROCUREMENT,
					Integer.parseInt(loProviderWidgetData.get(HHSConstants.STATUS).toString()));
			String lsProviderStatus = HHSUtil.getStatusName(HHSConstants.PROVIDER,
					Integer.parseInt(loProviderWidgetData.get(HHSConstants.PROVIDER_STATUS).toString()));
			aoRequest.setAttribute(HHSConstants.PROCUREMENT_STATUS, loProviderWidgetData.get(HHSConstants.STATUS)
					.toString());
			aoRequest.setAttribute(HHSConstants.PROVIDER_STATUS_KEY, lsProviderStatus);
			aoRequest.setAttribute(HHSConstants.PROVIDER_STATUS_ID,
					Integer.parseInt(loProviderWidgetData.get(HHSConstants.PROVIDER_STATUS).toString()));
			loProviderWidget.append("<div><b>Procurement Status:</b> " + lsProcurementStatus + "</div>");
			loProviderWidget.append("<div><b>Provider Status:</b> " + lsProviderStatus + "</div>");
		}
		return loProviderWidget.toString();
	}

	/**
	 * This method fetches control widget details (last modified date, last
	 * modified by user Id, last published date and last published by user Id)
	 * that has to be rendered on procurement screens
	 * <ul>
	 * <li>1. Retrieve procurement status and procurement Id and set them in the
	 * Channel object</li>
	 * <li>2. Call transaction <b>getProcurementChangeControlWidget</b> to fetch
	 * the map populated with procurement change control widget</li>
	 * <li>3. If retrieved procurement widget map is not null and retrieved
	 * "LAST_MODIFIED_DATE" and "LAST_PUBLISHED_DATE" are not null then parse
	 * the dates into SimpleDateFormat type</li>
	 * <li>4. Append the fetched result in the StringBuffer object and return
	 * the same by converting it into String type</li>
	 * </ul>
	 * 
	 * @param aiProcurementId - string representation of procurement Id
	 * @param asProcurementStatus - string representation of procurement status
	 * @return - String - procurement change control widget details
	 * @throws ApplicationException
	 * @throws ParseException
	 */
	private String fetchProcurementDetails(int aiProcurementId, String asProcurementStatus)
			throws ApplicationException, ParseException
	{
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.AI_PROC_ID, aiProcurementId);
		loChannel.setData(HHSConstants.SCREEN_NAME, screenName);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_PROCUREMENT_CHANGE_CONTROL_WIDGET);
		HashMap<Object, Object> loProcurementControlWidgetData = (HashMap<Object, Object>) loChannel
				.getData(HHSConstants.PROC_WIDGET_HASHMAP);
		StringBuffer loChangeWidget = new StringBuffer();
		SimpleDateFormat loDateFormat = new SimpleDateFormat(HHSConstants.MMDDYYFORMAT);
		SimpleDateFormat loDateFor = new SimpleDateFormat(HHSConstants.YYYY_MM_DD);
		Date loLastModifiedDate = null;
		Date loPublishedDate = null;
		if (loProcurementControlWidgetData != null
				&& loProcurementControlWidgetData.get(HHSConstants.LAST_MODIFIED_DATE) != null)
		{
			loLastModifiedDate = loDateFor.parse(loProcurementControlWidgetData.get(HHSConstants.LAST_MODIFIED_DATE)
					.toString());
		}
		if (loProcurementControlWidgetData != null
				&& loProcurementControlWidgetData.get(HHSConstants.LAST_PUBLISHED_DATE_VALUE) != null)
		{
			loPublishedDate = loDateFor.parse(loProcurementControlWidgetData.get(HHSConstants.LAST_PUBLISHED_DATE_VALUE)
					.toString());
		}
		if (aiProcurementId > 0 && asProcurementStatus != null
				&& asProcurementStatus.equalsIgnoreCase(HHSConstants.DRAFT))
		{
			if (loLastModifiedDate != null
					&& (loProcurementControlWidgetData.get(HHSConstants.MODIFIED_BY_USERID) != null && !((String) loProcurementControlWidgetData
							.get(HHSConstants.MODIFIED_BY_USERID)).equalsIgnoreCase(HHSConstants.NULL)))
			{
				loChangeWidget.append("<div><b>Last Modified:</b> " + loDateFormat.format(loLastModifiedDate)
						+ "&nbsp;&nbsp;by&nbsp;&nbsp;"
						+ loProcurementControlWidgetData.get(HHSConstants.MODIFIED_BY_USERID) + "</div>");
			}
			else
			{
				loChangeWidget.append("<div><b>Last Modified:</b> N/A </div>");
			}
			loChangeWidget.append("<div><b>Last Published:</b> N/A </div>");
		}
		else if (aiProcurementId > 0 && asProcurementStatus != null
				&& !asProcurementStatus.equalsIgnoreCase(HHSConstants.DRAFT))
		{
			if (loLastModifiedDate != null
					&& (loProcurementControlWidgetData.get(HHSConstants.MODIFIED_BY_USERID) != null && !((String) loProcurementControlWidgetData
							.get(HHSConstants.MODIFIED_BY_USERID)).equalsIgnoreCase(HHSConstants.NULL)))
			{
				loChangeWidget.append("<div><b>Last Modified:</b> " + loDateFormat.format(loLastModifiedDate)
						+ "&nbsp;&nbsp;by&nbsp;&nbsp;"
						+ loProcurementControlWidgetData.get(HHSConstants.MODIFIED_BY_USERID) + "</div>");
			}
			else
			{
				loChangeWidget.append("<div><b>Last Modified:</b> N/A </div>");
			}
			if (loPublishedDate != null
					&& (loProcurementControlWidgetData.get(HHSConstants.LAST_PUBLISHED_USERID) != null && !((String) loProcurementControlWidgetData
							.get(HHSConstants.LAST_PUBLISHED_USERID)).equalsIgnoreCase(HHSConstants.NULL)))
			{
				loChangeWidget.append("<div><b>Last Published:</b> " + loDateFormat.format(loPublishedDate)
						+ "&nbsp;&nbsp;by&nbsp;&nbsp;"
						+ loProcurementControlWidgetData.get(HHSConstants.LAST_PUBLISHED_USERID) + "</div>");
			}
			else
			{
				loChangeWidget.append("<div><b>Last Published:</b> N/A </div>");
			}
		}
		else
		{
			loChangeWidget.append("<div><b>Last Modified:</b> N/A </div>");
			loChangeWidget.append("<div><b>Last Published:</b> N/A </div>");
		}
		return loChangeWidget.toString();
	}

	public void setBottomLevelStatus(String bottomLevelStatus)
	{
		this.bottomLevelStatus = bottomLevelStatus;
	}

	public String getBottomLevelStatus()
	{
		return bottomLevelStatus;
	}
}
