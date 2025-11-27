/**
 * This function is used when user click on the continue button then hyper link on the accordion title
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
	function displayAccortion(obj){
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
		// set the css class 
		$("input[value='Continue']").each(function(i){
		      var currentElt = $(this);
		      var currentEltId = currentElt.attr("id");
		      var ulId = currentEltId.replace("input", "");
			  currentElt.removeClass().addClass("addTaxonomyButtons");
		      $("ul[id='"+ulId+"']").hide();
		});
	
		$("input[value='+ Add'], input[value='- Remove']").each(function(i){
		     var currentElt = $(this);
		     var currentEltId = currentElt.attr("id");
		     var id = currentEltId.replace("input", "");
			 if(currentElt.val()=='+ Add'){
				 currentElt.removeClass().addClass("addTaxonomyButtons");
		     }
			 else{
				currentElt.removeClass().addClass("removeTaxonomyButtons");
				currentElt.attr("title","- Remove");
			 }
		     var eltName = currentElt.parent().siblings(".titleClass").text();
		});
	
		$("div>ul>li>div").attr("class","custToggleHead");
		
		var visibleCounter = $("input[value='Continue']:visible").length;
		visibleCounter = visibleCounter/2;
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
			  	displayAccortion(this);
			  });
			  $("#topElementId"+counter).removeClass().addClass("custDataRowHead");
			  $("#topElementId"+counter).nextAll().removeClass().addClass("custContainer");
		      $("li[id='"+ulId+"']").find('div').eq(1).append("<span class='custAccordianCol1'>Service</span> <span class='custAccordianCol2'>Description</span>").attr("style","float:left");
		    }
		});
	
	collapseExpandAll('collapseAll');
	
		$("#displayContinue").hide();
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
			
			$("#searchButton").click(function(){
				var searchedData = $("#searchText").val();
				if(searchedData.length>=3 && searchedData.length<=50){
				pageGreyOut();
				var jqxhr = $.post($("#contextPath").val()+"/searchServiceServlet",{searchText:searchedData})
	                 .success(function() {
	                 	$("#errorMessageSearch").html("");
	                                 // comes here on successful response
	                    responsetext = jqxhr.responseText;
						var data = $.parseJSON(responsetext);
						var content = "";
						if(data.taxonomyList!=null){
							data.taxonomyList.sort(function (a, b) {
							    a = a.qualifiedName,
							    b = b.qualifiedName;
							    return a.localeCompare(b);
							});
							content = "<table cellspacing='0' cellpadding='0' width='100%'>";
							 $.each(data.taxonomyList, function(i,taxonomyList){
							 	content += "<tr>";
							 	content += '<td class="col1">' + taxonomyList.qualifiedName + '</td>';
							 	content += '<td class="col2 searchResultDescription">' + taxonomyList.description + '</td>';
							 	 if($("#selected_Services li[id=displayService"+taxonomyList.id+"]").length==0)
					            	content += '<td class="col3"><input type="button" class="addTaxonomyButtons" value="+ Add" title="+ Add" id="input' + taxonomyList.id + '" onclick="addRemoveService(\''+taxonomyList.name+'\', this, \''+taxonomyList.id+'\')"/></td></tr>';
					            else{
					            	var disabled = "";
					            	if($("#displayService"+taxonomyList.id).hasClass("displayNone")){
					            		disabled = "disabled=\"disabled\"";
					            	}
					            	content += '<td class="col3"><input type="button" class="removeTaxonomyButtons" value="- Remove" title="- Remove" id="input' + taxonomyList.id + '" onclick="addRemoveService(\''+taxonomyList.name+'\', this, \''+taxonomyList.id+'\')" '+ disabled+'/></td></tr>';
					            }
					            	
					          });
					           content += "<table>";
					          $(".searchTextToDisplay").html(searchedData);
					          $(".searchResults").html(content);
					          $(".returnButton").show();
					          $(".searchResultsContainer").show();
					          $("#addServiceText").hide();
					          $("#searchServiceText").show();
					          $(".addServiceBlock").hide();
					          $(".addServiceBlock1").hide();
					          $("#errorMessage").html("");
				          }else{
				          	$("#errorMessageSearch").html(data.error);
				          	$("#errorMessage").html("");
				          }
						removePageGreyOut();
	                })
	                .error(function() {
						removePageGreyOut();
	                    return false;
	                 });
				}else{
					$("#errorMessageSearch").html("Please enter search data between 3 to 50 characters");
					$("#errorMessage").html("");
				}
			});
			$(".returnButton").click(function(){
				pageGreyOut();
				$("#searchText").val("");
				$(".returnButton").hide();
				$(".searchResultsContainer").hide();
				$(".addServiceBlock").show();
				$(".addServiceBlock1").hide();
				$("#addServiceText").show();
				$("#searchServiceText").hide();
				$("#errorMessageSearch").html("");
				$("#errorMessage").html("");
				removePageGreyOut();
			});
			
			$("#removeAll").click(function(){
				var visibleLen = $("#selected_Services li:visible");
				visibleLen.each(function(i){
					var liId = $(this).attr("id");
					var liText = $("#"+liId).find("span").html();
					liId = liId.substring(14,$(this).attr("id").length);
					var inputId = "input"+liId;
				 	addRemoveService(liText,document.getElementById(inputId),liId);
				});
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
            element.title="Remove "+id;
            
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
            $(obj).attr("title","- Remove");
           
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
		            $(this).attr("title","- Remove");
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
                $(obj).attr("title","+ Add");
            }
            var removeId = $(obj).attr("id");
			$("input[type=button]").each(function(i){
				if($(this).attr("id")==removeId){
					$(this).val("+ Add");
		            $(this).attr("class","addTaxonomyButtons");
				}
			});
            var commaSeperatedString = $("#addSelectedServices").val().split(",");
            var tempCommaSeperated = "";
            var temp = false;
            for(var i = 0; i <commaSeperatedString.length; i++) {
                if(commaSeperatedString[i]!=addedServicesId){
                	tempCommaSeperated+=commaSeperatedString[i]+",";
                	temp = true;
                }
                if(commaSeperatedString.length==1){
                    $("#addSelectedServices").val("");
                }
            }
            if(temp){
            	$("#addSelectedServices").val(tempCommaSeperated);
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
        var liList = $("#selected_Services").find("li");
        	liList.each(function(i){
				if($(this).attr("title")!='' && $(this).attr("title")!=undefined){
					++liTitle;
				}
			});
			if(liTitle==0){
				$("#noneSelected").show();
			}else{
				$("#noneSelected").hide();
			}
			if(liTitle>=2){
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
	    $("#"+hideSection).attr("class","button");
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
	            $("li[id='"+displayName+"']").text($(nameObj).text());
	            serviceId = "input"+serviceId;
				$("input[type=button]").each(function(i){
					if($(this).attr("id")==serviceId){
						$(this).val("- Remove").attr("class","removeTaxonomyButtons").attr("disabled","true");
						$(this).attr("title","- Remove");
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
		if(saveServices == 'back'){
			if($("#addSelectedServices").val().length > 0){ 
		       $('<div id="dialogBox"></div>').appendTo('body')
               .html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
               .dialog({
                     modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
                     width: 'auto', modal: true, resizable: false, draggable:false,
                     dialogClass: 'dialogButtons',
                     buttons: {
                           OK: function () {
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
        	   	$("#saveServices").val(saveServices);
        	   	$("#saveServices").closest("form").submit();
			}
		}else if($("#addSelectedServices").val()==null || $("#addSelectedServices").val()==''){
			$("#errorMessageSearch").html("");
   			$("#errorMessage").html("You must select at least one service.");
	    	return false;
	   	}else{
	   		setTimeout(function(){
			document.getElementById("saveService").disabled= true;
			}, 2 );
    		$("#saveServices").val(saveServices);
    		return true;
    	}
	}
	