<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil"%>
<%@page
	import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<fmt:setBundle basename="com/nyc/hhs/properties/statusproperties" />
<portlet:defineObjects />
<%-- 
This jsp is used for payment Chart of allocation grid in Payment module
 for grids with static headers.
 --%>

<H3>Chart of Accounts Allocation</H3>

<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow'escapeXml='false'>
	<portlet:param name="gridLabel" value="payment.paymentCOF.grid" />
</portlet:resourceURL>


<%-- Code for Security matrix and readonly value --%> 
<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>


<c:set var="gridColNames"><%=HHSUtil.getHeader("payment.paymentCOF.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("payment.paymentCOF.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("payment.paymentCOF.grid")%></c:set>

<%-- loading the page  --%>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
	<portlet:param name="transactionName" value="paymentCOF" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.PaymentChartOfAllocation" />
	<portlet:param name="gridLabel" value="payment.paymentCOF.grid" />
</portlet:resourceURL>

<%-- operations for Edit/Update/Save --%>
<portlet:resourceURL var='paymentCOFOperationGrid' id='gridOperation' escapeXml='false'>
	<portlet:param name="transactionName" value="paymentCOF" />
	<portlet:param name="beanName" value="com.nyc.hhs.model.PaymentChartOfAllocation" />
</portlet:resourceURL>

<%-- JGrid for adding dynamic table --%>
			<jq:grid id="paymentCOFGrid" 
			 isReadOnly="${readOnlyPageAttribute}"
				gridColNames="${gridColNames}"
				gridColProp="${gridColProp}" 
				subGridColProp="${subGridColProp}"
				gridUrl="${SubGridHeaderRow}"
				subGridUrl="${loadGridData}"
				cellUrl="${paymentCOFOperationGrid}"
				editUrl="${paymentCOFOperationGrid}"
				dataType="json"
				methodType="POST" 
				columnTotalName="" 
				isPagination="true"
				rowsPerPage="5" isSubGrid="true"
				positiveCurrency="paymentAmount"  
				operations="del:false,edit:true,add:false,cancel:true,save:true" />
	
