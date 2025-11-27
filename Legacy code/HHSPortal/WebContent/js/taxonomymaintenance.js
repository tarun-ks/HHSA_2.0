//***This js file is used for Taxonomy maintenance pages***

//Below function is called to populate values in breadcrum (taxonomy with path)
function fillBreadCrumb(savedBranchId){
 	var path = getLocationPath(savedBranchId);
	var breadCrumbPath = getBreadcrumbPath(path);
	document.getElementById("breadcrumbId").innerHTML = breadCrumbPath;
}

//Below function gets the path for taxonomy(Breadcrum)
function getBreadcrumbPath(path) {
	var breadcrumbPath= "";
	var arr = path.split(">");
	var lengthLimit=arr.length-1;
	
	for(var i =0; i<arr.length;i++){
	   if (i==lengthLimit){
          breadcrumbPath = breadcrumbPath+ "<li style='color:#000000' >"+arr[i]+"</li>";
	   }
	   else{	
	      breadcrumbPath = breadcrumbPath+ "<li><a href='JavaScript:void(0);'>"+arr[i]+"</a></li>"+ "<li>&gt;</li>";
       }
	}
    return breadcrumbPath;
}

//Below function calls the details page when user clicks on taxonomy in left tree
function showDetailPage(elementId){

	pageGreyOut();
	document.forms[0].action=formAction+'&removeNavigator=true&removeMenu=true&next_action=detailPage&elementId='+elementId;
	var options = 
	  {	
	   	success: function(responseText, statusText, xhr ) 
		{
		showTopAndBottom();
		if(document.getElementById("detailDiv")==null){
		  document.getElementById("detailDivIfTaxonomyAdded").innerHTML=responseText;
		}else{
		  document.getElementById("detailDiv").innerHTML=responseText;
		}
		ddtreemenu.createTree("treemenu3", true);
		var nameStatus = checkTaxonomy(savedBranchId);
		if(nameStatus){
			document.getElementById("taxonomyElementName").readOnly=true;
		}else{
			document.getElementById("taxonomyElementName").readOnly=false;
		}
		setChkBoxState();

		changeFlag=false;
		document.getElementById("deleteTaxonomyButton").disabled=false;
        document.getElementById("saveButton").disabled=false;
        if(document.getElementById("recacheButonId")!=null){
        	document.getElementById("recacheButonId").disabled=false;
        }
        getLinkagePathfromSpan();
		fillBreadCrumb(branchValue);
	    setChkBoxVisibility();
		removePageGreyOut();
		},
		error:function (xhr, ajaxOptions, thrownError)
		{                     
			showErrorMessagePopup();
			document.getElementById("taxonomyElementNameId").style.display="none";
		    document.getElementById("returnLink").style.display="none";
		    document.getElementById("detailButton").style.display="none";
			//$.unblockUI();
			removePageGreyOut();
		}
     };
   $(document.forms[0]).ajaxSubmit(options);
}

//Below function builds form path with next action								
function shareDocument(form){
	form.action = formAction+'&removeNavigator=true&removeMenu=true&next_action=selectTaxonomy';
}	

//Below function if the the display of return and taxonomy name at the top of screen
function showTopAndBottom(){
	document.getElementById("taxonomyElementNameId").style.display="block";
	document.getElementById("returnLink").style.display="inline";
	document.getElementById("detailButton").style.display="inline";
}

//Below function initializes/deletes saved values
function deleteSaveValues(){
	overLayFlag  = false;
  	taxonomyTypeRadio = "";
  	locationValue = "";
  	newItemValue = "";
 	branchValue = "";
  	locationPath = "";
  	elementType = "";
  	recache = false;
  	$(".alert-box-sharedoc").hide();
    $(".overlay").hide();
    $("#overlayedJSPContent").html("");
	removePageGreyOut();
  	returnSuccess();
    return false;	
}	

//Below function is called from xslt when user clicks on the left side of tree
function showList(elementId,branchid,type){
	if (changeFlag){
	     noChangeElementId=elementId;
	     noChangeBranchid = branchid;
	     noChangeType = type;
	    var pageW = $(document).width();
	    var pageH = $(document).height();
		$("#changeConfirmationTitle").html("Return Confirmation");
		$("#changeMessagediv").html("You have unsaved data. If you would like to leave this screen without saving your data, click OK. If you would like to save your data, click Cancel and save your data.");
		$(".goToDetail-taxonomy-box").show();
		$(".deleteOverlay").show();
		$(".deleteOverlay").width(pageW);
	    $(".deleteOverlay").height(pageH);
	}else{
	changeFlag = false;
    $("li>span").css("background","white");
    $("li[id="+elementId+"]>span").css("background","#81B5DC");
	hideErrorStatusDiv();
	if (document.getElementById("transactionStatusDiv")!=null){
		document.getElementById("transactionStatusDiv").style.display="none";
	}    
	if (overLayFlag){
		locationValue = elementId;
		branchValue=branchid;
		elementType = type;
		locationPath = getLocationPath(branchid);
	}else {
	    branchValue=branchid;
		detailPageElementId = elementId;
		savedBranchId = branchValue; 
        savedElementId = detailPageElementId; 
        saveElementType = type;
        elementType = type;
		document.getElementById("taxonomyElementName").value = document.getElementById(detailPageElementId).title;
		if(document.getElementById("taxonomyPageTitle")!=null){
			document.getElementById("taxonomyPageTitle").style.display="none";
		}
		var nameStatus = checkTaxonomy(branchValue);
		if (nameStatus){
			document.getElementById("taxonomyElementName").readOnly=true;
		}else{
			document.getElementById("taxonomyElementName").readOnly=false;
		}
		showDetailPage(detailPageElementId);
	}
	}
}

//Below function highlights the taxonomy in linkage
function showLinkageTaxonomy(elementId,branchid){
     $("#treemenu3 li>span").css({'background':'white'});
     $("#treemenu3 li[id="+elementId+"]>span").css({'background':'#81B5DC'});
	 linkageElementId = elementId;
	 linkageBranchValue = branchid;
}

//Below function checks taxonmy whether it is amongst the top level (population, language,etc)
function checkTaxonomy(branchValue){
	var status = false;
	var arr = branchValue.split(",");
	if (arr.length==2){
		status = true;
	}
	return status;
}

//Below function builds the current taxonomy path from parent, separated by '>'
function getLocationPath(branchid){
	var path= "";
	var arr = branchid.split(",");
	for(var i=0; i<arr.length-1; i++){
	path=path+(document.getElementById(arr[i]).title)+">";
	}
	path=path.replace(/.$/, '');
    return path;
}

//Below function is called when user clicks on remove taxonomy button
function deleteTaxonomyItem(){
	if (detailPageElementId==""){
		detailPageElementId=savedElementId;
	}
	if (detailPageElementId==""){
		return false;
	}
	if ($("#"+detailPageElementId+" li").size() > 0){
		cannotRemove();             
	}
	else{
	    var deleteTaxonomyName = document.getElementById(detailPageElementId).title;
		document.myform.action=formAction+'&removeNavigator=true&elementId='+detailPageElementId+'&deleteBranchId='+branchValue+'&deleteTaxonomyName='+deleteTaxonomyName+'&next_action=removeTaxonomyItem';
		displayConfirmation();
	}
}

//Below function asks for confirmation from user before deleting any taxonomy
function displayConfirmation(){
    var pageW = $(document).width();
    var pageH = $(document).height();
	$("#DeleteConfirmationTitle").html("Delete Taxonomy Confirmation");
	$("#ConfirmMessagediv").html("Are you sure you want to remove "+document.getElementById(detailPageElementId).title+" ?");
	document.getElementById("cancelId").title="Cancel";
	document.getElementById("okId").title="Confirm";
	document.getElementById("cancelId").value="Cancel";
	document.getElementById("okId").value="Confirm";
	$(".confirm-taxonomy-box").show();
	$(".deleteOverlay").show();
	$(".deleteOverlay").width(pageW);
    $(".deleteOverlay").height(pageH);
}

//Below function prompts the user that the selected taxonomy cannot be deleted, if children are present for the selected taxonomy
function cannotRemove(){
    var pageW = $(document).width();
    var pageH = $(document).height();
	$("#DeleteTitle").html("Delete Taxonomy Promt");
	$("#removeMessagediv").html("You cannot remove this taxonomy item. This service contains children items that must be removed first.");
	$(".ok-taxonomy-box").show();
	$(".deleteOverlay").show();
	$(".deleteOverlay").width(pageW);
    $(".deleteOverlay").height(pageH);
}

//Below function is called when user clicks on recache button
function recache(){
    var pageW = $(document).width();
    var pageH = $(document).height();
    document.myform.action=formAction+'&removeNavigator=true&elementId='+savedElementId+'&saveBranchId='+savedBranchId+'&next_action=recache&removeMenu=true';
	$("#DeleteConfirmationTitle").html("re-cache Confirmation");
	$("#ConfirmMessagediv").html("Are you sure you want to re-cache the entire taxonomy?");
	document.getElementById("cancelId").title="No";
	document.getElementById("okId").title="Yes";
	document.getElementById("cancelId").value="No";
	document.getElementById("okId").value="Yes";
	$(".confirm-taxonomy-box").show();
	$(".deleteOverlay").show();
	$(".deleteOverlay").width(pageW);
    $(".deleteOverlay").height(pageH);
}

//Below function bring the user to main screen, prompting if any unsaved changes are left
function returnToMain(){
	if (changeFlag){
	    var pageW = $(document).width();
	    var pageH = $(document).height();
		$("#returnConfirmationTitle").html("Return Confirmation");
		$("#returnMessagediv").html("You have unsaved data. If you would like to leave this screen without saving your data, click OK. If you would like to save your data, click Cancel and save your data.");
		$(".return-taxonomy-box").show();
		$(".deleteOverlay").show();
		$(".deleteOverlay").width(pageW);
	    $(".deleteOverlay").height(pageH);
	}else{
		returnSuccess();
	}
}

//Below function is called when user choose to recache entire taxonomy
function proceed(){
	if(recache){
		recacheSubmit();
	}else{
	document.myform.submit();
	}
  }
  
//Below function takes user to taxonomy main page  
function returnSuccess(){
    var url =contextPathVariable+"/portal/hhsweb.portal?_nfpb=true&_pageLabel=portlet_hhsweb_portal_page_maintaintaxonomy&_nfls=false&removeNavigator=true&navigatefrom=landing";
    location.href= url;
  }

//Below function setflags for transaction status and message  
function setFlag(){
	changeFlag=true;
	hideErrorStatusDiv();
	if (document.getElementById("transactionStatusDiv")!=null){
		document.getElementById("transactionStatusDiv").style.display="none";
	}
}

//Below function is called when user clicks on recache button and pop ups for the confirmation
function recacheCall(){
	recache = true;
    var pageW = $(document).width();
    var pageH = $(document).height();
    document.myform.action=formAction+'&removeNavigator=true&elementId='+savedElementId+'&saveBranchId='+savedBranchId+'&next_action=recache&removeMenu=true';
	$("#DeleteConfirmationTitle").html("re-cache Confirmation");
	$("#ConfirmMessagediv").html("Are you sure you want to re-cache the entire taxonomy ?");
	document.getElementById("cancelId").title="No";
	document.getElementById("okId").title="Yes";
	document.getElementById("cancelId").value="No";
	document.getElementById("okId").value="Yes";
	$(".confirm-taxonomy-box").show();
	$(".deleteOverlay").show();
	$(".deleteOverlay").width(pageW);
    $(".deleteOverlay").height(pageH);
}   

//Below function gets path for all the taxonomy whose evidence is not present
function getEvidenceFailIdsPath(lsNewElementId){  //function shud get   elementid|abc>pqr>mno
	var path= "";
	var linkagebranch = $("li[id="+lsNewElementId+"]>span>a").attr("id");
	var arr = linkagebranch.split(",");
	for(var i =0; i<arr.length-1;i++){
	path = path+(document.getElementById(arr[i]).title)+">";
	}
	path =path.replace(/.$/, '');
	var returnpath = "<li><label>"+path+" " + "</label></li>";
	return returnpath;
	}
    
//Below function calls taxonomy servlet for recaching entire taxonomy
function recacheSubmit(){
    var pageW = $(document).width();
    var pageH = $(document).height();
    $(".confirm-taxonomy-box").hide();
    $(".deleteOverlay").hide();
    hideErrorStatusDiv();
	//pageGreyOut();
    $(".wait-taxonomy-box").show();
    $(".deleteOverlay").show();
    $(".deleteOverlay").width(pageW);
    $(".deleteOverlay").height(pageH);
	recache = false;
    var v_parameter="";
 	var urlAppender = contextPathVariable+"/TaxonomyServlet.jsp?next_action=recache";
        jQuery.ajax({
        type: "POST",
        url: urlAppender,
        data: v_parameter,
        
        success: function(e){
            
              var msg = e.split("#");
              if(msg[0]=="success"){
	            	var splitPipe = msg[1].split("|");
	              	$("#errorStatusDiv").html(splitPipe[0]); 
	              	if(document.getElementById("recacheTopDiv")!=null){
	              		$("#recacheTopDiv").html(splitPipe[1]+" by "+splitPipe[2]); 
	              	}else {
	              		$("#recacheBottomDateDiv").html(splitPipe[1]+" by "+splitPipe[2]); 
	              	}
		            $("#errorStatusDiv").addClass('passed');
		            $("#errorStatusDiv").show();
              }else{
            	  if( msg[1].indexOf("|")>=0){
            		  var splitPipe = msg[1].split("|");
            		  splitPipe[1]=splitPipe[1].replace(/.$/, '');
            		  splitPipe[1]=splitPipe[1].replace("[","");
            		  var errorPathIds =splitPipe[1].split(",");
            		  splitPipe[3]=splitPipe[3].replace(/.$/, '');
            		  splitPipe[3]=splitPipe[3].replace("[","");
            		  var countFlagArray =splitPipe[3].split(",");
            		  var path="";
            		  var pathForMoreEvid="";
            		  for(var i=0;i<errorPathIds.length;i++){
            			  if(trim(countFlagArray[i])=="zero"){
            			  path = path+ getEvidenceFailIdsPath(errorPathIds[i]);
            			  }else {
            			  pathForMoreEvid = pathForMoreEvid+ getEvidenceFailIdsPath(errorPathIds[i]);
            			  }
            		  }
            		  if(path=="" && pathForMoreEvid!="" ){
            			  showErrorStatusDiv(splitPipe[0]+pathForMoreEvid+splitPipe[4]);
            		  }else if(path!="" && pathForMoreEvid=="" ){
            			  showErrorStatusDiv(splitPipe[0]+path+splitPipe[2]);
            		  }else{
            			  showErrorStatusDiv(splitPipe[0]+path+splitPipe[2]+"<br><br>"+splitPipe[0]+pathForMoreEvid+splitPipe[4]);
            		  }
            	  }else{
            		  showErrorStatusDiv(msg[1]);  
            	  }
                          
              }
              if (document.getElementById("transactionStatusDiv")!=null){
            	  	document.getElementById("transactionStatusDiv").style.display="none";
	          }
				removePageGreyOut();
           },
           beforeSend: function(){  //function for loading wheel
           
           },
           complete: function(){  //function for loading wheel
        	   $(".wait-taxonomy-box").hide();
        	   $(".deleteOverlay").hide();
           }
         });
   } 
   
//Below function adds linkage to the selected taxonomy
function addLinkages(){
	var lsNewElementId = linkageElementId;   
	var lsNewBranchId = linkageBranchValue; 
	addLinkageItem(lsNewElementId,lsNewBranchId);
}

//Below function displays error div along with error message
function showErrorStatusDiv(lsErrorMsg){
	$("#errorStatusDiv").html(lsErrorMsg); 
	$("#errorStatusDiv").addClass('failed');
	$("#errorStatusDiv").show();  		
}

//Below function hide error status div(this happens when user changes or takes another action) 
function hideErrorStatusDiv(){
	$("#errorStatusDiv").hide();
	$("#errorStatusDiv").html(""); 
	$("#errorStatusDiv").removeClass('failed');
}

//Below function submits the form with next request
function nextRequest(){
	if(detailPageElementId==""){
		detailPageElementId=savedElementId;
	}
	if(detailPageElementId==""){
		return false;
	}
	if(branchValue==""){
		branchValue=savedBranchId;
	}
	if(elementType==""){
		elementType=saveElementType;
	}
	document.myform.action=formAction+'&removeNavigator=true&elementId='+detailPageElementId+'&saveBranchId='+branchValue+'&elementType='+elementType+'&next_action=savePage';
	document.myform.submit();
}

//Below function removes spaces from left and right of the string
function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

//Below function adds synonym to selected taxonomy
function addItem(){
	hideErrorStatusDiv();
	var lsNewItem=document.getElementById('newItem');
	var lsSelect=document.getElementById('items');
	var lsNewTagOption="";
	var lsDuplicateValue="";
	var lsDuplicateFlag=false;
	var lsErrorMsg="";
	for(var liCounter=0; liCounter<lsSelect.length; liCounter++ ){
		if (lsNewItem.value.toLowerCase() == lsSelect[liCounter].value.toLowerCase()){
	  		lsDuplicateFlag=true;
	  		lsDuplicateValue=lsNewItem.value;
	  		break;
	  	}
	}

	if(trim(lsNewItem.value) != ""){
	    if (lsDuplicateFlag==true){			
	        lsErrorMsg="Synonym "+lsDuplicateValue+" already added";			
		    showErrorStatusDiv(lsErrorMsg);
    }	
    else {              
		hideErrorStatusDiv();
	    lsNewTagOption = document.createElement('option');
	    lsNewTagOption.value=lsNewItem.value;
   	    lsNewTagOption.text=lsNewItem.value;
   	    lsNewTagOption.title=lsNewItem.value;
        lsSelect.options.add(lsNewTagOption);
        lsNewItem.value='';
		setFlag();
    }
  }  
}

//Below function removes selected synonym for selected taxonomy  
function removeItem(){
	var lsSelectId=document.getElementById('items');
	var lsCounter;
	
  	for (lsCounter=lsSelectId.length - 1; lsCounter>=0; lsCounter--) {
  		if (lsSelectId.options[lsCounter].selected) {
  			lsSelectId.remove(lsCounter);
        	setFlag();
      }
    }
}

//Below function adds new linkage selected from tree
function addLinkageItem(lsNewElementId,linkageBranchValue){  //function shud get   elementid|abc>pqr>mno
	hideErrorStatusDiv();
	var path= "";
	var str = linkageBranchValue;
	var arr = str.split(",");
	var lsNewTagOption="";
	var lsDuplicateValue="";
	var lsDuplicateFlag=false;
	var lsErrorMsg="";
	var lsSelectTag = document.getElementById('exisingLinkageItems');
	
	for(var i =0; i<arr.length-1;i++){
		path = path+(document.getElementById(arr[i]).title)+">";
	}
	path =path.replace(/.$/, '');
    
	for(var liCounter=0; liCounter<lsSelectTag.length; liCounter++){
		if (lsNewElementId == lsSelectTag[liCounter].value){
	  		lsDuplicateFlag=true;
	  		lsDuplicateValue=lsNewElementId;
	  		break;
  	  }
    }
	if(trim(lsNewElementId) != ""){
		if(lsDuplicateFlag==true){	
		    lsErrorMsg="Linkage "+path+" already added"; 					
		    showErrorStatusDiv(lsErrorMsg);
	  }	
	  else{
		  lsNewTagOption=document.createElement('option');
		  lsNewTagOption.value= lsNewElementId;
		  lsNewTagOption.text=path ;  
		  lsNewTagOption.title=path ;
		  lsSelectTag.options.add(lsNewTagOption);
	      setFlag();
	  }
  }
}

//Below function gets path from parent to child for the selected taxonomy
function getLinkagePath(lsNewElementId){  //function shud get   elementid|abc>pqr>mno
	var path= "";
	var lsSelectTag = document.getElementById('exisingLinkageItems');
	var linkagebranch = $("li[id="+lsNewElementId+"]>span>a").attr("id");
	var arr = linkagebranch.split(",");
	for(var i =0; i<arr.length-1;i++){
		path = path+(document.getElementById(arr[i]).title)+">";
	}
	path =path.replace(/.$/, '');
	var lsNewTagOption = document.createElement('option');
	lsNewTagOption.value= lsNewElementId;
	lsNewTagOption.text=path ;  
	lsSelectTag.options.add(lsNewTagOption);
}

//Below function gets path for the linkage item
function getLinkagePathfromSpan(){  //function shud get   elementid|abc>pqr>mno
	var spanLinkageId = $("#detailLinkageIds").html();
	if(spanLinkageId.indexOf(",")>=0){
		var linkageIds= spanLinkageId.split(",");
		for(var j =0; j<linkageIds.length-1;j++){
			var lsNewElementId =linkageIds[j];
			var path= "";
			var lsSelectTag = document.getElementById('exisingLinkageItems');
			var linkagebranch = $("li[id="+lsNewElementId+"]>span>a").attr("id");
			var arr = linkagebranch.split(",");
			for(var i =0; i<arr.length-1;i++){
				path = path+(document.getElementById(arr[i]).title)+">";
			}
			path =path.replace(/.$/, '');
			var lsNewTagOption = document.createElement('option');
			lsNewTagOption.value= lsNewElementId;
			lsNewTagOption.text=path ;  
			lsSelectTag.options.add(lsNewTagOption);
		}
	}
}

//Below function removes selected linkage under particular selected taxonomy
function removeLinkageItem(){
	var lsSelectId=document.getElementById('exisingLinkageItems');
	var lsCounter=0;
  	for (lsCounter=lsSelectId.length - 1; lsCounter>=0; lsCounter--) {
      if (lsSelectId.options[lsCounter].selected) {
         lsSelectId.remove(lsCounter);
         setFlag();
      }
    }
}

//Below function adds ids of allsynonyms separated by delimiter '|''
function appendItemsInUrl(lsTagId){
	var lsSelectId=document.getElementById(lsTagId);
	var lsCounter;
	var lsLength;
	var lsValuesToAppend = "";	
	if(lsSelectId.length>0){
  	  for (lsCounter=0; lsCounter<lsSelectId.length; lsCounter++) {
  		  lsValuesToAppend=lsValuesToAppend + lsSelectId.options[lsCounter].value  +"|";
	  }
	lsLength=lsValuesToAppend.length;
	lsValuesToAppend=lsValuesToAppend.slice(0,(lsLength-1));  	
	}
    return lsValuesToAppend;
}

//Below function sets hidden values for synonym items
function setSynonymHiddenValue(){
	var lsValuesToAppend1= appendItemsInUrl('items');	
    $("#synonymValues").val(lsValuesToAppend1);
    
	//below sets the hidden value for 'description' tag
    var lsDescription = document.getElementById("description").value;
    $("#hiddenDescription").val(lsDescription);
}

//Below function sets hidden values for linkage items
function setLinkageHiddenValue(){
	var lsValuesToAppend1= appendItemsInUrl('exisingLinkageItems');	
    $("#linkageValues").val(lsValuesToAppend1);
}

//Below function set the hidden values for flag that is used to determine old and new state of flags
function setFlagsHiddenValue(){
	var lsFlags=document.getElementsByName('flags');
	
	for (var lsCount=0; lsCount<lsFlags.length; lsCount++){
		var lsTempId=lsFlags[lsCount].getAttribute("id");

		if (lsFlags[lsCount].checked==true && lsTempId == "chkEvidance"){
		 document.getElementById("hiddenChkEvidance").value="1";
		}
		else if (lsFlags[lsCount].checked==false && lsTempId == "chkEvidance"){
		 document.getElementById("hiddenChkEvidance").value="0";
		}
		
		else if (lsFlags[lsCount].checked==true && lsTempId == "chkApproval"){
		document.getElementById("hiddenChkApproval").value="1";
		}
		else if (lsFlags[lsCount].checked==false && lsTempId == "chkApproval"){
		document.getElementById("hiddenChkApproval").value="0";
		}

		else if (lsFlags[lsCount].checked==true && lsTempId =="chkTaxonomy"){
		 document.getElementById("hiddenChkTaxonomy").value="1";
		}
		else if (lsFlags[lsCount].checked==false && lsTempId =="chkTaxonomy"){
		 document.getElementById("hiddenChkTaxonomy").value="0";
		}

    }
}

//Below function sets hidden values that are needed in corresponding controller
function setHiddenValues(){
	var lbreturn = validateText('description');
	if(!lbreturn){
		var lsErrorMsg = "Your response contains invalid characters. Please re-type your response or visit Online Help for instructions on how to Copy and Paste as plain text.";
		showErrorStatusDiv(lsErrorMsg);
	}
	else{
		    hideErrorStatusDiv();
		    setSynonymHiddenValue();
		    setFlagsHiddenValue();
		    setLinkageHiddenValue();
			var lsErrorMsg = "Taxonomy name is required!";
			var lsNewElementName;
			lsNewElementName=trim(document.getElementById("taxonomyElementName").value);
		    if ("" != lsNewElementName){
			   $("#hiddenElementName").val(lsNewElementName);
			   nextRequest();
		    }
		    else{
		  		showErrorStatusDiv(lsErrorMsg);
		    }
	}
}
 
//Below function sets checkbox state to disabled based upon below criteria
function setChkBoxState(){
   if(document.getElementById("chkEvidance").checked==true){
      document.getElementById("chkApproval").checked=true;
      document.getElementById("chkTaxonomy").checked=true;
      document.getElementById("chkApproval").disabled=true;
      document.getElementById("chkTaxonomy").disabled=true;
   }
   else{
      document.getElementById("chkApproval").disabled=false;
      document.getElementById("chkTaxonomy").disabled=false;
   }

}

//Below function sets checkbox visibility depending upon it is 'function' or 'service area' or rest of them
function setChkBoxVisibility(){
	if((saveElementType != "") && (checkTaxonomy(savedBranchId) && (document.getElementById(savedElementId).title).toLowerCase()!="function" && (document.getElementById(savedElementId).title).toLowerCase()!="service area" )){
	   if(document.getElementById("chkEvidanceId")!=null){
      document.getElementById("chkEvidanceId").style.visibility="hidden";
      document.getElementById("chkApprovalId").style.visibility="hidden";
      }
	}else if( (saveElementType != "") && !checkTaxonomy(savedBranchId)  && (saveElementType.toLowerCase() != "function" && saveElementType.toLowerCase() != "service area")) {
      if(document.getElementById("chkEvidanceId")!=null){
      document.getElementById("chkEvidanceId").style.visibility="hidden";
      document.getElementById("chkApprovalId").style.visibility="hidden";
      }
	} 
	else{
	  if(document.getElementById("chkEvidanceId")!=null){
      document.getElementById("chkEvidanceId").style.visibility="visible";
      document.getElementById("chkApprovalId").style.visibility="visible";
      }
	} 
}

// this function is called on click of ok buuton to navigate to detail page
function callShowList(){
	changeFlag = false;
	$(".goToDetail-taxonomy-box").hide();
	$(".deleteOverlay").hide();
	showList(noChangeElementId,noChangeBranchid,noChangeType);
}
// this function sets the max limit
//updated in R5
function setMaxLength(obj,maxlimit){
	if(($(obj).val().length + $(obj).val().split("\n").length-1)  > maxlimit){
		$(obj).val($(obj).val().substring(0, maxlimit - $(obj).val().split("\n").length + 1));
		return false;
	}
}

function validateText(id) {
	convertSpecialCharactersHTMLGlobal(id,true);
	var lsDescription = document.getElementById(id).value;
	lsResult = "^[0-9a-zA-Z !@\\\#\\\$%^\\\&*()-_+=|\\\\{}\\\[\\\];:\\\"\\\'<>,.?\\\/`~\xA7\n\r\t]+$";
	var re = new RegExp(lsResult);
	if(null!=lsDescription && lsDescription!=""){
		return re.test(lsDescription);
	}else{
		return true;
	}
}

