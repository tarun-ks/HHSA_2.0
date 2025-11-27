package com.nyc.hhs.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.UserThreadLocal;
import com.nyc.hhs.util.PortalUtil;

/**
 * Servlet implementation class SaveTextSizeInSession 
 * SaveTextSizeInSession used for saving font size in session
 * 
 */

public class SaveTextSizeInSession extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 1L;

	public SaveTextSizeInSession()
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
	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException, IOException
	{
		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method handle the post request of a servlet. When a action is
	 * initiated from a jsp, it process the action by calling transactions to
	 * the end.
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
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException, IOException
	{
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
        UserThreadLocal.setUser(lsUserId);
        
		String lsAction = PortalUtil.parseQueryString(aoRequest, ApplicationConstants.NEXT_ACTION);
		if(null != lsAction && lsAction.equalsIgnoreCase("aaaValueToSet")){
			aoRequest.getSession().setAttribute("aaaValueToSet", PortalUtil.parseQueryString(aoRequest, "aaaValueToSet"));
			aoRequest.setAttribute("aaaValueToSet", PortalUtil.parseQueryString(aoRequest, "aaaValueToSet"));
		}
		final PrintWriter loOut = aoResponse.getWriter();
		final StringBuffer loOutputBuffer = new StringBuffer();
		loOutputBuffer.append(System.currentTimeMillis());
		loOut.print(loOutputBuffer.toString());
		loOut.flush();
		loOut.close();
		UserThreadLocal.unSet();
	}
}
