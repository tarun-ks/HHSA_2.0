<%-- This jsp file has been added in R4--%>
<%-- This JSP is for add new tag overlay--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <div class="" id="serviceSelectionId"> 
		<div id='taxonomyTaggingWrapper' >
			<h3>Selected Services/Function:</h3>
			<div>
				<select class='widthFull' id = "serviceFunctionDrpDwn" name="serviceFunctionDrpDwn">
				     <option value=""></option>
				</select>
			</div>
			<div id="errorStatusDiv" class=""> </div>		
			<div class='clear'>&nbsp;</div>
			
			<h3>Associated Modifiers:</h3>
			<c:set var="lotaxonomyTree" value="${taxonomyTree}"></c:set>
			<div class='taxonomyTaggingWrapper'>
					<div class='lftContainer'>
							<div class='generateListPanel agencyGreyTitle alignCenter bold'>All Taxonomy Modifiers</div>
							 <div class="taxonomyTaggingLftCell"><A class=link href="javascript:ddtreemenu.flatten('treemenu', 'contact')">Collapse All</A> | <A class=link href="javascript:ddtreemenu.flatten('treemenu', 'expand')">Expand All</A> 
									<ul id=treemenu class=treeview>
										${lotaxonomyTree}
									</ul><%-- To create Dynamic  menu tree, just call the function ddtreemenu.createTree(): --%>
				
							</div>	
								<div class='taskButtons floatRht'>
									<input type='button' value='Add Modifier' class='add' id="addModifierButton" disabled onclick="addLinkages();"/>
								</div>
						</div>
						<div class='rhtContainer'>
							<div class='generateListPanel agencyGreyTitle  alignCenter bold'>Selected Taxonomy Modifiers</div>
									<select class="linkageTree taxonomy" id="exisingLinkageItems" name="exisingLinkageItems" size="6" multiple="multiple">
		            				</select>
								<div class='taskButtons floatRht'>
									<input type='button' value='Remove Modifier' class='remove' id="removeModifierButton" disabled onclick="removeLinkageItem();"/>
								</div>
									
							<p></p>
							
						<%-- Button Wrapper --%>
						<div class="buttonholder">
							<input type="button" class="graybtutton" id="cancelAddNewTagInBulk" value="Cancel" />
							<input type="button" value="Save" onclick="saveChangesInBulk(false);"/>
							<input type="button" value="Save &amp; Complete" id='saveToggle' onclick="saveChangesInBulk(true);"/>
						</div>				
						</div>
						<input type="hidden" name="allLinkages" value="" id="linkageValues"/>
						<input type="hidden" id="taxonomyTaggingId" value="${taxonomyTaggingId}" />
						<c:set var="lotaxonomyHiddenTree" value="${taxonomyHiddenTree}"></c:set>
						<div style="display: none" class="taxonomyTaggingLftCell"><A class=link href="javascript:ddtreemenu.flatten('treemenu7', 'contact')">Collapse All</A> | <A class=link href="javascript:ddtreemenu.flatten('treemenu7', 'expand')">Expand All</A> 
													<ul id=treemenu7 class=treeview>
														${lotaxonomyHiddenTree}
													</ul><%-- To create Dynamic  menu tree, just call the function ddtreemenu.createTree(): --%>
								
						</div>
						<div class='clear'>&nbsp;</div>
					   
					</div>
					<c:set var="elementIdHidden" value=""/>
					<c:set var="branchIdHidden" value=""/>
				    <c:forEach var="taxonomyList" items="${serviceFunctionList}">
				    	<c:set var="elementIdHidden">${elementIdHidden}--${taxonomyList.msElementid}</c:set>
				    	<c:set var="branchIdHidden">${branchIdHidden}--${taxonomyList.msBranchid}</c:set>
					</c:forEach>
				</div>
		</div>
		<input type="hidden" name="allLinkages" value="" id="linkageValues"/>
		<input type="hidden" id="taxonomyTaggingId" value="${taxonomyTaggingId}" />
		<input type="hidden" name="elementIdHidden" value="${elementIdHidden}" id="elementIdHidden"/>
		<input type="hidden" name="branchIdHidden" value="${branchIdHidden}" id="branchIdHidden"/>
		<input type="hidden" name="taxonomyTaggingBean" value="${taxonomyTaggingBean}" id="taxonomyTaggingBean"/>
		<c:set var="lotaxonomyHiddenTree" value="${taxonomyHiddenTree}"></c:set>
		<div style="display: none" class="taxonomyTaggingLftCell"><A class=link href="javascript:ddtreemenu.flatten('treemenu7', 'contact')">Collapse All</A> | <A class=link href="javascript:ddtreemenu.flatten('treemenu7', 'expand')">Expand All</A> 
		<ul id=treemenu7 class=treeview>
			${lotaxonomyHiddenTree}
		</ul><%-- To create Dynamic  menu tree, just call the function ddtreemenu.createTree(): --%>
				
		</div>
		<div class='clear'>&nbsp;</div>
