angular.module('LoginModule', [
    'ngRoute'
]).config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/agilize/login', {
            templateUrl: 'views/login/login.html',
            controller: 'LoginControler'
        });
    }
]).controller('LoginControler', [
    '$rootScope', '$scope', '$location', 'PropriedadesCompartilhadas', 'APP_CONFIG', '$routeParams',
    function ($rootScope, $scope, $location, PropriedadesCompartilhadas, APP_CONFIG, $routeParams) {
    	
    	//Informando que o userName deste Login é CPF.
    	$rootScope.login.user.typeAuthentication = 1;
    	
    	//Zerando as informações
    	$rootScope.login.clear();
    	
    	//caso a chamada venha de outros sistemas
    	//var urlBack = $routeParams.urlBack;
    }

]);