<!-- This page will be displayed when a user click on the add service button.It display  list of services that can be selected.-->
<%@page import="com.nyc.hhs.model.TaxonomyServiceBean"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
	<portlet:defineObjects />
<style type="text/css">
	.returnButton, .searchResultsContainer, .displayNone{
		display:none;
	}
	.individualError{
		float: none;
	}
	.titleClass{
		color: #666;
	    float: left;	   
	    padding: 4px 8px 4px 0;
	    width: 12%;
	    word-wrap: break-word;	    
	    text-align: right;
	    font-weight: bold;
	}
	.descriptionTreeClass {
	    color: #666;
	    float: left;	    
	    padding: 4px 10px 4px 8px;
	     width: 77%;
	    /*word-break: break-all;*/	     
	    position: relative;	   
	    min-height: 28px;	
	    word-wrap: break-word;    
	}
	.descriptionTreeClass input {
	    float: right;
	    position: absolute;
	    right: -72px;
	    top: 4px;
	}
	.descriptionTreeClass .ulTreeClass {
		float:left;
	}
	.liTreeClass { 
	    clear: both;
	    border-bottom: 1px solid #E4E4E4;
	}
	.iconQuestion{
		margin-left: 6px;
	}
	.col3{
		width: auto;
		text-align: right;
	}
</style>
<script type="text/javascript" src="../resources/js/addService.js"></script>
<script type="text/javascript">
	$(function(){
		<c:forEach var="map1" items="${loBusinessStatusBeanMap}">
			setStatusSection("${map1.key}", "${map1.value.msSectionStatus}");
		</c:forEach>
	  });
</script>

<form method="post" action="<portlet:actionURL><portlet:param name='business_app_id' id='business_app_id' value='${business_app_id }' /></portlet:actionURL>" name="addservice">
	<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
		<h2 style='width: 72%;'>Add Services</h2> 
		<div class="individualError" id="errorMessage"></div>
		<div class="overlay"></div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
		<input type="button" title="Return" class="floatRht returnButton" name="returnButton" value="Return"/>
		<div class='hr'></div>
		<div id="addServiceText">
		   <p>Below is a full list of Services for which your organization may apply.   Services are grouped by category.  To add a Service, click the "Add" button or the 
		   			"Continue" button to view more Services.</p>
		   <p>You must add at least one Service to complete your HHS Accelerator Application. For each Service selected, you will be required to add supporting information.
		   		 A Service search is also available at the bottom of the page.  Once you have finished your Service selection, click the "Complete Selections" button on the bottom 
		   		 of the page. </p>
		</div>
		<div id="searchServiceText" style="display:none">
			<p>Below are the search results for the keyword(s) you entered. Click the "Add" button next each Service to 
					add it to the list of serviceServices your organization would like to apply for.</p>
		   	<p>Once all the Services have been searched for and added to the "Selected Services" table, click the "Return" button 
					on the top right corner of the page. </p>
		</div>
		<div class='evenRows floatRht' style='margin-top:-11px;'>
			<a href="javascript:;" id="removeAll" title='Remove All' class="displayNone"><b>Remove All</b></a>
		</div>
		<div class="selectedContainer">
			<h4>Selected Services</h4>
			<ul id="selected_Services">
				<c:forEach var="selectedItems" items="${selectedServiceList}">
					<input type="hidden" id="hiddenSelectedServices" value="${selectedItems.serviceElementId}" />
					<li class='displayNone' onclick="hideShowDisplayService(this,'${selectedItems.serviceElementId}')" id="displayService${selectedItems.serviceElementId}">
						${selectedItems.serviceElementId}</li>
				</c:forEach>
				<li class="noneSelected" id="noneSelected">None selected...</li>
			</ul>
		</div>
		<div class="hr"></div>	
		<!-- Selected Container end -->
			
		<!--  add service block -->
		<div class="addServiceBlock">
			<h3 style="display: inline;" 
			title="This list contains all the Services the City has identified in its Services Catalog that will be procured through RFPs.">Select from Full List</h3><br />
			<div class="expandCollapseLink">
			    <a href="#" title="Collapse all" onclick="collapseExpandAll('collapseAll');return false;">Collapse all</a> |
			    <a href="#" title="Expand all" onclick="collapseExpandAll('expandAll');return false;">Expand all</a>
			</div>
			
			<div>${finalTreeAsString}</div>      			
			<div id="displayContinue">${finalTreeAsString}</div>
			<div style="display:none">
				<div id="tempId" style="display:none"></div>
			</div>
		</div>
		
			<!-- Search Container Start -->
		<div class="searchContainer">
			<h5>Search</h5>
			<p>
				<input type="text" size="44" maxlength="50" name="searchText" id="searchText" title='Please enter keyword(s) that describe the Service you provide. For best results, please keep your search terms simple. (Example: enter "senior center" instead of "Senior recreational center for adults over age 60+ who require supervision'/>
				<input type="button" value="Clear" title="Clear" class="graybtutton button" id="clearButton"/>
				<input type="button" value="Search" title="Search" class="button" id='searchButton'/>
			</p>
			<div class="individualError" id="errorMessageSearch"></div>
		</div>		
		<!-- Search Container End -->
			
		<!-- Search results Start-->
		<div class="searchResultsContainer">
			<table cellspacing='0' cellpadding="0" class='grid' width='100%'>
				<tr>
					<td colspan=3 class='tabularCustomHead'>
						<span>Search Results for:</span><span class="searchTextToDisplay"></span>
					</td>
				</tr>
				<tr class='accDataRowHead'>
					<th class='col1'>
						Service
					</th>
					<th class='col2'>
						<strong>Description</strong>
					</th>
					<th>&nbsp;</th>
				</tr>
			</table>
			<div class="searchResults">
			</div>
		</div>
		<!-- Search results End-->
		
		
		<div class="addServiceBlock">
			<div class="buttonholder">
				<input type="button" class="graybtutton" value="Cancel" title="Cancel" onclick="return setValue('back', event)"/>
				<input id="saveService" name="saveService" type="submit" class="button" value="Complete Selections" title="Complete Selections" onclick="return setValue('saveServices', event)"/>
				<input type="hidden" name="selectedService" value="" id="addSelectedServices">
				<input type="hidden" name="next_action" value="" id="saveServices">
				<input type="hidden" name="subsection" value="addservice" id="addservice">
				<input type="hidden" name="section" value="servicessummary" id="sectionId">
			</div>
		</div>
		<!--  add service end -->
</form>
     			
<script type="text/javascript">
	setSelectedServices(null);
	showSelected('${section }','${subsection }');
</script>
