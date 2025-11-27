
<%@page import="com.nyc.hhs.constants.HHSR5Constants"%>
<%@page import="com.nyc.hhs.util.DateUtil"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, java.util.Date" %>
<%@ page errorPage="/error/errorpage.jsp" %>


<form id="myform">
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
	<div class="wizardTabs" style='width:800px;'>
		<h3><b>Document Information:</b><%=document.getDocName()%></h3>
			
		<div class='hr'></div>
		<div class="formcontainer">
			<div class="row">
			<%-- Start changes for R5 --%>
				<span class="label">Document Location:</span>
				<span class="formfield folderPathProp wrap-by-para documentLocationPath"><%=document.getFilePath()%></span>
			<%-- End changes for R5 --%>
			</div>
			<div class="row">
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
			<%-- Start changes for R5 --%>
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
			<%-- End changes for R5 --%>
			<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
			Iterator loIterator = docProps.iterator();
			while(loIterator.hasNext()){
				DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
				if(loDocPropsBean.getPropertyType().equalsIgnoreCase("string"))
				{
					if(!loDocPropsBean.getPropDisplayName().equalsIgnoreCase("") && loDocPropsBean.isIsdisabled())
						{
					    %>
					    	<div class="row">
					      	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
					       	<span class="formfield"><%=loDocPropsBean.getPropValue().toString()%>
					    <%  
						}
					else
						{
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
					            	<!-- Adding a fix to defect *5459* - Added a check to show Property value for Property ID: 'Implementation Status' -->
					            	<%if(null != loDocPropsBean.getPropertyId() && loDocPropsBean.getPropertyId().equalsIgnoreCase(ApplicationConstants.IMPLEMENTATION_STATUS)){%>
					            		<span class="formfield"><%=loDocPropsBean.getPropValue().toString()%></span>
					            	<%}else {%>
					            		<span class="formfield"><input type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/></span>
					            	<%} %>
			    		 		</div>
			    		  		<% 
					      	}}
					     	%>
				
				<%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("date")){ %>
			    	<div class="row">
						<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						<%String lsDate = DateUtil.getDateByFormat(HHSR5Constants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,HHSR5Constants.MMDDYYFORMAT,loDocPropsBean.getPropValue().toString());%>
					    <span class="formfield"><%=lsDate%></span>
			    	</div>
			    <%}else if("boolean".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
			    	<div class="row">
						<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
					    <%if(Boolean.valueOf(loDocPropsBean.getPropValue().toString())){ %>
					    <span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" checked disabled="disabled"/></span>
						<% }else{ %>	
						<span class="formfield"><input type="checkbox" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" disabled="disabled"/></span>	    
						<%} %>		   
			    	</div>
			    <% }else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("int")){ %>
			    <div class="row">
					<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
					<span class="formfield"><%=Integer.valueOf(loDocPropsBean.getPropValue().toString())%></span>
			    </div>
				<%}} %>			
		</div>
	</div>
</form>
