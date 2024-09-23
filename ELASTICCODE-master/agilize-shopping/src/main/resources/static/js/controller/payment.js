angular.module('ShoppingCartModule', ['ngRoute', 'restResource'])

.config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/app/shopping/payment', {
            templateUrl: 'templates/shoppingCart.html',
            controller: 'ShoppingCartController'
        });
    }])
    
.controller('ShoppingCartController', ['$routeParams', '$rootScope', '$scope', '$location', 'APP_CONFIG', '$msgbox', 'restResource', 'DataService', '$window',
    function ($routeParams, $rootScope, $scope, $location, APP_CONFIG, $msgbox, $restResource, DataService, $window) {
    	
		var Store = $restResource('store', 'store');
		var cart = DataService.cart;
		$scope.cart = cart;
		
    	var msg = $routeParams.menssage;
    	if(!isNullValue(msg)) {
    		$msgbox.show(msg, undefined, true);
    		$location.search('menssage', null);
    		$location.path(APP_CONFIG.urlHome);
    		return;
    	}
    	
    	$scope.payment = function() {
        	if($rootScope.login.logged) {
        		var order = {orders: $scope.cart.getShoppingItems()}
        		Store.post('/order', order).then(function (response) {
            		if(!isNullValue(response)) {
                		var value = $scope.cart.getTotalPrice();
                		// SERVER
                		var url = APP_CONFIG.urlPayment + '?hostBack=' + APP_CONFIG.host + '&urlSameBack=/app/shopping/product/' + value +
                			'&urlSucBack=' + APP_CONFIG.urlHome + '&amount=' + value + '&nmAplic=' + APP_CONFIG.nmApplication + 
                			'&token=' + $rootScope.login.token + '&merchantOrderId=' + response.id;
                		$window.location.href = url;
            		}
        		});
        	}
        	else {
        		$rootScope.login.open();
        	}
    	};
		
    }
]);