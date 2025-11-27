<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.BudgetList"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/budgetList.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>


<portlet:defineObjects/>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<input type="hidden" id="orgType" value="${org_type}"/>
<portlet:renderURL var="duplicateRender" escapeXml="false">
<portlet:param name="duplicate_render" value="duplicateRender" />
<portlet:param name="action" value="budgetListAction"/>
</portlet:renderURL>
<input type="hidden" name="duplicateRender" id="duplicateRender"  value="${duplicateRender}"/>
	<%--code updation for R4 starts--%>
<c:set var="budgetListSection"><%=HHSComponentMappingConstant.BUDGET_LIST%></c:set>

<d:content section="${budgetListSection}" authorize="" isReadOnly="false">
	<%--code updation for R4 ends--%>
<jsp:include page="financeHeader.jsp"></jsp:include>
<div class='clear'></div>

<portlet:actionURL var="navigateToContractBudget" escapeXml="false">
<portlet:param name="submit_action" value="viewContractBudget"/>
<portlet:param name="action" value="budgetListAction"/>
</portlet:actionURL>
<portlet:actionURL var="navigateToContractInvoiceURL" escapeXml="false">
<portlet:param name="launchInvoice" value="contractInvoiceScreen"/>
<portlet:param name="action" value="invoiceListAction"/>
</portlet:actionURL>
<input type='hidden' value="${navigateToContractInvoiceURL}" id="navigateToContractInvoiceURL"/>
<portlet:actionURL
	var="addBudgetFinanceUrl" escapeXml="false">
	<portlet:param name="action" value="budgetListAction" />
	<portlet:param name="budgetAction" value="budgetFinacialsMap" />
</portlet:actionURL>

<portlet:renderURL var="budgetListUrl" escapeXml="false"><portlet:param name='action' value='budgetListAction' /></portlet:renderURL>

<portlet:resourceURL var="getEpinListResourceUrl" id="getEpinListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getEpinListResourceUrl}' id='hiddengetEpinListResourceUrl'/>
<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>

<portlet:resourceURL var="getContractNoListResourceUrl" id="getContractNoListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getContractNoListResourceUrl}' id='hiddengetContractNoListResourceUrl'/>

<portlet:resourceURL var='selectOverayContent' id='selectOverayContent' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${selectOverayContent}' id='hiddenSelectOverayContentUrl'/>
<input type = 'hidden' value='${budgetListUrl}' id='budgetListUrl'/>
<input type = 'hidden' value='${navigateToContractBudget}' id='navigateToContractBudgetURL'/>
	<%--code updation for R4 starts--%>
<portlet:resourceURL var="BudgetCustomizedTabOverlay" id="BudgetCustomizedTabOverlay" escapeXml="false">
</portlet:resourceURL>
<input type="hidden" name="BudgetCustomizedTabOverlay" id="BudgetCustomizedTabOverlay" value="${BudgetCustomizedTabOverlay}"/>
	<%--code updation for R4 ends--%>
<%--Start : Added in R5 --%>
<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromList'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<%--End : Added in R5 --%>
<script type="text/javascript">
		showSelected('budgetListAction','');
		showHeaderSelected('header_financials');
		// Javascript for filter popup
		function setVisibility(id, visibility) {
		callBackInWindow("closePopUp");
	      if ($("#" + id).is(":visible")) {
	            document.forms[0].reset();
	      }
	      $("#" + id).toggle();
	      disableProgramDropDown();
		}
	</script>

<div class='clear'></div>

<%-- Container Starts --%>
<form:form id="financebudgetform"
	action="${addBudgetFinanceUrl}" method="post" name="financebudgetform"
	commandName="BudgetList" onkeydown="if (event.keyCode == 13) {submitListBudget();}">
<%-- ------------Sorting parameters added to retain sorting on pagination of earlier page------------------- --%>
<form:hidden path="firstSort"/>
<form:hidden path="secondSort"/>
<form:hidden path="firstSortType"/>
<form:hidden path="secondSortType"/>
<form:hidden path="firstSortDate"/>
<form:hidden path="secondSortDate"/>
<form:hidden path="sortColumnName"/>

<%--Added in R7 for Flagged Contracts Pagination--%>
 <form:hidden path="isApprovedModificationChecked"/>  
<%--R7 End --%>

<%-- ------Sorting Parameters Ends-------- --%>
<input type="hidden" id="orgType" value="${org_type}"/>	
<input type = 'hidden' value='' name="providerId" id='providerId'/>
<div id='tabs-container'>
<h2>Budget List</h2>
<div id="helpIcon" class="iconQuestion">
   <c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
    <d:content section="${helpIconProvider}">
     <div id="helpIcon" class="marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
          <input type="hidden" id="screenName" value="Budget List" name="screenName"/>
   </d:content> 
  </div>
<div class='hr'></div>

<%
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionBudgetMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionBudgetMessage");
	}
	if(null!=request.getAttribute("transactionBudgetStatus") && "passed".equalsIgnoreCase((String)request.getAttribute("transactionBudgetStatus"))){%>
	<div id="transactionStatusDiv" class="passed" style="display: block"><%=lsTransactionMsg%>
	</div>
	<%}else if(( null != request.getAttribute("transactionBudgetStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionBudgetStatus"))){%>
	<div id="transactionStatusDiv" class="failed" style="display: block"><%=lsTransactionMsg%>
	</div>
	<%}
	session.removeAttribute("transactionBudgetMessage");%>
	
<div class="failed" id="errorMsgForInvoiceList">${IsSubmitInvoiceErrorMsg}</div>
  <%
    String lsBudgetMessage="";
    if(null!=session.getAttribute("budgetMessageType")){
	lsBudgetMessage=(String)session.getAttribute("budgetMessageType");
  %>
  <div class="failed" id="errorMsgBudgetList" style="display: block"><%=lsBudgetMessage%>
  </div>
  <%}
    session.removeAttribute("budgetMessageType");%>
<div class="passed" id="successMsgBudgetList">${messageBudgetList}</div> 

<c:choose>
  <c:when test="${org_type eq 'provider_org'}">
    <p>Listed below are the Budgets for your organization. A default filter has been applied.</p>
  </c:when>
  <c:otherwise>
    <p>Listed below are the Budgets for your Agency. A default filter has been applied.</p>
  </c:otherwise>
</c:choose>
<c:set value="${lbFirstLoad}"  var="lbFirstLoad" />
<%--Filter and Reassign section starts --%>
<div class="taskButtons nowrap">
    <span class='floatLft'><input type="button" value="Filter Budgets" class="filterDocument"  onclick="setVisibility('documentValuePop', 'inline');" /></span>
	<span class='count'>
		 <span>Budgets:<label>${aoBudgetListLabel}</label></span>
	</span>
 
    <%-- Popup for Filter Task Starts --%>
    <div id="documentValuePop" class='formcontainerFinance' style='width:478px !important'>
        <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
        <div class='row'>    
            <span class='label'>Budget Type:</span>
            <span class='formfield'>
				<span class='rightColumn'>
					 <span><form:checkbox path="budgetTypeList" id="chkModification" name="Budget Modification" value="Budget Modification" checked="${(lbFirstLoad)||fn:contains(BudgetListHome.budgetTypeList,'Budget Modification')?'checked':''}" /><label for="chkModification" class="budgetModification">Modification</label></span>
				     <span><form:checkbox path="budgetTypeList" id="chkRegistered1"  name="Budget Update" value="Budget Update" checked="${(lbFirstLoad)||fn:contains(BudgetListHome.budgetTypeList,'Budget Update')?'checked':''}" /><label for="chkRegistered1" class="BudgetUpdate">Update</label></span>
				</span>
				<span class='leftColumn'>
				     <span><form:checkbox path="budgetTypeList" id="chkAmendment" name="Budget Amendment" value="Budget Amendment" checked="${(lbFirstLoad)||fn:contains(BudgetListHome.budgetTypeList,'Budget Amendment')?'checked':''}" /><label for="chkAmendment" class="BudgetAmendment" >Amendment</label></span>
					 <span><form:checkbox path="budgetTypeList" id="chkBase" name="Contract Budget" value="Contract Budget" checked="${(lbFirstLoad)||fn:contains(BudgetListHome.budgetTypeList,'Contract Budget')?'checked':''}" /><label for="chkBase" class="ContractBudget" >Contract</label></span>
					 
				</span>
            </span>
        </div>
        <div class='row'>
            <span class='label'>Procurement/ Contract Title:</span>
            <span class='formfield'>
				<form:input path="contractTitle" cssClass="proposalConfigDrpdwn" id="contractTitle" name="contractTitle" maxlength="120"  onkeyup="removeNonRequiredCharacter(this)"/>
            </span>
            <span class='error'></span>
        </div>
         <c:if test="${org_type eq 'city_org' or org_type eq 'provider_org'}">
        <div class='row'>
            <span class='label'>Agency:</span>
            <span class='formfield'>
				  <form:select name="agency" cssClass="widthFull" path="agencyName" id="agency">
				   <form:option id="All NYC Agencies" value="">All NYC Agencies</form:option>
						<c:forEach items="${aoAgencyList}" var="agencyDetail">
						<form:option value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</form:option>
						</c:forEach>
				</form:select>
            </span>
        </div>
        </c:if>
        <div class='row'>
        <c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
            <span class='label'>Program Name:</span>
            <span class='formfield'>
				 <form:select name="programNameFilter" cssClass="widthFull" path="programName" id="programName">
                   <form:option id="Program Name Filter" value=""></form:option>
						<c:forEach items="${programNameList}" var="programObject">
						<form:option title='${programObject.programName}' value="${programObject.programId}">${programObject.programName}
						</form:option>
						</c:forEach>
				</form:select>
            </span>
        </div>
       
         
         <c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
           <div class='row'>
            <span class='label'>Provider:</span>
            <span class='formfield'>
				 <form:input path="providerName" cssClass="proposalConfigDrpdwn" id="providerName" name="providerFilter" maxlength="50"  onkeyup="removeNonRequiredCharacter(this)"/>
			</span>
			<span class='error'></span>
           </div>
        </c:if>
		<div class='row'>
            <span class='label'>CT#:</span>
            <span class='formfield'>
				<form:input path="ctId" cssClass="proposalConfigDrpdwn" id="ctId" maxlength="32" onkeyup="removeNonRequiredCharacter(this)"/>
            </span>
            <span class='error'></span>
        </div>
		
		<c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
		<div class='row'>
	            <span class='label'>Award E-PIN:</span>
	            <span class='formfield'>
					  <form:input path="awardEpin" cssClass="proposalConfigDrpdwn" id="awardEpin" validate="alphaNumericEpin"  />
				</span>
	        </div>
        </c:if>
        
		<div class='row'>
            <span class='label'>Budget Value from($) :</span>
            <span class='formfield'>
                <form:input path="budgetValueFrom" cssStyle="width:43.5%" validate="number" /> To: <form:input path="budgetValueTo" cssStyle="width:44%" validate="number" />
			</span>
        </div>
		<div class='row'>
            <span class='label'>Status:</span>
			   <span class='formfield'>

				<c:forEach items="${aoBudgetStatus}" var="statusObject">		
					<c:choose>
					  <c:when test="${statusObject.statusId eq '82'or statusObject.statusId eq '83' or statusObject.statusId eq '84'or statusObject.statusId eq '85' or statusObject.statusId eq '86'}">
					   <c:if test="${statusObject.statusId eq '82'or statusObject.statusId eq '83' or statusObject.statusId eq '84'or statusObject.statusId eq '85'}">
							<span class='leftColumn' style='width:48% !important'>
							  <span>
						 	 	<form:checkbox path="budgetStatusList" id="${statusObject.statusId}" name="stausFilter" value="${statusObject.statusId}" checked="${(fn:contains(BudgetList.budgetStatusList,statusObject.statusId)||fn:contains(BudgetListHome.budgetStatusList,statusObject.statusId)||(lbFirstLoad))?'checked':''}" />
						 		 <label for="${statusObject.statusId}" name="${statusObject.status}">${statusObject.status}</label>
						 		</span>
					    	</span>
					   </c:if>
					   <c:if test="${statusObject.statusId eq '86'}">
					   	   <span class='leftColumn' style='width:48% !important'>
					   	    <span>
					      	 <form:checkbox path="budgetStatusList" id="${statusObject.statusId}" name="stausFilter" value="${statusObject.statusId}" checked="${(fn:contains(BudgetList.budgetStatusList,'86')||fn:contains(BudgetListHome.budgetStatusList,'86')||(lbFirstLoad))?'checked':''}" />
						 	 <label for="${statusObject.statusId}" name="${statusObject.status}">${statusObject.status}</label>
						 	</span>
					      </span> 
					   </c:if>
					  </c:when>
					 
				     <c:otherwise>
				      <c:if test="${statusObject.statusId ne '106'}">
					    <span class='leftColumn' style='width:48% !important'>
					    	<span>
					   		 <form:checkbox path="budgetStatusList" id="unSelectedCheckBox${statusObject.statusId}" name="stausFilter" value="${statusObject.statusId}"/>
					   		 <label for="unSelectedCheckBox${statusObject.statusId}" name="${statusObject.status}">${statusObject.status}</label>
					   		</span>
				        </span>
				      </c:if>
				     </c:otherwise>
			       </c:choose>
			   </c:forEach>
			   </span>
				</span>
        </div>
        
        
		<div class='row'>
            <span class='label'>Fiscal Year:</span>
            <span class='formfield'>
                 <form:select path="fiscalYearId" cssClass="widthFull" id="fiscalYearFilter">
						<form:option id="All Fiscal Year" value=""></form:option>
						<c:forEach items="${aoFiscalInformation}" var="fiscalObject">
						<form:option value="${fiscalObject.fiscalYearId}">${fiscalObject.fiscalYearId}
						</form:option>
						</c:forEach>
				</form:select>
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
		<!-- Added in R7 -->
		<!-- Updated for Change Logs -->
		<div class='row'>
		  <span class='label'></span>
            <span class='formfield'>
            <span class="leftColumn">
             <span><form:checkbox path="budgetTypeList" id="chkApprovedModif"  name="Approved Modification" value="" /><label for="chkApprovedModif">&nbsp;&nbsp;Show Approved Modifications</label></span>
             </span>
            </span>
		</div>
		<!-- R7 End -->
        <div class="buttonholder">
            <input type="button" class='graybtutton' value="Set to Default Filters" onclick="setDefaultToFilters();"  onmousedown="setDefaultToFilters();"/>
            <input type="button" value="Filter" name="Filter" onclick="submitListBudget()"/>
        </div> 
    </div>
    <%-- Popup for Filter Task Ends --%>
 
      
   
	</div>
<%--Filter and Reassign section ends --%>
 



<div class='clear'></div>

	<%--Start changes  for defect id 6248 release 3.3.0 --%>
<div class='tabularWrapper budgetListDiv' style='min-height:700px !important'>

	<st:table 
	objectName="financialsBudgetList" cssClass="heading"
	alternateCss1="evenRows" alternateCss2="oddRows" pageSize="${allowedObjectCount}">

	<c:if test="${org_type eq 'city_org'}">
	<st:property headingName="Agency" 
		columnName="agencyId" size="6%" sortType="agencyId" >
			</st:property>
	</c:if>
	<st:property headingName="Procurement/Contract Title"
		columnName="contractTitle" size="22%" align="center" sortType="contractTitle" >
	<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.BudgetActionFinancialExtension" />
	</st:property>
	<c:choose>
		<c:when test="${org_type eq 'provider_org'}">
			<st:property headingName="Agency" 
		columnName="agencyId" size="14%" sortType="agencyId" >
			</st:property>
		</c:when>
		<c:otherwise>
			<st:property headingName="Provider" 
		columnName="providerName" size="15%" sortType="providerName" >
			<%--Start : Added in R5 --%>
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.BudgetActionFinancialExtension" />
			<%--End : Added in R5 --%>
			</st:property>
	 </c:otherwise>
	</c:choose>
	<c:if test="${(org_type eq 'provider_org') or (org_type eq 'city_org')}">
	<st:property headingName="Fiscal Year" 
		columnName="fiscalYear" size="12%" sortType="fiscalYear" >
	</st:property>
	<st:property headingName="CT#" 
		columnName="ctId" size="15%" sortType="ctId" >
	</st:property>
	</c:if>
	<c:if test="${(org_type eq 'agency_org')}">
	<st:property headingName="CT#" 
		columnName="ctId" size="15%" sortType="ctId" >
	</st:property>
	<st:property headingName="Fiscal Year" 
		columnName="fiscalYear" size="12%" sortType="fiscalYear" >
	</st:property>
	</c:if>
	<st:property headingName="Budget Value($)" 
	columnName="budgetValue" size="17%" sortType="budgetValue" >
	<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.BudgetActionFinancialExtension" />
	</st:property>
	<!--Start Updated in R5 -->
	<st:property headingName="Last Updated" 
	columnName="dateOfLastUpdate" size="15%" sortType="dateOfLastUpdate" >
	<!--End Updated in R5 -->
	<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.BudgetActionFinancialExtension" />
	</st:property>
	<st:property headingName="Status" 
		columnName="status" size="15%" sortType="status" >
	</st:property>
<c:choose>
<c:when test="${org_type eq 'provider_org'}">
		<st:property headingName="Action" columnName="actions" 
				align="right" size="5%" >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.BudgetProviderActionFinancialExtension" />
		</st:property>
		</c:when>
		<c:otherwise>
		<st:property headingName="Action" columnName="actions" 
				align="right" size="5%" >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.BudgetAgencyActionFinancialExtension" />
		</st:property>
		</c:otherwise>
		</c:choose>
</st:table>
<%--End changes  for defect id 6248 release 3.3.0 --%>
	<c:if test="${aoBudgetListLabel eq 0}">
		<div class="noRecordCityBudgetDiv" id="noRecordCityBudgetDiv"
			style='border: 1px solid #ccc; border-top: 0; padding: 6px;'><i>No
		Records Found</i></div>
	</c:if>
	<div class='floatLft'><span> Budgets: <label>${aoBudgetListLabel}</label></span></div>
</div>
<p>&nbsp;</p>

</div>
</form:form> 
	<div class="overlay"></div>
	<div class="alert-box-help">
				<div class="tabularCustomHead toplevelheaderHelp"></div>
	            <div id="helpPageDiv"></div>
		  		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
			</div>
			<div class="alert-box-contact">
				<div id="contactDiv"></div>
			</div>
	<div class="alert-box-link-to-vault" style='width:41%'>
	<div id="overlayContent"></div>
	  
	</div>

	
</d:content>

<!-- Added in R7 for the defect 8644 cancel and merge in order to display the cancel and merge jsp page on click of cancel and merge option in BudgetList Screen  -->
 <portlet:resourceURL var='CancelAndMergeOverlay' id='CancelAndMergeOverlay' escapeXml='false'/>
	<input type="hidden" id="CancelAndMergeOverlayUrl" value="${CancelAndMergeOverlay}"/>
<!-- Added in R7 for Cost center for update services screen -->
<portlet:resourceURL var='updateServicesOverlay' id='updateServicesOverlay' escapeXml='false'/>
	<input type="hidden" id="updateServicesOverlayUrl" value="${updateServicesOverlay}"/>

