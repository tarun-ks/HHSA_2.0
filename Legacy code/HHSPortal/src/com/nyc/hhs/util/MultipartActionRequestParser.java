package com.nyc.hhs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.portlet.ActionRequest;
import javax.portlet.PortletException;

/**
 * This class has operations that takes the content from the multipart form.
 * This is called from the 'processAction' method of a JSR286 portlet and uses
 * actionrequest to perform operations.
 * 
 */

public class MultipartActionRequestParser
{
	// The MIME prefix for all multipart media types
	private static final String MULTIPART_PREFIX = "multipart/";
	// The MIME content type for the file upload form data
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	// The MIME content type for plain text data
	private static final String PLAIN_TEXT = "text/plain";
	private ActionRequest loActionRequest;
	// these are initialized lazily
	private Map<String, List<String>> loFormParams;
	private Map<String, List<MimeBodyPart>> loFormBodyParts;
	private boolean mbIsInitialized = false;
	private boolean mbHasMultipart = false;

	/**
	 * Constructor
	 */
	public MultipartActionRequestParser(ActionRequest aoRequest)
	{
		loActionRequest = aoRequest;
	}

	/**
	 * This method will get parameter name from request depending upon input
	 * string
	 * 
	 * @param asName
	 *            a string value of parameter name
	 * @return a string value of parameter name from request
	 * @throws PortletException
	 *             If a Portlet Exception occurs
	 */
	public String getParameter(String asName) throws PortletException
	{
		String loValue = loActionRequest.getParameter(asName);
		if (loValue != null)
		{
			return loValue;
		}
		initParameters();
		if (!mbHasMultipart)
		{
			return null;
		}
		List<String> loParamList = (List<String>) loFormParams.get(asName);
		if (loParamList != null)
		{
			return (String) loParamList.get(0);
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method will get the parameter values for the input parameter name
	 * 
	 * @param asName
	 *            a string value of parameter name
	 * @return a string array of parameter values
	 * @throws PortletException
	 *             If a Portlet Exception occurs
	 */
	public String[] getParameterValues(String asName) throws PortletException
	{
		String[] lsValues = loActionRequest.getParameterValues(asName);
		if (!mbHasMultipart)
		{
			return lsValues;
		}
		initParameters();
		List<String> loValueList = loFormParams.get(asName);
		if (loValueList == null)
		{
			return lsValues;
		}
		else
		{
			int liSize = loValueList.size();
			if (lsValues != null)
			{
				List<String> loNewValueList = new ArrayList<String>(lsValues.length + liSize);
				loNewValueList.addAll(Arrays.asList(lsValues));
				loNewValueList.addAll(loValueList);
				loValueList = loNewValueList;
			}
			lsValues = new String[liSize];
			loValueList.toArray(lsValues);
			return lsValues;
		}
	}

	/**
	 * This method will get list of MimeBodyPart for input parameter name
	 * 
	 * @param asName
	 *            a string value of parameter name
	 * @return a list of MimeBodyPart for input parameter name
	 * @throws PortletException
	 *             If a Portlet Exception occurs
	 */
	public MimeBodyPart getMimeBodyPart(String asName) throws PortletException
	{
		initParameters();
		if (!mbHasMultipart)
		{
			return null;
		}
		List<MimeBodyPart> loParts = (List<MimeBodyPart>) loFormBodyParts.get(asName);
		return loParts == null ? null : (MimeBodyPart) loParts.get(0);
	}

	/**
	 * This method will get an array of MimeBodyPart for input parameter name
	 * 
	 * @param asName
	 *            a string value of parameter name
	 * @return an array of MimeBodyPart for input parameter name
	 * @throws PortletException
	 *             If a Portlet Exception occurs
	 */
	public MimeBodyPart[] getMimeBodyParts(String asName) throws PortletException
	{
		initParameters();
		if (!mbHasMultipart)
		{
			return null;
		}
		List<MimeBodyPart> loParts = loFormBodyParts.get(asName);
		if (loParts == null)
		{
			return null;
		}
		MimeBodyPart[] loMimeBodyParts = new MimeBodyPart[loParts.size()];
		loParts.toArray(loMimeBodyParts);
		return loMimeBodyParts;
	}

	/**
	 * This method will initialize the default parameters on class loading
	 * 
	 * @throws PortletException
	 *             If a Portlet Exception occurs
	 */
	private void initParameters() throws PortletException
	{
		if (mbIsInitialized)
		{
			return;
		}
		String lsContentType = loActionRequest.getContentType();
		if (lsContentType == null)
		{
			mbIsInitialized = true;
			return;
		}
		int liSepIndex = lsContentType.indexOf(';');
		if (liSepIndex != -1)
		{
			lsContentType = lsContentType.substring(0, liSepIndex).trim();
		}
		if (lsContentType.equalsIgnoreCase(MULTIPART_FORM_DATA))
		{
			loFormParams = new HashMap<String, List<String>>(20);
			loFormBodyParts = new HashMap<String, List<MimeBodyPart>>(20);
			DataSource loDatasource = new DataSource()
			{
				public InputStream getInputStream() throws IOException
				{
					return loActionRequest.getPortletInputStream();
				}

				public OutputStream getOutputStream() throws IOException
				{
					throw new IOException("OutputStream not available");
				}

				public String getContentType()
				{
					return loActionRequest.getContentType();
				}

				public String getName()
				{
					return getClass().getName();
				}
			};
			try
			{
				MimeMultipart loMultipartMessage = new MimeMultipart(loDatasource);
				parseMultiPart(loMultipartMessage, null);
			}
			catch (MessagingException aoExp)
			{
				throw new PortletException(aoExp);
			}
			catch (IOException aoExp)
			{
				throw new PortletException(aoExp);
			}
			mbHasMultipart = true;
		}
		mbIsInitialized = true;
	}

	/**
	 * This method will Go through each body part, decided on its 'type' and add
	 * to the parameter map and file list as appropriate
	 * 
	 * @param aoMultipartMessage
	 *            a Multipart message object
	 * @param asParentFieldName
	 *            a string value of parent field name
	 * @throws MessagingException
	 *             If a Messaging Exception occurs
	 * @throws IOException
	 *             If an Input Output Exception occurs
	 */
	private void parseMultiPart(MimeMultipart aoMultipartMessage, String asParentFieldName) throws MessagingException, IOException
	{
		int liPartCount = aoMultipartMessage.getCount();
		BodyPart loPart;
		MimeBodyPart loMimePart;
		Object loContent;
		String[] lsArrDisps;
		String lsDisp, lsDispTwo;
		List<MimeBodyPart> loPartValues;
		List<String> lsValues;
		for (int liCount = 0; liCount < liPartCount; liCount++)
		{
			loPart = aoMultipartMessage.getBodyPart(liCount);
			if (!(loPart instanceof MimeBodyPart))
			{
				continue;
			}
			loMimePart = (MimeBodyPart) loPart;
			// The Content Disposition header tells us how to treat the body
			// part
			lsArrDisps = loMimePart.getHeader("Content-Disposition");
			if (lsArrDisps == null || lsArrDisps.length == 0)
			{
				continue;
			}
			lsDisp = lsArrDisps[0];
			lsDispTwo = lsDisp.toLowerCase();
			// Get the field name out of the disposition header, if present
			int liNameStart, liNameEnd;
			if ((liNameStart = lsDispTwo.indexOf("name=\"")) != -1 && (liNameEnd = lsDispTwo.indexOf("\"", liNameStart + 6)) != -1)
			{
				asParentFieldName = lsDisp.substring(liNameStart + 6, liNameEnd);
			}
			// If we don't have a field name, there's not much we can do with
			// this body part
			if (asParentFieldName == null)
			{
				continue;
			}
			// If this is a multipart body part, we recurse on its contents
			// using
			// the current field name
			if (loMimePart.getContentType().toLowerCase().startsWith(MULTIPART_PREFIX))
			{
				loContent = loMimePart.getContent();
				if (loContent instanceof MimeMultipart)
				{
					parseMultiPart((MimeMultipart) loContent, asParentFieldName);
				}
			}
			// Decide whether this is a parameter or a file, according to
			// whether
			// the filename attribute is present in the disposition header
			else if ((liNameStart = lsDispTwo.indexOf("filename=\"")) != -1 && (liNameEnd = lsDispTwo.indexOf("\"", liNameStart + 10)) != -1)
			{
				loPartValues = loFormBodyParts.get(asParentFieldName);
				if (loPartValues == null)
				{
					loPartValues = new ArrayList<MimeBodyPart>();
					loFormBodyParts.put(asParentFieldName, loPartValues);
				}
				loPartValues.add(loMimePart);
				// get the Filename from header -- so we can clip the path if it
				// exists.
				String lsFilename = getFileNameFromHeader(lsDispTwo.substring(liNameStart + 10, lsDispTwo.length() - 1));
				if (!(lsFilename).equals(loMimePart.getFileName()))
				{
					loMimePart.setFileName(lsFilename);
				}
			}
			else if (loMimePart.getContentType().toLowerCase().startsWith(PLAIN_TEXT))
			{
				loContent = loMimePart.getContent();
				if (loContent instanceof String)
				{
					lsValues = loFormParams.get(asParentFieldName);
					if (lsValues == null)
					{
						lsValues = new ArrayList<String>();
						loFormParams.put(asParentFieldName, lsValues);
					}
					lsValues.add((String) loContent);
				}
			}
		} // end for each body part
	}

	/**
	 * This method will get valid file name from request header's parameter file
	 * name
	 * 
	 * @param filename
	 *            a string value of file name
	 * @return a string value of file name
	 */
	private String getFileNameFromHeader(String asFilename)
	{
		// The filename may contain a full path. Cut to just the filename.
		int liSlash = Math.max(asFilename.lastIndexOf('/'), asFilename.lastIndexOf('\\'));
		if (liSlash > -1)
		{
			asFilename = asFilename.substring(liSlash + 1); // past last slash
		}
		return asFilename;
	}
}
