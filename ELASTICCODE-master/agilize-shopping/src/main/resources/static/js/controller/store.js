angular.module('StoreModule', ['ngRoute', 'restResource'])

.config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/app/shopping/store', {
            templateUrl: 'templates/store.html',
            controller: 'StoreControler',
        });
    }])
    
.controller('StoreControler', ['$routeParams', '$rootScope', '$scope', '$location', 'APP_CONFIG', '$msgbox', 'restResource', 'DataService',
    function ($routeParams, $rootScope, $scope, $location, APP_CONFIG, $msgbox, $restResource, DataService) {
    	
		var Store = $restResource('store', 'store');
		$scope.cart = DataService.cart;
		$scope.products = {pageableFilterDTO: {page: 1, totalRows: 0}, jsonList:[]};
		
		$scope.openProduct = function(id) {
			$location.path('/app/shopping/product/'+id);
		};
		
		
		$rootScope.findByFilter = $scope.findByFilter = function() {
    		
    		var paramsFilter = [];
    		if(!isNullValue($scope.search)) {
    			paramsFilter.push({param: '_soundex', valueParam: $scope.filter.name, predicateType: 1, filterOperator: 13 });
    		}
    		
    		Store.filter({page:0, rowsPerPage:5, paramsFilter: paramsFilter}).then(function (result) {
    			$scope.products = result;
    			$scope.cart.loadItens(isNullValue(result) ? [] : result.jsonList);
    			//$scope.products.list = convertJsonToString(result);
    			/*if(isNullValue($scope.products.totalRowsInCachePageable)) {
    				if(!isNullValue($scope.products.list)) {
    					$scope.products.totalRowsInCachePageable = $scope.products.list.length;
    				}
    				else {
    					$scope.products.totalRowsInCachePageable = 0;
    				}
    			}*/
    			Store.pagination($scope, $scope.products, function (newResult) { 
    				$scope.products = newResult; 
        			$scope.cart.loadItens(isNullValue(newResult) ? [] : newResult.jsonList);
    				//$scope.products.list = convertJsonToString(newResult);
    			});
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

])
;