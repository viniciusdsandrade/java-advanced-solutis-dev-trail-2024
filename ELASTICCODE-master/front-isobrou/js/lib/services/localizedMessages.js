angular.module('services.localizedMessages', []).factory('localizedMessages', ['$interpolate', 'I18N.MESSAGES', function ($interpolate, i18nmessages) {

  var handleNotFound = function (msg, msgKey) {
    return msg || '?' + msgKey + '?';
  };

  return {
    get : function (msgKey, interpolateParams) {
      var msg =  i18nmessages[msgKey];
      if (msg) {
    	  if(interpolateParams){
    		  if(interpolateParams instanceof Array){
    			  var arrayParams = interpolateParams;
    			  interpolateParams= {};
    			  for(var i=0; i<arrayParams.length; i++){
    				  interpolateParams['_'+i] = arrayParams[i];
    			  }
    		  }
    	  }
        return $interpolate(msg)(interpolateParams);
      } else {
        //return handleNotFound(msg, msgKey);
        return msgKey;
      }
    }
  };
}]);