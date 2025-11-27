package com.nyc.hhs.frameworks.grid;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.model.Document;

/**
 * This class will be used to create custom select element for
 * document grid view
 * 
 */
public class DocumentActionApplicationExtension implements DecoratorInterface
{
	/**
	 * This method is used to create drop down for document screen grid table. 
	 * 
	 * @param aoEachObject  
	 * 				Bean Name	
	 * @param aoCol
	 * 				Column name
	 * @param aoSeqNo
	 * 				Sequence Number
	 * @return control
	 */
	public String getControlForColumn(Object aoEachObject, Column aoCol, Integer aoSeqNo)
	{
		Document loFileUpload = (Document) aoEachObject;
		String lsReadOnly = loFileUpload.getReadOnly();
		Boolean loIsCeo = loFileUpload.isMbIsCeo();
		String lsReadOnlyCeo="";
		if(!loIsCeo){
			lsReadOnlyCeo = "disabled=disabled";
		}
		String lsHTMLScript = "";
		if (!loFileUpload.getSectionId().equalsIgnoreCase("servicessummary") && loFileUpload.getSectionId() != null)
		{
			if (lsReadOnly!=null && !lsReadOnly.equalsIgnoreCase("disabled=disabled"))
			{
				if ((null == loFileUpload.getDocumentId() || loFileUpload.getDocumentId().equalsIgnoreCase("")
					|| loFileUpload.getDocName().equalsIgnoreCase("doc id is present in document table but not in filenet")))
				{
					lsHTMLScript = "<select " + lsReadOnlyCeo + "name=action" + aoSeqNo
							+" class='documentterms' id='documentterms' style='width: 231px' " +
							"docId=\""+loFileUpload.getDocumentId()+
							"\" formName=\""+loFileUpload.getFormName()+
							"\" formVersion=\""+loFileUpload.getFormVersion()+ 
							"\" orgId=\""+loFileUpload.getOrganizationId()+ 
							"\" docType=\""+loFileUpload.getDocType()+ 
							"\"  appId=\""+loFileUpload.getApplicationId()+
							"\" docCat=\""+loFileUpload.getDocCategory()+ 
							"\" userId=\""+loFileUpload.getUserId()+ "\" " +
							"formId=\""+loFileUpload.getFormId()+ 
							"\" docName=\""+loFileUpload.getDocName()+ 
							"\" functionName=\"uploadDocument"+ 
							"\"><option value=I need to... >I need to...</option>" + "<option value=Upload Document>Upload Document</option>"
							+ "<option value=Select Document from Vault >Select Document from Vault</option>" + "</select>";
				}
				else
				{
					lsHTMLScript = "<select " +lsReadOnlyCeo+ " name=action2" + aoSeqNo
							+ " class='viewOrRemDoc' id='viewOrRemDoc' style='width: 231px' " +
							"docId=\""+loFileUpload.getDocumentId()+
							"\" formName=\""+loFileUpload.getFormName()+
							"\" formVersion=\""+loFileUpload.getFormVersion()+ 
							"\" orgId=\""+loFileUpload.getOrganizationId()+ 
							"\" docType=\""+loFileUpload.getDocType()+ 
							"\"  appId=\""+loFileUpload.getApplicationId()+
							"\" docCat=\""+loFileUpload.getDocCategory()+ 
							"\" userId=\""+loFileUpload.getUserId()+ "\" " +
							"formId=\""+loFileUpload.getFormId()+ 
							"\" docName=\""+loFileUpload.getDocName()+ 
							"\" functionName=\"openDocument"+ 
							"\"><option value=I need to... >I need to...</option>" + "<option value='View Document'>View Document</option>"
							+ "<option value='Remove Document'>Remove Document</option>" + "<option value='Upload Document'>Upload Document</option>"
							+ "<option value='Select Document from Vault' >Select Document from Vault</option>" + "</select>";
				}
		}
			else{
				if (loFileUpload.getMsOrgType()!=null && loFileUpload.getMsOrgType().equalsIgnoreCase(ApplicationConstants.CITY_ORG) &&
						(null == loFileUpload.getDocumentId() || loFileUpload.getDocumentId().equalsIgnoreCase("")) &&  !loFileUpload.isUserAccess()){
					lsHTMLScript = "<select "+ lsReadOnlyCeo + " name=action2" + aoSeqNo
					+" class='viewOrRemDoc' id='viewOrRemDoc' disabled=disabled style='width: 231px' " +
						"docId=\""+loFileUpload.getDocumentId()+
						"\" formName=\""+loFileUpload.getFormName()+
						"\" formVersion=\""+loFileUpload.getFormVersion()+ 
						"\" orgId=\""+loFileUpload.getOrganizationId()+ 
						"\" docType=\""+loFileUpload.getDocType()+ 
						"\"  appId=\""+loFileUpload.getApplicationId()+
						"\" docCat=\""+loFileUpload.getDocCategory()+ 
						"\" userId=\""+loFileUpload.getUserId()+ "\" " +
						"formId=\""+loFileUpload.getFormId()+ 
						"\" docName=\""+loFileUpload.getDocName()+ 
						"\" functionName=\"openDocument"+
						"\"><option value=I need to... >I need to...</option>" + "<option value='View Document'>View Document</option>"
					+ "<option value='View Document Information'>View Document Information</option>";
				}
				else{
				lsHTMLScript = "<select "+ lsReadOnlyCeo + " name=action2" + aoSeqNo
				+" class='viewOrRemDoc' id='viewOrRemDoc' style='width: 231px' " +
						"docId=\""+loFileUpload.getDocumentId()+
						"\" formName=\""+loFileUpload.getFormName()+
						"\" formVersion=\""+loFileUpload.getFormVersion()+ 
						"\" orgId=\""+loFileUpload.getOrganizationId()+ 
						"\" docType=\""+loFileUpload.getDocType()+ 
						"\"  appId=\""+loFileUpload.getApplicationId()+
						"\" docCat=\""+loFileUpload.getDocCategory()+ 
						"\" userId=\""+loFileUpload.getUserId()+ "\" " +
						"formId=\""+loFileUpload.getFormId()+ 
						"\" docName=\""+loFileUpload.getDocName()+ 
						"\" functionName=\"openDocument"+
						"\"><option value=I need to... >I need to...</option>" + "<option value='View Document'>View Document</option>"
				+ "<option value='View Document Information'>View Document Information</option>";
			}
			}
			}
		else
		{
			if (lsReadOnly!=null && !lsReadOnly.equalsIgnoreCase("disabled=disabled"))
			{
			if (null == loFileUpload.getDocumentId() || loFileUpload.getDocumentId().equalsIgnoreCase(""))
			{
				lsHTMLScript = "<select " + lsReadOnlyCeo+  " name=action" + aoSeqNo
						+ " class='documentterms' id='documentterms' style='width: 231px' " +
						"docId=\""+loFileUpload.getDocumentId()+
						"\" formName=\""+loFileUpload.getFormName()+
						"\" formVersion=\""+loFileUpload.getFormVersion()+ 
						"\" orgId=\""+loFileUpload.getOrganizationId()+ 
						"\" docType=\""+loFileUpload.getDocType()+ 
						"\"  appId=\""+loFileUpload.getApplicationId()+
						"\" docCat=\""+loFileUpload.getDocCategory()+ 
						"\" userId=\""+loFileUpload.getUserId()+ "\" " +
						"formId=\""+loFileUpload.getFormId()+ 
						"\" docName=\""+loFileUpload.getDocName()+ 
						"\" serviceAppId=\""+loFileUpload.getServiceAppID()+ 
						"\" sectionId=\""+loFileUpload.getSectionId()+
						"\" entityId=\""+loFileUpload.getMsEntityId()+ 
						"\" functionName=\"uploadDocumentServiceSummary"+
						"\"><option value=I need to... >I need to...</option>"
						+ "<option value=Upload Document>Upload Document</option>"
						+ "<option value=Select Document from Vault >Select Document from Vault</option>" + "</select>";
			}
			else
			{
				lsHTMLScript = "<select "+ lsReadOnlyCeo +  " name=action2" + aoSeqNo
						+ " class='viewOrRemDoc' id='viewOrRemDoc' style='width: 231px'  " +
						"docId=\""+loFileUpload.getDocumentId()+
						"\" formName=\""+loFileUpload.getFormName()+
						"\" formVersion=\""+loFileUpload.getFormVersion()+ 
						"\" orgId=\""+loFileUpload.getOrganizationId()+ 
						"\" docType=\""+loFileUpload.getDocType()+ 
						"\"  appId=\""+loFileUpload.getApplicationId()+
						"\" docCat=\""+loFileUpload.getDocCategory()+ 
						"\" userId=\""+loFileUpload.getUserId()+ "\" " +
						"formId=\""+loFileUpload.getFormId()+ 
						"\" docName=\""+loFileUpload.getDocName()+ 
						"\" serviceAppId=\""+loFileUpload.getServiceAppID()+ 
						"\" sectionId=\""+loFileUpload.getSectionId()+ 
						"\" entityId=\""+loFileUpload.getMsEntityId()+ 
						"\" functionName=\"openDocumentServiceSummary"+
						"\"><option value=I need to... >I need to...</option>" + "<option value='View Document'>View Document</option>"
						+ "<option value='Remove Document'>Remove Document</option>" + "<option value='Upload Document'>Upload Document</option>"
						+ "<option value='Select Document from Vault' >Select Document from Vault</option>" + "</select>";
			}
		}
			else{
				if (loFileUpload.getMsOrgType()!=null && loFileUpload.getMsOrgType().equalsIgnoreCase(ApplicationConstants.CITY_ORG) &&
						(null == loFileUpload.getDocumentId() || loFileUpload.getDocumentId().equalsIgnoreCase("")) && !loFileUpload.isUserAccess()){
					lsHTMLScript = "<select "+ lsReadOnlyCeo +  " name=action2" + aoSeqNo
					+ " class='viewOrRemDoc' disabled=disabled id='viewOrRemDoc' style='width: 231px'  " +
						"docId=\""+loFileUpload.getDocumentId()+
						"\" formName=\""+loFileUpload.getFormName()+
						"\" formVersion=\""+loFileUpload.getFormVersion()+ 
						"\" orgId=\""+loFileUpload.getOrganizationId()+ 
						"\" docType=\""+loFileUpload.getDocType()+ 
						"\"  appId=\""+loFileUpload.getApplicationId()+
						"\" docCat=\""+loFileUpload.getDocCategory()+ 
						"\" userId=\""+loFileUpload.getUserId()+ "\" " +
						"formId=\""+loFileUpload.getFormId()+ 
						"\" docName=\""+loFileUpload.getDocName()+ 
						"\" serviceAppId=\""+loFileUpload.getServiceAppID()+ 
						"\" sectionId=\""+loFileUpload.getSectionId()+ 
						"\" functionName=\"openDocumentServiceSummary"+
						"\"><option value=I need to... >I need to...</option>" + "<option value='View Document'>View Document</option>"
					+ "<option value='View Document Information'>View Document Information</option>";
				}
				else
				{
					lsHTMLScript = "<select "+ lsReadOnlyCeo +  " name=action2" + aoSeqNo
					+ " class='viewOrRemDoc' id='viewOrRemDoc' style='width: 231px'  " +
						"docId=\""+loFileUpload.getDocumentId()+
						"\" formName=\""+loFileUpload.getFormName()+
						"\" formVersion=\""+loFileUpload.getFormVersion()+ 
						"\" orgId=\""+loFileUpload.getOrganizationId()+ 
						"\" docType=\""+loFileUpload.getDocType()+ 
						"\"  appId=\""+loFileUpload.getApplicationId()+
						"\" docCat=\""+loFileUpload.getDocCategory()+ 
						"\" userId=\""+loFileUpload.getUserId()+ "\" " +
						"formId=\""+loFileUpload.getFormId()+ 
						"\" docName=\""+loFileUpload.getDocName()+ 
						"\" serviceAppId=\""+loFileUpload.getServiceAppID()+ 
						"\" sectionId=\""+loFileUpload.getSectionId()+ 
						"\" functionName=\"openDocumentServiceSummary"+
						"\"><option value=I need to... >I need to...</option>" + "<option value='View Document'>View Document</option>"
					+ "<option value='View Document Information'>View Document Information</option>";
				}
			}
		}
		return lsHTMLScript;
	}

	/**
	 * This Method is used to check all the checkboxes if present.
	 * @param aoCol 
	 * 			column name
	 * @return String
	 */
	public String getControlForHeading(Column aoCol)
	{

		String lsCtrl = "RESUME";
		return lsCtrl;
	}
}
