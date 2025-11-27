<%-- This jsp shows the comments that were being given while marking a proposal selected or not selected--%>
<%-- Overlay Popup Starts --%>
<div class='tabularContainer'>
	<p>Below are the comments for the following Proposal:</p>
	<div class='clear'></div> 
		<%-- Form Container Starts --%>
		<div class="formcontainer">
			<div class="row">
				  <span class="label"><label for='orgName'>Provider Name:</label></span>
				  <span class="formfield">${proposalDetails.organizationName}</span>
			</div>
			<div class="row">
				  <span class="label"><label for='proposalTitle'>Proposal Title:</label></span>
				  <span class="formfield">${proposalDetails.proposalTitle}</span>
			</div>
			<div class="row">
				  <span class="label" style='height:100px'><label for='txtEnterComments' style='height:100px'>Comments:</label></span>
				  <span class="formfield">${proposalDetails.comments}</span>
			</div>
		</div>
		<%-- Form Container Ends --%>
</div>
  <%-- Overlay Popup Ends --%>
