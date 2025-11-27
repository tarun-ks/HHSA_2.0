(function($){

	$.fn.alphanumeric = function(p) {

		p = $.extend({
			ichars: "!@#$%^&*()+=[]\\\';,/{}|\":<>?~`.- ",
			nchars: "",
			allow: ""
		  }, p);

		return this.each
			(
				function()
				{

					if (p.nocaps) p.nchars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					if (p.allcaps) p.nchars += "abcdefghijklmnopqrstuvwxyz";

					s = p.allow.split('');
					for ( i=0;i<s.length;i++) if (p.ichars.indexOf(s[i]) != -1) s[i] = "\\" + s[i];
					p.allow = s.join('|');

					var reg = new RegExp(p.allow,'gi');
					var ch = p.ichars + p.nchars;
					ch = ch.replace(reg,'');

					$(this).keypress
						(
							function (e)
								{

								if (e.charCode > 122 || e.which > 122){
									 e.preventDefault();
								}

									if (!e.charCode) k = String.fromCharCode(e.which);
										else k = String.fromCharCode(e.charCode);

									if (ch.indexOf(k) != -1) e.preventDefault();
									if (e.ctrlKey&&k=='v') e.preventDefault();


								}

						);

					$(this).bind('contextmenu',function () {return false});

				}
			);

	};

	$.fn.alphanumericABC = function(p) {

		p = $.extend({
			ichars: "!@#$%^&*()+=[]\\\';,/{}|\":<>?~`.- ",
			nchars: "",
			allow: ""
		  }, p);

		return this.each
			(
				function()
				{

					if (p.nocaps) p.nchars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					if (p.allcaps) p.nchars += "abcdefghijklmnopqrstuvwxyz";

					s = p.allow.split('');
					for ( i=0;i<s.length;i++) if (p.ichars.indexOf(s[i]) != -1) s[i] = "\\" + s[i];
					p.allow = s.join('|');

					var reg = new RegExp(p.allow,'gi');
					var ch = p.ichars + p.nchars;
					ch = ch.replace(reg,'');
					$(this).keypress
						(
							function (e)
								{

									if (!e.charCode) k = String.fromCharCode(e.which);
										else k = String.fromCharCode(e.charCode);

									if (ch.indexOf(k) != -1) e.preventDefault();
									if (e.ctrlKey&&k=='v') e.preventDefault();

									txt = document.selection.createRange();

									try{

										if(txt.text == ""){

										var sumStr = this.value+""+k;
										var sum = Number(sumStr);
										if(sum > 100){
											e.preventDefault();

										}
										}
									    }catch(err){   }

								}

						);

					$(this).bind('contextmenu',function () {return false});

				}
			);

	};

	$.fn.alphanumeric135 = function(p) {
		paramP = p;
		p = $.extend({
			ichars: "!@#$%^&*()+=[]\\\';,/{}|\":<>?~`.- ",
			nchars: "",
			allow: ""
		  }, p);

		return this.each
			(
				function()
				{

					if (p.nocaps) p.nchars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					if (p.allcaps) p.nchars += "abcdefghijklmnopqrstuvwxyz";

					s = p.allow.split('');
					for ( i=0;i<s.length;i++) if (p.ichars.indexOf(s[i]) != -1) s[i] = "\\" + s[i];
					p.allow = s.join('|');

					var reg = new RegExp(p.allow,'gi');
					var ch = p.ichars + p.nchars;
					ch = ch.replace(reg,'');
					$(this).keypress
						(
							function (e)
								{

									if (!e.charCode) k = String.fromCharCode(e.which);
										else k = String.fromCharCode(e.charCode);

									if (ch.indexOf(k) != -1) e.preventDefault();
									if (e.ctrlKey&&k=='v') e.preventDefault();

									txt = document.selection.createRange();

									try{

										if(txt.text == ""){

										var sumStr = this.value+""+k;
										var sum = Number(sumStr);
										if(sum > paramP){
											e.preventDefault();

										}
										}
									    }catch(err){   }

								}

						);

					$(this).bind('contextmenu',function () {return false});

				}
			);

	};

	$.fn.onlyNumberLimiters = function(p,q) {

		var paramP = p;
		var paramQ = q;
		p = $.extend({
			ichars: "!@#$%^&*()+=[]\\\';,/{}|\":<>?~`.-ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ",
			nchars: "",
			allow: ""
		  }, p);

		return this.each
			(
				function()
				{

					if (p.nocaps) p.nchars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					if (p.allcaps) p.nchars += "abcdefghijklmnopqrstuvwxyz";

					s = p.allow.split('');
					for ( i=0;i<s.length;i++) if (p.ichars.indexOf(s[i]) != -1) s[i] = "\\" + s[i];
					p.allow = s.join('|');

					var reg = new RegExp(p.allow,'gi');
					var ch = p.ichars + p.nchars;
					ch = ch.replace(reg,'');
					$(this).keypress
						(
							function (e)
								{

									if (!e.charCode) k = String.fromCharCode(e.which);
										else k = String.fromCharCode(e.charCode);

									if (ch.indexOf(k) != -1) e.preventDefault();
									if (e.ctrlKey&&k=='v') e.preventDefault();

									txt = document.selection.createRange();

									try{

										if(txt.text == ""){

										var sumStr = this.value+""+k;
										var sum = Number(sumStr);
										if(paramP<sum && sum>paramQ){
											e.preventDefault();

										}
										}
									    }catch(err){   }

								}

						);

					$(this).bind('contextmenu',function () {return false});

				}
			);

	};


	$.fn.numeric = function(p) {

		var az = "abcdefghijklmnopqrstuvwxyz";
		az += az.toUpperCase();

		p = $.extend({
			nchars: az
		  }, p);

		return this.each (function()
			{
				$(this).alphanumeric(p);
			}
		);

	};

		$.fn.numeric_WithoutDec = function(p) {

		var az = "abcdefghijklmnopqrstuvwxyz";
		az += az.toUpperCase();

		p = $.extend({
			nchars: az
		  }, p);

		return this.each (function()
			{
				$(this).alphanumeric(p);
			}
		);

	};

	$.fn.numeric_WithoutDec2 = function(p) {

		var az = "abcdefghijklmnopqrstuvwxyz";
		az += az.toUpperCase();

		p = $.extend({
			nchars: az
		  }, p);

		return this.each (function()
			{
				$(this).alphanumeric(p);
			}
		);

	};

		$.fn.numericRange1To100 = function(p) {

		var az = "abcdefghijklmnopqrstuvwxyz";
		az += az.toUpperCase();
		p = $.extend({
			nchars: az
		  }, p);

		return this.each (function()
			{
				$(this).alphanumericABC(p);
			}
		);

	};

	$.fn.numericRange1ToMaxRange = function(p) {

		var az = "abcdefghijklmnopqrstuvwxyz";
		az += az.toUpperCase();

		return this.each (function()
			{
				$(this).alphanumeric135(p);
			}
		);

	};

	$.fn.numberLimitters = function(p,q) {

		var az = "abcdefghijklmnopqrstuvwxyz";
		az += az.toUpperCase();



		return this.each (function()
			{
				$(this).onlyNumberLimiters(p,q);
			}
		);

	};

	$.fn.alpha = function(p) {

		var nm = "1234567890";

		p = $.extend({
			nchars: nm
		  }, p);

		return this.each (function()
			{
				$(this).alphanumeric(p);
			}
		);

	};


		$.fn.alphanumeric2 = function(p) {

		p = $.extend({
			ichars: "!@#$%^&*()+=[]\\\';,/{}|\":<>?~`.- ",
			nchars: "",
			allow: ""
		  }, p);

		return this.each
			(
				function()
				{

					if (p.nocaps) p.nchars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					if (p.allcaps) p.nchars += "abcdefghijklmnopqrstuvwxyz";

					s = p.allow.split('');
					for ( i=0;i<s.length;i++) if (p.ichars.indexOf(s[i]) != -1) s[i] = "\\" + s[i];
					p.allow = s.join('|');

					var reg = new RegExp(p.allow,'gi');
					var ch = p.ichars + p.nchars;
					ch = ch.replace(reg,'');

					$(this).keypress
						(
							function (e)
								{

									if (!e.charCode) k = String.fromCharCode(e.which);
										else k = String.fromCharCode(e.charCode);

									if (ch.indexOf(k) != -1) e.preventDefault();
									if (e.ctrlKey&&k=='v') e.preventDefault();
									if(e.keyCode > 128)   e.preventDefault();

								}

						);

					$(this).bind('contextmenu',function () {return false});

				}
			);

	};


})(jQuery);
