<div>
	<div id="newTabs" class='wizardUploadTabs'>
		<div class="tabularCustomHead">Default Task Assignment?</div>
		<div class='padLft defaultTaskPadLeft'>Default Task Assignment?</div>
		<hr
			style="border-top: 0px solid #B2B2B2; width: 90%; margin-left: 10px; clear: both;" />
		<div class="addpadding">
			<div class='row'>
				<span class="labelDefaultAssignee">Assignment:</span> <span
					class="aligntext" id="assignTo"></span>
			</div>
			<br>

			<div class='row'>
				<span class="labelDefaultAssignee">Task Type:</span> <span
					class="aligntext" id="taskType"></span>
			</div>
			<br>
			<div class='row'>
				<span class="labelDefaultAssignee">Level:</span> <span
					class="aligntext" id="taskLevel"></span>
			</div>
			<p>Should this user remain the default user assigned to this task
				type and the level for the selected contract(s)?</p>
			<div class='row' id='checkDefaultTask'>
				<span class="labelDefaultAssignee">New Default Assignment?:</span> <input
					type="radio" name='assignment' class='checkDefaultTask' value='Yes'>Yes
				<input type='radio' name='assignment' class='checkDefaultTask'
					value='No'>No
			</div>
			<br>
			<div class='row' id="setDefaultManually">
				<span class="labelDefaultAssignee addheight">Other Options:</span> <input
					type="checkbox" name="keepCurrentDefault" value="Y"
					id="keepCurrentDefault">Keep Current default assignment<br>
				<span class="" id="dontAskSpan">
				<input type="checkbox" name="askAgain" id="askAgain"
					title="Selecting this box will prevent you from changing the default task assignment if you re-assign tasks"
					value="Y"><span>Don't ask me again.</span>
					</span>
			</div>
			<br>
		</div>
		<div class="buttonholder" style="margin-right: 10px;">
			<input type="button" title="Cancel" class="graybtutton exit-panel"
				id="restoreCancel" value=" Cancel " /> <input type="button"
				title="Confirm" class="button" id="restoreButton" value=" Confirm "
				disabled="disabled" onclick="ressignCallConfirm()" />
		</div>
	</div>

</div>
<input type="hidden" id="hiddenAskFlagOverlay" name="hiddenAskFlagOverlay" value="false">
<a href="javascript:void(0);" id="exitUpload"
	class="exit-panel upload-exit" title="Exit">&nbsp;</a>
