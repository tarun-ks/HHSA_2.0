<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="jq" uri="/WEB-INF/tld/jqgridtag.tld"%>
<%@taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib prefix="task" uri="/WEB-INF/tld/task-taglib.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page import="com.nyc.hhs.util.HHSUtil"%>



    <portlet:resourceURL var='SubGridHeaderRow'
		id='SubGridHeaderRow' escapeXml='false'>
		<portlet:param name="gridLabel" value="advanceContractBudgetSetUp.grid" />
	</portlet:resourceURL>

					
					<portlet:resourceURL var='AdvanceContractBudgetGrid' id='loadGridData' escapeXml='false'>
					<portlet:param name="transactionName" value="getContractBudgetAdvanceGrid"/>
					<portlet:param name="beanName" value="com.nyc.hhs.model.AdvanceSummaryBean"/>
					<portlet:param name="gridLabel" value="advanceContractBudgetSetUp.grid"/>
					<portlet:param name="subBudgetId" value="${subBudgetId}"/>
					</portlet:resourceURL>

				<c:set var="gridColNames"><%=HHSUtil.getHeader("advanceContractBudgetSetUp.grid")%></c:set>
				<c:set var="gridColProp"><%=HHSUtil.getHeaderProp("advanceContractBudgetSetUp.grid")%></c:set>
				<c:set var="subGridColProp"><%=HHSUtil.getSubGridProp("advanceContractBudgetSetUp.grid")%></c:set>

                      
       <jq:grid id="advanceContractBudgetGrid" 
         isReadOnly="true"
         gridColNames="${gridColNames}" 
	     gridColProp="${gridColProp}" 
	     subGridColProp="${subGridColProp}" 
		 gridUrl="${SubGridHeaderRow}" isNewRecordDelete="true"
		 subGridUrl="${AdvanceContractBudgetGrid}"
	     cellUrl="AdvanceContractBudgetGridOperation"
	     editUrl="AdvanceContractBudgetGridOperation"
	     dataType="json" methodType="POST"
	     columnTotalName=""
	     isPagination="true"
	     rowsPerPage="5"
         isSubGrid="true"
         operations="del:false,edit:false,add:false,cancel:false,save:false"
/>

