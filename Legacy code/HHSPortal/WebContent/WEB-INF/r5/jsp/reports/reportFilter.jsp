<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/tld/custom-birt.tld" prefix="cb"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="/WEB-INF/tld/custom-birt.tld" prefix="cb"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<link rel="stylesheet" href="../css/report.css" type="text/css"></link>
<style type="text/css">
.reportLink{
	color: #5077AA;
    font-size: 1.42em;
    font-weight: bold;
    padding: 21px 0px 8px;
    float: left;
}
.autocomplete{
	width:284px !important;
}
</style>	
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<portlet:defineObjects/>

<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>




<%--start report header --%>
<c:if test="${!(jspName eq 'detailedReport'  )}">
 	
	<div class="appnavbar" id="nyc_app_sections">
	<ul class="roundcorners">
		<li style="width: 158px;height:17px !important;text-align:center;padding-left: 9px !important;padding-right: 9px !important" id="section_reportfinancials" ><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_report&_nfls=false&app_menu_name=BIRT&removeNavigator=true&reportType=financials" title=" Financial Reports">Financial Reports</a></li>
		<li style="width: 158px;height:17px !important;text-align:center;padding-left: 9px !important;padding-right: 9px !important" id="section_reportprocurement" ><a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_report&_nfls=false&app_menu_name=BIRT&removeNavigator=true&reportType=procurement" title="Procurement Reports">Procurement Reports</a></li>
	</ul>
 </div>
 </c:if>
  <div class='clear'></div>
 	<c:if test="${jspName eq 'detailedReport'  }">
 	<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_report&_nfls=false&app_menu_name=BIRT&removeNavigator=true&reportType=financials" title="Reports"><div class="reportLink">Reports</div></a> <div class="reportLink" style="color:#333;"> > ${loReportValue} Report</div>
 	<div class="clear"></div>
 	</c:if>
<div class="">

<div class="formcontainer paymentFormWrapper dashboardDropDown">
<c:if test="${(jspName eq 'detailedReport'  )}">
<c:if test="${requestReportType eq 'financials'}">
    <span class="display_summary">All data displayed is based on the contract financial information available within the HHS Accelerator application.</span>
</c:if>
<c:if test="${requestReportType eq 'procurement'}">
    <span class="display_summary">All data displayed is based on procurement proposal information available within the HHS Accelerator application.</span>
</c:if>
</c:if>



<c:if test="${!(jspName eq 'detailedReport'  )}">
<c:if test="${requestReportType eq 'financials'}">
    <span class="display_summary">All data displayed is based on the contract financial information available within the HHS Accelerator application.</span>
</c:if>
<c:if test="${requestReportType eq 'procurement'}">
    <span class="display_summary">The Proposal Report displays procurement data from all the Fiscal Years since the launch of HHS Accelerator Procurements.</span>
</c:if>
</c:if>
</div>
<div class="formcontainer paymentFormWrapper dashboardDropDown">
   <div class="left">
   <span class="drop_dwn"> 
   <c:if test="${!(jspName eq 'detailedReport'  )}">
   <c:if test="${requestReportType eq 'financials'}">
   <label for="year" class="report_dashboard">Fiscal Year:</label>&nbsp;&nbsp;
				<form:select path="fyYear" cssClass="taskreassign"  id="fyYearId" onchange="viewDetailedReport();">
						<c:forEach items="${loFiscalInformation}" var="fiscalObject">
						
						<c:choose>
						<c:when test="${fiscalObject eq fyYearId}">
						<form:option value="${fiscalObject}"   selected="selected">${fiscalObject}</form:option>
						</c:when>
						<c:otherwise>
						<form:option value="${fiscalObject}"   >${fiscalObject}</form:option>
						</c:otherwise>
						</c:choose>
						
						
						</c:forEach>
				</form:select>
		</c:if>		
	 </c:if>			
</span>
   </div>
   <div class="drop_down_report">
    <span class="drop_dwn" > <label for="year" class="report_dashboard">Jump to:</label>  
    <form:select path="reportId" cssClass="" id="reportId" onchange="jumpToDetailedReport();">
						<form:option id="" value="" selected="selected">Reports Homepage</form:option>
						<c:forEach items="${reportListOptions}" var="reportListObject">
						<form:option value="${reportListObject.reportId}">${reportListObject.reportValue}
						</form:option>
						</c:forEach>
	</form:select>
   </span>
   </div>
</div>
    <div class="hr"></div>
</div>
<%-- end report header--%>
<%--Filter and Reassign section starts --%>
<c:if test="${jspName eq 'detailedReport'  }">
<div class="taskButtons nowrap" style="z-index: 99">
    <span class='floatLft'>
    <input type="button" value="Filter Report" class="filterDocument floatLft marginReset"  onclick="setVisibility('documentValuePop', 'inline');" />
</span>
    <%--  Popup for Filter Task Starts  --%>
    <div id="documentValuePop" class='formcontainerFinance' style='width:460px;'>
        <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
    <c:if test="${requestReportType eq 'financials'}">
        <div class='row'>
            <span class='label'>Fiscal Year:</span>
            <span class='formfield'>
                <form:select path="fyYear"  cssClass="defaultFilter" id="fyYearId" >
						<c:forEach items="${loFiscalInformation}" var="fiscalObject">
						<form:option value="${fiscalObject}">${fiscalObject}</form:option>
						</c:forEach>
				</form:select>
            </span>
        </div>
	</c:if>
<c:if test="${!(requestReportType eq 'procurement' and org_type eq 'agency_org')}"> 
	<div class='row'>
	            <span class='label'>Agency:</span>
	            <span class='formfield'>
	                  <form:select path="agencyId" cssClass="widthFull defaultFilter" id="agency">
	                  	<form:option id="All NYC Agencies" value="">All NYC Agencies</form:option>
	                  	<c:forEach items="${agencyDetails}" var="agencyDetail">
	                  		<form:option title="${agencyDetail['AGENCY_NAME']}"  value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</form:option>
		                </c:forEach>
	                  </form:select>
	            </span>
	        </div>
</c:if>		 

<c:if test="${(requestReportType eq 'procurement' and org_type eq 'agency_org')}"> 
<input type="hidden" id="agency" value="${user_organization}"/>
</c:if>       
	 <c:if test="${requestReportType eq 'procurement'}">
        <div class='row'>
            <span class='label'>Procurement Title:</span>
            <span class='formfield'>
            	<form:input path="procurementTitle" id="procurementTitle" cssClass="proposalConfigDrpdwn defaultFilterTextBox" maxlength="120"  onkeyup="removeNonRequiredCharacter(this)" />
            </span>
            <span class="error"></span>
        </div>
        <div class='row'>
            <span class='label'>Competition Pool:</span>
            <span class='formfield'>
            	<form:input path="compitionPool" id="compitionPool" cssClass="proposalConfigDrpdwn defaultFilterTextBox" disabled="${empty ProposalDetailsBean.competitionPool}" maxlength="120" onkeyup="removeNonRequiredCharacter(this)" />
            </span>
            <span class="error"></span>
        </div>
	</c:if>
           <div class='row'>
        <c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
            <span class='label'>Program:</span>
            <span class='formfield'>
					<!-- start -->
					<div class="ddcombo" id="box1">
						<table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable"><tbody>
							<tr>
								<td class="ddcombo_td1">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
									
										<form:input cssClass="ddcombo_input1 ddcombo_input defaultFilterTextBox" id="typeheadbox" name="typeheadbox" value="" 
										path="programName"  style="width:256px;" ddcombo_autocomplete="off" ></form:input>
									</div>
								</td>
								<td valign="top" align="left" class="ddcombo_td2" id="combotable_button"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td>
							</tr></tbody>
						</table>
					</div>
					<div style="display:none; position: absolute; width: 271px;" class="ddcombo_results" id="optionsBox" >
						 <ul id= "dropdownul" style="max-height: 180px; overflow: auto; ">
							 <c:forEach items="${programNameList}" var="programObject">
						         <li class="ddcombo_event data" key="${programObject.programName}" id="li_${programObject.programId}" value="${programObject.programId}">${programObject.programName}</li>
						    </c:forEach>
						</ul>
					</div>
					<!-- end -->
            </span>
        </div>
        <c:if test="${requestReportType eq 'financials'}">
        <c:if test="${org_type ne 'provider_org' }">
		 <div class='row'>
	            <span class='label'>Provider:</span>
	            <span class='formfield'>
					<form:input path="provider" id="provider" cssClass="proposalConfigDrpdwn defaultFilterTextBox" maxlength="100"  onkeyup="removeNonRequiredCharacter(this)"/>
	            </span>
	            <span class="error"></span>
	        </div>
	       </c:if>
        </c:if>
        <c:if test="${requestReportType eq 'procurement'}">
        	<div class='row'>
            <span class='label'>Submitted Date Range From:</span>
            <span class='formfield'>
            	<span class=floatLft>
           			<form:input path="submitDateFrom" id="submitDateFromId" cssClass="datepicker defaultFilterTextBox" validate="calender" maxlength="10"></form:input>
            		<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('submitDateFromId',event,'mmddyyyy');return false;">  
					&nbsp;&nbsp; 
					</span>
					<span id="submitDateFromError" class="error clear"></span>
							<b>To: </b> 
				<span>
			 		<form:input path="submitDateTo" id="submitDateToId" cssClass="datepicker defaultFilterTextBox" validate="calender" maxlength="10"></form:input>
					<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('submitDateToId',event,'mmddyyyy');return false;">
            	</span>
            	 <span id="submitDateToIdError" class="error clear"></span>
            	 <span class="error clear" id="dateCheck"></span>
            	</span>
        </div>
        
        </c:if>
        <c:if test="${requestReportType eq 'financials'}">
         <div class='row'>
            <span class='label'>CT#:</span>
            <span class='formfield'>
				 <form:input path="ctNumber" id="ctId" cssClass="proposalConfigDrpdwn defaultFilterTextBox" maxlength="32" onkeyup="removeNonRequiredCharacter(this)"/>
            </span>
            <span class='error'></span>
        </div>
       
		<div class='row'>
            <span class='label'>Contract Title:</span>
            <span class='formfield'>
            	<form:input path="contractTitle" id="contractTitle" cssClass="proposalConfigDrpdwn defaultFilterTextBox" maxlength="120"  onkeyup="removeNonRequiredCharacter(this)" />
            </span>
            <span class="error"></span>
        </div>
	    </c:if>   
        <div class="buttonholderReport buttonholder">
            <input type="button" class='graybtutton filterButton' value="Clear Filters" onmousedown="settoDefaultFilters();" onclick="settoDefaultFilters()" />
            <input type="button" class='filterButton' value="Filter" onclick="viewDetailedReport()" />
        </div> 
        
    </div>
    <form:hidden path="providerIdReport"  id="providerIdReport"/>
    <%-- Popup for Filter Task Ends --%>
    	</div>
<%--Filter and Reassign section ends --%>
</c:if>
<input type="hidden" id="orgType" value="${org_type}"/>
<input type="hidden" value="${requestReportType}" id="requestReportType" />
<input type="hidden" value="${Agency_ID}" id="Agency_ID" />
<script>
$(document).ready(function() {
	if($("#compitionPool").val() != "" || '${isCompititionPoolEnabled}' == 'false'){
		$("#compitionPool").prop('disabled', false);
	}else{
		$("#compitionPool").prop('disabled', true);
	}
});
</script>