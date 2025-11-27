<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.ArrayList,java.util.Iterator,com.nyc.hhs.model.Document, com.nyc.hhs.constants.ApplicationConstants"%>

<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<%@ page errorPage="/error/errorpage.jsp"%>

<form name="helppageform" method="post">
	<div id="helpPopup" class='pad6 clear'>
		<div class="messagedivover" id="messagedivover"></div>
		<h3>Help Documents</h3>
		<div class="helppopupScroll" >
			<%
			ArrayList<Document> helpList = (ArrayList<Document>)request.getAttribute("helpList");
			if(null != helpList){
				Iterator iter = helpList.iterator();
				while(iter.hasNext()){
					Document document = (Document)iter.next();
					%>
					<ol class="helpDocPop">
						<li><a href="#" onClick="javascript:viewDocument('<%=document.getDocumentId()%>','<%=document.getDocName()%>')"><%=document.getDocName()%></a>
							<div><%=document.getDocumentDescription()%></div>
						</li>
					</ol>
					<%	
				}
			}
			if(!ApplicationConstants.CITY_ORG.trim().equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE).toString().trim()) &&
					!ApplicationConstants.AGENCY_ORG.trim().equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE).toString().trim())){ 
			%>
			<!-- Start QC 9587 R 8.10.0 Remove Contact Us link
			</div>
			<h3>Contact Us</h3>
			<div class="leftPaddingContact">If you have a question or feedback about this screen, please click the link below:
				<div><a href="#" class='link' title="Contact Us" onclick="contactUsClickFromHelp('${helpCategory}');"><b>Contact Us</b></a></div>
			</div>
				-->
			<div>
				<!--[Start] Update Language      R9.6.1 QC9693	-->			
				If you need assistance, please visit <a  href="https://www.nyc.gov/mocshelp"  target="_blank" style="color:#5077AA;">www.nyc.gov/mocshelp.</a>
				<!-- [End] Update Language      R9.6.1 QC9693	-->
			</div>
			<!-- 
			End QC 9587 R 8.10.0 Remove Contact Us link
			-->
		<%}%>
	</div>
	<input type=hidden value="${helpCategory}" id="helpCategoryHidden"/>
</form>

<script type="text/javascript">
//Below function is for message div display
function onReady(){
	if("null" != '<%= request.getAttribute("message")%>'){
		$(".messagedivover").html('<%= request.getAttribute("message")%>'+"<img src=\"../framework/skins/hhsa/images/iconClose.jpg\" id=\"box\" class=\"message-close\" title=\"Close\" alt=\"Close\" onclick=\"showMe('messagedivover', this)\" />");
		$(".messagedivover").addClass('<%= request.getAttribute("messageType")%>');
		$(".messagedivover").show();
	}
}
</script>



