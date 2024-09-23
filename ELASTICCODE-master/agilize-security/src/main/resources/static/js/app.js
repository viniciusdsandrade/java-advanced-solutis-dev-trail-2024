(function () {

    var app = angular.module('app', ['ngRoute', 'services.i18nNotifications', 'services.localizedMessages', 'authorizationModule', 'angularUUID2',
    	'services.propriedades', 'services.httpRequestTracker', 'restResource', 'msgbox', 'localStorage', 'background', /*'ui.mask', 'ngCpfCnpj', 'satellizer',*/
    	'LoginModule']);
    
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
    	//host: 'http://localhost:8100',
    	hostBack: 'http://localhost:8084', 
    	baseUrlBack: 'http://localhost:8084/agilize/facade',
    	urlAutenticate:'http://localhost:8084/agilize/login/authenticate',

    	/*host: 'https://agilize-front.herokuapp.com/',
    	hostBack: 'https://agilize-security.herokuapp.com/', 
    	baseUrlBack: 'https://agilize-security.herokuapp.com/agilize/facade',
    	urlAutenticate:'https://agilize-security.herokuapp.com/agilize/login/authenticate',*/
    	
    	//SERVER
    	//host: 'http://localhost:8100',
    	/*hostBack: 'http://lazarogilvan.hopto.org:8084', 
    	baseUrlBack: 'http://lazarogilvan.hopto.org:8084/agilize/facade',
    	urlAutenticate:'http://lazarogilvan.hopto.org:8084/agilize/login/authenticate',*/
    	
    	timeOutNotification: 3000,
        // Quantidade padrão de itens retornados em listas
        rowsPerPage: 5,
        keySessionUser: 'userLogged',
        keySessionUrls: 'pathUrl',
        keyTokenSession: 'X-Auth-Token',
        keyPathUrl: 'X-Header-Path',
        keyApplication: 'X-Header-Application',
        
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
        
        $routeProvider.otherwise('/agilize/login');
        
    })
    .controller('AppCtrl', ['$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', 'localizedMessages', '$msgbox', 
    						'$q', '$location', '$sce', '$store', 'httpRequestTracker', 
    						function ($scope, restResource, $rootScope, $store, APP_CONFIG, localizedMessages, 
    								  $msgbox, $q, $location, $sce, $store, httpRequestTracker) {
    	
        	$scope.sce = $sce;
        	
        	var urlBackHost = $location.search().urlBackHost;
        	var urlBackPath = $location.search().urlBackPath;
        	var params = $location.search().params;
        	
        	if(!isNullValue(urlBackHost) && !isNullValue(urlBackPath)) {
            	$store.set('urlBackHost', urlBackHost);
            	$store.set('urlBackPath', urlBackPath);
            	$store.set('urlParams', params);
        	}
        	
        	$location.search('urlBackHost', null);
        	$location.search('urlBackPath', null);
        	$location.search('params', null);
        	
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
                    //$('#messageModal').modal('show');
                    $msgbox.show($scope.message.text, undefined, true);
                }
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
                        $store.logoff(APP_CONFIG.keySessionUser);
                        $store.set(APP_CONFIG.keyTokenSession, null);
                    },
                    exit: function () {
                        window.scrollTo(0, 0);
                        $rootScope.login.clear();
                        $location.path(APP_CONFIG.urlHome);
                    }
                };
        	}
    	])
})();