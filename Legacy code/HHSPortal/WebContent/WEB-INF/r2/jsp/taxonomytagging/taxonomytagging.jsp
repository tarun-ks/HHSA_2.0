<%-- This JSP is for Taxonomy tagging base screen --%>
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/taxonomytagging.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/simpletreemenu.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>

<%-- Code updated for R4 Starts --%>
<script type="text/javascript">
	var linkageElementId ="";
	var linkageBranchValue = "";
	var serviceElementId = "";
	var modifiers="";
</script>
<style>
#headerTableTaxonomy, #serviceFunctionList{
	width: 98%;
	margin-bottom: 10px;
	margin-top: 10px;
}
.tabularContainer{
	border: 0px;
}
</style><%-- Code updated for R4 Ends --%>
<portlet:defineObjects/>

<%-- BEGIN QC 6523 3.7.0 --%>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>

<portlet:resourceURL var="getEpinListResourceUrl" id="getEpinListResourceUrl" escapeXml="false"></portlet:resourceURL>
<input type = 'hidden' value='${getEpinListResourceUrl}' id='getEpinListResourceUrl'/>
<%-- END QC 6523 3.7.0 --%>

<%-- Code updated for R4 Starts --%>
<%--resourceURL for typeheads--%>
<portlet:resourceURL var="fetchTypeAheadNameList" id="fetchTypeAheadNameList" escapeXml="false"/>
<%--resourceURL for edit taxonomy tags in bulk--%>
<portlet:resourceURL var="editTaxonomyTagsInBulkUrl" id="editTaxonomyTagsInBulkUrl" escapeXml="false"/>
<%-- Code updated for R4 Ends --%>
<%--resourceURL for edit taxonomy tags--%>
<portlet:resourceURL var="editTaxonomyTagsUrl" id="editTaxonomyTagsUrl" escapeXml="false"/>
<%--resourceURL for adding new taxonomy tags--%>
<portlet:resourceURL var="addNewTaxonomyTagsUrl" id="addNewTaxonomyTagsUrl" escapeXml="false"/>
<%--resourceURL for saving taxonomy tags--%>
<portlet:resourceURL var="saveTaxonomyTagUrl" id="saveTaxonomyTagUrl" escapeXml="false"/>
<%--resourceURL for deleting taxonomy tags--%>
<portlet:resourceURL var="deleteTaxonomyTagUrl" id="deleteTaxonomyTagUrl" escapeXml="false"/>
<portlet:resourceURL var="deleteTaxonomyTagUrlInBulk" id="deleteTaxonomyTagUrlInBulk" escapeXml="false"/>
<portlet:resourceURL var="removeAllTaxonomyTagUrlPopUpInBulk" id="removeAllTaxonomyTagUrlPopUpInBulk" escapeXml="false"/>
<%--actionURL for sort/paginate/filtering the grid--%>
<portlet:actionURL var="taxonomyTreeUrl" escapeXml="false"/>
<%--resourceURL to view organization summary--%>
<portlet:resourceURL var='organizationSummary' id="organizationSummary" escapeXml='false'>
	<portlet:param name="jspPath" value="procurement/"/>
</portlet:resourceURL>
<input type = "hidden" value='${fetchTypeAheadNameList}' id="hiddenFetchTypeAheadNameList" />
<input type = "hidden" value='${editTaxonomyTagsInBulkUrl}' id="hiddenEditTaxonomyTaggingInBulk" />
<input type = "hidden" value='${editTaxonomyTagsUrl}' id="hiddenEditTaxonomyTagging" />
<input type = "hidden" value='${addNewTaxonomyTagsUrl}' id="hiddenAddNewTaxonomyTag" />
<input type = "hidden" value='${saveTaxonomyTagUrl}' id="hiddenSaveTaxonomyTag" />
<input type = "hidden" value='${deleteTaxonomyTagUrl}' id="hiddenDeleteTaxonomyTag" />
<input type = "hidden" value='${deleteTaxonomyTagUrlInBulk}' id="hiddenDeleteTaxonomyTagInBulk" />
<input type = "hidden" value='${removeAllTaxonomyTagUrlPopUpInBulk}' id="hiddenRemoveAllTaxonomyTagPopUpInBulk" />
<input type = "hidden" value='${removeAllTaxonomyTagUrlInBulk}' id="hiddenRemoveAllTaxonomyTagUrlInBulk" />
<input type = "hidden" id="hiddenViewProposalSummary" value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_taxonomytagging&removeMenu=adas&submit_action=viewProposalSummary&IsProcDocsVisible=true&jspPath=procurement%2Fevaluation%2F&_nfls=true&_urlType=action&_windowLabel=portletInstance_33"/>
<input type = "hidden" value='${organizationSummary}' id="hiddenOrganizationSummary" />
<form:form id="taxonomytaggingform" name="taxonomytaggingform" action="${taxonomyTreeUrl}" method ="post" commandName="taxonomyTaggingBean">
<input type="hidden" id="submit_action" name="submit_action" value=""/>
<input type="hidden" id="procurementSummaryURL" value="<render:standalonePortletUrl portletUri='/r2/portlet/procurement/procurement.portlet'><render:param name='topLevelFromRequest' value='ProcurementInformation' /><render:param name='midLevelFromRequest' value='ProcurementSummary' /><render:param name='render_action' value='viewProcurement' /><render:param name='hideExitProcurement' value='true' /></render:standalonePortletUrl>"	/>
<h2 class='autoWidth'>Taxonomy Tagging</h2>
<div class='linkReturnValut' style='margin-top:8px; margin-right:8px'>
	<a href="javascript:void(0);" onclick="returnToMain();">Return to Maintenance Homepage</a>
</div>

<div class='hr'></div>
<c:if test="${transactionStatus eq 'failed'}">
   <div id="transactionStatusDiv" class="failed" style="display:block" >${transactionMessage}</div>
</c:if><%-- Code updated for R4 Starts --%>
<%-- check for fail or success message --%>
	<c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" 
			onclick="showMe('messagediv', this)">
		</div>
	</c:if><%-- Code updated for R4 Ends --%>
<%--Filter and Reassign section starts --%>
<div class="taskButtons">
    <span><input type="button" value="Filter Items" class="filterDocument"  onclick="setVisibility('documentValuePop', 'inline');" /></span>
     <span class=''>&nbsp;
				<input type="button" value="Add New Tag(s) in Bulk" disabled="disabled" id="addNewTagsBulk" class="add marginReset" />
	</span>
	 <span class=''>&nbsp;
				<input type="button" value="Remove All Tags in Bulk" disabled="disabled" id="removeAllTagsBulk" class="remove marginReset" />
	</span>
	<span>Proposals &amp; Contracts: <label>${records}</label></span>
	
    <%-- Popup for Filter Task Starts --%>
    <div id="documentValuePop" class='formcontainerFinance dateValidateWrapper' style='width:460px;'>
        <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
        <div class='row'>
        <%-- Code updated for R4 Starts --%>
        <span class='label'>Results displayed per page:</span>
       	<span><form:radiobutton path="selectedTaxonomy"  value="20" cssStyle="width:30px"/>20
		<form:radiobutton path="selectedTaxonomy"  value="50"  cssStyle="width:30px"/>50
		<form:radiobutton path="selectedTaxonomy"  value="100" cssStyle="width:30px"/>100</span>
		<span class="error"></span>
	
	 	</div>
        <div class='row'>
        <%-- Code updated for R4 Ends --%>
            <span class='label'>Proposal Title:</span>
            <span class='formfield'>
                 <form:input path="proposalTitle" cssClass="widthFull" id="proposalTitle" maxlength="60" title="Enter at least 5 characters of the Proposal Title" />
            </span>
            <span class="error"></span>
        </div>
        <div class='row'>
            <span class='label'>Provider Name:</span>
            <span class='formfield'>
            	<form:input path="providerName" cssClass="widthFull" id="providerName" maxlength="100" title="Enter at least 3 characters of the Provider Name and select from the suggestions listed"/>
            </span>
            <span class="error"></span>
        </div>
        <div class='row'>
            <span class='label'>Procurement / Contract Title:</span>
            <span class='formfield'>				
				<form:input path="procurementContractTitle" cssClass="widthFull" id="procurementContractTitle" maxlength="120" title="Enter at least 3 characters of the Procurement/Contract Title and select from the suggestions listed" />
				<form:hidden path="procurementId" id="procurementId"/>
            </span>
            <span class="error"></span>
        </div>
        <div class='row'>
        <%-- Code updated for R4 Starts --%>
            <span class='label'>Competition Pool:</span>
            <span class='formfield'>				
				<form:input path="competitionPoolTitle" cssClass="widthFull" id="competitionPoolTitle" maxlength="120" title="Enter at least 3 characters of the Competition Pool and select from the suggestions listed" />
				<form:hidden path="competitionPoolId" id="competitionPoolId"/>
            </span>
            <span class="error"></span>
        </div>
        <%--BEGINS  QC 6523  Release 3.7.0--%>
        <div class='row'>
	            <span class='label'>Agency:</span>
	            <span class='formfield'>
	                  <form:select path="contractAgencyName" cssClass="widthFull" id="agency">
	                  	<form:option id="All NYC Agencies" value="">All NYC Agencies</form:option>
	                  	<c:forEach items="${agencyDetails}" var="agencyDetail">
	                  		<form:option title="${agencyDetail['AGENCY_NAME']}"  value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</form:option>
		                </c:forEach>
	                  </form:select>
	            </span>
	    </div>
        <div class='row'>
        <c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
            <span class='label'>Program Name:</span>
            <span class='formfield'>
                  <form:select path="programName" cssClass="widthFull" id="programName">
                  		<form:option value=""/>
                  		<c:forEach items="${programNameList}" var="programObject">
							<form:option title='${programObject.programName}' value="${programObject.programId}">${programObject.programName}</form:option>
						</c:forEach>
					</form:select>
            </span>
        </div>
        <div class='row'>
	            <span class='label'>Award E-PIN:</span>
	            <span class='formfield'>
					  <form:input path="awardEpin" id="awardEpin" validate="alphaNumericEpin" cssClass="proposalConfigDrpdwn" maxlength="30"/> 
				</span>
	    </div>
	    
	    <%--ENDS  QC 6523  Release 3.7.0--%>
        <div class='row'>
        <%-- Code updated for R4 Ends --%>
				<span class='label'>Date Approved from:</span>
				<span class='formfield'>
				<span class='floatLft'>							
						<form:input path="dateApprovedFrom" class='datepicker' id="dateApprovedFrom"  validate="calender" maxlength="10" />
						<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateApprovedFrom',event,'mmddyyyy');return false;"/> 
						&nbsp;&nbsp;
				</span>
				<span class="error"></span>
				<b>to:</b>
				<span>							
						<form:input path="dateApprovedTo" class='datepicker' id="dateApprovedTo" validate="calender" maxlength="10" />
						<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateApprovedTo',event,'mmddyyyy');return false;"/>
				</span>
				<span class="error"></span>
			</span>
	    </div>
		 <div class='row'>
            <span class='label'>Tagged:</span>
             <span class='formfield'>
				<span class='leftColumn'>
					<span><form:checkbox path="tagged" id="tagged" value="1"/><label for='tagged'>Yes</label></span>
				 </span>
				 <span class='rightColumn'>
				 	<span><form:checkbox path="tagged" id='notTagged' value="0"/><label for='notTagged'>No</label></span>
				 </span>
            </span>
        </div>
       
		<div class="buttonholder">
			<input type="button" id="clearfilter" value="Set to Default Filters" onclick="clearTaxonomyTaggingFilter()" class="graybtutton" disabled/>
			<input type="button" value="Filter" id="filter" onclick="displayFilter()"/>
		</div>  
    </div>
    <%-- Popup for Filter Task Ends --%>
 </div>  
<%--Filter and Reassign section ends --%>
<div class='clear'></div>
<div class="tabularWrapper taxonomyTaggingDiv" id="taxonomyTaggingTable" style='min-height:450px;'>
	<st:table objectName="ProcurementProposalList"  cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}' >
		
		<st:property headingName="Select" columnName="procurementId" align="center"
							size="3%" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaxonomyTaggingExtension" />
		</st:property>
		<st:property headingName="Proposal Title" columnName="proposalTitle" sortType="proposalTitle" sortValue="asc" 
		       align="center" size="14%" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaxonomyTaggingExtension" />
		</st:property>
		
		<st:property headingName="Provider Name" columnName="providerName" sortType="providerName" sortValue="asc"  align="center" size="14%" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaxonomyTaggingExtension" />
		</st:property>
		
		<st:property headingName="Procurement/Contract Title" columnName="procurementContractTitle" sortType="procurementContractTitle" sortValue="asc"
		                             align="center" size="14%" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaxonomyTaggingExtension" />
		</st:property>
		
		<st:property headingName="Award EPIN" columnName="awardEpin" sortType="awardEpin" sortValue="asc"
		                             align="center" size="10%" >
		</st:property>
	
		<st:property headingName="Date Approved" columnName="approvalDate" sortType="approvalDate" sortValue="asc" align="center" size="9%" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaxonomyTaggingExtension" />
		</st:property>		
		<st:property headingName="Competition Pool" columnName="competitionPoolTitle" sortType="competitionPoolTitle" sortValue="asc"
		                             align="center" size="15%" >
		</st:property>
		<st:property headingName="Tagged" columnName="isTagged" sortType="isTagged" sortValue="asc" align="center" size="1%"  />
	
		<st:property headingName="Action" columnName="action" align="center" size="9%" >
		  <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.TaxonomyTaggingExtension" />
		</st:property>
	</st:table>
	<c:if test="${ProcurementProposalList eq null or fn:length(ProcurementProposalList) eq 0}">
		<div class="messagedivNycMsg noRecord" id="messagedivNycMsg">No Data found.</div>
	</c:if>
</div>

			<form:hidden path="firstSort"/>
			<form:hidden path="secondSort"/>
			<form:hidden path="firstSortType"/>
			<form:hidden path="secondSortType"/>
			<form:hidden path="sortColumnName"/>
			<form:hidden path="firstSortDate"/>
			<form:hidden path="secondSortDate"/>

<div class='clear'></div>
<%-- Grid Starts --%>
		<div class="tabularWrapper gridfixedHeight">
		<%-- Pagination --%>
			<div class="floatLft"><span>Proposals &amp; Contracts: <label>${records}</label></span></div>
			<%-- Pagination for Bottom of the grid --%>
		</div>


		
<div>&nbsp;</div>	
</form:form>

<%-- Overlay starts --%> 
<%--Edit Taxonomy tag Overlay --%>
<div class="alert-box alert-box-editTaxonomyTag">
		<%-- Code updated for R4 Starts --%>
		<form name="editTaxonomyTaggingForm" id="editTaxonomyTaggingForm" action="${taxonomyTreeUrl}" method ="post" >
			<div id="editTaxonomyTaggingDiv">
			    <div class="tabularCustomHead">Taxonomy Tagging</div>
			    <div class="tabularContainer"> 
				     <div id="popup1MessageDiv" style="display:none;"  ><div></div> 
					</div>
			    	<h2>Taxonomy Tagging</h2>
			    	<div class='hr'></div>
					<div class="formcontainer tabularWrapper clear" id="addNewTagInBulkGrid">
						<table id="headerTableTaxonomy">
							<tr><td width="33%">Proposal Title</td><td width="33%">Provider Name</td><td width="33%">Procurement/Contract Title</td></tr>
						</table>
						<input type="hidden" id="hiddenContractIdBulk" name="hiddenContractIdBulk" value=""/>
						<input type="hidden" id="hiddenDeletedTags" name="hiddenDeletedTags" value=""/>
						<input type="hidden" id="hiddenProposalIdBulk" name="hiddenProposalIdBulk" value=""/>
						<input type="hidden" id="hiddenProcurementIdBulk" name="hiddenProcurementIdBulk" value=""/>
						<input type="hidden" id="submit_action" name="submit_action" value="saveTaxonomyTagUrlInBulk"/>
					</div>
					<%--	first screen popup start		--%>
					<div id="firstScreenPortion">
						<div id='tagListWrapper' class="addNewTagInBulkPopUpDiv">
							<div class='taskButtons'>
								<input type='button' class='add' id='addNewTagInBulkPopUp' value='Add New Tag' onclick="addNewTag()" />
							</div>	
						</div>
						<div class="tabularWrapper clear">
							<table id="serviceFunctionList">
								<tr><td width="20%">Service/Function</td><td width="60%">Modifiers</td><td width="20%">Actions</td></tr>
								<tr id="blankRow" class="noRecord"><td colspan="3" class="noRecord">No tags have been added yet...</td></tr>
							</table>
						</div>
						<div id="tagAllDiv" style="display:none;">
							<p>Once tags are added in bulk, tags can only be edited individually.
							<b>Please ensure you are tagging the correct proposals and tags.</b></p>
							<div id='tagListWrapper' class="tagAllSelectedProposals">
								<div class='taskButtons floatRht'>
									<input type='button' class='add' id='tagAllSelectedProposals' value='Tag All Selected Proposals' />
								</div>	
							</div>
						</div>
					</div>
					<%--	first screen popup ends		--%>
					<%--	second screen popup start		--%>
					<div id="secondScreenPortion" style="display:none">
					</div>
					<%--	second screen popup ends		--%>
				</div>
			</div>
		</form>
		<%-- Code updated for R4 Ends --%>
		<a  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_taxonomytagging&_nfls=true&removeNavigator=false&navigatefrom=taxonomytaggingpage&_urlType=render&_windowLabel=portletInstance_33" class="exit-panel upload-exit ttpopup-close" title="Exit">&nbsp;</a>
</div>

<%--Add Taxonomy Overlay --%>
<div class="alert-box alert-box-addTaxonomyTag">
		<div id="addTaxonomyTaggingDiv"></div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit">&nbsp;</a>
</div>
<%-- Code updated for R4 Starts --%>
<%--Edit Taxonomy tag Overlay --%>
<div class="alert-box alert-box-removeAllTaxonomyTag">
		<div id="removeAllTaxonomyTaggingDiv"></div>
		<a  href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_taxonomytagging&_nfls=true&removeNavigator=false&navigatefrom=taxonomytaggingpage&_urlType=render&_windowLabel=portletInstance_33" class="exit-panel removeall-exit" title="Exit">&nbsp;</a>
</div>
<div class="overlay"></div>
<%-- Code updated for R4 Ends --%>