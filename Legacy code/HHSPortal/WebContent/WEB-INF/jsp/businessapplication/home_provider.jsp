<!--This jsp will include all other screen for organization profile like Basics, language, members and users etc.-->
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<portlet:defineObjects />
<script type="text/javascript">
function getmycookie(myname)
// this function is called by the function mydefaultsize()
// this function merely looks for any previously set cookie and then returns its value
{
	// if any cookies have been stored then
	if (document.cookie.length>0)
	  {
		  // where does our cookie begin its existence within the array of cookies  
		  mystart=document.cookie.indexOf(myname + "=");
		  // if we found our cookie name within the array then
		  if (mystart!=-1)
		    {
		  		// lets move to the end of the name thus the beginning of the value
		  		// the '+1' grabs the '=' symbol also
		    	mystart=mystart + myname.length+1;
				// because our document is only storing a single cookie, the end of the cookie is found easily
		    	myend=document.cookie.length;
				// return the value of the cookie which exists after the cookie name and before the end of the cookie
	    		return document.cookie.substring(mystart,myend);
		    }
	  }
	// if we didn't find a cookie then return nothing  
	return "";
}

function mydefaultsize(){
	// this function is called by the body onload event
	// this function is used by all sub pages visited by the user after the main page
	var div = document.getElementById("mymain");
	// call the function getmycookie() and pass it the name of the cookie we are searching for
	// if we found the cookie then
		if (getmycookie("mysize")>0)
		{
			// apply the text size change	
			div.style.fontSize = getmycookie("mysize") + "px";
		}
}
</script>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/framework/skeletons/hhsa/js/calendar.js"></script>


<portlet:defineObjects />
<script type="text/javascript">
		var actionUrl  = '<portlet:renderURL/>'+"&section="+'basics'+"&subsection="+'${subsection }'+"&forUpdate="+'${forUpdate}' ;
		function submitForm(anchor){
			$('#'+anchor).attr("href", actionUrl) ;
	}
		var lastDataArray = new Array();
		$(function(){
			$("a[id!='smallA'][id!='mediumA'][id!='largeA']").click(function(e) {
				if($("#tabs-container").size() > 0
						&& !$(this).hasClass("byPassLink")
						&& ($(this).parents("#tabs-container").length == 0 || $(this).attr("id") == "returnSummaryPage")){
					var $self=$(this);
					var isSame = true;
					if(lastDataArray != null && lastDataArray.length > 0){
						$.each(lastDataArray, function(i) {
							if(!$(lastDataArray[i][1]).compare($("form[name='"+lastDataArray[i][0]+"']").serializeArray())){
								isSame = false;
							}
						});
					}
					if(!isSame && lastDataArray != null & lastDataArray.length > 0){
						e.preventDefault();
						$('<div id="dialogBox"></div>').appendTo('body')
						.html('<div><h6>You have unsaved data. <br />If you would like to leave this screen without saving your data, click <b>OK</b>. <br/>If you would like to save your data, click <b>Cancel</b> and save your data.</h6></div>')
						.dialog({
							modal: true, title: 'Unsaved Data', zIndex: 10000, autoOpen: true,
							width: 'auto', modal: true, resizable: false, draggable:false,
							dialogClass: 'dialogButtons',
							buttons: {
								OK: function () {
									//Start R5: UX module, clean AutoSave Data
									deleteAutoSaveData();
									//End R5: UX module, clean AutoSave Data
									document.location = $self.attr('href');
									$(this).dialog("close");
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
					}
				}
			});
		});
		
		$(window).load(function(){
			var ignoreForms = ["myinboxform", "myTaskMform"];
			$("form").each(function(){
				if(typeof($(this).attr("name")) != "undefined" && $.inArray($(this).attr("name"), ignoreForms) < 0){
					lastDataArray[lastDataArray.length] = new Array($(this).attr("name"), $(this).serializeArray());
				}
			});
		});
	</script>		
<%
	String lsMenu="";
	if(renderRequest.getAttribute(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU) != null){
		lsMenu = (String)renderRequest.getAttribute(ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_MENU);
	}
	String lsFilePath = "";
	if(renderRequest.getAttribute("fileToInclude") != null){
		lsFilePath = (String) renderRequest.getAttribute("fileToInclude");
	}
	if(renderRequest.getAttribute("filePathForDocumentList") != null){
		lsFilePath = (String) renderRequest.getAttribute("filePathForDocumentList");
	}

 %>
<h2>Organization Information</h2>
<br/>
<div class="clear"></div>
 		<div class="customtabs">
 			<ul>
					<li><a id="subsection_questions" title="Basics" href="<portlet:renderURL><portlet:param name="action" value="orgBasicInformation" /><portlet:param name="section" value="basics" /><portlet:param name="app_menu_name" value="header_organization_information" /><portlet:param name="subsection" value="questions" /><portlet:param name="next_action" value="showquestion" /><portlet:param name="fb_formName" value="OrgProfile" /></portlet:renderURL>">Basics</a></li>
					<li><a id="subsection_geography" title="Geography" href='<portlet:renderURL><portlet:param name="action" value="orgBasicInformation" /><portlet:param name="section" value="basics" /><portlet:param name="app_menu_name" value="header_organization_information" /><portlet:param name="subsection" value="geography"/><portlet:param name="next_action" value="open" /></portlet:renderURL>'>Geography</a></li>
					<li><a id="subsection_languages" title="Languages" href='<portlet:renderURL><portlet:param name="action" value="orgBasicInformation" /><portlet:param name="section" value="basics" /><portlet:param name="app_menu_name" value="header_organization_information" /><portlet:param name="subsection" value="languages"/><portlet:param name="next_action" value="open" /></portlet:renderURL>'>Languages</a></li>
					<li><a id="subsection_populations" title="Population" href="<portlet:renderURL><portlet:param name="action" value="orgBasicInformation" /><portlet:param name="section" value="basics" /><portlet:param name="app_menu_name" value="header_organization_information" /><portlet:param name="subsection" value="populations"/><portlet:param name="next_action" value="open" /></portlet:renderURL>" >Population</a></li>
					<!-- Updated in 3.1.0. Added check for Defect 6346, adding a identifier for Tab Access from main header.-->
					<li><a id="subsection_memberandusers" title="Members & Users" href="<portlet:renderURL><portlet:param name="action" value="manageMembers" /><portlet:param name="section" value="basics" /><portlet:param name="app_menu_name" value="header_organization_information" /><portlet:param name="subsection" value="memberandusers"/><portlet:param name="next_action" value="displayOrgMember" /><portlet:param name="isHeaderTab" value="isHeaderTab" /></portlet:renderURL>" >Members & Users</a></li>
			</ul>
	   </div>
   		<script type="text/javascript">
			showSelectedForProvider('basics','${subsection }');
		</script>
   		<div id="tabs-container">
			<!-- Form Data Starts -->
			<div id="mymain">
				<div class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="pageSpecificHelp('Organization Information');"></a></div>
				<jsp:include page="<%=lsFilePath%>"></jsp:include>
			</div>
		</div>
		<div class="overlay"></div>
		<div class="alert-box-help">
	       <div class="content">
	             <div id="newTabs" class='wizardTabs'>
	                   <div class="tabularCustomHead">Organization Information - Help Documents</div>
	             <div id="helpPageDiv"></div>
	             </div>
	       </div>
		   <a  href="javascript:void(0);" class="exit-panel">&nbsp;</a>
		</div>
		<div class="alert-box-contact">
			<div class="content">
				<div id="newTabs">
					<div id="contactDiv"></div>
				</div>
			</div>
		</div>
