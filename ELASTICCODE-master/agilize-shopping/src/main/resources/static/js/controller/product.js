angular.module('ProductModule', ['ngRoute', 'restResource'])

.config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/app/shopping/product/:id', {
            templateUrl: 'templates/product.html',
            controller: 'ProductControler',
            resolve:{
            	id : ['$route', function($route) {
    				return $route.current.params.id;
    			}]
    	    }
        });
    }])
    
.controller('ProductControler', ['$routeParams', '$rootScope', '$scope', '$location', 'APP_CONFIG', '$msgbox', 'restResource', 'DataService', 'id',
    function ($routeParams, $rootScope, $scope, $location, APP_CONFIG, $msgbox, $restResource, DataService, id) {
    	
		var Store = $restResource('store', 'store');
		var cart = DataService.cart;
		$scope.product = cart.findOne(id);
		$scope.cart = cart;
    }
]);