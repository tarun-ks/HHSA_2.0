// This will execute when retun to vault link is clicked
function backtoDocVault(){
	document.versionform.action = versionform.action+'&removeNavigator=true&next_action=checkFilterParams';
	pageGreyOut();
	document.versionform.submit();
}