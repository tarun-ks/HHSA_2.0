<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects/>
<script type="text/javascript" >
	var suggestionVal ="";
	var orgFormAction;
	var isValid = false;
	$(document).ready(function(){
		orgFormAction = document.myOrgform.action;
		var inputBox = $("#provName");
		$("#dropdownul li").unbind("click").unbind("mouseover").unbind("mouseout").click(function(e){
			$("#providerId").val($(this).attr('id'));
			inputBox.val($(this).text());
			enableDisableSelectButton();
			$("#optionsBox").hide();
			e.stopPropagation();
		}).mouseover(function(){
			$("#dropdownul li").removeClass("selectLiCombo");
			$(this).addClass("selectLiCombo");
		}).mouseout(function(){
			$(this).removeClass("selectLiCombo");
		});
		
		$("#combotable_button").unbind("click").click(function(e){
			$("#optionsBox").toggle();
			e.stopPropagation();
		});
		$(document).click(function(){
			$("#optionsBox").hide();
			$('#file_menu .fileoptions1').hide();
		});
		document.getElementById("SubmitButton").disabled=true;
		$(".messageDiv").hide();
		$('#provName').keyup(function()
		{ 
			var providersName = $.trim($('#provName').val());
			if (suggestionVal.length > 0)
			{
				isValid = isAutoSuggestMatch(providersName, suggestionVal);
	  			$(".messageDiv").hide();
				$(".messageDiv").html("");
	  			  if(isValid){
	  				document.myOrgform.action = orgFormAction+"&submit_action=manageOrganization&agencyProviderLookUp=true";
	  			  }else{
	  				document.myOrgform.action = "";
	     			document.getElementById("SubmitButton").disabled=true;
	   			}
			}
  		});
    	var onAutocompleteSelect = function(value, data) {
    		$(".messageDiv").hide();
			$(".messageDiv").html("");
      		document.getElementById("providerId").value=data;
      		document.myOrgform.action = orgFormAction+"&submit_action=manageOrganization&agencyProviderLookUp=true"; 
       		document.getElementById("SubmitButton").disabled=false;
       		
       		isValid = true;
    	};
    	var options = {
	      	serviceUrl: $("#contextPath").val()+'/AutoCompleteServlet.jsp?isProvider=false',
	        width: 365,
		    minChars:3,
		    maxHeight:100,
		    onSelect: onAutocompleteSelect,
		    clearCache: true,
		    deferRequestBy: 0, //miliseconds
	        params: { city: $("#provName").val() }
        };
    	$('#provName').autocomplete(options);
   		$("#SubmitButton").click(function() {
			var providerId = $('#providerId').val();
			var provName= $("#provName").val();
			$('#sharedPageOrg').val(providerId);
			pageGreyOut();
			
			document.myOrgform.action = orgFormAction+'&submit_action=manageOrganization&providerId='+providerId;
	     
			  var options1 = {
						success : function(responseText, statusText, xhr) {
							var response = new String(responseText);
							var responses = response.split("|");
							if(!(responses[1] == "Error" || responses[1] == "Exception"))
							{
								$("#searchDoc,#recyclebindiv,#findOrgDocbtn, .hideShow, #downloadAll").hide();
								$("#findOrgDocbtn").show();
								$(".overlay").closeOverlay();
								$('#OrgNameHeader').show();
								$('#removeList').show();
								if(provName.length > 25)
								{
									$('#OrgNameHeader').text(provName.substring(0, 25)+'...');
								}
								else
								{
									$('#OrgNameHeader').text(provName);
								}
								$('#selectOrgnization').show();
								$js('#selectOrgnization').jstree("destroy");	
								tree($('#hdnopenTreeAjaxVar').val(), 'selectOrgnization', 'selectedfolderid', '', $('#providerId').val());
								$(".overlay").closeOverlay();
							}
						},
						error : function(xhr, ajaxOptions, thrownError) {
							showErrorMessagePopup();
							removePageGreyOut();
						}
					};
			$(document.myOrgform).ajaxSubmit(options1);
		});
	
	});
	//The method is added in R5 for exit button in select organization overlay- fix for defect 8357.
	function clickExit(){
		$('.autocomplete').hide();
		close();
		checkOpacity();
	}
	// This will execute during type head search for provider
	function isAutoSuggestMatch(variableName, suggestionVal) {
		var uoValid = false;
		if (suggestionVal.length > 0) {
			for (var i = 0; i < suggestionVal.length; i++) {
				var arrVal = suggestionVal[i];
				if (arrVal == variableName) {
					uoValid = true;
					break;
				}
			}
		}
		return uoValid;
	}
	
	function enableDisableSelectButton()
	{
		if(null != $('#provName').val())
			document.getElementById("SubmitButton").disabled=false;
		else
			document.getElementById("SubmitButton").disabled=true;
	}
	
	function selOrg(){
		 document.getElementById("SubmitButton").click();
	}
</script>

<div id="overlayDiv">
<!-- Upload Overlay -->
	<div class="alert-box alert-box-upload">
		<div class="content">
			<div id="newTabs"  class='wizardTabs wizardUploadTabs-align'>
				<div class="tabularCustomHead">Upload Document</div> 
				<h2 class='padLft'>Upload Document</h2>
				<div class='hr'></div>
				<ul>
					<li id='step1' class='active' style='margin-left:10px;'>Step 1: File Selection</li>
					<li id='step2' style="padding:0 16px;">Step 2: Document Information</li>
					<li id='step3' class="last" style="padding:0 10px;">Step 3: Document Location</li>
				</ul>
		       	<div id="tab1"></div>
		        <div id="tab2"></div>
		        <div id="tabnew"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" id="exitUpload" class="exit-panel upload-exit" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- ShareDoc Overlay -->
	<div class="alert-box-sharedoc">
		<div class="content">
			<div class='wizardTabber'>
				<div class="tabularCustomHead">Share Document(s)<label id="sharelabel" class="overlay-subtitle"></label></div>
				<ul id="sharewiz" class='wizardUlStep2' >
					<li id='step1confirmDoc' >Step 1:Confirm Documents</li>
					<li id='step2selectOrg' class='step2selectOrg-align'>Step 2:Select NYC Agencies</li>
					<li id='step3selectNycAgency'>Step 3:Select Organizations</li>
					<li id='step4confirmSel'>Step 4:Confirm Selections</li>
				</ul>
		        <div id="tab3"></div>
		        <div id="tab4"></div>
		        <div id="tab5"></div>
		        <div id="tab6"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" id="exitShare" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	
	<!-- UnShareAll Overlay -->
	<div class="alert-box-unshareall">
		<div class="content">
			<div id="newTabs">
				<div class="tabularCustomHead">Remove All Access</div>
				<div id="unshareall"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- UnshareByProvider Overlay-->
	<div class="alert-box-unsharebyprovider">
		<div class="content">
			<div id="newTabs">
				<div class="tabularCustomHead">Remove Access By Organization</div>
				<div id="unshareprovider"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- Remove Selected Rows -->
		<div class="alert-box-removeselectedprovs">
		<div class="content">
		  	<div id="newTabs">
				<div class="tabularCustomHead"><label id="removeprovlabel" class="overlay-subtitle"></label></div>
		        <div id="displayshared"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
<!-- Help Overlay -->
	<div class="alert-box-help">
		<div class="content">
			<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Document Vault - Help Documents</div>
		        <div id="helpPageDiv"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel" title="Exit">&nbsp;</a>
	</div>
 	<!-- End -->
	<!-- Restore Overlay --><div class="alert-box alert-box-restore">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Restore
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
				</br><b class="boldclass" style="padding-left: 20px;">Restore</b>
				<hr class="restoreHeader">
				<div id="deleteDiv" style="padding: 0px 10px;">
					<div class="pad6 clear promptActionMsg">Are you sure you want to restore the selected document(s) or folder(s)?</br></br>
					<!-- Fix for Defect 8029 Enhancement Request-->
					Please note that any restored documents that match a document in the Document Vault with the same document name and document type will be automatically renamed.
					<!-- Fix for Defect 8029 Enhancement Request end-->
					</div>
					<div class="buttonholder txtCenter" style="margin-top: -5px">
						<input type="button" title="Cancel" class="graybtutton exit-panel" id="restoreCancel" value="    Cancel    " />
						<input type="button" title="Yes,Restore" class="button" id="restoreButton" value="Yes, Restore" />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- Delete Overlay -->
	<div class="alert-box alert-box-delete">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Delete
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
				<div id="deleteDiv" class="linePadding">
				<b class="boldclass"><br>Delete</b>
				<hr class="restoreHeader" align="left" />
				<!-- Fix for Defect 8026 -->
					<div class="pad6 clear promptActionMsg">Are you sure you want to move the selected document(s) and/or folder(s) to the Recycle Bin?
				<!-- Fix for Defect 8026 end -->	
					</div>
					<div id ="redMessage" style="padding: 0px 10px;">
<p>You have selected at least 1 document or folder that is currently being shared with another organization.</p>
<p>Please note that any sharing privileges on the selected document(s) or folders will be deleted permanently and cannot be restored.</p>
</div>
					<div class="buttonholder txtCenter pad6">
						<input type="button" title="Cancel" class="graybtutton exit-panel" id="nodeleteDoc" value="   Cancel   " />
						<input type="button" title="Yes,Delete" class="redbtutton" id="deleteDoc" value=" Yes, Delete " />
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- Warning Overlay -->
	<div class="alert-box-warning">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
					<div class="tabularCustomHead">Remove Document from Document Vault
						<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
					</div>
					<div id="deleteDiv1">
					    <div class="pad6 clear promptActionMsg">This document is linked to one or more Draft Proposals. <BR/>
													    Continue if you would like to remove this document from the Document Vault
													    and any Draft Proposals it is linked to.</div>
					    <div class="buttonholder txtCenter">
							<div class="buttonholder txtCenter">
								<input type="button" title="No" class="graybtutton exit-panel" id="nodeleteDoc1" value="No" />
								<input type="button" title="Yes" class="button" id="deleteDoc1" value="Yes" />
							</div>
					    </div>
					</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- Move Overlay -->
	<div class="alert-box alert-box-move">
<div class="content">
<div id="newTabs">
<div class="tabularCustomHead">Move</div>

<div class='overlayWrapper formcontainer'>
<div class="messagedivover error failed message-move" id="messagedivovermove" ></div>
</br>
<b class="boldclass">Move</b>
<hr class="restoreHeader" align="left" />
Select the folder location you would like to move the selected documents or folders:
<div class='clear'>&nbsp;</div>
<div id="moveTree" class='leftTreeOverlay widthClass'></div>
<p style="color:red;" class="widthClass">If you are moving the documents to a folder that it currently being shared, the selected files and folders will inherit the sharing rights.</p>
<div class="buttonholder" style="margin-right: 20px;"><input type="button" class="graybtutton"
	name="cancelButton" title="Cancel" value="  Cancel  " id="cancelButton" />
<input type="button" class="button" name="move"
	value="   Move   " title="Move" id="movebutton" />
</div>
</div>
</div>
</div>
<a href="javascript:void(0);" class="exit-panel upload-exit"
	title="Exit">&nbsp;</a></div>
	<!-- End -->
	<!-- Contact Overlay -->
	<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
				<div id="contactDiv"></div>
				</div>
			</div>
		</div>
		<!-- End -->

<!-- Share Doc Status overlay -->
<!-- End -->

<!-- Linkage Overlay -->
<div class="alert-box alert-box-linkDocStatus" style="height:300px;display: none;">
	<div class="content">
		<div id="newTabs">
			<div class="tabularCustomHead" >Document Linkages</div>
            <!-- QC 8914 R7.2 read only role  -->
            <input type="hidden" id="role_current" name = "role_current" value="<%= session.getAttribute("role_current") %>"/>
            
			<div class='overlayWrapper formcontainer' style="padding-right:12px;">
				<div class="messagedivover" id="messagedivover"></div>
				</br>
				<b class="boldclass addborder"
					>Document Linkages </b>
					</br></br>
				<div class="linkageTableParentDiv" style="height:180px;overflow-y:auto" >
				
				<st:table objectName="fetchedData" cssClass="heading linkageTable"
					alternateCss1="evenRows" alternateCss2="oddRows" >
					<st:property headingName="Date" columnName="date"
						align="center" size="20%">
						<st:extension
							decoratorClass="com.nyc.hhs.frameworks.grid.LinkageGrid" />
					</st:property>
					
					<st:property headingName="Entity" columnName="entityLinked"
						align="center" size="80%">
						<st:extension
							decoratorClass="com.nyc.hhs.frameworks.grid.LinkageGrid" />
					</st:property>
				</st:table>
				</div>

				
			</div>
			<div class="buttonholder" style="padding-right:12px;">
				<input class="button graybtutton" type="button" id="linkedCancelButton"  name="cancelButton"
					title="Close" value="Close"  />

			</div>
		</div>
	</div>
	<a href="javascript:void(0);" class="exit-panel upload-exit"
		title="Exit">&nbsp;</a>
</div>
<!-- End -->
<!-- R5 release select organization Grid -->
<!-- Select Organization -->
<div class="alert-box overLay1" style="display: none;border:1px solid grey">
<form id="myOrgform" name="myOrgform" action="<portlet:actionURL/>" method="post" onsubmit="selOrg(); return false;">
<input type="hidden" name="contextPath" id="contextPath" value="${pageContext.servletContext.contextPath}" />
<input type="hidden" value="" id="searchTextValue" name="searchTextValue"/>
<input type="hidden" id="role_current" name = "role_current" value="<%= session.getAttribute("role_current") %>"/>
<div class="tabularCustomHead">Select Organization</div>
<div style="padding-left:6px;padding-right:12px;">
</br><b class="boldclass">Select Organization</b>
<hr>
<p style="padding-left: inherit;">Use the type-ahead field to select the organization to<br> view their documents.</p>
<%--Combo Box code comment --%>
<table cellspacing="0" cellpadding="0" border="0" class="ddcombo_table" id="combotable"><tbody>
							<tr>
								<td class="ddcombo_td1">
									<div class="ddcombo_div4" style="background: url(&quot;../framework/skins/hhsa/images/transparent_pixel.gif&quot;) repeat scroll 0% 0% transparent;">
										<!-- Changed Maxlength to 50 -Defect #7809 -->
										<input type="text" style="width:365px;height:20px;";class="input" name="provName" maxlength="50" onkeypress="if (this.value.length > 50) { return false; }" id="provName"/>
										<input type="hidden" name="providerId" id ="providerId">
										<div class="error messageDiv"></div>
									</div>
								</td>
								<!-- <td valign="top" align="left" class="ddcombo_td2" id="combotable_button"><a></a><img src="../framework/skins/hhsa/images/button2.png" style="display: none;"></td> -->
							</tr></tbody>
						</table>
<div style="display:none;margin-left:5px;margin-right:5px;border: 0px solid black;
				background-color: white;
				overflow: hidden;
				z-index: 99999;" id="optionsBox">
						<ul id= "dropdownul" style="max-height: 180px; overflow: auto;">
							
						       <c:forEach items="${dropDownList}" var="entry">
						        <li class="ddcombo_event data" id='${entry.hiddenValue}'>${entry.displayValue}</li>
						    </c:forEach>
						</ul>
					</div>

</br></br>     
<div class="buttonholder pd6">
	<input type="button" class="graybtutton" name="cancelButton"  id ="cancelOrgButton" value="Cancel" />
	<input type="button" class="button" name="SubmitButton" id="SubmitButton" value="Select"/>
</div>
</div>
<a href="javascript:void(0);" class="exit-panel" title="Exit" onclick="clickExit();">&nbsp;</a> 

</form>
</div>

			<!-- End -->
			<!-- End -->
						<!-- Delete Forever Overlay -->
	<div class="alert-box alert-box-delete-forever">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Delete Forever
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
					<h2 class='padLft'>Delete Forever</h2>
					<hr class="restoreHeader">	
					
				<div id="deleteDiv" class="linePadding">
					<div class="pad6 clear promptActionMsg">Please be aware permanently deleted documents cannot be restored at any point.
					<br><br>Are you sure you would like to delete the selected document or folder forever?
					</div>
					
					<div class="buttonholder txtCenter">
						<input type="button" title="Cancel" class="graybtutton exit-panel" id="nodeleteDoc" value="   Cancel   " />
						<input type="button" title="Yes,Delete" class="redbtutton" id="deleteForeverDoc" value="Yes, Delete Forever" onclick="deleteEntity()"/>
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
	<!-- Empty RecycleBin Overlay -->
	<div class="alert-box alert-box-Empty-RecycleBin">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Empty Recycle Bin
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
					<h2 class='padLft' style="padding-left:10px;">Empty Recycle Bin</h2>
				<hr class="restoreHeader">					
				<div id="deleteDiv" class="linePadding">
				<div class="pad6 clear promptActionMsg" align="left">Are you sure you would like to empty your organization's recycle bin?
					<br><br>Emptying the Recycle Bin permanently deletes documents from your Document Vault. Permanently deleted documents cannot be restored.
					</div>
					
					<div class="buttonholder txtCenter">
						<input type="button" title="Cancel" class="graybtutton exit-panel" id="nodeleteDoc" value="   Cancel   " />
						<input type="button" title="Yes,Delete" class="redbtutton" id="deleteForeverDoc" value="Yes, Empty Recycle Bin" onclick="emptyRecycleBin()"/>
					</div>
				</div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
	</div>
	<!-- End -->
</div>