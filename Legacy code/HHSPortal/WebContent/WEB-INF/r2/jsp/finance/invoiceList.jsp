<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.InvoiceList"%>
<%@page import="com.nyc.hhs.frameworks.grid.*"%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/invoiceList.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/invoiceWithdraw.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>

<portlet:defineObjects/>
<portlet:renderURL var="duplicateRenderInvoice" escapeXml="false">
<portlet:param name="duplicate_render_invoice" value="duplicateRenderInvoice" />
<portlet:param name="action" value="invoiceListAction"/>
</portlet:renderURL>
<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>
<input type="hidden" name="duplicateRenderInvoice" id="duplicateRenderInvoice"  value="${duplicateRenderInvoice}"/>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<input type="hidden" id="orgType" value="${org_type}"/>
	<%--code updation for R4 starts--%>
<c:set var="invoiceListSection"><%=HHSComponentMappingConstant.INVOICE_LIST%></c:set>
<!--Start Added in R5 -->
<d:content section="${invoiceListSection}" authorize="" isReadOnly="false">
<!--End Added in R5 -->	
	<%--code updation for R4 ends--%>
<jsp:include page="financeHeader.jsp"></jsp:include>
<div class='clear'></div>

<%-- This is for the type ahead function to CT number --%>

<portlet:resourceURL var="getInvoiceCtResourceUrl" id="getInvoiceCtResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getInvoiceCtResourceUrl}' id='hiddenInvoiceCtResourceUrl'/>

<portlet:resourceURL var="withdrawInvoice" id="withdrawInvoice" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${withdrawInvoice}' id='hiddenWithdrawInvoiceUrl'/>

<portlet:resourceURL var="deleteInvoice" id="deleteInvoice" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${deleteInvoice}' id='hiddenDeleteInvoiceUrl'/>

<portlet:actionURL var="viewInvoice" id="viewInvoice" escapeXml="false">
<portlet:param name="viewInvoice" value="contractInvoiceScreen"/>
<portlet:param name="action" value="invoiceListAction"/>
</portlet:actionURL>
<input type = 'hidden' value='${viewInvoice}' id='hiddenViewInvoiceUrl'/>

<%--Start for Enhancement  id 6461  release 3.4.0 --%>
<portlet:actionURL var="navigateToContractBudget" escapeXml="false">
<portlet:param name="submit_action" value="viewContractBudget"/>
<portlet:param name="action" value="budgetListAction"/>
</portlet:actionURL>
<input type = 'hidden' value='${navigateToContractBudget}' id='navigateToContractBudgetURL'/>
<%--End for Enhancement  id 6461  release 3.4.0 --%>

<%-- Container Starts --%>
<div class='formcontainer'>

<portlet:actionURL var="filterInvoiceListUrl" escapeXml="false">
	<portlet:param name="submit_action" value="filterInvoices"/>
	<portlet:param name="action" value="invoiceListAction" />
</portlet:actionURL> 

<%--Start : Added in R5 --%>
<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromList'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<%--End : Added in R5 --%>

<script type="text/javascript">
   //show selected method.
		showSelected('invoiceListAction','');
		showHeaderSelected('header_financials');
		// Javascript for filter popup
</script>
<form:form id="invoiceFilterForm" action="${filterInvoiceListUrl}" method ="post" name="invoiceFilterForm" commandName="InvoiceList" onkeydown="if (event.keyCode == 13) {submitListInvoice();}">
<div class='clear'></div>
<div id='tabs-container'>
<input type = 'hidden' value='' name="providerId" id='providerId'/>
<h2>Invoice List</h2>
<div id="helpIcon" class="iconQuestion">
   <c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
    <d:content section="${helpIconProvider}">
     <div id="helpIcon" class="marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
          <input type="hidden" id="screenName" value="Invoice List" name="screenName"/>
   </d:content> 
  </div>
<div class='hr'></div>

<div class="failed" id="errorGlobalInvoiceMsg"></div>

<div class="passed" id="successGlobalInvoiceMsg">${message}</div>
	<%if(null!=session.getAttribute("TransactionSuccess")){%>
		<div id="transactionInvoiceStatusDivInvoice" class="passed breakAll" style="display:block" >${TransactionSuccess}</div>
	<%}

	 if(null!=session.getAttribute("TransactionSuccess")){%>
		<div id="transactionInvoiceStatusDivInvoice" class="passed breakAll" style="display:block" >${TransactionSuccess}</div>
	<%}
	  session.removeAttribute("TransactionSuccess");
	%>
	<%--fix done as a part of release 3.1.2 defect 6420 - start--%>
	<%if(null!=session.getAttribute("TransactionFailure")){%>
		<div id="" class="failed breakAll" style="display:block" >${TransactionFailure}</div>
	<%}
	  session.removeAttribute("TransactionFailure");
	%>
	<%--fix done as a part of release 3.1.2 defect 6420 - end--%>
<c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
<p>Listed below are the invoices for your Agency.</p>
</c:if>
<c:if test="${org_type eq 'provider_org'}">
<p>Listed below are the invoices for your organization.</p>
</c:if>

<%--Filter and Reassign section starts --%>
<div class="taskButtons">
    <span class='floatLft'><input type="button" value="Filter Invoices" class="filterDocument"  onclick="setVisibility('documentValuePop', 'inline');" /></span>
    <span class='count'>
    	<span>Invoices:<label>${aoInvoiceCount}</label></span>
	</span>
	
    <%-- Popup for Filter Task Starts --%>
    <div id="documentValuePop" class='formcontainer formcontainerFinance' style='width:460px;'>
        <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
         <div class='row'>
            <span class='label'>Procurement/ Contract Title:</span>
            <span class='formfield'>
            <form:input path="invoiceContractTitle" cssClass="proposalConfigDrpdwn" maxlength="120" id="invoiceContractTitle"      onkeyup="removeNonRequiredCharacter(this)"/></span>
             <span class="error"></span>
         </div>
        <c:if test="${org_type eq 'city_org' or org_type eq 'provider_org'}">
        <div class='row'>
            <span class='label'>Agency:</span>
            <span class='formfield'>
				  <form:select name="agency" path="agency" class="widthFull"  > 
				   <form:option id="All NYC Agencies" value="">All NYC Agencies</form:option>
						<c:forEach items="${aoAgencyList}" var="agencyDetail">
						<form:option value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</form:option>
						</c:forEach>
					  </form:select>
            </span>
            <span class="error"></span>
        </div>
        </c:if>
          <div class='row'>
          <c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
            <span class='label'>Program Name:</span>
            <span class='formfield'>
                   <form:select id="programName" path="invoiceProgramName" class="widthFull"    >
                   <form:option id="Program Name Filter" value=""></form:option>
						<c:forEach items="${programNameList}" var="programObject">
						<form:option title='${programObject.programName}' value="${programObject.programId}">${programObject.programName}
						</form:option>
						</c:forEach>
					  </form:select>
            </span>
             <span class="error"></span>
        </div>
        
        <c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
        <div class='row'>
            <span class='label'>Provider:</span>
            <span class='formfield'>
            <form:input path="invoiceProvider" cssClass="proposalConfigDrpdwn" id="invoiceProvider" maxlength="50"      onkeyup="removeNonRequiredCharacter(this)"/></span>
             <span class="error"></span>
        </div>
        </c:if>
        
        
        
        <div class='row'>
           <span class='label'>CT#:</span>
            <span class='formfield'>
             <form:input path="invoiceCtId" cssClass="proposalConfigDrpdwn" id="invoiceCtId" maxlength="32"  onkeyup="removeNonRequiredCharacter(this)"
             title="The CT# is the contract registration number issued by the City’s Financial Management System (FMS). You can use this number to search for additional information in the FMS’ Payee Information Portal – https://nyc.gov/pip"/>
             </span>
         <span class="error"></span>
        </div>     
        
        <div class='row'>
            <span class='label'>Fiscal Year:</span>
             <span class='formfield'>
		    	 <form:select path="invoiceFiscalYearId" class="widthFull" id="invoiceFiscalYearId"    >
					<form:option id="All Fiscal Year" value=""></form:option>
			     		<c:forEach items="${aoFiscalInformation}" var="fiscalObject">
				    		<form:option value="${fiscalObject.fiscalYearId}">${fiscalObject.fiscalYearId}
					    	</form:option>
						</c:forEach>
				</form:select>
				</span>
	     <span class="error"></span>
        </div>
        
       <div class='row'>
            <span class='label'>Invoice Number:</span>
             <span class='formfield'>
              <form:input path="invoiceNumber" cssClass="proposalConfigDrpdwn" id="invoiceNumber" maxlength="9"      onkeyup="removeNonRequiredCharacter(this)"/></span>
              <span class="error"></span>
        </div>
        
        <div class='row'>
            <span class='label'>Invoice Value from($):</span>
            <span class='formfield'>
             <form:input path="invoiceValueFrom" validate="number"  id="invoiceValueFrom" maxlength="19"  cssStyle="width:43.5%"  /><b> to:</b> <form:input path="invoiceValueTo" validate="number" id="invoiceValueTo"  maxlength="19"  cssStyle="width:43.5%"  />
               </span>
        <span class="error"></span>
        </div>
     
	   <div class='row'>
           <span class='label'>Status:</span>
            <span class='formfield' id="formfieldInvoice">
            
			 <c:forEach items="${aoInvoiceStatus}" var="invoiceStatusObject">			 
				<c:choose>
				<c:when test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
					 <c:if test="${invoiceStatusObject.statusId eq '71'or invoiceStatusObject.statusId eq '72'or invoiceStatusObject.statusId eq '73'}">
						<span class='leftColumn'>
			    			<span>
						    <form:checkbox path="invoiceStatusList" id="${invoiceStatusObject.statusId}" 
									name="stausFilter" value="${invoiceStatusObject.statusId}"  checked="${(fn:contains(loInvoiceFilterBean,invoiceStatusObject.statusId) || (lbFirstLoad))  ? 'checked' : ''}"/>
									<label for="${invoiceStatusObject.statusId}" name="${invoiceStatusObject.status}">${invoiceStatusObject.status}</label>
				    		 </span>
					   	</span>
					 </c:if>
					 <c:if test="${invoiceStatusObject.statusId eq '74'or invoiceStatusObject.statusId eq '75'}">
				     		<span class='rightColumn' style='float:none'>
					          <span>
						          <form:checkbox path="invoiceStatusList" id="${invoiceStatusObject.statusId}" 
									name="stausFilter" value="${invoiceStatusObject.statusId}" checked="${(fn:contains(loInvoiceFilterBean,invoiceStatusObject.statusId) || (lbFirstLoad))  ? 'checked' : ''}"/>									
									<label for="${invoiceStatusObject.statusId}" name="${invoiceStatusObject.status}">${invoiceStatusObject.status}</label>
					   	    	</span>
						    </span>
					 </c:if>
     			  </c:when>
			 <c:otherwise>
				   <c:if test="${invoiceStatusObject.statusId eq '70'or invoiceStatusObject.statusId eq '71'or invoiceStatusObject.statusId eq '72'}">
						<span class='leftColumn'>
					    	<span>
						           <form:checkbox path="invoiceStatusList" id="${invoiceStatusObject.statusId}" 
									name="stausFilter" value="${invoiceStatusObject.statusId}"  checked="${(fn:contains(loInvoiceFilterBean,invoiceStatusObject.statusId) || (lbFirstLoad)) ? 'checked' : ''}"/>
									<label for="${invoiceStatusObject.statusId}" name="${invoiceStatusObject.status}">${invoiceStatusObject.status}</label>
							 </span>
						</span>
					</c:if>
				    <c:if test="${invoiceStatusObject.statusId eq '73'or invoiceStatusObject.statusId eq '74'or invoiceStatusObject.statusId eq '75'}">
				    		<span class='rightColumn floatNone'>
					    	   <span>
						           <form:checkbox path="invoiceStatusList" id="${invoiceStatusObject.statusId}" 
									name="stausFilter" value="${invoiceStatusObject.statusId}"  checked="${(fn:contains(loInvoiceFilterBean,invoiceStatusObject.statusId) || (lbFirstLoad))  ? 'checked' : ''}" />
									<label for="${invoiceStatusObject.statusId}" name="${invoiceStatusObject.status}">${invoiceStatusObject.status}</label>
								</span>
						    </span>
					 </c:if>
			 </c:otherwise>
			</c:choose>
		</c:forEach>
	  </span>
	  </div>
       
		 <div class='row'>			
        <span class='label'>Date Submitted from:</span>
            <span class='formfield'>
            	<span class='floatLft'>
	            <form:input path="dateSubmittedFrom" id="dateSubmittedFrom" validate="calender" cssClass="datepicker"     maxlength="10"/>
	             <img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateSubmittedFrom',event,'mmddyyyy');return false;"> 
	            &nbsp;&nbsp; 
	            </span>
					<span id="dateSubmittedFromError" class="error clear"></span>
	             <b>to:</b>
	             <span>
				  <form:input path="dateSubmittedTo" id="dateSubmittedTo" validate="calender" cssClass="datepicker"     maxlength="10"/>
				<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateSubmittedTo',event,'mmddyyyy');return false;">
            	</span>
            	<span id="dateSubmittedToError" class="error clear"></span>
            	<span class="error" id="dateCheck2"></span>
            </span>
             
             </div>
          
           <div class='row'>	   
        <span class='label'>Date Approved from:</span>
            <span class='formfield'>
            	<span class='floatLft'>
            		 <form:input path="dateApprovedFrom" id="dateApprovedFrom" validate="calender" cssClass="datepicker"      maxlength="10"/>
		             <img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateApprovedFrom',event,'mmddyyyy');return false;">  
					  &nbsp;&nbsp;
					  </span>
					<span id="dateApprovedFromError" class="error clear"></span>
					 <b>to:</b>
					 <span>
					  <form:input path="dateApprovedTo" id="dateApprovedTo" validate="calender" cssClass="datepicker"     maxlength="10"/>
					<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateApprovedTo',event,'mmddyyyy');return false;">
            	</span>
            	<span id="dateApprovedToError" class="error clear"></span>
            	<span class="error" id="dateCheck"></span>
            </span>
              </div>
        <div class="buttonholder">
            <input type="button" class='graybtutton' value="Set to Default Filters" onclick="settoDefaultFilters()" onmousedown="settoDefaultFilters()"/>
            <input  type="button" value="Filter" onclick="submitListInvoice()"/>
        </div> 
    </div>
     
    <%-- Popup for Filter Task Ends --%>
</div>
<%--Filter and Reassign section starts --%>

<div class='clear'></div>
<%-- Form Data Starts --%>
<div class='tabularWrapper gridfixedHeight invoiceListDiv' style='min-height:700px !important'>
<st:table 
	objectName="invoiceList" cssClass="heading"
	alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
		<%--Made for defect id 6248 release 3.3.0 --%>
	<c:if test="${(org_type eq 'city_org') or (org_type eq 'provider_org')}">
	<st:property headingName="Agency" columnName="invoiceAgencyId"
		size="10%"  sortType="invoiceAgencyId" >
	</st:property>
	</c:if>
		<%--End for defect id 6248 release 3.3.0 --%>
	<st:property headingName="Invoice Number"
		columnName="invoiceNumber" size="15%" sortType="invoiceNumber">
	</st:property>
		<%--Start for defect id 6248 release 3.3.0 --%>
	<c:if test="${org_type eq 'city_org'}">
	<st:property headingName="Provider" columnName="invoiceProvider"
		size="30%"  sortType="invoiceProvider" >
		<%--Start : Added in R5 --%>
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
		<%--End : Added in R5 --%>
	</st:property>
	</c:if>
		<%--End for defect id 6248 release 3.3.0 --%>
	<st:property headingName="Date Submitted" columnName="invoiceDateSubmitted"
		size="15%" sortType="invoiceDateSubmitted">
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
	</st:property>
	<st:property headingName="Date Approved" columnName="invoiceDateApproved" size="15%"
		 sortType="invoiceDateApproved">
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
	</st:property>
	<%--Start for defect id 6248 release 3.3.0 --%>
	<c:if test="${org_type eq 'agency_org'}">
	<st:property headingName="Provider" columnName="invoiceProvider"
		size="30%"  sortType="invoiceProvider" >
		<%--Start : Added in R5 --%>
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
		<%--End : Added in R5 --%>
	</st:property>
	</c:if>
	<%--Start for Enhancement  id 6461  release 3.4.0 --%>
	<st:property headingName="CT#" columnName="invoiceCtId"
		size="15%"  sortType="invoiceCtId" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
	</st:property>
	<%--End for Enhancement  id 6461  release 3.4.0 --%>
		<%--End for defect id 6248 release 3.3.0 --%>
	<st:property headingName="Value($)" columnName="invoiceValue"
		size="15%"  sortType="invoiceValue" >
	<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
	</st:property>
	<st:property headingName="Status" columnName="invoiceStatus"
		size="20%"  sortType="invoiceStatus" >
	</st:property>
	<st:property headingName="Action" columnName="invoiceAction" 
	 size="20%" >
	<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.InvoiceActionExtension" />
	</st:property>
</st:table>
	<c:if test="${aoInvoiceCount eq 0}">
		<div class="noRecordCityInvoiceDiv" id="noRecordCityInvoiceDiv"
			style='border: 1px solid #ccc; border-top: 0; padding: 6px;'><i>No
		Records Found</i></div>
	</c:if>
<form:hidden path="firstSort"/>
<form:hidden path="secondSort"/>
<form:hidden path="firstSortType"/>
<form:hidden path="secondSortType"/>
<form:hidden path="firstSortDate"/>	
<form:hidden path="sortColumnName"/>	
	
	<div class='floatLft'><span> Invoices: <label>${aoInvoiceCount}</label></span></div>
</div>
<p>&nbsp;</p>
 </div>
 
</div>
</form:form>  

	<div class="overlay"></div>
	<div class="alert-box-link-to-vault" style='width:41%'>
	<div id="overlayContent"></div>
	 
	</div>
	 <div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
	
</d:content>