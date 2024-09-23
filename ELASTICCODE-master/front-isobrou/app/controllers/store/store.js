angular.module('StoreModule', [
    'ngRoute','resources.store','ui.bootstrap.modal', 'resources.uf'
]).config([
    '$routeProvider', '$httpProvider', 
    function ($routeProvider, $httpProvider) {
        $routeProvider.when('/app/isobrou/store', {
        	templateUrl: 'app/views/register_store.html',
            controller: 'StoreCtrl',
            resolve:{
            	store : ['Store', function(Store){
    		        return new Store();
    		     }],
    		     uf : ['UF', function (UF) {
    		    	return UF.findAllUF(); 
    		     }]
    		     
		    	
            }
            	/*value : ['$route', function($route) {
    				return $route.current.params.value;
    			}]
    	    }*/
        });
    }
]).controller('StoreCtrl', [
                                  '$sce', '$scope','localizedMessages', '$rootScope', 'APP_CONFIG', '$location', '$msgbox',
                                  '$store', 'httpRequestTracker', 'authorizationService', '$http', 'restResource', '$q', 'FileUpload','Store','store', 'uf',
                                  function ($sce, $scope, localizedMessages, $rootScope, APP_CONFIG, $location, $msgbox,
                                  			$store, httpRequestTracker, authorizationService, $http, restResource, $q, FileUpload, Store, store, uf) {
                                	
                                	  APP_CONFIG.baseUrlBack = APP_CONFIG.hostBack;
                                	  
                                	 /* $rootScope.login.user.username = 13005553787;
                                	  $rootScope.login.user.password = 123456;
                                	  $rootScope.login.user.id = 1;*/
                                	  //var idUser = $rootScope.login.user.id
                                	  
                                	  
                                	 // $scope.teste = 'teste';
                                	  $scope.store = {};
                                	  $scope.store = store;
                                	  $scope.uf = [];
                                	  
                                	  
                                	  
                                	  $scope.uf = uf;
                                	  //$scope.store.name = 'teste';
                                	  //$scope.store.description = 'teste';
                                	  /*$scope.entity.name = 'teste';
                                	  $scope.entity.name = 'teste';
                                	  $scope.entity.name = 'teste';*/
                                	  
                                	  $scope.dto = {fileToImport: {nmEntity: null, idUserCreate: null, contentType: null, pathPhysical: null, name: null, pathLogical: null, id: null}};
                                      //$scope.dto.fileToImport.nmEntity = APP_CONFIG.nmEntity;
                                      $scope.dto.fileToImport.idUserCreate = (!isNullValue($rootScope.login.user) && !isNullValue($rootScope.login.user.id)) ? $rootScope.login.user.id : null;
                                	  
                                      var limit = 1024*1024*1024;
                                	  var upOptions = { url: $scope.uploadURL, maxFileSize:limit, acceptFileTypes: /(\.|\/)(txt|zip|png|bmp|jpg)$/i };
                                      FileUpload.setOptions(upOptions);
                                	  
                                      // tratamento do retorno do upload
                                      $scope.onUploadResponse = function(data) {
                                      	var ret = $scope.onSuccess(data.result);
                                      	if(!isNullValue(ret) && ret.length > 0) {
                                      		$scope.dto.fileToImport.contentType = ret[0].contentType; 
                                      		$scope.dto.fileToImport.pathPhysical = ret[0].pathPhysical;
                                      		$scope.dto.fileToImport.name = ret[0].name; 
                                      		$scope.dto.fileToImport.pathLogical = ret[0].pathLogical;
                                      		if(!isNullValue(ret[0].id)) {
                                      			$scope.dto.fileToImport.id = ret[0].id;
                                      		}
                                      	}
                                      };
                                      
                                	 /**
                                	  * Valida campos obrigatorio
                                	  * 
                                	  * */ 
                                      
                                  	function validateRequiredField() {
                                  		if (isNullValue($scope.store.companyName) || isNullValue($scope.store.fantasyName) ||
                              				isNullValue($scope.store.cnpj) /*|| isNullValue($scope.store.stateRegistration) ||
                              				isNullValue($scope.store.complementaryInformation) */|| isNullValue($scope.store.password) ||
                              				isNullValue($scope.store.commercialPhone) /*|| isNullValue($scope.store.cellPhone) ||
                              				isNullValue($scope.store.fax) */|| isNullValue($scope.store.email) ||
                              				isNullValue($scope.store.zipCode) || isNullValue($scope.store.street) ||
                              				isNullValue($scope.store.number)/* || isNullValue($scope.store.complement) ||
                              				isNullValue($scope.store.referencePoint) */|| isNullValue($scope.store.neighborhood) ||
                              				isNullValue($scope.store.initials) || isNullValue($scope.store.nameCounty)
                              				) {
                                  			$scope.message.show("msg.error.requireds.field", false, undefined);
                                            return false;
										}
                                  		
                                  		return true;
                                  		
                                  	}
                                		/*if (isNullValue($scope.entity.name) || isNullValue($scope.entity.description) || isNullValue($scope.entity.applications)) {
                                			$scope.exibError = true;
                                			$scope.message.show("msg.error.requireds.field", false, undefined);
                                            return false;
                                        }
                                		if (isNullValue($scope.entity.fields)) {
                                			$scope.exibError = true;
                                        	var lista = $scope.getMessage("list.entity.field.title");
                                        	$scope.message.show("msg.error.list.null", false, [lista]);
                                            return false;
                                        }
                                		
                                		return true;
                                	}
                                      */
                                      
                                  	$scope.save = function() {
                                	/*	if(!validateRequiredField()) {
                                			return;
                                		}*/
                                		/*var apps = $scope.entity.applications;
                                		$scope.entity.applications = [];
                                    	angular.forEach(apps, function(elem){
                                			$scope.entity.applications.push({application: elem});
                                		});*/
                                	
                                	$scope.store.$save().then(function (result) {
                                		if(result && result.success) {
                            				//reset();
                                			$scope.message.show("msg.save.success", false, undefined);
                                			$location.path('/');
                            			}
                            			else {
                            				$scope.message.show("msg.save.error", true, undefined);
                            			}
                                		return;
                            		});
                                		
                                	}
                                  	
                                  	
                                  /*	$scope.cepPesquisar = function(){
                                  		$location.path('http://correiosapi.apphb.com/cep/76873274');
                                  		
                                  	}
                                  	$scope.cepPesquisar();*/
                                      
                                  }
                                  ]);