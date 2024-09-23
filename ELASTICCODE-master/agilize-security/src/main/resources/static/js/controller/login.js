angular.module('LoginModule', [
    'ngRoute'
]).config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/agilize/login', {
            templateUrl: 'js/views/login.html',
            controller: 'LoginControler'
        });
        
        $routeProvider.when('/signin/logged/:token', {
            controller: 'LoginSiginControler',
            templateUrl: 'js/views/login.html',
            resolve:{
            	token : ['$route', function($route) {
    				return $route.current.params.token;
    			}]
    	    }
        });
    }
    
]).controller('LoginSiginControler', ['$rootScope', 'token', '$store', 'APP_CONFIG', function ($rootScope, token, $store, APP_CONFIG) {  

	login(token, $rootScope, $store, APP_CONFIG);
	
}]).controller('LoginControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', '$routeParams', '$store', 'authorizationService', 'restResource',
    function ($rootScope, $scope, $location, APP_CONFIG, $routeParams, $store, authorizationService, restResource) {
    	
    	$scope.application = {backgroundImage: {pathLogical: ''}};
    	
        var RestResource = restResource('RestResource', '');
    	var urlParams = $store.get('urlParams');
    	if(!isNullValue(urlParams)) {
    		urlParams = urlParams.split(';');
            RestResource.application(urlParams[0]).then(function (result) {
            	if(!isNullValue(result) && !isNullValue(result.backgroundImage) && !isNullValue(result.backgroundImage.pathLogical)) {
            		$scope.application = result;
            	}
            });
    	}
    	
    	//Informando que o userName deste Login é CPF.
    	$rootScope.login.user.typeAuthentication = 1;
    	//Zerando as informações
    	$rootScope.login.clear();
    	
        $rootScope.login.send = function () {
                	$scope.message.text = "";
                    $rootScope.message.text = "";
                    $store.set(APP_CONFIG.keyTokenSession, null);
                    if (isNullValue($scope.login.user.username)) {
                        var strLogin = $scope.getMessage("lbl.email");
                        $scope.message.show("msg.error.required.field", false, [strLogin]);
                    } else if (isNullValue($scope.login.user.password)) {
                        var strPassw = $scope.getMessage("lbl.password");
                        $scope.message.show("msg.error.required.field", false, [strPassw]);
                    } else {
                        var data = JSON.stringify({
                            username: $scope.login.user.username,
                            password: authorizationService.encode($scope.login.user.password),
                            typeAuthentication: $scope.login.user.typeAuthentication,
                            device: window.$.device.type.id
                        });
                        
                        var urlBackHost = $routeParams.urlBackHost;
                        
                        $rootScope.login.logged = false;
                        
                        RestResource.login(data).then(function (result) {
                        	if(!isNullValue(result) && !isNullValue(result.token)) {
                                result.password = null;
                            	login(result.token, $rootScope, $store, APP_CONFIG);
                        	}
                        });
                    }
                };
    }
    
]);

function login(token, $rootScope, $store, APP_CONFIG) {
	
	var urlBackHost = $store.get('urlBackHost');
	var urlBackPath = $store.get('urlBackPath');
	var urlParams = $store.get('urlParams');
	$rootScope.login.logged = false;
	if (!isNullValue(token)) {
        $rootScope.login.logged = true;
        $rootScope.login.token = token;
        
        $rootScope.login.dateAuthentication = Date.now();

        $store.logoff(APP_CONFIG.keySessionUser);
        $store.bind($rootScope, APP_CONFIG.keySessionUser, $rootScope.login);
        
        window.scrollTo(0, 0);
        
        $store.set(APP_CONFIG.keyTokenSession, token);
        $store.set(APP_CONFIG.keySessionUrls, []);
        
        var url = null;
        if(!isNullValue(urlBackHost) && !isNullValue(urlBackPath)) {
        	url = urlBackHost + '/#/' + urlBackPath + '/' + token;
        	if(!isNullValue(urlParams)) {
            	url = url + '&' + urlParams;
            }
        	
        	$store.set('urlBackHost', null);
        	$store.set('urlBackPath', null);
        	$store.set('urlParams', null);
        	
            window.location.href = url;
        }
        else {
        	$rootScope.login.clear();
        }
        return;
    }
}