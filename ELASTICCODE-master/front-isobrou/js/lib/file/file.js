angular.module('resources.file', ['restResource']);
angular.module('resources.file').factory('File', ['restResource', function ($restResource) {

  var File = $restResource('file','file');
  
  File.findAllLibraryForUserLogged = function() {
      return File.queryPathVariable('/userlogged/findalllibray');
  };
  File.registerDownloadLog = function(fileId) {
      return File.queryPathVariable('/downloadTracker/' + fileId);
  };
  File.findAvailableByTraining = function(objectId) {
      return File.queryPathVariable('/findavailablebytraining/' + objectId);
  };
  File.findAvailableByTrainings = function(objectIds) {
      return File.queryPathVariable('/findavailablebytrainings/' + objectIds);
  };
  File.countAvailableByTraining = function(objectId) {
      return File.queryPathVariable('/countavailablebytraining/' + objectId, function(){}, function(){}, false);
  };
  File.countAvailableByTrainings = function(objectIds) {
      return File.queryPathVariable('/countavailablebytrainings/' + objectIds, function(){}, function(){}, false);
  };
  File.countavailablebytraininginroute = function(objectId) {
      return File.queryPathVariable('/countavailablebytraininginroute/' + objectId, function(){}, function(){}, false);
  };
  
  return File;
   
}]);