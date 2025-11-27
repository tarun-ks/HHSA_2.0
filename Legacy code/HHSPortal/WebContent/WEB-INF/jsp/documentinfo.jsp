<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, java.util.Date" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Insert title here</title>
	<script>
		function editDocument(){
			document.forms[0].action = document.forms[0].action+'&removeNavigator=true&next_action=editDocumentProps';
			document.forms[0].submit();
		}
		function backtoDocVault(){
			document.forms[0].action = document.forms[0].action+'&removeNavigator=true';
			document.forms[0].submit();
		}
	</script>
</head>
<body>
	<form id="myform" action="<portlet:actionURL/>" method ="post" >
		<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<div class="wizardTabs" style='width:800px;'>
		<div class="linkReturnVault"><a href="#" onclick="javascript:backtoDocVault()">Return to Vault</a></div>
				<h3><b>Document Information:</b><%=document.getDocName()%><label class="linkEdit"><a href="#" onclick="javascript:editDocument()">Edit Properties</a> </label></h3>
				
				<div class='hr'></div>
				<div class="formcontainer">
					<div class="row">
					      <span class="label">Document Category:</span>
					      <span class="formfield"><%=document.getDocCategory()%></span>
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
					<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
						       Iterator loIterator = docProps.iterator();
						       while(loIterator.hasNext()){
						       		DocumentPropertiesBean loDocPropsBean = (DocumentPropertiesBean) loIterator.next();
						       		if(loDocPropsBean.getPropertyType().equalsIgnoreCase("string")){
						       		       		
					%>
					<div class="row">
						     <span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						     <span class="formfield"><%=loDocPropsBean.getPropValue().toString()%></span>
				    </div>
				    <%}else if(loDocPropsBean.getPropertyType().equalsIgnoreCase("date")){ %>
				    <div class="row">
						     <span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
						     <span class="formfield"><%=loDocPropsBean.getPropValue().toString()%></span>
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
</body>
</html>