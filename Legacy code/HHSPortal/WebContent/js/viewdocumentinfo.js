/**
 *  This will execute when EditDocumentPRoperties link is clicked
 * */
function editDocument(id){
	pageGreyOut();
	document.viewdocform.action = document.viewdocform.action+'&removeNavigator=true&next_action=editDocumentProps&documentId='+id;
	document.viewdocform.submit();
}

/**
 * This will execute when retun to vault link is clicked from view document screen
 * */
function backtoDocVault(){
	pageGreyOut();
	// Start Updated in R5
	document.viewdocform.action = document.viewdocform.action+'&removeNavigator=true&submit_action=checkFilterParams';
	// End Updated in R5
	document.viewdocform.submit();
}

/**
 *  This will execute when Cancel button is clicked from edit document properties screen
 **/
function cancelEdit(id){
	pageGreyOut();
	document.editpropform.action = editFormAction+'&removeNavigator=true&next_action=cancelEdit&documentId='+id;
	document.editpropform.submit();
}

/**
 * This will execute when retun to vault link is clicked from edit document screen
 **/
function returntoVault(){
	pageGreyOut();
	// Start Updated in R5
	document.editpropform.action = editFormAction+'&removeNavigator=true&submit_action=checkFilterParams';
	// End Updated in R5
	document.editpropform.submit();
}