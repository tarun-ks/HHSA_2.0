package com.nyc.hhs.report.framework.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.util.XMLUtil;

/**
 * This class has utility function for reporting module
 * 
 */
public final class HHSReportUtil
{

	private HHSReportUtil()
	{

	}

	/**
	 * This method get the required configuration details.
	 * @param asOrgType organization type as input
	 * @param asReportId report id as input
	 * @param asTabName Tab name as input
	 * @return loConfigDetailMap - Map<String, Map<String, Object>>
	 * @throws ApplicationException Exception in case of failure
	 */
	public static Map<String, Map<String, Object>> getReportConfigDetails(String asOrgType, String asReportId,
			String asTabName) throws ApplicationException
	{
		Map<String, Object> loDataSet = new HashMap<String, Object>();
		Map<String, Map<String, Object>> loConfigDetailMap = new HashMap<String, Map<String, Object>>();
		Element loVariableElement = null;
		List<Element> loParamElementList = null;
		Map<String, Object> loReportRenderParam = new HashMap<String, Object>();
		Document loDocUserReportMapping = null;
		String lsXPathTop = null;
		Element loNodeChart = null;
		Document loDocReportChartMapping = null;
		String lsXPathDataGridDetails = null;
		try
		{
			String lsTabName = null;
			if (asTabName == null)
			{
				lsTabName = HHSConstants.EMPTY_STRING;
			}
			else
			{
				lsTabName = asTabName;
			}
			// for performance testing
			if (asReportId != null && asReportId.contains(HHSR5Constants.TYPE_UPDATED))
			{
				loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSR5Constants.REPORT_MAPPING_UPDATED);
			}
			else
			{
				loDocUserReportMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
						HHSR5Constants.REPORT_MAPPING);
			}
			lsXPathTop = "//user[(@name=\"" + asOrgType + "\")]//reportName[@id='" + asReportId + "']";
			loNodeChart = XMLUtil.getElement(lsXPathTop, loDocUserReportMapping);
			loDocReportChartMapping = (Document) BaseCacheManagerWeb.getInstance().getCacheObject(
					loNodeChart.getAttributeValue(HHSR5Constants.CONFIG_FILE));
			if (asReportId != null)
			{
			lsXPathDataGridDetails = "//charts//chart[@id='" + asReportId.concat(lsTabName) + "']";
			}
			loNodeChart = XMLUtil.getElement(lsXPathDataGridDetails, loDocReportChartMapping);
			if (loNodeChart != null)
			{
				List<Attribute> loConfigAttr = loNodeChart.getAttributes();
				for (Attribute loAttribute : loConfigAttr)
				{
					loReportRenderParam.put(loAttribute.getName(), loAttribute.getValue());
				}
				loConfigDetailMap.put(HHSR5Constants.RENDER_DETAILS, loReportRenderParam);
			}
			// set parameters in data set
			lsXPathDataGridDetails = "//charts//chart[@id='" + asReportId.concat(lsTabName)
					+ "']//report-params//param";
			loParamElementList = XMLUtil.getElementList(lsXPathDataGridDetails, loDocReportChartMapping);
			if (loParamElementList != null)
			{
				for (Element inputElt : loParamElementList)
				{
					loDataSet.put(inputElt.getAttributeValue(HHSR5Constants.TARGET), ApplicationConstants.EMPTY_STRING);
				}
				loConfigDetailMap.put(HHSR5Constants.INPUT, loDataSet);
			}
			lsXPathDataGridDetails = "//charts//chart[@id='" + asReportId.concat(lsTabName) + "']//report-variables";
			loVariableElement = XMLUtil.getElement(lsXPathDataGridDetails, loDocReportChartMapping);
			if (null != loVariableElement)
			{
				loDataSet = new HashMap<String, Object>();
				Element loReportParam = loVariableElement.getChild(HHSR5Constants.VARIABLE);
				loDataSet
						.put(loReportParam.getAttributeValue(HHSR5Constants.TARGET), ApplicationConstants.EMPTY_STRING);
				loConfigDetailMap.put(HHSR5Constants.VARIABLE, loDataSet);
			}
		}
		catch (ApplicationException loAppExc)
		{
			throw loAppExc;
		}
		catch (Exception loException)
		{
			throw new ApplicationException("Error Occured while reading Report Configuration", loException);
		}
		return loConfigDetailMap;
	}
}