function isNullValue(val) {
    return (val === null || !angular.isDefined(val) || (angular.isNumber(val) && !isFinite(val) || val == "" || val === "undefined"));
}

function getMessageParamText(text, args, localizedMessages) {
    var textRet = "";
    if (!isNullValue(args)) {
        textRet = localizedMessages.get(text);
        var nLab = '';
        for (var i = 0; i < args.length; i++) {
        	if(textRet.indexOf("{N}") > 0) {
        		nLab = nLab + localizedMessages.get(args[i]);
        		if(i != (args.length-1)) {
        			nLab = nLab + ',';
        		}
        	}
        	else {
        		textRet = textRet.replace("{" + i + "}", localizedMessages.get(args[i]));
        	}
        }
        if(textRet.indexOf("{N}") > 0) {
        	textRet = textRet.replace("{N}", nLab);
        }
    } else {
        textRet = localizedMessages.get(text);
    }
    return textRet;
}

function calculateDiffHoursToDtAtual(dataDiff) {

    var dateActual = Date.now();

    var hours = Math.abs(dateActual - dataDiff) / 36e5;
    return hours;
}

function onSuccess(response, localizedMessages, $msgbox, $q, $rootScope) {
	
	if(!isNullValue(response)) {
    	
		var data = response;
    	
		if(!isNullValue(data.success) && data.success === true) {
    		return data.data;
    	}
		else {
			
			if(!isNullValue(data.error)) {
	    		var msg = getMessageParamText(data.error.i18nKey, data.error.params, localizedMessages);
				$msgbox.show(msg, undefined, true);
	    		return;
	    	}
			
			if(!isNullValue(data.data)) {
	    		data = data.data;
	    	}
			
	    	if((!isNullValue(data.errorCode) && data.errorCode > 0) && !isNullValue(data.i18nKey)) {
	    		
	    		if(data.errorCode === 1013 || data.errorCode === 1014 || data.errorCode === 1015) {
	    			$rootScope.isErrorPath = true;
	    		}
	    		
	    		var parameters = [];
				if(!isNullValue(data.params)) {
					angular.forEach(data.params, function(param) {
						parameters.push(getMessageParamText(param, undefined, localizedMessages));
					});
				}
	    		var msg = getMessageParamText(data.i18nKey, parameters, localizedMessages);
	    		if(!isNullValue(data.errors)) {
	    			var opts = {};
	    			opts.title = msg;
	    			opts.mensagens = [];
	    			angular.forEach(data.errors, function(item) {
	    				
	    				var params = [];
	    				if(!isNullValue(item.params)) {
	    					angular.forEach(item.params, function(param) {
	    						params.push(getMessageParamText(param, undefined, localizedMessages));
	    					});
	    				}
	    				opts.mensagens.push(getMessageParamText(item.i18nKey, params, localizedMessages));
	    			});
	    			$msgbox.show('', opts, true);
	    		}
	    		else {
	    			$msgbox.show(msg, undefined, true);
	    		}
	    		//$q.reject("error_path");
	    		return;
			}
		}
	}
	return response;
}

function onError(response, $msgbox, localizedMessages) {
	/*if(response) {
		var msg = response.data || {message: "Request failed"};
		$msgbox.show(msg.message, undefined, true);
	}
	else {
		$msgbox.show("Request failed", undefined, true);
	}
	return undefined;*/
	
	if(!isNullValue(response)) {
    	
		if(!isNullValue(response.data) && !isNullValue(response.data.i18nKey)) {
			
			var data = response.data; 
			
			if(data.errorCode === 1013 || data.errorCode === 1014 || data.errorCode === 1015) {
    			$rootScope.isErrorPath = true;
    		}
    		
    		var parameters = [];
			if(!isNullValue(data.params)) {
				angular.forEach(data.params, function(param) {
					parameters.push(getMessageParamText(param, undefined, localizedMessages));
				});
			}
			
    		var msg = getMessageParamText(data.i18nKey, parameters, localizedMessages);
    		
    		if(!isNullValue(data.errors)) {
    			var opts = {};
    			opts.title = msg;
    			opts.mensagens = [];
    			angular.forEach(data.errors, function(item) {
    				
    				var params = [];
    				if(!isNullValue(item.params)) {
    					angular.forEach(item.params, function(param) {
    						params.push(getMessageParamText(param, undefined, localizedMessages));
    					});
    				}
    				opts.mensagens.push(getMessageParamText(item.i18nKey, params, localizedMessages));
    			});
    			$msgbox.show('', opts, true);
    		}
    		else {
    			$msgbox.show(msg, undefined, true);
    		}
    		return;
		}
		else {
			var msg = response.data || {message: "Request failed"};
			$msgbox.show(msg.message, undefined, true);
			return;
		}
	}
	$msgbox.show("Request failed", undefined, true);
	return response;
}

function getPath($store, APP_CONFIG) {
	var links = $store.get(APP_CONFIG.keySessionUrls);
	if(!isNullValue(links) && links.length > 0) {
		var index = links.length-1;
		var path = links[index];
		links.splice(index, 1);
		$store.set(APP_CONFIG.keySessionUrls, links);
		
		if(path.indexOf('/#/') > 1) {
			path = path.substring(path.indexOf('/#/') + 2);
		}
		
		return path;
	}
	return APP_CONFIG.urlHome;
}

function addPath($store, APP_CONFIG, next) {
	//Adicionando a Url a pilha
	var links;
	if(!isNullValue(next)) {
		links = $store.get(APP_CONFIG.keySessionUrls);
		if(isNullValue(links) || links.length < 1) {
			links = [];
			links.push(next);
		}
		else {
			if(next != links[links.length-1]) {
				links.push(next);
			}
		}
	}
	$store.set(APP_CONFIG.keySessionUrls, links);
}

function removeTags(strHtml) {
    if (!isNullValue(strHtml)) {
        return strHtml.replace(/<\/?[^>]+(>|$)/g, "");
    }
    return "";
}

function convertJsonToString(data) {
	if(!isNullValue(data)) {
		if(!isNullValue(data.jSon)) {
			return angular.fromJson(data.jSon)
		}
		if(!isNullValue(data.list)) {
			var ret = [];
			angular.forEach(data.list, function(param) {
				ret.push(angular.fromJson(param.jSon));
			});
			return ret;
		}
	}
	return data;
}

function stringToObject(data) {
	if(!isNullValue(data)) {
		data = angular.fromJson(data);
		/*if(!isNullValue(data.length)) {
			if(data.length === 1) {
				return angular.fromJson(data[0]);
			}
			var ret = [];
			angular.forEach(data, function(param) {
				ret.push(angular.fromJson(param));
			});
			return ret;
		}*/
		if(!isNullValue(data.length) && data.length === 1) {
			return data[0];
		}
	}
	return data;
}

function getPathServerFileAgilize() {
	return "file/upload/";
}

function getHostServerFileAgilize() {
	return "http://localhost:8087/agilize/fileserver/";
	//return "http://lazarogilvan.hopto.org:8087/agilize/fileserver/";
}

function renderImage(fileString) {
	return getHostServerFileAgilize() + (stringToObject(fileString)).pathLogical;
}