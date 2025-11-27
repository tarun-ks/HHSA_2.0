<%-- This jsp used to display the internal and external user 
       Agency user add and delete the selected agency from this page for the evaluation
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" uri="/WEB-INF/tld/navigationSM.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="d" uri="/WEB-INF/tld/contentdisplay-taglib.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.nyc.hhs.constants.HHSComponentMappingConstant"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@ page import="com.nyc.hhs.frameworks.grid.*,com.nyc.hhs.constants.*, com.nyc.hhs.util.CommonUtil"%>

<fmt:setBundle basename="com/nyc/hhs/properties/messages"/>
<portlet:defineObjects />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/r2/js/evaluationsettings.js"></script>
<%-- Navigation Tag Starts --%>


<nav:navigationSM screenName="EvaluationSettings">
<%-- Save Evaluation Detail Action URL Begins --%>
<portlet:actionURL var="saveEvaluationDetailsUrl" escapeXml="false">
       <portlet:param name="action" value="propEval"/>
       <portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
       <portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
       <portlet:param name="submit_action" value="saveEvaluationSettings"/>
       <portlet:param name="procurementId" value="${procurementId}"/>
       <%-- Code updated for R4 Starts --%>
       <portlet:param name="competitionPoolId" value="${competitionPoolId}"/>
       <portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
       <portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
       <portlet:param name="ES" value="1"/>
<%-- Code updated for R4 Ends --%>
</portlet:actionURL>
<%-- Save Evaluation Detail Action URL Ends --%>
<%--Start : Added in R5 --%>
<portlet:actionURL var="saveDocumentDetailsUrl" escapeXml="false">
       <portlet:param name="action" value="propEval"/>
       <portlet:param name="topLevelFromRequest" value="${topLevelFromRequest}"/>
       <portlet:param name="midLevelFromRequest" value="${midLevelFromRequest}"/>
       <portlet:param name="submit_action" value="saveDocumentSettings"/>
       <portlet:param name="procurementId" value="${procurementId}"/>
       <portlet:param name="competitionPoolId" value="${competitionPoolId}"/>
       <portlet:param name="evaluationGroupId" value="${evaluationGroupId}"/>
       <portlet:param name="evaluationPoolMappingId" value="${evaluationPoolMappingId}"/>
       <portlet:param name="ES" value="1"/>
</portlet:actionURL>
<input type = 'hidden' value='${saveDocumentDetailsUrl}' id='hiddenSaveDocumentDetailsUrl'/>
<%--End : Added in R5 --%>

<%-- Form Tag Begins --%>
<form:form id="evaluationSettingsform" name="evaluationSettingsform" action="${saveEvaluationDetailsUrl}" method ="post" commandName="Evaluator">
<div id='tabs-container'>
<d:content isReadOnly="${((accessScreenEnable eq false) or hideExitProcurement) and (procurementBean.status ne 7 and procurementBean.status ne 8)}" >
<%-- Get Internal Evaluator URL Begins --%>
<portlet:resourceURL var="getInternalEvaluatorsUrl" id="getInternalEvaluatorsUrl" escapeXml="false">
       <portlet:param name="internalEvaluator" value="getInternalEvaluatorsUrl"/>
</portlet:resourceURL>
<%-- Get Internal Evaluator URL Ends --%>
<%-- Get External Evaluator URL Begins --%>
<portlet:resourceURL var="getExternalEvaluatorsUrl" id="getExternalEvaluatorsUrl" escapeXml="false">
       <portlet:param name="externalEvaluator" value="getExternalEvaluatorsUrl"/>
</portlet:resourceURL>
<%-- Get External Evaluator URL Ends --%>
<portlet:resourceURL var="setAgency" id="setAgency" escapeXml="false"></portlet:resourceURL>
       
<input type = 'hidden' value='${getInternalEvaluatorsUrl}' id='getInternalEvaluatorsUrl'/>
<input type = 'hidden' value='${getExternalEvaluatorsUrl}' id='getExternalEvaluatorsUrl'/>
<input type = 'hidden' value='${setAgency}' id='setAgency'/>
<input type = 'hidden' value='' id='internalEvaluatorNames' name="internalEvaluatorNames"/>
<input type = 'hidden' value='' id='externalEvaluatorNames' name="externalEvaluatorNames"/>
<input type = 'hidden' value='' id='providerId'/>
<input type = 'hidden' value='${Agency_ID}' id='agencyID' name="asAgencyId"/>
<input type = 'hidden' value='${fn:escapeXml(procurementBean.procurementTitle)}' id='procurementTitle' name="procurementTitle"/>
<form:hidden path="evaluatorCount"/>
<input type = 'hidden' value='' id='topAgencyId'/>
<%-- Form Data Starts --%>


<h2>Evaluation Settings
       <a id="returnEvaluationSummary" class="floatRht returnButton" href="#">Proposals and Evaluations Summary</a>
</h2>

<c:set var="readOnlyValue" value=""></c:set>
<%-- modified the check to make page read only if review score task has been generated once. --%>
<c:if test="${org_type eq 'city_org' or isEvaluationScoreSend or ReviewTaskPresent or loEvalPoolCancelledStatus}">
       <c:set var="readOnlyValue" value="disabled='disabled'"></c:set>
</c:if>
<input type="hidden" value="${isEvaluationScoreSend}" id="isEvaluationScoreSend"/>
<input type="hidden" value="${org_type}" id="cityUser"/>
<input type="hidden" value="${ReviewTaskPresent}" id="isReviewTaskPresent"/>
<c:set var="helpIconProvider"><%=HHSComponentMappingConstant.SOLICITATION_MANAGEMENT_HELP%></c:set>

              <d:content section="${helpIconProvider}">
                   <div id="helpIcon" class="iconQuestion"><a href="javascript:void(0);" title="Need Help?" onclick="smFinancePageSpecificHelp();"></a></div>
                    		<input type="hidden" id="screenName" value="Evaluation Settings" name="screenName"/>
				</d:content>
			<div class='hr'></div>
                           <div class="failed" id="errorMessage">You must select at least three evaluators.<img
                                  src="../framework/skins/hhsa/images/iconClose.jpg" id="box"
                                  class="message-close" title="Close" alt="Close"
                                  onclick="showMe('errorMessage', this)">
                           </div>
              <c:if test="${message ne null}">
                     <div class="${messageType}" id="messagediv" style="display:block">${message}<img src="../framework/skins/hhsa/images/iconClose.jpg" id="box" class="message-close" onclick="showMe('messagediv', this)"></div>
              </c:if>
              <c:if test="${(accessScreenEnable eq false) and (procurementBean.status ne 7 and procurementBean.status ne 8)}">
                     <c:set var="lockAccess"><fmt:message key='SCREEN_LOCKED'/></c:set>
                     <div class="lockMessage" style="display:block">${lockedByUser} ${lockAccess}</div>
              </c:if>
              
       <p>Please make selections below to configure your evaluation team and criteria.</p>
       <%-- Code updated for R4 Starts --%>
       <div class="formcontainer">
              <c:if test="${procurementBean.isOpenEndedRFP eq '1'}">
                     <div class="row">
                           <span class="label">Evaluation Group:</span>
                           <span class="formfield">${groupTitleMap['EVALUATION_GROUP_TITLE']}</span>
                     </div>
                     <div class="row">
                           <span class="label">Closing Date:</span>
                           <span class="formfield">${groupTitleMap['SUBMISSION_CLOSE_DATE']}</span>
                     </div>
              </c:if>
              <div class="row">
                     <span class="label">Competition Pool:</span>
                     <span class="formfield">${groupTitleMap['COMPETITION_POOL_TITLE']}</span>
              </div>
       </div>
       <%-- Code updated for R4 Ends --%>
       <h3>Identify Internal Evaluators</h3>
       <p>Select an Agency and type the first three letters of Evaluator's name to search for users to add to the Evaluation Team.</p>
       <div class='formcontainer'>
              <div class="row">
                     <span class="label">Agency:</span>
                                  <span class="formfield">
                                         <form:select  path="agencyId" cssClass="input agencyClassDropDown" id="agencyId">
                                                <form:option id="-1" value=""> </form:option>
                                                <form:options items="${nycAgencyMaster}" />
                                         </form:select>
                                  </span>                    
                           <span class="formfield error">
                                  <form:errors path="agencyId" cssClass="ValidationError"></form:errors>
                           </span>
              </div>
              <div class='row'>
                     <span class='label' style='height:55px'>Internal Evaluator Name:</span>
                     <span class='formfield'>
                           <input name="internalItem" id="internalItem" type="text" class='input' 
                           title="This person must be a user in the system. Visit General Help to learn more about adding users to the system." ${readOnlyValue} maxlength="90"/>
                           <span class='taskButtons'>
                                  <input id ='internalAdd' type="button" class='add' value="Add User" disabled="disabled" onclick="addItem('internal')" />
                           </span>
                     </span>
                     <span id="errorMsgInternal" class="error"></span>
              </div>
              <div class='row'>
                     <span class='label' style='height:110px'>Internal Evaluators:</span>
                     <span class='formfield'>
                           <select id="internalItems" name="items" size="5" class='input' multiple="multiple" ${readOnlyValue}>
                                 <c:forEach var="internalList" items="${evaluationListInternal}">
                                        <option value="${internalList.agencyId}~${internalList.userId}">${internalList.name}</option>
                                 </c:forEach>
                           </select>
                           <span class='taskButtons'>
                                  <input id="removeInternal" disabled="disabled"  type="button" class="remove"  value="Remove User" onclick="removeItem(this,'internal')" />
                           </span>
                     </span>
              </div>
       </div>
       <h3>Identify External Evaluators</h3>
       <p title="Each External Evaluator selected must be accompanied by an Agency representative. This agency representative must be from the ACCO unit and will serve as a proxy for the External Evaluator.">
              If applicable, please include any external evaluators who will be working outside the system. For each external evaluator, 
                     <br>
              select an Agency representative who will receive a task in the system on behalf of the evaluator.
       </p>
       <div class='formcontainer'>
              <div class='row'>
                     <span class='label'>External Evaluator Name:</span>
                     <span class='formfield'>
                           <input name="externalEvaluator" id="externalEvaluator" type="text" class='input' ${readOnlyValue} maxlength="90"/>
              </span>
              </div>
              <div class='row'>
                     <span class='label' style='height:55px'>Agency Representative:</span>
                     <span class='formfield'>
                           <input name="externalItem" ${readOnlyValue}  id="externalItem" type="text" class='input' title="The Agency representative must be a user in the system. This person will complete a task on behalf of external evaluator. Visit General Help to learn more about adding users to the system" />
                           <span class='taskButtons'>
                                  <input type="button" disabled="disabled"  class='add' value="Add User" onclick="addItem('external')" id="externalAdd"/>
                           </span>
                     </span>
                     <span id="errorMsgExternal" class="error"></span>
              </div>
              <div class='row'>
                     <span class='label' style='height:110px'>External Evaluators:</span>
                     <span class='formfield'>
                           <select id="externalItems" name="items" size="5" class='input' multiple="multiple" ${readOnlyValue}     >
                                 <c:forEach var="externalList" items="${evaluationListExternal}">
                                        <option value="${externalList.extEvaluatorName}~${externalList.agencyUserId}">
                                                            ${externalList.extEvaluatorName}(via ${externalList.name})</option>
                                 </c:forEach>
                           </select>
                           <span class='taskButtons'>
                                  <input id="removeExternal" disabled="disabled" type="button" class="remove" value="Remove User" onclick="removeItem(this,'external')"/>
                           </span>
                     </span>
              </div>
       </div>
       <!--   R5 code starts -->
       	<c:set var="isRadioBttnDisable" value="" />
		<c:if test="${!isAwardStatusReturned}">
			 <c:set var="isRadioBttnDisable" value="disabled" />
		</c:if>       
       <c:if test="${not empty evaluatorDocumentList}">
              <c:set var="hasOptional" value="false"></c:set>
              <h3>Show/Hide Required Documents</h3><br>
              Choose to show or hide each required document from evaluators in the Evaluate Proposal Task.
              <div class='formcontainer'>
                     
                           <!-- <span class='label' style='height:55px'></span> -->
                           <span style="position: relative;left: 340px;">
					<b>Show&nbsp;&nbsp;Hide</b>
				</span>
                     
              
                     <c:forEach var="evalDocument" items="${evaluatorDocumentList}"  varStatus="loop">
                           <c:if test="${evalDocument.requiredFlag eq '1' }">
                           <div class='row'>
                                  <span class='label' >${evalDocument.documentType}:</span>
                                  <span class='formfield' style="margin-left: 10px;">
                                         <c:set var="showVar"></c:set>
                                         <c:set var="hideVar"></c:set>
                                         <c:choose>
                                                <c:when test="${evalDocument.visibility eq '1'}">
                                                       <c:set var="showVar">checked="checked"</c:set>
                                                </c:when>
                                                <c:otherwise>
                                                       <c:set var="hideVar">checked="checked"</c:set>
                                                </c:otherwise>
                                         </c:choose>
                                         <input type="radio" name="documentVisibilityList[${loop.index}].visibility" value="1" ${showVar} ${isRadioBttnDisable}/>&nbsp;&nbsp;&nbsp;&nbsp;
                                         <input type="radio" name="documentVisibilityList[${loop.index}].visibility" value="0" ${hideVar} ${isRadioBttnDisable}/>
                                         <form:hidden path="documentVisibilityList[${loop.index}].documentType" value="${evalDocument.documentType}"/>
                                         <form:hidden path="documentVisibilityList[${loop.index}].evaluationPoolMappingId" value="${evalDocument.evaluationPoolMappingId}"/>
                                         <form:hidden path="documentVisibilityList[${loop.index}].procurementDocumentId" value="${evalDocument.procurementDocumentId}"/>
                                         <form:hidden name="documentVisibilityList[${loop.index}].documentVisibilityId" value="${evalDocument.documentVisibilityId}" path="documentVisibilityList[${loop.index}].documentVisibilityId"/>
                                  </span>
                                  <span id="errorMsgExternal" class="error"></span>
                           </div>
                           </c:if>
                           <c:if test="${evalDocument.requiredFlag eq '0' }">
                                  <c:set var="hasOptional" value="true"></c:set>
                           </c:if>
                     </c:forEach>
              </div>
              
              <c:if test="${hasOptional eq 'true' }">
                     <h3>Show/Hide Optional Documents</h3><br>
                     Choose to show or hide each optional document from evaluators in the Evaluate Proposal Task.
                     <div class='formcontainer'>
                                  <span style="position: relative;left: 330px;">
					<b>Show&nbsp;&nbsp;Hide</b>
				</span>
                     
                           <c:forEach var="evalDocument" items="${evaluatorDocumentList}"  varStatus="loop">
                                  <c:if test="${evalDocument.requiredFlag eq '0' }">
                                  <div class='row'>
                                         <span class='label'>${evalDocument.documentType}:</span>
                                         <span class='formfield'>
                                                <c:set var="showVar"></c:set>
                                                <c:set var="hideVar"></c:set>
                                                <c:choose>
                                                       <c:when test="${evalDocument.visibility eq '1'}">
                                                              <c:set var="showVar">checked="checked"</c:set>
                                                       </c:when>
                                                       <c:otherwise>
                                                              <c:set var="hideVar">checked="checked"</c:set>
                                                       </c:otherwise>
                                                </c:choose>
                                                <input type="radio" name="documentVisibilityList[${loop.index}].visibility" value="1" ${showVar}  ${isRadioBttnDisable}/>&nbsp;&nbsp;&nbsp;&nbsp;
                                                <input type="radio" name="documentVisibilityList[${loop.index}].visibility" value="0" ${hideVar}  ${isRadioBttnDisable}/>
                                                <form:hidden path="documentVisibilityList[${loop.index}].documentType" value="${evalDocument.documentType}"/>
                                                <form:hidden path="documentVisibilityList[${loop.index}].evaluationPoolMappingId" value="${evalDocument.evaluationPoolMappingId}"/>
                                                <form:hidden path="documentVisibilityList[${loop.index}].procurementDocumentId" value="${evalDocument.procurementDocumentId}"/>
                                                <form:hidden name="documentVisibilityList[${loop.index}].documentVisibilityId" value="${evalDocument.documentVisibilityId}" path="documentVisibilityList[${loop.index}].documentVisibilityId"/>
                                         </span>
                                         <span id="errorMsgExternal" class="error"></span>
                                  </div>
                                  </c:if>
                           </c:forEach>
                     </div>
              </c:if>
       </c:if>
       <!--   R5 code ends -->
       
       <div class='buttonholder'>
       <%-- modified the check to hide save button if review score task has been generated once. --%>
                           <c:if test="${org_type eq 'agency_org'}">
                                  <%-- Start : Modified in R5 --%>
                                  <c:choose>
                                  		<c:when test="${!isEvaluationScoreSend && !ReviewTaskPresent}">
                                  			<input type="button" class="button" title='Save changes' value="Save" id="saveEvaluators" />
                                  		</c:when>
                                  		<c:otherwise>
                                  		<c:choose>
                                  			<c:when test="${isAwardStatusReturned}"> 
                                  				<input type="submit" class="button" title='Save changes' value="Save" id="saveDocumentData" />
                                  			</c:when>
                                  			<c:otherwise>
                                  				<input type="submit" class="button" title='Save changes' value="Save" id="saveDocumentData" disabled="disabled" />
                                  			</c:otherwise>
                                  			</c:choose>
                                  		</c:otherwise>
                                  </c:choose>
                                  <%-- End : Modified in R5 --%>
                           </c:if>
                           
              </div>
              </d:content>
</div>
</form:form>
<%-- Form Tag Ends --%>



</nav:navigationSM>
<%-- Navigation Tag Ends --%>

<div class="overlay"></div>
	
