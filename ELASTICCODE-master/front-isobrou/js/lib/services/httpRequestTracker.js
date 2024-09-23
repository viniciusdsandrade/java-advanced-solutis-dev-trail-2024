angular.module('services.httpRequestTracker', []);
angular.module('services.httpRequestTracker').factory('httpRequestTracker', ['$http', function($http){

  var httpRequestTracker = {};
  httpRequestTracker.loaderText = 'Carregando...';
  httpRequestTracker.forceLoader = false;
  
  httpRequestTracker.hasPendingRequests = function() {
	  if(!httpRequestTracker.forceLoader){
		  var hasPending = $http.pendingRequests.length > 0;
		  if(!hasPending) {
			  httpRequestTracker.loaderText = 'Carregando...';	  
		  }
		  return hasPending;
	  }
    return httpRequestTracker.forceLoader;
  };

  return httpRequestTracker;
}]);