<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>

<%--This JSP is for update Contract screen in Financials tab to update existing contracts.  --%>

<style>
.contractPopup span.error{
	width:44% !important
}
.contractPopup .date{
	width:88% !important
}
.tabularContainer{
		overflow-y:auto;
		height:640px
	}
	.button{
		position:static !important
	}
.tabularContainer .formcontainer .row span.error{
	margin-left:49%
}
.alert-box-confirmFiscalYear {
    background: #FFF;
    display: none;
    z-index: 1001;
      position:fixed !important;
      top:25% !important
}

</style>
<portlet:defineObjects />
<%--  Overlay Popup Starts --%>
<%-- This Portlet resource URL is for Submit button for updating contract. --%>
<portlet:resourceURL var="updateContractSubmit" id="updateContractSubmit" escapeXml="false">
<portlet:param name="hdnContractId" value='${epinBeanDetails.contractId}'/>
</portlet:resourceURL>

<input type="hidden" id="getProgramListForAgency" value="${getProgramListForAgency}"/>
<input type = 'hidden' value='${updateContractSubmit}' id='updateContractSubmitUrl'/>
<input type = 'hidden' value='${epinBeanDetails.contractId}' id='hdnContractId'/>


<form:form id="updateContractForm" action="${updateContractSubmit}" method ="post" name="updateContractForm">
<div class="content">
	<div id="newTabs">
		<div class='tabularCustomHead'>
			<span id="contractTypeId">Update Contract</span> 
			<a href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		
		<div id="updateContract">
			<div class="tabularContainer">
			<%if(( null != request.getAttribute("Error") ) && !"".equalsIgnoreCase((String)request.getAttribute("Error"))){%>
				     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute("Error")%> </div>
				<%}%>
				 <div id="ErrorDiv" class="failed breakAll"> </div>
				<h2>Update Contract</h2>
				<div class='hr'></div>
				<c:set var="epinBean" value="${epinBeanDetails}"></c:set>
				<div class="formcontainer contractPopup">
				<div class="row">
					<span class="label equalForms"><label class="required">*</label>Award E-PIN:</span> 
					<span class="formfield equalForms">
						<input id="awardEpin" name="awardEpin" type="text" value="${epinBean.awardEpin}" readonly='readonly' class='proposalConfigDrpdwn' />
					</span>
					<span class="error"></span>
				</div>
				
			<div class="row">
				<span class="label equalForms">Procurement Start Date:</span> 
				<span class="formfield equalForms">
					<input id="procurementStartDate" name="procurementStartDate" type="text" value="${epinBean.procurementStartDate}" readonly='readonly' class='proposalConfigDrpdwn' /></span><span class="error">
				</span>
			</div>
		
			<div class="row">
	            <span class="label equalForms"><label class="required">*</label>Agency:</span>
	            <span class="formfield equalForms">
	            	<select id="agencyId" name="agencyId" value="${epinBean.agencyId}" readonly='readonly' class='widthFull' disabled="true" >
						<OPTION value="${epinBean.agencyId}" selected>${epinBean.agencyName}</OPTION>
					</select>
	            </span>
	            <span class="error"></span>
	        </div>
				
			<div class="row">
				<span class="label equalForms">Agency Division:</span> 
				<span class="formfield equalForms">
					<input id="agencyDivision" name="agencyDivision" type="text" value="${epinBean.agencyDiv}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Agency ID:</span> 
				<span class="formfield equalForms">
					<input id="agencyIdFromAPT" name="agencyIdFromAPT" type="text" value="${epinBean.agencyId}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Award Agency ID:</span> 
				<span class="formfield equalForms">
					<input id="awardAgencyId" name="awardAgencyId" type="text" value="${epinBean.awardAgencyId}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Procurement Method:</span> 
				<span class="formfield equalForms">
					<input id="procurementMethod" name="procurementMethod" type="text" value="${epinBean.procMethod}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">APT Project/Program:</span> 
				<span class="formfield equalForms">
					<input id="aptProject" name="aptProject" type="text" value="${epinBean.projProg}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
       
            <div class="row">
				<span class="label equalForms"><label class="required">*</label>Accelerator Program Name:</span> 
				<span class="formfield equalForms">
                  <select path="programName" name="accProgramName" id="accProgramName" Class="proposalConfigDrpdwn">
                  			<option id = "${epinBean.programNameId}" value="${epinBean.programNameId}" title="${epinBean.programName}" selected="true">${epinBean.programName}</option>
                  		<c:forEach items="${epinBean.programNameList}" var="programObject">
                  		<c:if test="${programObject.programId ne epinBean.programNameId}">
							<option id = "${programObject.programId}" value="${programObject.programId}" title="${programObject.programName}">${programObject.programName}</option>
						</c:if>	
						</c:forEach>
					</select>
            	</span>
				<span class="error"></span>
        	</div>
        	
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract Title (APT Procurement Desc.):</span> 
				<span class="formfield equalForms">
					<input id="contractTitlePopUp" name="contractTitlePopUp" type="text" value="${epinBean.contractTitle}" class='proposalConfigDrpdwn' maxlength="120"/>
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Awarded Vendor FMS ID:</span> 
				<span class="formfield equalForms">
					<input id="vendorFmsId" name="vendorFmsId" type="text" value="${epinBean.vendorFmsId}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Awarded Vendor FMS Name:</span> 
				<span class="formfield equalForms">
					<input id="vendorFmsName" name="vendorFmsName" type="text" value="${epinBean.vendorFmsName}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms">Accelerator Provider Legal Name:</span> 
				<span class="formfield equalForms">
					<input id="providerLegalName" name="providerLegalName" type="text" value="${epinBean.providerLegalName}" readonly='readonly' class='proposalConfigDrpdwn' />
				</span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract Value (Award Amount) ($):</span> 
				<span class="formfield equalForms">
					<input id="contractValue" name="contractValue" type="text" value="${epinBean.contractValue}" class='proposalConfigDrpdwn' validate="number" />
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract Start Date:</span> 
				<span class="formfield equalForms">
					<input id="contractStartDate" name="contractStartDate" type="text" value="${epinBean.contractStart}" class='proposalConfigDrpdwn date' validate="calender" maxlength="10" />
				</span>
				<span class="error"></span>
			</div>
				
			<div class="row">
				<span class="label equalForms"><label class="required">*</label>Contract End Date:</span> 
				<span class="formfield equalForms">
					<input id="contractEndDate" name="contractEndDate" type="text" value="${epinBean.contractEnd}" validate="calender" maxlength="10" class='proposalConfigDrpdwn date' />	
				</span>
				<span class="error"></span>
			</div>
			</div>
			
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="Cancel" onclick="clearAndCloseOverLay();"/> 
			<input type="submit" id="updateContractButton"  class="button" value="Update Contract" />
		</div>
		
		</div>
		</div>
	</div>
</div>
</form:form>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/updateContract.js"></script>