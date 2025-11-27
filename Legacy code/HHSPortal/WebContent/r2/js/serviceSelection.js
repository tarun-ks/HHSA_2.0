/**
 /* This function is used when user click on the continue button then hyper link on the accordion title
 * clicking on that link 
 * @param ulId id of the ul
 * @param prevId prev id
 * @param appendPrevId 
 */
function backToService(ulId,prevId,appendPrevId){
	    $("#"+appendPrevId).nextAll().remove();
	    var replaceContent = $("div #displayContinue ul[id='"+prevId+"']").html();
		$("#tempId").after(replaceContent);
		var tempId = $("#tempId").next().attr("id");
		var secondAnchor = $("div #displayContinue li[id='"+tempId+"']").parent().attr("id");//html();
	
		var finalTopId = $("div #displayContinue li[id='"+secondAnchor+"']").parent().attr("id");
	    var headerName = $("#"+appendPrevId).parent().find("div").eq('0').text();
	     
	    headerName = headerName.substring(0,headerName.lastIndexOf(">"));
	    $("#"+appendPrevId).parent().find("div").eq('0').html(headerName);
		headerName = headerName.substring(0,headerName.lastIndexOf(">"));
		var prevId = $("#"+appendPrevId).next("ul").attr("id");
	    if(prevId=='' || prevId== undefined ){
			prevId = $("#nextElement").next().attr("id");
	    }
		var tempName = headerName;
		tempName = headerName.substring(tempName.lastIndexOf(">"),tempName.length);
		tempName = tempName.replace(">","");
		var	anchorTag = "";
		if(headerName==' ' || headerName==''){
			anchorTag="";
		}else{
				anchorTag = "<div id='nextElement'><a title='"+tempName+"' href='javascript:;' onclick=backToService('"+secondAnchor+"','"+finalTopId+"','"+appendPrevId+"')> &lt;&lt;&lt; Back to "+tempName+"</a></div>"
		}
	    $("#"+appendPrevId).after(anchorTag+"<ul id='"+secondAnchor+"'>"+replaceContent+"</ul>");
		$("#"+appendPrevId).removeClass().addClass("custDataRowHead");
	}
//This method called when page is getting loaded and set the values
$(document)
.ready(
		function() {
			var result = "";
			var first = true;
			var liTitle = 0;
			var inputObj = $("ul#selected_Services").find("input[type=hidden]");
			inputObj.each(function(){
				 if(first) {
				        result+=$(this).val();
				        first=false;
				    } else {
				        result+=","+$(this).val();
				    }
				 ++liTitle;
			});
			$("#addSelectedServices").val(result);

			if(liTitle>=2){
				$("#removeAll").removeAttr("class");
			}else{
				$("#removeAll").addClass("displayNone");
			}
			if($("#noneHidden").val()==0){
				$("#noneSelected").show();
			}
			//onclick of back button, redirecting to procurement summary page.
			$("#backAction").click(
					function() {
						document.seviceSelectionForm.action = $(
								"#backTOProcurementServiceURL").val();
						//submitting the page.
						document.seviceSelectionForm.submit();
					});
		});

/**
 * this function is called on click of next button, 
 * redirect the page to the approved provider page
 */
function nextToApprovedProviders() {
	$("#hiddenDiv>#midLevelFromRequest").val("ApprovedProviders");
	document.hiddenFormUrl.action = document.hiddenFormUrl.action + "&render_action=approvedproviders";
	//submitting the page.
	document.hiddenFormUrl.submit();
}


	
/**
 * This method is used when user click on the continue button
 * @param obj obj of that button 
 */
	function clickButton(obj){
	      var currentElt = $(obj);
	      var currentEltId = currentElt.attr("id");
	      var ulId = currentEltId.replace("input", "");
	
	      $("ul:first[id='"+ulId+"']").show();
	      $("li:first[id='"+ulId+"']").find('div').eq(0).hide();
	      $("li:first[id='"+ulId+"']").find('div').eq(1).hide();
	      var title = currentElt.attr("alt");
	      var searchParam = "topElementId"+title;
	
		  var prevId = $("#"+searchParam).next("ul").attr("id");
	      if(prevId=='' || prevId== undefined ){
	            prevId = $("#nextElement").next().attr("id");
	    	}
	      var oldData = $("ul[id='"+ulId+"']").html();
	
	      $("#"+searchParam).nextAll().remove();
	      var eltName = currentElt.parent().siblings(".titleClass").text();
	      var headerName = $("#"+searchParam).parent().find("div").eq('0').text();
	      var anchorTagName = headerName;
	      $("#"+searchParam).parent().find("div").eq('0').html(headerName + " > "+eltName);
	      if(anchorTagName.indexOf(">")!=-1){
	            anchorTagName = anchorTagName.substring(anchorTagName.lastIndexOf(">")+2,anchorTagName.length);
	      }
	      var anchorTag = "<div id='nextElement'><a title='"+anchorTagName+"' href='javascript:;' onclick=backToService('"+ulId+"','"+prevId+"','"+searchParam+"')> &lt;&lt;&lt; Back to "+anchorTagName+"</a></div>"
	      oldData = anchorTag + "<ul id='"+ulId+"'>"+oldData+"</ul>";
	
	      $("#"+searchParam).after(oldData);
	      currentElt.hide();
	}
	
	/**
	 * this method will remove the services from the selected services box
	 * by calling the method addRemoveService
	 */
	function removeAllServices(){
		var visibleLen = $("#selected_Services li:visible");
		visibleLen.each(function(i){
			var liId = $(this).attr("id");
			var liText = $("#"+liId).find("span").html();
			liId = liId.substring(14,$(this).attr("id").length);
			var inputId = "input"+liId;
		 	addRemoveService(liText,document.getElementById(inputId),liId);
		});
		$("#removeAll").addClass("displayNone");
	 }
	/**
	 * This function is called for expand all and collapse all
	 * @param expandCollapse
	 */
	function collapseExpandAll(expandCollapse){
	var topHeaderIds= $("div[class*='custToggleHead']");
		topHeaderIds.each(function(){
			if(expandCollapse=='collapseAll'){
				$(this).next().removeClass();
				$(this).next().addClass("close");
				$(this).nextAll().hide();
				$(this).attr("style","background:url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
			}else{
				$(this).next().removeClass();
				$(this).next().addClass("custDataRowHead");
				$(this).nextAll().show();
				$(this).attr("style","background:url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
			}
		});
	}
	
	/**
	 * This function is used open and hide the accordion
	 * @param obj
	 */
	function displayAccordion(obj){
		var openShow = $(obj).next().attr("class");
		if(openShow=='custDataRowHead'){
			$(obj).next().removeAttr("class");
			$(obj).next().addClass("close");
			$(obj).attr("style","background:url('../framework/skins/hhsa/images/arrowExpand.png') no-repeat scroll 99% center #4297E2");
			$(obj).nextAll().hide();
		}else{
			$(obj).next().removeAttr("class");
			$(obj).attr("style","background:url('../framework/skins/hhsa/images/arrowCollapse.png') no-repeat scroll 99% center #4297E2");
			$(obj).next().addClass("custDataRowHead");
			$(obj).nextAll().show();
		}
	}
	
	$(function(){
		// set the css class  for each input elements having valuye as Continue
		$("input[value='Continue']").each(function(i){
		      var currentElt = $(this);
		      var currentEltId = currentElt.attr("id");
		      //currentElt.attr("onclick","clickButton(this)");
		      var ulId = currentEltId.replace("input", "");
			  currentElt.removeClass().addClass("addTaxonomyButtons");
		      $("ul[id='"+ulId+"']").hide();
		});
	//set the css class  for each input elements having valuye as Add or remove
		$("input[value='+ Add'], input[value='- Remove']").each(function(i){
		     var currentElt = $(this);
		     var currentEltId = currentElt.attr("id");
		     var id = currentEltId.replace("input", "");
			 if(currentElt.val()=='+ Add'){
				 currentElt.removeClass().addClass("addTaxonomyButtons");
		     }
			 else{
				currentElt.removeClass().addClass("removeTaxonomyButtons");
			 }
		     var eltName = currentElt.parent().siblings(".titleClass").text();
		     if($("#org_type").val()=='agency_org'){
		    	 currentElt.attr('disabled','disabled');
		    	 currentElt.attr('disabled',true);
		     }
		});
	
		$("div>ul>li>div").attr("class","custToggleHead");
		
		var visibleCounter = $("input[value='Continue']:visible").length;
		visibleCounter = visibleCounter/2;
		//set the css class  for each input elements having valuye as Continue
		$("input[value='Continue']:visible").each(function(i){
		    if(i<visibleCounter){
		      var currentElt = $(this);
		      var currentEltId = currentElt.attr("id");
		      var ulId = currentEltId.replace("input", "");
		      $("ul[id='"+ulId+"']").show();
		      currentElt.hide();
		      var counter = ++i;
		
		      $("li[id='"+ulId+"']").find("input").attr("alt",counter);
		      $("li[id='"+ulId+"']").find('div').eq(1).text("").attr("id","topElementId"+counter).removeClass();

			  $("#topElementId"+counter).prev().click(function(){
			  	displayAccordion(this);
			  });
			  $("#topElementId"+counter).removeClass().addClass("custDataRowHead");
			  $("#topElementId"+counter).nextAll().removeClass().addClass("custContainer");
		      $("li[id='"+ulId+"']").find('div').eq(1).append("<span class='custAccordianCol1'>Service</span> <span class='custAccordianCol2'>Description</span>").attr("style","float:left");
		    }
		});
	
	collapseExpandAll('collapseAll');
	
		$("#displayContinue").hide();
		//on click of clear button
	       $("#clearButton").click(function(){
				$("#searchText").val("");
				$("#errorMessageSearch").html("");
			});
			$("#searchText").keypress(function(e)
	        {
	            code= (e.keyCode ? e.keyCode : e.which);
	            if (code == 13){ 
		            $("#searchButton").click();
		            e.preventDefault();
	            }
	        });
			
				 if($("#org_type").val()=='agency_org' || $("#isCancelledProcurement").val()=='true'){
				$(".addServiceBlock").find("input[type=button]").each(function(){
					$(this).remove();
				});
				$(".descriptionTreeClass").each(function(){
					$(this).removeClass("descriptionTreeClass").addClass("descriptionTreeClassAgency");
				});
			 }
			 $("input[value='+ Add'], input[value='- Remove'] , input[value='Continue']").each(function(i){
				 $(this).removeAttr("title");
			 });
	  });
           
/**
 * This function is used to add and remove the service on add service page
 * @param id id of the service
 * @param obj obj of the service
 * @param addedServicesId 
 */
	 function addRemoveService(id, obj,addedServicesId) {
        if(obj.value == '+ Add'){
            //previous code by which the values were added
            var serviceSelected = document.getElementById('selected_Services');
            
            var serviceSelected = document.getElementById('selected_Services');
            var element = document.createElement('li');
            element.appendChild(document.createTextNode(id));
            element.id = "displayService"+addedServicesId;
          
            
            serviceSelected.appendChild(element);
            var functionScript1 = "addRemoveService('"+id+"','Remove','"+addedServicesId+"')";
            var functionScript = new Function(functionScript1);
            if ( typeof(element.attachEvent) != "undefined" ){
                  element.attachEvent("onclick", function(){addRemoveService(id,obj,addedServicesId)}) ;
             }else{
                  element.addEventListener("click", function(){addRemoveService(id,obj,addedServicesId)}, false) ;
             }
            obj.value = '- Remove';
            $(obj).attr("class","removeTaxonomyButtons");
            $(obj).removeAttr("title");
         
           
            if($("#addSelectedServices").val()!="" && $("#addSelectedServices").val()!=undefined){
                $("#addSelectedServices").val($("#addSelectedServices").val()+","+addedServicesId);
            }else{
                $("#addSelectedServices").val(addedServicesId);
            }
            
            var addId = $(obj).attr("id");
			$("input[type=button]").each(function(i){
				if($(this).attr("id")==addId){
					$(this).val("- Remove");
		            $(this).attr("class","removeTaxonomyButtons");
		          
				}
			});
        }else if(obj.value == '- Remove'){
            var element = document.getElementById("displayService"+addedServicesId);
            if(element==null || element==undefined){
                setSelectedServices(addedServicesId);
            }else{
                element.parentNode.removeChild(element);
                obj.value = '+ Add';
                $(obj).attr("class","addTaxonomyButtons");
                $(obj).removeAttr("title");
            
            }
            var removeId = $(obj).attr("id");
			$("input[type=button]").each(function(i){
				if($(this).attr("id")==removeId){
					$(this).val("+ Add");
		            $(this).attr("class","addTaxonomyButtons");
				}
			});
            var commaSeperatedString = $("#addSelectedServices").val().split(",");
            $("#addSelectedServices").val("");
            for(var i = 0; i <commaSeperatedString.length; i++) {
                if(commaSeperatedString[i]!=addedServicesId){
                	if(i == 0){
                		$("#addSelectedServices").val(commaSeperatedString[i]);
                	} else {
                		$("#addSelectedServices").val($("#addSelectedServices").val() + "," + commaSeperatedString[i]);
                	}
                    
                }
                if(commaSeperatedString.length==1){
                    $("#addSelectedServices").val("");
                }
            }
            if($("#addSelectedServices").val()!=null && $("#addSelectedServices").val()!=""){
                var finalValue = $("#addSelectedServices").val();
                if(finalValue.charAt(finalValue.length-1)==","){
                    $("#addSelectedServices").val(finalValue.substring(0,finalValue.length-1));
                }else if(finalValue.charAt(0)==","){
                    $("#addSelectedServices").val(finalValue.substring(1,finalValue.length));
                }
            }
        }
        
       	var liTitle = 0;
       	var liList = $("#selected_Services").find("li[id^=displayService]");
        	liList.each(function(i){
				if($(this).attr("title")!='' && $(this).attr("title")!=undefined){
					++liTitle;
				}
			});
			if(liList.length==0){
				$("#noneSelected").show();
			}else{
				$("#noneSelected").hide();
			}
			if(liList.length>=2){
				$("#removeAll").removeAttr("class");
			}else{
				$("#removeAll").addClass("displayNone");
			}
			
    }
	 /**
	  * This function is hide and show the services on the page load
	  * @param obj
	  * @param elementId
	  */
	function hideShowDisplayService(obj,elementId){
	    $(obj).remove();
	    var hideSection =  "input"+elementId;
	    $("#"+hideSection).val("+ Add");
	    $("#"+hideSection).attr("class","addTaxonomyButtons");
	    var commaSeperatedString = $("#addSelectedServices").val().split(",");
	    var value ="";
	    for(var i = 0; i <commaSeperatedString.length; i++) {
	        if(commaSeperatedString[i]==elementId){
	            if(commaSeperatedString.length==1){
	                $("#addSelectedServices").val("");
	            }
	        }else{
	            value = value + ',' + commaSeperatedString[i];
	        }
	    }
	    $("#addSelectedServices").val(value);
	   
	    if($("#addSelectedServices").val()!=null && $("#addSelectedServices").val()!=""){
	        var finalValue = $("#addSelectedServices").val();
	        if(finalValue.charAt(finalValue.length-1)==","){
	            $("#addSelectedServices").val(finalValue.substring(0,finalValue.length-1));
	        }else if(finalValue.charAt(0)==","){
	            $("#addSelectedServices").val(finalValue.substring(1,finalValue.length));
	        }
	    }
	    
		var liTitle = 0;
        var liList = $("#selected_Services").find("li[id^=displayService]");
        	liList.each(function(i){
				if($(this).attr("title")!='' && $(this).attr("title")!=undefined){
					++liTitle;
				}
			});
        	
			if(liList.length==0){
				$("#noneSelected").show();
			}else{
				$("#noneSelected").hide();
			}
			if(liList.length>=2){
				$("#removeAll").removeAttr("class");
			}else{
				$("#removeAll").addClass("displayNone");
			}
			
	}
	
	/**
	 * This function is used to set the selected services
	 * @param selectedId
	 */
	function setSelectedServices(selectedId){
	    var inputList = $("input[id='hiddenSelectedServices'][type='hidden']");
	    var selectedServiceId;
	    inputList.each(function(i){
	        var serviceId = $(this).attr("value");
	        if(selectedId==null){
	            var nameObj = $("li[id='"+serviceId+"']").find('div:first')[0];
	            var displayName = "displayService"+serviceId;
	           
	            serviceId = "input"+serviceId;
				$("input[type=button]").each(function(i){
					if($(this).attr("id")==serviceId){
						$(this).val("- Remove").attr("class","removeTaxonomyButtons").attr("enabled","true");
						
					}
				});
	        }
	        else if(selectedId!=null && selectedId==serviceId){
	                serviceId = "input"+selectedId;
	                $("#"+serviceId).val("+ Add");
	                $("#"+serviceId).attr("class","addTaxonomyButtons");
	                $("#displayService"+selectedId).hide();
	            }
	         
	        });
	}
	/**
	 * This function is used check the changed value on the form
	 * @param saveServices
	 * @param e
	 * @returns {Boolean}
	 */
	function setValue(saveServices, e){
		var actionParam = '';
		if(saveServices == 'backtoProcurementSummary'){
			actionParam ="&submit_action=moveonprocurementsummarypage";
			if($("#addSelectedServices").val().length > 0){ 
		       $('<div id="dialogBox"></div>').appendTo('body')
               .html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
               .dialog({
                     modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
                     width: 'auto', modal: true, resizable: false, draggable:false,
                     dialogClass: 'dialogButtons',
                     buttons: {
                           OK: function () {
                        	    $("#seviceSelectionForm").attr("action","");
                   				$("#seviceSelectionForm").attr("action",$("#actionURL").val()+actionParam);
                       			$("#saveServices").val(saveServices);
                        	   	$(this).dialog("close");
                        	   	$("#saveServices").closest("form").submit();
                        	   	$(this).remove();
                       			return true;
                           },
                           Cancel: function () {
                                $(this).dialog("close");
                                $(this).remove();
                           }
                     },
                     close: function (event, ui) {
                           $(this).remove();
                     }
               });
		       $("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
			}else{
				
				pageGreyOut();
				$("#saveServices").val(saveServices);
       			$("#saveServices").closest("form").submit();
			}
		}
		
		else if($("#addSelectedServices").val()==null || $("#addSelectedServices").val()==''){
   			$("#errorMessage").show();
   			$(window).scrollTop($('#errorMessage').offset().top);
	    	return false;
	   	}
		else{
			pageGreyOut();
    		$("#saveServices").val(saveServices);
    		return true;
    	}
	}
