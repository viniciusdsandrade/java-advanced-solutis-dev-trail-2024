(function () {

    var app = angular.module('app', ['ngRoute', 'services.localizedMessages', 'services.httpRequestTracker',
        'restResource', 'msgbox', 'localStorage', 'angularUUID2', 'authorizationModule', 'ui.mask', 'ngCpfCnpj', 
        'blueimp.fileupload', 'upload', 'resources.file', 'service.fileUpload',
        'PaymentModule','StoreModule']);
    
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
    	
       	// SERVER
    	baseUrlBack: 'https://agilize-security.herokuapp.com/agilize/facade',
    	urlLogin: 'https://agilize-security.herokuapp.com/#/agilize/login?urlBackHost=https://front-isobrou.herokuapp.com&urlBackPath=app/isobrou/logged',
    	urlValidate: 'https://agilize-security.herokuapp.com/agilize/login/validateToken',
    	host: 'https://front-isobrou.herokuapp.com',
    	hostBack: 'https://agilize-security.herokuapp.com/',
    	urlPayment: 'https://agilize-payment.herokuapp.com/agilize/merci/#/merci',
    	
    	/*baseUrlBack: 'http://localhost:8084/agilize/facade',
    	urlLogin: 'http://localhost:8084/#/agilize/login?urlBackHost=http://localhost:8400&urlBackPath=app/isobrou/logged',
    	urlValidate: 'http://localhost:8084/agilize/login/validateToken',
    	host: 'http://localhost:8400',
    	hostBack: 'http://localhost:8084/',
    	urlPayment: 'http://localhost:8088/agilize/merci/#/merci'*/ 
    	
    	urlLogged: 'app/isobrou/logged',
    	urlHome: '/app/isobrou',
    	timeOutNotification: 3000,
        // Quantidade padrão de itens retornados em listas
        rowsPerPage: 5,
        keySessionUser: 'userLogged',
        keySessionUrls: 'pathUrl',
        keyTokenSession: 'X-Auth-Token',
        keyPathUrl: 'X-Header-Path',
        keyApplication: 'X-Header-Application',
        notFirstTime: 'notFirstTime',
        nmApplication: 'ISOBROU',
        nmEntity: 'ISOBROU'
        
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
        //$httpProvider.interceptors.push('XSRFInterceptor');
        
        $routeProvider.when('/app/isobrou', {
            templateUrl: 'app/views/app.html',
            controller: 'AppCtrl'
        });
        $routeProvider.when('/app/isobrou/logged/:token', {
            templateUrl: 'app/views/app.html',
            controller: 'AppLoggedCtrl',
            resolve:{
            	token : ['$route', function($route) {
    				return $route.current.params.token;
    			}]
    	    }
        });
        
        $routeProvider.when('/app/isobrou/email/send', {
            templateUrl: 'app/views/app.html',
            controller: 'AppEmailCtrl'
        });
      
        $routeProvider.otherwise('app/isobrou');
        
    }).run(['$rootScope', '$location', 'APP_CONFIG', '$store', '$window',
        		function ($rootScope, $location, APP_CONFIG, $store, $window) {
        
    	    
	    	$rootScope.$on("$locationChangeStart", function (event, next, current) {
	            
	            var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);
	            var isFirstTime = $store.get(APP_CONFIG.notFirstTime);
	            var test = next.indexOf(APP_CONFIG.urlHome, next.length - APP_CONFIG.urlHome.length) !== -1;
	            test = test || next.indexOf(APP_CONFIG.host+'/', next.length - (APP_CONFIG.host+'/').length) !== -1;
	            test = test || next.indexOf(APP_CONFIG.urlLogged) >  APP_CONFIG.host.length;
	            test  = test || next.indexOf(APP_CONFIG.urlHome+'/store') !== -1;
	            
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
    ]).controller('AppEmailCtrl', ['$http', '$scope',
    								function ($http, $scope) {
    	
    	var url = 'https://agilize-assync.herokuapp.com/agilize/async/queue/start';
    	//var url = 'http://localhost:8089/agilize/async/queue/start';
    		
    		var email = JSON.stringify({
        		idUserCreate: 1, 
        		nmEntity: "Store", 
        		application: {id: 4}, 
        		queue: {id: 0}, 
        		priority: {id: 0}, 
        		historic: true, 
        		mapParameters: {to: "lazaro.silva@agilizeware.com", subject: "Teste de Email", codTemplateStandard: 1, 
        			parameters: {name: "Primeiro Contato", email: "lazarogilvan@yahoo.com.br", telefone: "(11) 98662-3972", site: "www.yahoo.com.br", cargo: "CEO", qtdFunc: 10, textContract: "Eu gostaria de confeccionar um WebSite", findIn: "Pesquisa no Google"}}
        		});
    		
    		//$http.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
    		/*$http({
    	        method: 'POST',
    	        data: email,
    	        url: url,
    	        headers: {
    	            'Content-Type': 'application/json; charset=UTF-8',
    	            'X-Auth-Service': 'KSI98kmuybIWn8102N9n9N9UIS910J23hj8g0a(SLiux)JNhh721aAbbbuUU19432l0lI1I1'
    	        }
    	    }).then(function (response) {
    			var ret = $scope.onSuccess(result);
    			alert('ID da Fila = '+ret.id);
    		}, function (response) {
    	    	  alert('DEU PAU!!!');
    	    });*/    		
    		
    		$http.defaults.headers.common['X-Auth-Service'] = 'KSI98kmuybIWn8102N9n9N9UIS910J23hj8g0a(SLiux)JNhh721aAbbbuUU19432l0lI1I1';
    		
    		var httpPromise = $http.post(url, email);
    		httpPromise.then(function (response) {
    			var ret = $scope.onSuccess(response.data);
    			alert('ID da Fila = '+ret.id);
    		}, function (response) {
    	    	  alert('DEU PAU!!!');
    	    });
    		
    		 /*var xmlhttp = new XMLHttpRequest();
    	        xmlhttp.open("POST", url, true);
    	        xmlhttp.setRequestHeader("Content-Type", "application/json");
    	        xmlhttp.setRequestHeader("X-Auth-Service", "KSI98kmuybIWn8102N9n9N9UIS910J23hj8g0a(SLiux)JNhh721aAbbbuUU19432l0lI1I1");
    	        xmlhttp.send(email);*/

        }
    
    ]).controller('AppLoggedCtrl', ['token', '$scope', 'restResource', '$rootScope', '$store', 'APP_CONFIG', 'localizedMessages', '$msgbox', '$q', '$location',
    								function (token, $scope, restResource, $rootScope, $store, APP_CONFIG, localizedMessages, $msgbox, $q, $location) {
        	
    	$store.set(APP_CONFIG.notFirstTime, null);
    	//$store.set(APP_CONFIG.keySessionUrls, null);
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
        '$store', 'httpRequestTracker', 'authorizationService', '$http', 'restResource', '$q', 'FileUpload', '$window',
        function ($sce, $scope, localizedMessages, $rootScope, APP_CONFIG, $location, $msgbox,
        			$store, httpRequestTracker, authorizationService, $http, restResource, $q, FileUpload, $window) {
        	
        	//$.import_js('/js/lib/common.js');
        	
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
            
            $rootScope.openPath = $scope.openPath = function (urlLocation) {
            	addPath($store, APP_CONFIG, urlLocation);
            	$location.path(urlLocation);
            };
            
            $rootScope.renderHtml = $scope.renderHtml = function (htmlCode) {
                if (!isNullValue(htmlCode)) {
                    return $sce.trustAsHtml(htmlCode);
                }
            };
            
            $rootScope.removeTags = $scope.removeTags = function (strHtml) {
                return removeTags(strHtml);
            };
            
            /*$rootScope.getHostServerFileAgilize = function () {
                return getHostServerFileAgilize();
            }
            
            $rootScope.getPathServerFileAgilize = function () {
            	return getPathServerFileAgilize();
            }*/

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
            
            /*$scope.returnPath = function(response) {
            	$rootScope.isErrorPath = false;
            	//Necessário retirar dois elementos da pilha
            	var url = getPath($store, APP_CONFIG);
            	url = getPath($store, APP_CONFIG);
            	$location.path(url);
            }*/
        
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
            
            
            /*TESTE FILE*/
            $scope.dto = {fileToImport: {nmEntity: null, idUserCreate: null, contentType: null, pathPhysical: null, name: null, pathLogical: null, id: null}};
            $scope.dto.fileToImport.nmEntity = APP_CONFIG.nmEntity;
            $scope.dto.fileToImport.idUserCreate = (!isNullValue($rootScope.login.user) && !isNullValue($rootScope.login.user.id)) ? $rootScope.login.user.id : 1;
            
        	$scope.uploadURL = getHostServerFileAgilize() + getPathServerFileAgilize() +
        					   //"http://lazarogilvan.hopto.org:8087/agilize/fileserver/file/upload/" +
        					   $scope.dto.fileToImport.nmEntity+"/"+$scope.dto.fileToImport.idUserCreate+"/";
        	var limit = 1024*1024*1024;  	
        	var upOptions = { url: $scope.uploadURL, maxFileSize:limit, acceptFileTypes: /(\.|\/)(txt|zip|png|bmp|jpg)$/i };
            FileUpload.setOptions(upOptions);
            
            // tratamento do retorno do upload
            $scope.onUploadResponse = function(data) {
            	var ret = $scope.onSuccess(data.result);
            	if(!isNullValue(ret) && ret.length > 0) {
            		$scope.dto.fileToImport.contentType = ret[0].contentType; 
            		$scope.dto.fileToImport.pathPhysical = ret[0].pathPhysical;
            		$scope.dto.fileToImport.name = ret[0].name; 
            		$scope.dto.fileToImport.pathLogical = ret[0].pathLogical;
            		if(!isNullValue(ret[0].id)) {
            			$scope.dto.fileToImport.id = ret[0].id;
            		}
            	}
            };
            
            $scope.saveFile = function() {
        		//$http.defaults.headers.common['X-Auth-Service'] = 'KSI98kmuybIWn8102N9n9N9UIS910J23hj8g0a(SLiux)JNhh721aAbbbuUU19432l0lI1I1';
        		$http.defaults.headers.common['X-Auth-Service'] = '12wsmvkmLD03Llkdmco84dm9k120Io01Okmsjhk84fks9mv799skc8jGG09282J8J6G0kd';
        		var url = 'http://localhost:8087/agilize/fileserver/file/saveTempFile';
        		//var url = 'https://agilize-file.herokuapp.com/agilize/fileserver/file/saveTempFile';
        		// var url = 'http://lazarogilvan.hopto.org:8087/agilize/fileserver/file/saveTempFile';
        		var httpPromise = $http.post(url, [$scope.dto.fileToImport]);
        		httpPromise.then(function (response) {
        			var ret = $scope.onSuccess(response.data);
        			alert('ID da Fila = '+ret.length);
        		}, function (response) {
        	    	  alert('DEU PAU!!!');
        	    });

            }
            /*FIM TESTE FILE*/
            
            /*TESTE GET*/
            
            $scope.getProduto = function() {
        		/*$http.defaults.headers.common['X-Auth-Token'] = 'lazarogilvan@yahoo.com.br:1494172802662:bcdc54d4ade8e9f0f61f42b4ec9ed213';
        		$http.defaults.headers.common['Content-Type'] = 'application/json';
        		$http.defaults.headers.common['X-Header-Path'] = 'product/filter';
        		$http.defaults.headers.common['X-Header-Application'] = 'ISOBROU';*/
            	
            	//$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
        		
        		//var url = 'http://localhost:8087/agilize/fileserver/file/saveTempFile';
        		//var url = 'https://agilize-file.herokuapp.com/agilize/fileserver/file/saveTempFile';
        		
        		var url = 'https://agilize-security.herokuapp.com/agilize/facade';
        		//var url = 'http://localhost:8084/agilize/facade';
        		var params = {parameters:JSON.stringify({flgWithoutAddress: true})};
        		
        		var xoxota = angular.extend({}, {}, params);
        		//var httpPromise = $http.get(url, {params:xoxota});
        			
        		$http({
                        url: url,
                        method: 'GET',
                        headers: {
                            'Content-Type':'application/json',
                            'X-Auth-Token': 'lazarogilvan@yahoo.com.br:1494172802662:bcdc54d4ade8e9f0f61f42b4ec9ed213',
                            'X-Header-Path': 'product/filter',
                            'X-Header-Application': 'ISOBROU'
                        },
                        params: xoxota
                    });
                };
        		
        		/*httpPromise.then(function (response) {
        			var ret = $scope.onSuccess(response.data);
        			alert('ID da Fila = '+ret.length);
        		}, function (response) {
        	    	  alert('DEU PAU!!!');
        	    });

            };*/
            
            /*FIM DO TESTE GET*/
        }
    ])
    
})();

/*(function($)
		{
		    
		     * $.import_js() helper (for JavaScript importing within JavaScript code).
		     
		    var import_js_imported = [];

		    $.extend(true,
		    {
		        import_js : function(script)
		        {
		            var found = false;
		            for (var i = 0; i < import_js_imported.length; i++)
		                if (import_js_imported[i] == script) {
		                    found = true;
		                    break;
		                }

		            if (found == false) {
		                $("head").append('<script type="text/javascript" src="' + script + '"></script>');
		                import_js_imported.push(script);
		            }
		        }
		    });

		})(jQuery);*/