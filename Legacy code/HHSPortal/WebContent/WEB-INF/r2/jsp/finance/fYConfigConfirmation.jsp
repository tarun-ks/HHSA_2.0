<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/fyConfigConfirmation.js"></script>
	

<portlet:resourceURL var="fyConfigConfirmUrl" id="fyConfigConfirm" escapeXml="false">	
	<portlet:param name="contractId" value="${ContractId}" />
	<portlet:param name="contractTypeId" value="${ContractTypeId}" />
	<portlet:param name="fyConfigFiscalYear" value="${fyConfigFiscalYear}" />
</portlet:resourceURL>
<input type="hidden" id="fyConfigConfirmUrl" value="${fyConfigConfirmUrl}"/>


<%-- Overlay Popup Starts --%>
    <div class="tabularCustomHead">Confirm New FY Configuration</div>
    <div class="tabularContainer"> 
		<div class="failed" id="errorMsg"></div>
		<p>
			Proceeding will begin a New FY Configuration. Click on the Start New FY Configuration button to continue.
		</p>
	
	<div class="buttonholder">
		<input type="button" class="graybtutton" value="Cancel" onclick="clearAndCloseOverLay()"/>
		<input type="button" class="button" value="Start New FY Configuration" onclick="NewFYConfigBtn()" />
	</div>
  
    </div>
    <a href="javascript:void(0);" class="exit-panel"></a> 
