//This method submit all the selected population and save it on db.
function selectAllAndSubmit(pageToDirect,bussAppId,section,subSection) {
 		var boxList = $('input:checkbox:checked');
		if (boxList.length == 0) {
			document.geographyform.next_action.value="nodirect";
			if($("#errorMessage").html()=='' || $("#errorMessage").html()==undefined){
    			$("#errorMessage").append("You must select at least one geography");
    		}
		}else {
    		document.geographyform.next_action.value=pageToDirect;
    		document.geographyform.action = document.geographyform.action+"&business_app_id="+bussAppId+"&section="+section+"&subsection="+subSection;
    		document.geographyform.submit();
		}
	}

//Initialize for Collapse and Expand Demo:

function EmptyListItem(id) {
      var mycheckbox = document.getElementById(id);
      var boxList = $('input:checkbox');
      if(mycheckbox.checked){
            boxList.each(function(i){
                  var checkBoxId = $(this).attr("id");
                  if(checkBoxId!="close"){
                        this.checked = false;
                        this.disabled=true;
                  }
            });
            $("#errorMessage").html("");
            var accordionHeaderIds= $("div[id*='accordionHeaderId']");
			accordionHeaderIds.each(function(){
				$(this).css({
					'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
					'cursor':'auto',
					'background-color':'#C1C1C1'});
					$(this).next().removeClass();
					$(this).next().addClass("close");
					$(this).nextAll().hide();
				});
      }
      else{
           boxList.each(function(i){
              var checkBoxId = $(this).attr("id");
              if(checkBoxId!="close"){
                    this.checked = false;
                    this.disabled=false;
              }
           });
           var accordionHeaderIds= $("div[id*='accordionHeaderId']");
			accordionHeaderIds.each(function(){
				$(this).css({
					'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
					'cursor':'pointer',
					'background-color':'#4297E2'});
					$(this).next().removeClass();
					$(this).next().addClass("close");
					$(this).nextAll().hide();
				});
      }
}
//This method perform functions on the accordion when we perform various action.
$(function(){
if($("#close").attr("checked")){
	var accordionHeaderIds= $("div[id*='accordionHeaderId']");
		accordionHeaderIds.each(function(){
			$(this).css({
			'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
			'cursor':'auto',
			'background-color':'#C1C1C1'});
			$(this).nextAll().hide();
	});
}else{
	var accordionHeaderIds= $("div[id*='accordionHeaderId']");
		accordionHeaderIds.each(function(){
		$(this).css({
		'background':"url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2",
		'cursor':'pointer',
		'background-color':'#4297E2'});
		$(this).nextAll().hide();
	});
}
		// Tabs
	if($("#close").attr("checked")){
		var boxList = $('input:checkbox');
        boxList.each(function(i){
	         var checkBoxId = $(this).attr("id");
	         if(checkBoxId!="close"){
		         this.checked = false;
		         this.disabled=true;
	         }
         });
    }
	$('#tabs').tabs();
	$('#newTabs').tabs();
	// Dialog
	$('#dialog').dialog({
		autoOpen: false,
		width: 600,
		buttons: {
			"Ok": function() {
				$(this).dialog("close");
			},
			"Cancel": function() {
				$(this).dialog("close");
			}
		}
	});
});

window.onload=function(){
	 $(".accContainer").each(function(i){
	 	var isValid = true;
        var checkBoxId = $(this).find("input:checkbox");
        checkBoxId.each(function(i){
        if(i>0){
        	if($(this).attr("checked")){
		}else{
    		isValid = false;
        }
       }
 	}); 
   	if(isValid){
	    var topId = $(checkBoxId)[0];
	    $(topId).attr("checked",true);
    }
  });  
};
//This method enable all the text boxes
function  enableAllTextbox(obj,key){
	var boxList = $('input:checkbox');
	     if($(obj).attr("checked")){
	           boxList.each(function(i){
	                 var checkBoxId = $(this).attr("id");
	                 if(checkBoxId==key){
	                       this.checked = true;
	                 }
	           }); 
	     }
	     else{
	           boxList.each(function(i){
	                 var checkBoxId = $(this).attr("id");
	                 if(checkBoxId==key){
	                       this.checked = false;
	                 }
	           });   
	     }
}
//This method remove all the selected value
function removeSelectAll(obj,key,topCheckBoxId){
     var boxList = $('input:checkbox');
     
     if($(obj).attr("checked")==undefined || $(obj).attr("checked")==false){
           boxList.each(function(i){
                 var checkBoxId = $(this).attr("id");
                 if(checkBoxId==topCheckBoxId){
                       this.checked = false;
                 }
           }); 
     }
     $(".accContainer").each(function(i){
	 	var isValid = true;
        var checkBoxId = $(this).find("input:checkbox");
        checkBoxId.each(function(i){
        if(i>0){
        	if($(this).attr("checked")){
		}else{
    		isValid = false;
        }
       }
 	}); 
   	if(isValid){
     var topId = $(checkBoxId)[0];
     	$(topId).attr("checked",true);
        }
   });  
     
}
//This method perform expand and collapse function
function collapseExpandAll(expandCollapse){
	if($("#close").attr("checked")!='checked'){
		var topHeaderIds= $("div[id*='accordionHeaderId']");
			topHeaderIds.each(function(){
				if(expandCollapse=='collapseAll'){
					$(this).next().removeClass();
					$(this).next().addClass("close");
					$(this).nextAll().hide();
					$(this).attr("style","background:url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
				}else{
					$(this).next().removeClass();
					$(this).next().addClass("custDataRowHead1");
					$(this).nextAll().show();
					$(this).attr("style","background:url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
				}
			});
		}
}
//This method display the accordian.
function displayAccortion(obj){
		if($("#close").attr("checked")!='checked'){
			var openShow = $(obj).next().attr("class");
			if(openShow=='custDataRowHead1'){
				$(obj).next().removeAttr("class");
				$(obj).next().addClass("close");
				$(obj).attr("style","background:url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
				$(obj).nextAll().hide();
			}else{
				$(obj).next().removeAttr("class");
				$(obj).attr("style","background:url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
				$(obj).next().addClass("custDataRowHead1");
				$(obj).nextAll().show();
			}
		}
	}
	//This method perform the functionality of going to previous page on clicking back button
function GoToPreviousPage(pageToDirect,bussAppId,section,subSection) {
  	document.geographyform.next_action.value=pageToDirect;
  	document.geographyform.action = document.geographyform.action+"&business_app_id="+bussAppId+"&section="+section+"&subsection="+subSection;
	document.geographyform.submit();
}