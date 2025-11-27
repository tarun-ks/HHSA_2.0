<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant" %>

<%if(CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.HP_S028_PAGE, request.getSession())) {%>
  <div class="portletTextBold">HHS Accelerator Homepage</div>
<%} %>
