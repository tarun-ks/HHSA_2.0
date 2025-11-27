package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.BusinessApplicationSummary;

/**
 * This class generates an extension that will create links corresponding 
 * to the documents.
 *
 */

public class BassicApplicationSummaryNameExtension implements DecoratorInterface {

	/**
	 * This method will generate html code for a particular column of table
	 * depending upon the input column name
	 * 
	 * @param aoEachObject
	 *            an object of list to be displayed in grid
	 * @param aoCol
	 *            a column object
	 * @param aiSeqNo
	 *            an integer value of sequence number
	 * @return a string value of html code formed
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol,
			Integer aoSeqNo) {
		BusinessApplicationSummary loBuisinessAppSummary = (BusinessApplicationSummary) aoEachObject; 
		String lsColName=aoCol.getColumnName();
		String lsControl=null;

		if(lsColName.equalsIgnoreCase("msQuestionsDocumentName")){
			

				if(null != loBuisinessAppSummary.getMsDocumentType()){
					if((loBuisinessAppSummary.getMsDocumentType().equalsIgnoreCase(ApplicationConstants.CEO_NAME)|| 
							loBuisinessAppSummary.getMsDocumentType().equalsIgnoreCase(ApplicationConstants.CFO_NAME))
						&& null == loBuisinessAppSummary.getMsDocumentID()){
						lsControl =  loBuisinessAppSummary.getMsQuestionsDocumentName();
					}
					else{
					lsControl  = "<a style='text-transform: capitalize' href=\"#\" title='"+loBuisinessAppSummary.getMsQuestionsDocumentName()+"' onclick=\"viewDocument('"+loBuisinessAppSummary.getMsDocumentID()+"','" +loBuisinessAppSummary.getMsQuestionsDocumentName()+"');\">"+loBuisinessAppSummary.getMsQuestionsDocumentName()+"</a>";	
						}
				}
				else if(null != loBuisinessAppSummary.getMsQuestionsDocumentName() && loBuisinessAppSummary.getMsQuestionsDocumentName().equalsIgnoreCase(ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION)){
					lsControl  = "<a style='text-transform: capitalize' href=\"#\" title='"+loBuisinessAppSummary.getMsSectionID()+" "+loBuisinessAppSummary.getMsQuestionsDocumentName()+"' onclick=\"openSummaryLink('"+loBuisinessAppSummary.getMsSectionID()
					+"','"+loBuisinessAppSummary.getMsQuestionsDocumentName()
					+"','"+"showquestion"
					+"');\">"
					+loBuisinessAppSummary.getMsSectionID()+" "+loBuisinessAppSummary.getMsQuestionsDocumentName()+"</a>";
				}
				else if((null == loBuisinessAppSummary.getMsDocumentType() || ("").equalsIgnoreCase(loBuisinessAppSummary.getMsDocumentType()))&&
						loBuisinessAppSummary.getMsQuestionsDocumentName().equalsIgnoreCase("documentlist")){
					lsControl  = "<a style='text-transform: capitalize' title='"+loBuisinessAppSummary.getMsQuestionsDocumentName()+"' href=\"#\" onclick=\"openSummaryLink('"+loBuisinessAppSummary.getMsSectionID()
					+"','"+loBuisinessAppSummary.getMsQuestionsDocumentName()
					+"','"+"open"
					+"');\">"
					+loBuisinessAppSummary.getMsSectionID()+" "+"Documents"+"</a>";
				}
				else if((null == loBuisinessAppSummary.getMsDocumentType() || ("").equalsIgnoreCase(loBuisinessAppSummary.getMsDocumentType())) && !"documentlist".equalsIgnoreCase(loBuisinessAppSummary.getMsQuestionsDocumentName())){
					lsControl  = "<a style='text-transform: capitalize' title='"+loBuisinessAppSummary.getMsQuestionsDocumentName()+"' href=\"#\" onclick=\"openSummaryLink('"+loBuisinessAppSummary.getMsSectionID()
					+"','"+loBuisinessAppSummary.getMsQuestionsDocumentName()
					+"','"+"open"
					+"');\">"
					+loBuisinessAppSummary.getMsQuestionsDocumentName()+"</a>";
				}
			
		}

		return lsControl;
	}
	
	/**
	 * This method will generate html code for a particular column header of
	 * table depending upon the input column name
	 * 
	 * @param aoCol
	 *            a column object
	 * 
	 * @return a string value of html code formed
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsDataControl = "RESUME"; 
		return lsDataControl;
	}


}
