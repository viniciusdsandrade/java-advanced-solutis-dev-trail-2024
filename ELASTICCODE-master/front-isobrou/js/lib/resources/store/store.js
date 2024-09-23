angular.module('resources.store', ['restResource']);
angular.module('resources.store').factory('Store', ['restResource','$http','$rootScope', '$q', '$msgbox', 
											function ($restResource, $http, $rootScope, $q, $msgbox) {
	  
	var Store = $restResource('store', 'store');
	
	Store.findAllUF = function() {
	      return Store.queryPathVariable('/ufAll');
	 };
	
	return Store;
	
}]);