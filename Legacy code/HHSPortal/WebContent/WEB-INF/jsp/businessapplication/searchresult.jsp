<!-- This page will be displayed when a user click on the search button on service summary.
It display  list of Search Results services that can be selected.-->
<%@page import="com.nyc.hhs.model.TaxonomyServiceBean"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="java.util.List"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<form method="post" action="<portlet:actionURL/>" name="searchresultform">
<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Applications');"></a></div>
<div class="overlay"></div>
<div class="alert-box-contact">
	<div class="content">
		<div id="newTabs">
			<div id="contactDiv"></div>
		</div>
	</div>
</div>
<h3 style="display:inline">Add Services</h3>

<p>Below is a full list of Services for which your organization may apply.   Services are grouped by category.  To add a Service, click the "Add" button or the 
			"Continue" button to view more Services.</p>
<p>You must add at least one Service to complete your HHS Accelerator Application. For each Service selected, you will be required to add supporting information.
		 A Service search is also available at the bottom of the page.  Once you have finished your Service selection, click the "Complete Selections" button on the bottom 
		 of the page. </p>
<div class="selectedContainer">
	<h4>Selected Services</h4>

<ul id="selected_Services" ></ul>

<div class="hr"></div>
<!-- Selected Container end -->

<!-- Search Container Start -->
<div class="searchContainer">
	<h5>Search</h5>
	<input type="text" size="44" /><input type="button" value="Clear" class="button" title="Clear" onclick="searchValue('search')" />
	
	<input type="submit" value="Search" title="Search" class="button" />
</div>
<!-- Search Container End -->
<div class="tabularWrapper">
	<table style="min-height:200px;">
		<tr>
			<th style="text-align:right; padding-right:10px;">Service</th>
			<th colspan="2">Definition</th>
		</tr>
		<%	List<TaxonomyTree> loLRelatedServices = null;
		if(request.getAttribute("loRelatedServicesList")!=null ){
			loLRelatedServices = (List<TaxonomyTree>) request.getAttribute("loRelatedServicesList");
		}
		Iterator<TaxonomyTree> loItr = loLRelatedServices.iterator();
		while(loItr.hasNext()){
			TaxonomyTree loService = loItr.next();
			%>
			<tr>
			<td width="100px" style="text-align:right; padding-right:10px; border-bottom: 1px dotted;"><b><%=loService.getMsDisplayName()%></b></td>
			<td width="680px" style="text-align:left; border-bottom: 1px dotted;"><%=loService.getMsElementDescription()%></td>
			<td width="100px" style="border-bottom: 1px dotted;">
				<input id="myButton<%=loService.getMsElementid()%>" type="button" class="button" style="float:right;" value="+ Add" title="+ Add" onclick="addRemoveService('<%=loService.getMsDisplayName()%>',this,'<%=loService.getMsElementid()%>');"/>
			</td>
			</tr>
			<%}%>
	</table>
</div>
<div class="hr"></div>
<div class="buttonholder">
	<input type="button" class="button" title="Cancel" value="Cancel" />
	<input name="saveService" type="button" class="button" value="Complete Selection" title="Complete Selection" onclick="setValue('saveServices')"/>
	<input type="hidden" name="selectedService" value="" id="addSelectedServices">
	<input type="hidden" name="submitButtonValue" value="" id="saveServices">
	<input type="hidden" name="subsection" value="" id="addservice">
</div>
</form>