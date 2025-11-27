<%@page import="com.nyc.hhs.model.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages"/>
<script type="text/javascript" src="../js/viewdocumentinfo.js"></script>
<script type="text/javascript" src="../resources/js/calendar.js"></script>
<portlet:defineObjects/>
<style>
	.iconQuestion{
		  margin-left:15px;
		  *margin-top:7px;		  
	}
	.linkReturnVault{
		margin-top: 7px;
	}
	h2{
		width: 81%;
	}
	.errorMessages{
		display:none;
	}
	.formcontainer .row span.label{
		width:32%;
	}
	.formcontainer .row span.formfield{
		width:34% !important;
	}	
	.formcontainer .row span.error{
		 float: left;
	  	 padding: 4px 0;
	     text-align: left; 
		 color:red;
		 width:31%;
	}
</style>
<script type="text/javascript" src="../js/viewdocumentinfo.js"></script>
<script type="text/javascript">
var editFormAction;
//on load function to perform various checks on loading of jsp
$(document).ready(function() {
	editFormAction = document.editpropform.action;
	if('null' != '${message}' && '' !=  '${message}'){
			$(".messagediv").html("${message}"+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagediv', this)\" />");
			$(".messagediv").addClass('${messageType}');
			$(".messagediv").show();
			<%session.removeAttribute("message");%>
		}
	if('<%=ApplicationConstants.CITY_ORG%>' == '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>'){
			$('#docType').hide();
	}
	//This will execute when Save button is clicked. It will validate form for mandatory fields and the submit the form 
	$("#editpropform").validate({
		rules: {
			docName: {required: true, 
				maxlength: 50, allowSpecialChar: ["A"," _-"]},
			periodcoveredfrom: {required: true},
			periodcoveredto: {required: true},
			implementationstatus: {required: true},
			datelastupdated: {required: true},
			effectivedate: {required: true},
			periodcoveredfromyear: {required: true,
				maxlength: 4, minlength: 4},
			periodcoveredtoyear: {required: true,
				maxlength: 4, minlength: 4},
			meetingdate: {required: true},
			helpcategory: {required: true},
			helpradio: {required: true},
			helpdesc: {required: true, 
				maxlength: 250},
			samplecategory: {required: true},
			sampletype: {required: true}
		},
		messages: {
			docName: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
				maxlength: "<fmt:message key='INPUT_50_CHAR'/>",
				allowSpecialChar: "<fmt:message key='ALPHANUMERIC_ALLOWED_DOCUMENT_NAME'/>"},
			periodcoveredfrom: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
				date: "<fmt:message key='INVALID_DATE'/>"},
			periodcoveredto: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			implementationstatus: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			datelastupdated: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			effectivedate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			periodcoveredfromyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
				minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
				maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
			periodcoveredtoyear: {required: "<fmt:message key='REQUIRED_FIELDS'/>",
				minlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>",
				maxlength: "<fmt:message key='YEAR_LENGTH_CHECK'/>"},
			meetingdate: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			helpcategory: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			helpradio: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			helpdesc: {required: "<fmt:message key='REQUIRED_FIELDS'/>", 
				maxlength: "<fmt:message key='INPUT_250_CHAR'/>"},
			samplecategory: {required: "<fmt:message key='REQUIRED_FIELDS'/>"},
			sampletype: {required: "<fmt:message key='REQUIRED_FIELDS'/>"}
		},
		submitHandler: function(form){
			var isValid = true;
			$("input[type='text']").each(function(){
		        if($(this).attr("validate")=='calender'){
		              if(!verifyDate(this)){
		            	  isValid = false;
		              }
		        }
		    });
			if(isValid){
				var id = document.getElementById("docId").value;
				pageGreyOut();
				document.editpropform.action = editFormAction+'&removeNavigator=true&next_action=saveProperties&isAjaxCall=false&documentId='+id;
				document.editpropform.submit();
			}
		},
		errorPlacement: function(error, element) {
		      error.appendTo(element.parent().parent().find("span.error"));
		}
	});
	
	// This will execute when any option is selected for filter Document Category
	$('#samplecategory').change(function() {
		if(samplefilterCategoryForCity()){
		getFilterSampleTypeForCity();
		}	
	});
});

// This will execute when any option is selected for filter Document Category
function samplefilterCategoryForCity() {
	var e = document.getElementById('samplecategory');
	var category = e.options[e.selectedIndex].value;
	if (category == null || category == "") {
		document.getElementById("sampletype").value = "";
		document.getElementById("sampletype").disabled = true;
		return false;
	} else {
		document.getElementById("sampletype").disabled = false;
		return true;
	}

}

// This method will get the filter document category and 
//will return the list of document types through servlet call
function getFilterSampleTypeForCity() {
	pageGreyOut();
	var selectedInput = document.getElementById("samplecategory").value;
	var url = $("#contextPathSession").val()+"/GetContent.jsp?selectedInput=" + selectedInput+"&organizationId=<%=ApplicationConstants.PROVIDER_ORG%>";
	postRequest(url);
	//$.unblockUI();
	removePageGreyOut();
}

//This will execute when any servlet call is made
function postRequest(strURL) {
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
			updatepage(xmlHttp.responseText);
		}
	};
	xmlHttp.send(strURL);
}

//This will execute to get filter document type for filter document category
function updatepage(str) {
	var n = str.split("|");
	var selectbox = document.getElementById("sampletype");
	var i;
	for (i = selectbox.options.length - 1; i >= 0; i--) {
		selectbox.remove(i);
	}
	if (null != n) {
		var optn = document.createElement("OPTION");
		optn.text = "";
		optn.value = "";
		optn.setAttribute("title", "");
		selectbox.options.add(optn);
		for ( var i = 0; i < n.length - 1; i++) {
			var optn = document.createElement("OPTION");
			optn.text = n[i];
			optn.value = n[i];
			optn.setAttribute("title", n[i]);
			selectbox.options.add(optn);
		}
	}
}
</script>

<form name="editpropform" action="<portlet:actionURL/>" method ="post" id="editpropform">
	<input type="hidden" id="docId" value="${document.documentId}">
	<div class="messagediv" id="messagediv"></div>
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
		<div class="linkReturnVault"><a href="#" title="Return to Vault" onclick="javascript:returntoVault()">Return to Vault</a></div>
		<div>
			<h2><b>Document Information:</b><%=document.getDocName()%></h2>
			<div class='hr'></div>
			<div id="reqiredDiv" style="display:none" class="reqiredDiv"><label class="required">*</label>Indicates a Required Field</div>
			<div class="formcontainer">
				<div class="row">
				    <span class="label">Document Category:</span>
				    <span class="formfield"><%=document.getDocCategory()%></span>
				</div>
				<div class="row" id="docType">
					<span class="label">Document Type:</span>
				    <span class="formfield"><%=document.getDocType()%></span>
				</div>
				<div class="row">
				    <span class="label"><label class="required">*</label>Document Name:</span>
				    <span class="formfield"><input type="text" id="docName" name="docName" value="<%=document.getDocName()%>" /></span>
				    <span class="error"></span>
				</div>
				<div class="row">
				    <span class="label">File Type:</span>
				    <span class="formfield"><%=document.getFileType()%></span>
				</div>
				
				<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
				Iterator loIterator = docProps.iterator();
				while(loIterator.hasNext()){%>
					<script type="text/javascript">
						$(".reqiredDiv").show();
					</script>
					<%DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();%>
					<c:set var="loDocPropsBean" value="<%=loDocPropsBean%>"></c:set>
					<%
					if("string".equalsIgnoreCase(loDocPropsBean.getPropertyType()))
					{
						if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled())
					    {
							%>
							<div class="row">
							<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
							<span class="formfield"><input class= "readonly" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
							<%  
					    }
					    else
					    {
					    	if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
								%>
								<input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>"/>
								</span>
								<span class="error"></span>
								</div>
								<% 	
								}
								else if(loDocPropsBean.isIsdropdown())
					       		{
									%>
									<div class="row">
								    <span class="label"><font class="required">*</font><%=loDocPropsBean.getPropDisplayName()%>:</span>
								    <span class="formfield">
									<%if("Implementation Status".equals(loDocPropsBean.getPropDisplayName())){%>
										<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input">
											<c:forEach var="status" items="${document.implementationStatus}" >
												<c:if test="${status eq loDocPropsBean.propValue}">
													<option value="<c:out value="${status}"/>" selected><c:out value="${status}"/></option>
												</c:if>
												<c:if test="${status ne loDocPropsBean.propValue}">
													<option value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
												</c:if>
											</c:forEach>
										</select>
										<%}else if("Sample Document Category".equals(loDocPropsBean.getPropDisplayName())){ %>
											<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input">
												<c:forEach var="status" items="${document.sampleCategoryList}" >
													<c:if test="${status eq loDocPropsBean.propValue}">
														<option value="<c:out value="${status}"/>" selected><c:out value="${status}"/></option>
													</c:if>
													<c:if test="${status ne loDocPropsBean.propValue}">
														<option value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
													</c:if>
												</c:forEach>
											</select>
											<%} else if("Sample Document Type".equals(loDocPropsBean.getPropDisplayName())){ %>
											<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input">
												<c:forEach var="status" items="${document.sampleTypeList}" >
													<c:if test="${status eq loDocPropsBean.propValue}">
														<option value="<c:out value="${status}"/>" selected><c:out value="${status}"/></option>
													</c:if>
													<c:if test="${status ne loDocPropsBean.propValue}">
														<option value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
													</c:if>
												</c:forEach>
											</select>
											<%} else if("Help Category".equals(loDocPropsBean.getPropDisplayName())){ %>
												<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input">
													<c:forEach var="status" items="${document.helpCategoryList}" >
														<c:if test="${status eq loDocPropsBean.propValue}">
															<option value="<c:out value="${status}"/>" selected><c:out value="${status}"/></option>
														</c:if>
														<c:if test="${status ne loDocPropsBean.propValue}">
															<option value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
														</c:if>
													</c:forEach>
												</select>
											<%} %>
										</span>
										<span class="error"></span>
										</div>
										<%     
					      				}else if("Document Description".equals(loDocPropsBean.getPropDisplayName())){%>
					      					<div class="row">
										    	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
										        <span class="formfield"><textarea rows="8" cols="35" name="<%=loDocPropsBean.getPropertyId()%>" id="<%=loDocPropsBean.getPropertyId()%>"><%=loDocPropsBean.getPropValue()%></textarea></span>
										        <span class="error"></span>
								        	</div>
					      				<%}
					      				else
					       		 		{%>
											<div class="row">
										    	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
										        <span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>"/></span>
										        <span class="error"></span>
								    		 </div>
								    	<% 
					      				}
					      			}	
					     		}else if("boolean".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
			    					<div class="row">
					    				<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
					    				<%if("DISPLAY_HELP_ON_APP".equals(loDocPropsBean.getPropSymbolicName())){ %>
					   					<%if(Boolean.valueOf(loDocPropsBean.getPropValue().toString())){ %>
										<input type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="yes" id="rdoyes" checked/><label for="rdoyes">Yes</label><br>
										<input type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="no" id='rdono' /><label for='rdono' >No</label><br>
									
										<%}else{%>
										<input type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="yes" id="rdoyes" /><label for="rdoyes">Yes</label><br>
										<input type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="no" id='rdono' checked/><label for='rdono' >No</label><br>
										<%}}else{ %>
									    <%if(Boolean.valueOf(loDocPropsBean.getPropValue().toString())){ %>
									  		  <span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" checked/></span>
										<% }else{ %>	
										 		<span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>	    
										<%}
									    } %>
									     <span class="error"></span>		   
							    	</div>
						    <% }else if("int".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
						    	<div class="row">
									<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
								    <span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>"/></span>
								    <span class="error"></span>
						    	</div>
							<% }else if("date".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%> 		
								<div class="row">
							       	<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
								    <span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>" validate="calender" maxlength="10"/>
										  <img src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;"/>
									</span>
									<span class="error"></span>
						    	</div>
							<%
					       	}}
						%>
				<div class="buttonholder">
					  <input type="button" title="Cancel" id="cancel" value="Cancel" onclick="cancelEdit('${document.documentId}')" class="graybtutton"/>
					  <input type="submit" title="Save" value="Save"/>
				</div>
			</div>
		</div>
		<div class="overlay"></div>
		<div class="alert-box-help">
			<div class="content">
		  		<div id="newTabs" class='wizardTabs'>
					<div class="tabularCustomHead">Document Vault - Help Documents</div>
		            <div id="helpPageDiv"></div>
				</div>
		  	</div>
		  	<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
		</div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
	<input type="hidden" name="hiddenDocCategory" value='<%=document.getFilterDocCategory()%>' />	
	<input type="hidden" name="hiddenDocType" value='<%=document.getFilterDocType()%>'/>
	<input type="hidden" name="hiddenFilterModifiedFrom" value='<%=document.getFilterModifiedFrom()%>' />
	<input type="hidden" name="hiddenFilterModifiedTo" value='<%=document.getFilterModifiedTo()%>' />
	<input type="hidden" name="hiddenFilterProviderId" value='<%=document.getFilterProviderId()%>' />
	<input type="hidden" name="hiddenFilterNYCAgency" value="<%=document.getFilterNYCAgency()%>"/>
	<input type="hidden" name="hiddenDocShareStatus" value='<%=document.getDocSharedStatus()%>' />	
	<input type="hidden" name="hiddenSampleCategory" value='<%=document.getFilterSampleCategory()%>'/>	
	<input type="hidden" name="hiddenSampleType" value='<%=document.getFilterSampleType()%>'/>
	<input type="hidden" name="sortBy" value='${sortBy}'/>
	<input type="hidden" name="sortType" value='${sortType}'/>
</form>