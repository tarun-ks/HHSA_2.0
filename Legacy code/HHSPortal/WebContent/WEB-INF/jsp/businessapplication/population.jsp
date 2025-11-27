<!-- This page is displayed when a user click on save and next button on  basic's language screen.
It will display list of corresponding population from which a user can select by selecting the checkboxes .-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="com.nyc.hhs.model.Population"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="javax.portlet.*"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<style type="text/css">
#newTabs h5{
	background:#E4E4E4;
	color: #5077AA;
    font-size: 13px;
    font-weight: bold;
    padding: 6px 0px 6px 6px;
}
.popuAgeRangeNone {
    display: none;
    right: 25px;
    top: 0;
    float:right;
}
.popuAgeRangeBlock {
    display: block;
    right: 25px;
    top: 0;
    float:right;
    *margin-top:-25px;
    width:300px;

}
.popuCol1 {
     display: block;
    float: left;
  	height: 23px;
    margin-right: 15px;
    padding-top: 13px;
    /*padding-bottom: 10px;*/
    width: 48%;
    position:relative;
    word-break: break-all
}

</style>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/taxonomyPopulation.js"></script>
<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S054_S054R_PAGE, request.getSession())
//Start : R5 Condition Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S080_PAGE, request.getSession())
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())){
//End : R5 Condition Added
		%>
<form name="populationform" action='<portlet:actionURL/>'  method="post">
<input type="hidden" id="hiddenOtherCheckBox" value="${otherCheckBoxSelected}"/>

<c:set var="readOnlyValue" value=""></c:set>
<c:if test="${loReadOnlySection}">
	<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
</c:if>

<div id="Populations">
	<h2 title="Populations refer to groups for which your organization has developed tailored services.">Populations</h2>
	<div id="errorMessage" class="individualError" style="float:left;font-size:12px;"></div>
    <p>Does your organization offer specialized programs for any of the following populations?</p>
	<p>Please check all that apply. If your organization provides specialized programs for other populations, please select 
			"Other," and specify those populations. Once complete, click the "<span id='buttonName'></span>" button.</p>
    <div class="clear">
   
	<%List<String> fromToAgeList= ApplicationConstants.FROM_TO_AGE_LIST; %>    
    <c:set var="ageFromToList" value="<%=fromToAgeList%>"></c:set>
    <c:set var="displayChecked" value="true"></c:set>
    <c:choose>
    	<c:when test="${(fn:length(taxonomy_tree_list) % 2) eq 0}">
    		<c:set var="numRows" value="${(fn:length(taxonomy_tree_list) / 2)}" />
    		<c:set var="firstLoop" value="${(fn:length(taxonomy_tree_list) / 2)}" />
    		<c:set var="secondLoop" value="${(fn:length(taxonomy_tree_list))}" />
    		<c:set var="firstLoopCounter" value="${firstLoop}"></c:set>
    		<c:set var="loopValue" value="1"></c:set>
    	</c:when>
    	<c:otherwise>
    		<c:set var="numRows" value="${(fn:length(taxonomy_tree_list) / 2)+1}" />
    		<c:set var="listLength" value="${(fn:length(taxonomy_tree_list) / 2)}"></c:set>
    		<c:choose>
    			<c:when test="${fn:contains((fn:length(taxonomy_tree_list) / 2), '.')}">
    				<c:set var="firstLoop" value="${fn:substring(listLength, 0, fn:indexOf(listLength,'.'))}" />
    			</c:when>
				<c:otherwise>
					<fmt:formatNumber var="firstLoop" value="${(fn:length(taxonomy_tree_list) / 2)}" maxFractionDigits="0" />
				</c:otherwise>
    		</c:choose>
			<fmt:formatNumber var="secondLoop" value="${(fn:length(taxonomy_tree_list))}" maxFractionDigits="0" />
    		<c:set var="firstLoopCounter" value="${firstLoop+1}"></c:set>
    		<c:set var="loopValue" value="0"></c:set>
    	</c:otherwise>
    </c:choose>
    
    <table width="100%" border="1">
	    <c:set var="index" value="0"></c:set>
		    <c:forEach begin="${loopValue}" end="${firstLoop}" varStatus="counter">
		    	<c:set var="populationList" value="${taxonomy_tree_list[index]}"></c:set>
		    	<tr>
	    			<td class="popuCol1">
	    				<c:choose>
				    		<c:when test="${fn:indexOf(ageFromToList,populationList.msElementName) ne -1}">
				    			<c:choose>
				    				<c:when test="${populationList.selectedPopulation eq true or populationList.selectedPopulation eq 'true'}">
				    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" checked="checked" onclick="displayAgeRange(this,'ageRange')" id="validateAgeRange_${populationList.msElementid}"/> ${populationList.msElementName} 
											<div id="allAgeRangeIds" class="popuAgeRangeBlock">(Age Range from 
												<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="2" name="ageFromInput${populationList.msElementid}" value="${populationList.ageFrom}" id="ageRangeFrom_${populationList.msElementid}"/> to 
												<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="3" name="ageToInput${populationList.msElementid}" value="${populationList.ageTo}" id="ageRangeTo_${populationList.msElementid}"/> )
											</div>
											<c:set var="displayChecked" value="false"></c:set>
				    				</c:when>
				    				<c:otherwise>
				    					<c:if test="${!empty populationList.msElementid}">
					    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" onclick="displayAgeRange(this,'ageRange')" id="validateAgeRange_${populationList.msElementid}"/> ${populationList.msElementName} 
												<div id="allAgeRangeIds" class="popuAgeRangeNone">(Age Range from 
													<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="2" name="ageFromInput${populationList.msElementid}"  value="${populationList.ageFrom}" id="ageRangeFrom_${populationList.msElementid}"/> to 
													<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="3" name="ageToInput${populationList.msElementid}" value="${populationList.ageTo}" id="ageRangeTo_${populationList.msElementid}"/> )
												</div>
										</c:if>
									</c:otherwise>
				    			</c:choose> 
							</c:when>
				    		<c:otherwise>
				    			<c:choose>
				    				<c:when test="${populationList.selectedPopulation eq true or populationList.selectedPopulation eq 'true'}">
				    					<c:set var="displayChecked" value="false"></c:set>
				    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" checked="checked" id="validateElseAgeRange${populationList.msElementid}" onclick="displayAgeRange(this,'')"/> ${populationList.msElementName}
				    				</c:when>
					    			<c:otherwise>
					    				<c:if test="${!empty populationList.msElementid}">
					    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" id="validateElseAgeRange${populationList.msElementid}" onclick="displayAgeRange(this,'')"/> ${populationList.msElementName} 
				    					</c:if>
				    				</c:otherwise>
				    			</c:choose>
				    		</c:otherwise>
			    		</c:choose>
	    			</td>
		    		<c:set var="populationList" value="${taxonomy_tree_list[firstLoopCounter]}"></c:set>
		    		<c:if test="${secondLoop ne firstLoopCounter}">
					<td class="popuCol1">
    					<c:choose>
				    		<c:when test="${fn:indexOf(ageFromToList,populationList.msElementName) ne -1}">
				    			<c:choose>
				    				<c:when test="${populationList.selectedPopulation eq true or populationList.selectedPopulation eq 'true'}">
				    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" checked="checked" onclick="displayAgeRange(this,'ageRange')" id="validateAgeRange_${populationList.msElementid}"/> ${populationList.msElementName} 
											<div id="allAgeRangeIds" class="popuAgeRangeBlock">(Age Range from 
												<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="2" name="ageFromInput${populationList.msElementid}" value="${populationList.ageFrom}" id="ageRangeFrom_${populationList.msElementid}"/> to 
												<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="3" name="ageToInput${populationList.msElementid}" value="${populationList.ageTo}" id="ageRangeTo_${populationList.msElementid}"/> )
											</div>
											<c:set var="displayChecked" value="false"></c:set>
				    				</c:when>
				    				<c:otherwise>
				    					<c:if test="${!empty populationList.msElementid}">
					    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" onclick="displayAgeRange(this,'ageRange')" id="validateAgeRange_${populationList.msElementid}"/> ${populationList.msElementName} 
												<div id="allAgeRangeIds" class="popuAgeRangeNone">(Age Range from 
													<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="2" name="ageFromInput${populationList.msElementid}"  value="${populationList.ageFrom}" id="ageRangeFrom_${populationList.msElementid}"/> to 
													<input ${readOnlyValue} validate="number" type="text" size="3" maxlength="3" name="ageToInput${populationList.msElementid}" value="${populationList.ageTo}" id="ageRangeTo_${populationList.msElementid}"/> )
												</div>
										</c:if>
									</c:otherwise>
				    			</c:choose> 
							</c:when>
				    		<c:otherwise>
				    			<c:choose>
				    				<c:when test="${populationList.selectedPopulation eq true or populationList.selectedPopulation eq 'true'}">
				    					<c:set var="displayChecked" value="false"></c:set>
				    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" checked="checked" id="validateElseAgeRange${populationList.msElementid}" onclick="displayAgeRange(this,'')"/> ${populationList.msElementName}
				    				</c:when>
					    			<c:otherwise>
					    				<c:if test="${!empty populationList.msElementid}">
					    					<input ${readOnlyValue} type="checkbox" name="populationCheckBoxex" value="${populationList.msElementid}" id="validateElseAgeRange${populationList.msElementid}" onclick="displayAgeRange(this,'')"/> ${populationList.msElementName} 
				    					</c:if>
				    				</c:otherwise>
				    			</c:choose>
				    		</c:otherwise>
			    		</c:choose>
    				</td>
    				</c:if>   
		    	</tr>
		    	<c:set var="firstLoopCounter" value="${firstLoopCounter+1}"></c:set>
		    	<c:set var="index" value="${index+1}"></c:set>
		    </c:forEach>
    </table>  
   <p class="clear">&nbsp;</p>	
   <ul>
	   <li>
	   <c:choose>
	    	<c:when test="${otherCheckBoxAtLast}">
	    		<input ${readOnlyValue} type="checkbox" checked="checked" onclick="displayOtherArea(this)" value="-1" id="otherCheckBox" name="populationCheckBoxex"/>Other (please specify)
	    	</c:when>
	    	<c:otherwise>
	    		<input ${readOnlyValue} type="checkbox" onclick="displayOtherArea(this)" value="-1" id="otherCheckBox" name="populationCheckBoxex"/>Other (please specify)
	    	</c:otherwise>
	    </c:choose>
	    </li>
		<li id="populationOther">
	   	    <c:choose>
	  			<c:when test="${otherCheckBoxAtLast}">
	   				<textarea onkeyup="setMaxLength(this,500)" onkeypress="setMaxLength(this,500)" ${readOnlyValue} class="textarea floatLft" id="otherTextBox" name="otherTextBox" style="display:block" value="${otherCheckBoxAtLastValue}">${otherCheckBoxAtLastValue}</textarea>
	   			</c:when>
	    		<c:otherwise>
	    			<textarea onkeyup="setMaxLength(this,500)" onkeypress="setMaxLength(this,500)" ${readOnlyValue} class="textarea floatLft" id="otherTextBox" name="otherTextBox" style="display:none"></textarea>	
	    		</c:otherwise>
	    	</c:choose>
		</li>
    </ul>
    <p class="clear"></p>	
      <ul>
    	 <li>
    	 	<c:choose>
    	 		<c:when test="${otherCheckBoxSelected}">
    	 			<input ${readOnlyValue} type="checkbox" checked="checked" name="noPopulation" value="-2" id="noPopulation" onclick="disableAllPopulation(this)" />	
    	 		</c:when>
    	 		<c:otherwise>
    	 			<input ${readOnlyValue} type="checkbox"  name="noPopulation" value="-2" id="noPopulation" onclick="disableAllPopulation(this)" />
    	 		</c:otherwise>
    	 	</c:choose>
	     	<span title="Select this option if your organization is open to all or does not offer specialized programs or services for target populations.">My organization does not service a specific population</span>
     	</li>
      </ul>
   
    </div>
</div>
<c:if test="${!loReadOnlySection}"> 
	<div class="buttonholder">
	<c:choose>
       <c:when test="${app_menu_name == 'header_organization_information' }">
            <input type="button" title="Cancel" class="graybtutton" value="Cancel" onclick="GoToPreviousPage('refresh','<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>')"/>
            <input type="hidden" name="app_menu_name" value="header_organization_information"/>
       </c:when>
       <c:otherwise>
              <input type="button" class="graybtutton" title="<< Back" value="&lt;&lt; Back" onclick="GoToPreviousPage('back','<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>')"/>
       </c:otherwise>
    </c:choose>
	     <input type="button" class="button" title="Save" value="Save" onclick="selectAllAndSubmit('save','<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>')" />
	      <c:if test="${app_menu_name != 'header_organization_information' }">
	           <input id="saveAndNextButtonId" type="button" class="button" title="Save & Next" value="Save &amp; Next" onclick="selectAllAndSubmit('save_next','<%=renderRequest.getAttribute("business_app_id")%>','<%=renderRequest.getAttribute("section")%>','<%=renderRequest.getAttribute("subsection")%>')"/>
	      </c:if>
	</div>
</c:if>
</form>
<% } else {%>
	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
<%} %>
