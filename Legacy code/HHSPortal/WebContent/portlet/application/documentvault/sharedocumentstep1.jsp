<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/sharedocuments.js"></script>
<script type="text/javascript">
//on load function to perform various checks on loading of jsp
function onReady(){
		// This will execute when Next button is clicked from Share document Step1 screen
		$(".alert-box-sharedoc").find('#nextshare1').click(function() { // bind click event to link
		pageGreyOut();
		shareScreen1('<%=request.getAttribute("proNameString")%>');
    	var options = 
    	{
    	success: function(responseText, statusText, xhr ) 
		{
            $("#tab3").empty();
			$("#tab4").empty();
			$("#tab5").empty();
			$("#tab6").empty();
            $("#tab4").html(responseText);
			$("#sharelabel").html("- Step 2");
			callBackInWindow("onReady");
			$('#sharewiz').removeClass('wizardUlStep1').addClass('wizardUlStep2');
			$('step2confirmDoc').css("background-color", "#333333");
			//$.unblockUI();
			removePageGreyOut();
		},
		error:function (xhr, ajaxOptions, thrownError)
		{                     
			showErrorMessagePopup();
			removePageGreyOut();
		}
    };
    $(this.form).ajaxSubmit(options);
	 return false;
	});
}
</script>
<div class="overlaycontent">
	<!--Start of R4 Document Vault changes: Component Mapping check added for Agency security matrix-->
	<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S039_PAGE, request.getSession()) || CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S033_AGENCY_PAGE, request.getSession())){%>
	<!--End of R4 Document Vault changes -->
	<form id="share1" action="<portlet:resourceURL/>" method ="post" name="share1">
	<input type="hidden" name="next_action" value="shareDocumentStep2">
	<input type="hidden" name="proNameString" value="" id="proNameString">
		<div class="wizardTabs" style='width:800px;'>
				<p>You have selected the following documents to grant "read-only" access to other NYC Providers and/or NYC Agencies.</p>
				<p>- Click "Cancel" to return to the Document Vault page and change your document selections</p>
				<p>- Click "Next" to continue with these documents</p>
				<div class='hr'></div>
				<div class="tabularWrapper" id="tableContainerDiv" style='overflow:auto; height:250px !important;'>
					<table>
                       <tr>
                             <th>Document Name</th>
                             <th>Document Type</th>
                       </tr>
                       <c:forEach items="${shareDocumentList}" var="shareDocumentList" varStatus="counter">
                             <tr class=${counter.index % 2 eq 0?'evenRows':'oddRows'}>
                                   <td>${shareDocumentList.docName}</td>
                                   <td>${shareDocumentList.docType}</td>
                             </tr>
                       </c:forEach>
                    </table>
				</div>
				<div class="buttonholder">
						<input type="button" id="cancelshare1" value="Cancel" title="Cancel" class="graybtutton"/>
						<input type="button" value="Next" title="Next" id="nextshare1"/>
				</div>
		 </div>
	</form>
	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</div>