package com.nyc.hhs.frameworks.grid;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.grid.Column;
import com.nyc.hhs.frameworks.grid.DecoratorInterface;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.util.XMLUtil;

/**
 * Added in Release 6: This class is used to create custom select element for
 * returned payment check grid view. Values in Action column on returned payment
 * list page for Accelerator/Provider are populated using it as a decorator
 * class in grid.
 * 
 */
public class ReturnedPaymentProviderCityActionExtension implements DecoratorInterface
{

	@Override
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo) throws ApplicationException
	{

		ReturnedPayment loReturnedPayment = (ReturnedPayment) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		if ("action".equalsIgnoreCase(aoCol.getColumnName()))
		{
			String lsStatusId = loReturnedPayment.getCheckStatus();

			Document loDoc = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					HHSR5Constants.RETURNED_PAYMENT_MAPPING_FILE);
			lsControl.append("<select name=action" + aoSeqNo + " class='contractAmend' id='action" + aoSeqNo
					+ "' style='width: 150px' onchange=\"onSelectChange(this);\"" + "><option title="
					+ "'I need to...'" + "value=I need to... >I need to...</option>");

			String lsXPathTop = "//page[(@name=\"returnedPaymentProviderCity\")]//status";
			List<Element> loNodeList = XMLUtil.getElementList(lsXPathTop, loDoc);
			for (Element loNode : loNodeList)
			{
				if (loNode.getAttribute("name").getValue().equals(lsStatusId))
				{
					lsControl = getControle(lsControl, loNode, loReturnedPayment);
					break;
				}
			}
			lsControl.append("</select>");
		}
		else if ("checkAmount".equalsIgnoreCase(aoCol.getColumnName()))
		{ //Fix for defect of returned payment alignment grid
			lsControl = lsControl.append("<label class='tableCheckAmountValue' style='text-align:right; float:right; margin-right: 23%'>"
					+ loReturnedPayment.getCheckAmount() + "</label>");
		}
		return lsControl.toString();
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * 
	 * @param aoCol column name
	 * @return lsControl - String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsControl = HHSConstants.RESUME;
		return lsControl;
	}

	/**
	 * This Method is used to build controle from node.
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoNode - Element object
	 * @throws ApplicationException
	 */
	private void setNodeValue(StringBuilder asControl, Element aoNode, String asId)
	{
		asControl.append("<option title=" + "'" + aoNode.getValue() + "'" + " value=\"" + aoNode.getValue() + "-" + asId
				+ "\">" + aoNode.getValue() + "</option>");
	}

	/**
	 * This Method is used to build controle from node.<br>
	 * 
	 * @param asControl - StringBuilder object
	 * @param aoNode - Element object
	 * @param aoReturnedPayment - ReturnedPayment Object
	 * @return asControl - StringBuilder Object
	 * @throws ApplicationException - ApplicationException Object
	 * 
	 */
	@SuppressWarnings("unchecked")
	private StringBuilder getControle(StringBuilder asControl, Element aoNode, ReturnedPayment aoReturnedPayment)
			throws ApplicationException
	{
		List<Element> loElements = (List<Element>) aoNode.getChildren();
		for (Element loNode : loElements)
		{
			setNodeValue(asControl, loNode, aoReturnedPayment.getReturnedPaymentId());
		}
		return asControl;
	}
}
