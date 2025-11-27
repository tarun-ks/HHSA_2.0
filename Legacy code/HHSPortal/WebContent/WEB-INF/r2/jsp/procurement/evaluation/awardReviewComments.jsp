<%-- This jsp displays the award review comments corresponding to the procurement Id--%>
<%-- Overlay Popup Starts   --%>
<div class='tabularContainer'>
	<div class='clear'></div> 
		<div class="formcontainer">
			<div class="row">
				  <b>HHS Accelerator : ${awardReviewDate}</b>
			</div>
			<div class='clear'></div> 
			<div class="row">
				<div class="content wordWrap">
				${awardReviewComments}
				</div>
			</div>
		</div>
</div>
<%-- Overlay Popup Ends  --%>
