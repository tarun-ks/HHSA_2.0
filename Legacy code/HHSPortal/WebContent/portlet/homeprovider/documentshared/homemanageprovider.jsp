<!--This page will display a text box where a accelerator user can enter a value to find a provider/View full list of Providers. -->
<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.frameworks.grid.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<script type="text/javascript" id="sharedocumentstep2">
	suggestionVal ="";
	isValid = false;
	var originalFormAction;
	$(document).ready(function(){
		document.getElementById("continue").disabled=true;
		$(".messageDiv").hide();
		$('#provName').keyup(function()
		{ 
			var providersName = $.trim($('#provName').val());
  			isValid = isAutoSuggestValid(providersName,suggestionVal);
  			$(".messageDiv").hide();
			$(".messageDiv").html("");
  			  if(isValid){
  				document.myform.action = $("#formAction").val(); 
   				document.getElementById("continue").disabled=false;
  			  }else{
  				document.myform.action = "";
     			document.getElementById("continue").disabled=true;
     			
     			
     		
   			}
  		});
    	var onAutocompleteSelect = function(value, data) {
    		
    		document.getElementById("providerName").value=value;
      		document.getElementById("providerId").value=data;
      		document.myform.action = $("#formAction").val(); 
       		document.getElementById("continue").disabled=false;
       		isValid = true;
    	};
    	var options = {
	      	serviceUrl: $("#contextPath").val()+'/AutoCompleteServlet.jsp?isProvider=false',
	        width: 252,
		    minChars:3,
		    maxHeight:100,
		    onSelect: onAutocompleteSelect,
		    clearCache: true,
		    deferRequestBy: 0, //miliseconds
	        params: { city: $("#provName").val() }
        };
    	$('#provName').autocomplete(options);
		$("#continue").click(function() {
			var providerId = $("#providerId").val();
			if(isValid && providerId != null && providerId != ""){
				$("#searchTextValue").val(providerId);
				document.myform.submit();
			}
		
		});

	});
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
</script>

<!-- Body Wrapper Start -->
<form id="myform" name="myform" action="" method ="post" >
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession())) {%>
		<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
		<input type="hidden" value="" id="searchTextValue" name="searchTextValue"/>
		<div  class="tabularWrapper portlet1Col">
		 	<div class="tabularCustomHead">Manage Organization</div>
		    <table cellspacing="0" cellpadding="0"  class="grid">                 
		    	<tr>
                	<td>Use the text box below to find an organization.</td>                    
				</tr>
                <tr>
                	<td><input type="text" class="input" name="provName" maxlength="60" onkeypress="if (this.value.length > 60) { return false; }" id="provName"/>
                    	<input type="button" class="button" value="Continue" name="continue" id="continue"/>
                   	</td>
                    <input type="hidden" name="providerId" id ="providerId">
                    <%-- Start changes for R5 --%>
                    <input type="hidden" name="providerName" id ="providerName">
                    <%-- End changes for R5 --%>
                </tr>
                <tr>
                <td>
                	<div class="error messageDiv"></div>
                </td>
                </tr>
			</table>
		</div>	
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
	<input type="hidden" value="<portlet:actionURL/>" id="formAction"/>	
</form>	
