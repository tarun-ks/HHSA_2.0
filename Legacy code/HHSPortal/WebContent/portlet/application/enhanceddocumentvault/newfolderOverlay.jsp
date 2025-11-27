<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<!-- Jsp Body Starts -->
<!-- New Folder Overlay -->
<form id="newfolderform" action="<portlet:actionURL/>" method ="post" name='newfolderform'>
<div class="alert-box alert-box-newFolder">
<div class="content">
<div id="newTabs">
<div class="tabularCustomHead">New Folder</div>
		
<div class='overlayWrapper formcontainer' style="padding: 12px;">
<div class="messagedivover" id="messagedivover"></div>
</br><b class="boldlayout">New Folder</b>
<hr style="border-top: dotted 1px;width:85%;" align="left" />
Enter a folder name and select the location of the new folder. 
					<div class='clear'>&nbsp;</div>
						<div class='row'>
							<span class='label'>
								<label class="required">*</label>Folder Name:
							</span>
							<span class='formfield'  style='width:44% !important'>
							<!-- Fix for Defect# 7870- Max length of folder name -->
								<input type="text" maxlength="50" name="folderName" id="folderName"/>
							<!-- Fix for Defect# 7870 end -->
								<input type="hidden" name="selectedfolderid" id ="selectedfolderid" value='' />	
								
								<input type="hidden" name="toSelectFolderId" id ="toSelectFolderId" value='' />	
								<input type="hidden" name="newFolderId" id ="newFolderId" value='<%=request.getAttribute("newFolderId")%>' />
							</span></br>
							<span class="error" id="errorId" style="width: 200px;margin-left: 180px;margin-bottom: -5px; margin-top:-5px;"></span>
						</div>
						<div id="newFolderTree" class='leftTreeOverlay' style="width:100%">
							
							
						</div>
						
					<div class="buttonholder">
				<input type="button" class="graybtutton" name="cancelButton" title="Cancel" value="   Cancel   " id="cancelButton"/>
				<input type="submit" class="button" name="createFolder" value="Create Folder" title="Create Folder" id="createFolderbutton"/>
			</div>
				 </div>
</div>

</div>
<a  href="javascript:void(0);" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
</div>
</form>

<!-- End -->