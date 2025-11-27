package com.nyc.hhs.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;

public class ValidateXMLFile
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ApplicationException
	{
		ValidateXMLFile loObject = new ValidateXMLFile();
		loObject.validate();

	}

	public void validate() throws ApplicationException
	{
		Document loDocument = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
				HHSConstants.TRANSACTION_ELEMENT_PATH));
		String lsXPath = HHSConstants.DOUBLE_FORWARD_SLASHES + ApplicationConstants.TRANSACTION_ELEMENT;
		List<Element> loElementList = XMLUtil.getElementList(lsXPath, loDocument);
		Iterator loIt = loElementList.iterator();
		List<String> loFinalList = new ArrayList<String>();
		List<String> loDuplicateList = new ArrayList<String>();
		while (loIt.hasNext())
		{
			Element loElement = (Element) loIt.next();
			if (loFinalList.contains(loElement.getAttribute(HHSConstants.ID).getValue()))
			{
				loDuplicateList.add(loElement.getAttribute(HHSConstants.ID).getValue());
			}
			else
			{
				loFinalList.add(loElement.getAttribute(HHSConstants.ID).getValue());
			}

		}
	}
}
