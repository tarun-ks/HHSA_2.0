<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/resources/js/calendar.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/grid.locale-en.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jquery.formatCurrency-1.4.0.js"></script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/jqgridDialogOveride.js"></script>
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
<portlet:resourceURL var="amendContractSubmit" id="amendContractSubmit" escapeXml="false">
</portlet:resourceURL>
<portlet:resourceURL var='populateAmendContractPage' id='populateAmendContractPage' escapeXml='false'>
</portlet:resourceURL>
<input type = 'hidden' value='${populateAmendContractPage}' id='populateAmendContractPageUrl'/>
<input type = 'hidden' value='${amendContractSubmit}' id='AmendContractSubmitUrl'/>
<form:form id="amendContractForm" action="" method ="post" name="amendContractForm">
<div class="content">
<div id="newTabs">
<div class='tabularCustomHead'><span id="contractTypeId">Amend
Contract</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
<div id="amendContract">
<div class="tabularContainer">
<%if(( null != request.getAttribute("Error") ) && !"".equalsIgnoreCase((String)request.getAttribute("Error"))){%>
	     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute("Error")%> </div>
	<%}%>
	 <div id="ErrorDiv" class="failed localTabs"  > </div>
<h2>Amend Contract</h2>
<div class='hr'></div>
	<p>To amend a contract, please search for the award E-PIN and click
	the 'Find E-PIN' button to populate the information for the selected
	E-PIN. Then, populate all required fields, update or add any missing
	information, and create the Amendment by selecting the 'Amend Contract'
	button.</p>
	<p><label class="required">*</label>Indicates a required field</p>
	<c:set var="epinBean" value="${epinBeanDetails}"></c:set>
	<div class="formcontainer contractPopup">
	<div class="row">
		<div>
		     <span class="label equalForms">
		          <input id="epin" name="epin" type="text" value="" maxlength="30" class='proposalConfigDrpdwn' />
		     </span> 
		     <span class="formfield equalForms">
		          <input id="searchepinbutton" name="searchepinbutton" type="button" class="button" value="Find E-PIN" onclick="findEpin(document.getElementById('epin').value);" />
		     </span>
		</div>
		<div><span class="error"></span></div>
	</div>
	<%-- Release 6 : Changes for Non Apt Epins--%>
	<input type="hidden" id="refAptEpinId" name="refAptEpinId" value="${epinBean.refAptEpinId}"/>
	<input type="hidden" id="contractAgencyId" name="contractAgencyId" value="${epinBean.agencyName}"/>
	<%-- Release 6 : Changes for Non Apt Epins end--%>
	<div class="row">
	    <span class="label equalForms"> <label class="required">*</label>Award E-PIN:</span> 
	    <span class="formfield equalForms"><input id="awardEpin" name="awardEpin" type="text" value="${epinBean.awardEpin}"
		readonly='readonly' class='proposalConfigDrpdwn' /></span><span
		class="error"></span>
    </div>

	<div class="row">
	    <span class="label equalForms">Procurement Start Date:</span> 
	    <span class="formfield equalForms">
	          <input id="procurementStartDate" name="procurementStartDate" type="text" value="${epinBean.procurementStartDate}" readonly='readonly'	class='proposalConfigDrpdwn' />
	    </span><span class="error"></span>
    </div>

	<div class="row"><span class="label equalForms">Agency Division:</span> 
	    <span class="formfield equalForms">
	           <input id="agencyDivision" name="agencyDivision" type="text" value="${epinBean.agencyDiv}" readonly='readonly'  class='proposalConfigDrpdwn' />
	    </span>
	    <span class="error"></span>
	</div>

	<div class="row"><span class="label equalForms">Agency ID:</span>
        <span class="formfield equalForms">
	         <input id="agencyId" name="agencyId" type="text" value="${epinBean.agencyId}" readonly='readonly' class='proposalConfigDrpdwn' />
	    </span>
	    <span class="error"></span>
    </div>

	<div class="row">
	    <span class="label equalForms">Procurement Method:</span> 
	    <span class="formfield equalForms">
	         <input id="procurementMethod" name="procurementMethod" type="text" value="${epinBean.procMethod}" readonly='readonly' class='proposalConfigDrpdwn' />
	    </span>
	    <span class="error"></span>
    </div>

	<div class="row">
	     <span class="label equalForms">APT Project/Program:</span> 
	     <span class="formfield equalForms">
	           <input id="aptProject" name="aptProject" type="text" value="${epinBean.projProg}" readonly='readonly' class='proposalConfigDrpdwn' />
	     </span>
	     <span class="error"></span>
	</div>

	<div class="row">
	    <span class="label equalForms"></label>APT Procurement Desc:</span> 
	    <span class="formfield equalForms">
	         <input id="procDescription" name="procDescription" type="text" value="${epinBean.procDescription}" readonly='readonly' class='proposalConfigDrpdwn' />
	    </span>
	    <span class="error"></span>
	</div>

	<div class="row">
	    <span class="label equalForms">  <label class="required">*</label>Awarded Vendor FMS ID:</span> 
	    <span class="formfield equalForms"><input id="vendorFmsId" name="vendorFmsId" type="text" value="${epinBean.vendorFmsId}" readonly='readonly' class='proposalConfigDrpdwn' /></span>
	    <span class="error"></span>
	</div>

	<div class="row">
	    <span class="label equalForms"></label>Awarded Vendor FMS Name:</span> 
	    <span class="formfield equalForms">
	           <input id="vendorFmsName" name="vendorFmsName" type="text" value="${epinBean.vendorFmsName}" readonly='readonly'  class='proposalConfigDrpdwn' />
	    </span>
	    <span class="error"></span>
    </div>

	<div class="row">
	    <span class="label equalForms">Accelerator Provider Legal Name:</span> 
	    <span class="formfield equalForms">
	          <input id="providerLegalName" name="providerLegalName" type="text" value="${epinBean.providerLegalName}" readonly='readonly' class='proposalConfigDrpdwn' />
	     </span><span class="error"></span>
	</div>

	<div class="row">
	     <span class="label equalForms">Contract Amount ($):</span> 
	     <span class="formfield equalForms"> <input id="contractValueTxt" name="contractValueTxt" type="text" value="${epinBean.contractValue}" class='proposalConfigDrpdwn' readonly="readonly"/> </span>
	     <span class="error"></span>
    </div>

	<input type="hidden" id="contractValue" name="contractValue" value="${epinBean.contractValue}"/>

	<div class="row">
	    <span class="label equalForms"> <label class="required">*</label>Amendment Amount ($):</span> 
	    <span class="formfield equalForms"><input id="amendValue" name="amendValue" type="text" value="${epinBean.amendValue}" class='proposalConfigDrpdwn' /></span>
	    <span class="error"></span>
	</div>
	<%--
	<div class="row"><span class="label equalForms">New Total
	Contract Amount ($):</span> <span class="formfield equalForms"><input
		id="newTotalAmount" name="newTotalAmount" type="text"
		 value="${epinBean.newTotalContractAmount}" readonly='readonly' class='proposalConfigDrpdwn' /></span><span
		class="error"></span></div>
	--%>
	
	<div class="row">
	     <span class="label equalForms">Contract Start Date:</span> 
	     <span class="formfield equalForms"><input id="contractStartDate" name="contractStartDate" type="text" value="${epinBean.contractStart}" readonly='readonly' class='proposalConfigDrpdwn' /></span>
	     <span class="error"></span>
	</div>

	<div class="row">
		<span class="label equalForms"><label class="required">*</label>Contract End Date:</span>
		<span class="formfield equalForms">
			<input id="proposedContractEnd" name="proposedContractEnd" type="text" value="${epinBean.contractEnd}" class='proposalConfigDrpdwn date' validate="calender" maxlength="10" />
			<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" title="Change Contract End Date only if extending or reducing the term as part of the amendment." onclick="NewCssCal('proposedContractEnd',event,'mmddyyyy');return false;"/>
		</span>
		<span class="error"></span>
	</div>

	<div class="row">
		<span class="label equalForms"><label class="required">*</label>Amendment Start Date:</span>
		<span class="formfield equalForms">
			<input id="amendmentStart" name="amendmentStart" type="text" value="${epinBean.amendmentStart}" class='proposalConfigDrpdwn date' validate="calender" maxlength="10" />
			<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('amendmentStart',event,'mmddyyyy');return false;"/>
		</span>
		<span class="error"></span>
	</div>

	<div class="row">
		<span class="label equalForms"><label class="required">*</label>Amendment End Date:</span>
		<span class="formfield equalForms">
			<input name="amendmentEnd" type="text" value="${epinBean.amendmentEnd}" id="amendmentEnd" validate="calender" maxlength="10" class='proposalConfigDrpdwn date' />
			<img src="../framework/skins/hhsa/images/calender.png" class="imgclassPlanned" onclick="NewCssCal('amendmentEnd',event,'mmddyyyy');return false;"/>
		</span>
		<span class="error"></span>
	</div>
	</div>
	<%--code updation for R4 starts--%>
	<div class="row"><span class="label equalForms"><label
		class="required">*</label>Amendment Title:</span> <span
		class="formfield equalForms"><input id="amendmentTitle"
		name="amendmentTitle" type="text" value="${epinBean.contractTitle}"
		class='proposalConfigDrpdwn' maxlength="120"/></span><span class="error"></span></div>
	<%--code updation for R4 ends--%>
	<div class="row"><span class="label equalForms"><label
		class="required">*</label>Amendment Reason (PEG ID):</span> <span
		class="formfield equalForms"><input id="amendmentReason"
		name="amendmentReason" type="text" value="${epinBean.amendmentReason}"
		class='proposalConfigDrpdwn' maxlength="100"/></span><span class="error"></span></div>


	<div class="buttonholder">
	     <input type="button" class="graybtutton" title="Cancel" value="Cancel" onclick="clearAndCloseOverLay();" /> 
	     <input type="submit" id="amendContractButton" class="button" title="Amend Contract" value="Amend Contract" />
	</div>

</div>
</div>
</div>
</div>
<input type = 'hidden' value='${epinBean.contractEnd}' id='originalContractEndDate' name='originalContractEndDate' />
<input type = 'hidden' value='${epinBean.contractId}' id='hdnContractId' name="hdnContractId"/>
</form:form>
<script type="text/javascript">
var requiredKey= "<fmt:message key='REQUIRED_FIELDS'/>";
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/amendContract.js"></script>