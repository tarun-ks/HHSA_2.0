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
<%-- 
This jsp is used for Personnel Services modification shown in Contract Budget Modification module.
 --%>

<c:set var="readOnlyPageAttribute" value="false"></c:set>
<c:if test="${subGridReadonly ne null}">
<c:set var="readOnlyPageAttribute" value="true"></c:set>
</c:if>

<c:set var="isNegativeAmend" value="true"></c:set>

<%-- set the amendment Type for positive and negative--%>
<c:set var="amendmentGrid" value="positive,positiveAmendmentMsg"></c:set>
<c:if test="${amendmentType ne 'positive'}">
<c:set var="amendmentGrid" value="negative,negativeAmendmentMsg"></c:set>
<c:set var="isNegativeAmend" value="false"></c:set>
</c:if>

<%-- Non-Grid data Starts --%>
<h3>Personnel Services</h3>
<div class="formcontainer paymentFormWrapper widthFull">
	<div class="row">
	     <span class="label">Amendment Total Salary &amp; Fringe:</span>
	     <span class="formfield">
	     	<span class='lftAmount'><label id="val1${subBudgetId}">${personnelServiceData.totalSalaryAndFringeAmount}</label></span>
	     </span>
	</div>
	 <div class="row">
	     <span class="label">Amendment Total Salary:</span>
	     <span class="formfield">
	     	<span class='lftAmount'><label id="val2${subBudgetId}">${personnelServiceData.totalSalaryAmount}</label></span>
	      </span>
	    </div>  
	<div class="row">
	  <span class="label">Amendment Total Fringe:</span>
	  <span class="formfield">
	  	<span class='lftAmount'><label id="val3${subBudgetId}">${personnelServiceData.totalFringeAmount}</label></span>
	 	<span class='rhtAmount' id="val5${subBudgetId}">${personnelServiceData.fringePercentage}</span>
	  </span>
	</div>
	
</div>
<%-- Non-Grid data Ends --%>

<div class='clear'>&nbsp;</div>

<%-- Salaried Employee Grid Starts --%>
<portlet:resourceURL var='SubGridHeaderRow' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentBudgetSalariedEmployee.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="amendmentBudgetSalariedEmployee.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='salariedEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="salariedEmployeeGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<c:set var="gridColNames"><%=HHSUtil.getHeader("amendmentBudgetSalariedEmployee.grid")%></c:set>
<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("amendmentBudgetSalariedEmployee.grid")%></c:set>
<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("amendmentBudgetSalariedEmployee.grid")%></c:set>

<jq:grid id="salariedEmployeeGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames}" 
		 gridColProp="${gridColProp}"          
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:true, editrules:{},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMin:'-99999.99',vMax:'99999.99'});}, 100);}}},
                  {editable:true, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow}"
		 subGridUrl="${loadGridData}"
	     cellUrl="${salariedEmployeeOperationGrid}"
	     editUrl="${salariedEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""	     
         isPagination="true"
	     rowsPerPage="5"
	     modificationType="${amendmentGrid}"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="budgetAmount,remainingAmount" 
         negativeCurrency="amendmentAmount"               
   	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     dropDownData="${personnelServiceMasterData}"
/>
<%-- Salaried Employee Grid Ends --%>

<%-- Hourly Employee Grid Starts --%>
<portlet:resourceURL var='SubGridHeaderRow1' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentBudgetHourlyEmployee.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData1' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="amendmentBudgetHourlyEmployee.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='hourlyEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="hourlyEmployeeGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames1"><%=HHSUtil.getHeader("amendmentBudgetHourlyEmployee.grid")%></c:set> 
<c:set var="gridColProp1"><%=HHSUtil.getHeaderProp("amendmentBudgetHourlyEmployee.grid")%></c:set> 
<c:set var="subGridColProp1"><%=HHSUtil.getSubGridProp("amendmentBudgetHourlyEmployee.grid")%></c:set> 
<br>
<jq:grid id="hourlyEmployeeGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames1}" 
	     gridColProp="${gridColProp1}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:true, editrules:{},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMin:'-99999.99',vMax:'99999.99'});}, 100);}}},
                  {editable:true, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow1}"
		 subGridUrl="${loadGridData1}"
	     cellUrl="${hourlyEmployeeOperationGrid}"
	     editUrl="${hourlyEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
	     modificationType="${amendmentGrid}"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="budgetAmount,remainingAmount" 
         negativeCurrency="amendmentAmount"               
   	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     dropDownData="${personnelServiceMasterData}"
/>
<%-- Hourly Employee Grid Ends --%>

<%-- Seasonal Employee Grid Starts --%>
<portlet:resourceURL var='SubGridHeaderRow2' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentBudgetSeasonalEmployee.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData2' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="seasonalEmployeeGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="amendmentBudgetSeasonalEmployee.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='seasonalEmployeeOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="seasonalEmployeeGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames2"><%=HHSUtil.getHeader("amendmentBudgetSeasonalEmployee.grid")%></c:set> 
<c:set var="gridColProp2"><%=HHSUtil.getHeaderProp("amendmentBudgetSeasonalEmployee.grid")%></c:set> 
<c:set var="subGridColProp2"><%=HHSUtil.getSubGridProp("amendmentBudgetSeasonalEmployee.grid")%></c:set> 
<br>
<jq:grid id="seasonalEmployeeGrid-${subBudgetId}" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="${gridColNames2}" 
	     gridColProp="${gridColProp2}" 
	     subGridColProp="{editable:true, editrules:{isMandatoryField},edittype : 'select',editoptions : {}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:false, editrules:{required:true,number:true}},
                  {editable:true, editrules:{},editoptions:{dataInit: function (elem){setTimeout(function(){$(elem).autoNumeric({aSep:'',vMin:'-99999.99',vMax:'99999.99'});}, 100);}}},
                  {editable:true, editrules:{required:true,number:true}}"
		 gridUrl="${SubGridHeaderRow2}"
		 subGridUrl="${loadGridData2}"
	     cellUrl="${seasonalEmployeeOperationGrid}"
	     editUrl="${seasonalEmployeeOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="true"
	     rowsPerPage="5"
	     modificationType="${amendmentGrid}"
         isSubGrid="true" isNewRecordDelete="true"
         nonEditColumnName="budgetAmount,remainingAmount"
         negativeCurrency="amendmentAmount"      
   	     operations="del:true,edit:true,add:${isNegativeAmend},cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
   	     dropDownData="${personnelServiceMasterData}"
/>
<%-- Seasonal Employee Grid Ends --%>

<%-- Fringe Benefits Grid Starts --%>

<portlet:resourceURL var='SubGridHeaderRow3' id='SubGridHeaderRow' escapeXml='false'>
<portlet:param name="gridLabel" value="amendmentBudgetFringeBenefits.grid"/>
</portlet:resourceURL>
<portlet:resourceURL var='loadGridData3' id='loadGridData' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenefitsGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.CBGridBean"/>
<portlet:param name="gridLabel" value="amendmentBudgetFringeBenefits.grid"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL>
<portlet:resourceURL var='fringeBenefitsOperationGrid' id='gridOperation' escapeXml='false'>
<portlet:param name="transactionName" value="fringeBenefitsGridAmendment"/>
<portlet:param name="beanName" value="com.nyc.hhs.model.PersonnelServiceBudget"/>
<portlet:param name="subBudgetId" value="${subBudgetId}"/>
<portlet:param name="parentSubBudgetId" value="${parentSubBudgetId}"/>
</portlet:resourceURL> 

<c:set var="gridColNames3"><%=HHSUtil.getHeader("amendmentBudgetFringeBenefits.grid")%></c:set> 
<c:set var="gridColProp3"><%=HHSUtil.getHeaderProp("amendmentBudgetFringeBenefits.grid")%></c:set> 
<c:set var="subGridColProp3"><%=HHSUtil.getSubGridProp("amendmentBudgetFringeBenefits.grid")%></c:set> 
<br>
<jq:grid id="fringeBenefitsGrid-${subBudgetId}_" 
        isReadOnly="${readOnlyPageAttribute}"
         gridColNames="'Fringe Benefits','Approved FY Budget','Remaining Amount','','Amendment Amount'" 
	     gridColProp="{name:'fringeBenifits',width:'330'},{name:'budgetAmount',template:currencyTemplate},{name:'remainingAmount',template:currencyTemplate},{name:''},{name:'amendmentAmount',template:currencyTemplate}" 
	     subGridColProp="{editable:false,editrules:{required:true}},{editable:false,editrules:{required:true,number:true}},{editable:false,editrules:{required:true,number:true}},{classes:'cellColor'},{editable:true,editrules:{required:true,number:true}}" 
		 gridUrl="${loadGridData3}"
		 subGridUrl=""
	     cellUrl="${fringeBenefitsOperationGrid}"
	     editUrl="${fringeBenefitsOperationGrid}"
	     dataType="json" methodType="POST"
	     columnTotalName=""
         isPagination="false"
	     rowsPerPage="5"
	     modificationType="${amendmentGrid}"
         isSubGrid="false" 
         nonEditColumnName="budgetAmount,remainingAmount" 
         negativeCurrency="amendmentAmount"         
   	     operations="del:false,edit:true,add:false,cancel:true,save:true"
   	     callbackFunction="refreshNonGridData('${subBudgetId}');"
/>
<%-- Fringe Benefits Grid Ends --%>
	<%--code updation for R4 starts--%>
<div>&nbsp;</div>
<c:set var="entityType"><%=HHSConstants.AUDIT_CONTRACT_BUDGET_AMENDMENT%></c:set>
<c:set var="entityTypeForAgency"><%=HHSConstants.TASK_BUDGET_AMENDMENT%></c:set>
<div class='gridFormField'>
<c:choose>
<c:when test="${detailsBeanForTaskGrid.isTaskScreen}">
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_personnelServices_${subBudgetId}"
	isTaskScreen=""
	commentsSection="comments"
	level="footer">
	</task:taskContent>
</c:when>
<c:otherwise>
	<task:taskContent
	workFlowId=""
	taskType="${entityTypeForAgency}"
	entityTypeTabLevel="TLC_personnelServices_${subBudgetId}"
	level="footer"
	textAreaSize="3000">
	</task:taskContent>
</c:otherwise>
</c:choose>
</div>
	<%--code updation for R4 ends--%>
<script>
$(document)
.ready(
		function() {
			var subBudgetID = ${subBudgetId};
			$("#val1" + subBudgetID).jqGridCurrency();
			$("#val2" + subBudgetID).jqGridCurrency();
			$("#val3" + subBudgetID).jqGridCurrency();
			
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

</script>