<%-- This JSP file is for generating Contract Certification of Funds Document S383.%--%>

<%--
Below HTML tags are used for generating Table with <TR> <TD> elements, to display Contract Certification of funds
read only document.
Grid data for "Chart of Accounts Allocation" and "Funding Source Allocation" along with header details are iterated
in <TR> <TD>. 
Header and Account details with FY are pre-populated in ProcurementCOF bean from the ConfigurationService class and 
ContractListController class.
Fields are used here in HTML tags to display the document. Account Details with FY's are iterated using <TR> <TD> tags 
with an array list of same bean fields to show "Chart of Accounts Allocation" and "Funding Source Allocation" details.
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nyc.hhs.model.AccountsAllocationBean"%>
<%@page import="org.apache.commons.beanutils.BeanUtils"%>
<%@page import="com.nyc.hhs.model.FundingAllocationBean"%>
<%@page import="com.nyc.hhs.model.ProcurementCOF"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.HHSComponentMappingConstant"%>



<portlet:resourceURL var="viewContractCOFUrl" id="viewContractCOF" escapeXml="false">	
</portlet:resourceURL>
<input id="viewContractCOFUrlId" type="hidden" value="${viewContractCOFUrl}" />

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="css/nyc.css" media="all" type="text/css" />
<title>NYC_Human Health Services Accelerator</title>
</head>
<body>
<script type="text/javascript">
//code updation for R4 starts
$(document).ready(function(){
	var isAmendmentAwardEPin = $("#isAmendmentAwardEPin").val();
	if(isAmendmentAwardEPin != null && isAmendmentAwardEPin != "")
			{
			$('.selDiv option:contains('+$("#isAmendmentAwardEPin").val()+')').prop('selected', true);
			}
});
//	code updation for R4 starts
		function jsFunction(){
			var baseAwardEpin = '${ProcurementCOF.awardEpin}';
			var selectVal = $('#awardEPIN :selected').val();
			var v_parameter =  "selectVal=" + selectVal + "&baseAwardEpin=" + baseAwardEpin;	
			var urlAppender = $("#viewContractCOFUrlId").val();
			jQuery.ajax({
				type : "POST",
				url : urlAppender,
				data : v_parameter,
				success : function(e) {
					$("#resourceDiv").html(e);	
				},
				error : function(data, textStatus, errorThrown) {
					showErrorMessagePopup();
				}
			});
			
}
</script>
	<%--code updation for R4 starts--%>
<div class="selDiv">
	<%--code updation for R4 ends--%>
	<select id="awardEPIN" name="awardEPIN" value="" class='contractCOFDropDown' onchange="jsFunction()">
		<c:forEach var="contractDetails" items="${awardEpinDropDown}">
			<OPTION value="${contractDetails.contractId}-${contractDetails.contractStartDate}-${contractDetails.contractEndDate}">${contractDetails.awardEpin}</OPTION>
		</c:forEach>
	</select>
	</div>
		<%--code updation for R4 starts--%>
	<input type="hidden" id="isAmendmentAwardEPin" value="${AwardEPin}"/>
		<%--code updation for R4 ends--%>
	
	<div class="hr"></div>

<div id="resourceDiv">

<%@include file="contractCOFResourceDoc.jsp" %>

</div>
<p>&nbsp;</p>


</body>
</html>


<script type="text/javascript">
$(document)
.ready(
		function() {
			$(".nycgov_header").hide();
			$("#nyc_header_div").hide();
			$(".breadcrumb").hide();
			$(".footer").hide();
		});
</script>