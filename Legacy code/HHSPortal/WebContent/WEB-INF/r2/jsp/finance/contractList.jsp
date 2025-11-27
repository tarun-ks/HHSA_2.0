<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.ContractList"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page import="com.nyc.hhs.util.ActionStatusUtil"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractlist.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/suspendContract.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<portlet:defineObjects/>
<portlet:renderURL var="duplicateRender" escapeXml="false">
<portlet:param name="duplicate_render" value="duplicateRender" />
</portlet:renderURL>
<input type="hidden" name="duplicateRender" id="duplicateRender"  value="${duplicateRender}"/>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
	<%--code updation for R4 starts--%>
<c:set var="contractListSection"><%=HHSComponentMappingConstant.CONTRACT_LIST%></c:set>

<d:content section="${contractListSection}" authorize="" isReadOnly="false">
	<%--code updation for R4 ends--%>
<jsp:include page="financeHeader.jsp"></jsp:include>
<div class='clear'></div>
<div class='formcontainer'>

<portlet:renderURL var="viewContractConfigReadOnly" escapeXml="false">
	<portlet:param name="action" value="contractListAction" />
	<portlet:param name="render_action" value="showContractConfigReadOnlyDetails"/>
</portlet:renderURL>
<input type="hidden" name="viewContractConfigReadOnly" id="viewContractConfigReadOnly"  value="${viewContractConfigReadOnly}"/>

<portlet:actionURL var="view1ContractConfigReadOnly" escapeXml="false">
	<portlet:param name="action" value="contractListAction"/>
	<portlet:param name="submit_action" value="fetchContractConfigReadOnlyDetails"/>
</portlet:actionURL>
	<%--code updation for R4 starts--%>
<portlet:actionURL var="viewAmendmentsList" escapeXml="false">
	<portlet:param name="action" value="contractListAction"/>
	<portlet:param name="submit_action" value="amendment"/>
</portlet:actionURL>

<input type="hidden" name="view1ContractConfigReadOnly" id="view1ContractConfigReadOnly"  value="${view1ContractConfigReadOnly}"/>
<input type="hidden" name="viewAmendmentsList" id="viewAmendmentsList"  value="${viewAmendmentsList}"/>
	<%--code updation for R4 ends--%>
<portlet:actionURL var="viewContractCOF" escapeXml="false">
	<portlet:param name="action" value="contractListAction"/>
	<portlet:param name="submit_action" value="showContractCOF"/>
</portlet:actionURL>
<input type="hidden" name="viewContractCOF" id="viewContractCOF"  value="${viewContractCOF}"/>	 
 
<portlet:resourceURL var='selectFinanceAmendContract' id='selectFinanceAmendContract' escapeXml='false'>
</portlet:resourceURL>
<portlet:resourceURL var="getEpinListResourceUrl" id="getEpinListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var="getContractNoListResourceUrl" id="getContractNoListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var="getContractTypeOverlayPage" id="getContractTypeOverlayPage" escapeXml="false">
</portlet:resourceURL>

<portlet:renderURL var="contractListUrl" escapeXml="false"><portlet:param name='action' value='contractListAction' /></portlet:renderURL>
<input type = 'hidden' value='${contractListUrl}' id='contractListUrl'/>

<portlet:resourceURL var="getBulkUploadTemplatePage" id="getBulkUploadTemplatePage" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getBulkUploadTemplatePage}' id='hiddenBulkContractUploadTemplateUrl'/>
<input type = 'hidden' value='${getEpinListResourceUrl}' id='getEpinListResourceUrl'/>
<input type = 'hidden' value='${getContractNoListResourceUrl}' id='getContractNoListResourceUrl'/>
<input type = 'hidden' value='${selectFinanceAmendContract}' id='hiddenSelectAmendContractUrl'/>
<input type = 'hidden' value='${getContractTypeOverlayPage}' id='hiddenContractTypeOverlayPageUrl'/>
<input type = 'hidden' value='' id='contractId'/>
<%--Start : Added in R5 --%>
<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromList'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<portlet:resourceURL var='getContractSharedListOverlay' id='getContractSharedListOverlay' escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${getContractSharedListOverlay}' id='getContractSharedListOverlay' />
<link rel="stylesheet" href="../css/style.css" type="text/css"></link>
<%--End : Added in R5 --%>
<style type="text/css">
.addContract {
	background:#FFF;
	display:none ;
	top: 20%;
	width: 50%;
	z-index: 1001;
	position: fixed
}
h2{width:82%}
 
.alert-box-amend-contract .ui-state-active {
	background: #4297E2 !important
}
.alert-box-amend-contract{
	background: #FFF;
    display: none;
    z-index: 1001;
    position: fixed
}

</style>


<portlet:actionURL var="filterContractListUrl" escapeXml="false">
	<portlet:param name="submit_action" value="filterContracts"/>
</portlet:actionURL>
<script type="text/javascript">
   //show selected method.
		showSelected('contractListAction','');
		showHeaderSelected('header_financials');
		// Javascript for filter popup
	</script>
<div class='clear'></div>

<%-- Container Starts --%>
<%-- Start || Changes made for enhancement 6482 for Release 3.8.0 --%>
<portlet:resourceURL var='updateContractInforOverlay' id='updateContractInforOverlay'
	escapeXml='false'>
</portlet:resourceURL>
<input type='hidden' value='${updateContractInforOverlay}'
	id='updateContractInforOverlay' />
	
<div class="overlay"></div>
<div class="alert-box alert-box-updateContractInfo">
<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
<div id="updateContractInfo"></div>
</div>
<%-- added for r5 getContractSharedList --%>
<div class="alert-box alert-box-getContractSharedList">
<a href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
<div id="getContractSharedList"></div>
</div>
<%-- End || Changes made for enhancement 6482 for Release 3.8.0 --%>

<div id='tabs-container'>
<h2>Contract List</h2>
   <div id="helpIcon" class="iconQuestion">
   <c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
   <c:set var="cityManager"><%=HHSComponentMappingConstant.SECTION_CONTRACT_LIST%></c:set>
    <d:content section="${helpIconProvider}">
     <div id="helpIcon" class="marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
          <input type="hidden" id="screenName" value="Contract List" name="screenName"/>
   </d:content> 
  </div>

<div class='hr'></div>
	<%--code updation for R4 starts--%>
	<%if(null!=session.getAttribute("successMessage")){%>
		<div id="transactionStatusDiv" class="passed breakAll" style="display:block" >${successMessage}</div>
	<%session.removeAttribute("successMessage");}
	 else if(null!=session.getAttribute("errorMessage")){%>
		<div id="transactionStatusDivError" class="failed breakAll" style="display:block" >${errorMessage}</div>
	<%
	 session.removeAttribute("errorMessage");}
	%>
<div class="failed" style="color:red" id="errorGlobalMsg">{success}</div>
<div class="passed" id="successGlobalMsg">${failure}</div>
		<div id="bulkUploadSuccessDiv" style="display:none"></div>
	
	<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
					<%session.removeAttribute("message");
	session.removeAttribute("messageType");
	%>
				</div>
	</c:if>
		<%--code updation for R4 ends--%>
<p>Listed below are the contracts for your <c:choose><c:when test="${org_type eq 'provider_org'}">organization</c:when><c:otherwise>Agency</c:otherwise></c:choose>. A default filter has been applied.</p>

<%--Filter and Reassign section starts --%>
<div class="taskButtons nowrap">
    <span class='floatLft'>
    <input type="button" value="Filter Contracts" class="filterDocument floatLft marginReset"  onclick="setVisibility('documentValuePop', 'inline');" />
    </span class='floatLft'>
    
    <!-- Begin QC8914 R7.2.0 Oversight Role Hide 'Add Contract' and 'Bulk Upload' Buttons -->
    <% 
 		if(! CommonUtil.hideForOversightRole(request.getSession()))
	{%>
		 <c:if test="${org_type eq 'city_org'}">
		   <d:content section="${cityManager}">
		 	<span class='floatLft'>&nbsp;<input type="button" value="Add Contract" class="add marginReset" onclick="fillAndShowOverlay('Add Contract','');"/></span>
		 		<%--code updation for R4 starts--%>
		 	<span class='floatLft'>&nbsp;<input type="button" value="Bulk Upload" class="add marginReset" onclick="getBulkUploadConfirmData('Bulk Upload Confirm','');"/></span>
		 		<%--code updation for R4 ends--%>
		 </d:content>
		 </c:if>
	<%}%>	
	<!-- End QC8914 R7.2.0 Oversight Role Hide 'Add Contract' and 'Bulk Upload' Buttons -->
	 
	<%--added in R7 --%>
	<span class='floatLft'>
	<input type="hidden" id="filterflaggedIcon" value="${ContractList.filterFlaggedContracts}" />
 	<c:if test="${org_type eq 'city_org' || (org_type eq 'agency_org')}">	
	<c:choose>
	<c:when test="${ContractList.filterFlaggedContracts eq 'false'}">
	<input type="button" value="  Show Flagged  " class="button buttonGap" style="margin-left: 4px;margin-top:0px;" id="displayFlaggedMessage" onclick="displayFilteredContracts()"/> 
	</c:when>
	<c:otherwise>
	<input type="button" value="   Show All   " class="button buttonGap" style="margin-left: 4px;margin-top:0px;"   id="displayAll" onclick="displayAll()" />
	</c:otherwise>
	</c:choose>
	</c:if>
	</span>
	<%--added in R7 --%>
		<span class='count'>
		 	<span>Contracts: <label>${totalCount}</label></span>
		 <c:if test="${org_type eq 'provider_org'}">
			<span>Total Value of Active Contracts: $<label id="contractsValue">${contractsValue}</label></span>
		 </c:if>
	</span>
	<%-- Form Data Starts --%>
<form:form id="contractFilterForm" name="contractFilterForm" action="${filterContractListUrl}" method ="post" commandName="ContractList" onkeydown="if (event.keyCode == 13) {displayFilter();}">
<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>

<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>
<input type = 'hidden' value='' name="providerId" id='providerId'/>
<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>
<input type = 'hidden' value='' name="providerId" id='providerId'/>
    <%--  Popup for Filter Task Starts  --%>
    <div id="documentValuePop" class='formcontainerFinance' style='width:460px;'>
        <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
        <div class='row'>
            <span class='label'>Procurement/ Contract Title:</span>
            <span class='formfield'>
            	<form:input path="contractTitle" id="contractTitle" cssClass="proposalConfigDrpdwn" maxlength="120"  onkeyup="removeNonRequiredCharacter(this)" />
            </span>
            <span class="error"></span>
        </div>
         <c:if test="${(org_type eq 'city_org') or (org_type eq 'provider_org')}">
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
        </c:if>
        
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
        
       
         <c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
	        <div class='row'>
	            <span class='label'>Provider:</span>
	            <span class='formfield'>
					<form:input path="provider" id="provider" cssClass="proposalConfigDrpdwn" maxlength="100"  onkeyup="removeNonRequiredCharacter(this)"/>
	            </span>
	            <span class="error"></span>
	        </div>
        </c:if>
         <div class='row'>
            <span class='label'>CT#:</span>
            <span class='formfield'>
				 <form:input path="ctId" id="ctId" cssClass="proposalConfigDrpdwn" maxlength="32" onkeyup="removeNonRequiredCharacter(this)"/>
            </span>
            <span class='error'></span>
        </div>
        <c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
	        <div class='row'>
	            <span class='label'>Award E-PIN:</span>
	            <span class='formfield'>
					  <form:input path="awardEpin" id="awardEpin" validate="alphaNumericEpin" cssClass="proposalConfigDrpdwn" maxlength="30"/>
				</span>
	        </div>
        </c:if>
		<div class='row'>
            <span class='label'>Contract Value From ($):</span>
            <span class='formfield'>
               <form:input path="contractValueFrom" id="contractValueFrom" validate="number" cssStyle="width:43%"/> to: <form:input path="contractValueTo" id="contractValueTo" validate="number" cssStyle="width:43%"/>
            </span>
        </div>
		<div class='row'>
            <span class='label'>Status:</span>
            <span class='formfield'>
				<span class='rightColumn'>
				<c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
					<span><form:checkbox path="contractStatusList" id='chkClosed' value="68"/><label for='chkClosed'>Closed</label></span>
				</c:if>
					 <span><form:checkbox path="contractStatusList" id='chkSuspended' value="67"  /><label for='chkSuspended'>Suspended</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkCancelled' value="69"  /><label for='chkCancelled'>Cancelled</label></span>
				</span>
				<span class='leftColumn'>
				 <c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
					 <span><form:checkbox path="contractStatusList" id='chkPendingConfig' checked="${(fn:contains(ContractList.contractStatusList, '59') ||(firstLoad)) ? 'checked' : ''}" value="59" /><label for='chkPendingConfig'>Pending Configuration</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkPendingCoF' checked="${(fn:contains(ContractList.contractStatusList, '60') ||(firstLoad)) ? 'checked' : ''}" value="60" /><label for='chkPendingCoF'>Pending CoF</label></span>
				</c:if>	
				<%-- R6.3 QC5690 - add Pendinng Notification status to Contracts Filter 
				     <span><form:checkbox path="contractStatusList" id='chkPendingNotification'  checked="${(fn:contains(ContractList.contractStatusList, '194') || firstLoad) ? 'checked' : ''}" value="194"/><label for='chkPendingNotification'>Pending Notification</label></span>	
					 --%>
					 <span><form:checkbox path="contractStatusList" id='chkPendingRegistration'  checked="${(fn:contains(ContractList.contractStatusList, '61') || firstLoad) ? 'checked' : ''}" value="61"/><label for='chkPendingRegistration'>Pending Registration</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkRegistered'  checked="${(fn:contains(ContractList.contractStatusList, '62') || firstLoad) ? 'checked' : ''}" value="62"/><label for='chkRegistered'>Registered</label></span>
				<c:if test="${org_type eq 'provider_org'}"> 
					<span><form:checkbox path="contractStatusList" id='chkClosed' value="68"/><label for='chkClosed'>Closed</label></span>
				</c:if>
				</span>
            </span>
        </div>
		<div class='row'>
            <span class='label'>Contract Term From:</span>
            <span class='formfield'>
                <span class='floatLft'>
                	<form:input path="contractStartDate" id="activeContractsFrom" validate="calender" cssClass="datepicker" maxlength="10"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('activeContractsFrom',event,'mmddyyyy');return false;">
                	&nbsp; &nbsp; 
                </span>
                 <span id="activeContractsFromError" class="error clear"></span>
                 
                to: 
                
                <span>
	                <form:input path="contractEndDate" id="activeContractsTo" validate="calender" cssClass="datepicker"  maxlength="10"/><img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('activeContractsTo',event,'mmddyyyy');return false;">
                </span>
                 <span id="activeContractsToError" class="error clear"></span>
            </span>
        </div>
		
		
        <div class="buttonholder">
            <input type="button" class='graybtutton' value="Set to Default Filters" onmousedown="clearContractFilter();" onclick="clearContractFilter()" />
            <input type="button" value="Filter" onclick="displayFilter()" />
        </div> 
        
    </div>
    <%-- Popup for Filter Task Ends --%>

     
   
	</div>
<%--Filter and Reassign section ends --%>

<input type="hidden" id="orgType" value="${org_type}"/>
<div class='clear'></div>

	<div class='tabularWrapper gridfixedHeight contractListDiv' style='min-height:700px !important'>
		<span id='contractListTable'><st:table 
		objectName="financialsList" cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}' >
		<%--Made changes  for defect id 6248 release 3.3.0 --%>
		<c:if test="${org_type eq 'city_org'}">
			<st:property headingName="Agency" columnName="contractAgencyName"
					size="15%" sortType="contractAgencyName">
			</st:property>
		</c:if>
		<st:property headingName="Procurement/Contract Title"
			columnName="contractTitle" size="20%" sortType="contractTitle">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractActionFinancialExtension"/>
		</st:property>
		<c:choose>
			<c:when test="${org_type eq 'provider_org'}">
				<st:property headingName="Agency" columnName="contractAgencyName"
					size="15%" sortType="contractAgencyName">
				</st:property>
			</c:when>
			<c:otherwise>
				<st:property headingName="Provider" columnName="provider"
					size="15%" sortType="provider" >
					<%--Start : Added in R5 --%>
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractActionFinancialExtension"/>
					<%--End : Added in R5 --%>
				</st:property>
		</c:otherwise>
		</c:choose>
		<st:property headingName="CT#" columnName="ctId" size="15%"
			sortType="ctId" >
		</st:property>
		<st:property headingName="Contract Value($)" size="15%" columnName="contractValue"
			 sortType="contractValue">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractActionFinancialExtension"/>
		</st:property>
		<c:if test="${org_type eq 'provider_org'}">
			<st:property headingName="Last Updated" columnName="lastUpdateDate"
				size="20%" sortType="lastUpdateDate" >
			</st:property>
		</c:if>
		<st:property headingName="Status" columnName="contractStatus"
			size="10%" sortType="contractStatus">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractActionFinancialExtension"/>
		</st:property>
		<%--R5 Updated logic  --%>
		<%-- <c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}"> --%>
			<st:property headingName="Action" columnName="actions" 
					size="20%" >
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractActionFinancialExtension"/>
			</st:property>
		<%-- </c:if> --%>
	</st:table></span>
	
	<form:hidden path="firstSort"/>
	<form:hidden path="firstSortDate"/>
	<form:hidden path="secondSort"/>
	<form:hidden path="secondSortDate"/>
	<form:hidden path="firstSortType"/>
	<form:hidden path="secondSortType"/>
	<form:hidden path="sortColumnName"/>
	<%--Added in R7 for Flagged Contracts --%>
 	<form:hidden path="filterFlaggedContracts"/> 
	<%--R7 End --%>
	<c:if test="${fn:length(financialsList) eq 0}">
	<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No Records Found</div>
	</c:if>
	<div class='floatLft'><span> Contracts: <label>${totalCount}</label></span></div>
	</div>

<p>&nbsp;</p>

<input type = 'hidden' value='' id='hdncontractId' name='hdncontractId' />
<input type = 'hidden' value='' id='hdncontractStartDt' name='hdncontractStartDt'/>
<input type = 'hidden' value='' id='hdncontractEndDt' name='hdncontractEndDt'/>
<input type = 'hidden' value='' id='hdncontractAmt' name='hdncontractAmt'/>
<input type = 'hidden' value='' id='hdnstatusId' name='hdnstatusId'/>
<input type = 'hidden' value='' id='hdncontractTypeId' name='hdncontractTypeId'/>


</form:form>
</div>

<div class="overlay"></div>
<div class="alert-box-amend-contract" id="overlayDivId">	
</div>


<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
</div>
</d:content>

<%--Added in R7 for Contract Level Message --%>
<portlet:resourceURL var='launchSubmitMessageOverlay' id='launchSubmitMessageOverlay'
	escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${launchSubmitMessageOverlay}' id='hiddenlaunchSubmitMessageOverlay'/>

<portlet:resourceURL var='fetchMessageOverlayDetails' id='fetchMessageOverlayDetails'
	escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${fetchMessageOverlayDetails}' id='fetchMessageOverlayDetails'/>

<portlet:resourceURL var='unflagContractMessage' id='unflagContractMessage'
	escapeXml='false'>
</portlet:resourceURL>
<input type="hidden" id="statusUnflag" name="statusUnflag" value="${status}" /> 
<input	type="hidden" id="unflagContractMessage" name="unflagContractMessage" value="${unflagContractMessage}" />
<input type="hidden" id="agencyRole" value="${role}"/>
<%-- <div class="overlay"></div>
<div class="alert-box-amend-contract" id="overlayDivId">	
</div> 
<div class="overlay"></div>
--%>
<div class="alert-box-help">
	<div class="tabularCustomHead toplevelheaderHelp"></div>
	<div id="helpPageDiv"></div>
	<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box alert-box-delete alert_bulk_notification">
	<div class="content">
		<div id="newTabs">
			<%-- updated for defect 8600 --%>
			<div class="tabularCustomHead" id="contractMessageHeader" style="margin-bottom: 18px;">
				Flag Contract<a href="javascript:void(0);"
					class="exit-panel nodelete" title="Exit"></a>
			</div>
			<div id="deleteDiv" class="linePadding">    
			<div class="messagediv failed" id="messagediv"  style="display:block; margin-top: -10px;margin-bottom: 5px;">! Comments must be entered in the comment box.<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" title="Close" alt="Close" onclick="showMe('messagediv', this)"></div>
				<b class="boldlayout" id="contractSubMessageHeader"><br>Contract Message</b>
				<%-- updated for defect 8600 --%>
				<hr class="restoreHeaderLess" align="left" />
				<div class="pad6 clear promptActionMsg"></div>
				<div class='clear' id="flaggedDescription" style="margin-bottom: 30px;">By flagging this contract, an informational message will appear on the associated financial tasks, invoice details, and contract budget details pages.</div>
				<div id="row" style="display:  block; width:  100%;padding-top:5px;">
					<span class='label'> Procurement/Contract Title:</span> 
					<span  style="float:left;width: 63%; " id="titleId"> </span>
				</div>
				<div style="clear:both;"></div>
				<div id="row" style="display:  block; width:  100%; padding-top:5px;">
					<span class='label'> CT#:</span> 
					<span  style="float:left;width: 63%; " id="ctNumber"> </span>
				</div>
				<div style="clear:both;"></div>
				<div id="row" style="display:  block; width:  100%;padding-top:5px;">
					<span class='label'> Agency:</span> 
					<span  style="float:left;width: 63%; " id="agencyType"> </span>
				</div>
				<div style="clear:both;"></div>
				<div id="row" style="display:  block; width:  100%;padding-top:5px;">
					<span class='label'> Provider:</span> 
					<span  style="float:left;width: 63%; " id="providerName"> </span>
				</div>
			<div style="clear:both;"></div>
					
									<div class='unflag unflagOverlayDiv' id="row">
					<span class='label'> Flagged By:
					</span> <span class='aligntext'> <span style="float:left;width: 63%; "
						id='flaggedBy'></span>
					</span>			</div>
					<div style="clear:both;"></div>
									<div class='unflag unflagOverlayDiv' id="row">
					<span class='label'> Flag Date:
					</span> <span class='aligntext'> <span style="float:left;width: 63%; "
						id='flaggedDate'></span>
					</span>			</div>
					<div style="clear:both;"></div>
					<div class='row reqiredDiv' style="margin-top: 35px;margin-bottom: -30px;" id="reqiredDiv">
					<label class="required">*</label>Indicates a Required Field
					</div>
<div>
<div class='heading' style="margin-top: 39px; width:540px; text-align: center;" > <label class="required">*</label>Comments:</div>
 	<textarea cols="150" rows="100" placeholder="Comments" name="publicCommentArea" id="publicCommentArea"
  		class='taskCommentsTxtarea' onkeyup="setMaxLength(this,200)" onkeypress="setMaxLength(this,200)" 
  	    style=" width:574px !important; height:102px; margin-top: 0px; "onchange="setChangeFlag()" 
   	    onblur="if(this.value == ''){this.placeholder = 'Comments'}"
    	onFocus="this.placeholder = '';if (this.value == this.defaultValue) {this.value = '';}"></textarea>
 <span id="textAreaError" class="error"></span>
</div>
			
<span class='clear' id="unFlaggedConfirmationMessage" >&nbsp;Are you sure you want to unflag this contract?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
 			
				<div class="buttonholder txtCenter pad6" style="
    margin-top: 24px; ">
					<input type="button" title="Cancel" class="graybtutton exit-panel"
						id="cancelBulk" value="   Cancel   " /> <input type="button"
						title="Save" onclick="submitMessageoverlay()"
						class="greenbtutton" id="submitBulk"
						value=" Save " />
						<input type="button"
						title="Unflag" onclick="UnflagContractMessage()"
						class="greenbtutton" id="submitUnflagBulk"
						value="Unflag" />
						<input type = 'hidden' value='' id='selectedContractId' name='selectedContractId'/>
				</div>
				<div class='hr'></div>
			</div>
		</div>
	</div>
	<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
</div>

<%-- R7 End--%>
