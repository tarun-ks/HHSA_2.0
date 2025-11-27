<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, java.util.Date, com.nyc.hhs.constants.ApplicationConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant,com.nyc.hhs.util.DateUtil,com.nyc.hhs.constants.HHSR5Constants" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<div class="overlaycontent" id="overlaycontent">
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<div>
	<div class="messagedivover" id="messagedivover"> </div>
	<%
		List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
	 	if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S112_PAGE, request.getSession())){%>
			
			<%if(("true").equalsIgnoreCase((String) request.getAttribute("EditVersionProp"))){%>
    			<%if(((request.getAttribute("isViewDocInfoOrg") ==null) || !(request.getAttribute("isViewDocInfoOrg").equals("true")))
    				&& (null != docProps && docProps.size()>0)){%>
    				<h2><b>Document Information:</b><%=document.getDocName()%><label class="linkEdit"><a href="#" title="Edit Properties"  
    				onclick="javascript:editDocument('<%=document.getDocumentId()%>')" id="edit">Edit Properties</a>
    				</label></h2>
   				<%}else{%>
   					h2><b>Document Information:</b><%=document.getDocName()%></h2>
  			<%}}%>
 			<%if(null != request.getAttribute("isLocked") && "true".equalsIgnoreCase((String)request.getAttribute("isLocked"))){%>
   				<script> 
   					$(".messagedivover").html("You can not edit this document as some one else is working on it. Please try after some time."+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
					$(".messagedivover").addClass("failed");
					$(".messagedivover").show();
					$("#edit").attr("disabled", "disabled");
				</script>
   			<%} %>
			<div class="formcontainer" >
			<div class='row'>
						<span class='label'>Document Location: </span> <span
							class="formfield folderPathProp wrap-by-para documentLocationPath"> <%=document.getFilePath()%></span>
					</div>
				<div class="row" id="docType">
				      <span class="label">Document Type:</span>
				      <span class="formfield"><%=document.getDocType()%></span>
				</div>
				<div class="row">
				      <span class="label">Document Name:</span>
				      <span class="formfield"><%=document.getDocName()%></span>
				</div>
				<div class="row">
				      <span class="label">File Type:</span>
				      <span class="formfield"><%=document.getFileType()%></span>
				</div>
				<div class='row'>
						<span class='label'>Modified By: </span> <span class="formfield"><%=document.getLastModifiedBy()%></span>
					</div>
					<div class='row'>
						<span class='label'>Modified Date: </span> <span class="formfieldTimestamp"><%=document.getDate()%></span>
					</div>
					<div class='row'>
						<span class='label'>Uploaded By: </span> <span class="formfield"><%=document.getCreatedBy()%></span>
					</div>
					<div class='row'>
						<span class='label'>Uploaded Date: </span> <span class="formfieldTimestamp"><%=document.getCreatedDate()%></span>
					</div>
				<% 	if(null != docProps || docProps.size()>0){
		        	Iterator loIterator = docProps.iterator();
			        while(loIterator.hasNext()){
			       		DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
				       	if(loDocPropsBean.getPropertyType().equalsIgnoreCase("string")){
				       		if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled()){
					       		%>
						       	<div class="row">
						      	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						       	<span class="formfield"><%=loDocPropsBean.getPropValue().toString()%>
						        <%  
					       		}
					       		else{
				       		 	if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
					       			%>
				       				<%=loDocPropsBean.getPropValue().toString()%></span>
					        		</div>
					       			<% 	
				       		 	}
				       		 	else
				       		 	{
				       		 	%>
									<div class="row">
							            <span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
							            <span class="formfield"><%=loDocPropsBean.getPropValue().toString()%></span>
					    		 	</div>
				    		 	 <% 
							     }}
							     %>
				
				
			            <%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("date")){ %>
			    		<div class="row">
						     <span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						     <span class="formfield"><% if(null != loDocPropsBean.getPropValue()){%>
						     	<c:out value="<%=loDocPropsBean.getPropValue()%>"/> 
						     	<% }%>
						     </span>
			    		</div>
			    		<%}else if("boolean".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
			    		<div class="row">
						    <span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						   	<%if("DISPLAY_HELP_ON_APP".equals(loDocPropsBean.getPropSymbolicName())){ %>
						   		<%if(Boolean.valueOf(loDocPropsBean.getPropValue().toString())){ %>
									<input type="radio" name="help" value="yes" id="rdoyes" checked disabled="disabled"/><label for="rdoyes">Yes</label><br>
									<input type="radio" name="help" value="no" id='rdono' disabled="disabled"/><label for='rdono' >No</label><br>
								<%}else{%>
									<input type="radio" name="help" value="yes" id="rdoyes" disabled="disabled"/><label for="rdoyes">Yes</label><br>
									<input type="radio" name="help" value="no" id='rdono' checked disabled="disabled"/><label for='rdono' >No</label><br>
							<%}}else{ %>
							    <%if(Boolean.valueOf(loDocPropsBean.getPropValue().toString())){ %>
							    	<span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" checked disabled="disabled"/></span>
								<% }else{ %>	
								 	<span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" disabled="disabled"/></span>	    
							<%}} %>		   
			    		</div>
			    		<% }else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("int")){ %>
			     		<div class="row">
					     	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
					     	<span class="formfield"><%=Integer.valueOf(loDocPropsBean.getPropValue().toString())%></span>
			    		</div>
				<%}}}%>			   
			   </div>
			<% } else {%>
		   		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		   <%} %>
	 </div>
</div>