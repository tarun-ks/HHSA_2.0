package com.nyc.hhs.frameworks.grid;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import com.ibm.icu.text.DecimalFormat;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ReportBean;

/**
 * Added for release 5 This class is used to generate html code for a particular
 * column of table depending upon the input column name
 */
public class DetailedReportExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException when any exception occurred wrap it into
	 *             application exception.
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		ReportBean loReportBean = (ReportBean) aoEachObject;
		StringBuilder lsControl = new StringBuilder();
		Object loPropertValue = null;
		try
		{
			final BeanInfo loBeanInfo = Introspector.getBeanInfo(loReportBean.getClass());
			for (final PropertyDescriptor property : loBeanInfo.getPropertyDescriptors())
			{
				String loPropertyName = property.getName().toString();
				if (aoCol.getColumnName().equalsIgnoreCase(loPropertyName))
				{
					loPropertValue = (Object) property.getReadMethod().invoke(loReportBean);
					break;
				}
				else
				{
					continue;
				}
			}
			if (loPropertValue != null)
			{
				if (HHSR5Constants.GET_EXTENTION_INFO.contains(aoCol.getColumnName()))
				{
					if (loPropertValue.toString().contains("-")
							&& aoCol.getColumnName().equalsIgnoreCase("pendingFyAmendmentAmount"))
					{
						String lsConvertedString = new DecimalFormat("#,###.##").format(Double
								.parseDouble(loPropertValue.toString()));
						lsControl.append("<label class='' style=' text-align:right; float:right'>(").append(
								lsConvertedString.replace("-", "") + ")" + "</label>");
					}
					else
					{
						lsControl.append("<label class='tableContractValue' style=' text-align:right; float:right'>"
								+ loPropertValue.toString() + "</label>");
					}
				}
				else
				{
					lsControl.append(loPropertValue.toString());
				}
			}

		}
		catch (Exception aoExp)
		{
			throw new ApplicationException("Exception occuured while setting grid variable", aoExp);
		}

		return lsControl.toString();
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{
		return HHSConstants.RESUME;
	}
}
