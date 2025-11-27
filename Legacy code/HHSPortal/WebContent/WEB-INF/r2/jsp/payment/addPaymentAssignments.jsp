<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSConstants"%>
<portlet:defineObjects />
<%--
This file is used to display Overlay for Add Assignee
 --%>

<%-- Resource URL to get Vendor List for Auto complete functionality in Vendor text-box --%>
<portlet:resourceURL var="getPaymentVendorListUrl" id="getPaymentVendorListUrl" escapeXml="false">
</portlet:resourceURL>

<%-- Resource URL to submit AddAssignee Form --%>
<portlet:resourceURL var='addPaymentAssigneeSubmitUrl' id='addPaymentAssigneeSubmitUrl' escapeXml='false'>
</portlet:resourceURL>

<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/addPaymentAssignee.js"></script>
<script type="text/javascript" >

</script>

<input type = 'hidden' value='${getPaymentVendorListUrl}' id='getVendorList'/>
<input type = 'hidden' value='${addPaymentAssigneeSubmitUrl}' id='addAssigneeSubmit'/>

<%-- Overlay Popup Starts --%>

<div class="alert-box----">
    <div class="tabularCustomHead">Add Assignee</div>
    <div class="tabularContainer"> 
    <%if(( null != request.getAttribute(HHSConstants.CLC_CAP_ERROR) ) && !"".equalsIgnoreCase((String)request.getAttribute(HHSConstants.CLC_CAP_ERROR))){%>
	     <div id="transactionStatusDiv" class="failed breakAll" style="display:block" ><%=request.getAttribute(HHSConstants.CLC_CAP_ERROR)%> </div>
	<%}%>
	 <div id="ErrorDiv" class="failed breakAll"  > </div>
	<h2>Add Vendor To "Assignees" List</h2>
	<div class='clear'>&nbsp;</div>
	
	 
<span id="errorMsgInternal" class="error"></span> 
	<form:form id="addAssigneeVendorForm" action="" method ="post" name="addAssigneeVendorForm">
	<input type = 'hidden' value='' name="providerId" id='providerId'/>
	<input type = 'hidden' value='${asBudgetId}' name="asBudgetId" id='asBudgetId'/>
	
	<div class="formcontainer">
		<div class="row">
			  <span class="label clear"><label for='txtVendorname'>Vendor Name:</label></span>
			  <span class="vendorfield"><input id="txtVendorname" name="txtVendorname" type="text" maxlength="20" class='input' value=''  /></span>
		</div>
	</div>
		
    <div class="buttonholder">
    	<input id="cancel" type="button" class="graybtutton" title="Cancel" value="Cancel"  onclick="clearAndCloseOverLay1();" />
    	<input id="addAssignee" type="button" class="button" title="Add Assignee" value="Add Assignee" disabled="disabled"/>
    </div>
  
    </div>
    <a href="javascript:void(0);" class="exit-panel"></a> 
	</div>
</form:form>
  <%-- Overlay Popup Ends --%>
  
</body>
</html>

