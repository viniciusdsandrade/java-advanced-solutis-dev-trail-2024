angular.module('PaymentModule', ['ngRoute', 'resources.payment', 'ui.bootstrap.modal'])
.config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {

    	$routeProvider.when('/agilize/payment/create/:amount/:nmAplic/:merchantOrderId', {
            templateUrl: 'js/views/initTran.html',
            controller: 'PaymentInitControler',
            resolve:{
            	amount : ['$route', function($route) {
    				return $route.current.params.amount;
    			}],
    			nmAplication : ['$route', function($route) {
    				return $route.current.params.nmAplic;
    			}],
    			merchantOrderId : ['$route', function($route) {
    				return $route.current.params.merchantOrderId;
    			}]
    	    }
        });
    	$routeProvider.when('/agilize/payment/capture/debit/:amount/:merchantOrderId', {
            template: '<div></div>',
            controller: 'PaymentCaptureDebitControler',
            resolve:{
            	merchantOrderId : ['$route', function($route) {
    				return $route.current.params.merchantOrderId;
    			}],
    			amount : ['$route', function($route) {
    				return $route.current.params.amount;
    			}]
    	    }
        });
    }
]).controller('PaymentInitControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', 'Payment', '$modal', '$msgbox', '$store', 'amount', 'nmAplication', 'merchantOrderId',
    function ($rootScope, $scope, $location, APP_CONFIG, Payment, $modal, $msgbox, $store, amount, nmAplication, merchantOrderId) {
    	
    	/*var amount = $store.get(APP_CONFIG.amount);
    	var nmAplication = $store.get(APP_CONFIG.keyNmAplic);
    	var idUser = $store.get(APP_CONFIG.keyUser);*/
    	
    	$scope.payment = new Payment();
    	$scope.payment.sale = {payment: {amount: amount, returnUrl: APP_CONFIG.returnUrl,
    								     creditCard: {brand: "", cardNumber: "", expirationDate: "", holder: "", securityCode: ""}}, 
    						   customer: {name: $rootScope.login.user.name}};
    	
    	$scope.payment.typeCard = {};
    	$scope.payment.idUserCreate = $rootScope.login.user.id;
    	$scope.cardNumber = {one: "", two: "", three: "", four: ""};
    	$scope.expirationDate = {month: "", year: ""};
    	$scope.payment.merchantOrderId = merchantOrderId;
    	
    	$scope.page = {};
    	$scope.page.title = $scope.getMessage("page.payment.title");
    	$scope.page.init = false;
    	$scope.page.label = $scope.getMessage("lbl.payment");
    	$scope.page.location = '/agilize/payment/create/'+amount;
    	
		$scope.payment.application = {name: nmAplication};
    	
    	$scope.typesBrand = [];
    	$scope.typesCardEnum = [];
    	Payment.enums(APP_CONFIG.classTypeCardEnum).then(function (result) {
    		var types = $scope.onSuccess(result);
    		angular.forEach(types, function(item){
    			$scope.typesCardEnum.push({id: item.id, label: item.label, description: item.description, msg: $rootScope.getMessage(item.label)});
    		});
		});
    	
    	$scope.changeTypeCard = function() {
    		$scope.typesBrand = [];
    		if(APP_CONFIG.const_TypeCardEnum_CREDIT === $scope.payment.typeCard.id) {
    			Payment.enums(APP_CONFIG.classBrandEnum).then(function (result) {
    	    		var types = $scope.onSuccess(result);
    	    		angular.forEach(types, function(item){
    	    			$scope.typesBrand.push({id: item.id, label: item.label, description: item.description, msg: $rootScope.getMessage(item.label)});
    	    		});
    			});
    		}
    		else {
    			Payment.enums(APP_CONFIG.classBrandEnum).then(function (result) {
    	    		var types = $scope.onSuccess(result);
    	    		angular.forEach(types, function(item){
    	    			if(APP_CONFIG.const_BrandEnum_VISA === item.id || APP_CONFIG.const_BrandEnum_MASTER === item.id) {
    	    				$scope.typesBrand.push({id: item.id, label: item.label, description: item.description, msg: $rootScope.getMessage(item.label)});
    	    			}
    	    		});
    			});
    		}
    	};

    	var pathRedirectReturn = $store.get(APP_CONFIG.keyBackHost) + '/#/' + $store.get(APP_CONFIG.keyBackSameUrl);
    	
    	$scope.cancel = function() {
    		window.location.href = pathRedirectReturn;
    	};
    	
    	function validNumberCard(numb, brand) {
    		
    		var cartoes = {
        		    Visa: /^4[0-9]{12}(?:[0-9]{3})/,
        		    Master: /^5[1-5][0-9]{14}/,
        		    Amex: /^3[47][0-9]{13}/,
        		    Dinners: /^3(?:0[0-5]|[68][0-9])[0-9]{11}/,
        		    Discover: /^6(?:011|5[0-9]{2})[0-9]{12}/,
        		    JCB: /^(?:2131|1800|35\d{3})\d{11}/,
        		    Elo: /^6(?:011|5[0-9]{2})[0-9]{12}/
        	};

    		for (var cartao in cartoes) {
    			if (numb.match(cartoes[cartao]) && cartao === brand) {
    				return true;
    			}
    		}
    	    return false;
    	}
    	
    	function validateFields() {
    		if (isNullValue($scope.payment.typeCard) || isNullValue($scope.cardNumber.one) || isNullValue($scope.cardNumber.two) ||
    				isNullValue($scope.cardNumber.three) || isNullValue($scope.cardNumber.four) || isNullValue($scope.expirationDate.month) ||
    				isNullValue($scope.expirationDate.year) || isNullValue($scope.payment.sale.payment.creditCard.brand) || 
    				isNullValue($scope.payment.sale.payment.creditCard.securityCode) ||
    				isNullValue($scope.payment.sale.payment.creditCard.holder)) {
    			$scope.message.show("msg.error.requireds.field", false, undefined);
                return false;
            }
    		
    		var numb = $scope.cardNumber.one.concat($scope.cardNumber.two).concat($scope.cardNumber.three).concat($scope.cardNumber.four);
    		if(numb.length < 16 || !validNumberCard(numb, $scope.payment.sale.payment.creditCard.brand)) {
    			$scope.message.show("msg.error.invalid.field", false, [$rootScope.getMessage('lbl.payment.card.number')]);
                return false;
    		}
    		
    		if($scope.expirationDate.month > 12 || $scope.expirationDate.year < 1900 || $scope.expirationDate.year > 2100) {
    			$scope.message.show("msg.error.invalid.field", false, [$rootScope.getMessage('lbl.payment.date.expiration')]);
                return false;
    		}
    		
    		var currentTime = new Date();
    		// returns the month (from 0 to 11)
    		var month = currentTime.getMonth() + 1;
    		// returns the year (four digits)
    		var year = currentTime.getFullYear();
    		
    		if($scope.expirationDate.year < year) {
    			$scope.message.show("msg.error.invalid.field", false, [$rootScope.getMessage('lbl.payment.date.expiration')]);
                return false;
    		}
    		else if($scope.expirationDate.year === year) {
    			if($scope.expirationDate.month < month) {
    				$scope.message.show("msg.error.invalid.field", false, [$rootScope.getMessage('lbl.payment.date.expiration')]);
                    return false;
    			}	
    		}

    		$scope.payment.sale.payment.creditCard.cardNumber = numb;
    		$scope.payment.sale.payment.creditCard.expirationDate = $scope.expirationDate.month + "/" + $scope.expirationDate.year;
    		
    		return true;
    	}
    	
    	$scope.executeTran = function() {
    		if(!validateFields()) {
    			return;
    		}
    		
    		/*$store.set(APP_CONFIG.keyTokenSession, result.token);
            $store.set(APP_CONFIG.keyBackHost, hostBack);
            $store.set(APP_CONFIG.keyBackSameUrl, urlSameBack);
            $store.set(APP_CONFIG.keyBackSuccessUrl, urlSucBack);
            $store.set(APP_CONFIG.keyNmAplic, nmAplic);*/
    		
    		var strAmount = parseInt($scope.payment.sale.payment.amount).formatMoney();
    		var text = $rootScope.getMessage("msg.confirm.payment", [strAmount]);
    		$msgbox.show(text).then(function () {
    			Payment.initPayment($scope.payment).then(function (result) {
    				if(result && result.success) {
    					$scope.message.show("msg.payment.success", false, undefined);
    					var record = result.data;
        				//var url = $store.get(APP_CONFIG.keyBackHost) + '/#/' + $store.get(APP_CONFIG.keyBackSuccessUrl);
        				if(APP_CONFIG.const_TypeCardEnum_CREDIT != record.typeCard.id) {
        					if(!isNullValue(record.sale.payment.authenticationUrl)) {
        						window.location.href = record.sale.payment.authenticationUrl;
        						return;
        					}
        				}
        				//window.location.href = url;
        				$location.path('/agilize/payment/capture/debit/'+amount+'/'+record.sale.payment.paymentId);
        			}
            		return;
        		});
    			return;
            }, function () {
            	window.location.href = pathRedirectReturn;
                return;
            });
    	};
    	
    }
])
.controller('PaymentCaptureDebitControler', ['$store', '$rootScope', '$scope', '$location', 'APP_CONFIG', 'Payment', 'merchantOrderId', 'amount',
    										 function ($store, $rootScope, $scope, $location, APP_CONFIG, Payment, merchantOrderId, amount) {
	
		var pathRedirectReturn = $store.get(APP_CONFIG.keyBackHost) + '/#' + $store.get(APP_CONFIG.keyBackSuccessUrl);

		$scope.payment = new Payment();
		$scope.payment.merchantOrderId = merchantOrderId;	
		$scope.payment.idUserCreate = $rootScope.login.user.id;
		$scope.payment.application = {name: $store.get(APP_CONFIG.keyNmAplic)};
		Payment.captureDebit($scope.payment).then(function (result) {
			if(result && result.success) {
				//Retornar para a aplicação que chamou
				$scope.message.show("msg.payment.success", false, undefined);
				window.location.href = pathRedirectReturn;
				return;
			}
			else {
				//Exibir mensagem de erro e retornar para o inicio da transação
				$scope.message.show("msg.error.payment", false, undefined);
				$location.path("/agilize/payment/create/"+amount+"/"+$store.get(APP_CONFIG.keyNmAplic));
			}
		});
	}
]);
