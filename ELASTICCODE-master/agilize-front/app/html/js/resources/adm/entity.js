angular.module('resources.entity', ['restResource']);
angular.module('resources.entity').factory('Entity', ['restResource', '$http', 'i18nNotifications', '$rootScope', '$q', '$msgbox', 
											function ($restResource, $http, i18nNotifications, $rootScope, $q, $msgbox) {
	  
	var Entity = $restResource('entity', 'entity');
	
	Entity.entitiesForApplications = function (applications) {
		
		var params = '';
		angular.forEach(applications, function(item){
			params = params + item.id + ';';
		});
		
		return Entity.get('/aplications', params);
    };
	
	return Entity;
	
}]);