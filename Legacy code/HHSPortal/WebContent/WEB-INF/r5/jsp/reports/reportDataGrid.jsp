<%@page import="com.nyc.hhs.frameworks.grid.ColumnTag"%>
<%@ taglib uri="/WEB-INF/tld/custom-birt.tld" prefix="cb"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>

<script type="text/javascript" >
$(document).ready(function() {
	$(".tableContractValue").each(function(e) {
		$(this).autoNumeric('init', {vMax: '9999999999999999',vMin:'-9999999999999999.99'});
	});
});
</script>
		<st:table objectName="reportListObject" cssClass="heading"
			alternateCss1="evenRows" alternateCss2="oddRows"
			pageSize='${allowedObjectCount}'>
			<c:forEach items="${loColumnTagList}" var="columnTag">
			<st:property headingName="${columnTag.headingName}" columnName="${columnTag.columnName}" size="${columnTag.size}" >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DetailedReportExtension"/>
			</st:property>
			</c:forEach>
			
		</st:table>
		<c:if test="${fn:length(reportListObject) eq 0}">
			<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No
				Records Found</div>
		</c:if>

<%--code for receivables--%>
<c:if test="${fn:contains(loReportValue, 'Receivables')}">
	<jsp:include page="receivablesGridNote.jsp"></jsp:include>
</c:if>
<%--code for receivables--%>


<input type="hidden" value="${tabName}" id="tabName" />

		