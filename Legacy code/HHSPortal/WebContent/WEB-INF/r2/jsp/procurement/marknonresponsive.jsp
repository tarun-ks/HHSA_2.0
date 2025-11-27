<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib prefix="st" uri="/WEB-INF/tld/grid-taglib.tld"%>
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/marknonresponsive.js"></script>
<portlet:defineObjects />

<portlet:actionURL var="markNonResponsive" id ="markNonResponsive" escapeXml="false">
<portlet:param name="action" value="propEval"/>
	<portlet:param name="submit_action" value="markProposalNonResponsive"/>
</portlet:actionURL>

<form:form id="viewResponseForm" name="nonResponseForm" action="${markNonResponsive}" method ="post" commandName="AuthenticationBean">
	<input type="hidden" id="procurementId" value="${procurementId}" />
	<div class='hr'></div>
				
					<div class="content">
						<div class='tabularContainer'>
						<h3>Mark Non-Responsive</h3>
							<div class='hr'></div>
							<c:if test="${message ne null}">
								<div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" onclick="showMe('messagediv', this)"></div>
							</c:if>
							<div id="retractProposal">
									<p>Are you sure you want to mark the following Proposal as Non-Responsive?</p>	
										<p>All Proposals marked Non-Responsive will not be sent to evaluators for review and will be disqualified from competing in this Procurement's competition pool.
								    	</p>				
								<div class="buttonholder">
							    	<input type="button" class="graybtutton"  id="cancel" value="Cancel" onclick="retractOverLay();" />
							    	<input type="submit" class="redbtutton"  id="yesMarkNonResponsive" value="Yes, Mark Non-Responsive"/>
							    </div>
							</div>
						</div>			
					</div>
</form:form>

