<%@page import="com.nyc.hhs.model.Document"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<style type="text/css">
	.iconQuestion{
		  margin:18px 0 15px 15px;
		  *margin-top:0px;		  
	}
	h2{
		width: 81%;
	}
}
</style>
<script type="text/javascript" src="../js/versionhistory.js"></script>
	<div>
		<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S035_PAGE, request.getSession())){%>
			<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
			<div class="linkReturnVault"><a href="#" title="Return to Vault" onclick="javascript:backtoDocVault()">Return to Vault</a></div>
			<h2>Document Version History: <%=((Document)request.getAttribute("documentObj")).getDocName()%></h2>
			
			<form name="versionform" action="<portlet:actionURL/>" method ="post" >
				<jsp:useBean id="documentObj" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
				<input type="hidden" name="next_action" value="" />
				<div  class="tabularWrapper">
					<st:table objectName="documentVersionList"  cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows">
						<st:property headingName="Version" columnName="versionNo"
							align="left" size="15%"  />
						<st:property headingName="Modified" columnName="date"
							align="left" size="25%" />
						<st:property headingName="Last Modified By" columnName="lastModifiedBy"
							align="right" size="25%" />
						<st:property headingName="Action" columnName="actions" 
							align="right" size="35%" >
							<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentVersionActionExtension" />
						</st:property>
					</st:table>
				</div>
				<input type="hidden" name="hiddenDocCategory" value='<%=documentObj.getFilterDocCategory()%>' />	
				<input type="hidden" name="hiddenDocType" value='<%=documentObj.getFilterDocType()%>'/>
				<input type="hidden" name="hiddenFilterModifiedFrom" value='<%=documentObj.getFilterModifiedFrom()%>' />
				<input type="hidden" name="hiddenFilterModifiedTo" value='<%=documentObj.getFilterModifiedTo()%>' />
				<input type="hidden" name="hiddenFilterProviderId" value='<%=documentObj.getFilterProviderId()%>' />
				<input type="hidden" name="hiddenFilterNYCAgency" value="<%=documentObj.getFilterNYCAgency()%>"/>
				<input type="hidden" name="hiddenDocShareStatus" value='<%=documentObj.getDocSharedStatus()%>' />	
				<input type="hidden" name="hiddenSampleCategory" value='<%=documentObj.getFilterSampleCategory()%>'/>	
				<input type="hidden" name="hiddenSampleType" value='<%=documentObj.getFilterSampleType()%>'/>
			</form>
		<% } else {%>
			<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		<%} %>
	</div>
 	<div class="overlay"></div>
	<div class="alert-box-help">
		<div class="content">
			<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Document Vault - Help Documents</div>
		        <div id="helpPageDiv"></div>
			</div>
		</div>
		<a href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<div class="alert-box-contact">
		<div class="content">
			<div id="newTabs">
				<div id="contactDiv"></div>
			</div>
		</div>
	</div>
<script type="text/javascript">
// This will execute when any value is selected from view version history drop down
function openDocument(documentId, selectElement, documentName){
		var value = selectElement.selectedIndex;
		if(value == 1){
			viewDocument(documentId, documentName);
		}
		if(value == 2){
			pageGreyOut();
			var maxDocumentId = '<%=request.getAttribute("maxDocumentId")%>';
			document.versionform.action = document.versionform.action+'&removeNavigator=true&VersionProp=true&documentId='+documentId+'&next_action=viewDocumentInfo&maxDocumentId='+maxDocumentId;
			document.versionform.submit();
		}
}
</script>


