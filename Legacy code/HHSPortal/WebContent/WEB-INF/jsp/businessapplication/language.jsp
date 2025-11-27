<!-- This page is displayed when a user click on save and next button on  basic's geograpgy screen.
It will display list of corresponding languages from which a user can select by selecting the checkboxes .-->
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<style type="text/css">
#newTabs h5 {
	background: #E4E4E4;
	color: #5077AA;
	font-size: 13px;
	font-weight: bold;
	padding: 6px 0px 6px 6px;
}

.langCol3 ul li {
	list-style-type: none;
	padding: 5px;
}

.langCol3 ul li input {
	margin-top: 10px;
}
.RemoveOne, .AddOne{
	width: 90px !important;
}
</style>

<script type="text/javascript">
var lastData = null;
//This method is used to transfer selected language in first selectio box to second selection box
    $(document).ready(
        function () {
        	if($("#saveAndNextButtonId").size()>0){
        		$("#buttonName").html("Save and Next");
        	}else{
        		$("#buttonName").html("Save");
        	}
        	// start if other is checked then display the langCol2
        	 list2= document.getElementById("list2");
     		if(list2.options.length != 0)
     		{
     		document.getElementById("fldcheckbox").checked=true;
     		document.getElementById("langCol2").style.display = "";
     		}
     		
     		<%	String other_checked=null;
     			if(renderRequest.getAttribute("other_checked") != null){
     				other_checked=(String)renderRequest.getAttribute("other_checked");
     			if(other_checked.equalsIgnoreCase("other_checked")){
     			%>
     				document.getElementById("fldcheckbox").checked=true;
     				document.getElementById("langCol2").style.display = "";
     			<%}}%>
     			
     			<%	String language_interpretation_services=null;
     			if(renderRequest.getAttribute("language_interpretation_services") != null){
     				other_checked=(String)renderRequest.getAttribute("language_interpretation_services");
     			if(other_checked.equalsIgnoreCase("language_interpretation_services")){
     			%>
     				document.getElementById("language_interpretation_services").checked=true;
     			<%}}%>
     			
     			<%String onLoadDisabled=""; 
     			Boolean isSubmitted=false; 
     			if(false){onLoadDisabled="disabled";
     			%>
     				document.getElementById("otherReadOnly").style.display = "";
     				document.getElementById("langCol2").style.display = "none";
     			<%} %>
     		// End
        	$('#btnAdd').attr('disabled', 'disabled');
        	$('#btnRemove').attr('disabled', 'disabled');
        	if($('#list2 option').length==0){
        		$('#btnRemove').attr('disabled', 'disabled');
        	}
        	$(function(){
        		$("#list1").change(function(){
        		var selectedValue = $(this).find(":selected").val();
        		if(selectedValue != null){
        			$('#btnAdd').removeAttr('disabled');
        		}
        		});
        		});
        	$(function(){
        		$("#list2").change(function(){
        		var selectedValue = $(this).find(":selected").val();
        		if(selectedValue != null){
        			$('#btnRemove').removeAttr('disabled');
        		}
        		});
        		});
            $('#btnAdd').click(
                function (e) {
                    $('#list1 > option:selected').appendTo('#list2');
                    $("#list2 option").removeAttr('selected');
                    
                    if($('#list2 option').length!=0){
                    $('#btnRemove').removeAttr('disabled');}
                    
                    if($('#list1 option').length==0){
                    	$('#btnAdd').attr('disabled', 'disabled');
                    }
                    if(typeof($("#list1").find(":selected").val())=='undefined'){
                    	$('#btnAdd').attr('disabled', 'disabled');
                	}
                    if(typeof($("#list2").find(":selected").val())=='undefined'){
                    	$('#btnRemove').attr('disabled', '');
                	}
                    e.preventDefault();
                });

            $('#btnRemove').click(
            function (e) {
                $('#list2 > option:selected').appendTo('#list1');
                $("#list1 option").removeAttr('selected');
                if($('#list1 option').length!=0){
                $('#btnAdd').removeAttr('disabled');}
                if($('#list2 option').length==0){
            		$('#btnRemove').attr('disabled', 'disabled');
            	}
                if(typeof($("#list1").find(":selected").val())=='undefined'){
                	$('#btnAdd').attr('disabled', 'disabled');
            	}
                if(typeof($("#list2").find(":selected").val())=='undefined'){
                	$('#btnRemove').attr('disabled', '');
            	}
                e.preventDefault();
            });
            var $form = $("#Languages").closest('form');
			lastData = $form.serializeArray();
	});
    //Submit the language if it passes all the validation rule. 
    function selectAllAndSubmit(list2,selectAll,pageToDirect) {
	    // have we been passed an ID
	    if (typeof list2 == "string") {
	       list2= document.getElementById(list2);
	    }
	
	    // is the select box a multiple select box?
	    if (list2.type == "select-multiple") {
	        for (var i = 0; i < list2.options.length; i++) {
	            list2.options[i].selected = selectAll;
	        }
	    }
	    if(document.getElementById("fldcheckbox").checked==true){
	    	document.languageform.other_checked.value="other_checked";
	    }
	    if(document.getElementById("language_interpretation_services").checked==true){
	        document.languageform.language_interpretation_services.value="language_interpretation_services";
	        }
	    
    	document.languageform.next_action.value=pageToDirect;
    	document.languageform.action = document.languageform.action+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
    	document.languageform.submit();
	}
//back button functionality on language screen
	function back(pageToRedirect){
		var $self=$(this);
		var $form = $("#Languages").closest('form');
		var isSame = false;
		data = $form.serializeArray();
		if(lastData != null){
			if($(lastData).compare($(data))){
				isSame = true;
			}
		}
		if(!isSame && lastData != null){
			$('<div id="dialogBox"></div>').appendTo('body')
			.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
			.dialog({
				modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
				width: 'auto', modal: true, resizable: false, draggable:false,
				dialogClass: 'dialogButtons',
				buttons: {
					OK: function () {
						if(pageToRedirect != 'refresh'){
						document.languageform.action = document.languageform.action+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&next_action=back&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
					    document.languageform.submit();
						$(this).dialog("close");
						}
						else{
							location.href=$("#contextPathSession").val()+"/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_21&_urlType=render&wlpportletInstance_21_next_action=open&wlpportletInstance_21_app_menu_name=header_organization_information&wlpportletInstance_21_subsection=languages&wlpportletInstance_21_action=orgBasicInformation&wlpportletInstance_21_section=basics";
						}
					},
					Cancel: function () {
						$(this).dialog("close");
					}
				},
				close: function (event, ui) {
					$(this).remove();
				}
			});
			$("div.dialogButtons div button:nth-child(2)").find("span").addClass("graybtutton");
		}else{
			if(pageToRedirect != 'refresh'){
			document.languageform.action = document.languageform.action+"&business_app_id="+'<%=renderRequest.getAttribute("business_app_id")%>'+"&next_action=back&section="+'<%=renderRequest.getAttribute("section")%>'+"&subsection="+'<%=renderRequest.getAttribute("subsection")%>';
		    document.languageform.submit();
		}
			else{
				location.href=$("#contextPathSession").val()+"/portal/hhsweb.portal?_nfpb=true&_st=&_windowLabel=portletInstance_21&_urlType=render&wlpportletInstance_21_next_action=open&wlpportletInstance_21_app_menu_name=header_organization_information&wlpportletInstance_21_subsection=languages&wlpportletInstance_21_action=orgBasicInformation&wlpportletInstance_21_section=basics";
			}
		}
	
		
	}
	// checking for others lanague
	function fnchecked(blnchecked)
	{
		if(blnchecked)
		{
			document.getElementById("langCol2").style.display = "";
		} 
		else
		{ 	//when others is unchecked
			var list2;
			var list1;
			list2= document.getElementById("list2");
			list1= document.getElementById("list1");
		    // is the select box a multiple select box?
		    if (list2.type == "select-multiple") {
		        for (var i = 0; i < list2.options.length; i++) {
		            list2.options[i].selected = true;
		        }
		    }
	
		$('#list2 > option:selected').appendTo('#list1');
	   	if (list1.type == "select-multiple") {
	        for (var i = 0; i < list1.options.length; i++) {
	            list1.options[i].selected = false;
	        }
	    }
	
		document.getElementById("langCol2").style.display = "none";
		}
	}
</script>

<%
	ArrayList<TaxonomyTree> loTaxonomyList = new ArrayList<TaxonomyTree>();
	if(renderRequest.getAttribute("TaxonomyList") != null){
		loTaxonomyList =(ArrayList<TaxonomyTree>) renderRequest.getAttribute("TaxonomyList");
	}
 	ArrayList<String> moLanguageIdList= new ArrayList<String>();
	if(renderRequest.getAttribute("moLanguageIdList") != null){
  		moLanguageIdList =(ArrayList<String>) renderRequest.getAttribute("moLanguageIdList");
	 }
	 String lscheckedInter="";
	 ArrayList<String> loLanguageinterpretationList= new ArrayList<String>();
	 if(renderRequest.getAttribute("aoLanguageinterpretationList") != null){
		  loLanguageinterpretationList =(ArrayList<String>) renderRequest.getAttribute("aoLanguageinterpretationList");
		  for(int i=0;i<loLanguageinterpretationList.size();i++){
			 if(loLanguageinterpretationList.get(i)!=null && loLanguageinterpretationList.get(i).equalsIgnoreCase("true")){
				 lscheckedInter="checked";
				 break;
			 }
		  }
		 
	 }
	List<String> moLanguageToDispaly= ApplicationConstants.CHECKBOX_LANGUAGE_DISPLAYED;	
%>
<body>

<% if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.BA_S053_S053R_PAGE, request.getSession())
		//Start : R5 Condition Added
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.TA_S080_PAGE, request.getSession())
		|| CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S100_SECTION, request.getSession())){
		//End : R5 Condition Added
		%>
<form action="<portlet:actionURL/>" method="post" name="languageform">
<input type="hidden" name="next_action" value="" /> <input
	type="hidden" name="other_checked" value="" /> 
<div id="Languages">
<h2>Languages</h2>
<!-- Error display area starts here --> <%  
 if(request.getAttribute("errorToDisplay") != null){
       ArrayList lsErrorMsg = (ArrayList)request.getAttribute("errorToDisplay");
       if(lsErrorMsg.size() >0){
%>
<div class='failedShow'>
	<%
    Iterator errorListIterator = lsErrorMsg.iterator(); 
    while(errorListIterator.hasNext()){
    %>
		<%= errorListIterator.next()%>
	<% }%>
</div>
<%} }%> <!-- Error display area ends here -->
<div title="Language refers to an organization's ability to serve and communicate with clients in their preferred language.">
	<p>In which languages can your organization communicate and provide
	services?</p>
	<p>Please check all that apply. If your organization is able to
	accommodate other languages as well, select "Other," and use the
	add/remove feature. Once complete, click the "<span id='buttonName'></span>" button</p>
</div>
<c:set var="readOnlyValue" value=""></c:set> 
<c:if
	test="${loReadOnlySection}">
	<c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
</c:if>

<div class="clear">
<div class="langCol1">
<ul id="check">
	<li class="langHeader">Languages</li>
	<%      
	String lsOtherElementId = "";
	boolean lsElementUnderOther = false;
    for(int count=0; count<loTaxonomyList.size() ; count++){
       	TaxonomyTree loTaxonomy = loTaxonomyList.get(count);
		if ("Other".equalsIgnoreCase(loTaxonomy.getMsElementName())){
			lsOtherElementId = loTaxonomy.getMsElementid();
		}
	}
	
    for(int displayLangList=0; displayLangList<loTaxonomyList.size() ; displayLangList++){
    
    	TaxonomyTree loTaxonomy = loTaxonomyList.get(displayLangList);
    	
    	lsElementUnderOther = false;
    	String lsBranchId = loTaxonomy.getMsBranchid();
    	String lsBranchIdSplitted[] = lsBranchId.split(",");
    	
    	for(int i=0; i<lsBranchIdSplitted.length; i++){
    		if(lsOtherElementId.equalsIgnoreCase(lsBranchIdSplitted[i])){
				lsElementUnderOther = true;
			}
		}	
    	
		if(!lsElementUnderOther){ 
			if(!loTaxonomy.getMsElementName().contains("In addition to the languages selected above, my organization has access to language interpretation services")){
			    if((moLanguageIdList.contains(loTaxonomy.getMsElementid()))){
			    %>
				<li><input name="language_checkbox" type="checkbox"
					${readOnlyValue}
					value="<%=loTaxonomyList.get(displayLangList).getMsElementid()%>"
					checked="checked" <%=onLoadDisabled%> /><%=loTaxonomyList.get(displayLangList).getMsElementName()%>
				</li>
				<%}
				else{
				%>
					<li><input name="language_checkbox" type="checkbox"
						${readOnlyValue}
						value="<%=loTaxonomyList.get(displayLangList).getMsElementid()%>"
						<%=onLoadDisabled%> /><%=loTaxonomyList.get(displayLangList).getMsElementName()%>
					</li>
				<%
				}
			}
		}
	}
   			%>
		<li><input name="other" onclick="fnchecked(this.checked);"
		${readOnlyValue}
						id="fldcheckbox" type="checkbox"
		<%=onLoadDisabled%> />Other</li>
</ul>



</div>
<div class="langCol2" id="langCol2" style="display: none;">
<p class="langHeader">Other Languages</p>
<div>


<div class="mulselect">
	<select multiple="true" size="6"
		${readOnlyValue}
			class="multiselect" id="list1" name="myselecttsms">
		<% for(int displayLangList=0; displayLangList<loTaxonomyList.size() ; displayLangList++){
	    	TaxonomyTree loTaxonomy = loTaxonomyList.get(displayLangList);
	    	
   	    	lsElementUnderOther = false;
	    	String lsBranchId = loTaxonomy.getMsBranchid();
    		String lsBranchIdSplitted[] = lsBranchId.split(",");
    	
    		for(int i=0; i<lsBranchIdSplitted.length; i++){
    			if(lsOtherElementId.equalsIgnoreCase(lsBranchIdSplitted[i])){
					lsElementUnderOther = true;
				}
			}	
	    	
	    	if(lsElementUnderOther){ 
	    		if(!(moLanguageIdList.contains(loTaxonomy.getMsElementid()))){ %>
				<option
				value="<%=loTaxonomyList.get(displayLangList).getMsElementid()%>"
				rel="0"
				title="<%=loTaxonomyList.get(displayLangList).getMsElementName()%>"><%=loTaxonomyList.get(displayLangList).getMsElementName()%></option>
		<% }}}%>
	
	</select>
</div>
<div class="muloptions" style='text-align: center;'><br />
	<input type="button" title="Add Selected &gt;&gt;" value="Add &gt;&gt;"
		rel="myselect" class="button AddOne" id="btnAdd"></input><br />
	<br />
	<input type="button" title="&lt;&lt; Remove Selected"
		value="&lt;&lt; Remove" rel="myselect"
		class="button redbtutton RemoveOne" id="btnRemove">
	</input>
</div>
<div class="mulselect">
	<select class="multiselect TakeOver"
		${readOnlyValue}
		multiple="multiple" size="6" id="list2"
		name="language_listbox">
		<% for(int displayLangList=0; displayLangList<loTaxonomyList.size() ; displayLangList++){
		    	TaxonomyTree loTaxonomy = loTaxonomyList.get(displayLangList);
		    	
		    	lsElementUnderOther = false;
	    		String lsBranchId = loTaxonomy.getMsBranchid();
	    		String lsBranchIdSplitted[] = lsBranchId.split(",");
	    	
	    		for(int i=0; i<lsBranchIdSplitted.length; i++){
	    			if(lsOtherElementId.equalsIgnoreCase(lsBranchIdSplitted[i])){
						lsElementUnderOther = true;
				}
			}	
	    	
	    	if(lsElementUnderOther){
			    if((moLanguageIdList.contains(loTaxonomy.getMsElementid()))){
			     %>
				<option
					value="<%=loTaxonomyList.get(displayLangList).getMsElementid()%>"
					rel="0"
					title="<%=loTaxonomyList.get(displayLangList).getMsElementName()%>"><%=loTaxonomyList.get(displayLangList).getMsElementName()%></option>
				<% }}}%>
	</select>
</div>


</div>

</div>

</div>

<p class='clear'></p>

<div class="langCol3">
	<ul>
		<li><input type="checkbox"
			${readOnlyValue} id="language_interpretation_services"
			name="language_interpretation_services" <%=lscheckedInter %> />In
			addition to the languages selected above, my organization has access to
			language interpretation services.</li>
	</ul>
</div>
</div>

<c:if test="${!loReadOnlySection}">
	<div class='buttonholder'>
	<c:choose>
		<c:when test="${app_menu_name == 'header_organization_information' }">
			<input type="button" title="Cancel" class="graybtutton"
				value="Cancel" onclick="back('refresh')"  />
			<input type="hidden" name="app_menu_name"
				value="header_organization_information" />
		</c:when>
		<c:otherwise>
			<input type="button" title="&lt;&lt; Back" class='graybtutton' value="&lt;&lt; Back" onclick="back('back')" />
		</c:otherwise>
	</c:choose> <input type="button" class='button' title="Save" value='Save'
		onclick="selectAllAndSubmit('list2',true,'save')" /> 
	<c:if
		test="${app_menu_name != 'header_organization_information' }">
		<input type="button" class='button' title="Save & Next"
			value='Save & Next' id="saveAndNextButtonId"
			onclick="selectAllAndSubmit('list2',true,'save_next')" />
	</c:if>
	</div>
</c:if>
<!-- Container Ends --> <!-- Body Container Ends --> <!-- Body Wrapper End -->

</form>

<% } else {%>
<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</body>