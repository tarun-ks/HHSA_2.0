<%-- The jsp is added in Release 6 for return payment. --%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>
<%-- Commenting below code as part of defect : 8627 fix --%>
<%-- <script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/contractBudget.js"></script> --%>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/returnedPaymentList.js"></script>

<portlet:resourceURL var='viewReturnedPayment' id="viewReturnedPayment"></portlet:resourceURL>
<!-- Changed for defect 8591 -->
<script type="text/javascript">
$(".tableCheckAmountValue").each(function(e) {
	$(this).html(jqGridformatCurrency($(this).html()).replace('$',''));
}); 
</script>

<input type='hidden' value='${viewReturnedPayment}'
	id='hiddenViewReturnedPayment' />
<!-- Including javaScript Files -->
<!-- This Jsp will display options for read only screen-->
<div id="returnedPaymentListTable" class="tabularWrapper">
	<st:table objectName="returnedPaymentCheckList" cssClass="heading"
		alternateCss1="evenRows" alternateCss2="oddRows">
<!--  Fix for alignment of grid -->
		<%--  Added extension for enhancement 8652--%>
		<st:property headingName="Check #" columnName="checkNumber" size="7%">
		<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ReturnedPaymentAgencyActionExtension" />
		</st:property>
		<st:property headingName="Agency Tracking #"
			columnName="agencyTrackingNumber" size="16%">
		</st:property>

		<st:property headingName="Received Date" columnName="receivedDate"
			size="15%">
		</st:property>
		<st:property headingName="Approved By" size="18%"
			columnName="approvedBy" sortType="approvedBy">
		</st:property>
		<st:property headingName="Check Amount($)" columnName="checkAmount"
			size="19%">

			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ReturnedPaymentAgencyActionExtension" />
		</st:property>
		<st:property headingName="Status" columnName="checkStatusName"
			size="15%">
		</st:property>
		<st:property headingName="Action" columnName="action" size="20%">
			<st:extension
				decoratorClass="com.nyc.hhs.frameworks.grid.ReturnedPaymentProviderCityActionExtension" />
		</st:property>
	</st:table>
	<c:if test="${fn:length(returnedPaymentCheckList) eq 0}">
	<div class="noRecordPaymentDiv noRecord" id="noRecordPaymentDiv">No Records Found</div>
	</c:if>
</div>