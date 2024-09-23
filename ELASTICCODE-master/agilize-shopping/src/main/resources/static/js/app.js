//(function () {

'use strict';

    var app = angular.module('agilizeShopping', ['ngRoute', 'services.localizedMessages', 
    	'restResource', 'msgbox', 'localStorage', 'directives.pagination', 'StoreModule', 'ProductModule', 'ShoppingCartModule']);
    
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
    	
        nmApplication: 'SHOPPING',
        nmEntity: 'SHOPPING',

        // SERVER
    	/*baseUrlBack: 'https://agilize-security.herokuapp.com/agilize/facade',
    	urlLogin: 'https://agilize-security.herokuapp.com/#/agilize/login?urlBackHost=https://front-isobrou.herokuapp.com&urlBackPath=app/isobrou/logged',
    	urlValidate: 'https://agilize-security.herokuapp.com/agilize/login/validateToken',
    	host: 'https://front-isobrou.herokuapp.com',
    	hostBack: 'https://agilize-security.herokuapp.com/',
    	urlPayment: 'https://agilize-payment.herokuapp.com/agilize/merci/#/merci',*/
    	
    	baseUrlBack: 'http://localhost:8084/agilize/facade',
    	urlLogin: 'http://localhost:8084/#/agilize/login?urlBackHost=http://localhost:9108/agilize/shopping&urlBackPath=app/shopping/logged&params=SHOPPING',
    	urlValidate: 'http://localhost:8084/agilize/login/validateToken',
    	host: 'http://localhost:9108/agilize/shopping',
    	hostBack: 'http://localhost:8084/',
    	urlPayment: 'http://localhost:8088/agilize/merci/#/merci', 
    	
    	/*baseUrlBack: 'http://lazarogilvan.hopto.org:8084/agilize/facade',
    	urlLogin: 'http://lazarogilvan.hopto.org:8084/#/agilize/login?urlBackHost=http://lazarogilvan.hopto.org:9108/agilize/shopping&urlBackPath=app/shopping/logged',
    	urlValidate: 'http://lazarogilvan.hopto.org:8084/agilize/login/validateToken',
    	host: 'http://lazarogilvan.hopto.org:9108/agilize/shopping',
    	hostBack: 'http://lazarogilvan.hopto.org:8084/',
    	urlPayment: 'http://lazarogilvan.hopto.org:8088/agilize/merci/#/merci', */
    	
    	urlLogged: 'app/shopping/logged',
    	urlHome: 'app/shopping',
    	timeOutNotification: 3000,
        // Quantidade padrão de itens retornados em listas
        rowsPerPage: 5,
        keySessionUser: 'userLogged',
        keySessionUrls: 'pathUrl',
        keyTokenSession: 'X-Auth-Token',
        keyPathUrl: 'X-Header-Path',
        keyApplication: 'X-Header-Application',
        notFirstTime: 'notFirstTime'
        
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
        
        $routeProvider.when('/app/shopping', {
        	controller: 'AppCtrl'
        });
        $routeProvider.when('/app/shopping/logged/:token', {
            controller: 'AppLoggedCtrl',
            template: '<div></div>',
            resolve:{
            	token : ['$route', function($route) {
    				return $route.current.params.token;
    			}]
    	    }
        });
        
        $routeProvider.otherwise('app/shopping/store');
        
    }).run(['$rootScope', '$location', 'APP_CONFIG', '$store', '$window',
        		function ($rootScope, $location, APP_CONFIG, $store, $window) {
        
    	    
	    	$rootScope.$on("$locationChangeStart", function (event, next, current) {
	            
	            var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);
	            var isFirstTime = $store.get(APP_CONFIG.notFirstTime);
	            var test = next.indexOf(APP_CONFIG.urlHome, next.length - APP_CONFIG.urlHome.length) !== -1;
	            test = test || next.indexOf(APP_CONFIG.host+'/', next.length - (APP_CONFIG.host+'/').length) !== -1;
	            test = test || next.indexOf(APP_CONFIG.urlLogged) >  APP_CONFIG.host.length;
	            
	            if(!test) {
	            	var url = next; 
	            	addPath($store, APP_CONFIG, url);
	            	
	            	//Verificando se está loggado.
	            	if (!isLogged && isNullValue(isFirstTime)) {
	            		//$window.location.href = APP_CONFIG.urlLogin; 
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
    ]).controller('AppLoggedCtrl', ['token', '$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', '$msgbox', '$location',
    								function (token, $scope, restResource, $rootScope, $store, APP_CONFIG, $msgbox, $location) {
        	
    	$store.set(APP_CONFIG.notFirstTime, null);
    	if(isNullValue(token)) {
    		$scope.message.show("msg.error.token.login.null");
    	}
    	else {
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
                    var urlLocation = getPath($store, APP_CONFIG);
                    if(isNullValue(urlLocation) || urlLocation === '/app/shopping') {
                    	$location.path('/app/shopping/store');
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
        '$store', 'restResource', '$q', '$window',
        function ($sce, $scope, localizedMessages, $rootScope, APP_CONFIG, $location, $msgbox,
        			$store, restResource, $q, $window) {
        	
        	$scope.sce = $sce;
        	
            $rootScope.onSuccess = $scope.onSuccess = function(response) {
            	return onSuccess(response, localizedMessages, $msgbox, $q, $rootScope);
            };
            
            $rootScope.onError = $scope.onError = function(response) {
            	return onError(response, $msgbox, localizedMessages);
            };
            
            $rootScope.getMessage = $scope.getMessage = function (key, interpolateParams) {
                return getMessageParamText(key, interpolateParams, localizedMessages);
            };
            
            $rootScope.openPath = $scope.openPath = function (urlLocation) {
            	addPath($store, APP_CONFIG, urlLocation);
            	$location.path(urlLocation);
            };
            
            $rootScope.renderHtml = $scope.renderHtml = function (htmlCode) {
                if (!isNullValue(htmlCode)) {
                    return $sce.trustAsHtml(htmlCode);
                }
            };
            
            $rootScope.renderImage = $scope.renderImage = function (fileString) {
                if (isNullValue(fileString)) {
                    return "";
                }
                return $sce.trustAsHtml(renderImage(fileString));
                //renderHtml(stringToObject(product.image).pathLogical)
            };
            
            $rootScope.removeTags = $scope.removeTags = function (strHtml) {
                return removeTags(strHtml);
            };
            
            $rootScope.stringToObject = $scope.stringToObject = function (data) {
            	return stringToObject(data);
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
                    open: function () {
                        window.scrollTo(0, 0);
                        var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);

                        if (isLogged) {
                            var text = $rootScope.getMessage("msg.user.logged", [$rootScope.login.user.name]);
                            $msgbox.show(text).then(function () {
                                $rootScope.login.clear();
                                $window.location.href = APP_CONFIG.urlLogin; 
                                return;
                            }, function () {
                                $location.path(getPath($store, APP_CONFIG));
                                return;
                            });
                        } else {
                            $rootScope.login.clear();
                            $window.location.href = APP_CONFIG.urlLogin; 
                        }
                    },
                    exit: function () {
                        window.scrollTo(0, 0);
                        $rootScope.login.clear();
                        $location.path(APP_CONFIG.urlHome);
                    }
                };
        }
    ]);
// create a data service that provides a store and a shopping cart that
// will be shared by all views (instead of creating fresh ones for each view).
app.factory("DataService", ['$store', '$rootScope', function ($store, $rootScope) {

    // create store
    //var myStore = new store();

    // create shopping cart
    var myCart = new shoppingCart("AgilizeShopping", $store, $rootScope);

    // enable PayPal checkout
    // note: the second parameter identifies the merchant; in order to use the 
    // shopping cart with PayPal, you have to create a merchant account with 
    // PayPal. You can do that here:
    // https://www.paypal.com/webapps/mpp/merchant
    //myCart.addCheckoutParameters("PayPal", "bernardo.castilho-facilitator@gmail.com");

    // enable Google Wallet checkout
    // note: the second parameter identifies the merchant; in order to use the 
    // shopping cart with Google Wallet, you have to create a merchant account with 
    // Google. You can do that here:
    // https://developers.google.com/commerce/wallet/digital/training/getting-started/merchant-setup
    /*myCart.addCheckoutParameters("Google", "500640663394527",
        {
            ship_method_name_1: "UPS Next Day Air",
            ship_method_price_1: "20.00",
            ship_method_currency_1: "USD",
            ship_method_name_2: "UPS Ground",
            ship_method_price_2: "15.00",
            ship_method_currency_2: "USD"
        }
    );*/

    // return data object with store and cart
   return {
        //store: myStore,
        cart: myCart
    };
}]);
//();