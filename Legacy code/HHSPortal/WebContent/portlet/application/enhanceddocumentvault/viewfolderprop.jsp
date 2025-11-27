<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>

<portlet:defineObjects/>

<div class="alert-box alert-box-viewFolderProperties" >
	<jsp:useBean id="document" class="com.nyc.hhs.model.Document" scope="request" ></jsp:useBean>
	<div class="content">
<div id="newTabs">
<div class="tabularCustomHead">Folder Information</div>
	
<div class='overlayWrapper formcontainer folderInfo'>
<!-- Folder Properties Overlay -->
<form id="folderSaveForm" action="<portlet:actionURL/>" method ="post" name='folderSaveForm'>
<div class="messagedivover" id="messagedivoveredit"></div>
</br><b class="boldlayout">Folder Information</b>
	<c:set var="check"><%= session.getAttribute("permissionType") %></c:set>
	<c:set var="EditVersionProp">${EditVersionProp}</c:set>
	<c:set var="user_organization">${user_organization}</c:set>
	<c:set var="jsp">${portletSessionScope.jspName}</c:set>
	<c:set var="locking">${lockingForView}</c:set>
	<!-- QC 8914 R7.2 read only role  -->
	<c:set var="role_current"><%= session.getAttribute("role_current") %></c:set>
	<c:set var="EditPropCheck">${editPropCheck}</c:set>
	<c:set var="user_organization">${user_organization}</c:set>
	<c:if test="${org_type eq 'city_org' }">
					<c:set var="user_organization">${org_type}</c:set>
					</c:if>
					
	<!-- QC 8914 R7.2 read only role : hide edit options for read only user --> 
	<c:set var="HideEditOptions" value="${false}"></c:set>
  	<%
  	if( CommonUtil.hideForOversightRole(request.getSession()) ){ %>
		<c:set var="HideEditOptions" value="${true}"></c:set>	
	<%}
	%> 		
	<c:if test="${(jsp ne 'recyclebin') and (document.userOrg eq user_organization) and check ne 'R'}">
		<c:choose>
		<c:when test="${(locking eq true) or (HideEditOptions eq true)}">
		<label class="linkEdit" style="margin-left: 45px"><label href="#"
								title="Edit Properties"
								id="editFolderInfo"  style="color:#999999">Edit Properties</label> </label>
		</c:when>
		<c:otherwise>
		<label class="linkEdit" style="margin-left: 45px"><a href="#" title="Edit Properties" 
			onclick="editFolderInfo('<%=document.getDocumentId()%>')" id="editFolderInfo">Edit Properties</a> </label>
		</c:otherwise>
		</c:choose>
	</c:if>
		<c:if test="${jsp ne 'recyclebin' and document.userOrg eq user_organization and check eq 'R'}">
			<label class="linkEdit" style="margin-left: 45px"><label href="#"
								title="Edit Properties"
								id="editFolderInfo"  style="color:#999999">Edit Properties</label> </label>
		</c:if>
					<hr class="dottedSpace" />													
							<c:choose>
							<c:when test="${(org_type eq 'agency_org' or org_type eq 'provider_org')  and (document.userOrg ne user_organization)}"></c:when>
							<c:otherwise>
							<div class='row' id='folderlocation'>
							<span class='label' >Folder Location:
							</span>
							<c:choose>
									<c:when test="${portletSessionScope.jspName eq 'recyclebin'}">
										<span class="formfield1 folderPathProp wrap-by-para folderLocationPath"><%=document.getMoveFromPath()%> </span>
									</c:when>
									<c:otherwise>
										<span class="formfield1 folderPathProp wrap-by-para folderLocationPath"><%=document.getFolderLocation()%> </span>
									</c:otherwise>
							</c:choose>							
							</div>
							</c:otherwise>
							</c:choose>
							<div class='row'>
							<span class='label'>Folder Name:
							</span>
							<span style="word-wrap:break-word;" id="editFolderName" class="formfield"><%=document.getDocName()%></span>						
							<input type="text" value="<%=document.getDocName()%>" maxlength="50" name="editFolderNameText" id="editFolderNameText"/>
							<span class="error"></span>
							</div>
							<div class='row'>
							<span class='label'>Modified By:
							</span>
							<span class="formfield"><%=document.getLastModifiedBy()%></span>
							</div>
							<div class='row'>
							<span class='label'>Modified Date:
							</span>
							<span class="formfieldTimestamp"><%=document.getDate()%></span>
							</div>
							<div class='row'>
							<span class='label'>Created By:
							</span>
							<span class="formfield"><%=document.getCreatedBy()%></span>
							</div>
							<div class='row'>
							<span class='label'>Creation Date:
							</span>
							<span class="formfieldTimestamp"><%=document.getCreatedDate()%></span>
							</div>
							<c:if test="${portletSessionScope.jspName ne 'recyclebin'}">
							<div class='row'>
							<span class='label'>Total Documents:
							</span>
							<span class="formfield"><%=document.getFolderCount()%></span>
							</div>
							</c:if>
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
							<!-- Added to get Share With list - Fix for Defect # 7493 -->
							<c:if test="${(org_type eq 'agency_org' or org_type eq 'city_org') and document.sharingOrgName ne null and document.userOrg ne user_organization}" >
							<div class='row'>
							<span class='label'>Shared With: </span> 
							<span class="formfield folderPathProp wrap-by-para folderShareWithList"><%=document.getSharingOrgName()%></span>
							</div>
							</c:if>									
							<!--Fix for Defect # 7493 end -->
			<input type="hidden" name="updatedFolderName" id ="updatedFolderName" value="${updatedFolderName}" />
			<input type="hidden" id="sharedPageOrgFolder" value=""/>	
			<div class="buttonholder buttonholder-align">			
			<input type="button" class="hiddenBlock graybtutton " name="editViewCancelButton" title="Cancel" value="Cancel" onclick="hideSaveCancelFolder()" id="editViewCancelButton" />
			<input type="submit" class="hiddenBlock button overlaybutton" name="editViewSaveButton" value="Save" title="Save" id="editViewSaveButton" />
			</div>
</form>

</div>

</div>
<a href="javascript:void(0);" class="exit-panel upload-exit"
	title="Exit">&nbsp;</a>
	
	
	</div>
	</div>
	<input type="hidden" name="orgFlag" id ="orgFlag" value="false" />
<!-- End -->