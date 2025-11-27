//** Accordion API created for Business Application module 
//** Created: Jun 22nd, 2012'. Last updated: Oct 29th, 2012 to v6.0


var ddaccordion={
	ajaxloadingmsg: '<img src="loading2.gif" /><br />Loading Content...', 

	headergroup: {}, //object to store corresponding header group based on headerclass value
	contentgroup: {}, //object to store corresponding content group based on headerclass value

	preloadimages:function($images){
		$images.each(function(){
			var preloadimage=new Image();
			preloadimage.src=this.src;
		});
	},

	expandone:function(headerclass, selected, scrolltoheader){ //PUBLIC function to expand a particular header
		this.toggleone(headerclass, selected, "expand", scrolltoheader);
	},

	collapseone:function(headerclass, selected){ //PUBLIC function to collapse a particular header
		this.toggleone(headerclass, selected, "collapse");
	},

	expandall:function(headerclass){ //PUBLIC function to expand all headers based on their shared CSS classname
		var $headers=this.headergroup[headerclass];
		this.contentgroup[headerclass].filter(':hidden').each(function(){
			$headers.eq(parseInt($(this).attr('contentindex'))).trigger("evt_accordion");
		});
	},

	collapseall:function(headerclass){ //PUBLIC function to collapse all headers based on their shared CSS classname
		var $headers=this.headergroup[headerclass];
		this.contentgroup[headerclass].filter(':visible').each(function(){
			$headers.eq(parseInt($(this).attr('contentindex'))).trigger("evt_accordion");
		});
	},

	toggleone:function(headerclass, selected, optstate, scrolltoheader){ //PUBLIC function to expand/ collapse a particular header
		var $targetHeader=this.headergroup[headerclass].eq(selected);
		var $subcontent=this.contentgroup[headerclass].eq(selected);
		if (typeof optstate=="undefined" || optstate=="expand" && $subcontent.is(":hidden") || optstate=="collapse" && $subcontent.is(":visible"))
			$targetHeader.trigger("evt_accordion", [false, scrolltoheader]);
	},

	ajaxloadcontent:function($targetHeader, $targetContent, config, callback){
		var ajaxinfo=$targetHeader.data('ajaxinfo');
		function handlecontent(content){ //nested function
			if (content){ //if ajax content has loaded
				ajaxinfo.cacheddata=content; //remember ajax content 
				ajaxinfo.status="cached" ; //set ajax status to cached
				if ($targetContent.queue("fx").length==0){ //if this content isn't currently expanding or collapsing
					$targetContent.hide().html(content) ; //hide loading message, then set sub content's HTML to ajax content
					ajaxinfo.status="complete" ; //set ajax status to complete
					callback() ; //execute callback function- expand this sub content
				}
			}
			if (ajaxinfo.status!="complete"){
				setTimeout(function(){handlecontent(ajaxinfo.cacheddata);}, 100); //call handlecontent() again until ajax content has loaded
			}
		} 

		if (ajaxinfo.status=="none"){ //ajax data hasn't been fetched yet
			$targetContent.html(this.ajaxloadingmsg);
			$targetContent.slideDown(config.animatespeed);
			ajaxinfo.status="loading" ; //set ajax status to "loading"
			$.ajax({
				url: ajaxinfo.url, //path to external menu file
				error:function(ajaxrequest){
					handlecontent('Error fetching content. Server Response: '+ajaxrequest.responseText);
				},
				success:function(content){
					content=(content=="")? " " : content ; //if returned content is empty, set it to "space" is content no longer returns false/empty (hasn't loaded yet)
					handlecontent(content);
				}
			});
		}
		else if (ajaxinfo.status=="loading")
			handlecontent(ajaxinfo.cacheddata);
	},

	//PUBLIC function to expand a particular header
	expandit:function($targetHeader, $targetContent, config, useractivated, directclick, skipanimation, scrolltoheader){
		var ajaxinfo=$targetHeader.data('ajaxinfo')
		if (ajaxinfo){ 
			if (ajaxinfo.status=="none" || ajaxinfo.status=="loading")
				this.ajaxloadcontent($targetHeader, $targetContent, config, function(){ddaccordion.expandit($targetHeader, $targetContent, config, useractivated, directclick)})
			else if (ajaxinfo.status=="cached"){
				$targetContent.html(ajaxinfo.cacheddata)
				ajaxinfo.cacheddata=null
				ajaxinfo.status="complete"
			}
		}
		this.transformHeader($targetHeader, config, "expand")
		$targetContent.slideDown(skipanimation? 0 : config.animatespeed, function(){
			config.onopenclose($targetHeader.get(0), parseInt($targetHeader.attr('headerindex')), $targetContent.css('display'), useractivated)
			if (scrolltoheader){
				var sthdelay=(config["collapseprev"])? 20 : 0
				clearTimeout(config.sthtimer)
				config.sthtimer=setTimeout(function(){ddaccordion.scrollToHeader($targetHeader)}, sthdelay)
			}
			if (config.postreveal=="gotourl" && directclick){ 
				var targetLink=($targetHeader.is("a"))? $targetHeader.get(0) : $targetHeader.find('a:eq(0)').get(0)
				if (targetLink) 
					setTimeout(function(){location=targetLink.href}, 200 + (scrolltoheader? 400+sthdelay : 0) ) 
			}
		})
	},

	//PUBLIC function to scroll to a particular header
	scrollToHeader:function($targetHeader){
		ddaccordion.$docbody.stop().animate({scrollTop: $targetHeader.offset().top}, 400)
	},

	//PUBLIC function to collapse a particular header
	collapseit:function($targetHeader, $targetContent, config, isuseractivated){
		this.transformHeader($targetHeader, config, "collapse")
		$targetContent.slideUp(config.animatespeed, function(){config.onopenclose($targetHeader.get(0), parseInt($targetHeader.attr('headerindex')), $targetContent.css('display'), isuseractivated)})
	},

	transformHeader:function($targetHeader, config, state){
		if($targetHeader.hasClass("noExpand") == false || 
				(!$targetHeader.hasClass("noExpand") && state=="expand" ) || state=="collapse"){ //alternate btw "expand" and "collapse" CSS classes
			$targetHeader.addClass((state=="expand")? config.cssclass.expand : config.cssclass.collapse) 
			.removeClass((state=="expand")? config.cssclass.collapse : config.cssclass.expand);
		}
		if (config.htmlsetting.location=='src'){ 
			$targetHeader=($targetHeader.is("img"))? $targetHeader : $targetHeader.find('img').eq(0) ; //Set target to either header itself, or first image within header
			$targetHeader.attr('src', (state=="expand")? config.htmlsetting.expand : config.htmlsetting.collapse) ; //change header image
		}
		else if (config.htmlsetting.location=="prefix") 	
			$targetHeader.find('.accordprefix').html((state=="expand")? config.htmlsetting.expand : config.htmlsetting.collapse);
		else if (config.htmlsetting.location=="suffix")
			$targetHeader.find('.accordsuffix').html((state=="expand")? config.htmlsetting.expand : config.htmlsetting.collapse);
	},

	urlparamselect:function(headerclass){
		var result=window.location.search.match(new RegExp(headerclass+"=((\\d+)(,(\\d+))*)", "i")) 
		if (result!=null)
			result=RegExp.$1.split(',')
		return result 
	},

	//fetch cookie 
	getCookie:function(Name){ 
		var re=new RegExp(Name+"=[^;]+", "i") 
		if (document.cookie.match(re)) //if cookie found
			return document.cookie.match(re)[0].split("=")[1] ;
		return null
	},

	//set cookie 
	setCookie:function(name, value){
		document.cookie = name + "=" + value + "; path=/";
	},

	//initialization
	init:function(config){
	document.write('<style type="text/css">\n');
	document.write('.'+config.contentclass+'{display: none}\n') ; //generate CSS to hide contents
	document.write('a.hiddenajaxlink{display: none}\n') ; //CSS class to hide ajax link
	document.write('<\/style>');
	jQuery(document).ready(function($){
		ddaccordion.urlparamselect(config.headerclass);
		var persistedheaders=ddaccordion.getCookie(config.headerclass);
		ddaccordion.headergroup[config.headerclass]=$('.'+config.headerclass) ; //remember header group for this accordion
		ddaccordion.contentgroup[config.headerclass]=$('.'+config.contentclass) ;  //remember content group for this accordion
		ddaccordion.$docbody=(window.opera)? (document.compatMode=="CSS1Compat"? jQuery('html') : jQuery('body')) : jQuery('html,body');
		var $headers=ddaccordion.headergroup[config.headerclass];
		var $subcontents=ddaccordion.contentgroup[config.headerclass];
		config.cssclass={collapse: config.toggleclass[0], expand: config.toggleclass[1]}; //store expand and contract CSS classes as object properties
		config.revealtype=config.revealtype || "click";
		config.revealtype=config.revealtype.replace(/mouseover/i, "mouseenter");
		if (config.revealtype=="clickgo"){
			config.postreveal="gotourl" ;
			config.revealtype="click" ; //overwrite revealtype to "click" keyword
		}
		if (typeof config.togglehtml=="undefined")
			config.htmlsetting={location: "none"}
		else
			config.htmlsetting={location: config.togglehtml[0], collapse: config.togglehtml[1], expand: config.togglehtml[2]} //store HTML settings as object properties
		config.oninit=(typeof config.oninit=="undefined")? function(){} : config.oninit 
		config.onopenclose=(typeof config.onopenclose=="undefined")? function(){} : config.onopenclose 
		var lastexpanded={} 
		var expandedindices=ddaccordion.urlparamselect(config.headerclass) || ((config.persiststate && persistedheaders!=null)? persistedheaders : config.defaultexpanded)
		if (typeof expandedindices=='string') 
			expandedindices=expandedindices.replace(/c/ig, '').split(',') 
		if (expandedindices.length==1 && expandedindices[0]=="-1") 
			expandedindices=[]
		if (config["collapseprev"] && expandedindices.length>1) 
			expandedindices=[expandedindices.pop()] 
		if (config["onemustopen"] && expandedindices.length==0) 
			expandedindices=[0]
		$headers.each(function(index){ 
			var $header=$(this)
			if (/(prefix)|(suffix)/i.test(config.htmlsetting.location) && $header.html()!=""){ 
				$('<span class="accordprefix"></span>').prependTo(this)
				$('<span class="accordsuffix"></span>').appendTo(this)
			}
			$header.attr('headerindex', index+'h') 
			$subcontents.eq(index).attr('contentindex', index+'c') 
			var $subcontent=$subcontents.eq(index)
			var $hiddenajaxlink=$subcontent.find('a.hiddenajaxlink:eq(0)') 
			if ($hiddenajaxlink.length==1){
				$header.data('ajaxinfo', {url:$hiddenajaxlink.attr('href'), cacheddata:null, status:'none'}) 
			}
			var needle=(typeof expandedindices[0]=="number")? index : index+'' 
			if (jQuery.inArray(needle, expandedindices)!=-1){ 
				ddaccordion.expandit($header, $subcontent, config, false, false, !config.animatedefault) 
				lastexpanded={$header:$header, $content:$subcontent}
			}  
			else{
				$subcontent.hide()
				config.onopenclose($header.get(0), parseInt($header.attr('headerindex')), $subcontent.css('display'), false) 
				ddaccordion.transformHeader($header, config, "collapse")
			}
		})
		
			
		$headers.bind("evt_accordion", function(e, isdirectclick, scrolltoheader){ 
				var $subcontent=$subcontents.eq(parseInt($(this).attr('headerindex'))) 
				if ($subcontent.css('display')=="none"){
					ddaccordion.expandit($(this), $subcontent, config, true, isdirectclick, false, scrolltoheader) 
					if (config["collapseprev"] && lastexpanded.$header && $(this).get(0)!=lastexpanded.$header.get(0)){ 
						ddaccordion.collapseit(lastexpanded.$header, lastexpanded.$content, config, true) 
					}
					lastexpanded={$header:$(this), $content:$subcontent}
				}
				else if (!config["onemustopen"] || config["onemustopen"] && lastexpanded.$header && $(this).get(0)!=lastexpanded.$header.get(0)){
					ddaccordion.collapseit($(this), $subcontent, config, true) 
				}
 		})
		$headers.bind(config.revealtype, function(){
			if (config.revealtype=="mouseenter"){
				clearTimeout(config.revealdelay)
				var headerindex=parseInt($(this).attr("headerindex"))
				config.revealdelay=setTimeout(function(){ddaccordion.expandone(config["headerclass"], headerindex, config.scrolltoheader)}, config.mouseoverdelay || 0)
			}
			else{
				$(this).trigger("evt_accordion", [true, config.scrolltoheader]) //last parameter indicates this is a direct click on the header
				return false 
			}
		})
		$headers.bind("mouseleave", function(){
			clearTimeout(config.revealdelay)
		})
		config.oninit($headers.get(), expandedindices)
		$(window).bind('unload', function(){ 
			$headers.unbind()
			var expandedindices=[]
			$subcontents.filter(':visible').each(function(index){ 
				expandedindices.push($(this).attr('contentindex'))
			})
			if (config.persiststate==true && $headers.length>0){ 
				expandedindices=(expandedindices.length==0)? '-1c' : expandedindices 
				ddaccordion.setCookie(config.headerclass, expandedindices)
			}
		})
	})
	}
}


ddaccordion.preloadimages(jQuery(ddaccordion.ajaxloadingmsg).filter('img'))