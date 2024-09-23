angular.module('resources.payment', ['restResource']);
angular.module('resources.payment').factory('Payment', ['restResource', '$http', 'i18nNotifications', '$rootScope', '$q', '$msgbox', 'APP_CONFIG',
											function ($restResource, $http, i18nNotifications, $rootScope, $q, $msgbox, APP_CONFIG) {
	  
	var Payment = $restResource('payment', 'payment');
	
	//Resource.prototype.$save
	Payment.initPayment = function (param) {
		var ret;
		if(APP_CONFIG.const_TypeCardEnum_CREDIT === param.typeCard.id) {
			ret = param.$postService('automPayment/simple/creditCard');
		}
		else {
			ret = param.$postService('init/simple/debitCard');
		}
		return ret;
    };
    
    Payment.captureDebit = function (paramId) {
		return paramId.$postService('finalize/simple/debitCard');
    };
	
	return Payment;
	
}]);