<%-- JSP Added  in R4--%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
  
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/updateBudgetTemplate.js"></script>
	

<portlet:resourceURL var='UpdateBudgetTemplate' id='UpdateBudgetTemplate' escapeXml='false'/>
	<input type="hidden" id="hdnUpdateBudgetTemplater" value="${UpdateBudgetTemplate}"/>   
	
<div class="content">
	<div class='tabularCustomHead'><span id="contractTypeId">Update Budget Template
		</span> <a href="javascript:void(0);" class="exit-panel">&nbsp;</a></div>

<div class="tabularContainer">
	<h2>Update Budget Template</h2>
	<div class='hr'></div>
	
		<p>To update the budget template, please select the budget categories to be added <br>
		   and then click the 'Update Template' button. Please note that budget categories <br>
		   cannot be removed once they are added.
		</p>
		

<form:form id="submitUpdateBudgetForm" action="" method="post" name="submitUpdateBudgetForm">

	<input type="hidden" value="${contractId}" name="contractId" id="contractId"/>	
	<input type="hidden" value="${budgetId}" name="budgetId" id="budgetId"/>
	<input type="hidden" value="${fiscalYearId}" name="fiscalYearId" id="fiscalYearId"/>
	<input type="hidden" value="${entryTypeId}" name="entryTypeId" id="entryTypeId"/>
	

<table id="budgetCustomizeTab">
	<tr>
		<td><input type="checkbox" id="1" name="1" />Personnel Services</td>
		<td><input type="checkbox" id="5" name="5" />Rent</td>
		<td><input type="checkbox" id="9" name="9" />Unallocated Funds</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="2" name="2" />Operations and Support</td>
		<td><input type="checkbox" id="6" name="6" />Contracted Services</td>
		<td><input type="checkbox" id="10" name="10" />Indirect Rate</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="3" name="3" />Utilities</td>
		<td><input type="checkbox" id="7" name="7" />Rate</td>
		<td><input type="checkbox" id="11" name="11" />Program Income</td>
	</tr>
	<tr>
		<td><input type="checkbox" id="4" name="4" />Professional Services</td>
		<td><input type="checkbox" id="8" name="8" />Milestone</td>
		<td></td>
	</tr>
</table>
</form:form>


<div class="buttonholder">
	<input type="button" class="graybtutton" title="" value="Cancel" onclick="cancelUpdateTemplate();"/> 
	<input type="button" class="button" title="" value="Update Template" onclick="UpdateTemplate();" />
</div>
</div>

<%-- Overlay Popup Ends --%>

</div>