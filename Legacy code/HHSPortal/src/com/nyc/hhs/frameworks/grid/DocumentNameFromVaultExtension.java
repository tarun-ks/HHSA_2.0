package com.nyc.hhs.frameworks.grid;

import org.apache.commons.lang.StringUtils;

import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.DocumentsSelFromDocVault;

/**
 * This class generates an extension element in the Grid which displays the
 * documents selected by the user from Document Vault via Business Application
 * 
 */

public class DocumentNameFromVaultExtension implements DecoratorInterface
{
	/**
	 * Logger Object Declared for ContractBudgetService
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(DocumentNameFromVaultExtension.class);

	/**
	 * This method is used to create radio button on the grid which is displayed
	 * when a user select document from vault on the document screen and set the
	 * hidden values to be passed.
	 * @param aoEachObject an object of list to be displayed in grid
	 * @param aoCol a column object
	 * @param aiSeqNo an integer value of sequence number
	 * 
	 * @return a string value of html code formed
	 */

	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aiSeqNo)
	{
		DocumentsSelFromDocVault loDocumentBean = (DocumentsSelFromDocVault) aoEachObject;
		String lsControl = "";
		Integer liSeqNo = aiSeqNo + 1;
		LOG_OBJECT.Info("DocumentNameFromVaultExtension : Method : getControlForColumn, Seq No.	:" + liSeqNo);
		if ("msDocumentName".equals(aoCol.getColumnName()) && null != loDocumentBean.getMsDocumentName())
		{
			lsControl = "<input type=radio name=radio id=radio value="
					+ loDocumentBean.getMsDocumentId()
					+ "^^&&^^"
					+ loDocumentBean.getMsDocumentTitle()
					+ ">"
					+

					"<input type=hidden name=docType value=\""
					+ loDocumentBean.getMsDocumnetType()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=docCategory value=\""
					+ loDocumentBean.getMsDocumnetCategory()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=formName value=\""
					+ loDocumentBean.getMsFormName()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=formVersion value=\""
					+ loDocumentBean.getMsFormVersion()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=lastModifiedBy value=\""
					+ loDocumentBean.getMsLastModifiedBy()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=lastModifiedDate value=\""
					+ loDocumentBean.getMsLastModifiedDate()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=submissionBy value=\""
					+ loDocumentBean.getMsSubmittedBy()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=submissionDate value=\""
					+ loDocumentBean.getMsSubmittedDate()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=userId value=\""
					+ loDocumentBean.getMsUserId()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=docTitle value=\""
					+ loDocumentBean.getMsDocumentTitle()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=formId value=\""
					+ loDocumentBean.getMsFormId()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=serviceAppID value=\""
					+ loDocumentBean.getMsServiceAppId()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=filePath value=\""
					+ loDocumentBean.getFilePath()
					+ "\">"
					+ "</input>"
					+ "<input type=hidden name=sectionID value=\""
					+ loDocumentBean.getMsSectionId()
					+ "\">"
					+ "</input>"
					+ "<img width='18px' height='20px' style='margin-left: 3px;' src=\"../framework/skins/hhsa/images/file.png\">"
					+ "<a style='top:-5px;position: relative;left: 5px;' href=\"#\" onclick=\"javascript: viewDocument("
					+ "'" + loDocumentBean.getMsDocumentId() + "','" + loDocumentBean.getMsDocumentName() + "');\">" + loDocumentBean.getMsDocumentName() + "</a>" +

					"</input>";
		}
		// Added for Release 5 - getting File path of document
		if ("filePath".equalsIgnoreCase(aoCol.getColumnName()) && StringUtils.isNotBlank(loDocumentBean.getFilePath()))
		{
			String lsPath = "";
			String lsFilePath = loDocumentBean.getFilePath();
			if (StringUtils.isNotBlank(lsFilePath))
			{
				lsPath = lsFilePath.substring(lsFilePath.indexOf("/" + HHSR5Constants.DOCUMENT_VAULT),
						lsFilePath.length());
			}
			// Fixed Defect #7609
			lsControl = "<label style='float:left' title=\"" + lsPath.replace("/", "\\") + "\" value='"
					+ lsFilePath.substring(lsFilePath.lastIndexOf("/") + 1) + "'>"
					+ lsFilePath.substring(lsFilePath.lastIndexOf("/") + 1) + "</label>";
		}
		return lsControl;
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

		String lsSrc = "RESUME";
		return lsSrc;
	}

}
