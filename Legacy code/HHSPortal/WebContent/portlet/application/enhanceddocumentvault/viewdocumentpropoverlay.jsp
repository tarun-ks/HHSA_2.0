<!-- Initial Imports -->
<%@page
	import="com.nyc.hhs.service.filenetmanager.p8constants.P8Constants"%>
<%@page import="com.nyc.hhs.model.Document"%>
<%@page import="java.util.ArrayList"%>
<%@page	import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>
<%@page	import="com.nyc.hhs.constants.ComponentMappingConstant,org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.nyc.hhs.util.DateUtil"%>
<%@page	import="java.util.Map,java.util.HashMap,com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb,java.util.List, java.util.Iterator"%>
<%@page import="com.nyc.hhs.model.DocumentPropertiesBean"%>
<!-- End -->
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<!-- Including Tag Libraries -->
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<fmt:setBundle basename="com/nyc/hhs/properties/errormessages" />
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<portlet:defineObjects/>
<!-- View Document Properties Overlay -->

<div class="alert-box alert-box-viewFolder" style="display: none">
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document"
		scope="request"></jsp:useBean>
	<div class="content">
		<div id="newTabs">
			<div class="tabularCustomHead">Document Information</div>
			<%
				if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.DV_S112_PAGE, request.getSession())){ 
				List<DocumentPropertiesBean> docProps = document.getDocumentProperties();
			%>
			<div class='overlayWrapper formcontainer docInfo'>
				<form id="docSaveForm" action="<portlet:actionURL/>" method="post"
					name='docSaveForm'>
					<div class="messagedivover" id="messagedivoverCs"></div>
					</br>
					<b class="boldlayout">Document Information</b>
					<c:set var="check"><%= session.getAttribute("permissionType") %></c:set>
					<c:set var="role_current"><%= session.getAttribute("role_current") %></c:set>
					<c:set var="EditVersionProp">${EditVersionProp}</c:set>
					<c:set var="jsp">${portletSessionScope.jspName}</c:set>
					<c:set var="locking">${lockingForView}</c:set>
					<c:set var="EditPropCheck">${editPropCheck}</c:set>
					<!-- QC 8914 R7.2 read only role  --> 
				  	<%
					if( CommonUtil.hideForOversightRole(request.getSession()) ){ %>
						<c:set var="EditVersionProp" value="${false}"></c:set>	
					<%}
					%> 
					
				
					<c:set var="user_organization">${user_organization}</c:set>
					<c:if test="${org_type eq 'city_org' }">
						<c:set var="user_organization">${org_type}</c:set>
					</c:if>
					<input type="hidden" value="${user_organization}" id="hddnuser_organization" />
					<input type="hidden" value="${document.userOrg}" id="hddndocument_userOrg" />
					<input type="hidden" value="${check}" id="hddnCheck" />
					<input type="hidden" value="${portletSessionScope.jspName}" id="hddnjspName" />
					<input type="hidden" value="${editPropCheck}" id="hddneditPropCheck" />
					<input type="hidden" value="${lockingForView}" id="hddnlockingForView" />
		<c:if test="${jsp ne 'recyclebin' and document.userOrg eq user_organization and check ne 'R'}">
		<c:choose>
		<c:when test="${EditVersionProp ne true or locking eq true}">
		<label class="linkEdit" style="margin-left: 45px"><label href="#"
								title="Edit Properties"
								id="editDocInfo"  style="color:#999999">Edit Properties</label> </label>
		</c:when>
		<c:otherwise>
		<label class="linkEdit" style="margin-left: 45px"><a href="#" title="Edit Properties" 
			onclick="editDocInfo('<%=document.getDocumentId()%>')" id="editDocInfo">Edit Properties</a> </label>
		</c:otherwise>
		</c:choose>
		</c:if>
		<c:if test="${jsp ne 'recyclebin' and document.userOrg eq user_organization and check eq 'R'}">
			<label class="linkEdit" style="margin-left: 45px"><label href="#"
								title="Edit Properties"
								id="editDocInfo"  style="color:#999999">Edit Properties</label> </label>
		</c:if>			
					<%
					if(null != request.getAttribute("isLocked") && "true".equalsIgnoreCase((String)request.getAttribute("isLocked"))){
					%>
					<script>
						$(".messagedivover")
								.html(
										"You can not edit this document as some one else is working on it. Please try after some time."
												+ "<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
						$(".messagedivover").addClass("failed");
						$(".messagedivover").show();
						$("#editDocInfo").attr("disabled", "disabled");
						$("#editDocInfo").removeAttr('href');
						$("#editDocInfo").removeAttr('onclick');
					</script>
					<%
						} if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))
					 				&& (request.getAttribute("isViewDocInfoOrg") == null)){
					%>
					<script>
						$(".messagedivover")
								.html(
										"You can only edit the document properties if the application the document is tied to is in a draft, returned, or deferred status");
						$(".messagedivover").addClass("failed");
						$(".messagedivover").show();
						$("#editDocInfo").attr("disabled", "disabled");
						$("#editDocInfo").removeAttr('href');
						$("#editDocInfo").removeAttr('onclick');
					</script>
					<%
						} if(("false").equalsIgnoreCase((String) request.getAttribute("EditVersionProp")) && ApplicationConstants.CITY_ORG.equalsIgnoreCase((String)session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE))){
					%>
					<script>
						$("#editDocInfo").hide();
					</script>
					<%
						} if(P8Constants.PROPERTY_CE_DOC_TYPE_BAFO_DOCUMENT.equalsIgnoreCase(document.getDocType())){
					%>
					<script>
						$("#editDocInfo").hide();
					</script>
					<%
						}
					%>
					<hr class="dottedSpace" />
					<c:choose>
					<c:when test="${(org_type eq 'agency_org' or org_type eq 'provider_org')  and document.userOrg ne user_organization}">
					</c:when>
					<c:otherwise>
					<div class='row' id='documentlocation'>
						<span class='label'>Document Location: </span> <span 
							class="formfield1 folderPathProp wrap-by-para documentLocationPath"> <%=document.getFilePath()%></span>
					</div>
					</c:otherwise>
					</c:choose>
					<div class='row' id="docTypeInfo">
						<span class='label'>Document Type:</span> <span class="formfield"><%=document.getDocType()%></span>
					</div>
					<div class='row'>
						<span class='label'>Document Name:</span> 
						<span id="editDocName"
							class="formfield" style="word-wrap:break-word;"><%=document.getDocName()%></span> <input
							style="display: none; margin-top:3px;" type="text" maxlength="50"
							value="<%=document.getDocName()%>" 
							name="editDocNameText" id="editDocNameText" /> <span
							class="error" />
					</div>
					<div class='row'>
						<span class='label'>File type: </span> <span class="formfield"><%=document.getFileType()%></span>
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
						<span class='label'>Upload Date: </span> <span class="formfieldTimestamp"><%=document.getCreatedDate()%></span>
					</div>
					<!-- Added to get Share With list - Fix for Defect # 7493 -->
				 	<c:if test="${(org_type eq 'agency_org' or org_type eq 'city_org') and document.sharingOrgName ne null and document.userOrg ne user_organization }" >
					<div class='row'>
						<span class='label'>Shared With: </span> 
						<span class="formfield folderPathProp wrap-by-para docShareWithList"><%=document.getSharingOrgName()%></span>
					</div>
					</c:if>
					<!--Fix for Defect # 7493 end -->
					<c:if test="${portletSessionScope.jspName eq 'recyclebin'}">
										<div class='row'>
							<span class='label'>Deleted By:
							</span>
							<span class="formfield"><%=document.getDeletedBy()%></span>
							</div>
							<div class='row'>
							<span class='label'>Deletion Date:
							</span>
							<span class="formfieldTimestamp"><%=document.getDeletedDate()%></span>
							</div>
									</c:if>

					<%
						if (null != docProps && docProps.size() > 0) {
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
										<span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue()%></span><input class= "readonly customProp" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" readonly="readonly" value= "<%=loDocPropsBean.getPropValue()%>"/>
										<%  
								    }
								    else
								    {
								    	if(loDocPropsBean.getPropDisplayName().equalsIgnoreCase("")){
											%>
											<span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue()%></span>
											<input class="customProp" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>"/>
											<span class="error"></span>
											</div>
											<% 	
											}
											else if(loDocPropsBean.isIsdropdown())
								       		{
												%>
												<div class="row periodCoveredClass">
											    <span class="label"><font class="required">*</font><%=loDocPropsBean.getPropDisplayName()%>:</span>
											    <span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue() %></span>
												<%if("Implementation Status".equals(loDocPropsBean.getPropDisplayName())){%>
													<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input customProp">
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
														<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input customProp">
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
														<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input customProp">
															<c:forEach var="status" items="${document.sampleTypeList}" >
																<c:if test="${status eq loDocPropsBean.propValue}">
																	<option class="customProp" value="<c:out value="${status}"/>" selected><c:out value="${status}"/></option>
																</c:if>
																<c:if test="${status ne loDocPropsBean.propValue}">
																	<option class="customProp" value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
																</c:if>
															</c:forEach>
														</select>
														<%} else if("Help Category".equals(loDocPropsBean.getPropDisplayName())){ %>
															<select id = "<%=loDocPropsBean.getPropertyId()%>" name="<%=loDocPropsBean.getPropertyId()%>" class="input customProp customProp2">
																<c:forEach var="status" items="${document.helpCategoryList}" >
																	<c:if test="${status eq loDocPropsBean.propValue}">
																		<option class="customProp" value="<c:out value="${status}"/>" selected><c:out value="${status}"/></option>
																	</c:if>
																	<c:if test="${status ne loDocPropsBean.propValue}">
																		<option class="customProp" value="<c:out value="${status}"/>"><c:out value="${status}"/></option>
																	</c:if>
																</c:forEach>
															</select>
														<%} %>
													<span class="error"></span>
													</div>
													<%     
								      				}else if("Document Description".equals(loDocPropsBean.getPropDisplayName())){%>
								      					<div class="row">
													    	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
													        <span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue()%></span><textarea class="customProp customProp1" rows="8" cols="20" name="<%=loDocPropsBean.getPropertyId()%>" id="<%=loDocPropsBean.getPropertyId()%>"><%=loDocPropsBean.getPropValue()%></textarea>
													        <span class="error"></span>
											        	</div>
								      				<%}
								      				else
								       		 		{%>
														<div class="row">
													    	<span class="label"><%=loDocPropsBean.getPropDisplayName()%>:</span>
													        <span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue() %></span><input type="text" class="customProp" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>"/>
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
								   				
													<input class="radioButtonHelp" disabled="disabled" type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="yes" id="rdoyes" checked/><label for="rdoyes">Yes</label><br>
													<input class="radioButtonHelp" disabled="disabled" type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="no" id='rdono' /><label for='rdono' >No</label><br>
												
													<%}else{%>
													<input class="readonly" type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="yes" id="rdoyes" /><label for="rdoyes">Yes</label><br>
													<input class="readonly" type="radio" name="<%=loDocPropsBean.getPropertyId()%>" value="no" id='rdono' checked/><label for='rdono' >No</label><br>
													<%}}else{ %>
												    <%if(Boolean.valueOf(loDocPropsBean.getPropValue().toString())){ %>
												  		  <span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue()%></span><input type="checkbox customProp" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" checked/>
													<% }else{ %>	
													 		<span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue()%></span><input type="checkbox customProp"  name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>"/>    
													<%}
												    } %>
												     <span class="error"></span>		   
										    	</div>
									    <% }else if("int".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%>
									    	<div class="row">
												<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
											    <span class="formfield customPropformfield"><%=loDocPropsBean.getPropValue()%></span><input class="customProp" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=loDocPropsBean.getPropValue().toString()%>"/>
											    <span class="error"></span>
									    	</div>
										<% }else if("date".equalsIgnoreCase(loDocPropsBean.getPropertyType())){%> 		
											<div class="row periodCoveredClass">
										       	<span class="label"><label class="required">*</label><%=loDocPropsBean.getPropDisplayName()%>:</span>
										      <%String lsDate = DateUtil.getDateByFormat(HHSR5Constants.HHSUTIL_E_MMM_DD_HH_MM_SS_Z_YYYY,HHSR5Constants.MMDDYYFORMAT,loDocPropsBean.getPropValue().toString());%>
											    <span class="formfield customPropformfield"><%=lsDate%></span><input class="customProp" type="text" name="<%=loDocPropsBean.getPropertyId()%>" id = "<%=loDocPropsBean.getPropertyId()%>" value="<%=lsDate%>" validate="calender" maxlength="10"/>
													  <span class="imgClass"> <img id= "cal" src="../framework/skins/hhsa/images/calender.png" onclick="NewCssCal('<%=loDocPropsBean.getPropertyId()%>',event,'mmddyyyy');return false;" style="display:none"/></span>
												<span class="error"></span>
									    	</div>
										<%
								       	}}
							}
					%>

					<div class="buttonholder pad6">
						<input type="button"
							class="hiddenBlock graybtutton" name="editViewCancelButtonDoc"
							onclick="hideSaveCancelDoc()" title="Cancel" value="Cancel"
							id="editViewCancelButtonDoc" /> <input type="submit"
							class="hiddenBlock button" name="editViewSaveButtonDoc"
							value="Save" title="Save" id="editViewSaveButtonDoc" />
					</div>
				</form>
			</div>
			<%
				} else {
			%>
			<h2>You are not authorised to view this page. Please contact
				your organisation Administrator to request additional permissions.</h2>
			<%
				}
			%>
		</div>
	</div>
	<a href="javascript:void(0);" class="exit-panel upload-exit"
		title="Exit">&nbsp;</a>
</div>
<!-- End -->