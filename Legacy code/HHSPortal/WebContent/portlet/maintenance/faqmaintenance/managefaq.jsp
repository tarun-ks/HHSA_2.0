
<%@page import="javax.portlet.RenderRequest"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.PortletSession"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections" %>
<%@page import="java.lang.String"%>
<%@page import="com.nyc.hhs.util.ApplicationSession" %>
<%@page import="com.nyc.hhs.model.FaqFormMasterBean" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ taglib prefix="render" uri="http://www.bea.com/servers/portal/tags/netuix/render" %>
<%@ page import="com.bea.netuix.servlets.controls.page.PagePresentationContext,
	com.bea.netuix.servlets.controls.page.BookPresentationContext"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant"%>
<portlet:defineObjects />
<script type="text/javascript" src="../js/faq.js"></script>

<form method="post" action="<portlet:actionURL/>">
	<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.FQ_S098_PAGE, request.getSession())) {%>
		<a style="visibility:hidden" id="providerHref" href="#" onclick="window.open('<render:standalonePortletUrl portletUri='/portlet/faqhelp/faqportlet.portlet'><render:param name='action' value='preview' /><render:param name='userType'  value='provider' /></render:standalonePortletUrl>','_blank','scrollbars, resizable, width=1000, height=580,left=0, top=0',true); return false;"></a>   
		<a style="visibility:hidden" id="agencyHref" href="#" onclick="window.open('<render:standalonePortletUrl portletUri='/portlet/faqhelp/faqportlet.portlet'><render:param name='action' value='preview' /><render:param name='userType'  value='agency' /></render:standalonePortletUrl>','_blank','scrollbars, resizable,  width=1000, height=580, left=0, top=0',true); return false;"></a>   
		<div class="container">
		 <%
	  String lsTransactionMsg = "";
	  if (null!=request.getAttribute("transactionMessage")){ 
		  lsTransactionMsg = (String)request.getAttribute("transactionMessage");
	  }
		  if(null!=request.getAttribute("transactionStatus") && "passed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
		  		<div id="transactionStatusDiv" class="passed" style="display:block" ><%=lsTransactionMsg%> </div>
		  <%}else if(( null != request.getAttribute("transactionStatus") ) && "failed".equalsIgnoreCase((String)request.getAttribute("transactionStatus"))){%>
	      		<div id="transactionStatusDiv" class="failed" style="display:block" ><%=lsTransactionMsg%> </div>
		  <%}%>
        <%if(null!=renderRequest.getPortletSession().getAttribute("publish", PortletSession.APPLICATION_SCOPE)){ 
	      	String lsPublishMsg = (String)renderRequest.getPortletSession().getAttribute("publish", PortletSession.APPLICATION_SCOPE);
	      	renderRequest.getPortletSession().setAttribute("publish",null, PortletSession.APPLICATION_SCOPE);
	      	renderRequest.getPortletSession().removeAttribute("publish", PortletSession.APPLICATION_SCOPE);
	      	String[] loArray = lsPublishMsg.split(":");
	      	String lsPublishStatus= loArray[0];
	      	String lsUserType= loArray[1];
	      	if("success".equalsIgnoreCase(lsPublishStatus)){
      		%>
		    	<div class='passed' style="display:block" id="messagediv">The FAQ page has been published for <%=loArray[1]%>  users to view with your changes</div>
   			<%}else {%>
   				<div class='failed' style="display:block" id="messagediv">The FAQ page has not been published for <%=loArray[1]%>  users to view with your changes</div>
   			<% }} %>   
        
		<h2>Provider FAQ Maintenance Page</h2>
        <div class="hr"></div>
        <p> 
        <h3 class="floatLft">Help Topics</h3> 
        <div class="taskButtons">&nbsp;&nbsp;<input type="submit" class="add" name="button" value="Add Topic" title="Add Topic" onclick="setValue('provideraddclikd')" />
	        <input type="button" id="PreviewId" style='background:#f5f5f5' name="PriviewId" value="Preview FAQ" title="Preview FAQ" onclick="navigateToProviderFaqSummary('provider');"/>
        </div>
        <input type="hidden" name="submitButtonValue" value="" id="saveServices">   
		<div class="clear"></div>
        <ul id="provider">
        <% 
	        List <FaqFormMasterBean> topicListt = (List <FaqFormMasterBean>)request.getAttribute("topicListMaster");
	        if(topicListt !=null && !topicListt.isEmpty()){
	           Iterator topicIterator = topicListt.iterator();
	           int counter = 0;
			   while(topicIterator.hasNext()){
	           		FaqFormMasterBean lsformBean=(FaqFormMasterBean)topicIterator.next();
	                String lsTopicName = lsformBean.getMsTopicName();
	                String lsType = lsformBean.getMsType();
	                int liTopicId = lsformBean.getMiTopicId();
	                String lsTopicId = String.valueOf(liTopicId);
	                if (lsType.equalsIgnoreCase("provider")){                  
	                	counter = counter + 1;
	         			%>
	   					<li> <a  class="link" title="<%=lsTopicName%>" href="<portlet:actionURL><portlet:param name="topicId" value="<%=lsTopicId%>"  />
	        	         </portlet:actionURL>"> <%=lsTopicName%>  </a></li>  
		       			<%
		                }
		 	   }
	       }       
	       %>   
		</ul>  
       	</p>
       	<p> 
        <h2>Agency FAQ Maintenance Page</h2>
	    <div class="hr"></div>
    	<h3 class="floatLft">Help Topics</h3> 
       	<div class="taskButtons">&nbsp;&nbsp;<input type="submit" class="add" name="button" value="Add Topic" title="Add Topic" onclick="setValue('agencyaddclikd')" />
       		<input type="button" style='background:#f5f5f5' id="PriviewId1" name="PriviewId1" value="Preview FAQ" title="Preview FAQ" onclick="navigateToAgencyFaqSummary('agency');" />
       	</div>
        <div class="clear"></div>
        <ul id="agency">         
        	<% 
        	List <FaqFormMasterBean> topicListt1 = (List <FaqFormMasterBean>)request.getAttribute("topicListMaster");
   		  	if(topicListt1 !=null && !topicListt1.isEmpty()){
	       	  	Iterator topicIterator = topicListt1.iterator();
	       		int counter = 0;
	           	while(topicIterator.hasNext()){
	               	FaqFormMasterBean lsformBean=(FaqFormMasterBean)topicIterator.next();
	               	String lsTopicName = lsformBean.getMsTopicName();
	               	int liTopicId = lsformBean.getMiTopicId();
	               	String lsTopicId = String.valueOf(liTopicId);
	               	String lsType = lsformBean.getMsType();
               		if (lsType.equalsIgnoreCase("agency")){                  
     	             	counter = counter + 1;
       					%>
           	        	<li><a class="link" title="<%=lsTopicName%>" href="<portlet:actionURL><portlet:param name="topicId" value="<%=lsTopicId%>"/>
       	      			</portlet:actionURL>"> <%=lsTopicName%>  </a></li>                 	       
        				<%
                	}
            	}
          	 }       
         	%>   
   	  	</ul>  
        </p>
		</div>

	<%}else{ %>
		<h2>You are not authorized to view this page. Please contact your organization Administrator to request additional permissions.</h2>
	<%} %>
</form>
