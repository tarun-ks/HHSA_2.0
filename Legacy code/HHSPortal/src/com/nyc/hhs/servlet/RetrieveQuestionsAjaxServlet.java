package com.nyc.hhs.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.RegisterNycIdBean;
import com.nyc.hhs.model.SecurityQuestionBean;
import com.nyc.hhs.model.UserThreadLocal;

/**
 * This Servlet populates and validates security questions and answers. Under
 * development
 * 
 */

public class RetrieveQuestionsAjaxServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(RetrieveQuestionsAjaxServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * This method is a constructor method used to call super method of class.
	 * 
	 */
	public RetrieveQuestionsAjaxServlet()
	{
		super();
	}

	/**
	 * This method is calling doPost method of servlet for processing security
	 * questions
	 * 
	 * @param aoRequest a HttpServletRequest aoRequest object
	 * @param aoResponse a HttpServletRequest aoResponse object
	 */

	protected void doGet(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		this.doPost(aoRequest, aoResponse);
	}

	/**
	 * This method is to provide below functionality 1. populate three different
	 * security questions 2. no three answers cann't not be same
	 * 
	 * @param aoRequest a HttpServletRequest request object
	 * @param aoResponse a HttpServletRequest response object
	 */
	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest aoRequest, HttpServletResponse aoResponse) throws ServletException,
			IOException
	{
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		try
		{
			List loFirstQuestionList = null;
			List loSecondQuestionList = null;
			List loThirdQuestionList = new ArrayList();
			String lsSelectedCombo = aoRequest.getParameter("selectedCombo");
			String lsSelectedQuestion = aoRequest.getParameter("selsetedQuetion");
			int liSelectedQuestion = Integer.parseInt(lsSelectedQuestion);
			SecurityQuestionBean loSecurityQuestionBean = null;
			RegisterNycIdBean loRegisterNycIdBean = null;
			if (aoRequest.getSession().getAttribute("RegisterNycIdBean") != null)
			{
				loRegisterNycIdBean = (RegisterNycIdBean) aoRequest.getSession().getAttribute("RegisterNycIdBean");
			}
			try
			{
				final PrintWriter loOut = aoResponse.getWriter();
				aoResponse.setContentType("text/html; charset=utf-8");
				String lsOutputaoResponse = "";
				if ("securityQuestion1".equalsIgnoreCase(lsSelectedCombo))
				{
					loFirstQuestionList = (List) copy(loRegisterNycIdBean.getMoSecurityQuestion1List());
					for (int liCntr = 0; liCntr < loFirstQuestionList.size(); liCntr++)
					{
						loSecurityQuestionBean = (SecurityQuestionBean) loFirstQuestionList.get(liCntr);
						if (liSelectedQuestion == loSecurityQuestionBean.getMiquestionId())
						{
							loFirstQuestionList.remove(liCntr);
							break;
						}
					}
					loRegisterNycIdBean.setMoSecurityQuestion2List(loFirstQuestionList);
					loRegisterNycIdBean.setMoSecurityQuestion3List(loThirdQuestionList);
					aoRequest.getSession().setAttribute("RegisterNycIdBean", loRegisterNycIdBean);
					lsOutputaoResponse = this.generateDelimitedResponse(loFirstQuestionList).toString().trim();
				}
				else if ("securityQuestion2".equalsIgnoreCase(lsSelectedCombo))
				{
					loSecondQuestionList = (List) copy(loRegisterNycIdBean.getMoSecurityQuestion2List());
					for (int liCntr = 0; liCntr < loSecondQuestionList.size(); liCntr++)
					{
						loSecurityQuestionBean = (SecurityQuestionBean) loSecondQuestionList.get(liCntr);
						if (liSelectedQuestion == loSecurityQuestionBean.getMiquestionId())
						{
							loSecondQuestionList.remove(liCntr);
							break;
						}
					}
					loRegisterNycIdBean.setMoSecurityQuestion3List(loSecondQuestionList);
					aoRequest.getSession().setAttribute("RegisterNycIdBean", loRegisterNycIdBean);
					lsOutputaoResponse = this.generateDelimitedResponse(loSecondQuestionList).toString().trim();
				}
				loOut.print(lsOutputaoResponse);
				loOut.flush();
			}
			catch (final IOException loAppEx)
			{
				LOG_OBJECT.Error("IOException occured in Retrieve Questions", loAppEx);
			}
			catch (final Exception loAppEx)
			{
				LOG_OBJECT.Error("Exception occured in Retrieve Questions", loAppEx);
			}
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in RetrieveQuestionsAjaxServlet.doPost ", loAppEx);
		}
		UserThreadLocal.unSet();
	}

	/**
	 * This method is to generate delimited response by operating on security
	 * question list
	 * 
	 * @param aoQuestionList list of security question that is to be delimited
	 * @return StringBuffer delimited security questions
	 * 
	 */
	@SuppressWarnings("rawtypes")
	final StringBuffer generateDelimitedResponse(final List aoQuestionList)
	{
		final StringBuffer lsOutputBuffer = new StringBuffer();

		SecurityQuestionBean loSecurityQuestionBean = null;
		for (int liCntr = 0; liCntr < aoQuestionList.size(); liCntr++)
		{
			loSecurityQuestionBean = (SecurityQuestionBean) aoQuestionList.get(liCntr);
			String loText = loSecurityQuestionBean.getMsQuestionText();
			String loValue = String.valueOf(loSecurityQuestionBean.getMiquestionId());
			lsOutputBuffer.append(loValue);
			lsOutputBuffer.append(":");
			lsOutputBuffer.append(loText);
			lsOutputBuffer.append("|");
		}
		lsOutputBuffer.append("status:success");

		return lsOutputBuffer;
	}

	/**
	 * This method is to make clone of security question list
	 * 
	 * @param aoOrig original object whose clone is to be created
	 * @throws ApplicationException
	 */

	public Object copy(Object aoOrig) throws ApplicationException
	{
		Object loObj = null;
		ObjectInputStream loInpStream = null;
		ObjectOutputStream loOutStream = null;
		ByteArrayOutputStream loByteArrayOutStrm = null;
		try
		{
			// Write the object out to a byte array
			loByteArrayOutStrm = new ByteArrayOutputStream();
			loOutStream = new ObjectOutputStream(loByteArrayOutStrm);
			loOutStream.writeObject(aoOrig);
			loOutStream.flush();
			loOutStream.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			loInpStream = new ObjectInputStream(new ByteArrayInputStream(loByteArrayOutStrm.toByteArray()));
			loObj = loInpStream.readObject();
		}
		catch (IOException loAppEx)
		{
			LOG_OBJECT.Error("IOException occured in making clone of security question list", loAppEx);
		}
		catch (ClassNotFoundException loAppEx)
		{
			LOG_OBJECT.Error("ClassNotFoundException occured in making clone of security question list", loAppEx);
		}
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Exception occured in RetrieveQuestionsAjaxServlet.copy() ", loAppEx);
		}
		finally
		{
			try
			{
				if (null != loInpStream)
				{
					loInpStream.close();
				}
				if (null != loByteArrayOutStrm)
				{
					loByteArrayOutStrm.close();
				}
			}
			catch (IOException loIoEx)
			{
				ApplicationException loAppex = new ApplicationException("Error While Closing Input Stream", loIoEx);
				throw loAppex;
			}
		}
		return loObj;
	}
}