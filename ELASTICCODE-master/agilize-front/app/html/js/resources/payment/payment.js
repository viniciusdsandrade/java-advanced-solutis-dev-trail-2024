angular.module('resources.payment', ['restResource']);
angular.module('resources.payment').factory('Payment', ['restResource', '$http', 'i18nNotifications', '$rootScope', '$q', '$msgbox', 'APP_CONFIG', '$httpProvider',
											function ($restResource, $http, i18nNotifications, $rootScope, $q, $msgbox, APP_CONFIG, $httpProvider) {
	  
	var Payment = $restResource('payment', 'payment');
	
	//Resource.prototype.$save
	Payment.initPayment = function (param) {
		var ret;
		$httpProvider.defaults.headers.common[APP_CONFIG.keyApplication] = APP_CONFIG.nmPaymentApplication;
		if(APP_CONFIG.const_TypeCardEnum_CREDIT === param.typeCard.id) {
			ret = param.$postService('/automPayment/simple/creditCard');
		}
		else {
			ret = param.$postService('/init/simple/debitCard');
		}
		$httpProvider.defaults.headers.common[APP_CONFIG.keyApplication] = APP_CONFIG.nmApplication;
		return ret;
    };
    
    Payment.captureDebit = function (paramId) {
		return paramId.$postService('/finalize/simple/debitCard');
    };
	
	return Payment;
	
}]);