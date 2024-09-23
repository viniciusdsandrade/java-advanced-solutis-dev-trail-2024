angular.module('StoreModule', ['ngRoute', 'restResource'])

.config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/app/shopping/store', {
            templateUrl: '../templates/store.html',
            controller: 'StoreControler',
        });
    }])
    
.controller('StoreControler', ['$routeParams', '$rootScope', '$scope', '$location', 'APP_CONFIG', '$msgbox', 'restResource',
    function ($routeParams, $rootScope, $scope, $location, APP_CONFIG, $msgbox, $restResource) {
    	
		var Store = $restResource('store', 'store');
		
		$scope.products = {pageableFilterDTO: {page: 1, totalRows: 0}, list:[]};
		
		$scope.findByFilter = function() {
    		
    		var paramsFilter = [];
    		if(!isNullValue($scope.search)) {
    			paramsFilter.push({param:'name', valueParam: $scope.filter.name, predicateType: 1, filterOperator: 0 });
    		}
    		
    		Entity.filter({page:0, rowsPerPage:5, paramsFilter: paramsFilter}).then(function (result) {
    			$scope.products = result;
    			Store.pagination($scope, $scope.products, function (newResult) { $scope.products = newResult; });
    		});
    	};
	
    	var msg = $routeParams.menssage;
    	
    	if(!isNullValue(msg)) {
    		$msgbox.show(msg, undefined, true);
    		$location.search('menssage', null);
    		$location.path(APP_CONFIG.urlHome);
    	}
    	else {
        	if($rootScope.login.logged) {
        		$scope.findByFilter();
        		
        	}
        	else {
        		$rootScope.login.open();
        	}
        	
        	
    	}
    }

]);