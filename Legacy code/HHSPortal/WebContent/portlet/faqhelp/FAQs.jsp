<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="com.nyc.hhs.constants.ApplicationConstants"%>
<%@page import="java.util.Iterator"%>
<%@ page import="com.nyc.hhs.model.FaqFormBean"%>
<%@ page errorPage="/error/errorpage.jsp"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<style type="text/css">
	h3 a {
		text-decoration: none !important;
		font-weight: bold;
		color: #333 !important;
	}
</style>
<script type="text/javascript" src="../js/faq.js"></script>

<% String lsNavigateFrom = null;
	if (renderRequest.getAttribute("previewPage") != null) {
			lsNavigateFrom = (String)renderRequest.getAttribute("previewPage");
	}
%>
<!-- Body Wrapper Start -->
<form name="faqform" method="post" action="<portlet:actionURL/>">
<input type="hidden" id="publishAction" name="publishAction" value="" />
	<div class='clear'>
	<!-- Body Container Starts -->
	<!-- Start QC 9587 R 8.10.0 Remove Contact Us link 
		
		<div class="floatRht" style='margin-top: -40px;'> 
		End QC 9587 R 8.10.0 Remove Contact Us link
		-->
		
		<div>
			<% 
			if(!"previewPage".equalsIgnoreCase(lsNavigateFrom)){
		          if( !ApplicationConstants.CITY_ORG.trim().equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE).toString().trim())&&
		        			!ApplicationConstants.AGENCY_ORG.trim().equalsIgnoreCase(session.getAttribute(ApplicationConstants.KEY_SESSION_ORG_TYPE).toString().trim())) {%>
				<!-- Start QC 9587 R 8.10.0 Remove Contact Us link 
				<div class="iconContact"><a href="javascript:void(0);"
				class="terms" title="Contact Us" onclick="contactUsClick();">Contact
				Us</a></div>
				-->
				<div>
				<!--[Start] Update Language      R9.6.1 QC9693	-->
				If you need assistance, please visit <a  href="https://www.nyc.gov/mocshelp"  target="_blank" style="color:#5077AA;">www.nyc.gov/mocshelp.</a>
				</div>
				<!-- [End] Update Language      R9.6.1 QC9693	-->
				
				<!-- 
				End QC 9587 R 8.10.0 Remove Contact Us link
				-->
			<%}}else {%>
				<div class="linkEdit"><input type="button" class='button'
				value="Publish" title="Publish" id="publish" title="publish"
				onclick="publishClick();" /></div>
			<%} %>
		</div>
		
		<h2>Frequently Asked Questions</h2>
		
		<%
		List<FaqFormBean>  FAQList ;
		Map<Integer,List<FaqFormBean>> FAQMap = new LinkedHashMap();
		//HashMap<Integer,List<FaqFormBean>>  FAQMap ;
		FAQMap = (LinkedHashMap<Integer,List<FaqFormBean>>)renderRequest.getAttribute("loQuestionListMap1");
		if(FAQMap!=null && !FAQMap.isEmpty()){
			Set keys = FAQMap.entrySet();
			Iterator i = keys.iterator();      
			while(i.hasNext()) { 
				Map.Entry me = (Map.Entry)i.next();     
			    List key = (List) me.getValue(); 
			    int counter =0;  
			    int quesCounter =1;  
			    for(int j=0; j<key.size(); j++){
					FaqFormBean bean = (FaqFormBean)key.get(j); 
		   			%> <%
		            if(counter ==0){
						%>
						<div class="hr"></div>
						<h3><%=bean.getMsTopic()%></h3>
						<% 
			        	counter++;
			        	}
			        	%>
					<ul class="helpLinks">
						<li><a href="#<%=bean.getMsQuestionId()%>"><%=bean.getMsQuestion()%></a></li>
					</ul>
					<%
					quesCounter++;
					}
			}
		}
		%> <%
		List<FaqFormBean>  FAQList1 ;
		//    HashMap<Integer,List<FaqFormBean>>  FAQMap1 ;
		Map<Integer,List<FaqFormBean>> FAQMap1 = new LinkedHashMap();
		FAQMap1 = (LinkedHashMap<Integer,List<FaqFormBean>>)renderRequest.getAttribute("loQuestionListMap1");
		if(FAQMap!=null && !FAQMap.isEmpty()){
			Set keys1 = FAQMap1.entrySet();
			Iterator i = keys1.iterator();      
			while(i.hasNext()) { 
				Map.Entry me = (Map.Entry)i.next();     
			    List key1 = (List) me.getValue(); 
			    int counter =0;  
			    int quesCounter1 =1;  
				for(int j=0; j<key1.size(); j++){
					FaqFormBean bean1 = (FaqFormBean)key1.get(j);
			       	if(counter ==0){
			        	%>
	   			     	
						<div class="tabularCustomHead"><%=bean1.getMsTopic()%></div>
						<% 
					    counter++;
					}
					    %>
					 <div class='tabularWrapper'>
						<ol class="helpWrapper">
						<li>
							<h3 class='floatLft'><a name="<%=bean1.getMsQuestionId()%>">Q. <%=bean1.getMsQuestion()%></a></h3>
							<div class='pad6'>&nbsp;</div>
							<div class=''><b class='floatLft'>A.&nbsp;</b> <%=bean1.getMsAnswer()%></div>
						
							<div class="topAnchor"><a title="Back to Top" href="#top">Back to Top</a></div>
							<div class='hr'></div>
						</li>
			           </ol>
		           </div>
					<%quesCounter1++; 
				}
			}%>
		            <%
		}%>
	</div>
</form>
<script type="text/javascript">
	//jquery ready function- executes the script after page loading
	$(document).ready(function() {
		$("a.exit-panel").click(function() {
			$(".alert-box-contact").hide();
			$(".overlay").hide();
		});
	});

	//Below function is called when user clicks on publish button
	function publishClick() {
		document.getElementById("publishAction").value = userType;
		//document.forms[0].submit();
		document.faqform.submit();
		//document.getElementById("faqform").submit();
	}
</script>
