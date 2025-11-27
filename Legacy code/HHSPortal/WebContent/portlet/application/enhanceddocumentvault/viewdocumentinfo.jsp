<%@page import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.Iterator, com.nyc.hhs.model.DocumentPropertiesBean, java.util.Date, com.nyc.hhs.constants.ApplicationConstants,com.nyc.hhs.constants.HHSConstants, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant,com.nyc.hhs.util.DateUtil" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/viewdocumentinfo.js"></script>
<script>
	//on load function to perform various checks on loading of jsp
	$(document).ready(function() {
		if(('<%=request.getAttribute("cityToProvider")%>' == 'null') && ('<%=request.getAttribute("cityToProvider")%>' != 'true')
				&& ('<%=ApplicationConstants.CITY_ORG%>' == '<%=session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE)%>')){
			$('#docType').hide();
		}
	});
</script>
<style>
	.iconQuestion2, .iconQuestion{
		  margin-left:15px;
		  margin-top:7px;
		  *margin-top:7px;  
		  	   display: block;
    float: right;
    margin-top: 10px;
	}
	.linkReturnVault{
		margin-top: 7px;
	}
	h2{
		width: auto;
	}
	h2 span{
		color: #5077aa;
		font-size: 14px;
	}
	
	
</style>
<!-- Document Information Overlay -->
<form id="myform" action="<portlet:actionURL/>" method ="post" name="viewdocform">
 <input type="hidden" name="cityUserSearchProviderId" value="${cityUserSearchProviderId}">
 <input type="hidden" name="documentOriginator" value="${documentOriginator}">
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request"></jsp:useBean>
		<!-- Updated for release 3.3.0, Defect 6449 -->
		<c:if test="${(headerNameForHelpOverlay eq null or headerNameForHelpOverlay eq '') and isViewDoc ne 'yes'}">
			<div class="iconQuestion2"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Document Vault');"></a></div>
			<div class="linkReturnVault">
				<c:choose>
					<c:when test="${app_menu_name eq null or app_menu_name eq 'header_organization_information' or app_menu_name eq 'header_application' or app_menu_name eq 'home_icon'}">
						<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_documentlist&_nfls=false&app_menu_name=header_document_vault&removeNavigator=true" title="Return to Vault">Return to Vault</a>
					</c:when>
					<c:otherwise>
						<a href="#" title="Return to Vault" onclick="javascript:backtoDocVault()">Return to Vault</a>
					</c:otherwise>
				</c:choose>
						
			</div>
		</c:if>
	<div class="messagedivover floatNone" id="messagedivover"> </div>
	<% List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
	if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S112_PAGE, request.getSession())){
	%>
	    <%if((request.getAttribute("isViewDocInfoOrg") ==null) || !(request.getAttribute("isViewDocInfoOrg").equals("true"))){%>
		    	<h2>Document Information: <span><%=document.getDocName()%></span><label class="linkEdit"><a href="#" title="Edit Properties"  
		    	onclick="javascript:editDocument('<%=document.getDocumentId()%>')" id="edit">Edit Properties</a>
		    	</label></h2>
		    	<div class='hr'></div>
	   		<%}else{%>
		   		<h2>Document Information:<span><%=document.getDocName()%></span></h2>
		   		
   		<c:if test="${(app_menu_name eq null or app_menu_name eq 'home_icon') and org_type eq 'provider_org'}">
		    	<div class="linkReturnVault">
				<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_4&_urlType=render&wlpportletInstance_4_next_action=open&return_action=open&cityUserSearchProviderId=${cityUserSearchProviderId}&documentOriginator=${documentOriginator}&wlpportletInstance_4_providerName=provider+new+manager1&wlpportletInstance_4_subsection=documentlist&wlpportletInstance_4_action=documentVault&wlpportletInstance_4_provider=provider&wlpportletInstance_4_section=sharedDoc" title="Return to Vault">Return to Vault</a>
				</div>
   		</c:if>
   		<c:if test="${org_type eq 'agency_org'}">
		    	<div class="linkReturnVault">
				<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_16&_urlType=render&wlpportletInstance_16_next_action=open&return_action=open&cityUserSearchProviderId=${cityUserSearchProviderId}&documentOriginator=${documentOriginator}&wlpportletInstance_16_providerName=provider+new+manager1&wlpportletInstance_16_subsection=documentlist&wlpportletInstance_16_action=documentVault&wlpportletInstance_16_provider=provider&wlpportletInstance_16_section=sharedDoc" title="Return to Vault">Return to Vault</a>
				</div>
   		</c:if>
   		
   		<c:if test="${org_type eq 'city_org' and (headerNameForHelpOverlay ne '' )}">
		    	<div class="linkReturnVault">
				<a href="${pageContext.servletContext.contextPath}/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_12&_urlType=render&wlpportletInstance_12_next_action=openProviderView&wlpportletInstance_12_subsection=documentlist&wlpportletInstance_12_action=documentVault&wlpportletInstance_12_provider=provider&wlpportletInstance_12_section=sharedDoc" title="Return to Vault">Return to Vault</a>
				</div>
   		</c:if>
		   		
		 <div class='hr'></div>
   		<%}if(null != request.getAttribute("isLocked") && "true".equalsIgnoreCase((String)request.getAttribute("isLocked"))){%>
   			<script> 
   				$(".messagedivover").html("You can not edit this document as some one else is working on it. Please try after some time."+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
				$(".messagedivover").addClass("failed");
				$(".messagedivover").show();
				$("#edit").attr("disabled", "disabled");
				$("#edit").removeAttr('href');
				$("#edit").removeAttr('onclick');

			</script>
   		<%}%>
   		<%if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))
   				&& (request.getAttribute("isViewDocInfoOrg") == null)){%>
   			<script> 
   				$(".messagedivover").html("You can only edit the document properties if the application the document is tied to is in a draft, returned, or deferred status");
				$(".messagedivover").addClass("failed");
				$(".messagedivover").show();
				$("#edit").attr("disabled", "disabled");
				$("#edit").removeAttr('href');
				$("#edit").removeAttr('onclick');

			</script>
   		<%} %>
   		<%if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.CITY_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))){%>
   				<script> $("#edit").hide();</script>
   		<%}%>
   		<%if(P8Constants.PROPERTY_CE_DOC_TYPE_BAFO_DOCUMENT.equalsIgnoreCase(document.getDocType())){%>
   				<script> $("#edit").hide();</script>
   		<%}%>
   		<c:if test="${document.docType eq appli }"></c:if>
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
				<span class="label">Document Name:</span>
				<span class="formfield"><%=document.getDocName()%></span>
			</div>
			<div class="row">
				<span class="label">File Type:</span>
				<span class="formfield"><%=document.getFileType()%></span>
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
						    <%String lsDate = DateUtil.getDateByFormat(HHSConstants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,HHSConstants.MMDDYYFORMAT,loDocPropsBean.getPropValue().toString());%>
						    <span class="formfield"><% if(null != loDocPropsBean.getPropValue()){%>
						     	<c:out value="<%=lsDate%>"/> 
						     	<% 
						     	}%>
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
			    <!-- Updated for release 3.3.0, Defect 6449 -->
			    <%if(((request.getAttribute("isViewDocInfoOrg") ==null) || !(request.getAttribute("isViewDocInfoOrg").equals("true"))) && (request.getAttribute("isViewDoc") == null)) {%>
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
				<%}%>
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
			<% } else {%>
		   		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
		    <%} %>
</form>