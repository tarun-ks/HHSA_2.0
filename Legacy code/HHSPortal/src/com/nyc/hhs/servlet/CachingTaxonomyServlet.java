package com.nyc.hhs.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.CacheList;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * It gets executed at server startup Reads elements to be cached from
 * cache.properties If taxonomy, it builds taxonomy DOM from database else
 * caches as DOM object in cache
 * 
 */
public class CachingTaxonomyServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	
	private static final long serialVersionUID = 1L;

	/**
	 * This is the non-parameterized constructor 
	 * 
	 */
	public CachingTaxonomyServlet()
	{
		super();
	}

	/**
	 * This method handle the get request of a servlet. This will internally
	 * call doPost method to process the servlet request and return the
	 * response.
	 * 
	 * @param aoRequest
	 *            HttpServlet request object
	 * @param aoResponse
	 *            HttpServlet response object
	 * @throws ServletException
	 *             If an Servlet Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs          
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
	 * 
	 * @param aoRequest
	 *            HttpServlet request object
	 * @param aoResponse
	 *            HttpServlet response object
	 * @throws ServletException
	 *             If an Servlet Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		LogInfo loLogger = new LogInfo(CachingTaxonomyServlet.class);
		ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		Object loCacheObject = null;
		CacheList loCacheList = CacheList.getInstance();
		try
		{
			ResourceBundle loRB = PropertyLoader.getProperties(ApplicationConstants.CACHE_FILES);
			Iterator loITer = loRB.keySet().iterator();
			while (loITer.hasNext())
			{
				String lsKey = (String) loITer.next();
				String lsFilePath = loRB.getString(lsKey);
				if (ApplicationConstants.TAXONOMY_ELEMENT.equalsIgnoreCase(lsKey))
				{
					// Method to fetch Taxonomy Master data from database and
					// set it in Cache
					PropertyUtil loTaxonomyUtil = new PropertyUtil();
					loTaxonomyUtil.setTaxonomyInCache(loCacheManager, lsKey);
					loLogger.Debug("Sucessfuly added Taxonomy DOM in Cache");
				}
				else
				{
					loCacheObject = XMLUtil.getDomObj(CachingTaxonomyServlet.class.getResourceAsStream(lsFilePath));
					loCacheList.putCacheLoader(loCacheObject);
					loCacheManager.putCacheObject(lsKey, loCacheObject);
				}
			}
		}
		catch (ApplicationException aoError)
		{
			loLogger.Error("Error occured while getting cache object.", aoError);
		}
		UserThreadLocal.unSet();
	}
}
