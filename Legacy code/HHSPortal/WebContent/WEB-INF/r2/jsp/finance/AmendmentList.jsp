<%-- JSP Added in R4 --%>
<%-- This is jsp for S436 – Amendment List (Accelerator/Agency) and S437 – Amendment List (Provider) and  S438 – Amendment List Filter--%>
<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.ContractList"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
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
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/contractlist.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/suspendContract.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/amendmentList.js"></script>
<portlet:defineObjects/>
<portlet:renderURL var="duplicateRender" escapeXml="false">
<portlet:param name="duplicate_render" value="duplicateRender" />
</portlet:renderURL>
<portlet:renderURL var="duplicateRenderAmendment" escapeXml="false">
<portlet:param name="duplicate_render_amendment" value="duplicateRenderAmendment" />
</portlet:renderURL>
<!--Start: Added in R7 for defect 8644  -->
<portlet:actionURL var="viewAmendmentsList" escapeXml="false">
	<portlet:param name="action" value="contractListAction"/>
	<portlet:param name="submit_action" value="amendment"/>
</portlet:actionURL>
<input type="hidden" name="view1ContractConfigReadOnly" id="view1ContractConfigReadOnly"  value="${view1ContractConfigReadOnly}"/>
<input type="hidden" name="viewAmendmentsList" id="viewAmendmentsList"  value="${viewAmendmentsList}"/>
<!--End: Added in R7 for defect 8644  -->
<input type="hidden" name="duplicateRender" id="duplicateRenderAmendment"  value="${duplicateRenderAmendment}"/>
<input type="hidden" name="duplicateRender" id="duplicateRender"  value="${duplicateRender}"/>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<c:set var="amendmentListSection"><%=HHSComponentMappingConstant.AMENDMENT_LIST%></c:set>
<!--Start Added in R5 -->
<d:content section="${amendmentListSection}" authorize="" isReadOnly="false">
<!--End Added in R5 -->
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
<input type="hidden" name="view1ContractConfigReadOnly" id="view1ContractConfigReadOnly"  value="${view1ContractConfigReadOnly}"/>
	 
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


<portlet:actionURL var="filterAmendedContractListUrl" escapeXml="false">
	<portlet:param name="submit_action" value="filterAmendedContracts"/>
</portlet:actionURL>
<script type="text/javascript">
   //show selected method.
		showSelected('amendmentListAction','');
		showHeaderSelected('header_financials');
		// Javascript for filter popup
	</script>
<div class='clear'></div>

<%-- Container Starts --%>

<div id='tabs-container'>
<h2>Amendment List</h2>
   <div id="helpIcon" class="iconQuestion">
   <c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
   <c:set var="cityManager"><%=HHSComponentMappingConstant.SECTION_CONTRACT_LIST%></c:set>
    <d:content section="${helpIconProvider}">
     <div id="helpIcon" class="marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
          <input type="hidden" id="screenName" value="Amendment List" name="screenName"/>
   </d:content> 
  </div>
<div class='hr'></div>

<div class="failed" id="errorGlobalMsg"></div>
 
	<%if(null!=session.getAttribute("successMessage")){%>
		<div id="transactionStatusDiv" class="passed breakAll" style="display:block" >${successMessage}</div>
	<%session.removeAttribute("successMessage");}
	 else if(null!=session.getAttribute("errorMessage")){%>
		<div id="transactionStatusDivError" class="failed breakAll" style="display:block" >${errorMessage}</div>
	<%
	 session.removeAttribute("errorMessage");}
	%>
	<c:if test="${message ne null}">
				<div class="${messageType}" id="messagediv" style="display:block">${message} <img
					src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
					class="message-close" 
					onclick="showMe('messagediv', this)">
				</div>
			</c:if>
<p>Listed below are the contract amendments for your <c:choose><c:when test="${org_type eq 'provider_org'}">organization</c:when><c:otherwise>Agency</c:otherwise></c:choose>. A default filter has been applied.</p>

<%--Filter and Reassign section starts --%>
<div class="taskButtons nowrap">
    <span class='floatLft'>
    <input type="button" value="Filter Amendments" class="filterDocument floatLft marginReset"  onclick="setVisibilityAmendment('documentValuePop', 'inline');" />
    </span class='floatLft'>
		
		<span class='count'>
		 	<span>Amendments: <label>${totalCount}</label></span>
		 <%--<c:if test="${org_type eq 'provider_org'}">
			<span>Total Value of Active Contracts: $<label id="contractsValue">${contractsValue}</label></span>
		 </c:if>
	--%></span>
	<%-- Form Data Starts --%>
<form:form id="contractFilterFormAmendment" name="contractFilterFormAmendment" action="${filterAmendedContractListUrl}" method ="post" commandName="AmendedContractList" onkeydown="if (event.keyCode == 13) {displayFilterAmendment();}">
<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>
<input type = 'hidden' value='' name="providerId" id='providerId'/>
    <%--  Popup for Filter Task Starts  --%>
    <div id="documentValuePop" class='formcontainerFinance' style='width:434px !important'>
        <div class='close'><a href="javascript:setVisibilityAmendment('documentValuePop', 'none');" >X</a></div>
        <div class='row'>
            <span class='label'>Procurement/ Contract Title:</span>
            <span class='formfield'>
            	<form:input path="baseContractTitle" id="baseContractTitle" cssClass="proposalConfigDrpdwn" maxlength="120"  onkeyup="removeNonRequiredCharacter(this)" />
            </span>
            <span class="error"></span>
        </div>
         <div class='row'>
            <span class='label'>Amendment Title:</span>
            <span class='formfield'>
            	<form:input path="contractTitle" id="contractTitle" cssClass="proposalConfigDrpdwn" maxlength="120"  onkeyup="removeNonRequiredCharacter(this)" />
            </span>
        </div>
       
         <c:if test="${(org_type eq 'provider_org') or (org_type eq 'city_org')}">
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
        <%--Start Enhancement id 6400 release 3.4.0--%>
        <c:if test="${(org_type eq 'city_org')}">
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
       </c:if>
        <%--End Enhancement id 6400 release 3.4.0--%>
         <c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
	        <div class='row'>
	            <span class='label'>Provider:</span>
	            <span class='formfield'>
					<form:input path="provider" id="provider" cssClass="proposalConfigDrpdwn" maxlength="100"  onkeyup="removeNonRequiredCharacter(this)"/>
	            </span>
	            <span class="error"></span>
	        </div>
        </c:if>
         <%--Start Enhancement id 6400 release 3.4.0--%>
        <c:if test="${(org_type eq 'agency_org')}">
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
       </c:if>
        <%--End Enhancement id 6400 release 3.4.0--%>
         <%--Start Enhancement id 6400 release 3.4.0--%> 
         <c:if test="${(org_type eq 'city_org')}">
			<div class='row'>
	            <span class='label'>CT#:</span>
	            <span class='formfield'>
					 <form:input path="ctId" id="amendCtIdCity" cssClass="proposalConfigDrpdwn" maxlength="32" onkeyup="removeNonRequiredCharacter(this)"/>
	            </span>
	            <span class='error'></span>
	        </div>
	     </c:if>   
        <%--End Enhancement id 6400 release 3.4.0--%>
	        <div class='row'>
	            <span class='label'>Amendment EPIN:</span>
	            <span class='formfield'>
					  <form:input path="awardEpin" id="awardEpinAmendment" validate="alphaNumericEpin" cssClass="proposalConfigDrpdwn" maxlength="30"/>
				</span>
	        </div>
	         <%--Start Enhancement id 6400 release 3.4.0--%> 
         <c:if test="${(org_type eq 'agency_org')}">
			<div class='row'>
	            <span class='label'>CT#:</span>
	            <span class='formfield'>
					 <form:input path="ctId" id="amendCtId" cssClass="proposalConfigDrpdwn" maxlength="32" onkeyup="removeNonRequiredCharacter(this)"/>
	            </span>
	            <span class='error'></span>
	        </div>
	     </c:if>   
        <%--End Enhancement id 6400 release 3.4.0--%>
		<div class='row'>
            <span class='label'>Amendment Value From ($):</span>
            <span class='formfield'>
               <form:input path="contractValueFrom" id="amendedContractValueFrom"  cssStyle="width:43%"/> to: <form:input path="contractValueTo" id="amendedContractValueTo"  cssStyle="width:43%"/>
            </span>
        </div>
        
        <div class='row'>
            <span class='label'>Date of Last Update From:</span>
            <span class='formfield'>
            	<span class=floatLft>
           			<form:input path="dateLastUpdateFrom" id="dateLastUpdateFrom" cssClass="datepicker" validate="calender" maxlength="10"></form:input>
            		<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateLastUpdateFrom',event,'mmddyyyy');return false;">  
					&nbsp;&nbsp; 
					</span>
					<span id="dateLastUpdateFromError" class="error clear"></span>
							To: 
				<span>
			 		<form:input path="dateLastUpdateTo" id="dateLastUpdateTo" cssClass="datepicker" validate="calender" maxlength="10"></form:input>
					<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateLastUpdateTo',event,'mmddyyyy');return false;">
            	</span>
            	 <span id="dateLastUpdateToError" class="error clear"></span>
            	 <span class="error clear" id="dateCheck"></span>
            	</span>
        </div>
		
        
		<div class='row'>
            <span class='label'>Status:</span>
            <span class='formfield'>
				<span class='rightColumn'>
				<c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
					 <span><form:checkbox path="contractStatusList" id='chkSentForRegistration' checked="${(fn:contains(ContractList.contractStatusList, '130') ||(firstLoad)) ? 'checked' : ''}" value="130"  /><label for='chkSentForRegistration'>Sent for Registration</label></span>
					</c:if>
				<c:if test="${org_type eq 'provider_org'}">
					 <span><form:checkbox path="contractStatusList" id='chkPendingRegistration' checked="${(fn:contains(ContractList.contractStatusList, '61') ||(firstLoad)) ? 'checked' : ''}" value="61"/><label for='chkPendingRegistration'>Pending Registration</label></span>
				</c:if>
					 <span><form:checkbox path="contractStatusList" id='chkRegistered' checked="${(fn:contains(ContractList.contractStatusList, '62') ||(firstLoad)) ? 'checked' : ''}" value="62"  /><label for='chkRegistered'>Registered</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkSuspended' checked="${(fn:contains(ContractList.contractStatusList, '67') ||(firstLoad)) ? '' : ''}" value="67"  /><label for='chkSuspended'>Suspended</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkCancelled' checked="${(fn:contains(ContractList.contractStatusList, '69') ||(firstLoad)) ? '' : ''}" value="69"  /><label for='chkCancelled'>Cancelled</label></span>
				</span>
				<span class='leftColumn'>
					 <span><form:checkbox path="contractStatusList" id='chkPendingConfig' checked="${(fn:contains(ContractList.contractStatusList, '59') ||(firstLoad)) ? 'checked' : ''}" value="59" /><label for='chkPendingConfig'>Pending Configuration</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkPendingCoF' checked="${(fn:contains(ContractList.contractStatusList, '60') ||(firstLoad)) ? 'checked' : ''}" value="60" /><label for='chkPendingCoF'>Pending CoF</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkPendingSubmission'  checked="${(fn:contains(ContractList.contractStatusList, '128') || firstLoad) ? 'checked' : ''}" value="128"/><label for='chkPendingSubmission'>Pending Submission</label></span>
					 <span><form:checkbox path="contractStatusList" id='chkPendingApp'  checked="${(fn:contains(ContractList.contractStatusList, '129') || firstLoad) ? 'checked' : ''}" value="129"/><label for='chkPendingApp'>Pending Approval</label></span>
					 <c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
					 <span><form:checkbox path="contractStatusList" id='chkPendingRegistration' checked="${(fn:contains(ContractList.contractStatusList, '61') ||(firstLoad)) ? 'checked' : ''}" value="61"/><label for='chkPendingRegistration'>Pending Registration</label></span>
					 </c:if>
				</span>
            </span>
        </div>
		<div class="buttonholder">
            <input type="button" class='graybtutton' value="Set to Default Filters" onmousedown="settoDefaultFilters();" onclick="settoDefaultFilters()" />
            <input type="button" value="Filter" onclick="displayFilterAmendment()" />
        </div> 
        
    </div>
    <%-- Popup for Filter Task Ends --%>

     
   
	</div>
<%--Filter and Reassign section ends --%>

<input type="hidden" id="orgType" value="${org_type}"/>
<div class='clear'></div>
	<%--Start changes for defect id 6248 release 3.3.0 --%>
	<div class='tabularWrapper gridfixedHeight amendmentListDiv' style='min-height:700px !important'>
		<st:table 
		objectName="financialsList" cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}' >

		<c:if test="${(org_type eq 'city_org')}">
		<st:property headingName="Agency" columnName="contractAgencyName"
			size="9%" sortType="contractAgencyName">
		</st:property>
		</c:if>
		<st:property headingName="Procurement/Contract Title"
			columnName="baseContractTitle" size="15%" sortType="baseContractTitle">
		</st:property>
		<c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
		<st:property headingName="Provider" columnName="provider"
				size="15%" sortType="provider" >
				<%--Start : Added in R5 --%>
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AmendmentActionFinancialExtension"/>
				<%--End : Added in R5 --%>
		</st:property>
		</c:if>
		<c:if test="${(org_type eq 'provider_org')}">
		<st:property headingName="Agency" columnName="contractAgencyName"
			size="9%" sortType="contractAgencyName">
		</st:property>
		</c:if>
		<c:if test="${org_type eq 'provider_org'}">
		<st:property headingName="Amendment Title" columnName="contractTitle"
					size="18%" sortType="contractTitle" >
		</st:property>
		</c:if>
		<c:if test="${(org_type eq 'provider_org')}">
		<st:property headingName="CT#" columnName="ctId"
					size="18%" sortType="ctId" >
		</st:property>
		</c:if>
		<c:if test="${(org_type eq 'city_org') or (org_type eq 'agency_org')}">
		<st:property headingName="Amendment EPIN" columnName="awardEpin" size="20%"
			sortType="awardEpin" >
		</st:property>
		</c:if>
		<st:property headingName="Amendment Value ($)" size="14%" columnName="contractValue"
			 sortType="contractValue">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AmendmentActionFinancialExtension"/>
		</st:property>
		<c:if test="${org_type eq 'provider_org'}">
			<st:property headingName="Last Updated" columnName="lastUpdateDate"
				size="10%" sortType="lastUpdateDate" >
			</st:property>
		</c:if>
		<st:property headingName="Status" columnName="contractStatus"
			size="25%" sortType="contractStatus">
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AmendmentActionFinancialExtension"/>
		</st:property>
		<%--R5 Updated logic  --%>
			<st:property headingName="Action" columnName="actions" 
					size="10%" >
					<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.AmendmentActionFinancialExtension"/>
			</st:property>
	</st:table>
		<%--End changes for defect id 6248 release 3.3.0 --%>
	<form:hidden path="firstSort"/>
	<form:hidden path="firstSortDate"/>
	<form:hidden path="secondSort"/>
	<form:hidden path="secondSortDate"/>
	<form:hidden path="firstSortType"/>
	<form:hidden path="secondSortType"/>
	<form:hidden path="sortColumnName"/>
	
	<c:if test="${fn:length(financialsList) eq 0}">
	<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No Records Found</div>
	</c:if>
	<div class='floatLft'><span> Amendments: <label>${totalCount}</label></span></div>
	</div>

<p>&nbsp;</p>

<%--Changes for Emergency Build 4.0.1 defect 8360 --%>
<input type = 'hidden' value='${hdnIsViewAmendment}' id='hdnIsViewAmendment' name='hdnIsViewAmendment' />
<input type = 'hidden' value='${hdncontractId}' id='hdncontractId' name='hdncontractId' />
<input type = 'hidden' value='' id='hdncontractStartDt' name='hdncontractStartDt'/>
<input type = 'hidden' value='' id='hdncontractEndDt' name='hdncontractEndDt'/>
<input type = 'hidden' value='' id='hdncontractAmt' name='hdncontractAmt'/>
<input type = 'hidden' value='${hdnstatusId}' id='hdnstatusId' name='hdnstatusId'/>
<input type = 'hidden' value='' id='hdnAmendContractId' name='hdnAmendContractId' />
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