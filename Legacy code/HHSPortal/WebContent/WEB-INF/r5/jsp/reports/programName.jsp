
							<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
							<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
							<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
							<portlet:defineObjects/>
							<c:set value='<%=portletSession.getAttribute("programNameList")%>' var="programNameList" ></c:set>
							
						 <ul id= "dropdownul" style="max-height: 180px; overflow: auto;">
							 <c:forEach items="${programNameList}" var="programObject">
						         <li class="ddcombo_event data" key="${programObject.programName}" id="li_${programObject.programId}" value="${programObject.programId}">${programObject.programName}</li>
						    </c:forEach>
						</ul>
