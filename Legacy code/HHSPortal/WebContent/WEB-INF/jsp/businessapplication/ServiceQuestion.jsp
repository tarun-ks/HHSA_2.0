<!-- This page is displayed when a user add a service and click on submit supporting confirmation link.
It will display service question screen where a user can add staff or contract -->
<%@page import="com.sun.org.apache.xalan.internal.xsltc.compiler.sym"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*"%>
<%@ page import ="com.nyc.hhs.util.DateUtil"%>
<%@ page import="java.util.List"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.model.ServiceQuestions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<portlet:defineObjects />
<style type="text/css">
	.commentHidden{
		display:none;
	}
	#Error_message,#Error_message2,#Error_message3{
		color:red;
	}
	.iconQuestion{
    	margin-left: 6px !important;
    }
    h2{
    	width: 96% !important;
    }
</style>
<script type="text/javascript">
/**
 * AJAX Method 
 * 
 **/
function postRequest(strURL, type) {
		pageGreyOut();
		var xmlHttp;
		if (window.XMLHttpRequest) { 
			var xmlHttp = new XMLHttpRequest();
		} else if (window.ActiveXObject) { 
			var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlHttp.open('POST', strURL, true);
		xmlHttp.setRequestHeader('Content-Type',
				'application/x-www-form-urlencoded');
		xmlHttp.onreadystatechange = function() {
			if (xmlHttp.readyState == 4) {
				updatepage(xmlHttp.responseText, type);
			}
		};
		xmlHttp.send(strURL);
	}
	//Updates the page
function updatepage(str, type) {
	$("#iFrameCLick").hide();
	$("#linkDiv").html(str);
	window.parent.document.getElementsByTagName("iFrame")[0].contentWindow.scrollTo(0,0);
	if(type == 'funder') {
		funderOnLoad();	
	} 
	removePageGreyOut();
}
	//shows funder on laod
function funderOnLoad() {
	var hiddenFlag = $("#contractHidden").val();
	$("#phoneId").fieldFormatter("XXX-XXX-XXXX");
	//$('#contractBudget').autoNumeric({vMax: '999999999999.99'});
	if( $("#contractType").val()=='NYC Government'){
			if(!hiddenFlag) {
				$("#nycAgency").show().find("select, input, textarea").attr('disabled',false);
			}
			$("#funderName").hide().find("select, input, textarea").attr('disabled',true);
			$("#firstName").hide().find("select, input, textarea").attr('disabled',true);
			$("#midName").hide().find("select, input, textarea").attr('disabled',true);
			$("#lastName").hide().find("select, input, textarea").attr('disabled',true);
			$("#title").hide().find("select, input, textarea").attr('disabled',true);
			$("#phone").hide().find("select, input, textarea").attr('disabled',true);
			$("#email").hide().find("select, input, textarea").attr('disabled',true);
			$("#reference").hide().find("select, input, textarea").attr('disabled',true);
		}else{
			$("#nycAgency").hide().find("select, input, textarea").attr('disabled',true);
			$("#funderName").show().find("select, input, textarea").attr('disabled',false);
			$("#firstName").show().find("select, input, textarea").attr('disabled',false);
			$("#midName").show().find("select, input, textarea").attr('disabled',false);
			$("#lastName").show().find("select, input, textarea").attr('disabled',false);
			if(!hiddenFlag) {
				$("#title").show().find("select, input, textarea").attr('disabled',false);
			}
			$("#phone").show().find("select, input, textarea").attr('disabled',false);
			$("#email").show().find("select, input, textarea").attr('disabled',false);
			$("#reference").show().find("select, input, textarea").attr('disabled',true);
		}
}
$(document).ready(function() {  
	//This is on ready function, it will prepopulate the contract/staff selected value for already saved data.
	<%String ques1="nothing";
	String ques2="nothing";
	String ques3="nothing";
	if(renderRequest.getAttribute("aoallQuestionDetails")!=null){
		ArrayList<ServiceQuestions> allQuestionDetails=(ArrayList<ServiceQuestions>)renderRequest.getAttribute("aoallQuestionDetails");
		if(allQuestionDetails != null && !allQuestionDetails.isEmpty()){
			ques1=allQuestionDetails.get(0).getMsQuestion1();
			ques2=allQuestionDetails.get(0).getMsQuestion2();
			ques3=allQuestionDetails.get(0).getMsQuestion3();
		}
	}%>
	onChange("<%=ques1%>");
	onChange2("<%=ques2%>");
	onChange3("<%=ques3%>");
	<%
	String contractSel=(String)renderRequest.getAttribute("contractSel") ;
	String staffSel=(String)renderRequest.getAttribute("staffSel") ;
	if(contractSel !=null){
		%>
		onChange("yes");
		<%}
		if(staffSel !=null){
		%>onChange("no");onChange2("yes");<%}%>



		<%String onLoadDisabled=""; 
		Boolean isSubmitted=false; 
		if(isSubmitted){onLoadDisabled="disabled";
		%>
		$("#contractDetails").removeAttr("class");
				$("#contractDetails").addClass("deActive");
				$("#staffDetails").removeAttr("class");
				$("#staffDetails").addClass("deActive");
				$("#buttonholder").removeAttr("class");
				$("#buttonholder").addClass("deActive");
				
		<%}%>
	if("null" != '<%= request.getAttribute("message")%>' && '<%= request.getAttribute("messageType")%>' =="yes"){
		if("null" != '<%=staffSel%>' &&  '<%=staffSel%>'=='yes'){
			$(".messagediv").html('You must add at least one Staff Member'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");	
		}
		else{
			$(".messagediv").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
		}
		$(".messagediv").addClass('failed');
		$(".messagediv").show();
		<%request.removeAttribute("message");%>
	}
});
//This method will used to edit or delete the seleced contract from the drop down.
function editOrRemoveDocumentContract(orgId,contractDetailsId,selectElement){
	var value = selectElement.selectedIndex;
		if(value == 2){
				document.forms[0].action = document.forms[0].action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=delSelectValue&selectBoxValue='+contractDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>';
			document.myform.submit();
		}
		else{
			if(null != <%=request.getParameter("removeNavigator")%>){
				var url = $("#sessionPath").val()+"/GetContent.jsp?iFrameClick=iFrameClickContract&organizationId="+orgId+"&service_app_id="+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=selectValue&selectBoxValue='+contractDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>'+"&removeNavigator="+'<%=renderRequest.getAttribute("removeNavigator")%>';
				postRequest(url,"funder");
				//document.forms[0].action = document.forms[0].action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=selectValue&selectBoxValue='+contractDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>'+"&removeNavigator="+'<%=renderRequest.getAttribute("removeNavigator")%>';
			}else {
				document.forms[0].action = document.forms[0].action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=selectValue&selectBoxValue='+contractDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>';
				document.myform.submit();
			}
		}
}
//This method will used to edit or delete the seleced staff from the drop down.
function editOrRemoveDocumentStaff(orgId,staffDetailsId,selectElement){
	var value = selectElement.selectedIndex;
	if(value == 2){
		document.forms[0].action = document.forms[0].action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=delSelectStaff&selectBoxValue='+staffDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>';
		document.myform.submit();
	}
	else{
			if(null != <%=request.getParameter("removeNavigator")%>){
				var url = $("#sessionPath").val()+"/GetContent.jsp?iFrameClick=iFrameClickStaff&organizationId="+orgId+"&service_app_id="+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=selectStaff&selectBoxValue='+staffDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>'+"&removeNavigator="+'<%=renderRequest.getAttribute("removeNavigator")%>';
				postRequest(url, "staff");
				//document.forms[0].action = document.forms[0].action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=selectStaff&selectBoxValue='+staffDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>'+"&removeNavigator="+'<%=renderRequest.getAttribute("removeNavigator")%>';
			}else{ 
				document.forms[0].action = document.forms[0].action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action=selectStaff&selectBoxValue='+staffDetailsId+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>';
				document.myform.submit();
			}
		}
}
//This method will be called when we change the first drop down on service question page.
function onChange(value)
{	
	
	$('#mySelect option[value="'+value+'"]').attr('selected', 'selected');
	if(value=="yes")
	{
	
		document.getElementById('ButtonQuestion1').style.display="";
		document.getElementById('Question2').style.display="none";
		document.getElementById('ButtonQuestion2').style.display="none";
		document.getElementById('mySelect2').value="nothing";
		document.getElementById('mySelect3').value="nothing";
		document.getElementById('Question3').style.display="none";	
		document.getElementById('narrative').style.display="none";
		document.getElementById('capabilityStatement').style.display="none";
		document.getElementById('Error_message').innerHTML="";	
		document.getElementById('Error_message2').innerHTML="";	
		document.getElementById('Error_message3').innerHTML="";	
	}
	else if(value=="no")	
		{			
			document.getElementById('Question2').style.display="";				
			document.getElementById('ButtonQuestion1').style.display="none";			
			document.getElementById('narrative').style.display="none";
			document.getElementById('capabilityStatement').style.display="none";
			document.getElementById('Error_message').innerHTML="";
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
		}
	else 
		{					
			//document.getElementById("Error_message").innerHTML="You must select atleast one value";			
			document.getElementById('ButtonQuestion1').style.display="none";
			document.getElementById('Question2').style.display="none";				
			document.getElementById('ButtonQuestion2').style.display="none";
			document.getElementById('Question3').style.display="none";
			document.getElementById('narrative').style.display="none";
			document.getElementById('capabilityStatement').style.display="none";	
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
		}
	
}
//This method will be called when we change the second drop down on service question page.
function onChange2(value)
{
$('#mySelect2 option[value="'+value+'"]').attr('selected', 'selected');
	if(value=="yes")
		{
	
			
			document.getElementById('ButtonQuestion2').style.display="";
			document.getElementById('mySelect3').value="nothing";
			document.getElementById('Question3').style.display="none";
			document.getElementById('narrative').style.display="none";
			document.getElementById('capabilityStatement').style.display="none";
			document.getElementById('Error_message').innerHTML="";
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
			
		}		
	else if(value=="no")
		{
			
			document.getElementById('ButtonQuestion2').style.display="none";
			document.getElementById('Question3').style.display="";
			document.getElementById('Error_message').innerHTML="";
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
		}
	else
		{			
			
			document.getElementById('ButtonQuestion2').style.display="none";
			document.getElementById('Question3').style.display="none";
			document.getElementById('narrative').style.display="none";
			document.getElementById('capabilityStatement').style.display="none";
			//document.getElementById("Error_message").innerHTML="You must select atleast one value";		
			document.getElementById('Error_message').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
		}
	
}
//This method will set the flag for opening an overlay to save/cancel data if any changes are done on service question jsp.
function setChangeFlag(){
   isdropDownChange = true;
}

//This method will be called when we change the third drop down on service question page.
function onChange3(value)
{
$('#mySelect3 option[value="'+value+'"]').attr('selected', 'selected');
	if(value=="yes")
		{
			document.getElementById('capabilityStatement').style.display="";
			document.getElementById('Error_message').innerHTML="";
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
			document.getElementById('narrative').style.display="none";
		}
	else if(value=="no")
		{
			document.getElementById('narrative').style.display="";
			document.getElementById('capabilityStatement').style.display="none";
			document.getElementById('Error_message').innerHTML="";
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message3').innerHTML="";	
		}
	else
		{
			document.getElementById('narrative').style.display="none";
			document.getElementById('capabilityStatement').style.display="none";
			//document.getElementById("Error_message").innerHTML="You must select atleast one value";
			document.getElementById('Error_message2').innerHTML="";	
			document.getElementById('Error_message').innerHTML="";	
		}
	
}
//This method is called when we save/save next the service question. 
 function selectAllAndSubmit(nextAction) {   
	ques1= document.getElementById('mySelect').value;
	ques2= document.getElementById('mySelect2').value;
	ques3= document.getElementById('mySelect3').value;
	if(ques1=="no" && ques2=="no" && ques3=="no"){
		document.getElementById('narrative').style.display="";
	}else if(ques1=="nothing"){
		$("#Error_message").html("! This field is required.").show();
	}else if(ques2=="nothing" && ques1=="no"){
		$("#Error_message2").html("! This field is required.").show();
	}else if(ques3=="nothing" && ques1=="no" && ques2=="no"){
		$("#Error_message3").html("! This field is required.").show();
	}
	else if(ques1=="nothing"){
	}
	else{
    	document.myform.action = document.myform.action+'&service_app_id='+'<%=renderRequest.getAttribute("service_app_id")%>'+'&business_app_id='+'<%=renderRequest.getAttribute("business_app_id")%>'+'&next_action='+nextAction+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>'+"&ques1="+ques1+"&ques2="+ques2+"&ques3="+ques3+"&elementId="+'<%=renderRequest.getAttribute("elementId")%>';
    	document.myform.submit();
	}
}
</script>

<form name="myform" action="<portlet:actionURL/>" method ="post" >
<c:if test="${loReadOnlySection}">
		<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
	</c:if>

<%if(request.getAttribute("aoContractList")!=null) {  %>
	<input type="hidden" name= "contractList" id="contractList" value="<%= ((List) request.getAttribute("aoContractList")).size()%>"/>
<%} else { %>
	<input type="hidden"  name= "contractList" id="contractList" value="0"/>
<%} %>
	<%if(request.getAttribute("aoStaffDetailsList")!=null) {  %>
<input type="hidden" id="staffList"  name= "staffList" value="<%= ((List) request.getAttribute("aoStaffDetailsList")).size()%>"/>
<%} else { %>
	<input type="hidden" id="staffList" name= "staffList" value="0"/>
<%} %>



<div id="linkDiv"></div>
<div id="iFrameCLick">
<input type="hidden" value="${pageContext.servletContext.contextPath}" id="sessionPath"/>
<input type="hidden" value="" id="validateForm"/>
<c:set var="hideServiceSummaryLink" value='<%=request.getParameter("removeNavigator")%>'></c:set>
			<c:if test="${hideServiceSummaryLink eq null || empty hideServiceSummaryLink}">
				<h2><label class='floatLft wordWrap'>Services: ${serviceName}</label>

					<c:choose>
						<c:when test="${loReadOnlySection}">
							<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${!loReadOnlySection and (loReadOnlyStatus ne null and (loReadOnlyStatus eq 'Returned for Revision' or loReadOnlyStatus eq 'Deferred'))}">
									<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="<portlet:renderURL><portlet:param name='business_app_id' value='${business_app_id}'/><portlet:param name='elementId' value='${elementId}' /><portlet:param name='section' value='${section}'/><portlet:param name='service_app_id' value='${service_app_id}'/><portlet:param name='subsection' value='summary'/><portlet:param name='next_action' value='checkForService'/><portlet:param name='displayHistory' value='displayHistory'/></portlet:renderURL>">Service Summary</a>
								</c:when>
								<c:otherwise>
									<c:set var="urlApplication"
									value="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_add_service&_nfls=false&bussAppStatus=${bussAppStatus}&business_app_id=${business_app_id}&applicationId=${applicationId}&section=servicessummary&subsection=summary&next_action=checkForService"></c:set>              
									<a id="returnSummaryPage" title="Service Summary"  class="floatRht returnButton"  href="${urlApplication}">Service Summary</a>
								</c:otherwise>							
							</c:choose>
						</c:otherwise>
					</c:choose>
				</h2>
			</c:if> 
<div class="hr"></div> 
<div class="messagediv" id="messagediv"></div>
<h3 class="floatLft">Questions</h3>
<div class='floatRht'>
<c:if test="${org_type eq 'provider_org'}">
<c:if test="${serviceComments ne null and ! empty serviceComments}">
							<%@include file="showServiceCommentsLink.jsp" %>
							 <div class="commentHidden" style="padding:10px;">
								<c:forEach var="loopItems" items="${serviceComments}" varStatus="counter">
								     	<c:if test="${counter.index ne 0}">
								     	 -------------------------------------------------<br>
								     	</c:if>
							     <b>${loopItems['USER_ID']} - <fmt:formatDate pattern="MM/dd/yyyy" value="${loopItems['AUDIT_DATE']}" /></b><br>
							      ${loopItems['DATA']}	<br>
						     </c:forEach>
					     </div>
					</c:if>
				</c:if>	
					</div>
	<div class='clear'></div>
		
<div class="servicesRow">
	<span class="servicesCol1">Please answer the question(s) below to indicate that your organization can perform the selected service.</span>
</div>

<div class="servicesRow" id="question1">
	<span class="servicesCol1">Does your organization currently have, or has it had in the past 5 years, contracts or grants to deliver the selected Service?</span>	
	<span class="servicesCol2">
		<select id="mySelect" onchange="onChange(this.value);setChangeFlag();"  ${readOnlyValue}>
	     	<option value="nothing"></option>
	     	<option value="yes">Yes</option>
	     	<option value="no">No</option>
	  	</select><br />
  </span>  
</div>

<div  id="Error_message"></div>

<div class="servicesRow" style="display:none" id="Question2">
	<span class="servicesCol1">Do key staff members employed by your organization have experience delivering the selected  Service?</span>
  	<span class="servicesCol2">
  	<select id="mySelect2" onchange="onChange2(this.value);setChangeFlag();"  ${readOnlyValue}>
     	<option value="nothing"></option>
     	<option value="yes">Yes</option>
      	<option value="no">No</option>
    </select></span>
</div>
 
<div  id="Error_message2"></div>

<div class="servicesRow" id="Question3" style="display:none">
	<span class="servicesCol1">
	You have indicated that your organization has not had contracts or grants within the past five years and does not currently employ staff with experience to perform the selected Service.
	Do you wish to submit a Capability Statement that describes current programs offered and the rationale for launching or expanding Services?
	</span>
  	<span class="servicesCol2">
  	<select id="mySelect3" onchange="onChange3(this.value);setChangeFlag();" ${readOnlyValue}>
    	<option value="nothing"></option>
      	<option value="yes">Yes</option>
      	<option value="no">No</option>
  	</select>
  	</span>
</div>
<div  id="Error_message3"></div>
<div id="ButtonQuestion1" style="display:none">

<c:if test="${!loReadOnlySection}">
	<p>Please add information about a contract or grant that best represents your organization&#39;s relevant Service experience in this area.
	<br/> Note:  For each contract and/or grant entered, you will be required to upload a scope of work/contract/award letter. 
	<br/>You will not be required to upload supporting documentation for any contracts with the City of New York.
	</p>
	<a class ="button right" id="addContract" href="<portlet:renderURL><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="elementId" value="${elementId}" /><portlet:param name="next_action" value="addContract" /><portlet:param name="section" value="servicessummary"/><portlet:param name="subsection" value="questions"/><portlet:param name="business_app_id" value="${business_app_id }" /></portlet:renderURL>">+ Add Contract/Grant Information</a>
</c:if>
<b>&nbsp;</b>
<%if(request.getAttribute("aoContractList")!=null && ((List) request.getAttribute("aoContractList")).size() > 0){%>
	<br/>
	<h2>Add Contract/Grant Information Table</h2>
	<div class="tabularWrapper">
	<st:table  objectName="aoContractList"  cssClass="heading" 
			alternateCss1="evenRows" alternateCss2="oddRows">
			
			<st:property headingName="Funder Name" columnName="msContractFunderName"
				align="left" size="20%" >			
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractNameExtension" />
				</st:property>
			<st:property headingName="Funder Type" columnName="msContractType"
				align="right" size="20%" />
			<st:property headingName="Reference Name" columnName="msContractRefFirstName"
				align="right" size="10%" >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractRefNameExtension" />
			</st:property>
			<st:property headingName="Contract Number" columnName="msContractID"
				align="right" size="10%" >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.ContractNumberNameExtension" />
			</st:property>
				<st:property headingName="Start Date" columnName="msStartDateToDisplay"
				align="right" size="10%" />
			<st:property headingName="End Date" columnName="msEndDateToDisplay"
				align="right" size="10%" />
			<st:property headingName="Action" columnName="msActions" 
				align="right" size="20%" >
				<st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentServiceSummaryContractExtension" />
			</st:property>
	
		
	</st:table>
	</div>
<%}%>
</div>
<div id="ButtonQuestion2" style="display:none">
	<c:if test="${!loReadOnlySection}">
		<p>Please add a key staff person that best represents your organization&#39;s relevant Service experience in this area. You will be required to upload the resume for each staff member added to complete your Service Application.
		</p>
		<a class ="button" id="addStaff" style="float:right;" href="<portlet:renderURL><portlet:param name="elementId" value="${elementId}" /><portlet:param name="business_app_id" value="${business_app_id }" /><portlet:param name="service_app_id" value="${service_app_id}" /><portlet:param name="next_action" value="addStaff" /><portlet:param name="section" value="servicessummary"/><portlet:param name="subsection" value="questions"/></portlet:renderURL>">+ Add Staff</a>
	</c:if>
	<div>&nbsp;</div>
	
	<%if(request.getAttribute("aoStaffDetailsList")!=null && ((List) request.getAttribute("aoStaffDetailsList")).size() > 0){%>
		<h2>Add Staff Member Table</h2>
		
		<div class="tabularWrapper">
		<st:table  objectName="aoStaffDetailsList"  cssClass="heading" 
	                                alternateCss1="evenRows" alternateCss2="oddRows">
	                                
	                                <st:property headingName="Name" columnName="msStaffFirstName"
	                                                align="left" size="10%"  >
	                                                 <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.StaffNameExtension" />
	                                   </st:property>              
	                                <st:property headingName="Title" columnName="msName"
	                                                align="right" size="20%" />
	                                <st:property headingName="Phone Number" columnName="msStaffPhone"
	                                                align="right" size="20%" />
	                                <st:property headingName="Email Address" columnName="msStaffEmail"
	                                                align="right" size="20%" />
	                                                
									<st:property headingName="Action" columnName="msActions" 
	                                                align="right" size="30%" >
	                                                <st:extension decoratorClass="com.nyc.hhs.frameworks.grid.DocumentServiceSummaryStaffExtension" />
	                                </st:property>
	
	                
	    </st:table>
		</div>
	<%}%>
</div>
<div id="capabilityStatement"  style="display:none">
	<ul>
		<b>On the following page,you will be prompted to upload your Capability Statement,which must</b>
		
		<li>1.Describe the current programs offered by organization;AND</li>
		<li>2.Provide a rationale for launching or expanding services;AND</li>
		<li>3.Explain how</li>
			<li>a.Current program relate to the selected services;OR	</li>
			<li>b.Current Staff capability can be leveraged to deliver the selected service</li>
		<li>Your organization's Capability Statement may not exceed 3 pages</li>
	</ul>
</div>

<div id="narrative"  style="display:none;color:red;">
	In order to complete your Service Application, you must have received funding, currently employ staff, or provide a Capability Statement to deliver selected Services.
	You must either update your answers above or return to the Services Summary and remove this service from your Services selection.
</div>


</br>

<c:if test="${!loReadOnlySection}">
	<div class="buttonholder" style="bottom: 10px; right: 0px;">
		<input type="button" class="button" id="saveNext" value="Save & Next " title="Save & Next" onclick="selectAllAndSubmit('save_next')" style="float:right;"/>
		<input type="button" class="button" value="Save" title="Save" onclick="selectAllAndSubmit('saveServiceQuestion')" style="float:right;"/>
	</div>
</c:if>
<!-- Container Starts -->
</div>
   
</form>

