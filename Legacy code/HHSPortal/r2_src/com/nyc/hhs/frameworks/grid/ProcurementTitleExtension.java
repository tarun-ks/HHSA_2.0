/**
 * 
 */
package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.Procurement;

/**
 * This is a extension class for Procurement title.
 */
public class ProcurementTitleExtension implements DecoratorInterface
{
	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * <ul>
	 * <li>
	 * This method fetches the provider's status,Epin,title id from the
	 * Procurement Bean and based on these values it will generate HTML code.</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * 
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * @return a string value of html code formed
	 * @throws ApplicationException
	 * 
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo) throws ApplicationException
	{
		Procurement loProcurementBean = (Procurement) aoEachObject;
		String lsControl = HHSConstants.EMPTY_STRING;
		String lsFavoriteChecked = HHSConstants.EMPTY_STRING;
		if (HHSConstants.PROCUREMENT_TITLE.equals(aoCol.getColumnName())
				&& null != loProcurementBean.getProcurementTitle())
		{
			// R 7.2.0 QC 8914 For Observer, only Procurements in certain status are clickable
			// Added the top IF Condition for Observer Role
			if (ApplicationConstants.ROLE_OBSERVER.equalsIgnoreCase(loProcurementBean.getRoleCurrent()) )
			{
				if (isTitleClickable(loProcurementBean)) {
					lsControl = "<a href=\"javascript: viewProcurementSummary(" + "'"
					+ loProcurementBean.getProcurementId() + "');\">" + loProcurementBean.getProcurementTitle()
					+ "</a>";
				}
				else {
					lsControl =  loProcurementBean.getProcurementTitle();
				}
			}
			else if (loProcurementBean.getProviderStatus() == null || loProcurementBean.getProviderStatus().isEmpty())
			{
				lsControl = "<a href=\"javascript: viewProcurementSummary(" + "'"
						+ loProcurementBean.getProcurementId() + "');\">" + loProcurementBean.getProcurementTitle()
						+ "</a>";
			}
			else
			{
				lsControl = "<a href=\"#\" class='procTitleLink' onclick=\"javascript: viewProcurementSummaryProvider("
						+ "'" + loProcurementBean.getProcurementId() + "');\">"
						+ loProcurementBean.getProcurementTitle() + "</a>";
			}
		}
		else if (HHSConstants.PROC_EPIN.equals(aoCol.getColumnName()))
		{
			if (null == loProcurementBean.getProcurementEpin()
					|| loProcurementBean.getProcurementEpin().equals(HHSConstants.EMPTY_STRING))
			{
				lsControl = HHSConstants.PENDING;
			}
			else
			{
				lsControl = loProcurementBean.getProcurementEpin();
			}
		}
		else if (HHSConstants.PROCUREMENT_FAVORITE.equals(aoCol.getColumnName()))
		{

			if (loProcurementBean.getIsFavorite() != null && !loProcurementBean.getIsFavorite().equals("-1"))
			{
				lsFavoriteChecked = "checked";
			}
			// Start Release 5 user notification
			if (loProcurementBean.isUserAccess())
			{
				lsControl = "<input type='checkbox' name='favoriteIdsChckBox' id='favoriteIdsChckBox' class='favoriteIds' value='"
						+ loProcurementBean.getProcurementId() + "' " + lsFavoriteChecked + "/>";
			}
			else
			{
				lsControl = "<input type='checkbox' name='favoriteIdsChckBox' id='favoriteIdsChckBox' disabled class='favoriteIds' value='"
						+ loProcurementBean.getProcurementId() + "' " + lsFavoriteChecked + "/>";
			}
			// End Release 5 user notification
		}
		return lsControl;
	}

	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name * Updated Method in R4
	 * 
	 * @param aoCol a column object
	 * 
	 * @return a string value of html code formed
	 * 
	 */
	public String getControlForHeading(Column aoCol)
	{
		String lsControl = HHSConstants.RESUME;
		if (HHSConstants.PROCUREMENT_FAVORITE.equals(aoCol.getColumnName()))
		{
			lsControl = aoCol.getHeadingName();
		}
		return lsControl;
	}
	
	/**
	 * R 7.2.0 QC 8914
	 * Procurement title is not clickable if status is 'Planned', 'Released', 'Proposals Received', 'Evaluations Complete'
	 * @param procurement
	 * @return
	 */
	private boolean isTitleClickable (Procurement procurement) {
		if (HHSConstants.PROC_STATUS_PLANNED.equalsIgnoreCase(procurement.getProcurementStatus()) 
				|| HHSConstants.PROCUREMENT_STATUS_RELEASED.equalsIgnoreCase(procurement.getProcurementStatus())
				|| HHSConstants.PROC_PROPOSALS_RECEIVED.equalsIgnoreCase(procurement.getProcurementStatus())
				|| HHSConstants.EVALUATIONS_COMPLETE.equalsIgnoreCase(procurement.getProcurementStatus()))
			return false;
		else
			return true;
	}
	
}
