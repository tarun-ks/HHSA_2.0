<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>

<script type="text/javascript" src="../js/sharedocuments.js"></script>
<script type="text/javascript">
suggestionVal ="";
isValid = false;
var shareAction2;
//on load function to perform various checks on loading of jsp
function onReady(){
		shareAction2 = $("#share2").attr('action');
		var agencyData = '<%=request.getAttribute("proNameString")%>';
		populateTable(agencyData,'');
		//This will execute when user types any character in Add Provider text box.
		$(".alert-box-sharedoc").find('#provName').keyup(function() {
 			 var providersName = $('#provName').val();
  			 isValid = isAutoSuggestValid(providersName,suggestionVal);
 
  				if(isValid){
   					document.getElementById("addProvider").disabled=false;
  				}else{
     				document.getElementById("addProvider").disabled=true;
   				}
  		});
		document.getElementById("addProvider").disabled=true;

    	var onAutocompleteSelect = function(value, data) {
      	isValid = true;
       		document.getElementById("addProvider").disabled=false;
    	}

    var options = {
      serviceUrl: $("#contextPathSession").val()+'/AutoCompleteServlet.jsp?getFullList=true',
      width: 240,
      minChars:3,
      maxHeight:150,
      onSelect: onAutocompleteSelect,
      clearCache: true,
      deferRequestBy: 0, //miliseconds
      params: { city: $("#provName").val() }
    };

    var a1 = $('#provName').autocomplete(options);
    $("#provName").keydown(function(evt){
      var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
      if (keyCode == 13) {
        evt.stopPropagation();
        return false;
      }
   });
}
</script>
<form id="share2" action="<portlet:resourceURL/>" method ="post" name="share2">
	<input type="hidden" name="proNameString" id="proNameString">
	<input type="hidden" name="next_action" id="nextAction2">
		<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
		<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S040_PAGE, request.getSession()) 
				|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())) {%>
		<!--End of R4 Document Vault changes -->
			<div class="wizardTabs" style='width:800px;'>
					<div style='padding-left:10px'>
					<p>If you would like to grant another Provider access to your documents, you can use the search below to find
					them by their Organization Legal Name(enter in at least 3 characters) and click the "Add Provider" button. Once complete, click 
					the "Next" button.</p>
					<br/>
					<p>If you do not want to grant another Provider access to your documents, click the "Next" button now.</p>
					<div class='hr'></div>
					<div class="formcontainer">
						<input type="text" name="provName" id="provName"/><input type="button" value="+ Add Provider" title="+ Add Provider" class="button" name="addProvider" id="addProvider" onclick='populateTable("","^PROVIDER")'/>
						<br>   </br>
						<div  class="tabularWrapper" style="height: 250px !important; overflow: auto;">
							Granting access to the following:
							<table border="1" id='mytable'>
							</table>
						</div>
						<div class="buttonholder">
							<input type="button" id="cancelshare2" value="Cancel" title="Cancel" class="graybtutton"/>
							<input type="button" id="backshare2" value="Back" title="Back" class="graybtutton"/>
							<input type="button" value="Next" title="Next" id="nextshare2"/>
						</div>
				 </div>
			 </div>
			 </div>
		 <%}else{ %>
		 	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		<%} %>
</form>
