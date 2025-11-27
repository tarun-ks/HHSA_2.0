/**
 * This function is used to show the alternate color in the table grid
 */
$(function(){
	var tableId= $("table[id='completeListId']");
	if(tableId!=''){
		var allChild = $(tableId).find("tr");
		$(allChild).each(function(i) {
			if(i>0){
				if(i%2==0){
					$(this).removeClass();
					$(this).addClass("alternate");
				}else{
					$(this).removeClass();
				}
			}
		});
	}
});