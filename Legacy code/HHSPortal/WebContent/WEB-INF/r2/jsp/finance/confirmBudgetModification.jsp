
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:renderURL var="confirmModification">    
    <portlet:param name="render_action" value="budgetList" />
</portlet:renderURL>
<portlet:resourceURL var='createButtonLink' id='createButtonLink' escapeXml='false'>
	<portlet:param name="asBudgetId" value="${asBudgetId}" />
	<portlet:param name="asContractId" value="${asContractId}" />
	<portlet:param name="budgetType" value="${budgetType}" />
	<portlet:param name="fiscalYearID" value="${fiscalYearId}" />
</portlet:resourceURL>
<input type="hidden" id="createButtonLinkURL" value="${createButtonLink}"/>
<portlet:actionURL var="navigateToContractModification" escapeXml="false">
<portlet:param name="submit_action" value="viewContractBudget"/>
<portlet:param name="action" value="budgetListAction"/>
<portlet:param name="asBudgetId" value="${asBudgetId}" />
<portlet:param name="asContractId" value="${asContractId}" />
<portlet:param name="budgetType" value="${budgetType}" />
<portlet:param name="fiscalYearID" value="${fiscalYearId}" />
</portlet:actionURL>
<div class='tabularCustomHead'><span>Confirm Budget Modification</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>
<div class='tabularContainer'>
<div id="errorMessageDiv" class="failedShow" style="display:none;"><c:if test="${asError ne 'no error' && not empty asError}"><fmt:message key="${asError}"/></c:if></div>
<%-- Overlay Popup Starts --%>
	<p>Proceeding will begin the process of modifying the Budget. Click on the Create Budget Modification button to continue.
	</p>
    <div class="buttonholder">
    	<input type="button" class="graybtutton" title="Cancel" value="Cancel" onclick="clearAndCloseOverLay()" />
    	<input type="button" class="button" title="Create Budget Modification" value="Create Budget Modification" onclick="modifyBudgetButton()" />
    </div>
</div>
<form:form id="confirmBudgetModificationForm" name="confirmBudgetModificationForm" action="${navigateToContractModification}">

</form:form>
    <a href="javascript:void(0);" class="exit-panel"></a> 
 <%-- Overlay Popup Ends --%>
