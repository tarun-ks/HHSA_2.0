package com.nyc.hhs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.FileNetOperationsUtils;

/**
 * Servlet implementation class This servlet will populate values for search
 * criteria whenever it will encounter 3 words.
 * 
 */

public class AutoCompleteServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(AutoCompleteServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String SINGLE_QUOTE = "\'";
	private static final String DQUOTES_COMMA = "\",";
	private static final String DOUBLE_QUOTES = "\"";
	private static final String SQUARE_BRAC_BEGIN = "[";
	private static final String COMMA = ",";

	public AutoCompleteServlet()
	{
		super();
	}

	/**
	 * This method handle the get request of a servlet. This will internally
	 * call doPost method to process the servlet request and return the
	 * response.
	 * 
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method handle the post request of a servlet. When a action is
	 * initiated from a jsp, it process the action by calling multiple
	 * transactions to the end.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		String lsPartialUoDenom = "";
		lsPartialUoDenom = aoRequest.getParameter("query");
		// R4 Homepage changes: fetch request parameter if 'Select Organization'
		// type head would be displaying both provider and agency names.
		// in the following case the isProvider parameter would be false.
		String lsIsProvider = aoRequest.getParameter(ApplicationConstants.IS_PROVIDER);
		List<ProviderBean> loProviderList = null;
		List<ProviderBean> loProviderAgencyList = new ArrayList<ProviderBean>();
		ProviderBean loProviderBeanProv;
		ProviderBean loProviderBeanAgency;
		TreeSet<String> loAgencyList = null;
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		// Change for Defect 7874 starts
		String lsOrgName = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME);
		String lsOrgType = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE);
		if(!lsOrgName.equalsIgnoreCase(HHSConstants.CITY) && lsOrgName.contains(HHSConstants.HYPHEN))
		{
			lsOrgName = lsOrgName.substring(0, lsOrgName.lastIndexOf(HHSConstants.HYPHEN)).trim();
		}
		String lsSameUserOrg = lsOrgName + HHSConstants.DELIMETER_SIGN + lsOrgType;
		// Change for Defect 7874 ends
		UserThreadLocal.setUser(lsUserId);
		try
		{
			Map<String, String> loDataMap = new HashMap<String, String>();
			/* Start : R5 Added */
			String lsAgencyProviderLookUp = aoRequest.getParameter(HHSConstants.AS_AGENCY_PROVIDER_LOOK_UP);
			String lsAgencyLogin = aoRequest.getParameter(HHSConstants.AS_AGENCY_LOGIN);
			String lsGetFullList = aoRequest.getParameter("getFullList");
			boolean lbAgnecyLogin = true;
			boolean lbGetFullList = true;
			if (StringUtils.isNotBlank(lsAgencyLogin) && Boolean.parseBoolean(lsAgencyLogin))
			{
				lbAgnecyLogin = false;
			}
			if (StringUtils.isNotBlank(lsGetFullList) && Boolean.parseBoolean(lsGetFullList))
			{
				lbGetFullList = false;
			}

			// Changing if condition in Emergency 4.0.2 for defect # 8388
			if (null != lsOrgType && lsOrgType.equalsIgnoreCase(HHSR5Constants.USER_AGENCY) && lbAgnecyLogin && lbGetFullList 
					&&  ( StringUtils.isNotBlank(lsAgencyProviderLookUp) && !lsAgencyProviderLookUp.equalsIgnoreCase(HHSConstants.STRING_TRUE)  ))
			{
				// Combo Box comment Box
				// loDataMap = (Map<String,
				// String>)BaseCacheManagerWeb.getInstance().getCacheObject("sharedDocForProvider");
/*[Start] R7.3.0 QC9016 type ahead
 * 				loDataMap = (Map<String, String>) aoRequest.getSession().getAttribute("sharedOrgDetailsForAgency");
 * 

				
				 Start QC 8719 R7.0.0 - verify that loDataMap is not Null 
				if (loDataMap != null) 
				{	
					for (Map.Entry<String, String> entry : loDataMap.entrySet())
					{
						 End : R5 Added 
						loProviderBeanAgency = new ProviderBean();
						loProviderBeanAgency.setHiddenValue(entry.getKey());
						loProviderBeanAgency.setDisplayValue(entry.getValue());
						loProviderAgencyList.add(loProviderBeanAgency);
					}
				} 
*/				/* End QC 8719 R7.0.0  */
			    
               loProviderList =   FileNetOperationsUtils.getProviderList();
                if(loProviderAgencyList != null && loProviderList != null){
                    loProviderAgencyList.addAll(loProviderList);
                }
/* [End] R7.3.0 QC9016 type ahead */
			}
			else
			{
				loProviderList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.PROV_LIST);

				// R4 Homepage changes: If condition added since now on the
				// 'Manage
				// Provider' section on Accelerator Homepage,
				// Type ahead will display both Provider and Agency names.
				// If lsIsProvider request parameter is not null and false then
				// Agency List is also added to the Provider List else
				// typehead would display only Provider names.
				if (lsIsProvider != null && lsIsProvider.equalsIgnoreCase(ApplicationConstants.FALSE))
				{
					Iterator<ProviderBean> loItrProvider = loProviderList.iterator();
					while (loItrProvider.hasNext())
					{
						loProviderBeanAgency = new ProviderBean();
						ProviderBean loProvider = loItrProvider.next();
						loProviderBeanAgency.setHiddenValue(loProvider.getHiddenValue()
								+ ApplicationConstants.TILD_PROVIDER);
						loProviderBeanAgency.setDisplayValue(loProvider.getDisplayValue());
						loProviderAgencyList.add(loProviderBeanAgency);
					}

					loAgencyList = (TreeSet<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
							ApplicationConstants.AGENCY_LIST);
					Iterator<String> loItrAgency = loAgencyList.iterator();
					while (loItrAgency.hasNext())
					{
						loProviderBeanProv = new ProviderBean();
						String lsAgency = (String) loItrAgency.next();
						String[] loAgencyName = lsAgency.split(ApplicationConstants.TILD);
						loProviderBeanProv.setDisplayValue(loAgencyName[1]);
						loProviderBeanProv.setHiddenValue(loAgencyName[0] + ApplicationConstants.TILD_AGENCY);
						loProviderAgencyList.add(loProviderBeanProv);
					}
					/* Start : R5 Added */
					if (!lbAgnecyLogin)
					{
						loDataMap = (Map<String, String>) aoRequest.getSession().getAttribute(
								"sharedOrgDetailsForAgency");
						/* Start QC 8719 R7.0.0 - verify that loDataMap is not Null */
						if(loDataMap != null)
						{	
							for (Map.Entry<String, String> entry : loDataMap.entrySet())
							{
								if (entry.getKey().contains("city_org"))
								{
									loProviderBeanAgency = new ProviderBean();
									loProviderBeanAgency.setHiddenValue(entry.getKey());
									loProviderBeanAgency.setDisplayValue(entry.getValue());
									loProviderAgencyList.add(loProviderBeanAgency);
								}
							}
						} 
						/* end QC 8719 R7.0.0 */
					}
					/* End : R5 Added */
				}
				else
				{
					loProviderAgencyList.addAll(loProviderList);
				}
			}
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Application Exception occurred while getting list of  ProviderBean", aoEx);
		}
		final int liMinLength = 3;
		// Change for Defect 7874 starts
		Iterator<ProviderBean> loIterator = loProviderAgencyList.iterator();
		String lsMatchUserOrg = null;
		while (loIterator.hasNext())
		{
			lsMatchUserOrg = (String) loIterator.next().getHiddenValue();
			if (lsMatchUserOrg.contains(lsSameUserOrg))
			{
				loIterator.remove();
				break;
			}
		}
		// Change for Defect 7874 ends
		if ((lsPartialUoDenom != null) && (lsPartialUoDenom.length() >= liMinLength))
		{
			try
			{
				final PrintWriter loOut = aoResponse.getWriter();
				aoResponse.setContentType("application/json");
				aoRequest.getSession().setAttribute("dropDownList", loProviderAgencyList);
				final String lsOutputJSONaoResponse = this
						.generateDelimitedResponse(loProviderAgencyList, lsPartialUoDenom, liMinLength).toString()
						.trim();
				loOut.print(lsOutputJSONaoResponse);
				loOut.flush();
			}
			catch (final IOException aoExp)
			{
				LOG_OBJECT.Error("IOException occurred while searching providers:", aoExp);
			}
			catch (final Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occurred while searching providers:", aoExp);
			}
		}
		// added in r5
		else
		{
			aoRequest.getSession().setAttribute("dropDownList", loProviderAgencyList);
		}
		// ends r5 changes
		UserThreadLocal.unSet();
	}

	/**
	 * This method will generate the list of providers depending upon first 3
	 * initials of provider name entered by user.
	 * 
	 * @param aoInputList a list of provider ids and names
	 * @param aoPartialSearchTerms a string value of Search terms
	 * @param aiMinLength a length of search word
	 * @return a string buffer object containing provider names depending upon
	 *         search criteria
	 */
	final StringBuffer generateDelimitedResponse(final List<ProviderBean> aoInputList, String aoPartialSearchTerms,
			final int aiMinLength)
	{
		final StringBuffer loOutputBuffer = new StringBuffer();
		aoPartialSearchTerms = StringEscapeUtils.escapeJavaScript(aoPartialSearchTerms);
		final Pattern loPattern = Pattern.compile(aoPartialSearchTerms.toLowerCase(), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.LITERAL);
		loOutputBuffer.append("{");
		loOutputBuffer.append("\"query\":\"");
		loOutputBuffer.append(aoPartialSearchTerms);
		loOutputBuffer.append("\",");
		loOutputBuffer.append("\"suggestions\":");
		loOutputBuffer.append(AutoCompleteServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			ProviderBean loBeaniterator = aoInputList.get(liCount);
			final String lsCurrValue = (String) loBeaniterator.getDisplayValue();
			final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
			if ((lsCurrValue.length() >= aiMinLength) && lsMatcher.find())
			{
				loOutputBuffer.append(AutoCompleteServlet.DOUBLE_QUOTES);
				loOutputBuffer.append(lsCurrValue.replace("\"", AutoCompleteServlet.SINGLE_QUOTE));
				loOutputBuffer.append(AutoCompleteServlet.DQUOTES_COMMA);
			}
		}

		if (loOutputBuffer.indexOf(AutoCompleteServlet.COMMA) != -1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(AutoCompleteServlet.COMMA));
		}
		loOutputBuffer.append("],");
		loOutputBuffer.append("\"data\":");
		loOutputBuffer.append(AutoCompleteServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			ProviderBean loBeaniterator = aoInputList.get(liCount);
			final String lsCurrValue = (String) loBeaniterator.getDisplayValue();
			final String lsHiddenValue = (String) loBeaniterator.getHiddenValue();
			final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
			if (lsMatcher.find())
			{
				loOutputBuffer.append(AutoCompleteServlet.DOUBLE_QUOTES);
				loOutputBuffer.append(lsHiddenValue.replace("\"", AutoCompleteServlet.SINGLE_QUOTE));
				loOutputBuffer.append(AutoCompleteServlet.DQUOTES_COMMA);
			}
		}
		if (loOutputBuffer.indexOf(AutoCompleteServlet.COMMA) != -1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(AutoCompleteServlet.COMMA));
		}
		loOutputBuffer.append("]");
		loOutputBuffer.append("}");
		return loOutputBuffer;
	}
}