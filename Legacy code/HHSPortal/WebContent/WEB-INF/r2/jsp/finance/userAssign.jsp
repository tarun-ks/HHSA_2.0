<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="java.util.List,java.util.ArrayList"%>
<%@ page
	import="com.nyc.hhs.util.CommonUtil, com.nyc.hhs.constants.ComponentMappingConstant, org.apache.commons.lang.StringEscapeUtils;"%>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/contractlist.js"></script>
<script type="text/javascript"
	src="${pageContext.servletContext.contextPath}/r2/js/autoNumeric.js"></script>
<portlet:resourceURL var="userAccessUrl" id="userAccessUrl"
	escapeXml="false">
</portlet:resourceURL>
<input type='hidden' value='${userAccessUrl}' id='userAccessUrl' />
<input type='hidden' value='${saveRestrictionDetailsUrl}'
	id='hiddenSaveRestrictionDetailsUrl' />
<div class="content">
	<div id="newTabs">
		<div class="tabularCustomHead">User Access</div>
		<form:form id="userAssignOverlay" name='userAssignOverlay'
			method="post" action="${saveRestrictionDetailsUrl}">
			<input type="hidden" id="contractIdUserAssign" value="${contractId}" />
			<div class='overlayWrapper formcontainer' style="padding: 0px 12px;">
				<div class="messagedivover" id="messagedivover"></div>
				<br>

				<%
					if (CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.USER_ACCESS, request.getSession()))
						{
				%>
				<b class="boldlayout">Assign Users</b>
				<div id="ErrorDiv" class="failed breakAll avoidOverlap"></div>
				<hr style="border-top: dotted 1px; width: 40%;" align="left" />
				Use the add and remove buttons to set user restrictions for this
				contract. Users without access can see the record of this contract in
				the Contract List but are unable to view contract details or the
				details of related budgets, invoices, payments, and amendments.
				Additionally, specific document types related to these entities will
				be restricted in the Document Vault.
				<%
					}
						else
						{
				%>
				<b class="boldlayout">View Users</b>
				<div id="ErrorDiv" class="failed breakAll avoidOverlap"></div>
				<hr style="border-top: dotted 1px; width: 40%;" align="left" />
				Users without access can see the record of this contract in the
				Contract List but are unable to view contract details or the details
				of related budgets, invoices, payments, and amendments.
				Additionally, specific document types related to these entities will
				be restricted in the Document Vault.
				<%
					}
				%>
				<br> <span class="error" id="SaveError"></span>
				<div class='clear'>&nbsp;</div>
				<div class='row'>
					<span class='label'> <label class="required">*</label>Procurement/Contract
						Title:
					</span> <span class='formfield' style='width: 44% !important'> <span
						id="titleId"> </span>

					</span> <span class="error"
						style="width: 200px; margin-left: 180px; margin-bottom: -5px; margin-top: -5px;"></span>
				</div>
				<div class='row'>
					<span class='label'> <label class="required">*</label>Agency:
					</span> <span class='formfield' style='width: 44% !important'> <span
						id='agencyType'></span>
					</span> <span class="error"
						style="width: 200px; margin-left: 180px; margin-bottom: -5px; margin-top: -5px;"></span>
				</div>
				<div class='row'>
					<span class='label'> <label class="required">*</label>CT#:
					</span> <span class='formfield' style='width: 44% !important'> <span
						id='contractNo'></span>
					</span> <span class="error"
						style="width: 200px; margin-left: 180px; margin-bottom: -5px; margin-top: -5px;"></span>
				</div>
				<div class='row'>
					<span class='label'> <label class="required">*</label>Contract
						Value:
					</span> <span class='formfield' style='width: 44% !important'> <span
						id='contractVal1'></span>
					</span> <span class="error"
						style="width: 200px; margin-left: 180px; margin-bottom: -5px; margin-top: -5px;"></span>
				</div>
				<br> <br>
				<div class='mymaindiv'>
					<div class="userAccessOverlay" id="userAccessleft">
						<p class="addInfouserAccess">
							User <span class='textGreen'>with</span> Access
						</p>
						<div class="mulselectuserAccess">
							<select multiple="true" size="6" ${readOnlyValue}
								class="multiselect" id="sharedList1" name="myselecttsms">
								<c:forEach var="userAccessListVar" items="${userAccessList}"
									varStatus="loop">
									<c:if test="${userAccessListVar.accessFlag eq '0'}">
										<option value="${userAccessListVar.staffId}"
											title="${userAccessListVar.userName}-${userAccessListVar.permission_level}-${userAccessListVar.email}">${userAccessListVar.userName}-${userAccessListVar.permission_level}-${userAccessListVar.email}
										</option>
									</c:if>
								</c:forEach>
							</select>

						</div>
					</div>

					<div class="muloptionsuserAccess" style='text-align: center;'>
						<%
							if (CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.USER_ACCESS, request.getSession()))
								{
						%>
						<input type="button" title="Add user"
							class='muloptionsuserAccessbtn' value="&gt;" rel="myselect"
							id="btnAdd" disabled="disabled" /><br> <br>
						<input type="button" title="Add all"
							class='muloptionsuserAccessbtn' value="&gt;&gt;" rel="myselect"
							id="btnAddAll" disabled="disabled" /> <br> <br>
						<input type="button" title="Remove all"
							class='muloptionsuserAccessbtn' value="&lt;&lt;" rel="myselect"
							id="btnRemoveAll" disabled="disabled" /> <br> <br> <input
							type="button" title="Remove user" class='muloptionsuserAccessbtn'
							value="&lt;" rel="myselect" id="btnRemove" disabled="disabled" />
						<%
							}
						%>
					</div>

					<div class="userAccessOverlay" id="userAccessRight">
						<p class="addInfouserAccess">
							User <span class='textRed'>without</span> Access
						</p>

						<div class="mulselectuserAccess">
							<select class="multiselect TakeOver" ${readOnlyValue}
								multiple="multiple" size="6" id="sharedList2"
								name="language_listbox">
								<c:forEach var="userAccessListVar" items="${userAccessList}">
									<c:if test="${userAccessListVar.accessFlag eq '1'}">
										<option value="${userAccessListVar.staffId}"
											title="${userAccessListVar.userName}-${userAccessListVar.permission_level}-${userAccessListVar.email}">${userAccessListVar.userName}
											-${userAccessListVar.permission_level}-${userAccessListVar.email}</option>
									</c:if>
								</c:forEach>

							</select>
						</div>
					</div>
				</div>
				<div class='clear'></div>
				<br>
				<div class="buttonholder">
					<%
						if (CommonUtil.getConditionalRoleDisplay(ComponentMappingConstant.USER_ACCESS, request.getSession()))
							{
					%>
					<input type="button" class="graybtutton" name="cancelButton"
						title="Cancel" value="   Cancel   " id="cancelButtonUserAccess" />
					<input type="button" class="button" name="createFolder"
						value="   Save   " title="Save" id="SaveButton"
						onclick="UpdateRestrictionList()" />
					<%
						}
							else
							{
					%>
					<input type="button" class="graybtutton" name="closeButton"
						title="Close" value="  Close  " id="closeButtonUserAccess" />
					<%
						}
					%>

				</div>
			</div>
		</form:form>
	</div>
</div>