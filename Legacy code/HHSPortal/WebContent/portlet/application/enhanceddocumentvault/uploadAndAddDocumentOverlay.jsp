<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/framework/skins/hhsa/css/jstree.style.min.css" type="text/css"></link>
<script type="text/javascript" src="../framework/skeletons/hhsa/js/jstree.min.js"></script> 
<%-- this div is opened when the user click on upload document button--%>
<div class="alert-box alert-box-upload">
	<div class="content">
		<div id="newTabs"  class='wizardTabs'>
			<div class="tabularCustomHead">Upload Document</div> 
			<h2 class='padLft'>Upload Document</h2>
			<div class='hr'></div>
			<ul>
					<li id='step1' class='active'>Step 1: File Selection</li>
					<li id='step2'>Step 2: Document Information</li>
					<li id='step3' class="last">Step 3: Document Location</li>
				</ul>
		       	<div id="tab1"></div>
		        <div id="tab2"></div>
		         <div id="tabnew"></div>
		</div>
	</div>
	<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- this div is opened when the user click on select document from vault  --%>
<div class="alert-box alert-box-addDocumentFromVault">
		<div class="content">
				<div class="tabularCustomHead">Select Existing Document from Document Vault</div>
				<div id="addDocumentFromVault"></div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel upload-exit" >&nbsp;</a>
</div>
<%-- this div will be opened when user select view document properties from the dropdown option --%>	
<div class="alert-box alert-box-viewDocumentProperties">
		<div class="content">
		
				<div class="tabularCustomHead">View Document Information</div>
				<div id="viewDocumentProperties"></div>
		
		</div>
		<a  href="javascript:void(0);" class="exit-panel docinfo-exit" onclick="navigateToMain();">&nbsp;</a>
</div>