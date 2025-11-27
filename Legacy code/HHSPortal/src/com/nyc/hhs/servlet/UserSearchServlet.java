package com.nyc.hhs.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.UserThreadLocal;

public class UserSearchServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final LogInfo LOG_OBJECT = new LogInfo(UserSearchServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String SINGLE_QUOTE = "\'";
	private static final String DQUOTES_COMMA = "\",";
	private static final String DOUBLE_QUOTES = "\"";
	private static final String SQUARE_BRAC_BEGIN = "[";
	private static final String COMMA = ",";

	public UserSearchServlet()
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
	 * 
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
		List<StaffDetails> loStaffDetailsList = new ArrayList<StaffDetails>();
		String lsUserId = (String) aoRequest.getSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		UserThreadLocal.setUser(lsUserId);
		StaffDetails loStaffDetailsBean = new StaffDetails();
		loStaffDetailsBean.setMsStaffEmail("%" + lsPartialUoDenom + "%");
		Channel loChannel = new Channel();
		loChannel.setData("aoStaffDetails", loStaffDetailsBean);

		try
		{
			TransactionManager.executeTransaction(loChannel, "searchUserOnEmailId");
			loStaffDetailsList = (List<StaffDetails>) loChannel.getData("aoStaffDetailBean");
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Application Exception occurred while getting list of  ProviderBean" + aoEx);
		}
		final int liMinLength = 3;

		if ((lsPartialUoDenom != null) && (lsPartialUoDenom.length() >= liMinLength))
		{
			try
			{
				final PrintWriter loOut = aoResponse.getWriter();
				aoResponse.setContentType("application/json");

				final String lsOutputJSONaoResponse = this
						.generateDelimitedResponse(loStaffDetailsList, lsPartialUoDenom, liMinLength).toString().trim();
				loOut.print(lsOutputJSONaoResponse);
				loOut.flush();
			}
			catch (final IOException aoExp)
			{
				LOG_OBJECT.Error("IOException occurred while searching providers:" + aoExp.getMessage());
			}
			catch (final Exception aoExp)
			{
				LOG_OBJECT.Error("Exception occurred while searching providers:" + aoExp.getMessage());
			}
		}
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
	final StringBuffer generateDelimitedResponse(final List<StaffDetails> aoInputList, String aoPartialSearchTerms,
			final int aiMinLength)
	{
		final StringBuffer loOutputBuffer = new StringBuffer();
		aoPartialSearchTerms = StringEscapeUtils.escapeJavaScript(aoPartialSearchTerms);
		final Pattern loPattern = Pattern.compile(aoPartialSearchTerms.toLowerCase(), Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL | Pattern.LITERAL);
		loOutputBuffer.append("{");
		loOutputBuffer.append("\"query\":'");
		loOutputBuffer.append(aoPartialSearchTerms);
		loOutputBuffer.append("',");
		loOutputBuffer.append("\"suggestions\":");
		loOutputBuffer.append(UserSearchServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			StaffDetails loBeaniterator = aoInputList.get(liCount);
			final String lsCurrValue = (String) loBeaniterator.getMsStaffEmail();
			final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
			if ((lsCurrValue.length() >= aiMinLength) && lsMatcher.find())
			{
				loOutputBuffer.append(UserSearchServlet.DOUBLE_QUOTES);
				loOutputBuffer.append(lsCurrValue.replace("\"", UserSearchServlet.SINGLE_QUOTE));
				loOutputBuffer.append(UserSearchServlet.DQUOTES_COMMA);
			}
		}
		if (loOutputBuffer.indexOf(UserSearchServlet.COMMA) != -1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(UserSearchServlet.COMMA));
		}
		loOutputBuffer.append("],");
		loOutputBuffer.append("\"data\":");
		loOutputBuffer.append(UserSearchServlet.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoInputList.size(); liCount++)
		{
			StaffDetails loBeaniterator = aoInputList.get(liCount);
			final String lsCurrValue = (String) loBeaniterator.getMsStaffEmail();
			final String lsHiddenValue = (String) loBeaniterator.getMsStaffId();
			final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
			if (lsMatcher.find())
			{
				loOutputBuffer.append(UserSearchServlet.DOUBLE_QUOTES);
				loOutputBuffer.append(lsHiddenValue.replace("\"", UserSearchServlet.SINGLE_QUOTE));
				loOutputBuffer.append(UserSearchServlet.DQUOTES_COMMA);
			}
		}
		if (loOutputBuffer.indexOf(UserSearchServlet.COMMA) != -1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(UserSearchServlet.COMMA));
		}
		loOutputBuffer.append("]");
		loOutputBuffer.append("}");
		return loOutputBuffer;
	}

}
