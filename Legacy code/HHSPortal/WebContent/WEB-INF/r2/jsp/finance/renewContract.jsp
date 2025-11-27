<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="../resources/js/autoNumeric-1.7.5.js"></script>
<style>
	.contractPopup span.error{
		width:44% !important
	}
	.contractPopup .date{
		width:86% !important
	}
	.tabularContainer{
		overflow-y:auto;
		height:640px
	}
	.button{
		position:static !important
	}
</style>
<portlet:defineObjects />

<%-- Overlay Popup Starts --%>
<portlet:resourceURL var="renewContractSubmit" id="renewContractSubmit" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var='populateRenewContractPage' id='populateRenewContractPage' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${populateRenewContractPage}' id='populateRenewContractPageUrl'/>
<input type = 'hidden' value='${renewContractSubmit}' id='RenewContractSubmitUrl'/>
<form:form id="renewContractForm" action="" method ="post" name="renewContractForm">
<div class="content">
	<div id="newTabs">
	<div class='tabularCustomHead'>
		<span id="contractTypeId">Renew Contract</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
	<div id="renewContract">
		<div class="tabularContainer">
		<%if(( null != request.getAttribute("Error") ) && !"".equalsIgnoreCase((String)request.getAttribute("Error"))){%>
			     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute("Error")%> </div>
			<%}%>
			 <div id="ErrorDiv" class="failed breakAll"  > </div>
		<h2>Renew Contract</h2>
		<div class='hr'></div>
		
		<p>To renew a contract, please search for the award E-PIN and click
		the 'Find E-PIN' button to populate the information for the selected
		E-PIN. Then, populate all required fields, update or add any missing
		information, and renew the Contract by selecting the 'Renew Contract'
		button.</p>
		<p><label class="required">*</label>Indicates a required field</p>
		<c:set var="epinBean" value="${epinBeanDetails}"></c:set>
        <%--Start: Release 6 APT Interface Epin changes --%>
		<input type="hidden" id="refAptEpinId" name="refAptEpinId" value="${epinBean.refAptEpinId}"/>
		<input type="hidden" id="contractAgencyId" name="contractAgencyId" value="${epinBean.agencyName}"/>
		<%--End: Release 6 APT Interface Epin changes --%>
		<div class="formcontainer contractPopup">
		<div class="row">
			<div>
				<span class="label equalForms">
					<input id="epin" name="epin" type="text" value="" maxlength="30" class='proposalConfigDrpdwn' />
				</span> 
				<span class="formfield equalForms">
					<input id="searchepinbutton" name="searchepinbutton" type="button" class="button" value="Find E-PIN" onclick="findEpin(document.getElementById('epin').value);"/>
				</span>
			</div>
			<div>	
				<span class="error"></span>
			</div>	
		</div>
			
		<div class="row">
			<span class="label equalForms">
				<label class="required">*</label>Award E-PIN:
			</span> 
			<span class="formfield equalForms">
				<input id="awardEpin" name="awardEpin" type="text" value="${epinBean.awardEpin}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
			<span class="error"></span>
		</div>
			
		<div class="row">
			<span class="label equalForms">Procurement Start Date:</span> 
			<span class="formfield equalForms">
				<input id="procurementStartDate" name="procurementStartDate" type="text" value="${epinBean.procurementStartDate}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
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
				<input id="agencyId" name="agencyId" type="text" value="${epinBean.awardAgencyId}" readonly='readonly' class='proposalConfigDrpdwn' />
				<input id="prevContractAgencyId" name="prevContractAgencyId" type="hidden" value="${epinBean.agencyId}" />
			</span>
		</div>
			
		<div class="row">
			<span class="label equalForms">Procurement Method:</span> 
			<span class="formfield equalForms">
				<input 	id="procurementMethod" name="procurementMethod" type="text" value="${epinBean.procMethod}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
		</div>
			
		<div class="row">
			<span class="label equalForms">APT Project/Program:</span> 
			<span class="formfield equalForms">
				<input id="aptProject" name="aptProject" type="text" value="${epinBean.projProg}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
		</div>
			
		<div class="row">
			<span class="label equalForms">
				<label class="required">*</label>Accelerator Program Name:
			</span> 
			<span class="formfield equalForms">
				<select id="accProgramName" name="accProgramName" value="${epinBean.programName}" readonly='readonly' class='widthFull' >
					<OPTION value="${epinBean.programNameId}" selected>${epinBean.programName}</OPTION>
				</select>
			</span>
			<span class="error"></span>
		</div>
			
		<div class="row">
			<span class="label equalForms">APT Procurement Desc:</span> 
			<span class="formfield equalForms">
				<input id="aptProcurementDesc" name="aptProcurementDesc" type="text" value="${epinBean.procDescription}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
		</div>
			
		<div class="row">
			<span class="label equalForms">
				<label class="required">*</label>Contract Title:
			</span> 
			<span class="formfield equalForms">
				<input id="contractTitlePopUp" name="contractTitlePopUp" type="text" value="${epinBean.contractTitle}" class='proposalConfigDrpdwn' maxlength="256"/>
			</span>
			<span class="error"></span>
		</div>
			
		<div class="row">
			<span class="label equalForms">
				<label class="required">*</label>Awarded Vendor FMS ID:
			</span> 
			<span class="formfield equalForms">
				<input id="vendorFmsId" name="vendorFmsId" type="text" value="${epinBean.vendorFmsId}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
			<span class="error"></span>
		</div>
			
		<div class="row">
			<span class="label equalForms">Awarded Vendor FMS Name:
			</span> 
			<span class="formfield equalForms">
				<input id="vendorFmsName" name="vendorFmsName" type="text" value="${epinBean.vendorFmsName}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
		</div>
			
		<div class="row">
			<span class="label equalForms">Accelerator Provider Legal Name:
			</span> 
			<span class="formfield equalForms">
				<input id="providerLegalName" name="providerLegalName" type="text" value="${epinBean.providerLegalName}" readonly='readonly' class='proposalConfigDrpdwn' />
			</span>
		</div>
			
			<div class="row">
				<span class="label equalForms">
					<label class="required">*</label>Contract Value (Renewal Amount) ($):
				</span> 
				<span class="formfield equalForms">
					<input id="contractValue" name="contractValue" type="text" value="${epinBean.contractValue}" class='proposalConfigDrpdwn' validate="number" />
				</span>
				<span class="error"></span>
			</div>
			
			<div class="row">
				<span class="label equalForms">
					<label class="required">*</label>Contract Start Date:</span> 
				<span class="formfield equalForms">
					<input id="contractStartDate" name="contractStartDate" type="text" value="${epinBean.contractStart}" class='proposalConfigDrpdwn date' validate="calender"  maxlength="10"/>
					<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('contractStartDate',event,'mmddyyyy');return false;"></span><span class="error">
				</span>
			</div>
			
			<div class="row">
				<span class="label equalForms">
					<label class="required">*</label>Contract End Date:
				</span> 
				<span class="formfield equalForms">
					<input id="contractEndDate" name="contractEndDate" type="text" value="${epinBean.contractEnd}" validate="calender" maxlength="10" class='proposalConfigDrpdwn date' />	
					<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('contractEndDate',event,'mmddyyyy');return false;"></span>
					<span class="error"></span>
			</div>
		</div>
		
		<div class="buttonholder">
			<input type="button" class="graybtutton" value="Cancel" onclick="clearAndCloseOverLay();"/> 
			<input type="submit" id="renewContractButton"  class="button" value="Renew Contract" />
		</div>
		
		</div>
	</div>
	</div>
</div>
<input type = 'hidden' value='${epinBean.contractId}' id='hdnContractId' name="hdnContractId"/>
</form:form>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/renewContract.js"></script>