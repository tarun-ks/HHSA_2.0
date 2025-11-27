package com.nyc.hhs.service.db.services.notification;

import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class provides the functionality of fetching the content for sending the
 * mails to the users.
 * 
 */

public class MessageProcessor
{

	/**
	 * This method fetches the content for sending the emails.
	 * 
	 * @param aoHMParameters required email parameters
	 * @param aoEle - node element for asMsgId
	 * @param aoDocObject - dom object for notification xml
	 * @param asMsgId id of the message that is to be fetched
	 * @param asOrgType - Organization type
	 * @param asContentType the content type of the message
	 * @return List<String> the list of the content
	 * @throws ApplicationException
	 */

	public static List<String> getMessage(HashMap aoHMParameters, Element aoEle, Document aoDocObject, String asMsgId,
			String asOrgType, String asContentType) throws ApplicationException
	{
		List<String> loContentList = XMLUtil.getNotificationContent(aoEle, aoDocObject, asContentType, asOrgType,
				asMsgId);
		String lsPreparedMsg = "";
		if (null != aoHMParameters && null != asContentType && !asContentType.equals("groups"))
		{
			lsPreparedMsg = ReplaceParams.replaceWithParams(loContentList.get(0), aoHMParameters);
			loContentList.clear();
			if (null != lsPreparedMsg && !lsPreparedMsg.equals(""))
			{
				loContentList.add(lsPreparedMsg);
			}
		}
		return loContentList;
	}

	/**
	 * This method will get element node for input asMsg Id
	 * 
	 * @param aoDocObject dom object for notification xml
	 * @param asMsgId id of the message that is to be fetched
	 * @return node element for asMsgId
	 * @throws ApplicationException
	 */
	public static Element getNotificationNode(Document aoDocObject, String asMsgId) throws ApplicationException
	{
		String lsXPath = "//eventtypetemplate//notification[(@eventid='" + asMsgId + "')]";
		Element loEle = XMLUtil.getElement(lsXPath, aoDocObject);
		return loEle;
	}
}
