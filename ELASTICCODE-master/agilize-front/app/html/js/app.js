(function () {

    var app = angular.module('app', ['ngRoute', 'services.i18nNotifications', 'services.localizedMessages', 'services.propriedades', 'services.httpRequestTracker',
        'restResource', 'msgbox', 'localStorage', 'RDash', 'LoginModule', 'angularUUID2', 'authorizationModule', 'ui.mask', 'ngCpfCnpj', 'formMenu',
        'breadCrumb', 'directives.pagination', 'directives.scrollable',
        'EntityModule', 'PaymentModule']);
    
    if (!String.prototype.contains) {
        String.prototype.contains = function (arg) {
            return !!~this.indexOf(arg);
        };
    }
    
    Number.prototype.formatMoney = function(c, d, t) {
    	var n = this, 
    	    c = isNaN(c = Math.abs(c)) ? 2 : c, 
    	    d = d == undefined ? "," : d, 
    	    t = t == undefined ? "." : t, 
    	    s = n < 0 ? "-" : "", 
    	    i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))), 
    	    j = (j = i.length) > 3 ? j % 3 : 0;
    	   return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };
    
    app.constant('APP_CONFIG', {
    	baseUrlBack:'http://localhost:8084/agilize/facade',
    	urlAutenticate:'http://localhost:8084/agilize/login/authenticate',
    	baseUrlAPP:'http://localhost:8100/',
    	urlApp: '/agilize',
    	// urlLogin: '/agilize/login',
    	urlLogin: 'http://localhost:8084/#/agilize/login?urlBackHost=http://localhost:8100/app/html&urlBackPath=logged',
    	urlHome: '/app/html',
    	urlValidate: 'http://localhost:8084/agilize/login/validateToken',
        timeOutNotification: 3000,
        // Quantidade padrão de itens retornados em listas
        rowsPerPage: 5,
        keySessionUser: 'userLogged',
        keySessionUrls: 'pathUrl',
        keyTokenSession: 'X-Auth-Token',
        keyPathUrl: 'X-Header-Path',
        keyApplication: 'X-Header-Application',
        nmApplication: 'ELASTICCODE',
        nmPaymentApplication: 'PAYMENT',
        notFirstTime: 'notFirstTime',
        
        classBrandEnum: 'br.com.agilizeware.enums.BrandEnum',
        classFieldEnum: 'br.com.agilizeware.enums.FieldEnum',
        classRelationShipEnum: 'br.com.agilizeware.enums.RelationShipEnum',
        classTypeCardEnum: 'br.com.agilizeware.enums.TypeCardEnum',
        
        const_FieldEnum_RELATIONSHIP: 0,
        const_TypeCardEnum_CREDIT: 0,
        const_BrandEnum_MASTER: 0,
        const_BrandEnum_VISA: 1
        
    }).factory('XSRFInterceptor', function ($store, $log, APP_CONFIG) {
    		var XSRFInterceptor = {
    				request: function(config) {
    					var token = $store.get(APP_CONFIG.keyTokenSession);
    					if (!isNullValue(token)) {
    						config.headers[APP_CONFIG.keyTokenSession] = token;
    					}
    					else {
    						delete config.headers[APP_CONFIG.keyTokenSession];
    					}
    					return config;
    				}
    		};
    		return XSRFInterceptor;
    }).config(function ($httpProvider, $routeProvider, APP_CONFIG) {
    	
    	//Enable cross domain calls
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
        
        $httpProvider.defaults.headers.common[APP_CONFIG.keyApplication] = APP_CONFIG.nmApplication;
        $httpProvider.interceptors.push('XSRFInterceptor');
        
        $routeProvider.when('/logged/:token', {
            templateUrl: 'views/entity/filter.html',
            controller: 'AppLoggedCtrl',
            resolve:{
            	token : ['$route', function($route) {
    				return $route.current.params.token;
    			}]
    	    }
        });
        
    }).run(['$rootScope', '$location', 'APP_CONFIG', 'PropriedadesCompartilhadas', '$store', '$window',
        		function ($rootScope, $location, APP_CONFIG, PropriedadesCompartilhadas, $store, $window) {
        
    	    
	    	$rootScope.$on("$locationChangeStart", function (event, next, current) {
	            
	            var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);
	            var isFirstTime = $store.get(APP_CONFIG.notFirstTime);
	
	            if (!(  next.indexOf(APP_CONFIG.urlLogin, next.length - APP_CONFIG.urlLogin.length) !== -1 ) ) {
	                
	            	var indexAgilize = next.indexOf('/agilize/');
	            	if(indexAgilize > 0) {
		            	var url = next.substring(indexAgilize);
		            	addPath($store, APP_CONFIG, url);
	            	}
	            	
	            	//Verificando se está loggado.
	            	if (!isLogged && isNullValue(isFirstTime)) {
	            		$window.location.href = APP_CONFIG.urlLogin; 
	            		$store.set(APP_CONFIG.notFirstTime, 1);
	                }
	            	else {
	            		$store.set(APP_CONFIG.notFirstTime, null);
	            	}
	            	
	            }
	        });
	    	
	    }
    ]).filter('trusted', ['$sce', 'APP_CONFIG', function ($sce, APP_CONFIG) {
            return function (url) {
                if (!isNullValue(url)) {
                    url = APP_CONFIG.baseUrlBack + url;
                    return $sce.trustAsResourceUrl(url);
                }
                return undefined;
            };
        }
    ]).controller('HeaderCtrl', ['$scope', 'localizedMessages', '$rootScope', 'APP_CONFIG', '$location', '$msgbox',
        			'PropriedadesCompartilhadas', '$store', 'httpRequestTracker', '$sce',
			        function ($scope, localizedMessages, $rootScope, APP_CONFIG, $location, $msgbox,
			        			PropriedadesCompartilhadas, $store, httpRequestTracker, $sce) {
    	
    	}
    ]).controller('AppLoggedCtrl', ['token', '$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', 'localizedMessages', '$msgbox', '$q', '$location',
		function (token, $scope, restResource, $rootScope, $store, APP_CONFIG, localizedMessages, $msgbox, $q, $location) {
    	
    	$store.set(APP_CONFIG.notFirstTime, null);
    	//$store.set(APP_CONFIG.keySessionUrls, null);
    	if(isNullValue(token)) {
    		$scope.message.show("msg.error.token.login.null");
    	}
    	else {
    		$store.set(APP_CONFIG.keyTokenSession, token)
    		var RestResource = restResource('RestResource', '');
            RestResource.validateToken(token).then(function (result) {
            	result = $rootScope.onSuccess(result);
                if (!isNullValue(result)) {
                    $rootScope.login.user = result;
                    $rootScope.login.logged = true;
                    $rootScope.login.token = result.token;

                    $rootScope.login.dateAuthentication = Date.now();

                    $store.logoff(APP_CONFIG.keySessionUser);
                    $store.bind($rootScope, APP_CONFIG.keySessionUser, $rootScope.login);
                    
                    window.scrollTo(0, 0);
                    
                    $store.set(APP_CONFIG.keyTokenSession, result.token);
                    //$store.set(APP_CONFIG.keySessionUrls, []);
                    var urlLocation = getPath($store, APP_CONFIG);
                    if(isNullValue(urlLocation)) {
                    	$location.path(APP_CONFIG.urlHome);
                    }
                    else {
                    	$location.path(urlLocation);
                    }
                }
            });
    	}
    }
    ]).controller('AppCtrl', [
        '$sce', '$scope', 'localizedMessages', '$rootScope', 'APP_CONFIG', '$location', '$msgbox',
        'PropriedadesCompartilhadas', '$store', 'httpRequestTracker', 'authorizationService', '$http', 'restResource', '$q', '$routeParams', '$window',
        function ($sce, $scope, localizedMessages, $rootScope, APP_CONFIG, $location, $msgbox,
        			PropriedadesCompartilhadas, $store, httpRequestTracker, authorizationService, $http, restResource, $q, $routeParams, $window) {
        	$scope.sce = $sce;
        	
        	// Define se exibe ou nao um icone de progresso durante as chamadas ao backend
    		$scope.hasPendingRequests = function () {
    			return httpRequestTracker.hasPendingRequests();
    		};
        	
            $rootScope.login = {
                logged: $store.isLogged(APP_CONFIG.keySessionUser),
                message: "",
                user: $store.user(),
                token: isNullValue($store.user().token) ? "-1" : $store.user().token,
                clear: function () {
                    $scope.message.text = "";
                    $rootScope.message.text = "";
                    $rootScope.login.user = {
                        username: "",
                        password: "",
                        typeAuthentication: null,
                        device: window.$.device.type.id
                    };
                    $rootScope.login.logged = false;
                    $rootScope.login.token = "-1";
                    //$rootScope.login.roles = [];
                    $store.logoff(APP_CONFIG.keySessionUser);
                    $store.set(APP_CONFIG.keyTokenSession, null);
                },
                open: function () {
                    window.scrollTo(0, 0);
                    var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);

                    if (isLogged) {
                        var text = $rootScope.getMessage("msg.user.logged", [$rootScope.login.user.name]);
                        $msgbox.show(text).then(function () {
                            $rootScope.login.clear();
                            $window.location.href = APP_CONFIG.urlLogin; //$location.path(APP_CONFIG.urlLogin);
                            return;
                        }, function () {
                            $location.path(getPath($store, APP_CONFIG));
                            return;
                        });
                    } else {
                        $rootScope.login.clear();
                        $window.location.href = APP_CONFIG.urlLogin; // $location.path(APP_CONFIG.urlLogin);
                    }
                },
                exit: function () {
                    window.scrollTo(0, 0);
                    $rootScope.login.clear();
                    $location.path(APP_CONFIG.urlHome);
                }
            };
            
            $rootScope.getMessage = $scope.getMessage = function (key, interpolateParams) {
                return getMessageParamText(key, interpolateParams, localizedMessages);
            };
            
            $rootScope.isErrorPath = false;
            $rootScope.onSuccess = $scope.onSuccess = function(response) {
            	return onSuccess(response, localizedMessages, $msgbox, $q, $rootScope);
            };
            
            $rootScope.onError = $scope.onError = function(response) {
            	return onError(response, $msgbox, localizedMessages);
            };
            
            $scope.returnPath = function(response) {
            	$rootScope.isErrorPath = false;
            	//Necessário retirar dois elementos da pilha
            	var url = getPath($store, APP_CONFIG);
            	url = getPath($store, APP_CONFIG);
            	$location.path(url);
            }
        }
    ])
})();
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
function removeTags(strHtml) {
    if (!isNullValue(strHtml)) {
        return strHtml.replace(/<\/?[^>]+(>|$)/g, "");
    }
    return "";
}
function getPath($store, APP_CONFIG) {
	var links = $store.get(APP_CONFIG.keySessionUrls);
	if(!isNullValue(links) && links.length > 0) {
		var index = links.length-1;
		var path = links[index];
		links.splice(index, 1);
		$store.set(APP_CONFIG.keySessionUrls, links);
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

