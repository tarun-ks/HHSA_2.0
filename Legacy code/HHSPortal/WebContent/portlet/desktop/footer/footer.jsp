<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="com.nyc.hhs.util.CommonUtil"%>
<%@page import="com.nyc.hhs.util.PropertyLoader"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="java.util.List, com.nyc.hhs.model.AutoSaveBean" %>
<%
	String lsRemoveMenu = request.getParameter("removeMenu");
	Date loTodaysDate = new Date();
	Calendar loCalendar = Calendar.getInstance();
	loCalendar.setTime(loTodaysDate);
	if(lsRemoveMenu == null){
%>

<%--Added for Autosave Release 5--%> 
<%
	List<AutoSaveBean> loBeanlist = (List) session.getAttribute("aopAutoSaveList");
	if(null != loBeanlist)
	{
		for(AutoSaveBean loAutoSaveBean : loBeanlist)
		{
			%>
			<script>
				var autoSaveData = {};
				autoSaveData.name = "<%=loAutoSaveBean.getTextareaName()%>";
				autoSaveData.value = "<%=loAutoSaveBean.getTextareaValue()%>";
				autoSaveDataTempArray.push(autoSaveData);
			</script>
			<% 
		}
	}
%>
<div class="footer">
	<div class="copyright">Copyright <%=loCalendar.get(Calendar.YEAR)%> The City of New York
		<% if (null!=CommonUtil.buildConstant() && !"".equalsIgnoreCase(CommonUtil.buildConstant())){ %>
		   <label><%=CommonUtil.buildConstant()%></label>
		  <%}%>
	</div>
	<div class="fotterlinks">
		<ul>
			<li><a href="http://www.nyc.gov/faqs" target="_blank" title="FAQ">FAQ</a></li>
			<li><a href="http://www.nyc.gov/privacy" target="_blank" title="Privacy Statement">Privacy Statement</a></li>
			<li class='nobdr'><a href="http://www.nyc.gov/sitemap" target="_blank" title="Site Map">Site Map</a></li>
		</ul>
	</div>
</div>
<%} %>