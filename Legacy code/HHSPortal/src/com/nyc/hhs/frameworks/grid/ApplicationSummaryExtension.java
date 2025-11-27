package com.nyc.hhs.frameworks.grid;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.util.DateUtil;

/**
 * This class generates an extension which fetches data in the Grid
 * for the Application History table
 *
 */

public class ApplicationSummaryExtension implements DecoratorInterface{

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject
	 *            an object of list to be displayed in grid
	 * @param aoCol
	 *            a column object
	 * @param aiSeqNo
	 *            an integer value of sequence number
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol,Integer aoSeqNo) throws ApplicationException 
	{
			ApplicationSummary loApplicationSummary=(ApplicationSummary) aoEachObject;	
			String lsColName=aoCol.getColumnName();
			String lsControl=null;
			String lsBusinessLink=null;
			String lsServiceLink=null;
			
		if("msAppName".equalsIgnoreCase(lsColName))
		{
			if("Service".equalsIgnoreCase(loApplicationSummary.getMsAppType()))
			{													   	
				if(ApplicationConstants.APP_STATUS_DRAFT.equalsIgnoreCase(loApplicationSummary.getMsAppStatus()))
				{	
					lsServiceLink="<img src=\"../framework/skins/hhsa/images/childArrow.png\">"+loApplicationSummary.getMsAppName();
				}
				else if("returned for revisions".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "deferred".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()))
				{
					lsServiceLink="<img src=\"../framework/skins/hhsa/images/childArrow.png\">"+"<a href=\"#\" onclick=\"javascript: openLink('OpenLink')\">"+loApplicationSummary.getMsAppName()+"</a>";
				}
				else 
				{
					if("in review".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "Approved".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "rejected".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()))
					{
						lsServiceLink="<img src=\"../framework/skins/hhsa/images/childArrow.png\">"+"<a href=\"#\" onclick=\"javascript: openLink('OpenLink')\">"+loApplicationSummary.getMsAppName()+"</a>";
					}
				}
				
				if("Requested".equalsIgnoreCase(loApplicationSummary.getMsWithdrawanStatus()))
				{
					lsControl=lsServiceLink+"<div class=\"withdrawal_info\">"
					+"<img src=\"../framework/skins/hhsa/images/exclamation.png\" alt=\"\" title=\"exclamation\" onMouseOver=\"setVisibility('1', 'inline');\" onMouseOut=\"setVisibility('1', 'none');\"/>"
					+"<div id=\"1\"class=\"withdrawal_info_tooltip\"><div class=\"tooltip_content\"><p><b>A request for withdrawal has been  submitted for this application</b></p><b>Requested Date:</b><span>"
					+DateUtil.getDateMMddYYYYFormat(loApplicationSummary.getMdRequestDate())+"</span><br/><b>Requester:</b><span>"+loApplicationSummary.getMsRequester() +"</span></div></div></div>";
				}				
				else
				{
					lsControl=lsServiceLink;
				}
			}
			else
			{		
				if(ApplicationConstants.APP_STATUS_DRAFT.equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "returned for revisions".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "deferred".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()))
				{
					lsBusinessLink  = "<a href=\"#\" onclick=\"javascript: openLink('OpenLink')\">"+loApplicationSummary.getMsAppName()+"</a>";
				}
				else 
				{
					if("in review".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "Approved".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()) || "rejected".equalsIgnoreCase(loApplicationSummary.getMsAppStatus()))
					{
						lsBusinessLink  = "<a href=\"#\" onclick=\"javascript: openLink('OpenLink')\">"+loApplicationSummary.getMsAppName()+"</a>";
					}
				}
				
				if("Requested".equalsIgnoreCase(loApplicationSummary.getMsWithdrawanStatus()))
				{
					lsControl=lsBusinessLink+"<div class=\"withdrawal_info\">"
					+"<img src=\"../framework/skins/hhsa/images/exclamation.png\" alt=\"\" title=\"exclamation\" onMouseOver=\"setVisibility('1', 'inline');\" onMouseOut=\"setVisibility('1', 'none');\"/>"
					+"<div id=\"1\"class=\"withdrawal_info_tooltip\"><div class=\"tooltip_content\"><p><b>A request for withdrawal has been  submitted for this application</b></p><b>Requested Date:</b><span>"
					+DateUtil.getDateMMddYYYYFormat(loApplicationSummary.getMdRequestDate())+"</span><br/><b>Requester:</b><span>"+loApplicationSummary.getMsRequester() +"</span></div></div></div>";															
				}				
				else
				{
					lsControl=lsBusinessLink;
				}
			}	
		}
		else if("mdAppSubmissionDate".equalsIgnoreCase(lsColName))
		{
			if(loApplicationSummary.getMdAppSubmissionDate()!=null)
			{
				lsControl=DateUtil.getDateMMddYYYYFormat(loApplicationSummary.getMdAppSubmissionDate());
			}
			else
			{
				lsControl="";
			}					
		}
		
		else if("mdAppStartDate".equalsIgnoreCase(lsColName))
		{
			if(loApplicationSummary.getMdAppStartDate()!=null)
			{
				lsControl=DateUtil.getDateMMddYYYYFormat(loApplicationSummary.getMdAppStartDate());
			}
			else
			{
				lsControl="";
			}
			}
		else if("mdAppExpirationDate".equalsIgnoreCase(lsColName))
		{
			if(loApplicationSummary.getMdAppExpirationDate()!=null)
			{
				lsControl=DateUtil.getDateMMddYYYYFormat(loApplicationSummary.getMdAppExpirationDate());
			}
			else
			{
				lsControl="";
			}	
		}
			return lsControl;	
	}
}