<%@page import="java.util.ArrayList"%>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects/>
<script type="text/javascript" src="../js/simpletreemenu.js"></script>
<style type="text/css"> 
	.tabularWrapper{
	 	height: 300px;
	 }
	.ui-widget{
		 font-family: Verdana !important;
		 font-size: 12px !important;
	}
	.alert-box-sharedoc{
	 	display: none;
	    position: fixed;
	    top: 20%;
	  
	    z-index: 1001;
	}
	.alert-box-sharedoc, .alert-box-sharedoc .bodycontainer{
		width: 72%;
		background: #fff;
	}
	#tabs-container{
		background: #fff;
		border: none !important;	
	}
	#newTabs h5,#newTabs-sharedoc h5{
	 	background:#E4E4E4;
	 	color: #5077AA;
	    font-size: 13px;
	    font-weight: bold;
	    padding: 6px 0px 6px 6px;
	}
	.alert-box-sharedoc .sub-started a{
	 	color:#fff;
	}
	.alert-box-sharedoc .sub-notstarted{
	 	background-image:none;
	 	color:#fff;
	}
	.formcontainer{
	 	position: relative;
	}
	#main-wrapper .bodycontainer br{
		display:block ;
	} 
	.nowrap{
		white-space: nowrap;
	}
	h2 input{
		color: #5077AA;
	    font-size: 17px;
	    width:50%;
	}
	.linkReturnValut a{
		position: absolute;
		right: 0;
		white-space: nowrap;
	}
	.positionInner table{
		border: none !important;
	}
	
	.confirm-taxonomy-box, .ok-taxonomy-box, .wait-taxonomy-box, .return-taxonomy-box, .goToDetail-taxonomy-box{
		background: #FFF;
		display: none;
		position: fixed;
		margin-left: 20%;
		top: 25%;
		width: 30%;
		z-index: 1001;
	}
		
	/* New changes */
	ul.wizardTabsNew{
		float:left;
	    width:100%;
	    margin:4px 0 12px 12px;
	}
	ul.wizardTabsNew li, ul.wizardTabsNew li.default, ul.wizardTabsNew li.last{
	    background: url(../images/tab_step_default_grayBg.png) no-repeat scroll right center transparent;
		float: left;
		line-height: 33px;
		margin-left: -2px;
		margin-right: 2px;
		padding: 0 22px;
		*padding: 0 20px;		
		width: auto;
	}
	:root #step1confirmDoc{
		width:auto !important;
		margin-left:0
	}
	
	ul.wizardTabsNew li, ul.wizardTabsNew li.default, ul.wizardTabsNew li.last{
		padding: 0 22px \0/IE8+9;
	}
	
	
	ul.wizardTabsNew li.default{
		z-index: 13;
		position: relative;
		margin-left: 14px;
		 background: url(../images/tab_step_default_blueBg.png) no-repeat right center transparent !important;
	}
	ul.wizardTabsNew li.active, ul.wizardTabsNew li.activeLast{
	     background:url(../images/tab_step_selected.png) no-repeat right !important;
	     font-weight:bold;
	     z-index: 12;
	     padding-left:12px;
	     position: relative;
	     right: -14px;
	}
	ul.wizardTabsNew li.activeLast{
		right:14px;
		color:#fff;
	}
	.wizardTabs ul, .wizardTabber ul {
	    float: none !important;
	    margin: 0 !important;
	    padding:0 !important;
	    width: auto !important;
	}
	#myform table{
		border: 0;
	}
	.taskButtons #recacheButonId{
		margin-bottom: 0
	}
</style>
<script type="text/javascript" src="../js/taxonomymaintenance.js"></script>
<%
	String taxonomyItemId = "";
	String taxonomyBranchId = "";
	String taxonomyElementType = "";
	String taxonomyTree = (String)renderRequest.getPortletSession().getAttribute("lsMainTree");
	String newTaxonomyItemAdded = null;
	if(renderRequest.getAttribute("newTaxonomyItemAdded")!=null){
		newTaxonomyItemAdded = (String)renderRequest.getAttribute("newTaxonomyItemAdded");
	}
	if(renderRequest.getAttribute("taxonomyItemId")!=null){
		taxonomyItemId = (String)renderRequest.getAttribute("taxonomyItemId");
		taxonomyBranchId = (String)renderRequest.getAttribute("taxonomyBranchId");
		taxonomyElementType= (String)renderRequest.getAttribute("taxonomyElementType");
	}
	
	TaxonomyTree loTaxonomyTreeBeanObj = (TaxonomyTree) request.getAttribute("TaxonomyTreeBean");
	String lsTaxonomyElementName ="";
	if(null !=loTaxonomyTreeBeanObj && null!=loTaxonomyTreeBeanObj.getMsElementName() ){
		lsTaxonomyElementName = loTaxonomyTreeBeanObj.getMsElementName();
	}

%>

<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.FQ_S091_PAGE, request.getSession())) {%>
	<form id="myform" action="<portlet:actionURL/>" method="post" name="myform">
	<input type="hidden" id="next_action" name="next_action" value="" />
	
	<div id="errorStatusDiv" class=""> </div>
	
	<% 
	String lastModifiedDateRecache = "";
	String lastModifiedByRecache = "";
	if(null!=request.getSession().getAttribute("lastModifiedDateRecache")){
		lastModifiedDateRecache = (String)request.getSession().getAttribute("lastModifiedDateRecache");
		lastModifiedByRecache = (String)request.getSession().getAttribute("lastModifiedByUserRecache");
	}
	
	String lastModifiedDateTax = "";
	String lastModifiedByTax = "";
	if(null!=request.getSession().getAttribute("lastModifiedDateTaxonomy")){
		lastModifiedDateTax = (String)request.getSession().getAttribute("lastModifiedDateTaxonomy");
		lastModifiedByTax = (String)request.getSession().getAttribute("lastModifiedByUserTaxonomy");
	}
	
	String lsTransactionMsg = "";
	if (null!=request.getAttribute("transactionMessage")){
		lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	}
	if(null!=request.getAttribute("transactionStatus") && "passed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		<div id="transactionStatusDiv" class="passed breakAll" style="display:block" ><%=lsTransactionMsg%> </div>
	<%}else if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
	     <div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
	<%}%>
		
	<%
	if(newTaxonomyItemAdded==null){
	%>
	
		<h2 id="taxonomyPageTitle">Taxonomy Maintenance</h2>
	<%} %>
	<h2 id="taxonomyElementNameId" style="display:none;">
	Taxonomy Maintenance: <span><input id="taxonomyElementName" name="taxonomyElementName" onchange="setFlag();" maxlength="200" type="text" value="<%=lsTaxonomyElementName%>" />
	</span></h2>
	<div id="returnLink" class="linkReturnValut" style='display: none;'><a href="javascript:void(0);" title="Return to Main Page" onclick="returnToMain();">Return to Main Page</a> </div>
	
	<div class=hr></div>
	<DIV class=container><!-- Developers code - should consider only this one -->
		<DIV class=maintainanceWrapper><!-- Left Column Starts here -->
		<DIV class="lftCell taxonomyLeftCell"><A class=link title="Collapse All" href="javascript:ddtreemenu.flatten('treemenu1', 'contact')">Collapse All</A> | <A class=link title="Expand All" href="javascript:ddtreemenu.flatten('treemenu1', 'expand')">Expand All</A> 
			<UL id=treemenu1 class=treeview>
			<%=taxonomyTree%>
			</UL><!-- To create Dynamic  menu tree, just call the function ddtreemenu.createTree(): -->
		
		</DIV>
		<!-- Left Column Ends here -->
		
		<!-- Right Column Starts here -->
		<%
		if(newTaxonomyItemAdded==null){
		%>
		<DIV id="detailDiv" class=rgtCell>
			<DIV class="buttonholder positionOuter">
				<DIV class="floatLft">
				<div>
					Select an item from the tree menu to the left to edit existing information.
				</div>
				<table>
					<tr>
						<td class='taxonomyTopButtons'>
						 <div class='taskButtons floatNone'> 
							<input id="shareDoc" name="shareDoc" class="add" value="Add New Taxonomy Item" title="Add New Taxonomy Item" type="button" style='color:#333; font-weight:normal;' />
							<input class="refresh"  value="Re-Cache Entire Taxonomy" title="Re-Cache Entire Taxonomy" onclick="recacheCall();" type="button" />
						 </div>
						</td>
						<td style="padding-left: 10px; font-size: 11px; margin-top:-1px"><B>Last Re-Cache of Taxonomy</B>:
						 <div id="recacheTopDiv" style="margin-top:-6px"><%=lastModifiedDateRecache%> by <%=lastModifiedByRecache%></div></td> 
					</tr>
				</table>			
				</DIV> 
			</DIV>
		</DIV>
		
		
		<%} else{%>
			<DIV id="detailDivIfTaxonomyAdded" class=rgtCell ><jsp:include page="/portlet/maintenance/taxonomymaintenance/mainttaxonomyitemdetail.jsp" /></DIV>
			<script>
			$(document).ready(function() {
			showTopAndBottom();
			});
			</script>
		<%} %>
		</DIV>
		<!-- Right Column Ends here -->
	</DIV>
		
	<div id="detailButton" style="display:none;" class="buttonholder" > 
		<table width='100%' class='clear'>
			<td>
				<b>Last Modified</b>:<span id="recacheBottomUserDiv"><%=lastModifiedDateTax %> by <%=lastModifiedByTax %> </span> <br />
	        	<b>Last Re-Cache of Taxonomy</b>: <span id="recacheBottomDateDiv"><%=lastModifiedDateRecache %> by <%=lastModifiedByRecache %> </span>
			</td>
			<td class='alignRht'>
				<span class='taskButtons floatNone' style='position: static'>
					<input type="button" id="recacheButonId" class="refresh" value="Re-Cache Entire Taxonomy" onclick="recacheCall();"></input>
				</span>
			    <input type="button" id="deleteTaxonomyButton" class="redbtutton" value="Remove this taxonomy item" onclick="javascript: deleteTaxonomyItem();" /> 
			    <input type="button" id="saveButton" class="button" value="Save Changes" onclick="javascript:setHiddenValues()"/>
			   
	        </td>
		</table>
		<div class='clear' style='height:3px;'></div>
	</div>
	<div class="overlay"></div>
	<div class="alert-box-sharedoc" style="position: absolute; width: 850px;  ">
		<div class="content">
		  	<div class=''>
				<div class="tabularCustomHead">Add New Taxonomy Item <label id="sharelabel" class="overlay-subtitle"></label></div>
				<ul id="sharewiz" class='wizardTabsNew wizardUlStep1' >
					<li style='margin-left: -17px;' id='step1confirmDoc' >Step 1:Select Taxonomy Type</li>
					<li style='padding-left: 6px;' id='step2selectOrg'>Step 2: Select Location &nbsp;&nbsp;</li>
					<li id='step3selectNycAgency'>Step 3: Name New Item &nbsp; &nbsp;</li>
					<li style='margin-left: -16px;' id='step4confirmSel'>Step 4: Confirm Selections</li>
				</ul>
		        <div id="tab3" ></div>
		        <div id="tab4"></div>
		        <div id="tab5"></div>
		        <div id="tab6"></div>
			</div>
		</div>
		<a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
	</div>	
			
	</form>	
	    <div class="deleteOverlay"></div>
		<div  class="confirm-taxonomy-box">
				<div class="content">
			  		<div id="newTabs" class=''>
						<div id = "DeleteConfirmationTitle" class="tabularCustomHead">
						</div><div id="deleteDiv">
						    <div id = "ConfirmMessagediv" class="pad6 clear promptActionMsg breakAll">
						    </div>
						    <div class="buttonholder txtCenter">
						        <input type="button" class="graybtutton  exitYesNo-panel"  id="cancelId" title="No" value="No" />
						        <input type="button" class="button" id="okId" title="Yes" onclick="proceed();" value="Yes" />
						    </div>
						    </div>
					</div>
			  	</div>
			  	<a  href="javascript:void(0);" class="exitYesNo-panel">&nbsp;</a>
		</div>
		<div  class="return-taxonomy-box">
				<div class="content">
			  		<div id="newTabs" class=''>
						<div id = "returnConfirmationTitle" class="tabularCustomHead">
						</div>
						<div id="deleteDiv">
						    <div id = "returnMessagediv" class="pad6 clear promptActionMsg">
						    </div>
						    <div class="buttonholder txtCenter">
						        <input type="button" class="graybtutton  exitYesNo-panel"  title="Cancel" value="Cancel" />
						        <input type="button" class="button" id="deleteDoc" title="OK" onclick="returnSuccess();" value="OK" />
						    </div>
						 </div>
					</div>
			  	</div>
			  	<a  href="javascript:void(0);" class="exitYesNo-panel">&nbsp;</a>
		</div>
		<div  class="ok-taxonomy-box">
				<div class="content">
			  		<div id="newTabs" class=''>
						<div id = "DeleteTitle" class="tabularCustomHead"> </div>
						<div id="deleteDiv">
							<div id = "removeMessagediv" class="pad6 clear promptActionMsg"> </div>
							<div class="buttonholder txtCenter">
								<input type="button" class="button exitYesNo-panel"  title="OK" value="OK" />
							</div>
						</div>
					</div>
			  	</div>
			  	<a  href="javascript:void(0);" class="exitYesNo-panel">&nbsp;</a>
		</div>
		<div  class="wait-taxonomy-box">
				<div class="content">
			  		<div id="newTabs" class=''>
						<div id = "waitTitle" class="tabularCustomHead">Re-Caching…</div>
						<div id="deleteDiv">
							<div id = "removeMessagediv" class="pad6 clear promptActionMsg" style="text-align: center"> The system is currently re-caching the entire taxonomy.<br/><img src='../framework/skins/hhsa/images/loading.gif' /><br/>This may take several minutes</div>
						</div>
					</div>
			  	</div>
		</div>
	<div  class="goToDetail-taxonomy-box">
				<div class="content">
			  		<div id="newTabs" class=''>
						<div id = "changeConfirmationTitle" class="tabularCustomHead">
						</div>
						<div id="deleteDiv">
						    <div id = "changeMessagediv" class="pad6 clear promptActionMsg">
						    </div>
						    <div class="buttonholder txtCenter">
						        <input type="button" class="graybtutton  exitYesNo-panel"  title="Cancel" value="Cancel" />
						        <input type="button" class="button" id="navigateToDetail" title="OK"  value="OK" onclick="callShowList();" />
						    </div>
						 </div>
					</div>
			  	</div>
			  	<a  href="javascript:void(0);" class="exitYesNo-panel">&nbsp;</a>
		</div>
	
		<div id="overlayedJSPContent" style="visibility:hidden"></div>
		  <%}else{ %>
 	<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
 	<%} %>
 	<SCRIPT type=text/javascript>  
 	var contextPathVariable = "<%=request.getContextPath()%>";
	var taxonomyTypeRadio = "";
    var locationValue = "";
    var branchValue="";
    var newItemValue = "";	
    var overLayFlag = false;
    var detailPageElementId = "";
    var linkageElementId = "";
	var linkageBranchValue="";
	var locationPath = "";
	var formAction = "";
	var savedBranchId = ''; 
    var savedElementId = '';
    var changeFlag=false;
    var elementType = "";
    var saveElementType = "";
    var recache = false;
    var taxonomyName = "";
    var noChangeElementId="";
    var noChangeBranchid = "";
    var noChangeType = "";
    function selectAllAndSubmit() {
	document.alertform.next_action.value="showpage";	
	document.alertform.submit();
}

//jquery ready function- executes the script after page loading
$(document).ready(function() {

	//creates tree structure
    ddtreemenu.createTree("treemenu1", true);
	var pageW = $(document).width();
	var pageH = $(document).height();
    formAction = document.forms[0].action;
    savedBranchId = '<%=taxonomyBranchId%>'; 
    savedElementId = '<%=taxonomyItemId%>'; 
    saveElementType = '<%=taxonomyElementType%>';
    if(savedElementId!=""){
    }else{
	    if(document.getElementById("recacheButonId")!=null){
		    document.getElementById("recacheButonId").disabled=true;
		    document.getElementById("deleteTaxonomyButton").disabled=true;
		    document.getElementById("saveButton").disabled=true;
	    }
    }
    if (""!=savedBranchId){
    	$("#treemenu1 li[id="+savedElementId+"]>span").css({'background':'#81B5DC'});
	   	fillBreadCrumb(savedBranchId);
    }
    setChkBoxVisibility();
    if(document.getElementById("chkEvidance") != null){
    	setChkBoxState();
    }
    
        
    //Below function is called when user clicks on add new taxonomy    
	$('#shareDoc').click(function() {
		hideErrorStatusDiv();
		overLayFlag = true;
		changeFlag = false;
	 	pageGreyOut();
     	taxonomyTypeRadio = "";
        locationValue = "";
        newItemValue = "";	
   		shareDocument(this.form);
   		var pageW = $(document).width();
   		var pageH = $(document).height();
   		$(".alert-box-sharedoc").show();
   		$(".overlay").show();
		$(".overlay").width(pageW);
	    $(".overlay").height(pageH);
		 var options = 
   		  {	
		   	success: function(responseText, statusText, xhr ) 
			{
				$("#tab3").empty();
			    $("#tab4").empty();
			    $("#tab5").empty();
			    $("#tab6").empty();
			  	$("#tab3").html(responseText);
				$("#sharelabel").html("");
			
				//$.unblockUI();
				removePageGreyOut();
				},
			error:function (xhr, ajaxOptions, thrownError)
			{                     
				showErrorMessagePopup();
				removePageGreyOut();
			}
	      };
	$(this.form).ajaxSubmit(options);
	return false;
					
	});	

	$("a.exit-panel").click(function(){
	    recache= false;	
		$(".alert-box-sharedoc").hide();
	    $(".overlay").hide();
	    deleteSaveValues();
	});

	$(".exitYesNo-panel").click(function(){	
	     recache= false;
		$(".confirm-taxonomy-box").hide();
		$(".ok-taxonomy-box").hide();
		$(".return-taxonomy-box").hide();
		$(".goToDetail-taxonomy-box").hide();
		$(".deleteOverlay").hide();
	});	
});	



</script>