package com.nyc.hhs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.CommonUtil;
import com.nyc.hhs.util.PortalUtil;

/**
 * Servlet implementation class SearchServiceServlet SearchServiceServlet used
 * for searching a service from taxonomy, used on add service page(search)
 * 
 */

public class SearchServiceServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(SearchServiceServlet.class);
	private static final long serialVersionUID = 1L;

	public SearchServiceServlet()
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
	 * initiated from a jsp, it process the action by calling transactions to
	 * the end.
	 * 
	 * @param aoRequest HttpServlet request object
	 * @param aoResponse HttpServlet response object
	 * @throws ServletException If an Servlet Exception occurs
	 * @throws IOException If an Input Output Exception occurs
	 */
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		String lsData = PortalUtil.parseQueryString(aoRequest, "searchText");
		String lsOrgId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ORG);
		String lsAppId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_BUSINESS_APP_ID);
		String lsUserEmail = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_EMAIL_ID);
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		try
		{
			Channel loChannelobj = new Channel();
			loChannelobj.setData("asData", lsData);
			CommonUtil.addAuditDataToChannel(loChannelobj, lsOrgId, ApplicationConstants.TAXONOMY_SEARCH_EVENT_NAME,
					ApplicationConstants.TAXONOMY_SEARCH_EVENT_TYPE, new Date(System.currentTimeMillis()), lsUserEmail,
					lsData, ApplicationConstants.TAXONOMY_SEARCH_ENTITY_TYPE,
					ApplicationConstants.TAXONOMY_SEARCH_ENTITY_ID, ApplicationConstants.FALSE, lsAppId, "",
					ApplicationConstants.AUDIT_TYPE_GENERAL);
			loChannelobj.setData("EntityIdentifier", ApplicationConstants.TAXONOMY_SEARCH_ENTITY_IDENTIFIER);
			// Transaction to check and get related search results from synonym
			// and taxonomy master
			TransactionManager.executeTransaction(loChannelobj, "getSearchResultService");
			List<TaxonomyTree> loList = (List<TaxonomyTree>) loChannelobj.getData("loListTaxonomy");
			StringBuffer loData = new StringBuffer();
			aoResponse.setContentType("text/html");
			PrintWriter loOut = aoResponse.getWriter();
			// generate json object if result size greater than 0
			if (!loList.isEmpty())
			{
				Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.TAXONOMY_ELEMENT);
				loData.append("{\"taxonomyList\": [");
				for (TaxonomyTree loTree : loList)
				{
					loData.append("{\"id\": \"").append(loTree.getMsElementid()).append("\", ")
							.append("\"description\": \"")
							.append(StringEscapeUtils.escapeXml(loTree.getMsElementDescription())).append("\", ")
							.append("\"qualifiedName\": \"")
							.append(BusinessApplicationUtil.getTaxonomyName(loTree.getMsElementid(), loDoc))
							.append("\", ").append("\"name\": \"")
							.append(StringEscapeUtils.escapeXml(loTree.getMsElementName())).append("\"},");
				}
				loOut.write(loData.substring(0, loData.length() - 1) + "]}");
			}
			else
			{
				// sets error message
				loData.append("{\"error\": \"Your search did not return any results. Please search again or browse for services.\"}");
				loOut.write(loData.toString());
			}
			loOut.flush();
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while executing transaction  ", aoAppEx);
		}
		UserThreadLocal.unSet();
	}
}
