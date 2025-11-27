<%-- This jsp file has been added in R4--%>
<%-- This JSP is for add new tag overlay--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@page import="javax.portlet.*"%>

<c:set var="transactionMsg" value="${transactionMessage}"></c:set>

<c:if test="${transactionStatus eq 'failed'}">
   <div id="transactionStatusDiv" class="failed" style="display:block" >${transactionMsg}</div>
</c:if>

    <div class="tabularCustomHead">Remove All Tags in Bulk</div>
    <div class="tabularContainer"> 
     <div id="ErrorDiv" class="failed breakAll"  > </div>
     <c:if test="${message ne null}">
		<div class="${messageType}" id="messagediv" style="display:block">${message} <img
			src="${pageContext.servletContext.contextPath}/framework/skins/hhsa/images/iconClose.jpg" id="box"
			class="message-close" title="Close" alt="Close"
			onclick="showMe('messagediv', this)">
		</div>
	</c:if>
	<h2>Remove All Tags</h2>
     	<p ><hr style="border-top: dotted 1px;" />
     	Are you sure you want to remove all tags from the selected proposals?<br/><br/>
		Once you remove all tags, you will not be able to undo this action. If you want to tag these proposals again, you will need to tag these proposals through the individual or bulk tagging process.</p>
    	<div class='hr'></div>
<div id='tagListWrapper' class="addNewTagInBulkPopUpDiv">
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
	<div class="buttonholder">
		<input type='button' id='cancel' class='graybtutton' value='Cancel' onclick="closeRemoveAllTaxRender()"/>
		<input type='button' id='removeAllTaxInBulk' class='redbtutton' value='Yes, remove ALL tags from the selected proposals' onclick="removeAllTaxRender(false)" />
	</div>	
</div>
	<input type="hidden"  value="${proposalId}" id="proposalIdRemoveInBulk"/>
	<input type="hidden"  value="${procurementId}" id="procurementIdRemoveInBulk"/>
	<input type="hidden"  value="${contractId}" id="contractIdRemoveInBulk"/>
