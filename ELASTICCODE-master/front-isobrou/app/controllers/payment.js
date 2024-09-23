angular.module('PaymentModule', [
    'ngRoute'
]).config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/app/isobrou/payment/:value', {
            templateUrl: 'app/views/app.html',
            controller: 'PaymentControler',
            resolve:{
            	value : ['$route', function($route) {
    				return $route.current.params.value;
    			}]
    	    }
        });
    }
]).controller('PaymentControler', [
    '$routeParams', '$rootScope', '$scope', '$location', 'APP_CONFIG', '$routeParams', 'value', '$window', '$msgbox',
    function ($routeParams, $rootScope, $scope, $location, APP_CONFIG, $routeParams, value, $window, $msgbox) {
    	
    	var msg = $routeParams.menssage;
    	
    	if(!isNullValue(msg)) {
    		$msgbox.show(msg, undefined, true);
    		$location.search('menssage', null);
    		$location.path(APP_CONFIG.urlHome);
    	}
    	else {
        	if($rootScope.login.logged) {
        		
        		// SERVER
        		var url = APP_CONFIG.urlPayment + '?hostBack=' + APP_CONFIG.host + '&urlSameBack=app/isobrou/payment/' + value +
        			'&urlSucBack=' + APP_CONFIG.urlHome + '&amount=' + value + '&nmAplic=' + APP_CONFIG.nmApplication + 
        			'&token=' + $rootScope.login.token;
        		
        		/*var url = 'http://localhost:8088/agilize/merci/#/merci?hostBack=http://localhost:8400&urlSameBack=app/isobrou/payment/'+value+
					'&urlSucBack=/app/isobrou&amount='+value+'&nmAplic='+APP_CONFIG.nmApplication+'&token='+$rootScope.login.token;*/
        		$window.location.href = url;
        		/*$window.location.href = 'http://localhost:8100/app/payment/#/payment?hostBack=http://localhost:8400&urlSameBack=app/isobrou/payment/'+value+
    				'&urlSucBack=/app/isobrou&amount='+value+'&nmAplic='+APP_CONFIG.nmApplication+'&token='+$rootScope.login.token;*/
        		/*$window.location.href = 'http://localhost:8100/app/payment/#/payment?hostBack=http://localhost:8400&urlSameBack=app/isobrou/payment/'+value+
										'&urlSucBack=/app/isobrou&amount='+value;*/
        	}
        	else {
        		$rootScope.login.open();
        	}
    	}

    	
    	//$rootScope.login.clear();
    }

]);