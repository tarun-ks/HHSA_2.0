<%-- This jsp display for agency user for returned payment task on finishing task with CANCEL status in level 1.
It is added in Release 6.--%>
<div class="alert-box alert-box-Cancel-ReturnedPayment">
		<div class="content">
		  	<div id="newTabs" class='wizardTabs'>
				<div class="tabularCustomHead">Cancel Returned Payment
					<a href="javascript:void(0);" class="exit-panel nodelete" title="Exit"></a>
				</div>
					<h2 class='padLft' style="padding-left:10px;">Cancel Returned Payment</h2>
					<%--Css added for Firefox --%>
				<hr class="restoreHeader" style="clear: both;">					
				<div id="deleteDiv" class="linePadding">
				<div class="pad6 clear promptActionMsg" align="left">Are you sure that you want to cancel this returned payment?
					</div>
					<br/>
					<div class="buttonholder txtCenter">
						<input type="button" title="No" class="graybtutton exit-panel" id="nodeleteDoc" value="No" />
						<input type="button" title="Yes" class="redbtutton" id="cancelTask" value="Yes" onclick="cancelWorkFlow();"/>
					</div>
				</div>
			</div>
		</div>
	<a  href="javascript:void(0);" class="exit-panel nodelete" title="Exit">&nbsp;</a>
</div>