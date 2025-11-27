<!--This page will display the update legal name and accounting period overlays on the organization profile screen.-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<portlet:defineObjects />
<style>
.overlayWrapper{
	clear:both;
	padding:10px	
}
#newLegalName, #newLegalNameReason{
	display:block
}
</style>
<div class="overlay"></div>
<div class="alert-box alert-boxLegalName">
	<form action="<portlet:actionURL><portlet:param name="section" value="oraganization" /><portlet:param name="subsection" value="newLegalName" /><portlet:param name="next_action" value="showquestion" /></portlet:actionURL>" method="POST"  name="newLegalNameForm" id="newLegalNameForm" >
		<div class="content">
			<div id="newTabs">
				<div class="tabularCustomHead">Update Organization Legal Name</div> 
				<div class='overlayWrapper'>
					<b>Notice:</b> Please complete the fields below to request a name change. 
					<div class=>
						<label class="required">*</label>Indicates required fields
					</div>
					
					<div class='clear'>&nbsp;</div>
					
					<div>
						<b>Current Organization Legal Name: </b> 
						<div id="currentNameDiv"></div>
						<input type="hidden" name="currentName" id="currentName"/>
					</div>
					
					<div class='clear'>&nbsp;</div>
					
					<b><label class="required">*</label>Proposed Organization Legal Name:</b>
					<br /><input type="text" maxlength="100" name="newLegalName" id="newLegalName" size="66"/>
					
					<div class='clear'>&nbsp;</div>
					
					<b><label class="required">*</label>Reasons for updating your Organization Legal Name:</b>
					<br /><textarea name="newLegalNameReason" id="newLegalNameReason" maxlength="250" onkeyup="return ismaxlength(this)" cols="50" rows="4" style="width: 475px; height: 75px;"></textarea>
				</div>
			</div>
			<div class="buttonholder floatRht">
				<input type="button" class="graybtutton" name="cancelButton" value="Cancel" title="Cancel" id="cancelButton"/>
				<input type="submit" class="button" name="submitRequest" value="Submit Request" title="Submit Request" id="submitRequest"/>
			</div>
		</div>
	</form>
	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>
<div class="alert-box alert-boxAccountingPeriod">
	<form action="<portlet:actionURL><portlet:param name="section" value="oraganization" /><portlet:param name="subsection" value="newPeriodForm" /><portlet:param name="next_action" value="showquestion" /></portlet:actionURL>" method="POST"  name="newPeriodForm" id="newPeriodForm">
		<div class="content">
			<div id="newTabs"  class='wizardTabs'>
				<div class="tabularCustomHead">Update Accounting Period</div> 
				  <div class='overlayWrapper formcontainer'   id="currentAccountingPeriod">
				  	<b>Notice:</b> Updating your organization's accounting period will affect when HHS Accelerator will require you to update your financial documentation which may affect your status as an Approved provider. 
					<div class='clear'>&nbsp;</div>
					<div id="errorMessageToBeDisplayed">
						<c:if test='${successMessage ne "yes"}'>
							<div class="error">
								${successMessage}
							</div>
						</c:if>
					</div>
					<label class="required">*</label>Indicates required fields
					<br />
						
						<div class='row'>
							<span class='label'>
								Current accounting period:
							</span>
							<span class='formfield' style='width:30%'>
								<label id="oldFrom"></label>
								<label>To</label>
								<label id="oldTo"></label>
							</span>
						</div>
						<div class='row'>
							<span class='label'>
								<label class="required">*</label>New accounting period:
							</span>
							<span class='formfield'  style='width:30%'>
								<span id="newFrom"></span>
								<label>To</label>
								<span id="newTo"></span>
							</span>
							<span class="error"></span>
						</div>
						<input type="hidden" name="oldFromMonth" id="oldFromMonth"/>
						<input type="hidden" name="oldToMonth" id="oldToMonth"/>
						<div class='row'>
							<span class='label'>
								<label class="required">*</label>Calendar year in which this change becomes effective(YYYY):
							</span>
							<span class='formfield'  style='width:30%'>
								<span><input type="text" maxlength="4" name="effectiveYear" id="effectiveYear" size="28"/></span>
							</span>
							<span class="error"></span>
						</div>
				 </div>
			</div>
			<div class="buttonholder">
				<input type="button" class="graybtutton" name="cancelButton1" title="Cancel" value="Cancel" id="cancelButton1"/>
				<input type="submit" class="button" name="submitRequest" value="Update" title="Update" id="updateButton"/>
			</div>
		</div>
	</form>
	<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
</div>

<!--  Popup start for Address Validation -->
<div class="alert-box alert-box-address">
   <div id="newTabs">
   		<div class="tabularCustomHead">Address Validation</div>
  		<div id="addressDiv" class='evenRows'></div>
  </div>
  <a href="javascript:void(0);" class="exit-panel" title="Close">&nbsp;</a>
</div>
<!--  Popup Ends for Address Validation -->

<!--  Popup start for Confirm Override -->
<div class="alert-box alert-confirm-override">
		<div class="content">
				<div class="tabularCustomHead">Confirm Override</div> 
				<div class='tabularContainer'>
				 	 <h2>Confirm Override</h2>
	               	 <div class='hr'></div>
						<p>Are you sure you want to change the corporate structure of your organization?</p>
						<br/>
						<p>Once confirmed, documents previously associated with your organization's corporate structure 
						will no longer be linked to items in the Filings section of the Business Application.</p>
						 <br/>
						 <p>
					   		<input type="checkbox" name="accept" id="validateCheckbox">
					   		<label for='validateCheckbox'>Yes, I confirm that my corporate structure has changed.</label>
			   			</p>
					<div class="buttonholder">
						<input type="button" class="graybtutton" name="cancelButton2" value="Cancel" onclick="cancelOverLay();"/> 
						<input type="button" id="confirmOverride" name="confirmOverride"  value="Yes, I confirm that my corporate structure has changed" onclick="confirmOverride1();"/>
					</div>
				</div>
		</div>
	<a  href="javascript:cancelOverLay();" class="exit-panel">&nbsp;</a>
</div>
<!--  Popup Ends for Confirm Override -->
