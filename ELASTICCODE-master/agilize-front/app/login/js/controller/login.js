angular.module('LoginModule', [
    'ngRoute'
]).config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/agilize/login', {
            templateUrl: 'js/views/login.html',
            controller: 'LoginControler'
        });
        $routeProvider.when('/agilize/user/create', {
            templateUrl: 'js/views/cadastroUser.html',
            controller: 'LoginCreateControler'
        });
    }
]).controller('LoginControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', '$routeParams', '$auth', '$store', 'authorizationService', 'restResource',
    function ($rootScope, $scope, $location, APP_CONFIG, $routeParams, $auth, $store, authorizationService, restResource) {
    	
    	//Informando que o userName deste Login é CPF.
    	$rootScope.login.user.typeAuthentication = 1;
    	//Zerando as informações
    	$rootScope.login.clear();
    	
    	$scope.authenticate = function(provider) {
    	    $auth.authenticate(provider).then(function (result) {
    	    	alert('1+1');
    	    });
    	};
    	
    	/*$scope.linkInstagram = function() {
    		  $auth.link('instagram')
    		    .then(function(response) {
    		      $window.localStorage.currentUser = JSON.stringify(response.data.user);
    		      $rootScope.currentUser = JSON.parse($window.localStorage.currentUser);
    		    });
    		};
    		
    		$scope.instagramLogin = function() {
    		      $auth.authenticate('instagram')
    		        .then(function(response) {
    		          $window.localStorage.currentUser = JSON.stringify(response.data.user);
    		          $rootScope.currentUser = JSON.parse($window.localStorage.currentUser);
    		        })
    		        .catch(function(response) {
    		          console.log(response.data);
    		        });
    		    };*/
    		    
    		    
    		
    	
    	//caso a chamada venha de outros sistemas
    	//var urlBack = $routeParams.urlBack;
    	
    	$rootScope.login.open = function () {
                    window.scrollTo(0, 0);
                    var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);

                    if (isLogged) {
                        var text = $rootScope.getMessage("msg.user.logged", [$rootScope.login.user.name]);
                        $msgbox.show(text).then(function () {
                            $rootScope.login.clear();
                            $location.path(APP_CONFIG.urlLogin);
                            return;
                        }, function () {
                            $location.path(getPath($store, APP_CONFIG));
                            return;
                        });
                    } else {
                        $rootScope.login.clear();
                        $location.path(APP_CONFIG.urlLogin);
                    }
                };
                
         $rootScope.login.creation = function () {
                    window.scrollTo(0, 0);
                    var isLogged = $store.isLogged(APP_CONFIG.keySessionUser);
                    if (isLogged) {
                        var text = $rootScope.getMessage("lbl.msg.user.logged", [$rootScope.login.user.name]);
                        $msgbox.show(text).then(function () {
                            $rootScope.login.clear();
                            $location.path('/agilize/user/create');
                            return;
                        }, function () {
                            $location.path('');
                            return;
                        });
                    } else {
                        $rootScope.login.clear();
                        $location.path('/agilize/user/create');
                    }
                    closeMenu();
                };
                
        $rootScope.login.send = function () {
                	$scope.message.text = "";
                    $rootScope.message.text = "";
                    $store.set(APP_CONFIG.keyTokenSession, null);
                    if (isNullValue($scope.login.user.username)) {
                        var strLogin = $scope.getMessage("lbl.email");
                        $scope.message.show("msg.error.required.field", false, [strLogin]);
                    } else if (isNullValue($scope.login.user.password)) {
                        var strPassw = $scope.getMessage("lbl.password");
                        $scope.message.show("msg.error.required.field", false, [strPassw]);
                    } else {
                        var data = JSON.stringify({
                            username: $scope.login.user.username,
                            password: authorizationService.encode($scope.login.user.password),
                            typeAuthentication: $scope.login.user.typeAuthentication,
                            device: window.$.device.type.id
                        });
                        $rootScope.login.logged = false;
                        var RestResource = restResource('RestResource', '');
                        
                        //chamada venha de outros sistemas
                    	var urlBackHost = $routeParams.urlBackHost;
                    	var urlBackPath = $routeParams.urlBackPath;
                    	var urlParams = $routeParams.params;
                        
                        RestResource.login(data).then(function (result) {
                            if (!isNullValue(result)) {
                                result.password = "";
                                $rootScope.login.user = result;
                                $rootScope.login.logged = true;
                                $rootScope.login.token = result.token;
                                
                                $rootScope.login.dateAuthentication = Date.now();

                                $store.logoff(APP_CONFIG.keySessionUser);
                                $store.bind($rootScope, APP_CONFIG.keySessionUser, $rootScope.login);
                                
                                window.scrollTo(0, 0);
                                
                                $store.set(APP_CONFIG.keyTokenSession, result.token);
                                $store.set(APP_CONFIG.keySessionUrls, []);
                                
                                var url = urlBackHost + '/#/' + urlBackPath + '/' + $rootScope.login.token;
                                if(!isNullValue(urlParams)) {
                                	url = url + '/' + urlParams;
                                }
                                window.location.href = url;
                                return;
                            }
                        });
                    }
                };
    }
]).controller('LoginCreateControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', '$routeParams',
    function ($rootScope, $scope, $location, APP_CONFIG, $routeParams) {
        
        $scope.user = {name: "", cpf: "", email: "", birthday: null, password: "", phone: "", userAnswer: null};
        $scope.userAnswer = {flgAcademic: false, flgWorks: false, institution: "", course: ""};
        
        $scope.createRecord = function () {
            if ($scope.disabledSend) {
                $rootScope.message.show('lbl.error.message.confirm.creation.user', false, undefined, undefined, true);
                return;
            }
            var valid;
            var EMAIL_REGEXP = /^[_a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/;
            //Validando Obrigatoriedade
            if ($rootScope.isNullValue($scope.user.name) || $rootScope.isNullValue($scope.user.cpf) || $rootScope.isNullValue($scope.user.email) ||
                    $rootScope.isNullValue($scope.dateTemporary) || $rootScope.isNullValue($scope.user.password) || $rootScope.isNullValue($scope.user.confirmPassword)) {
                $rootScope.message.show('lbl.error.required.fields', false, undefined, undefined, true);
                return;
            }
            if ($scope.userAnswer.flgAcademic == null || $scope.userAnswer.flgWorks == null) {
                $rootScope.message.show('lbl.error.required.fields', false, undefined, undefined, true);
                return;
            }
            if ($scope.userAnswer.flgAcademic == true) {
                if ($rootScope.isNullValue($scope.userAnswer.institution)) {
                    var strInstitution = $scope.getMessage('lbl.institution');
                    $rootScope.message.show('lbl.error.required.especific.field', false, [strInstitution], undefined, true);
                    return;
                }
                if ($rootScope.isNullValue($scope.userAnswer.course)) {
                    var strCourse = $scope.getMessage('lbl.course');
                    $rootScope.message.show('lbl.error.required.especific.field', false, [strCourse], undefined, true);
                    return;
                }
            }
            // validando telefone
            var valuePhone = $scope.user.phone;
            valuePhone = valuePhone.replace(/\D/g, '');//pega o valor sem mascara
            if (valuePhone.length < 10) {
                var strPhone = $scope.getMessage('lbl.phone');
                $rootScope.message.show('lbl.error.invalid.field', false, [strPhone], undefined, true);
                return;
            }
            var dateSplit = $scope.dateTemporary.split("/");
            $scope.user.birthday = new Date(dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0]);
            if ($scope.user.birthday.getTime() >= new Date().getTime()) {
                $rootScope.message.show('lbl.error.date.minor', false, undefined, undefined, true);
                return;
            }
            //Validando CPF
            var strCpf = $scope.getMessage('lbl.cpf');
            if (window && window.CPF) {
                valid = window.CPF.isValid($scope.user.cpf);
            } else {
                valid = module.CPF.isValid($scope.user.cpf);
            }
            if (!valid) {
                $rootScope.message.show('lbl.error.invalid.field', false, [strCpf], undefined, true);
                return;
            }
            //Validando Email
            var strEmail = $scope.getMessage('lbl.email');
            if (!EMAIL_REGEXP.test($scope.user.email)) {
                $rootScope.message.show('lbl.error.invalid.field', false, [strEmail], undefined, true);
                return;
            }
            //Validando o password
            var password = $scope.getMessage('lbl.password');
            if ($scope.user.password.length != 8) {
                $rootScope.message.show('lbl.error.invalid.field.password', false, [password, 8], undefined, true);
                return;
            }
            if ($scope.user.password !== $scope.user.confirmPassword) {
                $rootScope.message.show('lbl.error.invalid.field.confirm.password', false, undefined, undefined, true);
                return;
            }
            $scope.user.userAnswer = $scope.userAnswer;
            $scope.user.device = window.$.device.type.id;
            var data = JSON.stringify($scope.user);
            P01RestResource.post('user/createUser', data, $scope).then(function (result) {
                if (!$rootScope.isNullValue(result)) {
                    var page = $scope.getMessage($scope.pages.active);
                    $rootScope.login.user = result;
                    $rootScope.login.logged = true;
                    $rootScope.login.token = result.token;
                    $rootScope.message.show('lbl.msg.save.success', true, [page], undefined, true);
                    var url = P01_CONFIG.urlPortal;
                    /*if (!$rootScope.isNullValue(PropriedadesCompartilhadas.nextUrl) && !(url.contains(P01_CONFIG.urlCreationUser))) {
                        url = PropriedadesCompartilhadas.nextUrl;
                    }*/
                    window.scrollTo(0, 0);
                    $location.path(url);
                }
            });
        };
    }
]);