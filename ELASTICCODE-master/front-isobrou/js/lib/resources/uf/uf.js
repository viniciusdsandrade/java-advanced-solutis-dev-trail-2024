angular.module('resources.uf', ['restResource']);
angular.module('resources.uf').factory('UF', ['restResource', 'APP_CONFIG', 
											function ($restResource, APP_CONFIG) {
	  
	var UF = $restResource('uf', 'uf');
	
	UF.findAllUF = function() {
		return UF.ufAll();
	};
	
	return UF;
	
}]);