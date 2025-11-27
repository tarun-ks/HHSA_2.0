<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@page import="com.nyc.hhs.model.TaxonomyTree"%>
<%@page import="com.nyc.hhs.model.TaxonomySynonymBean"%>
<%@page import="com.nyc.hhs.model.TaxonomyLinkageBean"%>
<%@ page errorPage="/error/errorpage.jsp" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
   String lsOldName ="";
   if(request.getSession().getAttribute("lsOldElementName")!=null){
	   lsOldName = (String)request.getSession().getAttribute("lsOldElementName");
   }
   TaxonomyTree loTaxonomyTreeBean = (TaxonomyTree) request.getAttribute("TaxonomyTreeBean");      
   String lsElementDescription = loTaxonomyTreeBean.getMsElementDescription();
 %>

<style>
	.rgtCell #main-wrapper {width:100%;}
	.rgtCell #main-wrapper .bodycontainer {width:100%; padding:0;}
	.bodycontainer {width:100%; padding:0;}
	.rgtCell #main-wrapper .bodycontainer br {display:none;}
	.container {padding:0;}
	.tabularWrapper {
	    height: auto;
	}
	.maintainanceWrapper .lftCell{
		min-height:777px;	
	}
</style>

<%
	String taxonomyTree3 = (String)renderRequest.getPortletSession().getAttribute("lsLinkageTree");
%>

    <div class="maintainanceWrapper">
            
	<!-- Right Column Starts here -->
    <div class=""> 
    <!-- HHS Breadcrumb Starts -->
    <div class="breadcrumb breakAll">
   		<ul id="breadcrumbId"></ul>
    </div>
    <!-- HHS Breadcrumb Ends -->
    <p class="clear"></p>
    <div  class="tabularWrapper portlet1Col">
		<div class="tabularCustomHead">Description</div>
        <div class="tabularContainer">
	        <%
			if (null == lsElementDescription){
				lsElementDescription = "";
			}
			%>
	         <input type="hidden" name="hiddenChkEvidanceOld" value="<%=loTaxonomyTreeBean.getMsEvidenceReqd()%>"  id="hiddenChkEvidanceOld"/>
	         <input type="hidden" name="hiddenChkApprovalOld" value="<%=loTaxonomyTreeBean.getMsSelectionFlag()%>" id="hiddenChkApprovalOld"/>
	         <input type="hidden" name="hiddenChkTaxonomyOld" value="<%=loTaxonomyTreeBean.getMsActiveFlag()%>" id="hiddenChkTaxonomyOld"/>
	         <textarea class="descriptionTxtarea" id="description" onkeyup="setMaxLength(this,1000)" onchange="setFlag();"><%=lsElementDescription%></textarea>
        </div>
        <input type="hidden" name="hiddenDescription" id="hiddenDescription"/>    
	</div>
                
	<!-- Linkage section starts -->
	<div  class="portlet1Col">
    	<div class="tabularCustomHead">Linkages</div>
			<div class="tabularContainer">
            	<div class="linkageTree">
                	<a class=link title="Collapse All" href="javascript:ddtreemenu.flatten('treemenu3', 'contact')">Collapse All</a> | <a class=link title="Expand All" href="javascript:ddtreemenu.flatten('treemenu3', 'expand')">Expand All</a>
                 	<ul id="treemenu3" class="treeview">
						<%=taxonomyTree3%>
					</ul>
	            </div>
              	<div class="taskButtons alignRht floatNone">
                	<input type="button" class="add" value="Add Linkage" title="Add Linkage" onclick="addLinkages();"/>
                </div>
                      	    
              	<select class="linkageTree" style="height:70px; width: 100%;" id="exisingLinkageItems" name="exisingLinkageItems" size="6" multiple="multiple">
            	</select>
                        
                <input type="hidden" name="allLinkages" value="" id="linkageValues"/>
                        
               	<div class="taskButtons alignRht floatNone">
                   	<input type="button" class="remove" value="Remove Linkage" title="Remove Linkage" onclick="removeLinkageItem();" />
               	</div>
            </div>                    
	</div>
    </div>
    <!-- Linkage section Ends -->
    
    <!-- Synonyms/ Like Terms Starts -->
    <div class="tabularWrapper portlet2Col">
        <div class="tabularCustomHead">Synonyms/Like Terms</div>
        <div class="tabularContainer">
            <input id="newItem" type="text" style="width:99%;" />
            <div class="taskButtons alignRht floatNone">
                <input type="button" class="add" value="Add" title="Add" onclick="addItem()" />
            </div>
            <select id="items" name="items" size="5" style="width:100%;" multiple="multiple">
				<%
				//	TaxonomyTree loTaxonomyTreeBean = (TaxonomyTree) request.getAttribute("TaxonomyTreeBean");      	
				List<TaxonomySynonymBean> loSynonymBeanList = loTaxonomyTreeBean.getMsSynonymList();
	
				if(loSynonymBeanList !=null && !loSynonymBeanList.isEmpty()){
	     			Iterator topicIterator = loSynonymBeanList.iterator();
	       			while(topicIterator.hasNext()){
	          			TaxonomySynonymBean loSynonymBean=(TaxonomySynonymBean)topicIterator.next();
	          			String lsSynonym = loSynonymBean.getMsTaxonomySyn();
						%>	
						<option value="<%=lsSynonym%>"><%=lsSynonym%></option>
						<%   }
				}
				%>
            </select>
            <input type="hidden" name="allSynonyms" value="" id="synonymValues"/>   
            <div class="taskButtons alignRht floatNone">
                <input type="button" class="remove" value="Remove" title="Remove" onclick="removeItem()" />
            </div>
        </div>
    </div>
    <%
	//	TaxonomyTree loTaxonomyTreeBean = (TaxonomyTree) request.getAttribute("TaxonomyTreeBean");      	
	List<TaxonomyLinkageBean> loLinkageBeanList = loTaxonomyTreeBean.getMsLinkageList();
	String lsLinkgaeIdString ="";
	if(loLinkageBeanList !=null && !loLinkageBeanList.isEmpty()){
    	Iterator topicIterator = loLinkageBeanList.iterator();
        while(topicIterator.hasNext()){
        	TaxonomyLinkageBean loLinkageBean=(TaxonomyLinkageBean)topicIterator.next();
            String lsLinkage = loLinkageBean.getMsTaxonomyLinkageId();
            lsLinkgaeIdString = lsLinkgaeIdString+lsLinkage+",";
			%>
			<script type="text/javascript">getLinkagePath('<%=lsLinkage%>')</script>
		<%   }
		}
		%>
		<span id="detailLinkageIds" style="display:none"><%=lsLinkgaeIdString%></span> 
        <!-- Synonyms/ Like Terms Ends -->
                 
       	<!-- Settings Starts -->
        <div  class="tabularWrapper portlet2Col portletColRight" >
            <div class="tabularCustomHead">Settings</div>
            <div class="tabularContainer settingHeight">
                <ul>
                    <li id="chkEvidanceId">
                        <input type="checkbox" name="flags" id="chkEvidance" onclick="setChkBoxState();setFlag();" <%if(loTaxonomyTreeBean.getMsEvidenceReqd().equalsIgnoreCase("1")){ %> checked="checked" <%} %> />
                        <label for="chkEvidance">This service/function requires evidence</label>
                    </li>
                    <li style='height:5px;'></li>
                     <li id="chkApprovalId">
                        <input type="checkbox" name="flags" id="chkApproval" onclick="setFlag();" <%if(loTaxonomyTreeBean.getMsSelectionFlag().equalsIgnoreCase("1")){ %> checked="checked" <%} %> />
                        <label for="chkApproval">This service/function allows approval</label>
                    </li>
                    <li style='height:5px;'></li>
                    <li>
                        <input type="checkbox" name="flags" id="chkTaxonomy" onclick="setFlag();" <%if(loTaxonomyTreeBean.getMsActiveFlag().equalsIgnoreCase("1")){ %> checked="checked" <%} %>/>
                        <label for="chkTaxonomy">This taxonomy item is active (will display to provider user)</label>
                    </li>
                </ul>
                <input type="hidden" name="hiddenChkEvidance"  id="hiddenChkEvidance"/>
                <input type="hidden" name="hiddenChkApproval"  id="hiddenChkApproval"/>
                <input type="hidden" name="hiddenChkTaxonomy"  id="hiddenChkTaxonomy"/>
                <input type="hidden" name="hiddenElementName"  id="hiddenElementName"/>
                <input type="hidden" name="hiddenOldElementName"  id="hiddenOldElementName" value = '<%=lsOldName%>'/>
            </div>
        </div>
       <!-- Settings Ends -->
                
	</div>
    <!-- Right Column Ends here -->

<script type="text/javascript">

	//jquery ready function- executes the script after page loading
	$(document).ready(function() { 
		ddtreemenu.createTree("treemenu3", true);
	
	});	     
	
	//Below function get Linkage path for the selected taxonomy
	function getLinkagePath(lsNewElementId){  //function shud get   elementid|abc>pqr>mno
		var path= "";
		var lsSelectTag = document.getElementById('exisingLinkageItems');
		var linkagebranch = $("li[id="+lsNewElementId+"]>span>a").attr("id");
		var arr = linkagebranch.split(",");
		for(var i =0; i<arr.length-1;i++){
			path = path+(document.getElementById(arr[i]).title)+">";
		}
		path =path.replace(/.$/, '');
		var lsNewTagOption = document.createElement('option');
		lsNewTagOption.value= lsNewElementId;
		lsNewTagOption.text=path ;  
		lsSelectTag.options.add(lsNewTagOption);
	}

</script>  