<!-- This page is displayed when a user click on save and next button on  basic language screen.
It will display list of geography from which a user can select from using  check boxes -->
<%@ page import="java.util.*" %>
<%@ page import="com.nyc.hhs.model.TaxonomyTree" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript" src="../resources/js/geography.js"></script>
<portlet:defineObjects />
	
<style type="text/css">

#newTabs h5{
	background:#E4E4E4;
	color: #5077AA;
    font-size: 13px;
    font-weight: bold;
    padding: 6px 0px 6px 6px;
}

.custDataRowHead1 {
    background: none repeat scroll 0 0 #F2F2F2;
    border: 1px solid #CCCCCC;
    clear: both;
    color: #666666;
    display: block;
    font-weight: bold;
    line-height: 25px;
    overflow: hidden;
   /* width: 100%;*/
}
.accrodinWrapper {
    border-bottom: 2px solid #FFFFFF;
    cursor: pointer;
    float: left;
    width: 100%;
}
.accContainer{
	width:100% !important
}
</style>

<form action="<portlet:actionURL/>" method="post" name="geographyform">
<input type="hidden" name="next_action" value="" />
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S052_S052R_PAGE, request.getSession()) 
	//Start : R5 Condition Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S080_PAGE, request.getSession())
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())){
	//End : R5 Condition Added
		%>

<c:set var="readOnlyValue" value=""></c:set>
<c:if test="${loReadOnlySection}">
	<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
</c:if>

<input type="hidden" value="${loReadOnlySection}" id="readOnlyValueId"/>
<!-- Body Wrapper Start -->

<div id="Geography">
<h2>Geography</h2>
<div id="errorMessage" class="individualError" style="float:left;font-size:12px;"></div>
<div title="Geography refers to the neighborhood(s) where your organization provides services and/or to the neighborhood(s) where the clients that your organization has a specialized ability to serve are based.">
            <p> Which geographic areas of New York City does your organization serve?</p>
          	<p>  Click on any of the New York boroughs below to expand a list of all community districts and their associated neighborhoods that are in that borough. Once complete, click the "<span id='buttonName'></span>" button.
            </p>
            </div>
           
 	<c:if test="${!loReadOnlySection}">
	 	 <div class="expandCollapseLink">
		 	 <a href="#" title="Collapse All" onClick="collapseExpandAll('collapseAll'); return false" id="collapseAllId">Collapse all</a>  | 
		 	 <a href="#" title="Expand All" onClick="collapseExpandAll('expandAll'); return false" id="expandAllId">Expand all</a> 
	 	 </div>	
 	</c:if>
 	 
	<div class="clear"></div>

	<c:forEach items="${TaxonomyTypeMap}" var="outerMap">
	 	<div id="accordionTopId">
	 		<div class="accrodinWrapper hdng"  id="accordionHeaderId" onclick="displayAccortion(this)">
	      		<h5 class="wordWrap">${outerMap.key}</h5>
	      	</div>
	      	<div  id="accordianId"  class="close">
      		 	<div class="accContainer">
	    			<div class="accDataRowHead">
		    			<span class="col1">District</span>
		     			<span class="col2">Neighborhoods</span>
		     			<span class="col3"><input id="${outerMap.key}_cb" ${readOnlyValue} name="districts_cb" onClick="enableAllTextbox(this,'${outerMap.key}');"  type="checkbox"/></span>
	     			</div>
					<c:forEach items="${outerMap.value}" var="innerMap" varStatus="innerLoop">
				       <div class="accDataRow">
						    <span class="col1">${innerMap.msElementName}</span>
						    <span class="col2">${innerMap.msElementDescription}</span>
						    <span class="col3">
						    	<c:choose>
						    		<c:when test="${innerMap.msServiceStatus eq 'selected'}">
						    			<input  id="${outerMap.key}" checked ${readOnlyValue} name="geography" onclick="removeSelectAll(this,'${outerMap.key}','${outerMap.key}_cb')"  value="${innerMap.msElementid}" type="checkbox"/>
						    		</c:when>
						    		<c:otherwise>
						    			<input  id="${outerMap.key}" ${readOnlyValue} name="geography" onclick="removeSelectAll(this,'${outerMap.key}','${outerMap.key}_cb')"  value="${innerMap.msElementid}" type="checkbox"/>
						    		</c:otherwise>
						    	</c:choose>
					    	</span>
					    </div>
				    </c:forEach>
			    </div>
		    </div>
		</div>		  
	</c:forEach>
<!-- End accordion -->
<br>
<p>

<c:if test="${loTaxonomyIdList ne null}">
	<c:set var="emptyCheckBox" value="${fn:length(loTaxonomyIdList)}"/>
	<c:choose>
		<c:when test="${emptyCheckBox eq 1 and loTaxonomyIdList[0] eq null}">
			<input  id="close"   onClick="EmptyListItem(this.id);" ${readOnlyValue} checked="checked" type="checkbox" name="bottomCheckBox"/> My organization is not geographically based.
		</c:when>
		<c:otherwise>
			<input  id="close"   onClick="EmptyListItem(this.id);" ${readOnlyValue}  type="checkbox" name="bottomCheckBox"/> My organization is not geographically based.
		</c:otherwise>
	</c:choose>
</c:if>

</p>
<br>
<c:if test="${!loReadOnlySection}">
<div class='buttonholder'>
	
		<c:choose>
              <c:when test="${app_menu_name == 'header_organization_information' }">
             	 <input type="button" title="Cancel" class="graybtutton" value="Cancel"  onclick="GoToPreviousPage('refresh',
              					'<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>');"/>
             	 <input type="hidden" name="app_menu_name" value="header_organization_information"/>
              </c:when>
              <c:otherwise>
              		<input type="button" title="<< Back" class="graybtutton" id="backbutton"  value="<< Back" onclick="GoToPreviousPage('back',
              					'<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>');"/>
              </c:otherwise>
        </c:choose>
		<input type="button" class='button' title="Save" value='Save' onclick="selectAllAndSubmit('save',
					'<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>');" id="saveButtonId" />
		<c:if test="${app_menu_name != 'header_organization_information' }">
			<input type="button" class='button' value='Save & Next'  title="Save & Next" onclick="selectAllAndSubmit('save_next',
					'<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>');"  id="saveAndNextButtonId"/>
		</c:if>
	
</div>
</c:if>
<!-- Body Wrapper End -->
</div>

</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
