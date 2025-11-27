<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@page import="com.nyc.hhs.util.HHSUtil" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<fmt:bundle basename="com/nyc/hhs/properties/statusproperties"/>
<portlet:defineObjects />

<script type="text/javascript">
$(document).ready(function(){
	//var $subBudgetId = '${subBudgetId}';
	//refreshPSSummaryNonGridData($subBudgetId,'PSDetailScreen');
	$('#detailedView'+'${subBudgetId}').removeClass().addClass('blackLock');
	//Start: Added in 8470
	// Updated for 8468
	$(".tabChange"+'${subBudgetId}').click(function(event) {
		var idToSearch = '-'+$(event.target).closest(".accContainer").find("#hdnGridSubBudgetId").val()+'_';
		var validateFlag = false;
		for(var i=0; i< clickOnGridArr.length;i++ ){
		if(clickOnGridArr[i].indexOf(idToSearch) > 0){
			validateFlag = true;
			break;
		}
	}
	if(validateFlag){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						for(var i=clickOnGridArr.length-1; i>= 0;i-- ){
						if(clickOnGridArr[i].indexOf(idToSearch)!=-1){
							clickOnGridArr.splice(i,1);
						}
					}
						showPsSCreen($(event.target).attr("jspname"), $(event.target).closest(".accContainer").find("#hdnGridDivId").val(), $(event.target).closest(".accContainer").find("#hdnGridSubBudgetId").val(),$(event.target).closest(".accContainer").find("#hdnGridParentSubBudgetId").val());
						$(this).dialog("close");
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
			return false;
		}
		else{
			showPsSCreen($(event.target).attr("jspname"), $(event.target).closest(".accContainer").find("#hdnGridDivId").val(), $(event.target).closest(".accContainer").find("#hdnGridSubBudgetId").val(),$(event.target).closest(".accContainer").find("#hdnGridParentSubBudgetId").val());	
		}
	});
	//End: Added in 8470
});

</script>
<%-- 
This jsp is used for Personnel Services modification shown in Contract Budget Modification module.
 --%>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<h3>Personnel Services - Detail</h3>
<%-- Start: Added in 8470--%>
<div class="buttonholder">
   	<input type="button" class="graybtutton tabChange${subBudgetId}" value="Summary View" jspname='personnelServicesModificationSummary' id="summaryView${subBudgetId}"/>
   	<input type="button" class="blackLock tabChange${subBudgetId}" value="Detail View" jspname='personnelServicesModificationDetail' id="detailedView${subBudgetId}"/>
</div>
<%-- End: Added in 8470--%>
<jsp:include page="/WEB-INF/r2/jsp/contractbudget/personnelServiceDetailGrids.jsp"></jsp:include>



<!-- <script>
$(document)
.ready(
		function() {				
			var subBudgetID = ${subBudgetId};
			$("#val1" + subBudgetID).jqGridCurrency();
			$("#val2" + subBudgetID).jqGridCurrency();
			$("#val3" + subBudgetID).jqGridCurrency();
			$("#val4" + subBudgetID).jqGridCurrency();
			if($('#val5'+subBudgetID).html() == 0){
				$("#val5" + subBudgetID).html('(0.00%)');
			}else{
				if($('#val5'+subBudgetID).html().indexOf('E-') !== -1 || $('#val5'+subBudgetID).html().indexOf('e-') !== -1){
					$("#val5" + subBudgetID).html('('+new Big(Math.round($('#val5'+subBudgetID).html().replaceAll('e-',0).replaceAll('E-',0) * 100) / 100).toFixed(2)+'%)');
				}else{
					$("#val5" + subBudgetID).html('('+new Big(Math.round($('#val5'+subBudgetID).html() * 100) / 100).toFixed(2)+'%)');				
				}
			}
		});

</script> -->
<%-- Added in R7 for Program income grid in budget categories --%>
<c:if test="${(oldPIFlag eq 0) and (isPISelected == 'true')}">
&nbsp;
<jsp:include page="programIncomeModification.jsp">
	<jsp:param value="1" name="entryTypeId" />
	<jsp:param value="true" name="subGridReadonly" />
</jsp:include>
</c:if>
<%-- R7 changes end --%>



