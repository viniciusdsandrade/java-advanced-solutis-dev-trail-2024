(function () {

    var app = angular.module('app', ['ngRoute', 'services.i18nNotifications', 'services.localizedMessages', 
    	'services.propriedades', 'services.httpRequestTracker', 'restResource', 'msgbox', 'localStorage',
    	'ui.mask', 'ngCpfCnpj', 
    	'PaymentModule']);
    
    if (!String.prototype.contains) {
        String.prototype.contains = function (arg) {
            return !!~this.indexOf(arg);
        };
    }
    
    Number.prototype.formatMoney = function(c, d, t) {
    	var n = this/100, 
    	    c = isNaN(c = Math.abs(c)) ? 2 : c, 
    	    d = d == undefined ? "," : d, 
    	    t = t == undefined ? "." : t, 
    	    s = n < 0 ? "-" : "", 
    	    i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))), 
    	    j = (j = i.length) > 3 ? j % 3 : 0;
    	 var ret = s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    	 return ret;
    };
    
    app.constant('APP_CONFIG', {
    	
    	//SERVER
    	returnUrl: 'www.elasticcode.com.br/agilize/merci',
    	/*host: 'https://agilize-payment.herokuapp.com/',
    	baseUrlBack: 'https://agilize-security.herokuapp.com/agilize/facade',
    	urlValidate: 'https://agilize-security.herokuapp.com/agilize/login/validateToken',*/
    	
    	host: 'http://localhost:8088/agilize/merci',
    	baseUrlBack: 'http://localhost:8084/agilize/facade',
    	urlValidate: 'http://localhost:8084/agilize/login/validateToken',
    	
    	urlHome: '/merci',
    	timeOutNotification: 3000,
        // Quantidade padrão de itens retornados em listas
        rowsPerPage: 5,
        keySessionUser: 'userLogged',
        keySessionUrls: 'pathUrl',
        keyTokenSession: 'X-Auth-Token',
        keyPathUrl: 'X-Header-Path',
        keyApplication: 'X-Header-Application',
        nmApplication: 'PAYMENT',
        
        keyBackHost: 'keyBackHost',
        keyBackSameUrl: 'keyBackSameUrl',
        keyBackSuccessUrl: 'keyBackSuccessUrl',
        keyNmAplic: 'keyNmAplic',
        keyUser: 'keyUser',
        keyMenssage: 'menssage=',
        amount: 'amount',
        
        classTypeCardEnum: 'br.com.agilizeware.enums.TypeCardEnum',
        classBrandEnum: 'br.com.agilizeware.enums.BrandEnum',
        
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
        
        $routeProvider.when('/merci', {
            //templateUrl: 'views/teste.html',
        	template: '<div></div>',
            controller: 'AppFacadeCtrl'
        });
        
        $routeProvider.otherwise('/merci');
    
    }) /*.controller('AppFacadeCtrl', ['$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', 
    								'$location', 
				function ($scope, restResource, $rootScope, $store, APP_CONFIG, $location) {
    	
    	var keyBackHost = $location.search().keyBackHost;
    	var keyBackSameUrl = $location.search().keyBackSameUrl;
    	var keyBackSuccessUrl = $location.search().keyBackSuccessUrl;
    	var keyNmAplic = $location.search().keyNmAplic;
    	var keyUser = $location.search().keyUser;
    	var amount = $location.search().amount;
    	
    	if(!isNullValue(keyBackHost) && !isNullValue(keyNmAplic)) {
        	$store.set(APP_CONFIG.keyBackHost, keyBackHost);
        	$store.set(APP_CONFIG.keyBackSameUrl, keyBackSameUrl);
        	$store.set(APP_CONFIG.keyBackSuccessUrl, keyBackSuccessUrl);
        	$store.set(APP_CONFIG.keyNmAplic, keyNmAplic);
        	$store.set(APP_CONFIG.keyUser, keyUser);
        	$store.set(APP_CONFIG.amount, amount);
    	}
    	
    	if(isNullValue(keyUser) || isNullValue(keyNmAplic) || isNullValue(amount)) {
    		var pathRedirectReturn = keyBackHost + '/#/' + keyBackSameUrl + '&' + APP_CONFIG.keyMenssage + '=msg.error.required.parameters';
    		window.location.href = pathRedirectReturn;
    	}
    	
    	$location.search(APP_CONFIG.keyBackHost, null);
    	$location.search(APP_CONFIG.keyBackSameUrl, null);
    	$location.search(APP_CONFIG.keyBackSuccessUrl, null);
    	$location.search(APP_CONFIG.keyNmAplic, null);
    	$location.search(APP_CONFIG.keyUser, null);
    	$location.search(APP_CONFIG.amount, null);
    	    	
    	$location.path('/agilize/payment/create');
        
    })*/ .controller('AppCtrl', ['$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', 
    	                        'localizedMessages', '$msgbox', '$q', '$location', '$sce', 'httpRequestTracker', 
    								function ($scope, restResource, $rootScope, $store, APP_CONFIG, localizedMessages, 
    										  $msgbox, $q, $location, $sce, httpRequestTracker) {
    	
        	$scope.sce = $sce;
        	
        	// Define se exibe ou nao um icone de progresso durante as chamadas ao backend
    		$scope.hasPendingRequests = function () {
    			return httpRequestTracker.hasPendingRequests();
    		};
        	
            $rootScope.onSuccess = $scope.onSuccess = function(response) {
            	return onSuccess(response, localizedMessages, $msgbox, $q, $rootScope);
            };
            
            $rootScope.onError = $scope.onError = function(response) {
            	return onError(response, $msgbox, localizedMessages);
            };
            
            $rootScope.getMessage = $scope.getMessage = function (key, interpolateParams) {
                return getMessageParamText(key, interpolateParams, localizedMessages);
            };
            
            /*$rootScope.openPath = $scope.openPath = function (urlLocation) {
            	$location.path(urlLocation);
            };
            
            $rootScope.renderHtml = $scope.renderHtml = function (htmlCode) {
                if (!isNullValue(htmlCode)) {
                    return $sce.trustAsHtml(htmlCode);
                }
            };
            
            $rootScope.removeTags = $scope.removeTags = function (strHtml) {
                return removeTags(strHtml);
            };*/
            
            $rootScope.message = $scope.message = {
                text: undefined,
                type: "",
                show: function (text, type, args, noi18ln, hideCancel) {
                    if (type) {
                        $scope.message.type = "alert-success";
                    } else {
                        $scope.message.type = "alert-danger";
                    }

                    if (!isNullValue(noi18ln) && noi18ln == 1) {
                        $scope.message.text = text;
                    } else {
                        $scope.message.text = $scope.getMessage(text, args);
                    }
                    $msgbox.show($scope.message.text, undefined, true);
                }
            };
            
            $scope.returnPath = function(response) {
            	$rootScope.isErrorPath = false;
            	//Necessário retirar dois elementos da pilha
            	var url = getPath($store, APP_CONFIG);
            	url = getPath($store, APP_CONFIG);
            	$location.path(url);
            }
            
            $rootScope.login = {
                    logged: $store.isLogged(APP_CONFIG.keySessionUser),
                    message: "",
                    user: $store.user(),
                    token: isNullValue($store.user().token) ? "-1" : $store.user().token,
            	};
        	}
    ]).controller('AppFacadeCtrl', ['$routeParams', '$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', 
    	                        'localizedMessages', '$msgbox', '$q', '$location', '$sce', '$store',
    								function ($routeParams, $scope, restResource, $rootScope, $store, APP_CONFIG, localizedMessages, 
    										  $msgbox, $q, $location, $sce, $store) {
    	
    		var amount = $routeParams.amount;
    		var token = $routeParams.token;
    		var hostBack = $routeParams.hostBack;
    		var urlSameBack = $routeParams.urlSameBack;
    		var urlSucBack = $routeParams.urlSucBack;
    		var nmAplic = $routeParams.nmAplic;
    		var merchantOrderId = $routeParams.merchantOrderId;
        	
    		var test = window.location.hash.substring(1).startsWith(APP_CONFIG.urlHome); 
    		var url =  test ? hostBack + '/#/' + urlSameBack : window.location.hash.substring(1);
    		//var url = window.location.hash === ('#' + APP_CONFIG.urlHome) ? hostBack + '/#/' + urlSameBack : window.location.hash.substring(1);
	    	//function onOk(response, localizedMessages, $msgbox, $q, $rootScope) {
		    /*function onOk(response) {
	    		if(!isNullValue(response)) {
	    	    	var data = response;
	    	    	var opts = null;	
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
	    	    			opts = {};
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
	    	    			//$msgbox.show('', opts, true);
	    	    		}
	    	    		else {
	    	    			//$msgbox.show(msg, undefined, true);
	    	    		}
	    	    		
	    				if(url.indexOf('#') > 0) {
	    					if(url.indexOf('?') > 0) {
	    						url = url + '&' + APP_CONFIG.keyMenssage + msg;
	    					}
	    					else {
	    						url = url + '?' + APP_CONFIG.keyMenssage + msg;
	    					}
	    	    			window.location.href = url;
	    	    		}
	    	    		else {
	    	    			if(!isNullValue(opts)) {
	    	    				$msgbox.show('', opts, true);
	    	    			}
	    	    			else if(!isNullValue(msg)) {
	    	    				$msgbox.show(msg, undefined, true);
	    	    			}
	    	    			//$location.path(url);
	    	    		}
	                	
	                	return;
	    			}
	    	    	if(!isNullValue(data.error)) {
	    	    		var msg = getMessageParamText(data.error.i18nKey, data.error.params, localizedMessages);
	    				//$msgbox.show(msg, undefined, true);
	    	    		
	    				if(url.indexOf('#') > 0) {
	    					if(url.indexOf('?') > 0) {
	    						url = url + '&' + APP_CONFIG.keyMenssage + msg;
	    					}
	    					else {
	    						url = url + '?' + APP_CONFIG.keyMenssage + msg;
	    					}
	    	    			window.location.href = url;
	    	    		}
	    	    		else {
	    	    			$msgbox.show(msg, undefined, true);
	    	    			//$location.path(url);
	    	    		}
	    				
	    				return;
	    	    	}
	    	    	if(data.success) {
	    	    		return data.data;
	    	    	}
	    	    	return data;
	    		}
	    	}
	
	    	function onProblem(response) {
	    		var msg;
	    		if(response) {
	    			var message = response.data || {message: "Request failed"};
	    			msg = message.message;
	    			//$msgbox.show(msg, undefined, true);
	    		}
	    		else {
	    			msg = "Request failed";
	    			//$msgbox.show(msg, undefined, true);
	    		}

				if(url.indexOf('#') > 0) {
					if(url.indexOf('?') > 0) {
						url = url + '&' + APP_CONFIG.keyMenssage + msg;
					}
					else {
						url = url + '?' + APP_CONFIG.keyMenssage + msg;
					}
	    			window.location.href = url;
	    		}
	    		else {
	    			//$location.path(url);
	    			$msgbox.show(msg, undefined, true);
	    		}
	    		
	    		return;
	    	}*/

	    	var message;
    		if(isNullValue(token)) {
    			message = $rootScope.getMessage('msg.error.token.login.null');
        	}
        	else if(isNullValue(amount)) {
    			message = $rootScope.getMessage('msg.error.invalid.amount.sale');
        	}
        	else if(isNullValue(hostBack) || isNullValue(urlSameBack) || isNullValue(urlSucBack) || isNullValue(nmAplic)) {
    			message = $rootScope.getMessage('msg.error.path.null');
        	}
    		
    		if(!isNullValue(message)) {
				
    			if(url.indexOf('#') > 0) {
					if(url.indexOf('?') > 0) {
						url = url + '&' + APP_CONFIG.keyMenssage + message;
					}
					else {
						url = url + '?' + APP_CONFIG.keyMenssage + message;
					}
	    			window.location.href = url;
	    		}
	    		else {
	    			//$location.path(url);
	    			$msgbox.show(message, undefined, true);
	    		}
    			//$msgbox.show(message, undefined, true);
        		//$q.reject();
        		return;
    		}

    		$store.set(APP_CONFIG.keyTokenSession, token);
    		var RestResource = restResource('RestResource', '');
            RestResource.validateToken(token).then(function (result) {
                if (!isNullValue(result)) {
                    $rootScope.login.user = result;
                    $rootScope.login.logged = true;
                    $rootScope.login.token = result.token;

                    $rootScope.login.dateAuthentication = Date.now();

                    $store.logoff(APP_CONFIG.keySessionUser);
                    $store.bind($rootScope, APP_CONFIG.keySessionUser, $rootScope.login);
                    
                    window.scrollTo(0, 0);
                    
                    $store.set(APP_CONFIG.keyTokenSession, result.token);
                    $store.set(APP_CONFIG.keyBackHost, hostBack);
                    $store.set(APP_CONFIG.keyBackSameUrl, urlSameBack);
                    $store.set(APP_CONFIG.keyBackSuccessUrl, urlSucBack);
                    $store.set(APP_CONFIG.keyNmAplic, nmAplic);
                    
                    $location.search('amount', null);
                	$location.search('token', null);
                	$location.search('hostBack', null);
                	$location.search('urlSameBack', null);
                	$location.search('urlSucBack', null);
                	$location.search('nmAplic', null);
                	$location.search('merchantOrderId', null);
                	
                    $location.path('/agilize/payment/create/'+amount+'/'+nmAplic+'/'+merchantOrderId);
                }
            });
    	
    	}
    ]) 
})();