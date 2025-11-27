<!--This page will display the list of provider that have Documents shared with Organization in the drop down on the Home page -->
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>

<script>
	$(document).ready(function(){
		/*Start : Added in R5*/
		if($('#typeOfUser').val() != 'provider_org')
		{
			suggestionVal ="";
			$('#provName').keyup(function()
					{ 
						var providersName = $.trim($('#provName').val());
			  			isValid = isAutoSuggestValid(providersName,suggestionVal);
			  			$(".messageDiv").hide();
						$(".messageDiv").html("");
			  			  if(isValid){
			  				document.getShareDocForm.action = $("#formAction").val(); 
			   				document.getElementById("continue").disabled=false;
			  			  }else{
			  				document.getShareDocForm.action = "";
			     			document.getElementById("continue").disabled=true;
			   			}
			  		});
				var onAutocompleteSelect = function(value, data) {
					document.getElementById("providerName").value=value;
		      		document.getElementById("providerId").value=data;
		      		document.getShareDocForm.action = $("#formAction").val(); 
		       		document.getElementById("continue").disabled=false;
		       		isValid = true;
		    	};
		    	var options = {
			      	serviceUrl: $("#contextPath").val()+'/AutoCompleteServlet.jsp?isProvider=false&agencylogin=true',
			        width: 252,
				    minChars:3,
				    maxHeight:100,
				    onSelect: onAutocompleteSelect,
				    clearCache: true,
				    deferRequestBy: 0, //miliseconds
			        params: { city: $("#provName").val() }
		        };
		    	$('#provName').autocomplete(options);
		}
			/*End : Added in R5*/
		
	$("#continue").click(
		function(){
			var providerId = $("#providerId").val();
			if(providerId.length == 1 || providerId =='- Select an Organization -' ){
				$("#errorMessage").html("Please select the organization to check shared document.");
				return false;
			}
			else{
				document.getShareDocForm.action=document.getShareDocForm.action+"&providerId="+providerId;
				$("#getShareDocForm").submit();
			}
			$('#subsection_${subsection}').parent().removeClass();
			$('#subsection_${subsection}').addClass('current');		
		}
	);});
	/*Start : Added in R5*/
	function isAutoSuggestValid(providersName, suggestionVal) {
		var uoValid = false;
		if (suggestionVal.length > 0) {
			for (i = 0; i < suggestionVal.length; i++) {
				var arrVal = suggestionVal[i];
				if (arrVal == providersName) {
					uoValid = true;
					break;
				}
			}
		}
		return uoValid;
	}
	/*End : Added in R5*/
</script>


<!-- Body Wrapper Start -->
<form id="getShareDocForm" action="<portlet:actionURL/>" method ="post" name="getShareDocForm" >
		<%--Start : Added in R5 --%>		
		<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
		<%--End : Added in R5 --%>
		<input type="hidden" name="next_action" value="showOrgInformation"/>
	  	<div  class="tabularWrapper portlet1Col">
	  		<c:choose>
				<c:when test="${org_type eq 'provider_org'}">
					<div class="tabularCustomHead">Documents Shared with your Organization</div>	
				</c:when>
				<c:otherwise>
					<%--Start : Updated in R5 --%>
					<div class="tabularCustomHead">Manage Organization</div>
					<%--End : Updated in R5 --%>
				</c:otherwise>
			</c:choose>       
	       	<table cellspacing="0" cellpadding="0"  class="grid">                 
	        	<tr>
	            	<c:choose>
						<c:when test="${org_type eq 'provider_org'}">
							<td>Organizations have shared 1 or more documents with you.<br>Select an organization below and press "Continue" to view those<br>documents.</td>	
						</c:when>
						<c:otherwise>
						<%--Start : Updated in R5 --%>
							<td>Use the text box below to find an organization</td>
						<%--End : Updated in R5 --%>
						</c:otherwise>
					</c:choose>                
	           	</tr>
                <tr>
                <%--Start : Added in R5 --%>
                <c:choose>
					<c:when test="${org_type eq 'provider_org'}">
                	<td>
                		<select id = "providerId" name="providerId" class="input">
							<option value="- Select an Organization -">- Select an Organization -</option>
							<c:forEach var="organization" items="${portletSessionScope.sharedDocForProvider}">
								<option value="<c:out value="${organization.key}"/>"><c:out value="${organization.value}"/></option>
							</c:forEach>
						</select>
						<input id="continue" type="button" class="button" value="Continue" title="Continue"/>
					</td>
					</c:when>
					<c:otherwise>
						<td>
						<input type="text" class="input" name="provName" maxlength="60" onkeypress="if (this.value.length > 60) { return false; }" id="provName"/>
                    	<input type="button" class="button" value="Continue" name="continue" id="continue" disabled="disabled"/>
	                   	</td>
	                    <input type="hidden" name="providerId" id ="providerId">
	                      <input type="hidden" name="providerName" id ="providerName">
	                    <tr>
		                <td>
		                	<div class="error messageDiv"></div>
		                </td>
		                </tr>
					</c:otherwise>
				</c:choose>
				<%--End : Added in R5 --%>
                </tr>
			</table>
	        <div id="errorMessage" class="individualError"></div>
		</div>
<%--Start : Added in R5 --%>
		<input type="hidden" value="<portlet:actionURL/>" id="formAction"/>	
<%--End : Added in R5 --%>
</form>	
