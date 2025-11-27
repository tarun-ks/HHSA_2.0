package com.nyc.hhs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.input.SAXHandler;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

//import sun.misc.BASE64Decoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.accenture.util.SaveFormOnLocalUtil;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.AddressValidationBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.EntityStatusBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvidenceBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.FiscalDate;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.MasterStatusBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.TaskAuditBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class has utility function for procurement, provider etc
 * This class has been updated in R7 for Modification Auto Approval Enhancement.
 * 
 */
public class HHSUtil extends CommonUtil
{
	/**
	 * LogInfo Object
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(HHSUtil.class);
	private static String CASTOR_FILE_PATH = "";
	
	static{
		CASTOR_FILE_PATH = getCastorPath();
	}

	/**
	 * Returns list of services with evidence flag
	 * 
	 * @return Map
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getServicesList() throws ApplicationException
	{
		Map<String, String> loIdNameMap = new HashMap<String, String>();
		try
		{
			Document loTaxonomyDom = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.TAXONOMY_ELEMENT);
			String lsXPathTop = HHSConstants.SERVICE_STRING;
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loTaxonomyDom);
			Element loNewElt = null;
			if (!loNodeList.isEmpty())
			{
				loNewElt = (Element) loNodeList.get(0).clone();
				for (int liCount = 1; liCount < loNodeList.size(); liCount++)
				{
					Element loElt = loNodeList.get(liCount);
					List<Element> loChildren = loElt.getChildren(HHSConstants.HHSUTIL_ELEMENT);
					for (Element loEltChild : loChildren)
					{
						loNewElt.addContent((Element) loEltChild.clone());
					}
				}
			}
			if (null != loNewElt)
			{
				getServicesList(loNewElt, loIdNameMap);
			}
			loIdNameMap.put(HHSConstants.EMPTY_STRING, HHSConstants.EMPTY_STRING);
			loIdNameMap = sortByValues(loIdNameMap);
		}
		// handling Application Exception if list of services with evidence flag
		// is not proper
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while fetching list of services with evidence flag", aoAppEx);
			throw aoAppEx;

		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while fetching list of services with evidence flag", aoEx);
			LOG_OBJECT.Error("Exception Occured while fetching list of services with evidence flag", loAppEx);
			throw loAppEx;
		}
		return loIdNameMap;
	}

	/**
	 * Returns list of services with evidence flag
	 * 
	 * @param aoElement - elements from which evidence to be searched
	 * @param aoIdNameMap -
	 * @return
	 */
	private static void getServicesList(Element aoElement, Map<String, String> aoIdNameMap)
	{
		@SuppressWarnings("unchecked")
		List<Element> loList = aoElement.getChildren(HHSConstants.HHSUTIL_ELEMENT);
		for (Element loElt : loList)
		{
			boolean lbToPass = false;
			if (loElt.getAttributeValue(HHSConstants.HHSUTIL_ACTIVEFLAG).equalsIgnoreCase(HHSConstants.ONE))
			{
				lbToPass = true;
			}
			if (lbToPass)
			{
				String lsId = loElt.getAttributeValue(HHSConstants.ID);
				String lsName = loElt.getAttributeValue(HHSConstants.NAME);
				if (loElt.getAttributeValue(HHSConstants.EVE_REQ_FLAG).equalsIgnoreCase(HHSConstants.STRING_ZERO))
				{
					getServicesList(loElt, aoIdNameMap);
				}
				else
				{
					aoIdNameMap.put(lsId, lsName);
				}
			}
		}
	}

	/**
	 * Sorts Map on value
	 * 
	 * @param <K> key
	 * @param <V> Value
	 * @param aoMap Map
	 * @return return sorted values
	 * @throws ApplicationException Application Exception
	 */
	public static <K, String extends Comparable<String>> Map<K, String> sortByValues(final Map<K, String> aoMap)
			throws ApplicationException
	{
		Map<K, String> loSortedByValues = null;
		try
		{
			Comparator<K> loValueComparator = new Comparator<K>()
			{
				@Override
				public int compare(K aoK1, K aoK2)
				{
					int liCompare = ((String) aoMap.get(aoK2)).compareTo(
							((String) aoMap.get(aoK1)));
					if (liCompare == 0)
					{
						return 1;
					}
					else
					{
						return liCompare * -1;
					}
				}
			};
			loSortedByValues = new TreeMap<K, String>(loValueComparator);
			loSortedByValues.putAll(aoMap);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While preparing json response object", aoEx);
			LOG_OBJECT.Error("Error Occured While preparing json response object", loAppEx);
			throw loAppEx;
		}
		return loSortedByValues;
	}

	/**
	 * This method returns the date with time(Hours, minutes, seconds,
	 * milliseconds set to 00:00:00)
	 * 
	 * @param aoDate input date
	 * @return date with time set to 00:00:00
	 */
	public static Date getZeroTimeDate(Date aoDate) throws ApplicationException
	{
		Date loDate = aoDate;
		try
		{
			Calendar loCalendar = Calendar.getInstance();

			loCalendar.setTime(aoDate);
			loCalendar.set(Calendar.HOUR_OF_DAY, 0);
			loCalendar.set(Calendar.MINUTE, 0);
			loCalendar.set(Calendar.SECOND, 0);
			loCalendar.set(Calendar.MILLISECOND, 0);
			loDate = loCalendar.getTime();
		}

		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"ApplicationException occured while returning date with time"
							+ "(Hours, minutes, seconds,milliseconds set to 00:00:00)", aoEx);
			LOG_OBJECT.Error("Error Occured while returning date with time"
					+ "(Hours, minutes, seconds,milliseconds set to 00:00:00", loAppEx);
			throw loAppEx;
		}
		return loDate;
	}

	/**
	 * This method will generate the list of E-PIN depending upon first 3
	 * initials of E-pin entered by user
	 * <ul>
	 * <li>Create a String buffer with json data Array format</li>
	 * <li>after putting all the values in the string buffer return the string
	 * buffer</li>
	 * </ul>
	 * 
	 * @param aoInputList a list of provider ids and names
	 * @param aoPartialSearchTerms a string value of Search terms
	 * @param aiMinLength a length of search word
	 * @return a string buffer object containing provider names depending upon
	 *         search criteria
	 */
	/**
	 * @param aoInputList input list
	 * @param aoPartialSearchTerms search terms
	 * @param aiMinLength minimum length
	 * @return String Buffer
	 * @throws ApplicationException App Exception
	 */
	public static StringBuffer generateDelimitedResponse(final List<String> aoInputList,
			final String aoPartialSearchTerms, final int aiMinLength) throws ApplicationException
	{
		final StringBuffer loOutputBuffer = new StringBuffer();
		final StringBuffer loDataOutputBuffer = new StringBuffer();
		try
		{
			loOutputBuffer.append(HHSConstants.CRLI_BRACKT_START);
			loOutputBuffer.append(HHSConstants.HHSUTIL_APPEND_QUERY);
			loOutputBuffer.append(aoPartialSearchTerms);
			loOutputBuffer.append(HHSConstants.OPOSTOPHI_COMMA);
			loOutputBuffer.append(HHSConstants.HHSUTIL_SUGGESTIONS);
			loOutputBuffer.append(HHSConstants.SQUARE_BRAC_BEGIN);
			loDataOutputBuffer.append(HHSConstants.HHSUTIL_DATA);
			loDataOutputBuffer.append(HHSConstants.SQUARE_BRAC_BEGIN);
			for (int liCount = 0; liCount < aoInputList.size(); liCount++)
			{
				final String lsCurrValue = aoInputList.get(liCount);
				if (lsCurrValue.length() >= aiMinLength)
				{
					loOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
					loOutputBuffer.append(lsCurrValue.replace(HHSConstants.DOUBLE_QUOTES, HHSConstants.SINGLE_QUOTE));
					loOutputBuffer.append(HHSConstants.DQUOTES_COMMA);

					loDataOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
					loDataOutputBuffer.append(HHSConstants.DQUOTES_COMMA);
				}
			}
			if (loOutputBuffer.indexOf(HHSConstants.COMMA) != -1)
			{
				loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSConstants.COMMA));
			}
			loOutputBuffer.append(HHSConstants.RECTANGULAR_CLOSE_COMMA);
			if (loDataOutputBuffer.indexOf(HHSConstants.COMMA) != -1)
			{
				loDataOutputBuffer.deleteCharAt(loDataOutputBuffer.lastIndexOf(HHSConstants.COMMA));
			}
			loOutputBuffer.append(loDataOutputBuffer);
			loOutputBuffer.append(HHSConstants.SQUARE_BRAC_END);
			loOutputBuffer.append(HHSConstants.CRLI_BRACKT_END);

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While preparing json response object", aoEx);
			LOG_OBJECT.Error("Error Occured While preparing json response object", loAppEx);
			throw loAppEx;
		}
		return loOutputBuffer;
	}

	/**
	 * This method will generate the list of providers depending upon first 3
	 * initials of E-pin entered by user
	 * <ul>
	 * <li>Create pattern to match case insensitive</li>
	 * <li>Create a String buffer with json data Array format</li>
	 * <li>after putting all the values in the string buffer return the string
	 * buffer</li>
	 * </ul>
	 * 
	 * @param aoInputList a list of provider ids and names
	 * @param aoPartialSearchTerms a string value of Search terms
	 * @param aiMinLength a length of search word
	 * @return a string buffer object containing provider names depending upon
	 *         search criteria
	 * @throws ApplicationException App Exception
	 */
	public static StringBuffer generateDelimitedAutoCompleteResponse(final List<AutoCompleteBean> aoInputList,
			String aoPartialSearchTerms, final int aiMinLength) throws ApplicationException
	{
		final StringBuffer loOutputBuffer = new StringBuffer();
		try
		{
			final Pattern loPattern = Pattern.compile(aoPartialSearchTerms.toLowerCase(), Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL | Pattern.LITERAL);
			loOutputBuffer.append(HHSConstants.CRLI_BRACKT_START);
			loOutputBuffer.append("\"query\":\"");
			loOutputBuffer.append(StringEscapeUtils.escapeJavaScript(aoPartialSearchTerms));
			loOutputBuffer.append("\",");
			loOutputBuffer.append(HHSConstants.HHSUTIL_SUGGESTIONS);
			loOutputBuffer.append(HHSConstants.SQUARE_BRAC_BEGIN);
			for (int liCount = 0; liCount < aoInputList.size(); liCount++)
			{
				final String lsCurrValue = aoInputList.get(liCount).getDisplayName();
				final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
				if ((lsCurrValue.length() >= aiMinLength) && lsMatcher.find())
				{
					loOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
					loOutputBuffer.append(lsCurrValue.replace(HHSConstants.DOUBLE_QUOTES, HHSConstants.SINGLE_QUOTE));
					loOutputBuffer.append(HHSConstants.DQUOTES_COMMA);
				}
			}
			if (loOutputBuffer.indexOf(HHSConstants.COMMA) != -1)
			{
				loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSConstants.COMMA));
			}
			loOutputBuffer.append(HHSConstants.RECTANGULAR_CLOSE_COMMA);
			loOutputBuffer.append(HHSConstants.HHSUTIL_DATA);
			loOutputBuffer.append(HHSConstants.SQUARE_BRAC_BEGIN);
			for (int liCount = 0; liCount < aoInputList.size(); liCount++)
			{
				final String lsCurrValue = aoInputList.get(liCount).getDisplayName();
				final String lsHiddenValue = (String) aoInputList.get(liCount).getHiddenId();
				final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
				if (lsMatcher.find())
				{
					loOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
					loOutputBuffer.append(lsHiddenValue.replace(HHSConstants.DOUBLE_QUOTES, HHSConstants.SINGLE_QUOTE));
					loOutputBuffer.append(HHSConstants.DQUOTES_COMMA);
				}
			}
			if (loOutputBuffer.indexOf(HHSConstants.COMMA) != -1)
			{
				loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSConstants.COMMA));
			}
			loOutputBuffer.append(HHSConstants.SQUARE_BRAC_END);
			loOutputBuffer.append(HHSConstants.CRLI_BRACKT_END);
		}

		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While generating the list of providers", aoEx);
			LOG_OBJECT.Error("Error Occured While generating the list of providers", loAppEx);
			throw loAppEx;
		}
		return loOutputBuffer;
	}

	/**
	 * This method will add 50 days in current date and will return the output
	 */
	/**
	 * @return Time
	 * @throws ApplicationException Application Exception
	 */
	public static Date getToDate() throws ApplicationException
	{
		Calendar loCalenderToDate = new GregorianCalendar();
		try
		{
			loCalenderToDate.add(Calendar.YEAR, 50);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While adding 50 days in current date", aoEx);
			LOG_OBJECT.Error("Error Occured While adding 50 days in current date", loAppEx);
			throw loAppEx;
		}
		return loCalenderToDate.getTime();
	}

	/** This method will add from clause and to clause in the date */
	/**
	 * @param asFrom from
	 * @param asTo to
	 * @param asType type
	 * @return Date
	 * @throws ApplicationException Application Exception
	 */
	public static Date setDateToFrom(String asFrom, String asTo, String asType) throws ApplicationException
	{
		Date loDate = null;
		try
		{
			if (asType.equalsIgnoreCase(HHSConstants.FROM))
			{
				if (asFrom != null && !asFrom.isEmpty())
				{
					loDate = DateUtil.getDate(asFrom);
					loDate = HHSUtil.addFromClause(loDate);
				}
				else if (asTo != null && !asTo.isEmpty())
				{
					loDate = HHSConstants.FROM_DATE;
					loDate = HHSUtil.addFromClause(loDate);
				}
			}
			else
			{
				if (asTo != null && !asTo.isEmpty())
				{
					loDate = DateUtil.getDate(asTo);
					loDate = HHSUtil.addToClause(loDate);
				}
				else if (asFrom != null && !asFrom.isEmpty())
				{
					loDate = getToDate();
					loDate = HHSUtil.addToClause(loDate);
				}
			}
		}
		// handling Application Exception while performing clause in the date
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while adding from clause and to clause in the date", aoAppEx);
			throw aoAppEx;

		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while adding from clause and to clause in the date", aoEx);
			LOG_OBJECT.Error("Exception Occured while adding from clause and to clause in the date", loAppEx);
			throw loAppEx;
		}
		return loDate;
	}

	/**
	 * This Method Invokes hhsaudit transaction which insert the values in the
	 * Audit
	 * 
	 * calls the transaction 'hhsaudit'
	 * 
	 * @param aoAudit HhsAuditBean bean
	 * @throws ApplicationException Application Exception Thrown
	 */
	public static void setHhsAudit(HhsAuditBean aoAudit) throws ApplicationException
	{
		Channel loChannel = new Channel();
		try
		{
			loChannel.setData(HHSConstants.AUDIT_BEAN, aoAudit);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.HHSAUDIT);
		}
		// handling Application Exception while Invoking hhsaudit transaction
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while Invoking hhsaudit transaction which insert the values in the Audit"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while Invoking hhsaudit transaction which insert the values in the Audit", aoEx);
			LOG_OBJECT
					.Error("Exception Occured while Invoking hhsaudit transaction which insert the values in the Audit"
							+ loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This Method Invokes hhsaudit transaction which insert the values in the
	 * Audit
	 * 
	 * @param aoAudit HhsAuditBean bean
	 * @throws ApplicationException Application Exception Thrown
	 */
	// TODOO
	public static void deleteUserCommentsIfEmptyCommentsSaved(HhsAuditBean aoAudit) throws ApplicationException
	{
		Channel loChannel = new Channel();
		try
		{
			loChannel.setData(HHSConstants.AUDIT_BEAN, aoAudit);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			HHSTransactionManager.executeTransaction(loChannel,
					HHSConstants.HHS_DELETE_USER_COMMENTS_BLANK_COMMENTS_SAVE);
		}
		// handling Application Exception while Invoking hhsaudit transaction
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while Invoking hhsaudit transaction which insert the values in the Audit"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while Invoking hhsaudit transaction which insert the values in the Audit", aoEx);
			LOG_OBJECT
					.Error("Exception Occured while Invoking hhsaudit transaction which insert the values in the Audit"
							+ loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This Method Invokes hhsaudit transaction which insert the values in the
	 * Audit for Tab Level USer COmments Created for R4 Transaction Invoked:
	 * hhsauditTabLevel
	 * @param aoAudit HhsAuditBean bean
	 * @throws ApplicationException Application Exception Thrown
	 */
	public static void setHhsAuditForTabLevel(HhsAuditBean aoAudit, TaskDetailsBean aoTaskDetailsBean)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		try
		{
			loChannel.setData(HHSConstants.AUDIT_BEAN, aoAudit);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			loChannel.setData(HHSConstants.AO_TASK_DETAILS_BEAN, aoTaskDetailsBean);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.HHSAUDIT_TABLEVEL);
		}
		// handling Application Exception while Invoking hhsaudit transaction
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT
					.Error("ApplicationException Occured while Invoking hhsaudit transaction which insert the values in the Audit"
							+ aoAppEx);
			throw aoAppEx;

		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while Invoking hhsaudit transaction which insert the values in the Audit", aoEx);
			LOG_OBJECT
					.Error("Exception Occured while Invoking hhsaudit transaction which insert the values in the Audit"
							+ loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This Methods sets the Audit Data to Channel
	 * 
	 * @param aoChannel Channel Object
	 * @param asEventName Name Of the Event
	 * @param asEventType Type OF the Event
	 * @param asData Data to be inserted
	 * @param asEntityType Type of Entity
	 * @param asEntityId Id of Entity
	 * @param asUserID USer ID
	 * @param asTableIdentifier Table Identifier
	 * @param asAuditChannelName Audit Channel Name
	 * @throws ApplicationException Application Exception
	 */
	public static void addAuditDataToChannel(Channel aoChannel, String asEventName, String asEventType, String asData,
			String asEntityType, String asEntityId, String asUserID, String asTableIdentifier, String asAuditChannelName)
			throws ApplicationException
	{
		HhsAuditBean loAudit = new HhsAuditBean();
		try
		{
			loAudit.setEventName(asEventName);
			loAudit.setEventType(asEventType);
			loAudit.setData(asData);
			loAudit.setEntityType(asEntityType);
			loAudit.setEntityId(asEntityId);
			loAudit.setUserId(asUserID);
			loAudit.setAuditTableIdentifier(asTableIdentifier);
			aoChannel.setData(asAuditChannelName, loAudit);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting Audit Data to Channel", aoEx);
			LOG_OBJECT.Error("Error Occured While setting Audit Data to Channel", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method Add audit Bean list in Channel on Finish Of Financial Tasks
	 * <ul>
	 * <li>Create Audit bean object on Finish Event and add into Audit List</li>
	 * <li>If Provider comments are not null or empty add Audit for it</li>
	 * <li>If Agency comments are not null or empty add Audit for it</li>
	 * <li>Set Audit list in to Channel object</li>
	 * </ul>
	 * 
	 * @param aoTaskDetailsBean Task Detail Bean
	 * @param loChannel channel
	 * @throws ApplicationException Application Exception
	 */
	public static void setAuditOnFinancialFinishTask(TaskDetailsBean aoTaskDetailsBean, Channel loChannel)
			throws ApplicationException
	{
		List<HhsAuditBean> loAuditList = new ArrayList<HhsAuditBean>();
		String lsNewStatus = null;
		try
		{
			if (aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
			{
				lsNewStatus = HHSConstants.TASK_IN_REVIEW;
			}
			else
			{
				lsNewStatus = aoTaskDetailsBean.getTaskStatus();
			}
			if (null != aoTaskDetailsBean.getProviderComment()
					&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getProviderComment())))
			{
				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
						HHSConstants.AGENCY_COMMENTS_DATA, aoTaskDetailsBean.getProviderComment(),
						aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(),
						aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
			}
			if (null != aoTaskDetailsBean.getInternalComment()
					&& !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(aoTaskDetailsBean.getInternalComment())))
			{

				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS, aoTaskDetailsBean.getInternalComment(),
						aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(),
						aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
			}
			if (!lsNewStatus.equalsIgnoreCase(aoTaskDetailsBean.getCurrentTaskStatus()))
			{
				loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
						ApplicationConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE + HHSConstants.STR
								+ aoTaskDetailsBean.getCurrentTaskStatus() + HHSConstants.STR + HHSConstants.TO
								+ HHSConstants.STR + lsNewStatus + HHSConstants.STR, aoTaskDetailsBean.getEntityType(),
						aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
			}

			nextTaskAssignmentOnFinish(loAuditList, aoTaskDetailsBean);

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While adding audit Bean list in Channel on Finish Of Financial Tasks", aoEx);
			LOG_OBJECT
					.Error("Error Occured While adding audit Bean list in Channel on Finish Of Financial Tasks", aoEx);
			throw loAppEx;
		}
		loChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);

	}

	/**
	 * This method make entry in audit for next assignment on finish os task.
	 * @param aoAuditList List of audit bean
	 * @param aoTaskDetailsBean Task Detail Bean object
	 */
	private static void nextTaskAssignmentOnFinish(List<HhsAuditBean> aoAuditList, TaskDetailsBean aoTaskDetailsBean)
	{
		if (!(aoTaskDetailsBean.getTaskName().equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION) || aoTaskDetailsBean
				.getTaskName().equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_CONFIGURATION)))
		{
			boolean lbNextTaskAssignment = true;
			String lsCurrentLevel = aoTaskDetailsBean.getLevel();
			String lsTotalLevel = aoTaskDetailsBean.getTotalLevel();
			String lsUnassignedLevel = HHSConstants.SPACE + HHSConstants.TASK_UNASSIGNED;
			int liCurrentLevel = 1;
			int liTotalLevel = 1;
			if (null != lsCurrentLevel)
			{
				liCurrentLevel = Integer.valueOf(lsCurrentLevel);
			}
			if (null != lsTotalLevel)
			{
				liTotalLevel = Integer.valueOf(lsTotalLevel);
			}
			if (aoTaskDetailsBean.getEntityType().equalsIgnoreCase(HHSConstants.TASK_CONTRACT_COF)
					|| aoTaskDetailsBean.getEntityType().equalsIgnoreCase(HHSConstants.TASK_AMENDMENT_COF))
			{
				lbNextTaskAssignment = false;
			}
			if (liTotalLevel > liCurrentLevel
					&& (aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)))
			{
				lsUnassignedLevel = lsUnassignedLevel + (liCurrentLevel + 1);
				aoAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN,
						P8Constants.EVENT_NAME_ASSIGN, HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON
								+ lsUnassignedLevel, aoTaskDetailsBean.getEntityType(),
						aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));

			}

			if (liCurrentLevel > 1
					&& (aoTaskDetailsBean.getTaskStatus()
							.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
					&& lbNextTaskAssignment)
			{
				lsUnassignedLevel = lsUnassignedLevel + (liCurrentLevel - 1);
				aoAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.EVENT_NAME_ASSIGN,
						P8Constants.EVENT_NAME_ASSIGN, HHSConstants.TASK_ASSIGNED_TO + HHSConstants.COLON
								+ lsUnassignedLevel, aoTaskDetailsBean.getEntityType(),
						aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));

			}

			if (liCurrentLevel == 1
					&& (aoTaskDetailsBean.getTaskStatus()
							.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)))
			{
				String lsEntityType = (String) HHSConstants.AGENCY_PROVIDER_ENTITY_TYPE_MAP.get(aoTaskDetailsBean
						.getEntityType());
				if (null != lsEntityType)
				{
					aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
							HHSConstants.STATUS_CHANGE, ApplicationConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE
									+ HHSConstants.STR + HHSConstants.STATUS_PENDING_APPROVAL + HHSConstants.STR
									+ HHSConstants.TO + HHSConstants.STR
									+ ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS + HHSConstants.STR,
							lsEntityType, aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getUserId(),
							HHSConstants.AGENCY_AUDIT));
				}
			}
				
			if (liCurrentLevel == liTotalLevel
					&& (aoTaskDetailsBean.getTaskStatus().equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)))
			{
				String lsEntityType = (String) HHSConstants.AGENCY_PROVIDER_ENTITY_TYPE_MAP.get(aoTaskDetailsBean
						.getEntityType());
				if (null != lsEntityType)
				{
					aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
							HHSConstants.STATUS_CHANGE, ApplicationConstants.STATUS_CHANGED_FROM + HHSConstants.SPACE
									+ HHSConstants.STR + HHSConstants.STATUS_PENDING_APPROVAL + HHSConstants.STR
									+ HHSConstants.TO + HHSConstants.STR + aoTaskDetailsBean.getEntityStatus()
									+ HHSConstants.STR, lsEntityType, aoTaskDetailsBean.getEntityId(),
							aoTaskDetailsBean.getUserId(), HHSConstants.AGENCY_AUDIT));
				}
			}

		}

	}

	/**
	 * This method converts the subgrid data rows in json form.
	 * <ul>
	 * <li>Get the complete String from gridheader property file and tokenize it
	 * to get the header names.</li>
	 * <li>Iterate the list and form json form of data rows.</li>
	 * </ul>
	 * 
	 * @param aoBeanObj bean object populated with data
	 * @param aoListObj List Object consisting of data rows
	 * @param asRowsPerPage String rows per page to display
	 * @param asPage String page number to display
	 * @param asErrorMsg String error message if any error occured
	 * @param asfields String label define in gridheader property file
	 * @return a StringBuffer consisting of subgrid data rows.
	 * @throws ApplicationException Application Exception
	 * 
	 *             This method was updated in R4.
	 */
	@SuppressWarnings("rawtypes")
	public static StringBuffer populateSubGridRows(Object aoBeanObj, List aoListObj, String asRowsPerPage,
			String asPage, String asErrorMsg, String asfields) throws ApplicationException
	{
		StringBuffer loBuffer = new StringBuffer();
		StringBuffer loBufferTotal = null;
		try
		{
			String lsFieldsDetail = PropertyLoader
					.getProperty(HHSConstants.COM_NYC_HHS_PROPERTIES_GRIDHEADER, asfields);
			loBuffer.append("\"rows\":[");
			int liScreenRecordCount = 0;
			StringTokenizer loToken = null;
			if (null != aoListObj)
			{
				for (int liListCounter = 0; liListCounter < aoListObj.size(); liListCounter++)
				{
					loToken = new StringTokenizer(lsFieldsDetail, HHSConstants.HHSUTIL_DELIM_PIPE);
					aoBeanObj = aoBeanObj.getClass().cast(aoListObj.get(liListCounter));
					loBuffer.append("{\"id\":\"");
					loBuffer.append((String) BeanUtils.getProperty(aoBeanObj, "id"));
					while (loToken.hasMoreTokens())
					{
						String lsTokenVal = loToken.nextToken();
						StringTokenizer loTokenSubHeader = new StringTokenizer(lsTokenVal,
								HHSConstants.HHSUTIL_DELIM_ESCLAMATION);
						while (loTokenSubHeader.hasMoreTokens())
						{
							String lsTokenSubHeaderVal = loTokenSubHeader.nextToken().trim();
							if (lsTokenSubHeaderVal.startsWith(HHSConstants.NAME))
							{
								String[] loArray = lsTokenSubHeaderVal.split(HHSConstants.COLON);
								loBuffer.append("\",\"");
								loBuffer.append(loArray[1].trim());
								loBuffer.append(HHSConstants.UNDERSCORESCORE);
								loBuffer.append(StringEscapeUtils.escapeJavaScript((String) BeanUtils.getProperty(
										aoBeanObj, loArray[1].trim())));
								break;
							}
						}
					}
					loBuffer.append("\"},");
					liScreenRecordCount++;
				}
			}
			loBuffer.append(HHSConstants.SQUARE_CRLI);
			if (liScreenRecordCount != 0)
			{
				loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
			}
			if (loBuffer.indexOf(":[]") != -1)
			{
				loBuffer = new StringBuffer();
				loBuffer.append("{\"total\":\"\",\"page\":\"0\",\"records\":\"\",\"rows\":\"\",\"error\":\""
						+ asErrorMsg + "\"}");
			}
			else
			{
				loBufferTotal = new StringBuffer();
				int liTotal = liScreenRecordCount / Integer.parseInt(asRowsPerPage);
				if (liScreenRecordCount == Integer.parseInt(asRowsPerPage))
				{
					liTotal = 0;
				}
				String lsTotalPage = String.valueOf(liTotal + 1);
				String lsScreenRecordCount = String.valueOf(liScreenRecordCount);
				loBufferTotal.append(HHSConstants.HHSUTIL_TOTAL);
				loBufferTotal.append(lsTotalPage);
				loBufferTotal.append(HHSConstants.HHSUTIL_PAGE);
				loBufferTotal.append(asPage);
				loBufferTotal.append(HHSConstants.HHSUTIL_RECORDS);
				loBufferTotal.append(lsScreenRecordCount);
				loBufferTotal.append("\",\"error\":\"");
				loBufferTotal.append(asErrorMsg);
				loBufferTotal.append(HHSConstants.DQUOTES_COMMA);
				loBuffer = loBufferTotal.append(loBuffer);
			}
		}
		// handling Application Exception if conversion of subgrid data rows in
		// json form can't be done
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while converting the subgrid data rows in json form",
					aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while converting the subgrid data rows in json form", aoEx);
			LOG_OBJECT.Error("Exception Occured while converting the subgrid data rows in json form", loAppEx);
			throw loAppEx;
		}
		return loBuffer;
	}

	/**
	 * This method returns Header names of Jq grid
	 * <ul>
	 * <li>Get the complete String from gridheader property file and tokenize it
	 * to get the header names.</li>
	 * </ul>
	 * 
	 * @param asfields String label define in gridheader property file
	 * @return a String of Header Names.
	 * @throws ApplicationException Application Exception
	 */
	public static String getHeader(String asfields) throws ApplicationException
	{
		StringBuffer loBuffer = new StringBuffer();
		try
		{
			String lsFieldsDetail = PropertyLoader
					.getProperty(HHSConstants.COM_NYC_HHS_PROPERTIES_GRIDHEADER, asfields);
			StringTokenizer lsToken = new StringTokenizer(lsFieldsDetail, HHSConstants.HHSUTIL_DELIM_PIPE);
			while (lsToken.hasMoreTokens())
			{
				String lsTokenVal = lsToken.nextToken();
				StringTokenizer lsToken2 = new StringTokenizer(lsTokenVal, HHSConstants.HHSUTIL_DELIM_ESCLAMATION);
				if (lsToken2.hasMoreTokens())
				{
					String lsTokenVal2 = lsToken2.nextToken().trim();
					loBuffer.append(HHSConstants.STR);
					loBuffer.append(lsTokenVal2.trim());
					loBuffer.append(HHSConstants.OPOSTOPHI_COMMA);
				}

			}
			loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
		}
		// handling Application Exception if header name cannot be returned
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("ApplicationException Occured while returning Header names of Jq grid", aoAppEx);
			throw aoAppEx;

		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while returning Header names of Jq grid", aoEx);
			LOG_OBJECT.Error("Exception Occured while returning Header names of Jq grid", loAppEx);
			throw loAppEx;
		}
		return loBuffer.toString();
	}

	/**
	 * This method returns Header properties of Jq grid
	 * <ul>
	 * <li>Get the complete String from gridheader property file and tokenize it
	 * to get the header properties.</li>
	 * </ul>
	 * 
	 * @param asfields String label define in gridheader property file
	 * @return a String of Header properties.
	 * @throws ApplicationException Application Exception
	 */
	public static String getHeaderProp(String asfields) throws ApplicationException
	{
		StringBuffer loBuffer = new StringBuffer();
		try
		{
			String lsFieldsDetail = PropertyLoader
					.getProperty(HHSConstants.COM_NYC_HHS_PROPERTIES_GRIDHEADER, asfields);
			StringTokenizer lsToken = new StringTokenizer(lsFieldsDetail, HHSConstants.HHSUTIL_DELIM_PIPE);
			while (lsToken.hasMoreTokens())
			{
				String lsTokenVal = lsToken.nextToken();
				StringTokenizer lsToken2 = new StringTokenizer(lsTokenVal, HHSConstants.HHSUTIL_DELIM_ESCLAMATION);
				while (lsToken2.hasMoreTokens())
				{
					String lsTokenVal2 = lsToken2.nextToken().trim();
					if (lsTokenVal2.startsWith(HHSConstants.NAME))
					{
						String[] loArray = lsTokenVal2.split(HHSConstants.COLON);
						loBuffer.append("{name:'");
						loBuffer.append(loArray[1].trim());
						loBuffer.append("'},");
					}
					else if (lsTokenVal2.startsWith(HHSConstants.WIDTH))
					{
						String[] loArray = lsTokenVal2.split(HHSConstants.COLON);
						loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.CRLI_BRACKT_END));
						loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
						loBuffer.append(",width:'");
						loBuffer.append(loArray[1].trim());
						loBuffer.append("'},");
					}
					else if (lsTokenVal2.startsWith(HHSConstants.HHSUTIL_TEMPLATE))
					{
						String[] loArray = lsTokenVal2.split(HHSConstants.COLON);
						if (loArray.length > 1 && !HHSConstants.EMPTY_STRING.equalsIgnoreCase(loArray[1].trim()))
						{
							loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.CRLI_BRACKT_END));
							loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
							loBuffer.append(HHSConstants.HHSUTIL_APPENDED_TEMPLATE);
							loBuffer.append(loArray[1].trim());
							loBuffer.append(HHSConstants.CRLI_BRACKT_END_COMMA);
						}
						break;
					}
				}
			}
			loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
		}
		// handling Application Exception if error in getting Header properties
		// of Jq grid
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while returning Header properties of Jq grid", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while returning Header properties of Jq grid", aoEx);
			LOG_OBJECT.Error("Exception Occured while returning Header properties of Jq grid", loAppEx);
			throw loAppEx;
		}
		return loBuffer.toString();
	}

	/**
	 * This method returns the subgrid properties of Jq grid
	 * <ul>
	 * <li>Get the complete String from gridheader property file and tokenize it
	 * to get the subgrid properties.</li>
	 * </ul>
	 * 
	 * @param asfields String label define in gridheader property file
	 * @return a String of subgrid properties.
	 * @throws ApplicationException Application Exception
	 */
	public static String getSubGridProp(String asfields) throws ApplicationException
	{
		StringBuffer loBuffer = new StringBuffer();
		try
		{
			String lsFieldsDetail = PropertyLoader
					.getProperty(HHSConstants.COM_NYC_HHS_PROPERTIES_GRIDHEADER, asfields);
			StringTokenizer lsToken = new StringTokenizer(lsFieldsDetail, HHSConstants.HHSUTIL_DELIM_PIPE);
			while (lsToken.hasMoreTokens())
			{
				String lsTokenVal = lsToken.nextToken();
				StringTokenizer lsToken2 = new StringTokenizer(lsTokenVal, HHSConstants.HHSUTIL_DELIM_ESCLAMATION);
				while (lsToken2.hasMoreTokens())
				{
					String lsTokenVal2 = lsToken2.nextToken().trim();
					if (lsTokenVal2.startsWith(HHSConstants.HHSUTIL_EDITABLE))
					{
						loBuffer.append(HHSConstants.CRLI_BRACKT_START);
						loBuffer.append(lsTokenVal2);
						loBuffer.append(HHSConstants.COMMA);
					}
					else if (lsTokenVal2.startsWith(HHSConstants.HHSUTIL_EDITRULES))
					{
						loBuffer.append(lsTokenVal2);
						loBuffer.append(HHSConstants.CRLI_BRACKT_END_COMMA);
						break;
					}
				}
			}
			loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
		}
		// handling Application Exception if subgrid properties can not be
		// returned
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while returning the subgrid properties of Jq grid" + aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while returning the subgrid properties of Jq grid", aoEx);
			LOG_OBJECT.Error("Exception Occured while returning the subgrid properties of Jq grid", loAppEx);
			throw loAppEx;
		}
		return loBuffer.toString();
	}

	/**
	 * This method returns the subgrid header row of Jq grid
	 * <ul>
	 * <li>Get the complete String from gridheader property file and tokenize it
	 * to get the subgrid header row.</li>
	 * </ul>
	 * 
	 * @param asfields String label define in gridheader property file
	 * @return a String of subgrid header row.
	 * @throws ApplicationException Application Exception
	 */
	public static StringBuffer getSubGridHeaderRow(String asfields) throws ApplicationException
	{
		StringBuffer loBuffer = new StringBuffer();
		try
		{
			String lsFieldsDetail = PropertyLoader
					.getProperty(HHSConstants.COM_NYC_HHS_PROPERTIES_GRIDHEADER, asfields);
			StringTokenizer lsToken = new StringTokenizer(lsFieldsDetail, HHSConstants.HHSUTIL_DELIM_PIPE);
			String lsRowId = lsToken.toString().substring(lsToken.toString().lastIndexOf(HHSConstants.DOT) + 1);
			if (lsRowId.indexOf('@') != -1)
			{
				lsRowId = lsRowId.replaceAll(HHSConstants.AT_THE_RATE, HHSConstants.EMPTY_STRING);
			}
			loBuffer.append("{\"rows\":[{\"id\":\"");
			loBuffer.append(lsRowId);
			loBuffer.append(HHSConstants.DQUOTES_COMMA);
			while (lsToken.hasMoreTokens())
			{
				String lsTokenVal = lsToken.nextToken();
				StringTokenizer lsToken2 = new StringTokenizer(lsTokenVal, HHSConstants.HHSUTIL_DELIM_ESCLAMATION);
				if (lsToken2.hasMoreTokens())
				{
					lsToken2.nextToken();
					String lsSubHeaderVal = lsToken2.nextToken();
					String lsName = lsToken2.nextToken().trim();
					String[] lsNameArray = lsName.split(HHSConstants.COLON);

					loBuffer.append(HHSConstants.DOUBLE_QUOTES);
					if (lsNameArray.length > 1)
					{
						loBuffer.append(lsNameArray[1].trim());
					}
					loBuffer.append(HHSConstants.UNDERSCORESCORE);
					loBuffer.append(lsSubHeaderVal);
					loBuffer.append(HHSConstants.DQUOTES_COMMA);
				}
			}
			loBuffer.deleteCharAt(loBuffer.lastIndexOf(HHSConstants.COMMA));
			loBuffer.append(HHSConstants.GET_MEMBER_DETAILS_STRING_LTERAL4);
		}
		// handling Application Exception if returning the subgrid header row
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while returning the subgrid header row of Jq grid" + aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while returning the subgrid header row of Jq grid", aoEx);
			LOG_OBJECT.Error("Exception Occured while returning the subgrid header row of Jq grid", loAppEx);
			throw loAppEx;
		}
		return loBuffer;
	}

	/**
	 * This method will be used for fetch all the status from database. This
	 * will be used by caching listener.
	 * 
	 * calls the transaction 'getMasterStatusDB'
	 * 
	 * @return Final Status Map
	 * @throws ApplicationException Application Exception Thrown
	 */
	public static HashMap<String, Object> setMasterStatus() throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap<String, Object> loFinalStatusMap = new HashMap<String, Object>();
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.GET_MASTER_STATUS_DB);
			@SuppressWarnings("unchecked")
			List<MasterStatusBean> loMasterStatusBeanList = (List<MasterStatusBean>) loChannel
					.getData(HHSConstants.MASTER_STATUS_LIST);
			Iterator<MasterStatusBean> loIterator = loMasterStatusBeanList.iterator();
			while (loIterator.hasNext())
			{
				MasterStatusBean loMasterStatusBean = loIterator.next();
				if (loFinalStatusMap.containsKey(loMasterStatusBean.getProcessType()))
				{
					EntityStatusBean loStatusBean = (EntityStatusBean) loFinalStatusMap.get(loMasterStatusBean
							.getProcessType());
					loStatusBean.getStatusBean().add(loMasterStatusBean);
				}
				else
				{
					EntityStatusBean loEntityStatusBean = new EntityStatusBean();
					List<MasterStatusBean> loList = new ArrayList<MasterStatusBean>();
					loEntityStatusBean.setEntityName(loMasterStatusBean.getProcessType());
					loList.add(loMasterStatusBean);
					loEntityStatusBean.setStatusBean(loList);
					loFinalStatusMap.put(loMasterStatusBean.getProcessType(), loEntityStatusBean);

				}
			}
		}
		// handling Application Exception if all the status from database can
		// not be fetched
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while fetching all the status from database", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while fetching all the status from database", aoEx);
			LOG_OBJECT.Error("Exception Occured while fetching all the status from database", loAppEx);
			throw loAppEx;
		}
		return loFinalStatusMap;
	}

	/**
	 * This method will be used for getting a particular status name
	 * corresponding to the status id and process Type.
	 * 
	 * @param asProcessType :A String variable corresponding to the process_type
	 *            in status table
	 * @param aoStatusId : A int variable corresponding to the status_id in
	 *            status table
	 * @return Status return status value if in cache else null
	 * @throws ApplicationException Application Exception Thrown
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static String getStatusName(String asProcessType, Integer aoStatusId) throws ApplicationException
	{
		try
		{
			HashMap<String, Object> loMasterStatusMap = (HashMap<String, Object>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(HHSConstants.STATUS_LIST);
			if (null != loMasterStatusMap)
			{
				EntityStatusBean loEntityTypeBean = (EntityStatusBean) loMasterStatusMap.get(asProcessType);
				Iterator loIter = loEntityTypeBean.getStatusBean().iterator();
				while (loIter.hasNext())
				{
					MasterStatusBean loMasterStatusBean = (MasterStatusBean) loIter.next();
					if (loMasterStatusBean.getStatusId() == aoStatusId)
					{
						return loMasterStatusBean.getStatus();
					}
				}
			}
		}
		// handling Application Exception if a particular status name can not be
		// get
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while getting status name corresponding to status id and process Type"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while getting status name corresponding to status id and process Type", aoEx);
			LOG_OBJECT.Error("Exception Occured while getting status name corresponding to status id and process Type",
					loAppEx);
			throw loAppEx;
		}
		return null;
	}

	/**
	 * This method will be used for getting a particular status corresponding to
	 * status id and process Type.
	 * 
	 * @param asProcessType :A String variable corresponding to the process_type
	 *            in status table
	 * @param asStatus : A String variable corresponding to the status in status
	 *            table
	 * @return Zero
	 * @throws ApplicationException Application Exception Thrown
	 */

	public static int getStatusID(String asProcessType, String asStatus) throws ApplicationException
	{
		try
		{
			@SuppressWarnings("unchecked")
			HashMap<String, Object> loMasterStatusMap = (HashMap<String, Object>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(HHSConstants.STATUS_LIST);
			if (null != loMasterStatusMap)
			{
				EntityStatusBean loEntityTypeBean = (EntityStatusBean) loMasterStatusMap.get(asProcessType);
				@SuppressWarnings("rawtypes")
				Iterator loIter = loEntityTypeBean.getStatusBean().iterator();
				while (loIter.hasNext())
				{
					MasterStatusBean loMasterStatusBean = (MasterStatusBean) loIter.next();
					if (loMasterStatusBean.getStatus().equalsIgnoreCase(asStatus))
					{
						return loMasterStatusBean.getStatusId();
					}
				}
			}
		}
		// handling Application Exception if status id can not be get
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while fetching status corresponding to status id and process Type"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while fetching status corresponding to status id and process Type", aoEx);
			LOG_OBJECT.Error("Exception Occured while fetching status corresponding to status id and process Type",
					loAppEx);
			throw loAppEx;
		}
		return 0;
	}

	/**
	 * This method will be used for fetching all the status corresponding to one
	 * process Type.
	 * 
	 * @param asProcessType :A String variable corresponding to the process_type
	 *            in status table * @return
	 * @throws ApplicationException Application Exception Thrown
	 * @return Status List
	 * @throws ApplicationException Application Exception
	 */

	public static List<String> getStatusMap(String asProcessType) throws ApplicationException
	{
		List<String> lsStatusList = new ArrayList<String>();
		try
		{
			@SuppressWarnings("unchecked")
			HashMap<String, Object> loMasterStatusMap = (HashMap<String, Object>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(HHSConstants.STATUS_LIST);
			if (null != loMasterStatusMap)
			{
				EntityStatusBean loEntityTypeBean = (EntityStatusBean) loMasterStatusMap.get(asProcessType);
				@SuppressWarnings("rawtypes")
				Iterator loIter = loEntityTypeBean.getStatusBean().iterator();
				while (loIter.hasNext())
				{
					MasterStatusBean loMasterStatusBean = (MasterStatusBean) loIter.next();
					lsStatusList.add(loMasterStatusBean.getStatus());
				}
			}
		}
		// handling Application Exception if status can not be fetched
		// corresponding to one process Type.
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while fetching all the status corresponding to one process Type"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while fetching all the status corresponding to one process Type", aoEx);
			LOG_OBJECT.Error("Exception Occured while fetching all the status corresponding to one process Type",
					loAppEx);
			throw loAppEx;
		}
		return lsStatusList;
	}

	/**
	 * This method converts the treeset of agency names into agency map and
	 * returns the same.
	 * 
	 * @return - map of agency
	 * @throws ApplicationException - throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public static Map<String, String> getAgencyMap() throws ApplicationException
	{
		Map<String, String> loAgencyMap = null;
		try
		{
			Document loXMLDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSConstants.NYC_AGENCY_MASTER);
			if (null == loXMLDoc || loXMLDoc.toString().equals(HHSConstants.EMPTY_STRING))
			{
				throw new ApplicationException(
						"Nyc Agency List XML hasnt been loaded into memory for transaction key: "
								+ HHSConstants.NYC_AGENCY_MASTER);
			}
			String lsXPath = HHSConstants.HHSUTIL_AGENCY_MASTER;
			Element loEle = XMLUtil.getElement(lsXPath, loXMLDoc);
			if (null != loEle)
			{
				loAgencyMap = new TreeMap<String, String>();
				List<Element> loChildrenElementList = loEle.getChildren(HHSConstants.HHSUTIL_AGENCY);
				for (Element loElement : loChildrenElementList)
				{
					loAgencyMap.put(loElement.getAttributeValue(HHSConstants.ID),
							loElement.getAttributeValue(HHSConstants.VALUE));
				}
			}
		}
		// handling Application Exception if treeset of agency names can not be
		// converted into agency map
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("ApplicationException Occured while converting the treeset of agency names into agency map"
							+ aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception Occured while converting the treeset of agency names into agency map", aoEx);
			LOG_OBJECT.Error("Exception Occured while converting the treeset of agency names into agency map", loAppEx);
			throw loAppEx;
		}

		return loAgencyMap;
	}

	/**
	 * This method is for getting the current time stamp
	 * 
	 * @return - timestamp
	 * @throws ApplicationException Application Exception
	 */
	public static Timestamp getCurrentTimestampDate() throws ApplicationException
	{
		java.util.Date loDate = null;
		try
		{
			loDate = new java.util.Date();
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While getting the current time stamp", aoEx);
			LOG_OBJECT.Error("Error Occured While getting the current time stamp", loAppEx);
			throw loAppEx;
		}
		return new Timestamp(loDate.getTime());
	}

	/**
	 * This method count number of occurance of sub string in main string
	 * 
	 * @param asMain - Main Strin
	 * @param asSub - Sub String to be found
	 * @return total number of occurance of sub string in main string
	 * @throws ApplicationException Application Exception
	 */
	public static int getSubStringCount(String asMain, String asSub) throws ApplicationException
	{
		int liLastIndex = 0;
		int liCount = 0;
		try
		{
			while (liLastIndex != -1)
			{
				liLastIndex = asMain.indexOf(asSub, liLastIndex);
				if (liLastIndex != -1)
				{
					liCount++;
					liLastIndex += asSub.length();
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While counting number of occurance of sub string in main string", aoEx);
			LOG_OBJECT.Error("Error Occured While counting number of occurance of sub string in main string", loAppEx);
			throw loAppEx;
		}
		return liCount;
	}

	/**
	 * This method set all property required to launch Financial workflows in
	 * HashMap object
	 * 
	 * @param aoFinancialWFBean : Financial Bean containing the financial info
	 * @param asWorkFlowName : It contains the workflow name for which task in
	 *            launched
	 * @return loHmRequiredProps : returns the Map containing the financial info
	 *         required for further transaction
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static HashMap setFinancialWFProperty(FinancialWFBean aoFinancialWFBean, String asWorkFlowName)
			throws ApplicationException
	{
		HashMap loHmRequiredProps = new HashMap();
		try
		{
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, aoFinancialWFBean.getProcurementTitle());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_EPIN, aoFinancialWFBean.getProcEpin());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROVIDER_ID, aoFinancialWFBean.getProviderId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROVIDER_NAME, aoFinancialWFBean.getProviderName());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROGRAM_NAME, aoFinancialWFBean.getProgramName());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_BUDGET_ADVANCE_ID, aoFinancialWFBean.getAdvanceNumber());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, aoFinancialWFBean.getAwardEpin());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CT, aoFinancialWFBean.getContractNum());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, aoFinancialWFBean.getUserId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AGENCY_ID, aoFinancialWFBean.getAgencyId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, aoFinancialWFBean.getProcurementId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, aoFinancialWFBean.getContractId());
			//Added for R6: return payment review task
			loHmRequiredProps.put(HHSConstants.RETURN_PE_PAYMENT_DETAIL_ID, aoFinancialWFBean.getReturnPaymentDetailsId());
			//Added for R6: return payment review task end
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, asWorkFlowName);
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_TITLE, aoFinancialWFBean.getContractTitle());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ADVANCE_NUMBER, aoFinancialWFBean.getAdvanceNumber());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_BUDGET_ID, aoFinancialWFBean.getBudgetId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_INVOICE_ID, aoFinancialWFBean.getInvoiceId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_NEW_FISCAL_YEAR_ID, aoFinancialWFBean.getFiscalYearId());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_COF, aoFinancialWFBean.getLaunchCOF());
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_CONFIG_ID,
					aoFinancialWFBean.getContractConfigurationId());
			loHmRequiredProps.put(HHSConstants.ENTITY_ID, getFinancialEntityId(aoFinancialWFBean));
			loHmRequiredProps.put(HHSConstants.CURR_LEVEL, Integer.parseInt(HHSConstants.ONE));
			loHmRequiredProps.put(HHSConstants.CONTRACT_ID_WORKFLOW, aoFinancialWFBean.getContractId());
			loHmRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, aoFinancialWFBean.getBudgetId());
			loHmRequiredProps.put(HHSConstants.BUDGET_TYPE_ID, aoFinancialWFBean.getBudgetTypeId());
			loHmRequiredProps.put(HHSConstants.CLC_FISCAL_YEAR_ID, aoFinancialWFBean.getFiscalYearId());
			loHmRequiredProps.put(HHSConstants.PROC_USER, aoFinancialWFBean.getProcUser());
			loHmRequiredProps.put(HHSConstants.SUBMITTED_BY, aoFinancialWFBean.getUserId());
			//Added fo R6: Launch by org type added
			loHmRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE, aoFinancialWFBean.getLaunchByOrgType());
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While setting all property required to launch Financial workflows", aoEx);
			LOG_OBJECT
					.Error("Error Occured While setting all property required to launch Financial workflows", loAppEx);
			throw loAppEx;
		}
		return loHmRequiredProps;
	}

	/**
	 * This method get entity Id for audit on the basis of parameter like
	 * invoiceId,budgetId etc
	 * 
	 * @param aoFinancialWFBean Bean object
	 * @return Entity Id
	 * @throws ApplicationException Application Exception
	 */
	public static String getFinancialEntityId(FinancialWFBean aoFinancialWFBean) throws ApplicationException
	{
		String lsEntityId = HHSConstants.EMPTY_STRING;
		try
		{
			if (null != aoFinancialWFBean.getAdvanceNumber() && !(aoFinancialWFBean.getAdvanceNumber()).isEmpty())
			{
				lsEntityId = aoFinancialWFBean.getAdvanceNumber();
			}
			else if (null != aoFinancialWFBean.getInvoiceId() && !(aoFinancialWFBean.getInvoiceId()).isEmpty())
			{
				lsEntityId = aoFinancialWFBean.getInvoiceId();
			}
			//Added for R6: return payment review task 
			else if (null != aoFinancialWFBean.getBudgetId() && !(aoFinancialWFBean.getBudgetId()).isEmpty())
			{
				if (null != aoFinancialWFBean.getReturnPaymentDetailsId() && !(aoFinancialWFBean.getReturnPaymentDetailsId()).isEmpty())
				{
					lsEntityId = aoFinancialWFBean.getReturnPaymentDetailsId();
				}
				else
				{
					lsEntityId = aoFinancialWFBean.getBudgetId();
				}
			}
			//Added for R6: return payment review task end
			else if (null != aoFinancialWFBean.getContractId() && !(aoFinancialWFBean.getContractId()).isEmpty())
			{
				lsEntityId = aoFinancialWFBean.getContractId();
			}
			else if (null != aoFinancialWFBean.getProcurementId() && !(aoFinancialWFBean.getProcurementId()).isEmpty())
			{
				lsEntityId = aoFinancialWFBean.getProcurementId();
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While getting entity Id for audit on the basis of parameter", aoEx);
			LOG_OBJECT.Error("Error Occured While getting entity Id for audit on the basis of parameter", loAppEx);
			throw loAppEx;
		}
		return lsEntityId;
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy" format .
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 * @throws ApplicationException Application Exception
	 */
	public static Date addFromClause(Date aoDate) throws ApplicationException
	{
		final GregorianCalendar loCalendar = new GregorianCalendar();
		try
		{
			loCalendar.setTime(aoDate);
			loCalendar.set(Calendar.HOUR_OF_DAY, 0);
			loCalendar.set(Calendar.MINUTE, 0);
			loCalendar.set(Calendar.SECOND, 0);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While changing the inputed Date in MM/dd/yyyy format", aoEx);
			LOG_OBJECT.Error("Error Occured While changing the inputed Date in MM/dd/yyyy format", loAppEx);
			throw loAppEx;
		}
		return loCalendar.getTime();
	}

	/**
	 * This Method changes the inputed Date in "MM/dd/yyyy" format .
	 * 
	 * @param aoDate Date to be changed
	 * @return Date
	 * @throws ApplicationException Application Exception
	 */
	public static Date addToClause(Date aoDate) throws ApplicationException
	{
		final GregorianCalendar loCalendar = new GregorianCalendar();
		try
		{
			loCalendar.setTime(aoDate);
			loCalendar.set(Calendar.HOUR_OF_DAY, 23);
			loCalendar.set(Calendar.MINUTE, 59);
			loCalendar.set(Calendar.SECOND, 59);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While changing the inputed Date in MM/dd/yyyy format", aoEx);
			LOG_OBJECT.Error("Error Occured While changing the inputed Date in MM/dd/yyyy format", loAppEx);
			throw loAppEx;
		}
		return loCalendar.getTime();
	}

	/**
	 * Formats the Amount String removing commas and the blank spaces
	 * 
	 * @param asAmount - Element object for which string has to be generated
	 * @return formated String (String object)
	 * @throws ApplicationException Application Exception
	 */
	public static String formatAmount(String asAmount) throws ApplicationException
	{
		try
		{
			if (null == asAmount)
			{
				return HHSConstants.STRING_ZERO;
			}

			return asAmount.replace(HHSConstants.COMMA, HHSConstants.EMPTY_STRING).replace(HHSConstants.SPACE,
					HHSConstants.EMPTY_STRING);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Formatting the Amount String removing commas and the blank spaces", aoEx);
			LOG_OBJECT.Error("Error Occured While Formatting the Amount String removing commas and the blank spaces",
					loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * Converts String received from addressValidation to bean object
	 * 
	 * @param aoAddressBean - bean in which address related values needs to be
	 *            populated
	 * 
	 * @param asAddressRelatedData - address related data needs to be populated
	 * @throws ApplicationException Application Exception
	 * 
	 */
	public static void convertAddressValidationFields(String asAddressRelatedData, AddressValidationBean aoAddressBean)
			throws ApplicationException
	{
		try
		{
			String lsAddressRelatedDataArray[] = asAddressRelatedData.split(ApplicationConstants.KEY_SEPARATOR);
			aoAddressBean.setValStatusDescription(lsAddressRelatedDataArray[0]);
			aoAddressBean.setValStatusReasonText(lsAddressRelatedDataArray[1]);
			aoAddressBean.setValNormHouseNumber(lsAddressRelatedDataArray[2]);
			aoAddressBean.setValNormStreetName(lsAddressRelatedDataArray[3]);
			aoAddressBean.setValCity(lsAddressRelatedDataArray[4]);
			aoAddressBean.setValState(lsAddressRelatedDataArray[5]);
			aoAddressBean.setValZipCode(lsAddressRelatedDataArray[6]);
			aoAddressBean.setValCongressDistrict(lsAddressRelatedDataArray[7]);
			aoAddressBean.setValLatitude(lsAddressRelatedDataArray[8]);
			aoAddressBean.setValLongitude(lsAddressRelatedDataArray[9]);
			aoAddressBean.setValXCoordinate(lsAddressRelatedDataArray[10]);
			aoAddressBean.setValYCoordinate(lsAddressRelatedDataArray[11]);
			aoAddressBean.setValCommunityDistrict(lsAddressRelatedDataArray[12]);
			aoAddressBean.setValCivialCourtDistrict(lsAddressRelatedDataArray[13]);
			aoAddressBean.setValSchoolDistrName(lsAddressRelatedDataArray[14]);
			aoAddressBean.setValHealthArea(lsAddressRelatedDataArray[15]);
			aoAddressBean.setValBuildingIdNumber(lsAddressRelatedDataArray[16]);
			aoAddressBean.setValTaxBlock(lsAddressRelatedDataArray[17]);
			aoAddressBean.setValTaxLot(lsAddressRelatedDataArray[18]);
			aoAddressBean.setValSenatorDistrict(lsAddressRelatedDataArray[19]);
			aoAddressBean.setValAssemblyDistrict(lsAddressRelatedDataArray[20]);
			aoAddressBean.setValCouncilDistrict(lsAddressRelatedDataArray[21]);
			aoAddressBean.setValLowEndCrossStreetNo(lsAddressRelatedDataArray[22]);
			aoAddressBean.setValHighEndCrossStreetNo(lsAddressRelatedDataArray[23]);
			aoAddressBean.setValLowEndCrossStreetName(lsAddressRelatedDataArray[24]);
			aoAddressBean.setValHighEndCrossStreetName(lsAddressRelatedDataArray[25]);
			aoAddressBean.setValBorough(lsAddressRelatedDataArray[26]);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Converting String received from addressValidation to bean object", aoEx);
			LOG_OBJECT.Error("Error Occured While Converting String received from addressValidation to bean object",
					loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * This method calculates the Current Fiscal Year and the End Fiscal Year of
	 * the Contract.
	 * 
	 * @param aoContractEndDate contract end date
	 * @param aoCurrentDate current date
	 * @return loFYDetails FY details
	 * @throws ApplicationException Application Exception
	 */
	public static HashMap<String, Integer> getFYDetails(Date aoContractStartDate, Date aoContractEndDate,
			Date aoCurrentDate) throws ApplicationException
	{
		HashMap<String, Integer> loFYDetails = new HashMap<String, Integer>();
		Calendar loCal = Calendar.getInstance();
		try
		{
			loCal.setTime(aoCurrentDate);
			int liCurrentMonth = loCal.get(Calendar.MONTH) + 1;
			int liCurrentFiscalYear = loCal.get(Calendar.YEAR);
			loCal.setTime(aoContractEndDate);
			int liContractEndYear = loCal.get(Calendar.YEAR);
			int liContractEndMonth = loCal.get(Calendar.MONTH) + 1;
			loCal.setTime(aoContractStartDate);
			int liContractStartYear = loCal.get(Calendar.YEAR);
			int liContractStartMonth = loCal.get(Calendar.MONTH) + 1;
			if (liContractEndMonth >= 7)
			{
				liContractEndYear = liContractEndYear + 1;
			}
			if (liCurrentMonth >= 7)
			{
				liCurrentFiscalYear = liCurrentFiscalYear + 1;
			}
			if (liContractStartMonth >= 7)
			{
				liContractStartYear = liContractStartYear + 1;
			}

			loFYDetails.put(HHSConstants.CONTRACT_START_FY, liContractStartYear);
			loFYDetails.put(HHSConstants.CONTRACT_END_FY, liContractEndYear);
			loFYDetails.put(HHSConstants.CURRENT_FISCAL_YEAR, liCurrentFiscalYear);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Current Fiscal Year and the End Fiscal Year of the Contract",
					aoEx);
			LOG_OBJECT.Error(
					"Error Occured While calculating the Current Fiscal Year and the End Fiscal Year of the Contract",
					loAppEx);
			throw loAppEx;
		}
		return loFYDetails;
	}

	/**
	 * This method calculates the Current Fiscal Year and the End Fiscal Year of
	 * the Contract.
	 * 
	 * @param aoContractEndDate contract end date
	 * @param aoCurrentDate current date
	 * @return loFYDetails FY details
	 * @throws ApplicationException Application Exception
	 */
	public static HashMap<String, Integer> getFirstAndLastFYOfContract(Date aoContractStartDate, Date aoContractEndDate)
			throws ApplicationException
	{
		HashMap<String, Integer> loFYDetails = new HashMap<String, Integer>();
		Calendar loCal = Calendar.getInstance();
		try
		{
			loCal.setTime(aoContractStartDate);
			int liStartMonth = loCal.get(Calendar.MONTH) + 1;
			int liStartYear = loCal.get(Calendar.YEAR);

			loCal.setTime(aoContractEndDate);
			int liContractEndYear = loCal.get(Calendar.YEAR);
			int liContractEndMonth = loCal.get(Calendar.MONTH) + 1;

			if (liContractEndMonth >= 7)
			{
				liContractEndYear = liContractEndYear + 1;
			}
			int liStartFiscalYear;
			if (liStartMonth < 7)
			{
				liStartFiscalYear = liStartYear;
			}
			else
			{
				liStartFiscalYear = liStartYear + 1;
			}
			loFYDetails.put(HHSConstants.CONTRACT_END_FY, liContractEndYear);
			loFYDetails.put(HHSConstants.START_FISCAL_YEAR, liStartFiscalYear);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Start Fiscal Year and the End Fiscal Year of the Contract",
					aoEx);
			LOG_OBJECT.Error(
					"Error Occured While calculating the Start Fiscal Year and the End Fiscal Year of the Contract",
					loAppEx);
			throw loAppEx;
		}
		return loFYDetails;
	}

	/**
	 * This method calculates the Current Configurable Budget Start date in the String format of 'DD/MM/YYYY'
	 * <ul>
	 * <li>Returns Current FY(Fiscal Year) if Contract Start date is less than
	 * current FY else the Contract's first FY</li>
	 * </ul>
	 * 
	 * @param asContractStartDate contract start date
	 * @return asContractStartDate contract start date
	 * @throws ApplicationException Application Exception
	 */
	public static String getNewBudgetStartDate(String asContractStartDate, String asCurrentFiscalYear)
			throws ApplicationException
	{
		String[] loStartDateArray = asContractStartDate.split(HHSConstants.FORWARD_SLASH);

		try
		{
			Integer liContractStartYear = Integer.parseInt(loStartDateArray[2]);
			Integer liContractStartMonth = Integer.parseInt(loStartDateArray[1]);
			if (liContractStartMonth >= 7)
			{
				liContractStartYear = liContractStartYear + 1;
			}
			if (asCurrentFiscalYear.equals(liContractStartYear.toString()))
			{
				return asContractStartDate;
			}
			else
			{
				Integer liStartYear = Integer.parseInt(asCurrentFiscalYear) - 1;
				return HHSConstants.FISCAL_YEAR_DATE + liStartYear.toString();
			}

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Current Configurable Budget Start date", aoEx);
			LOG_OBJECT.Error("Error Occured While calculating the Current Configurable Budget Start date", loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This method calculates the Current Configurable Budget End date
	 * <ul>
	 * <li>Returns Current FY(Fiscal Year) if Contract Start date is less than
	 * current FY else the Contract's first FY</li>
	 * </ul>
	 * 
	 * @param asContractEndDate contract end date
	 * @return asContractEndDate contract end date
	 * @throws ApplicationException Application Exception
	 */
	public static String getNewBudgetEndDate(String asContractEndDate, String asCurrentFiscalYear)
			throws ApplicationException
	{
		String[] loEndDateArray = asContractEndDate.split(HHSConstants.FORWARD_SLASH);
        
		try
		{
			Integer liContractEndYear = Integer.parseInt(loEndDateArray[2]);
			Integer liContractEndMonth = Integer.parseInt(loEndDateArray[1]);
			if (liContractEndMonth >= 7)
			{
				liContractEndYear = liContractEndYear + 1;
			}
			if (asCurrentFiscalYear.equals(liContractEndYear.toString()))
			{   
				return asContractEndDate;
			}
			else
			{   
				return HHSConstants.FISCAL_YEAR_END_DATE + asCurrentFiscalYear;
			}

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Current Configurable Budget End date", aoEx);
			LOG_OBJECT.Error("Error Occured While calculating the Current Configurable Budget End date", loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This method calculates the Configurable Fiscal Year for a contract
	 * <ul>
	 * <li>Returns Current FY(Fiscal Year) if Contract Start date is less than
	 * current FY else the Contract's first FY</li>
	 * </ul>
	 * 
	 * @param aiContractStartFY Contract start Fiscal Year
	 * @return aiContractStartFY
	 * @throws ApplicationException Application Exception
	 */
	public static Integer getFYForContractBudgetConfig(Integer aiContractStartFY) throws ApplicationException
	{
		Calendar loCal = Calendar.getInstance();
		try
		{
			int liCurrentMonth = loCal.get(Calendar.MONTH) + 1;
			int liCurrentYear = loCal.get(Calendar.YEAR);
			int liCurrentFiscalYear = 0;
			if (liCurrentMonth < 7)
			{
				liCurrentFiscalYear = liCurrentYear;
			}
			else
			{
				liCurrentFiscalYear = liCurrentYear + 1;
			}
			if (liCurrentFiscalYear > aiContractStartFY)
			{
				return liCurrentFiscalYear;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Configurable Fiscal Year for a contract", aoEx);
			LOG_OBJECT.Error("Error Occured While calculating the Configurable Fiscal Year for a contract", loAppEx);
			throw loAppEx;
		}
		return aiContractStartFY;
	}

	/**
	 * @param aolist list
	 * @return false
	 * @throws ApplicationException Application Exception
	 */
	public static Boolean isEmptyList(@SuppressWarnings("rawtypes") List aolist) throws ApplicationException
	{
		try
		{
			if (aolist == null)
			{
				return true;
			}
			else if (aolist.size() == 0)
			{
				return true;
			}
			else if (aolist.isEmpty())
			{
				return true;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while returning list", aoEx);
			LOG_OBJECT.Error("Error Occured while returning list", loAppEx);
			throw loAppEx;
		}
		return false;
	}

	/**
	 * This method clears the temp folder - deletes file older then the time
	 * set(mins)
	 * 
	 * @param aoDeleteAll Delete All
	 * @throws ApplicationException Application Exception
	 */
	public static void cleanup(File aoDeleteAll) throws ApplicationException
	{
		File loToDelete = null;
		try
		{
			String loFileList[] = aoDeleteAll.list();
			java.util.Date loDate = new java.util.Date();
			for (int liCount = loFileList.length - 1; liCount >= 0; liCount--)
			{
				loToDelete = new File(aoDeleteAll.getAbsolutePath() + HHSConstants.FORWARD_SLASH + loFileList[liCount]);
				if (((loDate.getTime() - loToDelete.lastModified()) / 60 / 1000) > HHSConstants.TEMPORARY_FOLDER_CLEAN_TIME_MIN)
				{
					SaveFormOnLocalUtil.deleteFolder(loToDelete);
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While clearing the temp folder",
					aoEx);
			LOG_OBJECT.Error("Error Occured While clearing the temp folder", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * 
	 * This method Converts the AgencyName to acronym in () like DHS -
	 * Department of Homeless Services converts to Department of Homeless
	 * Services (DHS) /**
	 * 
	 * @param agencyName Agency Name
	 * @return Buffer
	 * @throws ApplicationException Application Exception
	 */
	public static String getAgencyName(String agencyName) throws ApplicationException
	{
		int liStartPos = 0;
		StringBuffer loBuffer = new StringBuffer();
		try
		{
			for (int loCount = 0; loCount < agencyName.length(); loCount++)
			{
				if (agencyName.charAt(loCount) == '-')
				{
					liStartPos = loCount;
					break;
				}
			}
			loBuffer.append(agencyName.substring(liStartPos, agencyName.length())
					.replace(HHSConstants.HYPHEN, HHSConstants.EMPTY_STRING).trim());
			loBuffer.append(HHSConstants.BEGINNING_BRACKET);
			loBuffer.append(agencyName.substring(0, liStartPos).trim());
			loBuffer.append(HHSConstants.CLOSING_BRACKET);
		}

		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While Converting the AgencyName to acronym", aoEx);
			LOG_OBJECT.Error("Error Occured While Converting the AgencyName to acronym", loAppEx);
			throw loAppEx;
		}
		return loBuffer.toString();
	}

	/**
	 * 
	 * This method returns the Budget's Initial Fiscal year
	 * 
	 * @param asContractStartDate contract start date
	 * @return integer
	 * @throws ApplicationException Application Exception
	 */
	public static Integer getBudgetFY(String asContractStartDate) throws ApplicationException
	{
		String[] loStartDateArray = asContractStartDate.split(HHSConstants.FORWARD_SLASH);
		Integer liContractFirstFY = 0;
		try
		{
			Integer liContractStartYear = Integer.parseInt(loStartDateArray[2]);
			liContractFirstFY = liContractStartYear;
			if (liContractStartYear >= 7)
			{
				liContractFirstFY = liContractStartYear + 1;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the Budget's Initial Fiscal year", aoEx);
			LOG_OBJECT.Error("Error Occured While returning the Budget's Initial Fiscal year", loAppEx);
			throw loAppEx;
		}
		return getFYForContractBudgetConfig(liContractFirstFY);
	}

	/**
	 * This method is used to convert double value up to the precison value
	 * given in second parameter
	 * 
	 * @param adValue double
	 * @param aiNoOfPrecision no of precision
	 * @return double value with having precision given
	 * @throws ApplicationException Application Exception
	 */

	public static double round(double adValue, int aiNoOfPrecision) throws ApplicationException
	{
		double ldPow = 0.0d;
		double ldTemp = 0.0d;
		try
		{
			ldPow = (double) Math.pow(10, aiNoOfPrecision);
			adValue = adValue * ldPow;
			ldTemp = Math.round(adValue);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While converting double value up to the precison value", aoEx);
			LOG_OBJECT.Error("Error Occured While converting double value up to the precison value", loAppEx);
			throw loAppEx;
		}
		return (double) ldTemp / ldPow;
	}

	/**
	 * @param aoRequest request parameter
	 * @return false
	 * @throws ApplicationException Application Exception
	 */
	public static Boolean isAgencyAccoUser(RenderRequest aoRequest) throws ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			String loUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			if (loUserRole.equals(HHSConstants.ACCO_STAFF_ROLE) || loUserRole.equals(HHSConstants.ACCO_MANAGER_ROLE)
					|| loUserRole.equals(HHSConstants.ACCO_ADMIN_STAFF_ROLE))
			{
				return Boolean.TRUE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured from request parameter", aoEx);
			LOG_OBJECT.Error("Error Occured from request parameter", loAppEx);
			throw loAppEx;
		}
		return Boolean.FALSE;
	}

	/**
	 * @param aoRequest request parameter
	 * @return false
	 * @throws ApplicationException Application Exception
	 */
	public static Boolean isAgencyNonAccoUser(RenderRequest aoRequest) throws ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			String loUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ROLE,
					PortletSession.APPLICATION_SCOPE);
			if (loUserRole.equals(HHSConstants.PROGRAM_STAFF_ROLE)
					|| loUserRole.equals(HHSConstants.PROGRAM_ADMIN_STAFF_ROLE)
					|| loUserRole.equals(HHSConstants.PROGRAM_MANAGER_ROLE)
					|| loUserRole.equals(HHSConstants.FINANCE_STAFF_ROLE)
					|| loUserRole.equals(HHSConstants.FINANCE_ADMIN_STAFF_ROLE)
					|| loUserRole.equals(HHSConstants.FINANCE_MANAGER_ROLE) || loUserRole.equals(HHSConstants.CFO_ROLE))
			{
				return Boolean.TRUE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured from request parameter", aoEx);
			LOG_OBJECT.Error("Error Occured from request parameter", loAppEx);
			throw loAppEx;
		}
		return Boolean.FALSE;
	}

	/**
	 * @param aoUserRole user role
	 * @return false
	 * @throws ApplicationException Application Exception
	 */
	public static Boolean isAgencyAccoUser(String aoUserRole) throws ApplicationException
	{
		try
		{
			if (aoUserRole.equals(HHSConstants.ACCO_STAFF_ROLE) || aoUserRole.equals(HHSConstants.ACCO_MANAGER_ROLE)

			|| aoUserRole.equals(HHSConstants.ACCO_ADMIN_STAFF_ROLE))
			{
				return Boolean.TRUE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while passing user role", aoEx);
			LOG_OBJECT.Error("Error Occured while passing user role", loAppEx);
			throw loAppEx;
		}
		return Boolean.FALSE;
	}

	/**
	 * @param aoRequest request parameter
	 * @return false
	 * @throws ApplicationException Application Exception
	 */
	public static Boolean isAcceleratorUser(RenderRequest aoRequest) throws ApplicationException
	{
		PortletSession loSession = aoRequest.getPortletSession();
		try
		{
			String loUserRole = (String) loSession.getAttribute(ApplicationConstants.KEY_SESSION_ORG_NAME,
					PortletSession.APPLICATION_SCOPE);
			if (loUserRole.equalsIgnoreCase(HHSConstants.CITY))
			{
				return Boolean.TRUE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While passing request parameter",
					aoEx);
			LOG_OBJECT.Error("Error Occured While passing request parameter", loAppEx);
			throw loAppEx;
		}
		return Boolean.FALSE;
	}

	/**
	 * This method simply return the transaction name based on given condition.
	 * 
	 * @param asEditingDocumentType String
	 * @param asUserOrgType String
	 * @return String
	 * @throws ApplicationException Application Exception
	 */
	public static String documentTypeTransactionName(String asEditingDocumentType, String asUserOrgType)
			throws ApplicationException
	{
		String lsTransctionName = null;
		try
		{
			if (asUserOrgType != null && ApplicationConstants.CITY_ORG.equalsIgnoreCase(asUserOrgType))
			{

				lsTransctionName = HHSConstants.UPDATE_RFP_DOC_PROPERTIES;
			}
			else if (asUserOrgType != null && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
			{

				if (asEditingDocumentType != null
						&& HHSConstants.AWARD_UPPER_CASE.equalsIgnoreCase(asEditingDocumentType))
				{
					lsTransctionName = HHSConstants.UPDATE_AWARD_DOC_PROPERTIES;
				}
				else
				{
					lsTransctionName = HHSConstants.UPDATE_PROPOSAL_DOC_PROPERTOES;
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the transaction name based on given condition", aoEx);
			LOG_OBJECT.Error("Error Occured While returning the transaction name based on given condition", loAppEx);
			throw loAppEx;
		}
		return lsTransctionName;

	}

	/**
	 * @param asUserOrgType user org type
	 * @param asUploadingDocType uploading doc type
	 * @param asModifiedDate modified date
	 * @param asParamMap param map
	 * @param asUserOrgId user organization id
	 * @return String This method simply return the transaction name based on
	 *         given condition.
	 * @throws ApplicationException Exception
	 * 
	 */

	public static String addDocumentFromVaultTransactionName(String asUploadingDocType, String asUserOrgType,
			Map<Object, Object> asParamMap, Date asModifiedDate, String asUserOrgId) throws ApplicationException
	{
		String lsTransactionName = null;

		try
		{
			if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
			{

				if (HHSConstants.AWARD_UPPER_CASE.equalsIgnoreCase(asUploadingDocType))
				{
					asParamMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
					asParamMap.put(HHSConstants.ORGANIZATION_ID, asUserOrgId);
					lsTransactionName = HHSConstants.INS_AWARD_DOC_DETAILS_DB;
				}
				else
				{
					asParamMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_COMPLETED));
					asParamMap.put(HHSConstants.DATE_LAST_MODIFIED, asModifiedDate);
					lsTransactionName = HHSConstants.INS_PROPOSAL_DOC_DETAILS_DB;
				}

			}
			else if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
			{
				asParamMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_DRAFT));
				lsTransactionName = HHSConstants.INS_RFP_DOC_DETAILS_DB;
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While getting the transaction Name",
					aoEx);
			LOG_OBJECT.Error("Error Occured While getting the transaction Name", loAppEx);
			throw loAppEx;
		}

		return lsTransactionName;
	}

	/**
	 * This method decides the transaction to upload document on the basis of
	 * type of document.
	 * Changes done for Enhancement #6429 for Release 3.4.0
	 * @param asUserOrgType usr org type
	 * @param asUploadingDocType uploading doc type
	 * @param asAwardId award id
	 * @param asProposalId proposal id
	 * @param aoParameterMap parameter map
	 * @param asOrganizationId organization id
	 * @return String This method simply return the transaction name based on
	 *         given condition.
	 * @throws ApplicationException exception
	 * 
	 *             This method was updated in R4.
	 * 
	 */

	public static String getTransactionNameInsertDocumentDetailsInDBOnUpload(String asUserOrgType,
			String asUploadingDocType, Map<String, Object> aoParameterMap, String asProposalId, String asAwardId,
			String asOrganizationId) throws ApplicationException
	{
		String lsTransactionName = null;

		try
		{
			if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.CITY_ORG))
			{

				aoParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_DRAFT));
				lsTransactionName = HHSConstants.INS_RFP_DOC_DETAILS_DB;
			}
			else if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))

			{
				// if user is uploading proposal documents
				if (asUploadingDocType.equalsIgnoreCase(HHSConstants.PROPOSAL_LOWERCASE))
				{
					aoParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_COMPLETED));
					lsTransactionName = HHSConstants.INS_PROPOSAL_DOC_DETAILS_DB;
				}
				// if the uploading document type is award type
				else if (asUploadingDocType.equalsIgnoreCase(HHSConstants.AWARD_LOWER_CASE))
				{
					aoParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
					aoParameterMap.put(HHSConstants.AWARD_ID, asAwardId);
					aoParameterMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
					aoParameterMap.put(HHSConstants.ORGANIZATION_ID, asOrganizationId);
					lsTransactionName = HHSConstants.INS_AWARD_DOC_DETAILS_DB;

				}
			}
			// If user is agency user and document type is BAFO, returns
			// transaction name to insert BAFO Document
			else if (asUserOrgType.equalsIgnoreCase(ApplicationConstants.AGENCY_ORG))
			{
				if (asUploadingDocType.equalsIgnoreCase(HHSConstants.BAFO))
				{
					aoParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
					lsTransactionName = HHSConstants.INSERT_BAFO_DOCS_DETAILS;
				}
				// Start || Changes done for Enhancement #6429 for Release 3.4.0
				else if (asUploadingDocType.equalsIgnoreCase(HHSConstants.STRING_AWARD_DOC))
				{
					aoParameterMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.DOCUMENT_SUBMITTED));
					lsTransactionName = HHSConstants.INSERT_AGENCY_AWARD_DOCS_DETAILS;
				}
				// End || Changes done for Enhancement #6429 for Release 3.4.0
			}
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While getting the transaction Name",
					aoEx);
			LOG_OBJECT.Error("Error Occured While getting the transaction Name", loAppEx);
			throw loAppEx;
		}

		return lsTransactionName;
	}

	/**
	 * This method will sort list of objects based on column name
	 * 
	 * @param aolistOfObjects a list of object to be sorted
	 * @param ascolumnName a string value of column name
	 * @throws ApplicationException Application Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static void sortList(List aolistOfObjects, String ascolumnName) throws ApplicationException
	{
		try
		{
			Collections.sort(aolistOfObjects, new com.nyc.hhs.frameworks.sessiongrid.SortComparator(ascolumnName, null,
					HHSConstants.ASC_STRING));
		}

		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While sorting list of objects based on column name", aoEx);
			LOG_OBJECT.Error("Error Occured While sorting list of objects based on column name", loAppEx);
			throw loAppEx;
		}
	}

	/**
	 * <p>
	 * This is a method that receives level users delimited by pipe (|) symbol
	 * in String format. It splits the string on pipe(|) and convert it into
	 * array of string
	 * </p>
	 * <ul>
	 * <li>Gets a particular level user names delimited by pipe(|) in String
	 * format</li>
	 * <li>Check if string length is zero then return String array of zero
	 * length</li>
	 * <li>else return string array after splitting on pipe(|) symbol</li>
	 * </ul>
	 * 
	 * @param asUserSeparatedByPipe all user name separated by pipe(|)
	 * @return String[] array of string containing user names
	 * @throws ApplicationException Exception thrown in case of any application
	 *             code failure.
	 */
	public static String[] convertStringToArray(String asUserSeparatedByPipe) throws ApplicationException
	{
		String loUserArr[] = null;
		try
		{
			if (asUserSeparatedByPipe.length() == 0)
			{
				loUserArr = new String[0];
			}
			else
			{
				loUserArr = asUserSeparatedByPipe.split(HHSConstants.DOUBLE_HHSUTIL_DELIM_PIPE);
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While receiving level users delimited by pipe (|) symbol in String format", aoEx);
			LOG_OBJECT.Error("Error Occured While receiving level users delimited by pipe (|) symbol in String format",
					loAppEx);
			throw loAppEx;
		}
		return loUserArr;
	}

	/**
	 * Changes done for Enhancement #6574 for Release 3.10.0
	 * This method returns the name of the transaction, depending upon the
	 * action to be performed out of two actions. here we are assuming the
	 * action name is the name of the transaction also.
	 * <ul>
	 * <li>Compare asAction with the asFirstAction, is matches then return
	 * asFirstAction as name of the transaction</li>
	 * <li>Compare asAction with the asSecondAction, is matches then return
	 * asSecondAction as name of the transaction</li>
	 * </ul>
	 * 
	 * @param asAction - action to be performed
	 * @param asFirstAction - first action of the two actions
	 * @param asSecondAction - second action of the two actions
	 * @return - name of the transaction
	 * @throws ApplicationException Application Exception
	 */
	public static String getTransactionName(String asAction, String asFirstAction, String asSecondAction, String asThirdAction)
			throws ApplicationException
	{
		String lsTransactionName = null;
		try
		{
			if (asAction != null && asAction.equalsIgnoreCase(asFirstAction))
			{
				lsTransactionName = asFirstAction;
			}
			else if (asAction != null && asAction.equalsIgnoreCase(asSecondAction))
			{
				lsTransactionName = asSecondAction;
			}
			// Start || Changes done for Enhancement #6574 for Release 3.10.0
			else if (asAction != null && asAction.equalsIgnoreCase(asThirdAction))
			{
				lsTransactionName = asThirdAction;
			}
			// End || Changes done for Enhancement #6574 for Release 3.10.0
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the name of the transaction", aoEx);
			LOG_OBJECT.Error("Error Occured While returning the name of the transaction", loAppEx);
			throw loAppEx;
		}
		return lsTransactionName;
	}

	/**
	 * This is custom method to copy one XML element list data to other List
	 * 
	 * @param aoDestination Destination XML element List
	 * @param aoSource Source XML element List
	 * @throws ApplicationException Application Exception
	 */
	public static List<Element> copyListToList(List<Element> aoDestination, List<Element> aoSource, String asCurrentTab)
			throws ApplicationException
	{
		List<Element> loOutputList = new ArrayList<Element>();
		try
		{
			if (null == asCurrentTab)
			{
				asCurrentTab = HHSConstants.AGENCY_TASK_TYPE_INBOX;
			}
			if (aoDestination != null && aoSource != null)
			{
				for (Element loSource : aoSource)
				{
					String lsTaskInboxEvaluateProposal = loSource.getAttributeValue(HHSConstants.TASK_INBOX);
					if (lsTaskInboxEvaluateProposal == null || lsTaskInboxEvaluateProposal.equals(asCurrentTab))
					{
						loOutputList.add((Element) loSource.clone());
					}
				}
				for (Element loSource : aoDestination)
				{
					loOutputList.add((Element) loSource.clone());
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while copying one XML element list data to other List", aoEx);
			LOG_OBJECT.Error("Error Occured while copying one XML element list data to other List", loAppEx);
			throw loAppEx;
		}
		return loOutputList;
	}

	/**
	 * This method returns the name of the transaction, depending upon the user
	 * org type.
	 * <ul>
	 * <li>Compare userOrgType with City,Provider or Accelerator and returning
	 * the transaction name to the homeProcurementController class</li>
	 * </ul>
	 * 
	 * @param asUserOrgType user org type
	 * @return - name of the transaction
	 * @throws ApplicationException Application Exception
	 */
	public static String homeProcurementTransaction(String asUserOrgType) throws ApplicationException
	{
		String lsTransctionName = null;
		try
		{
			if (asUserOrgType != null
					&& (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asUserOrgType) || ApplicationConstants.AGENCY_ORG
							.equalsIgnoreCase(asUserOrgType)))
			{
				lsTransctionName = HHSConstants.FETCH_PROC_COUNT_ACC_HOMEPAGE;
			}
			else if (asUserOrgType != null && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
			{
				lsTransctionName = HHSConstants.FETCH_PROCUREMENT_COUNT_PROV_HOME_PAGE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the name of the transaction", aoEx);
			LOG_OBJECT.Error("Error Occured While returning the name of the transaction", loAppEx);
			throw loAppEx;
		}
		return lsTransctionName;

	}

	/**
	 * This method returns the name of the transaction, depending upon the user
	 * org type.
	 * <ul>
	 * <li>Compare userOrgType with City,Provider or Accelerator and returning
	 * the transaction name to the homeFinancialController class</li>
	 * </ul>
	 * 
	 * @param asUserOrgType user org type
	 * @return - name of the transaction
	 * @throws ApplicationException Application Exception
	 */
	public static String homeFinancialTransaction(String asUserOrgType) throws ApplicationException
	{
		String lsTransactionName = null;
		try
		{
			if (asUserOrgType != null
					&& (ApplicationConstants.CITY_ORG.equalsIgnoreCase(asUserOrgType) || ApplicationConstants.AGENCY_ORG
							.equalsIgnoreCase(asUserOrgType)))
			{

				lsTransactionName = HHSConstants.FETCH_FINANCIAL_COUNT_ACC_HOME_PAGE;
			}
			else if (asUserOrgType != null && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(asUserOrgType))
			{

				lsTransactionName = HHSConstants.FETCH_FINANCIAL_COUNT_HOME_PAGE;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While preparing json response object", aoEx);
			LOG_OBJECT.Error("Error Occured While preparing json response object", loAppEx);
			throw loAppEx;
		}
		return lsTransactionName;

	}

	/**
	 * The Method will return the List of Evidence bean against list of Element
	 * Id's from Taxonomy Cache
	 * <ul>
	 * <li>1.If list of Element Id's is not null or empty ,load Taxonomy Cache
	 * document</li>
	 * <li>2.Parse the retrieved xml from 1. on the basis of elementId</li>
	 * <li>3.Get the desired value xml i.e evidencerequiredflag,activeflag,name
	 * and set into evidence bean</li>
	 * <li>4.Repeat step 3 for each elementId and add Evidence bean to list
	 * </ii>
	 * <li>.5Return the list</li>
	 * </ul>
	 * 
	 * @param aoElementIdList element id list
	 * @return List<EvidenceBean>
	 * @throws ApplicationException exception
	 */
	public static List<EvidenceBean> getEvidenceFlag(List<String> aoElementIdList) throws ApplicationException
	{
		List<EvidenceBean> loEvidenceBeanList = null;
		try
		{
			if (aoElementIdList != null && !aoElementIdList.isEmpty())
			{
				loEvidenceBeanList = new ArrayList<EvidenceBean>();
				final org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						ApplicationConstants.TAXONOMY_ELEMENT);
				for (String loElementId : aoElementIdList)
				{
					EvidenceBean loEvidenceBean = new EvidenceBean();
					Element loCorrRuleElt = XMLUtil.getElement("//element[@id=\"" + loElementId + "\"]", loDoc);
					String lsEvidenceRequiredflag = loCorrRuleElt.getAttributeValue(HHSConstants.EVE_REQ_FLAG);
					String lsActiveflag = loCorrRuleElt.getAttributeValue(HHSConstants.HHSUTIL_ACTIVEFLAG);
					String loElementName = loCorrRuleElt.getAttributeValue(HHSConstants.NAME);
					loEvidenceBean.setElementId(loElementId);
					loEvidenceBean.setElementName(loElementName);
					loEvidenceBean.setEvidenceFlag(lsEvidenceRequiredflag);
					loEvidenceBean.setActiveFlag(lsActiveflag);
					loEvidenceBeanList.add(loEvidenceBean);
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the List of Evidence bean against list of Element Id's from Taxonomy Cache",
					aoEx);
			LOG_OBJECT
					.Error("Error Occured While returning the List of Evidence bean against list of Element Id's from Taxonomy Cache",
							loAppEx);
			throw loAppEx;
		}
		return loEvidenceBeanList;
	}

	/**
	 * This method identifies whether or not '*'/'**' messgae will be displayed
	 * on the evaluation and result page below table.
	 * <ul>
	 * <li>1.Create map loDisplayStarsMap to hold result to be returned.</li>
	 * <li>2.Iterate aoEvaluationBeanList which method is getting as an input.</li>
	 * <li>3.if proposal status is selected then set loSelectedproposalMsgFlag
	 * as false.</li>
	 * <li>4. Make check to display star/double star.</ii>
	 * <li>5. Set the result in the map loDisplayStarsMap.</ii>
	 * <li>6. Return the map loDisplayStarsMap</li>
	 * </ul>
	 * 
	 * @param aoEvaluationBeanList - EvaluationBean list
	 * @return loDisplayStarsMap
	 * @throws ApplicationException - throws ApplicationException
	 */
	public static Map<String, Boolean> getStarDoubleStarStatus(List<EvaluationBean> aoEvaluationBeanList)
			throws ApplicationException
	{
		Boolean loSelectedproposalMsgFlag = Boolean.TRUE;
		Map<String, Boolean> loDisplayStarsMap = new HashMap<String, Boolean>();
		try
		{
			for (EvaluationBean loEvaluationBean : aoEvaluationBeanList)
			{
				if (loEvaluationBean.getProposalStatusId() != null
						&& loEvaluationBean.getProposalStatusId() == Integer.parseInt(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SELECTED))
						&& loSelectedproposalMsgFlag)
				{
					loSelectedproposalMsgFlag = Boolean.FALSE;
				}
				if (loEvaluationBean.getAwardApprovalDate() != null)
				{
					if (loEvaluationBean.getAwardReviewStatus() != null
							&& (loEvaluationBean.getAwardReviewStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS))
									|| loEvaluationBean.getAwardReviewStatusId().equalsIgnoreCase(
											PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
													HHSConstants.STATUS_AWARD_REVIEW_IN_REVIEW)) || loEvaluationBean
									.getAwardReviewStatusId().equalsIgnoreCase(
											PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
													HHSConstants.STATUS_AWARD_REVIEW_RETURNED))))
					{
						if (loEvaluationBean.getApprovedStatus() != null
								&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE))
						{
							loDisplayStarsMap.put(HHSConstants.DOUBLE_STAR, true);
						}
						//Start || Changes done for enhancement 6574 for Release 3.10.0
						if (loEvaluationBean.getApprovedStatus() != null
								&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
						{
							if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
									HHSConstants.PROPOSAL_STATUS_SELECTED)
									&& !loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE))
							{
								loDisplayStarsMap.put(HHSConstants.DOUBLE_STAR, true);
							}
							loDisplayStarsMap.put(HHSConstants.STAR, true);
						}
					}
					else if (loEvaluationBean.getAwardReviewStatus() != null
							&& (loEvaluationBean.getAwardReviewStatusId().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_AWARD_REVIEW_UPDATE_IN_PROGRESS_TEMP))))
					{
						if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS_SELECTED)
								&& loEvaluationBean.getApprovedStatus() != null
								&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE)
								&& !loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE))
						{
							loDisplayStarsMap.put(HHSConstants.DOUBLE_STAR, true);
						}
						else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(HHSConstants.PROPOSAL_STATUS_SELECTED)
								&& loEvaluationBean.getApprovedStatus() != null
								&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.YES_LOWERCASE)
								&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE))
						{
							loDisplayStarsMap.put(HHSConstants.STAR, true);
						}
						else if (loEvaluationBean.getProposalStatus().equalsIgnoreCase(
								HHSConstants.PROPOSAL_STATUS_SELECTED)
								&& loEvaluationBean.getModifiedFlag().equalsIgnoreCase(HHSConstants.ONE)
								&& loEvaluationBean.getApprovedStatus() != null
								&& loEvaluationBean.getApprovedStatus().equalsIgnoreCase(HHSConstants.NO))
						{
							loDisplayStarsMap.put(HHSConstants.STAR, true);
						}
					}
					//End || Changes done for enhancement 6574 for Release 3.10.0
				else
					{
						if (loDisplayStarsMap.get(HHSConstants.DOUBLE_STAR) == null)
						{
							loDisplayStarsMap.put(HHSConstants.DOUBLE_STAR, false);
						}
						if (loDisplayStarsMap.get(HHSConstants.STAR) == null)
						{
							loDisplayStarsMap.put(HHSConstants.STAR, false);
						}
					}
						}
				else
				{
					if (loDisplayStarsMap.get(HHSConstants.DOUBLE_STAR) == null)
					{
						loDisplayStarsMap.put(HHSConstants.DOUBLE_STAR, false);
					}
					if (loDisplayStarsMap.get(HHSConstants.STAR) == null)
					{
						loDisplayStarsMap.put(HHSConstants.STAR, false);
					}
				}
			}
			loDisplayStarsMap.put(HHSConstants.SELECTED_PROPOSAL_MESSAGE, loSelectedproposalMsgFlag);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While identifing whether or not '*'/'**' messgae will be displayed on the evaluation",
					aoEx);
			LOG_OBJECT
					.Error("Error Occured While identifing whether or not '*'/'**' messgae will be displayed on the evaluation",
							loAppEx);
			throw loAppEx;
		}
		return loDisplayStarsMap;
	}

	/**
	 * This method get Date from EPOCH time
	 * 
	 * @param asEpochTime EPOCH time as string
	 * @return Date
	 * @throws ApplicationException Application Exception
	 */
	public static Date getDateFromEpochTime(String asEpochTime) throws ApplicationException
	{
		Date loDate = null;
		try
		{
			loDate = new Date(Long.parseLong(asEpochTime) * 1000);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While getting Date from EPOCH time",
					aoEx);
			LOG_OBJECT.Error("Error Occured While getting Date from EPOCH time", loAppEx);
			throw loAppEx;
		}
		return loDate;
	}

	/**
	 * This method convert date into EPOCH time
	 * 
	 * @param asDate date object
	 * @return EPOCH time as string
	 * @throws ApplicationException Application Exception
	 */
	public static String getEpochTimeFromDate(String asDate) throws ApplicationException
	{

		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date loDate;
		try
		{
			loDate = loDateFormat.parse(asDate);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured While Parsing Date", aoEx);
			LOG_OBJECT.Error("Error Occured While Parsing Date", loAppEx);
			throw loAppEx;
		}
		long loEpochTime = loDate.getTime() / 1000;
		return String.valueOf(loEpochTime);
	}

	/**
	 * This Method changes the inputed Date in "yyyy-MM-dd hh:mm:ss" format
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static Date getUtilDate(String aoDate) throws ApplicationException
	{
		SimpleDateFormat loFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date loUtilDate = null;
		try
		{
			loUtilDate = loFormatter.parse(aoDate);
		}
		catch (ParseException aoExp)
		{
			ApplicationException loAppEx = new ApplicationException("Application Exception in DateUtil", aoExp);
			LOG_OBJECT.Error("Application Exception in DateUtil", loAppEx);
			throw loAppEx;
		}
		return loUtilDate;
	}

	/**
	 * This method marshal an object to Document
	 * 
	 * @param aoObject Object
	 * @return saxHandler.getDocument() Document
	 * @throws ApplicationException ApplicationException Object
	 */
	public static Document marshalObject(Object aoObject) throws ApplicationException
	{
		SAXHandler loSaxHandler = new SAXHandler();
		Mapping loMapping = new Mapping();
		try
		{
			if(CASTOR_FILE_PATH!=null && !StringUtils.isEmpty(CASTOR_FILE_PATH)){
				loMapping.loadMapping(CASTOR_FILE_PATH);
			}else{			
				loMapping.loadMapping(getCastorPath());
			}
			Marshaller loMarshaller = new Marshaller(loSaxHandler);
			loMarshaller.setMapping(loMapping);
			loMarshaller.marshal(aoObject);

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured while marshalling object ", aoEx);
			LOG_OBJECT.Error("Error occured while marshalling object", loAppEx);
			throw loAppEx;
		}
		return loSaxHandler.getDocument();
	}

	/**
	 * This method convert an String xml to Object
	 * 
	 * @param asCastorXml String XML
	 * @return saxHandler.getDocument() Document
	 * @throws ApplicationException ApplicationException Object
	 */

	public static Object unmarshalObject(String asCastorXml) throws ApplicationException
	{
		Object loObject = null;
		SAXBuilder loSaxBuilder = new SAXBuilder();
		Mapping loMapping = new Mapping();
		StringWriter loStringWriter = new StringWriter();
		XMLOutputter loXmlOut = new XMLOutputter();

		try
		{			
			if(CASTOR_FILE_PATH!=null && !StringUtils.isEmpty(CASTOR_FILE_PATH)){
				loMapping.loadMapping(CASTOR_FILE_PATH);
			}else{			
				loMapping.loadMapping(getCastorPath());
			}
			Document loDoc = loSaxBuilder.build(new StringReader(asCastorXml));
			loXmlOut.output(loDoc.getRootElement(), loStringWriter);
			Reader loReader = new StringReader(loStringWriter.toString());
			Unmarshaller loUnmarshaller = new Unmarshaller(loMapping);
			loObject = loUnmarshaller.unmarshal(loReader);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured while unmarshalling object", aoEx);
			throw new ApplicationException("Error occured while unmarshalling object ", aoEx);
		}
		return loObject;

	}

	private static String getCastorPath() {
		String lsCastorPath="";
		String lsClassName = HHSUtil.class.getName();
		int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
		if (liIndex > -1)
		{
			lsClassName = lsClassName.substring(liIndex + 1);
		}
		lsClassName = lsClassName + HHSConstants.DOT_CLASS;					
		String str =HHSUtil.class.getResource(lsClassName)+ HHSConstants.EMPTY_STRING;
		String packageName= HHSUtil.class.getPackage().getName();
		packageName = packageName.replaceAll("\\.", "\\/");			
		lsCastorPath = (str.replace( packageName + "/"+ lsClassName, HHSConstants.CASTOR_MAPPING ));
		LOG_OBJECT.Info("HHSUtil.getCastorPath(), lsCastorPath:"+lsCastorPath);
		return lsCastorPath;
	}

	/**
	 * This method convert a Document object to XML String
	 * 
	 * @param aoDoc Document
	 * @return saxHandler.getDocument() Document
	 * @throws ApplicationException ApplicationException Object
	 */
	public static String convertDocumentToXML(Document aoDoc) throws ApplicationException
	{
		StringWriter loWriter = new StringWriter();
		XMLOutputter loXmlOutput = new XMLOutputter(Format.getPrettyFormat());

		try
		{
			loXmlOutput.output(aoDoc, loWriter);
		}
		// Catch the IOException thrown at any instance and wrap it into
		// application exception and throw
		catch (IOException aoEx)
		{
			LOG_OBJECT.Error("Error occured while converting document to xml ", aoEx);
			throw new ApplicationException("Error occured while converting document to xml ", aoEx);
		}

		String lsXmlContent = loWriter.toString();
		return lsXmlContent;
	}

	/**
	 * This method convert InputStream to XML String
	 * 
	 * @param aoContent InputStream
	 * @return saxHandler.getDocument() Document
	 * @throws ApplicationException ApplicationException Object
	 */
	public static String convertInputStreamToXml(InputStream aoContent) throws ApplicationException
	{
		BufferedReader loBufferReader = null;
		StringBuilder loStringBuilder = new StringBuilder();

		String lsLine;
		try
		{

			loBufferReader = new BufferedReader(new InputStreamReader(aoContent));
			while ((lsLine = loBufferReader.readLine()) != null)
			{
				loStringBuilder.append(lsLine);
			}

		}
		catch (IOException loIOExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"IO Exception occured while converting InputStream to XML :  convertInputStreamToXml", loIOExp);
			LOG_OBJECT.Error("IO Exception occured while converting InputStream to XML :  convertInputStreamToXmll ",
					loAppEx);
			throw loAppEx;
		}
		finally
		{
			if (loBufferReader != null)
			{
				try
				{
					loBufferReader.close();
					if (null != aoContent)
					{
						aoContent.close();
					}
				}
				catch (IOException loExp)
				{
					ApplicationException loAppEx = new ApplicationException(
							"Exception occured while closing the stream in finally block :  convertInputStreamToXml",
							loExp);
					LOG_OBJECT.Error(
							"Exception occured while closing the stream in finally block :  convertInputStreamToXml ",
							loAppEx);
					throw loAppEx;
				}
			}
			else if (null != aoContent)
			{
				try
				{
					aoContent.close();
				}
				catch (IOException loExp)
				{
					ApplicationException loAppEx = new ApplicationException(
							"Exception occured while closing the ContentStream in finally block :  convertInputStreamToXml",
							loExp);
					LOG_OBJECT
							.Error("Exception occured while closing the ContentStream in finally block :  convertInputStreamToXml",
									loAppEx);
					throw loAppEx;
				}
			}

		}

		return loStringBuilder.toString();
	}

	/**
	 * This method converts XML String to FileInputStream object
	 * 
	 * @param aoMasterBean MasterBean object
	 * @return lsConvertedXml String
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public static FileInputStream convertXmlToStream(String asConvertedXml) throws ApplicationException
	{
		File loTmpFile = null;
		BufferedWriter loWriter = null;
		FileInputStream loFIS = null;
		try
		{
			loTmpFile = File.createTempFile("MyTempFile", null);
			loTmpFile.deleteOnExit();
			loWriter = new BufferedWriter(new FileWriter(loTmpFile));
			loWriter.write(asConvertedXml);
			loFIS = new FileInputStream(loTmpFile);
		}
		catch (IOException loIOExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"IO Exception occured while converting xml to stream :  convertXmlToStream", loIOExp);
			LOG_OBJECT.Error("IO Exception occured while converting xml to stream :  convertXmlToStream", loAppEx);
			throw loAppEx;
		}
		finally
		{
			try
			{
				if (loWriter != null)
				{
					loWriter.close();
				}
			}
			catch (Exception loExp)
			{
				ApplicationException loAppEx = new ApplicationException(
						"Exception occured while closing the stream in finally block :  convertXmlToStream", loExp);
				LOG_OBJECT.Error("Exception occured while closing the stream in finally block :  convertXmlToStream",
						loAppEx);
				throw loAppEx;
			}
		}
		return loFIS;

	}

	/**
	 * This method will get the agency map for the procurement
	 * 
	 * @return map
	 * @throws ApplicationException if any exception occurred
	 */
	public static Map<String, String> getAgencyMapForProcurement() throws ApplicationException
	{
		List<Map<String, String>> loAgencyMapList = getProcuringAgencyFromDB();
		Map<String, String> loAgencySortedMap = new HashMap<String, String>();
		for (Iterator loAgencyItr = loAgencyMapList.iterator(); loAgencyItr.hasNext();)
		{
			Map<String, String> loAgencyMap = (Map<String, String>) loAgencyItr.next();
			loAgencySortedMap.put(loAgencyMap.get("AGENCY_ID"), loAgencyMap.get("AGENCY_NAME")
					+ HHSConstants.BEGINNING_BRACKET + loAgencyMap.get("AGENCY_ID") + HHSConstants.CLOSING_BRACKET);
		}
		return sortByValues(loAgencySortedMap);
	}

	/**
	 * This Method changes the inputed Date in "yyyy-MM-dd hh:mm:ss" format to
	 * MM/DD/YYYY output format.
	 * 
	 * @param aoDate Date to be changed
	 * @return String
	 */
	public static String formatDateToMMDDYYYY(String asUnFormattedDate) throws ApplicationException
	{
		String lsStrFormattedDate = null;
		try
		{
			if (null != asUnFormattedDate && !asUnFormattedDate.isEmpty())
			{

				String[] loSplittedDateArray = asUnFormattedDate.split(HHSConstants.SPACE);
				String[] loSplittedDateArray2 = loSplittedDateArray[HHSConstants.INT_ZERO].split(HHSConstants.HYPHEN);

				String lsStrFormattedDateMonth = loSplittedDateArray2[HHSConstants.INT_ONE];
				String lsStrFormattedDateDay = loSplittedDateArray2[HHSConstants.INT_TWO];
				String lsStrFormattedDateYear = loSplittedDateArray2[HHSConstants.INT_ZERO];

				lsStrFormattedDate = lsStrFormattedDateMonth.concat(HHSConstants.FORWARD_SLASH)
						.concat(lsStrFormattedDateDay).concat(HHSConstants.FORWARD_SLASH)
						.concat(lsStrFormattedDateYear);
			}
			else
			{
				return asUnFormattedDate;
			}

		}
		catch (Exception aoExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception ocured while foarmatting date in HHSUtil:formatDateToMMDDYYYY method.", aoExp);
			LOG_OBJECT
					.Error("Exception ocured while foarmatting date in HHSUtil:formatDateToMMDDYYYY method.", loAppEx);
			throw loAppEx;
		}
		return lsStrFormattedDate;
	}

	/**
	 * This method will convert string date to date object
	 * 
	 * @return Date
	 */
	public static Date ConvertStringToDate(String asDate)
	{
		SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date loDate = null;
		try
		{
			loDate = loDateFormat.parse(asDate);
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while converting String to date", aoEx);
		}
		return loDate;
	}

	/**
	 * This method will get the current fiscal year.
	 * 
	 * @return Date
	 */
	public static Integer GetFiscalYear()
	{
		Integer liCurrentMonth = HHSConstants.INT_ZERO;
		Integer liCurrentYear = HHSConstants.INT_ZERO;
		Integer liFiscalYear = HHSConstants.INT_ZERO;
		try
		{
			Calendar loCalendar = Calendar.getInstance();
			liCurrentMonth = loCalendar.get(Calendar.MONTH) + HHSConstants.INT_ONE;
			liCurrentYear = loCalendar.get(Calendar.YEAR);
			if (liCurrentMonth < 7)
			{
				liFiscalYear = liCurrentYear;
			}
			else
			{
				liFiscalYear = liCurrentYear + 1;
			}

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while retriving current fiscal year", aoEx);
		}
		return liFiscalYear;
	}

	/**
	 * This method decrypt the encryped username and password *
	 * @return lsOutput decryped String
	 */
	public static String decrypt(String asEncryptedStr)
	{

		byte[] lsOutput;
        /*[Start] R7.8.0 replace to Aphach common*/
/*
		try
		{
			lsOutput = new BASE64Decoder().decodeBuffer(asEncryptedStr);
			return new String(lsOutput);
		}
		catch (IOException aoExc)
		{
			LOG_OBJECT.Error("Exception occur while decrypting ::: " + asEncryptedStr, aoExc);
		}
*/		
        lsOutput = new Base64().decode(asEncryptedStr);
        /*[End] R7.8.0 replace to Aphach common*/
		
		return new String(lsOutput);
	}

	/**
	 * This method return the decrypted text for an inputed encrypted text.
	 * 
	 */

	public static String decryptASEString(String aoEncryptedText) throws ApplicationException
	{

		return DecryptionUtil.decrytText(aoEncryptedText);
	}

	/**
	 * This method adds a folder to zip
	 */
	public static void zipFolder(String asSrcFolder, String asDestZipFile) throws ApplicationException
	{
		LOG_OBJECT.Info("Inside method zipFolder of Class SaveFormOnLocalUtil");
		ZipOutputStream loZip = null;
		FileOutputStream loFileWriter = null;
		try
		{
			loFileWriter = new FileOutputStream(asDestZipFile);
			loZip = new ZipOutputStream(loFileWriter);
		}
		catch (FileNotFoundException aoExp)
		{
			LOG_OBJECT.Error("FileNotFound error", aoExp);
			throw new ApplicationException("FileNotFound error", aoExp);
		}
		addFolderToZip("", asSrcFolder, loZip);
		try
		{
			loZip.flush();
			loZip.close();
		}
		catch (IOException aoExp)
		{
			LOG_OBJECT.Error("Error in IO ", aoExp);
			throw new ApplicationException("Error in IO", aoExp);
		}
	}

	/**
	 * This method is used to add a folder to zip at the path provided.
	 * @param path
	 * @param srcFolder
	 * @param zip
	 * @throws ApplicationException
	 */
	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws ApplicationException
	{
		LOG_OBJECT.Info("Inside method addFolderToZip of Class SaveFormOnLocalUtil");
		File loFolder = new File(srcFolder);
		String loFileList[] = loFolder.list();
		int liCount = 0;
		while (liCount < loFileList.length)
		{
			addToZip(((path.isEmpty()) ? loFolder.getName() : path + "/" + loFolder.getName()), srcFolder + "/"
					+ loFileList[liCount], zip);
			liCount++;
		}
	}

	/**
	 * This method is used to add a file to zip at the path provided.
	 * @param asPath
	 * @param asSrcFile
	 * @param asZip
	 * @throws ApplicationException
	 */
	private static void addToZip(String asPath, String asSrcFile, ZipOutputStream asZip) throws ApplicationException
	{
		LOG_OBJECT.Info("Inside method addToZip of Class SaveFormOnLocalUtil");
		File loFolder = new File(asSrcFile);
		FileInputStream loFileInputStream = null;
		if (loFolder.isDirectory())
		{
			addFolderToZip(asPath, asSrcFile, asZip);
		}
		else
		{
			// Transfer bytes from in to out
			byte[] loBuf = new byte[1024];
			int liLen;
			try
			{
				loFileInputStream = new FileInputStream(asSrcFile);
				asZip.putNextEntry(new ZipEntry(asPath + "/" + loFolder.getName()));
				while ((liLen = loFileInputStream.read(loBuf)) > 0)
				{
					asZip.write(loBuf, 0, liLen);
				}
			}
			catch (FileNotFoundException aoExp)
			{
				LOG_OBJECT.Error("FileNotFound error", aoExp);
				throw new ApplicationException("FileNotFound error", aoExp);
			}
			catch (IOException aoIOExp)
			{
				LOG_OBJECT.Error("Error in IO ", aoIOExp);
				throw new ApplicationException("Error in IO", aoIOExp);
			}
			finally
			{
				if (loFileInputStream != null)
				{
					try
					{
						loFileInputStream.close();
					}
					catch (IOException aoExp)
					{
						throw new ApplicationException("Error HHSUtill : addToZip - not able to close the stream",
								aoExp);
					}
				}
			}
		}
	}

	/**
	 * This method add comments or status change in audit for budgets on finish
	 * of config task or close of contract.
	 * 
	 * call the transaction 'getBudgetIdListFromContractId'
	 * 
	 * @param aoAuditList Audit list bean object.
	 * @param asContractId Contract Id
	 * @param asBudgetType Budget Type
	 * @param asEntityType Entity Type
	 * @param aoTaskDetailsBean Task details bean object
	 * @throws ApplicationException Application Exception
	 */
	public static void auditConfigCommnetsOnBudgets(List<HhsAuditBean> aoAuditList, String asContractId,
			String asBudgetType, String asEntityType, String asUserId, String asComment, Boolean aoCloseBudgetAudit)
			throws ApplicationException
	{
		try
		{
			if (null != aoAuditList
					&& ((null != asComment && !(HHSConstants.EMPTY_STRING.equalsIgnoreCase(asComment))) || aoCloseBudgetAudit))
			{
				Channel loChannel = new Channel();
				String lsBudgetId = null;
				String lsBudgetStatusId = null;
				String lsBudgetStatus = null;
				loChannel.setData(HHSConstants.CONTRACT_ID_WORKFLOW, asContractId);
				loChannel.setData(HHSConstants.BUDGET_TYPE_ID, asBudgetType);
				HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_BUDGET_LIST_TRN);
				List<HashMap> loBudgetList = (List) loChannel.getData(HHSConstants.BUDGET_ID_LIST);
				if (null != loBudgetList)
				{
					for (HashMap loBudgetDetails : loBudgetList)
					{
						lsBudgetId = (loBudgetDetails.get(HHSConstants.BUDGET_ID_HASH_KEY)).toString();
						lsBudgetStatusId = (loBudgetDetails.get(HHSConstants.STATUS_ID_KEY)).toString();
						lsBudgetStatus = HHSUtil.getStatusName(HHSConstants.BUDGETLIST_BUDGET,
								Integer.valueOf(lsBudgetStatusId));
						if (!aoCloseBudgetAudit)
						{
							aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
									HHSConstants.AGENCY_COMMENTS_DATA, asComment, asEntityType, lsBudgetId, asUserId,
									HHSConstants.AGENCY_AUDIT));
						}
						else
						{
							aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE,
									HHSConstants.STATUS_CHANGE, ApplicationConstants.STATUS_CHANGED_FROM
											+ HHSConstants.SPACE + HHSConstants.STR + lsBudgetStatus + HHSConstants.STR
											+ HHSConstants.TO + HHSConstants.STR + HHSConstants.STATUS_CLOSED
											+ HHSConstants.STR, asEntityType, lsBudgetId, asUserId,
									HHSConstants.AGENCY_AUDIT));
						}

					}
				}
			}

		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While adding audit Bean list in Channel on Finish Of Config Tasks", aoEx);
			LOG_OBJECT
					.Error("Error Occured While adding audit Bean list in Channel on Finish Of Config Tasks", loAppEx);
			throw loAppEx;
		}

	}

	/**
	 * This method is used to fetch procuring agencies list from database
	 **/
	private static List<Map<String, String>> getProcuringAgencyFromDB() throws ApplicationException
	{
		Channel loChannel = new Channel();
		List<Map<String, String>> loProcuringAgencyMap = null;
		try
		{
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.FETCH_PROCURING_AGENCIES);
			loProcuringAgencyMap = (List<Map<String, String>>) loChannel.getData(HHSConstants.PROCURING_AGENCIES_MAP);
		}// Catch the Exception thrown at any instance and wrap it into
			// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error Occured while getting procuring agency",
					aoEx);
			LOG_OBJECT.Error("Error Occured while getting procuring agency", loAppEx);
			throw loAppEx;
		}
		return loProcuringAgencyMap;
	}

	/**
	 * This method checks the span of fiscal years based on contract start and
	 * end date The method is added for enhancement 5707 as part of release
	 * 2.6.1
	 * <ul>
	 * <li>Get Fiscal Start Year</li>
	 * <li>Get Fiscal End Year</li>
	 * <li>Get max year span from cache</li>
	 * <li>check if start and end date fall under max limited span</li>
	 * </ul>
	 * @param asContractStartDate Contract Start Date
	 * @param asContractEndDate Contract End Date return boolean
	 * @throws ApplicationException Application Exception
	 */
	public static boolean checkContractFiscalYearsSpan(String asContractStartDate, String asContractEndDate)
			throws ApplicationException
	{
		boolean lbSpanExceed = false;
		try
		{
			if (null != asContractStartDate && !asContractStartDate.isEmpty() && null != asContractEndDate
					&& !asContractEndDate.isEmpty())
			{
				String[] loStartDateArray = asContractStartDate.split(HHSConstants.FORWARD_SLASH);
				String[] loEndDateArray = asContractEndDate.split(HHSConstants.FORWARD_SLASH);
				int liStartMonth = Integer.parseInt(loStartDateArray[0]);
				int liStartYear = Integer.parseInt(loStartDateArray[2]);
				int liEndMonth = Integer.parseInt(loEndDateArray[0]);
				int liEndYear = Integer.parseInt(loEndDateArray[2]);
				int liYearCount = 0;
				if (liStartMonth > HHSConstants.INT_SIX)
				{
					liStartYear = liStartYear + 1;
				}
				if (liEndMonth > HHSConstants.INT_SIX)
				{
					liEndYear = liEndYear + 1;
				}
				liYearCount = (liEndYear - liStartYear) + 1;
				String lsAppSettingMapKey = HHSConstants.MAX_CONTRACT_TERM_SPAN + HHSConstants.UNDERSCORE
						+ HHSConstants.MAX_CONTRACT_TERM_SPAN;
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				int liMaxContractSpan = Integer.valueOf((String) loApplicationSettingMap.get(lsAppSettingMapKey))
						.intValue();
				if (liYearCount > liMaxContractSpan)
				{
					lbSpanExceed = true;
				}
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While returning the fiscal years based on contract start and end date", aoEx);

			LOG_OBJECT.Error("Error:: HHSUtil:" + "checkContractFiscalYearsSpan method - "
					+ "Error Occured While returning the boolean after checking span of fiscal years", aoEx);
			throw loAppEx;
		}
		return lbSpanExceed;
	}
	
/*
 * [Start]  Added for QC9398 R8.3.0
 */
    private Calendar setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    /**
     * 
     * This method generate FY list for In-flight contract.
     * 1. All FY for contract are(is) generated up to current FY.  
     * 2. if contract Start FY is equal to or greater then current FY generate only the contract start FY
     *  
     */

    public ArrayList<Integer> FYListInt( Calendar aoCalendarSrt, Calendar aoCalendarEnd){
    	return FYListInt(  aoCalendarSrt,  aoCalendarEnd , 0 );
    }	

    public ArrayList<Integer> FYListInt( Calendar aoCalendarSrt, Calendar aoCalendarEnd , int aoNewFYDur){
    	ArrayList<Integer> loFYids = new ArrayList<Integer>();
    	
        FiscalDate loSrtCon = null;
        FiscalDate loEndCon = null;
        FiscalDate loNewFyCon = null;
        

        if( aoCalendarSrt.after(aoCalendarEnd)){
        	loSrtCon = new FiscalDate(aoCalendarEnd);
        	loEndCon = new FiscalDate(aoCalendarSrt);
        } else{
        	loSrtCon = new FiscalDate(aoCalendarSrt);
        	loEndCon = new FiscalDate(aoCalendarEnd);
        }

        FiscalDate cursorCon = loSrtCon ;

        FiscalDate loCurrentFY =  new FiscalDate(Calendar.getInstance());
    	Calendar loTmpCal = setDate(loCurrentFY.getCalendarYear()   , loCurrentFY.getCalendarMonth() , loCurrentFY.getCalendarDay());
		loTmpCal.add(Calendar.DAY_OF_MONTH, aoNewFYDur);
    	loNewFyCon = new FiscalDate(loTmpCal) ;

        //first loop is default to run for generating FY
        while(true){
	        int loFiscal= cursorCon.getFiscalYear();
	        if( !loFYids.contains(loFiscal)){
	        	loFYids.add(loFiscal);
	        }
	        FiscalDate  cue2 = new FiscalDate(setDate(cursorCon.getCalendarYear() + 1 , cursorCon.getCalendarMonth(), cursorCon.getCalendarDay() )) ;

        	if( loNewFyCon.getFiscalYear() <= cursorCon.getFiscalYear() ) {
        		break;
        	}
	        if( cue2.getFiscalYear()  >  loEndCon.getFiscalYear()  ){
	        	break;
	        }
	        
	        cursorCon = cue2;
        }

    	return loFYids;
    }
/*
 * [End] Added for QC9398 R8.3.0	
 */
	
	/**
	 * Method to convert list of maps to JSON Object
	 * @param aoList - list to be converted
	 * @return JSON object as string
	 * @throws ApplicationException
	 * 
	 *             This method was added in R4
	 */
	public static String listMapToJSON(List<Map<String, String>> aoList, String aoPartialSearchTerms, String asKeyName,
			String asValueName, int aiMinLength)
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
		loOutputBuffer.append(HHSConstants.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoList.size(); liCount++)
		{
			Map<String, String> loMapIterator = aoList.get(liCount);
			final String lsCurrValue = loMapIterator.get(asValueName);
			final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
			if ((lsCurrValue.length() >= aiMinLength) && lsMatcher.find())
			{
				loOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
				loOutputBuffer.append(lsCurrValue.replace("\"", HHSConstants.SINGLE_QUOTE));
				loOutputBuffer.append(HHSConstants.DQUOTES_COMMA);
			}
		}

		if (loOutputBuffer.indexOf(HHSConstants.COMMA) != -1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSConstants.COMMA));
		}
		loOutputBuffer.append("],");
		loOutputBuffer.append("\"data\":");
		loOutputBuffer.append(HHSConstants.SQUARE_BRAC_BEGIN);
		for (int liCount = 0; liCount < aoList.size(); liCount++)
		{
			Map<String, String> loMapIterator = aoList.get(liCount);
			final String lsCurrValue = loMapIterator.get(asValueName);
			final String lsHiddenValue = loMapIterator.get(asKeyName);
			final Matcher lsMatcher = loPattern.matcher(lsCurrValue.toLowerCase());
			if (lsMatcher.find())
			{
				loOutputBuffer.append(HHSConstants.DOUBLE_QUOTES);
				loOutputBuffer.append(lsHiddenValue.replace("\"", HHSConstants.SINGLE_QUOTE));
				loOutputBuffer.append(HHSConstants.DQUOTES_COMMA);
			}
		}
		if (loOutputBuffer.indexOf(HHSConstants.COMMA) != -1)
		{
			loOutputBuffer.deleteCharAt(loOutputBuffer.lastIndexOf(HHSConstants.COMMA));
		}
		loOutputBuffer.append("]");
		loOutputBuffer.append("}");
		return loOutputBuffer.toString();
	}

	/**
	 * <ul>
	 * <li>Method to fetch EntryTypelist of ContractBudget</li>
	 * <li>calls the transaction 'fetchEntryTypeDetails'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoContractId String Object
	 * @param aoBudgetId String Object
	 * @param lsUpdatedBudgetId String Object
	 * @param aoScreenName String Object
	 * @return loEntryTypeDetails List<String> Object
	 * @throws ApplicationException
	 * 
	 * 
	 * 
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static List<String> getEntryTypeDetail(String aoContractId, String aoBudgetId, String aoUpdatedBudgetId,
			String aoScreenName, String aoFy) throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, aoContractId);
		loHashMap.put(HHSConstants.BUDGET_ID, aoBudgetId);
		loHashMap.put(HHSConstants.UPDATE_CONTRACT_ID, aoUpdatedBudgetId);
		loHashMap.put(HHSConstants.SCREEN_NAME, aoScreenName);
		loHashMap.put(HHSConstants.FISCAL_YEAR, aoFy);
		Channel loChannelObj = new Channel();
		loChannelObj.setData(HHSConstants.AO_HASH_MAP, loHashMap);
		HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_ENTRY_TYPE_DETAILS);
		List<String> loEntryTypeDetails = (List<String>) loChannelObj.getData(HHSConstants.ENTRY_TYPE_LIST);
		return loEntryTypeDetails;
	}

	/**
	 * Method to publish EntryTypelist of ContractBudget
	 * @param loChannel Channel
	 * @param aoContractId String
	 * @param aoIsPublished String
	 * @param aoBudgetTypeId String
	 * @param aoFiscalYear String
	 * @param aoModiFyUserId
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static void setPublishEntryType(Channel loChannel, String aoContractId, String aoIsPublished,
			String aoBudgetTypeId, String aoModiFyUserId, String aoFiscalYear) throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, aoContractId);
		loHashMap.put(HHSConstants.PUBLISHED, aoIsPublished);
		loHashMap.put(HHSConstants.BUDGET_TYPE_ID, aoBudgetTypeId);
		loHashMap.put(HHSConstants.MOD_BY_USER_ID, aoModiFyUserId);
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, aoFiscalYear);
		loChannel.setData(HHSConstants.AO_HASH_MAP, loHashMap);
	}

	/**
	 * 
	 * @param asWorkflowId
	 * @param asTaskSequence
	 * @param asEventType
	 * @param asTaskType
	 * @param asEntityId
	 * @param asEntityName
	 * @param asTaskLevel
	 * @param asUserType
	 * @param asContractId
	 * @param asUserId
	 * @param asAssignedTo
	 * @param asNextLevel
	 * @param asIncludeEndDate
	 * @return
	 */
	public static TaskAuditBean getTaskAuditBean(String asWorkflowId, String asTaskSequence, String asEventType,
			String asTaskType, String asEntityId, String asEntityName, String asTaskLevel, String asUserType,
			String asContractId, String asUserId, String asAssignedTo, String asNextLevel)
	{
		TaskAuditBean loTaskAuditBean = new TaskAuditBean();
		loTaskAuditBean.setWorkflowId(asWorkflowId);
		loTaskAuditBean.setTaskSequence(asTaskSequence);
		loTaskAuditBean.setEventType(asEventType);
		loTaskAuditBean.setTaskType(asTaskType);
		loTaskAuditBean.setEntityId(asEntityId);
		loTaskAuditBean.setEntityName(asEntityName);
		loTaskAuditBean.setTaskLevel(asTaskLevel);
		loTaskAuditBean.setUserType(asUserType);
		loTaskAuditBean.setContractId(asContractId);
		if (asUserType.equalsIgnoreCase(HHSConstants.PROVIDER))
		{
			loTaskAuditBean.setCreatedByStaffId(asUserId);
			loTaskAuditBean.setModifiedByStaffId(asUserId);
		}
		else
		{
			loTaskAuditBean.setCreatedBy(asUserId);
			loTaskAuditBean.setModifiedBy(asUserId);
		}
		loTaskAuditBean.setAssignedTo(asAssignedTo);
		loTaskAuditBean.setNextLevel(asNextLevel);
		return loTaskAuditBean;
	}

	/**
	 * 
	 * @param asUserId
	 * @return
	 */
	public static String getUserTypeFromUserId(String asUserId)
	{
		String lsUserType = null;
		if (null != asUserId)
		{
			if (asUserId.startsWith(HHSConstants.CITY_))
			{
				lsUserType = HHSConstants.CITY;
			}
			else if (asUserId.startsWith(HHSConstants.AGENCY_))
			{
				lsUserType = HHSConstants.AGENCY;
			}
			else if (asUserId.startsWith(HHSConstants.SYSTEM_USER))
			{
				lsUserType = HHSConstants.SYSTEM_USER;
			}
			//Added in R7 for auto approver use name
			else if (asUserId.startsWith(HHSR5Constants.AUTO_APPROVER_ID))
			{
				lsUserType = HHSConstants.AGENCY;
			}
			//End in R7
			else
			{
				lsUserType = HHSConstants.PROVIDER;
			}
		}
		return lsUserType;
	}

	/**
	 * This method is used to generate Audit List with Provider Comments for
	 * Line Item Tabs to be entered in Audit. This method is created for R4.
	 * @param aoTabLevelCommentsMapSubmit - Map Containing User Comments at time
	 *            of WF Submit
	 * @param aoTabLevelCommentsMapDB - Map Containing User Comments from
	 *            Database
	 * @param aoAuditList - Audit List
	 * @param aoHMWFRequiredProps - Hash Map containing Workflow details
	 * @return List of HHSAudit Bean
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static List<HhsAuditBean> generateFinalAuditListForTabLevelCommentsProvider(
			Map<String, String> aoTabLevelCommentsMapDB, List<HhsAuditBean> aoAuditList, Map aoHMWFRequiredProps,
			String asEntitType)
	{
		if (null != aoHMWFRequiredProps && !aoHMWFRequiredProps.isEmpty() && null != asEntitType
				&& !asEntitType.isEmpty() && null != aoTabLevelCommentsMapDB && !aoTabLevelCommentsMapDB.isEmpty())
		{
			Iterator loTabLevelCommentsDBMapItr = aoTabLevelCommentsMapDB.entrySet().iterator();
			while (loTabLevelCommentsDBMapItr.hasNext())
			{
				Map.Entry<String, String> loTabLevelCommentsMapTemp = (Entry<String, String>) loTabLevelCommentsDBMapItr
						.next();
				if (null != aoHMWFRequiredProps.get(HHSConstants.PROPERTY_PE_TASK_TYPE)
						&& (HHSConstants.WF_INVOICE_REVIEW.equalsIgnoreCase((String) aoHMWFRequiredProps
								.get(HHSConstants.PROPERTY_PE_TASK_TYPE)) || HHSConstants.WF_INVOICE_REVIEW
								.equalsIgnoreCase((String) aoHMWFRequiredProps.get(HHSConstants.WORKFLOW_NAME))))
				{
					aoAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
							loTabLevelCommentsMapTemp.getKey(), loTabLevelCommentsMapTemp.getValue(), asEntitType,
							(String) aoHMWFRequiredProps.get(ApplicationConstants.ENTITY_ID),
							(String) aoHMWFRequiredProps.get(HHSConstants.SUBMITTED_BY), HHSConstants.PROVIDER_AUDIT));
				}
				else
				{
					aoAuditList.add(HHSUtil.addAuditDataToChannel(P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT,
							loTabLevelCommentsMapTemp.getKey(), loTabLevelCommentsMapTemp.getValue(), asEntitType,
							(String) aoHMWFRequiredProps.get(HHSConstants.BUDGET_ID_WORKFLOW),
							(String) aoHMWFRequiredProps.get(HHSConstants.SUBMITTED_BY), HHSConstants.PROVIDER_AUDIT));
				}
			}
		}
		return aoAuditList;
	}

	/**
	 * This method is used to populate ProviderComments map for Comments
	 * retrieved from DB corresponding to Line Item Tabs. This method is created
	 * for R4.
	 * @param loUserCommentsMapDBList User Comments Map List
	 * @return Map Populated with formatted User OCmments corresponging to Tabs
	 */
	public static Map<String, String> populateProviderCommentsMapFromDB(
			List<Map<String, String>> loUserCommentsMapDBList)
	{
		Map<String, String> loCommentsMap = null;
		Iterator<Map<String, String>> loUserCommentsMapDBListItr = loUserCommentsMapDBList.iterator();
		loCommentsMap = new HashMap<String, String>();
		while (loUserCommentsMapDBListItr.hasNext())
		{
			Map<String, String> loTempCommentsMap = loUserCommentsMapDBListItr.next();
			loCommentsMap.put(loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID),
					loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS));
		}
		return loCommentsMap;
	}

	/**
	 * This method populated Agency Comments Audit List to be used for Audit.
	 * This method is created for R4.
	 * @param loUserCommentsMapDBList - List of Map containing User Comments
	 * @return List of HHSAudit Bean
	 */
	public static List<HhsAuditBean> populateAgencyCommentsAuditList(List<Map<String, String>> loUserCommentsMapDBList,
			List<HhsAuditBean> aoAuditList, TaskDetailsBean aoTaskDetailsBean)
	{
		Iterator<Map<String, String>> loUserCommentsMapDBListItr = loUserCommentsMapDBList.iterator();
		while (loUserCommentsMapDBListItr.hasNext())
		{
			Map<String, String> loTempCommentsMap = loUserCommentsMapDBListItr.next();
			if (null != loTempCommentsMap.get(HHSConstants.USER_INTERNAL_COMMENT)
					&& !loTempCommentsMap.get(HHSConstants.USER_INTERNAL_COMMENT).isEmpty())
			{
				aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AUDIT_TASK_INTERNAL_COMMENTS,
						loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID),
						loTempCommentsMap.get(HHSConstants.USER_INTERNAL_COMMENT), aoTaskDetailsBean.getEntityType(),
						aoTaskDetailsBean.getEntityId(), aoTaskDetailsBean.getAssignedTo(), HHSConstants.AGENCY_AUDIT));
			}
			if (null != loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS)
					&& !loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS).isEmpty())
			{
				aoAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.AGENCY_COMMENTS_DATA,
						loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_ENTITY_ID),
						loTempCommentsMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS),
						aoTaskDetailsBean.getEntityType(), aoTaskDetailsBean.getEntityId(),
						aoTaskDetailsBean.getAssignedTo(), HHSConstants.AGENCY_AUDIT));
			}
		}
		return aoAuditList;
	}

	/**
	 * This method generates Sub Budget ID from the Entity Type Tab Level Key.
	 * This method is created for R4.
	 * @param asTabLevelEntityType - Entity Type Tab Level Key for a Line Item
	 *            Tab
	 * @return Sub Budget Id
	 */
	public static String getSubBudgetIdForTabLevelOnCommentsSave(String asTabLevelEntityType)
	{
		String lsSubBudgetId = null;
		if (null != asTabLevelEntityType && !asTabLevelEntityType.isEmpty())
		{
			String[] lsTabNameTempSpliter = asTabLevelEntityType.split(HHSConstants.UNDERSCORE);
			lsSubBudgetId = lsTabNameTempSpliter[2];
		}
		return lsSubBudgetId;
	}

	/**
	 * This method generates list of Line Item Tabs to highlight corresponding
	 * to the list of audit data received from Agency Audit corresponding to a
	 * Sub Budget, Budget. This method is created for R4.
	 * @param aoAuditResultList - List containing Audit data for Tab Highlight
	 *            entries
	 * @return List of Tabs to Highlight
	 */
	public static List<Integer> generateLineItemTabsToHighlightMapProvider(List<String> aoAuditResultList)
	{
		List<Integer> loTabHighlightList = null;
		if (null != aoAuditResultList && !aoAuditResultList.isEmpty())
		{
			Iterator<String> loResultListItr = aoAuditResultList.iterator();
			loTabHighlightList = new ArrayList<Integer>();
			while (loResultListItr.hasNext())
			{
				String[] lsTabNameTempSpliter = loResultListItr.next().split(HHSConstants.UNDERSCORE);
				String lsTabName = lsTabNameTempSpliter[1];
				if (HHSConstants.TAB_HIGHLIGHT_IDENTIFIER_MAP.containsKey(lsTabName))
				{
					loTabHighlightList.add(HHSConstants.TAB_HIGHLIGHT_IDENTIFIER_MAP.get(lsTabName));
				}
			}
		}
		return loTabHighlightList;
	}

	/**
	 * This method is used to fetch Application Settings for Bulk Upload. This
	 * method is created for R4.
	 * @return HashMap containing Application Settings.
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public static HashMap<String, String> getApplicationSettingsBulk() throws ApplicationException
	{
		HashMap<String, String> loApplicationSettingMap = new HashMap<String, String>();
		Channel loChannel = new Channel();
		HHSTransactionManager.executeTransaction(loChannel, "applicationSettingDB_BulkNew");
		List<HashMap<String, String>> loApplicationSettingMapList = (List<HashMap<String, String>>) loChannel
				.getData("loAppliocationSettingMap");
		for (Iterator loIterator = loApplicationSettingMapList.iterator(); loIterator.hasNext();)
		{
			HashMap<String, String> loMap = (HashMap<String, String>) loIterator.next();
			loApplicationSettingMap.put(loMap.get("COMPONENT_NAME").concat("_").concat(loMap.get("SETTINGS_NAME")),
					loMap.get("SETTINGS_VALUE"));

		}
		return loApplicationSettingMap;
	}

	/**
	 * This method is used to return the fiscal year counter in string format
	 * method is created for R4.
	 * @return String fiscal year counter.
	 */
	public static String getFiscalYearCounter(int aiCorrectFyCounter)
	{
		String lsCorrectFyCounter = String.valueOf(aiCorrectFyCounter);
		if (lsCorrectFyCounter.length() > 2)
		{
			lsCorrectFyCounter = lsCorrectFyCounter.substring(1);
		}
		else if (lsCorrectFyCounter.length() == 1)
		{
			lsCorrectFyCounter = HHSConstants.ZERO + lsCorrectFyCounter;
		}

		return lsCorrectFyCounter;
	}
	
	/**This method changes the name of sub budget to rendered correctly on html page.
	 * Observation Changes Release 3.4.0. 
	 * @param loSubBudgetList sub budget list as input.
	 */
	public static void changeSubBudgetNameForHTMLView(List<CBGridBean> loSubBudgetList) throws ApplicationException{
		//Observation Changes Release 3.4.0.
		if(loSubBudgetList!=null)
		{
			for(CBGridBean loSubBudgetListBean : loSubBudgetList)
			{
				loSubBudgetListBean.setSubBudgetName(StringEscapeUtils.escapeHtml(loSubBudgetListBean.getSubBudgetName()));
			}
		}
	}

	/**
	 * This method added to check task Advance Payment Request Contract
	 * Configuration
	 * @param lsTaskType
	 * @return lsTaskType
	 */
	public static String setTaskType(String lsTaskType)
	{
		if (lsTaskType != null && lsTaskType.equalsIgnoreCase(HHSConstants.TASK_ADVANCE_REVIEW))
		{
			return (HHSConstants.ADVANCE_REQUEST_REVIEW);
		}
		else if (lsTaskType != null && lsTaskType.equalsIgnoreCase(HHSConstants.TASK_CONTRACT_CONFIGURATION))
		{
			return (HHSConstants.TASK_CONTRACT_CONFIGURATION_FULL);
		}
		else
		{
			return lsTaskType;
		}
	}
	
	//	Method added in R5
	/**
	 * This method changes particular word to wrap on hyphen underscore and slash
	 * @param asWord - word to be converted
	 * @return - converted word
	 */
	public static String convertToWrappingWord(String asWord){
		asWord = asWord.replaceAll("_", "&#x200b;_&#x200b;");
		asWord = asWord.replaceAll("-", "&#x200b;-&#x200b;");
		asWord = asWord.replaceAll("/", "&#x200b;/&#x200b;");
		return asWord;
	}
	
	/**
	 * The method is Added in Release 6. It is set the audit history for final
	 * approved status to channel when task is last level.
	 * @param aoTaskDetailsBean
	 * @param aoChannel
	 */
	@SuppressWarnings("unchecked")
	public static void setAuditForApprovedStatus(TaskDetailsBean aoTaskDetailsBean, Channel aoChannel)
			throws ApplicationException
	{
		StringBuffer loDataSb = new StringBuffer();
		try
		{
			List<HhsAuditBean> loAuditList = (List<HhsAuditBean>) aoChannel.getData(HHSConstants.LO_AUDIT_LIST);
			loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
					.append(HHSConstants.STATUS_PENDING_APPROVAL).append(HHSConstants.STR).append(HHSConstants._TO_)
					.append(HHSConstants.STR).append(ApplicationConstants.STATUS_APPROVED).append(HHSConstants.STR);
			loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
					loDataSb.toString(), aoTaskDetailsBean.getEntityName(), aoTaskDetailsBean.getEntityId(),
					aoTaskDetailsBean.getUserId(), HHSR5Constants.AGENCY_AUDIT));
			aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While saving audit data for approved status ", aoEx);

			LOG_OBJECT.Error("Error:: HHSUtil:" + "setAuditForApprovedStatus method - "
					+ "Error Occured While saving audit data for approved status ", aoEx);
			throw loAppEx;
		}
	}

	/**
	 * The method is Added in Release 6. It is set the audit history for cancel
	 * status to channel.
	 * @param aoTaskDetailsBean
	 * @param aoChannel
	 */
	@SuppressWarnings("unchecked")
	public static void setAuditForCancelStatus(TaskDetailsBean aoTaskDetailsBean, Channel aoChannel)
			throws ApplicationException
	{
		StringBuffer loDataSb = new StringBuffer();
		try
		{
			List<HhsAuditBean> loAuditList = (List<HhsAuditBean>) aoChannel.getData(HHSConstants.LO_AUDIT_LIST);
			loDataSb.append(HHSConstants.STATUS_CHANGED_FROM).append(HHSConstants.DOUBLE_QUOTE)
					.append(HHSConstants.STATUS_PENDING_APPROVAL).append(HHSConstants.STR).append(HHSConstants._TO_)
					.append(HHSConstants.STR).append(HHSR5Constants.STATUS_CANCELLED).append(HHSConstants.STR);
			loAuditList.add(HHSUtil.addAuditDataToChannel(HHSConstants.STATUS_CHANGE, HHSConstants.STATUS_CHANGE,
					loDataSb.toString(), aoTaskDetailsBean.getEntityName(), aoTaskDetailsBean.getEntityId(),
					aoTaskDetailsBean.getUserId(), HHSR5Constants.AGENCY_AUDIT));
			aoChannel.setData(HHSConstants.LO_AUDIT_LIST, loAuditList);
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While saving audit data for cancel status ", aoEx);

			LOG_OBJECT.Error("Error:: HHSUtil:" + "setAuditForCancelStatus method - "
					+ "Error Occured While saving audit data for cancel status ", aoEx);
			throw loAppEx;
		}
	}
	
	/**
	 * The method is Added in Release 7. It is used to convert the
	 * list of String to JQGrid drop down data.
	 * @param aoPositionBean
	 * @return lsFinalString
	 * @throws ApplicationException 
	 */
	public static String convertListToGridDropDown(List<OrganizationBean> aoPositionBean, ResourceRequest aoRequest) throws ApplicationException
	{
		//[Start]R7.12.0 QC9311 Minimize Debug
		//LOG_OBJECT.Debug("Entering into convertListToGridDropDown with parameter::: " +aoPositionBean);
		LOG_OBJECT.Debug("Entering into convertListToGridDropDown::: ");
		//[End]R7.12.0 QC9311 Minimize Debug
		Iterator<OrganizationBean> loListIterator = aoPositionBean.iterator();
		StringBuilder loPositionString = new StringBuilder();
		Map<String,OrganizationBean> organizationMap = new HashMap<String,OrganizationBean>();
		Integer i=1;
		while (loListIterator.hasNext())
		{
			OrganizationBean orgBean = loListIterator.next();
			organizationMap.put(i.toString(), orgBean);
			
			loPositionString.append(i++).append(":").append(orgBean.getMsOrgLegalName()).append(HHSConstants.DELIMITER_SEMICOLON);
		}
		aoRequest.getPortletSession().setAttribute(HHSR5Constants.ORGANIZATION_MAP, organizationMap,
				PortletSession.APPLICATION_SCOPE);
		loPositionString.deleteCharAt(loPositionString.length() - HHSConstants.INT_ONE);
		if(!aoPositionBean.get(0).getMsOrgLegalName().contains(HHSR5Constants.SELECT_PROVIDER)){
			loPositionString.insert(HHSConstants.INT_ZERO, HHSConstants.DROPDOWN_BLANK_FORMAT);
		}
		LOG_OBJECT.Debug("Exiting  convertListToGridDropDown method");
		return loPositionString.toString();
	}
    /** [Start] QC 9165 R 7.8.0 */
	/**
     * The method is Added in Release 7.8.0 to clculate the Authentication Signature for SAML
     * @param aoPositionBean
     * @return lsSignitureString
     * @throws ApplicationException 
     */
	//[Start]R9.3.0 qc 9636 Vuln 3. CWE 321 - Use of Hard-coded Cryptographic Key
	/* public String calculateAuthenticationSignature() throws ApplicationException{
	    try {
	        String secret = "secret";
	        String message = "Message";

	        Mac sha256_HMAC = Mac.getInstance(ApplicationConstants.AUTHENTICATION_SIGNATURE_ALGORITHM_HMAC_SHA256);
	        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), ApplicationConstants.AUTHENTICATION_SIGNATURE_ALGORITHM_HMAC_SHA256);
	        sha256_HMAC.init(secret_key);

	        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
	        LOG_OBJECT.Info(hash);

       }catch (Exception aoEx){
	            ApplicationException loAppEx = new ApplicationException(
	                    "Error Occured While saving audit data for cancel status ", aoEx);

	            LOG_OBJECT.Error("Error:: HHSUtil:" + "setAuditForCancelStatus method - "
	                    + "Error Occured While saving audit data for cancel status ", aoEx);
	            throw loAppEx;
       }

	    return null;
	}*/
	//[End]R9.3.0 qc 9636 Vuln 3. CWE 321 - Use of Hard-coded Cryptographic Key
	/**
     * The method is Added in Release 7.8.0 to clculate the Signature for SAML Web Services
     * @param message
     * @param key - NYC.ID Web Service Account Password
     * @return lsSignitureString
     * @throws Exception 
     */
	public static String getSignature(String value, String key) {
	    try {
	        // Get an hmac_sha256 key from the raw key bytes
	        byte[] keyBytes = key.getBytes();
	        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, ApplicationConstants.AUTHENTICATION_SIGNATURE_ALGORITHM_HMAC_SHA256);

	        // Get an hmac_sha256 Mac instance and initialize with the signing key
	        Mac mac = Mac.getInstance(ApplicationConstants.AUTHENTICATION_SIGNATURE_ALGORITHM_HMAC_SHA256);
	        mac.init(signingKey);

	        // Compute the hmac on input data bytes
	        byte[] rawHmac = mac.doFinal(value.getBytes());

	        // Convert raw bytes to Hex
	        byte[] hexBytes = new Hex().encode(rawHmac);

	        // Covert array of Hex bytes to a String
	        return new String(hexBytes, "UTF-8");
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
    /**
     * The method is Added in Release 7.8.0 to obtain URL of NYC.ID 2.0 profile 
     * @param aoPositionBean
     * @return lsSignitureString
     * @throws ApplicationException 
     */
    public static String obtainNYCIDurl() throws ApplicationException{
        return PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
                ApplicationConstants.SAML_NYC_ID_USER_PROFILE_PROP_INX);
    }
    
    /**
     * The method is Added in Release 7.8.0 to obtain URL of NYC.ID 2.0 profile 
     * @param aoPositionBean
     * @return lsSignitureString
     * @throws ApplicationException 
     */
    public static String obtainNYCIDurl(String aoPropName) throws ApplicationException{
        return PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, aoPropName);
    }
    
    /*[End] R7.8.0 replace to Aphach common*/
    
    public static String logoutSAMLcity(HttpServletRequest loReq )
    {
    	
    	String redirectUrl =     "";
        try {
            	//LOG_OBJECT.Debug("1109=====logoutSAMLcity======== ");
                redirectUrl =   HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX_CITY)  ;
                //LOG_OBJECT.Debug("=====redirectUrl ::  "+redirectUrl);
            } catch (Exception e) {
                LOG_OBJECT.Error("Exception occurs while getting "+ ApplicationConstants.SAML_NYC_ID_LOGOUT_PROP_INX_CITY + " from properties file!!!!!");
            }
  
        try {
        		redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, 
        				HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX_CITY) ) ;
        		//LOG_OBJECT.Debug("1131=====logoutSAMLcity====redirectUrl ::  "+redirectUrl);
        	
        		return redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, 
                    HHSUtil.obtainNYCIDurl(ApplicationConstants.SAML_NYC_ID_LOGOUT_STATIC_PROP_INX_CITY) ) ;
        } catch (ApplicationException e) {
            	return redirectUrl.replace(ApplicationConstants.REPLACE_ACCELERATOR_CONTRACT_PATH_STR, loReq.getContextPath() ) ;
        }
    }
    
    /** [Start] R 8.4.1 QC_9429 Bulk upload should accept special characters in contract titles. INC000002888250*/
    public static String convertSpecialCharactersHTMLGlobal(String lsContractTitle) {	
		
    	if(lsContractTitle==null || StringUtils.isEmpty(lsContractTitle)){
    		return "";
    	}
    	else{
			// Check for Smart Double Quotes
			lsContractTitle = lsContractTitle.replaceAll("[\u201C|\u201D|\u201E]", "\"");			
			// Check for Single Quotes
			lsContractTitle = lsContractTitle.replaceAll("[\u2018|\u2019|\u201A]", "\'");
			// Check for Bullet
			lsContractTitle = lsContractTitle.replaceAll("[\u2022|\u00B7|\uF0B7]", "*");
			// ellipsis
			lsContractTitle = lsContractTitle.replaceAll("[\u2026]", "...");
			// dashes
			lsContractTitle = lsContractTitle.replaceAll("[\u2013|\u2014]", "-");
			// circumflex
			lsContractTitle = lsContractTitle.replaceAll("[\u02C6]", "^");
			// spaces
			lsContractTitle = lsContractTitle.replaceAll("[\u02DC|\u00A0|\u0020]", " ");
			lsContractTitle = lsContractTitle.replaceAll("\t", "    ");
			
			lsContractTitle = lsContractTitle.replace(HHSConstants.STRING_BACKSLASH,
				HHSConstants.EMPTY_STRING);
			return lsContractTitle;
    	}
	}
    /** [End] R 8.4.1 QC_9429 Bulk upload should accept special characters in contract titles. INC000002888250*/
    
    /** [Start] R 8.4.1 QC_9506 Bulk Upload Template rejecting EPINS*/
    public static String replaceWordSpace(String input) {	
		
    	if(input==null || StringUtils.isEmpty(input)){
    		return "";
    	}
    	else{    		
    		input = input.replaceAll("\u02DC", " ");
    		input = input.replaceAll("\u00A0", " ");
    		input = input.replaceAll("\u0020", " ");
    		input = input.replaceAll("\t", "    ");			
			return input;
    	}
	}
    /** [End] R 8.4.1 QC_9506 Bulk Upload Template rejecting EPINS*/
    
    /* [Start] R9.4.0 QC9627  Budget term is incorrect*/
	/**
	 * This method calculates the Current Configurable Budget Start date in the String format of 'MM/DD/YYYY'
	 * <ul>
	 * <li>Returns Current FY(Fiscal Year) if Contract Start date is less than
	 * current FY else the Contract's first FY</li>
	 * </ul>
	 * 
	 * @param asContractStartDate contract start date
	 * @return asContractStartDate contract start date
	 * @throws ApplicationException Application Exception
	 */
	public static String getNewBudgetStartDate_MMDDYYYY(String asContractStartDate, String asCurrentFiscalYear)
			throws ApplicationException
	{
		String[] loStartDateArray = asContractStartDate.split(HHSConstants.FORWARD_SLASH);

		try
		{
			Integer liSrtYear = Integer.parseInt(loStartDateArray[2]);
			Integer liSrtDay = Integer.parseInt(loStartDateArray[1]);
			Integer liSrtMonth = Integer.parseInt(loStartDateArray[0]);
			
			Integer liCurrFY = Integer.parseInt(asCurrentFiscalYear);
			
			FiscalDate loBudgetSrtFy = new FiscalDate(liSrtYear, liSrtMonth, liSrtDay) ;

			if (liSrtMonth >= 7)
			{
				liSrtYear = liSrtYear + 1;
			}
			if (asCurrentFiscalYear.equals(liSrtYear.toString()))
			{
				return asContractStartDate;
			}
			else
			{
				Integer liStartYear = Integer.parseInt(asCurrentFiscalYear) - 1;
				return HHSConstants.FISCAL_YEAR_DATE + liStartYear.toString();
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Current Configurable Budget Start date", aoEx);
			LOG_OBJECT.Error("Error Occured While calculating the Current Configurable Budget Start date", loAppEx);
			throw loAppEx;
		}

	}
	
	/**
	 * This method calculates the Current Configurable Budget Start date in the String format of 'MM/DD/YYYY'
	 * <ul>
	 * <li>Returns Current FY(Fiscal Year) if Contract Start date is less than
	 * current FY else the Contract's first FY</li>
	 * </ul>
	 * 
	 * @param asContractEndDate contract end date
	 * @return asContractEndDate contract end date
	 * @throws ApplicationException Application Exception
	 */
	public static String getNewBudgetEndDate_MMDDYYYY(String asContractEndDate, String asCurrentFiscalYear)
			throws ApplicationException
	{
		String[] loEndDateArray = asContractEndDate.split(HHSConstants.FORWARD_SLASH);
        
		try
		{
			Integer liEndYear = Integer.parseInt(loEndDateArray[2]);
			Integer liEndDay = Integer.parseInt(loEndDateArray[1]);
			Integer liEndMonth = Integer.parseInt(loEndDateArray[0]);
			
			Integer liCurrFY = Integer.parseInt(asCurrentFiscalYear);
			
			FiscalDate loBudgetEndDate = new FiscalDate(liEndYear, liEndMonth, liEndDay) ;

			if (liEndMonth >= 7)
			{
				liEndYear = liEndYear + 1;
			}
			if (asCurrentFiscalYear.equals(liEndYear.toString()))
			{   
				return asContractEndDate;
			}
			else
			{   
				return HHSConstants.FISCAL_YEAR_END_DATE + asCurrentFiscalYear;
			}
		}
		// Catch the Exception thrown at any instance and wrap it into
		// application exception and throw
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured While calculating the Current Configurable Budget End date", aoEx);
			LOG_OBJECT.Error("Error Occured While calculating the Current Configurable Budget End date", loAppEx);
			throw loAppEx;
		}

	}

    /** [Start] R9.4.0 QC9627  Budget term is incorrect*/
		
	/** [Start] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/
    public static String sanitizeCarriageReturns(String input) {	
    	if(input==null || StringUtils.isEmpty(input)){
    		return "";
    	}
    	else{
    		return StringEscapeUtils.escapeJava(input);
    	}
    }
    /** [End] R9.4.0 qc_9635  --Vuln 2. CWE 113: Improper Neutralization of CRLF Sequences in HTTP Headers ('HTTP Response Splitting')*/

    /**
	 * check and return the allowed fully qualified class name
	 * @param fullyQualifiedClassName the fully qualified class name
	 * @return allowed fully qualified class name
	 * @throws ApplicationException
	 */
	/* [Start] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */
	public static String checkClassAccessControl(String fullyQualifiedClassName) throws ApplicationException {
			
		if (ApplicationConstants.GRID_BEAN_NAME_MAP.get(fullyQualifiedClassName)!=null ){
			return ApplicationConstants.GRID_BEAN_NAME_MAP.get(fullyQualifiedClassName);			
		}   		
		else{
			LOG_OBJECT.Error("method checkClassAccessControl, Class not valid:"  + fullyQualifiedClassName);
			throw new ApplicationException("Class not valid: "  + fullyQualifiedClassName);			
		}
	}
	public static String checkReportEmitterClassAccessControl(String emitterClass) throws ApplicationException {
				
		if (ApplicationConstants.REPORT_EMITTER_CLASS_NAME_MAP.get(emitterClass)!=null ){
			return  ApplicationConstants.REPORT_EMITTER_CLASS_NAME_MAP.get(emitterClass); 
		}   		
		else{
			LOG_OBJECT.Error("method checkReportEmitterClassAccessControl, Class not valid:"  + emitterClass);
			throw new ApplicationException("Class not valid: "  + emitterClass);
		}
	}
	/* [End] R9.4.0 qc_9634 -- Vuln 1: CWE 470 - Use of Externally Controlled Input to Select Classes or Code ('Unsafe Reflection')   */

	/* [Start] Start R9.5.0 qc_9670 Remote Command Execution in Log4J (CVE-2021-44228) Vulnerability from IBM App Scan report (part 2 -- filtering by hhsportal for paramters in html form through post method )  */
	public static String removeSpace(String aoString) {
		String lsString = aoString.replaceAll("\\s", "")
				.replaceAll("\\%20", "").replaceAll("\\+", "")
				.replaceAll("\\&#32;", "").replaceAll("\\&#x20", "");
		return lsString;
	}
	/* [End] Start R9.5.0 qc_9670 Remote Command Execution in Log4J (CVE-2021-44228) Vulnerability from IBM App Scan report (part 2 -- filtering by hhsportal for paramters in html form through post method )  */
}