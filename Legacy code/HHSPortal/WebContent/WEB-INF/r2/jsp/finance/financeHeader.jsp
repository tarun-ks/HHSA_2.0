<%--This JSP is used to display headers for finance--%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
	  	<h2>Financials</h2>
	  	<!--Start Updated in R5 -->
	  	<div class="appnavbar appnavbarListAction" id="nyc_app_sections">
	<!--End Updated in R5 -->
	<ul class="roundcorners">
	<!--Made changes for Emergency Build 4.0.1 defect 8360 -->
		<li><a class="" id="section_contractListAction" href="<portlet:renderURL><portlet:param name='action' value='contractListAction' /><portlet:param name='fromFinancialTab' value='true' />' /></portlet:renderURL>">Contract List</a></li>
		<li><a class="" id="section_budgetListAction" href="<portlet:renderURL><portlet:param name='action' value='budgetListAction' /><portlet:param name='fromFinancialTab' value='true' />' /></portlet:renderURL>">Budget List</a></li>
		<li><a class="" id="section_invoiceListAction" href="<portlet:renderURL><portlet:param name='action' value='invoiceListAction' /><portlet:param name='next_action' value='invoice' /><portlet:param name='fromFinancialTab' value='true' />' /></portlet:renderURL>">Invoice List</a></li>
		<li><a class="" id="section_paymentListAction" href="<portlet:renderURL><portlet:param name='action' value='paymentListAction' /><portlet:param name='fromFinancialTab' value='true' />' /></portlet:renderURL>">Payment List</a></li>
			<%--code updation for R4 starts--%>
		<li><a class="" id="section_amendmentListAction" href="<portlet:renderURL><portlet:param name='action' value='contractListAction' /><portlet:param name='next_action' value='amendment' /><portlet:param name='fromFinancialTab' value='true' />' /></portlet:renderURL>">Amendment List</a></li>
			<%--code updation for R4 ends--%>
	</ul>
 </div>
  <div class='clear'></div>
 