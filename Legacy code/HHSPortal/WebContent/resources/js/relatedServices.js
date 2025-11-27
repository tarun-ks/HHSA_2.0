//This method add or remove service.
function addRemoveService(id, obj,addedServicesId) {
		if(obj.value == '+ Add'){
			//previous code by which the values were added
			var serviceSelected = document.getElementById('selected_Services');
			var element = document.createElement('li');
			element.appendChild(document.createTextNode(id)); 
			element.id = addedServicesId;
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
			obj.title = '- Remove';
			$(obj).attr("class","removeTaxonomyButtons");
			
			if($("#addSelectedServices").val()!="" && $("#addSelectedServices").val()!=undefined){
				$("#addSelectedServices").val($("#addSelectedServices").val()+","+addedServicesId);
			}else{
				$("#addSelectedServices").val(addedServicesId);
			}
		}else if(obj.value == '- Remove'){
			var element = document.getElementById(addedServicesId);
			
			if(element==null || element==undefined){
				setSelectedServices(addedServicesId);
			}else{
				element.parentNode.removeChild(element);
				obj.value = '+ Add';
				$(obj).attr("class","addTaxonomyButtons");
				$(obj).removeAttr("title");
				$(obj).attr("title","+ Add");
			}
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

//This method set the selected services.
function setSelectedServices(selectedId){
	var inputList = $("input[id='hiddenSelectedServices'][type='hidden']");
	var selectedServiceId;
	inputList.each(function(i){
		var serviceId = $(this).attr("value");
		if(selectedId==null){
			serviceId = "myButton"+serviceId;
			$("#"+serviceId).val("- Remove");
			$("#"+serviceId).attr("class","removeTaxonomyButtons");
		}
		else if(selectedId!=null && selectedId==serviceId){
				serviceId = "myButton"+selectedId;
				$("#"+serviceId).val("+ Add");
				$("#"+serviceId).attr("class","button");
				$("#displayService"+selectedId).hide();
			}
			if(i>0){
				selectedServiceId = $("#addSelectedServices").val()+","+$(this).attr("value");
			}else{
				selectedServiceId = $(this).attr("value");
			}
			$("#addSelectedServices").val(selectedServiceId);
		});
}
//This method set the value for similar services
function setValue(saveServices,bussAppId){
   	$("#saveServices").val(saveServices);
   	document.servicesummaryform.action = document.servicesummaryform.action+"&section=servicessummary&subsection=similarservice&next_action="+saveServices+'&business_app_id='+bussAppId;
   	document.servicesummaryform.submit();
}