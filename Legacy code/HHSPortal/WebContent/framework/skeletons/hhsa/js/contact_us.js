//This function launches the contact us popup on click of contact us link
function contactUsClick(){
		pageGreyOut();
		var pageW = $(document).width();
		var pageH = $(document).height(); 
		     $(".overlay").show();
			 $(".alert-box-contact").show();
			 $(".overlay").width(pageW);
			 $(".overlay").height(pageH);
			 
      var v_parameter="";
    	var urlAppender = $("#contextPathSession").val()+ '/ContactUsServlet.jsp';
         jQuery.ajax({
         type: "POST",
         url: urlAppender,
         data: v_parameter,
         success: function(e){
               $("#contactDiv").empty();
			   $("#contactDiv").html(e);
			   removePageGreyOut();
            },
            beforeSend: function(){  
            },
            complete: function(){  
                $("#helpButton").hide();
            }
          });
}

//This function launches the contact us popup on click of contact us link from help page
function contactUsClickFromHelp(helpCategory){
	pageGreyOut();
	$(".alert-box-help").hide();
	var urlAppender = $("#contextPathSession").val()+"/ContactUsServlet.jsp?helpCategory="+helpCategory;
     jQuery.ajax({
     type: "POST",
     url: urlAppender,
     data: "",
     success: function(e){
           $("#contactDiv").empty();
		   $("#contactDiv").html(e);
		   removePageGreyOut();
		   $("").launchOverlay($(".alert-box-contact"), $(".exit-panel"));
        },
        beforeSend: function(){  
        
        },
        complete: function(){  
            $("#cancelbutton").hide();
        }
      });
}

//This function call contactus Servlet based upon help category
function pageSpecificHelp(helpCategory){
	 pageGreyOut();
	 var urlAppender = $("#contextPathSession").val()+"/ContactUsServlet.jsp?action=helpPage&helpCategory="+helpCategory;
	   jQuery.ajax({
	   type: "POST",
	   url: urlAppender,
	   data: "",
	   success: function(e){
		   	$("#helpPageDiv").empty();
			$("#helpPageDiv").html(e);
			$(".overlay").launchOverlay($(".alert-box-help"), $(".exit-panel"));
			removePageGreyOut();
	      },
	       beforeSend: function(){  
	  	}
	  });
}

//This function is used to view document based upon document id and document name
function viewDocument(documentId, documentName){
	window.open($("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+documentName);
}

//This function is used to view document based upon document type and document name
function viewDocumentByType(documentId, documentName){
	window.open($("#contextPathSession").val()+"/GetContent.jsp?action=displayAppendix&documentType="+documentId+"&documentName="+documentName);
}

//This function is used to view document based upon document id
function viewDocumentTask(documentId,docName){
	var url=$("#contextPathSession").val()+"/GetContent.jsp?action=displayDocument&documentId="+documentId+"&documentName="+docName;
	previewUrl(url,'linkDiv');
}

