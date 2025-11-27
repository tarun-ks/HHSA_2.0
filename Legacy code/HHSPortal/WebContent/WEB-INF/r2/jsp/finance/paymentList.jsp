<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="java.util.List"%>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="javax.portlet.PortletContext"%>
<%@page import="java.util.ArrayList,com.nyc.hhs.constants.*"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/paymentlist.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<portlet:defineObjects/>
<portlet:actionURL var="addPaymentFinanceUrl" escapeXml="false">
	<portlet:param name="action" value="paymentListAction"/>
	<portlet:param name="paymentAction" value="finacialsPaymentMap"/>
</portlet:actionURL>
<portlet:resourceURL var="getPaymentCtResourceUrl" id="getPaymentCtResourceUrl" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var="getProviderListResourceUrl" id="getProviderListResourceUrl" escapeXml="false">
</portlet:resourceURL>
<input type = 'hidden' value='${getProviderListResourceUrl}' id='hiddengetProviderListResourceUrl'/>
<portlet:resourceURL var="getProgramListForAgency" id="getProgramListForAgency"></portlet:resourceURL>
<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<input type="hidden" id="orgType" value="${org_type}"/>
<input type = 'hidden' value='${getPaymentCtResourceUrl}' id='hiddenPaymentCtResourceUrl'/>
<%--Start for Enhancement  id 6356   release 3.4.0 --%>
<portlet:actionURL var="navigateToContractBudget" escapeXml="false">
<portlet:param name="submit_action" value="viewContractBudget"/>
<portlet:param name="action" value="budgetListAction"/>
</portlet:actionURL>
<input type = 'hidden' value='${navigateToContractBudget}' id='navigateToContractBudgetURL'/>

<portlet:actionURL var="viewInvoice" id="viewInvoice" escapeXml="false">
<portlet:param name="viewInvoice" value="contractInvoiceScreen"/>
<portlet:param name="action" value="invoiceListAction"/>
</portlet:actionURL>
<input type = 'hidden' value='${viewInvoice}' id='hiddenViewInvoiceUrl'/>
<%--End for Enhancement  id 6356   release 3.4.0 --%>
<%--Start : Added in R5 --%>
<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
<portlet:actionURL var='navigateListScreenUrl' escapeXml='false'>
	<portlet:param  name='controllerAction' value='redirectFromList'/>
</portlet:actionURL>
<input type="hidden" name="hiddenNavigateListScreenUrl" id="hiddenNavigateListScreenUrl"  value="${navigateListScreenUrl}"/>
<%--End : Added in R5 --%>
	<%--code updation for R4 starts--%>
<c:set var="paymentListSection"><%=HHSComponentMappingConstant.PAYMENT_LIST%></c:set>
<!--Start Added in R5 -->
<d:content section="${paymentListSection}" authorize="" isReadOnly="false">
<!--End Added in R5 -->	
	<%--code updation for R4 ends--%>
<jsp:include page="financeHeader.jsp"></jsp:include>
<div class='clear'></div>
<div class='formcontainer'>
<script type="text/javascript">
var contextPathVariable = "<%=request.getContextPath()%>"
   //show selected method.
		showSelected('paymentListAction','');
		showHeaderSelected('header_financials');
	</script>

<form:form id="paymentform" action="${addPaymentFinanceUrl}" method ="post" name="paymentform" commandName="PaymentSortAndFilter" onkeydown="if (event.keyCode == 13) {submitListPayment();}">

 
<%-- Container Starts --%>
<input type = 'hidden' value='' name="providerId" id='providerId'/>
<div id='tabs-container'>
<h2>Payment List</h2>
<div id="helpIcon" class="iconQuestion">
   <c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>
    <d:content section="${helpIconProvider}">
     <div id="helpIcon" class="marginReset"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
          <input type="hidden" id="screenName" value="Payment List" name="screenName"/>
   </d:content> 
  </div>
<div class='hr'></div>

<div class="failed" id="errorMsgBudgetList">${message}</div>
<c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
<p>Listed below are the payments for your Agency.</p>
</c:if>
<c:if test="${org_type eq 'provider_org'}">
<p>Listed below are the payments for your organization.</p>
</c:if>
 
<%--Filter and Reassign section starts --%>
<div class="taskButtons">
    <span class='floatLft'><input type="button" value="Filter Payments" class="filterDocument"  onclick="setVisibility('documentValuePop', 'inline');" /></span>
    <span class='count'>
    	<span>Payments: <label>${aoPaymentListSize}</label></span>
	</span>
	
    <%-- Popup for Filter Task Starts --%>
    <div id="documentValuePop" class='formcontainerFinance' style='width:460px;'>
        <div class='close'><a href="javascript:setVisibility('documentValuePop', 'none');" >X</a></div>
        <div class='row'>
            <span class='label'>Procurement/ Contract Title:</span>
            <span class='formfield'>
            <form:input path="paymentContractTitle" cssClass="proposalConfigDrpdwn" maxlength="120" id="paymentContractTitle"  onkeyup="removeNonRequiredCharacter(this)"/></span>
             <span class="error"></span>
        </div>
        
        <c:if test="${org_type eq 'city_org' or org_type eq 'provider_org'}">
	        <div class='row'>
	            <span class='label'>Agency:</span>
	            <span class='formfield'>
					  <form:select name="agency" path="agency" class='widthFull' id="agency" > 
					   <form:option id="All NYC Agencies" value="">All NYC Agencies</form:option>
							 <c:forEach items="${aoAgencyList}" var="agencyDetail">
		                  		<form:option value="${agencyDetail['AGENCY_ID']}">${agencyDetail['AGENCY_NAME']}</form:option>
			                </c:forEach>
						  </form:select>
	            </span>
	             <span class="error"></span>
	        </div>
        </c:if>
        
        <div class='row' id="reRenderId" style="display:block">
        	<div>
        	<c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
	            <span class='label'>Program Name:</span>
	            <span class='formfield'>
	                   <form:select path="paymentProgramName" class='widthFull'  id="programName">
	                   <form:option id="Program Name Filter" value=""></form:option>
							<c:forEach items="${programNameList}" var="programObject">
								<form:option title='${programObject.programName}' value="${programObject.programId}">${programObject.programName}
							</form:option>
							</c:forEach>
						  </form:select>
	            </span>
	             <span class="error"></span>
	         </div>
        </div>
        
        <c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
        <div class='row'>
            <span class='label'>Provider:</span>
            <span class='formfield'>
            <form:input path="paymentProvider" cssClass="proposalConfigDrpdwn" id="paymentProvider" maxlength="50"   onkeyup="removeNonRequiredCharacter(this)" /></span>
             <span class="error"></span>
        </div>
        </c:if>
        
         <div class='row'>
            <span class='label'>CT#:</span>
            <span class='formfield'>
             <form:input path="paymentCtId" cssClass="proposalConfigDrpdwn" id="paymentCtId" maxlength="32" onkeyup="removeNonRequiredCharacter(this)"
             title="The CT# is the contract registration number issued by the City’s Financial Management System (FMS). You can use this number to search for additional information in the FMS’ Payee Information Portal – https://nyc.gov/pip"/>
             </span>
              <span class="error"></span>
        </div>
        <div class='row'>
            <span class='label'>Fiscal Year:</span>
            <span class='formfield'>
				 <form:select path="fiscalYearId" class='widthFull' id="fiscalYearId"    >
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
            <span class='label'>Payment Voucher Number:</span>
            <span class='formfield'>
             <form:input path="paymentVoucherNumber" cssClass="proposalConfigDrpdwn" id="paymentVoucherNumber" maxlength="11"    onkeyup="removeNonRequiredCharacter(this)"/></span>
              <span class="error"></span>
        </div>
        <div class='row'>
            <span class='label'>Disbursement Number:</span>
            <span class='formfield'>
            <form:input path="paymentDisNum" cssClass="proposalConfigDrpdwn" id="paymentDisNum" maxlength="15"    onkeyup="removeNonRequiredCharacter(this)"/></span>
             <span class="error"></span>
        </div>
		<div class='row'>
            <span class='label'>Payment Value From($):</span>
            <span class='formfield'>
             <form:input path="paymentValueFrom" cssStyle="width:43%" id="paymentValueFrom" validate="number"  />  <b> to: </b>  <form:input path="paymentValueTo" cssStyle="width:43.5%" id="paymentValueTo" validate="number" />
               </span>
                <span class="error"></span>
        </div>
        <c:if test="${org_type eq 'city_org' or org_type eq 'agency_org'}">
		<div class='row'>
            <span class='label'>Status:</span>
            <span class='formfield' id="formfieldPayment">
            <c:forEach items="${aoPaymentStatus}" var="paymentStatusObject">
            <c:choose>
            <c:when test="${paymentStatusObject.statusFilter lt 4}">
            <span class='leftColumn'>
			<c:set value="${lbFirstLoad}" var="lbFirstLoad" ></c:set>
					<span><form:checkbox path="paymentStatusList" id="${paymentStatusObject.statusId}" 
					 value="${paymentStatusObject.statusId}"   checked="${(fn:contains(paymentStatus,paymentStatusObject.statusId) || (lbFirstLoad)) ? 'checked' : ''}" ></form:checkbox>
					<label for="${paymentStatusObject.statusId}" name="${paymentStatusObject.status}">${paymentStatusObject.status}</label>
					</span>
			</span>
			</c:when>
			<c:otherwise>
			
			<span class='rightColumn' style='float:none'>
			<c:if test="${paymentStatusObject.statusId ne '159'}">
			<c:set value="${lbFirstLoad}" var="lbFirstLoad" ></c:set>
					<span><form:checkbox path="paymentStatusList" id="${paymentStatusObject.statusId}" 
					 value="${paymentStatusObject.statusId}"   checked="${(fn:contains(paymentStatus,paymentStatusObject.statusId) || (lbFirstLoad)) ? 'checked' : ''}" ></form:checkbox>
					<label for="${paymentStatusObject.statusId}" name="${paymentStatusObject.status}">${paymentStatusObject.status}</label>
					</span>
			</c:if>
<%--below block is added  as part of build 3.1.0, enhancement 6023  to show check box conditionally based on payment status--%>			
			<c:if test="${paymentStatusObject.statusId eq '159'}">
			<c:set value="${lbFirstLoad}" var="lbFirstLoad" ></c:set>
					<span><form:checkbox path="paymentStatusList" id="${paymentStatusObject.statusId}" 
					 value="${paymentStatusObject.statusId}"  class="paymentRejected" ></form:checkbox>
					<label for="${paymentStatusObject.statusId}" name="${paymentStatusObject.status}">${paymentStatusObject.status}</label>
					</span>
			</c:if>
			</span>
			
			</c:otherwise>
			</c:choose>
			</c:forEach>
			 </span>
			  <span class="error"></span>
        </div>
		<div class='row'>
            <span class='label'>Date of Last Update From:</span>
            <span class='formfield'>
            <span class="floatLft">
            	<form:input path="dateLastUpdateFrom" id="dateLastUpdateFrom" cssClass="datepicker" validate="calender"  maxlength="10"></form:input>
            	<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateLastUpdateFrom',event,'mmddyyyy');return false;">  
            		<b> to: </b> 
			 	<form:input path="dateLastUpdateTo" id="dateLastUpdateTo" cssClass="datepicker" validate="calender"  maxlength="10"></form:input>
					<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateLastUpdateTo',event,'mmddyyyy');return false;">
				</span>
				 <span class="error clear" id="dateCheck"></span>
            </span>
        </div>
		</c:if>
		<%--Start || Changes done for enhancement 6495 for Release 3.12.0--%>
		 <c:if test="${org_type eq 'provider_org'}">
		 <div class='row'>
            <span class='label'>Status:</span>
            <span class='formfield' id="formfieldPayment">
			<span class='leftColumn'>
			<span><form:checkbox path="paymentStatusList" id='chkPaymentApproved'  checked="${(fn:contains(paymentStatus,'64') || (lbFirstLoad)) ? 'checked' : ''}" value="64" /><label for='chkPaymentApproved'>Pending EFT</label></span>
			</span>
			<span class='rightColumn'>
			<span><form:checkbox path="paymentStatusList" id='chkPaymentDibursed'  checked="${(fn:contains(paymentStatus,'65') || (lbFirstLoad)) ? 'checked' : ''}" value="65" /><label for='chkPaymentDibursed'>Disbursed</label></span>
			</span>
			 </span>
			 
			  <span class="error"></span>
        </div>
        <%--End || Changes done for enhancement 6495 for Release 3.12.0--%>
		<div class='row'>
            <span class='label'>Date Disbursed From:</span>
            <span class='formfield'>
            	<span class='floatLft'>
		             <form:input path="dateDisbursedFrom" id="dateDisbursedFrom" class='datepicker' validate="calender"  maxlength="10"/>
		             <img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateDisbursedFrom',event,'mmddyyyy');return false;">  
             	&nbsp;&nbsp;
             	</span>
             	<span id="dateDisbursedFromError" class="error clear"></span>
             <b> to: </b>
             <span>
			 	<form:input path="dateDisbursedTo" id="dateDisbursedTo" class='datepicker' validate="calender" maxlength="10"/>
			 
			<img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('dateDisbursedTo',event,'mmddyyyy');return false;">
			</span>
			<span id="dateDisbursedToError" class="error clear"></span>
            </span>
             <span class="error" id="dateCheck"></span>
        </div>
        </c:if>
        <div class="buttonholder">
            <input type="button" class='graybtutton' value="Set to Default Filters" onclick="settoDefaultFilters()" onmousedown="settoDefaultFilters()" /><input type="button" value="Filter" id="filterPayment" name="filterPayment" onclick="submitListPayment()"/>
        </div> 
    </div>
    <%-- Popup for Filter Task Ends --%>

      
   
	</div>
<%--Filter and Reassign section ends --%>
 
 
<div class='clear'></div>
<%-- Form Data Starts --%>

	<c:choose>
	<c:when test="${org_type eq 'provider_org'}">
	<div class='tabularWrapper paymentListProviderDiv' style='min-height:700px !important'>
	<st:table 
	objectName="aoPaymentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
	<%--Start changes for defect id 6248 release 3.3.0 --%>
	<st:property headingName="Agency"
		columnName="paymentAgencyId" size="5%" align="center" sortType="paymentAgencyId" >
	</st:property>
		<%--End changes for defect id 6248 release 3.3.0 --%>
	<st:property headingName="Procurement/ Contract Title:"
		columnName="paymentContractTitle" size="15%" align="center" sortType="paymentContractTitle" >
			<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtensionProvider" />
	</st:property>
	<st:property headingName="Payee Name"
		columnName="paymentPayeeName" size="15%" align="center" sortType="paymentPayeeName" >
	</st:property>
	<%--Start for Enhancement  id 6356   release 3.4.0 --%>
	<st:property headingName="CT#"
		columnName="paymentCtId" size="10%" align="center" sortType="paymentCtId" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<st:property headingName="Payment Voucher Number"
		columnName="paymentVoucherNumber" size="10%" align="center" sortType="paymentVoucherNumber" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<%--End for Enhancement  id 6356   release 3.4.0 --%>
	<st:property headingName="Value($)" 
		columnName="paymentValue" size="10%" align="center" sortType="paymentValue" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
		<st:property headingName="Disbursement Number"
		columnName="paymentDisNum" size="10%" align="center" sortType="paymentDisNum">
	</st:property>
	<st:property headingName="Date Disbursed"
		columnName="paymentDisDate" size="10%" align="center" sortType="paymentDisDate" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<%--Start : Added in R5 --%>
	<st:property headingName="Action"
		columnName="action" size="14%" align="center">
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<%--End : Added in R5 --%>
	</st:table>
	<c:if test="${aoPaymentListSize eq 0}">
	<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No Records Found</div>
	</c:if>
	<div class='floatLft'><span> Payments: <label>${aoPaymentListSize}</label></span></div>
	</div>
	</c:when>
	<c:otherwise>


	<div class='tabularWrapper paymentListCityDiv' style='min-height:700px !important'>
	<st:table 
	objectName="aoPaymentList" cssClass="heading"
						alternateCss1="evenRows" alternateCss2="oddRows" pageSize='${allowedObjectCount}'>
		<%--Start changes for defect id 6248 release 3.3.0 --%>
	<c:if test="${org_type eq 'city_org'}">
	<st:property headingName="Agency"
		columnName="paymentAgencyId" size="5%" align="center" sortType="paymentAgencyId" >
	</st:property>
	</c:if>
		<%--End changes for defect id 6248 release 3.3.0 --%>
	<st:property headingName="Procurement/ Contract Title:"
		columnName="paymentContractTitle" size="15%" align="center" sortType="paymentContractTitle" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<st:property headingName="Provider"
		columnName="paymentProvider" size="15%" align="center" sortType="paymentProvider" >
		<%--Start : Added in R5 --%>
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
		<%--End : Added in R5 --%>
	</st:property>
	
	<%--Start changes for defect id 6248 release 3.3.0 --%>
	<%--Start for Enhancement  id 6356   release 3.4.0 --%>
	<st:property headingName="CT#"
		columnName="paymentCtId" size="10%" align="center" sortType="paymentCtId" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<%--End for Enhancement  id 6356   release 3.4.0 --%>
		<%--End changes for defect id 6248 release 3.3.0 --%>
	<%--Start: R5 Added --%>
	<st:property headingName="Payment Voucher Number"
		columnName="paymentVoucherNumber" size="10%" align="center" sortType="paymentVoucherNumber" >
	</st:property>
	<%--End: R5 Added --%>
	
	<st:property headingName="Value($)" 
		columnName="paymentValue" size="10%" align="center" sortType="paymentValue" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<st:property headingName="Status"
		columnName="paymentStatus" size="6%" align="center" sortType="paymentStatus" >
	</st:property>
	<st:property headingName="Last Updated"
		columnName="paymentLastUpdateDate" size="10%" align="center" sortType="paymentLastUpdateDate" >
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<%--Start : Added in R5 --%>
	<st:property headingName="Action"
		columnName="action" size="14%" align="center">
		<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.PaymentActionValueFinancialExtension" />
	</st:property>
	<%--End : Added in R5 --%>
	</st:table>
	<c:if test="${aoPaymentListSize eq 0}">
	<div class="noRecordCityPaymentDiv noRecord" id="noRecordCityPaymentDiv">No Records Found</div>
	</c:if>
	<div class='floatLft'><span> Payments: <label>${aoPaymentListSize}</label></span></div>
	</div>
	</c:otherwise>
	</c:choose>



<form:hidden path="firstSort"/>
<form:hidden path="secondSort"/>
<form:hidden path="firstSortType"/>
<form:hidden path="secondSortType"/>
<form:hidden path="firstSortDate"/>
<form:hidden path="secondSortDate"/>
<form:hidden path="sortColumnName"/>

				

<p>&nbsp;</p>
 
<%-- Container Ends --%>
</div>
 
</div>
 
 </form:form>
 
<%-- Below div is used to launch help overlay content for all jsps --%>
<div class="overlay"></div>
<div class="alert-box-help">
                        <div class="tabularCustomHead toplevelheaderHelp"></div>
                  <div id="helpPageDiv"></div>
                        <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box-contact">
      <div class="content">
            <div id="newTabs">
                  <div id="contactDiv"></div>
            </div>
      </div>
</div>
</d:content>