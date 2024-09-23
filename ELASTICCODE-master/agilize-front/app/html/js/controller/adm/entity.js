angular.module('EntityModule', ['ngRoute', 'resources.entity', 'ui.bootstrap.modal'])
.config([
    '$routeProvider', '$httpProvider',
    function ($routeProvider, $httpProvider) {

    	$routeProvider.when('/agilize/entity/list', {
            templateUrl: 'views/entity/filter.html',
            controller: 'EntityListControler'
        });
    	$routeProvider.when('/agilize/entity/create', {
            templateUrl: 'views/entity/save.html',
            controller: 'EntitySaveControler',
            resolve:{
    	    	item: [function() { return null; }]
    	    }
        });
    	$routeProvider.when('/agilize/entity/edit/:id', {
            templateUrl: 'views/entity/save.html',
            controller: 'EntitySaveControler',
            resolve:{
            	item : ['$route','Entity', function($route, Entity) {
    				return Entity.getById($route.current.params.id);
    			}]
    	    }
        });
    }
]).controller('EntityListControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', 'Entity',
    function ($rootScope, $scope, $location, APP_CONFIG, Entity) {
    	
    	$scope.filter = {name: "", description: "", applications: []};
    	$scope.entities = {pageableFilterDTO: {page: 1, totalRows: 0}, list:[]};
    	
    	$scope.page = {}; 
    	$scope.page.title = $scope.getMessage("page.entity.filter.title");
    	$scope.page.init = true;
    	$scope.page.label = $scope.getMessage("lbl.filter");
    	$scope.page.location = '/agilize/entity/list';

    	$scope.findByFilter = function() {
    		
    		var paramsFilter = [];
    		if(!isNullValue($scope.filter.name)) {
    			paramsFilter.push({param:'name', valueParam: $scope.filter.name, predicateType: 1, filterOperator: 0 });
    		}
    		if(!isNullValue($scope.filter.description)) {
    			paramsFilter.push({param:'description', valueParam: $scope.filter.description, predicateType: 1, filterOperator: 0 });
    		}
    		
    		if(!isNullValue($scope.filter.applications)) {
    			var i = 0;
    			var listIds = '';
        		for (var key in $scope.filter.applications) {
                    var flag = typeof $scope.filter.applications[key] === "object";
                    if (!flag) {
                        break;
                    }
                    if(i === $scope.filter.applications.length-1) {
                    	listIds = listIds + $scope.filter.applications[key].id;
                    }
                    else {
                    	listIds = listIds + $scope.filter.applications[key].id + ',';
                    }
                    i = i + 1;
                }
        		paramsFilter.push({param:'applications', valueParam: listIds, predicateType: 1, filterOperator: 0 });
    		}
    		
    		Entity.filter({page:0, rowsPerPage:5, paramsFilter: paramsFilter}).then(function (result) {
    			$scope.entities = $rootScope.onSuccess(result);
    			Entity.pagination($scope, $scope.entities, function (newResult) { $scope.entities = newResult; });
    		});
    	};
    	
    	$scope.deleteItem = function(item) {
    		var index = $scope.entities.list.indexOf(item);
    		
    		var entity = new Entity();
    		entity.id = item.id;
    		entity.$remove().then(function (result) {
    			if(result && result.success) {
        			$scope.message.show("msg.delete.success", false, undefined);
            		if (index > -1) {
            			$scope.entities.list.splice(index, 1);
            		}
    			}
    		});
    	};
    	
    	$scope.edit = function(item) {
    		$location.path('/agilize/entity/edit/'+item.id);
    	};
    	
    	$scope.applicationsList = [];
    	Entity.applications().then(function (result) {
    		$scope.applicationsList = $scope.onSuccess(result);
		});
    	$scope.checkApplication = function (item) {
            if ($scope.filter.applications.indexOf(item) === -1) {
                $scope.filter.applications.push(item);
            } else {
                $scope.filter.applications.splice($scope.checkedApplications.indexOf(item), 1);
            }
        };
    	
    }

])
.controller('EntitySaveControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', 'Entity', 'item', '$modal', '$msgbox',
    function ($rootScope, $scope, $location, APP_CONFIG, Entity, item, $modal, $msgbox) {
    	
    	$scope.page = {};
    	//Editar
    	if(!isNullValue(item) && !isNullValue(item.data)) {
    		item = item.data;
        	$scope.entity = new Entity();
    		$scope.entity.id = item.id;
    		$scope.entity.name = item.name;
        	$scope.entity.description = item.description;
        	if(!isNullValue(item.fields)) {
        		$scope.entity.fields = item.fields;
        	}
        	else {
        		$scope.entity.fields = [];
        	}
        	$scope.entity.applications = [];
        	angular.forEach(item.applications, function(elem){
    			$scope.entity.applications.push(elem.application);
    		});
        	
        	$scope.page.title = $scope.getMessage("page.entity.update.title");
        	$scope.page.init = false;
        	$scope.page.label = $scope.getMessage("lbl.update");
        	$scope.page.location = '/agilize/entity/edit/'+item.id;
        	
    	}//New
    	else {
    		reset();
        	
        	$scope.page.title = $scope.getMessage("page.entity.save.title");
        	$scope.page.init = true;
        	$scope.page.label = $scope.getMessage("lbl.save");
        	$scope.page.location = '/agilize/entity/create';
    	}
    	
    	$scope.applicationsList = [];
    	Entity.applications().then(function (result) {
    		$scope.applicationsList = $scope.onSuccess(result);
		});
    	$scope.checkApplication = function (item) {
            if ($scope.entity.applications.indexOf(item) === -1) {
                $scope.entity.applications.push(item);
            } else {
                $scope.entity.applications.splice($scope.checkedApplications.indexOf(item), 1);
            }
        };
    	
    	$scope.openModalField = function() {
    		
    		if(isNullValue($scope.entity.applications)) {
    			$scope.message.show("msg.error.application.required", false, undefined);
                return;
    		}
    		
    		$scope.opts = {
    		        backdrop: true,
    		        backdropClick: true,
    		        dialogFade: false,
    		        keyboard: true,
    		        templateUrl : 'views/entity/modal.field.html',
    		        controller : 'EntityFieldModalControler',
    		        resolve: {
    		        	fields : function() {return  $scope.entity.fields;},
    		        	applications : function() {return  $scope.entity.applications;}
    		        } 
    		};

            var modalInstance = $modal.open($scope.opts);

            modalInstance.result.then(function() {
        	   //on ok button press
            },function(){
        	   //on cancel button press
            });
    	};
    	
    	$scope.remove = function(item) {
    		var index = $scope.entity.fields.indexOf(item);
    		if (index > -1) {
    			$scope.entity.fields.splice(index, 1);
    		}
    	};
    	
    	$scope.currentPage = 1;
    	$scope.numPages = 1;
    	$scope.calcPagination = function (resultado) {
    		if(resultado && resultado.length) {
    			$scope.numPages = parseInt((resultado.length) / 5);
    		}
    	};
    	$scope.selectPage = function(page) {
    		$scope.currentPage = page;
    	};
    	$scope.calcPagination($scope.entity.fields);
    	
    	function validateRequiredField() {
    		if (isNullValue($scope.entity.name) || isNullValue($scope.entity.description) || isNullValue($scope.entity.applications)) {
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
    	
    	function save() {
    		if(!validateRequiredField()) {
    			return;
    		}
    		
    		var apps = $scope.entity.applications;
    		$scope.entity.applications = [];
        	angular.forEach(apps, function(elem){
    			$scope.entity.applications.push({application: elem});
    		});
    		
    		$scope.entity.$save().then(function (result) {
    			if(result && result.success) {
    				reset();
        			$scope.message.show("msg.save.success", false, undefined);
    			}
    			else {
            		$scope.entity.applications = apps;
    			}
        		return;
    		});
    		
    	}
    	
    	function update() {
    		if(!validateRequiredField()) {
    			return;
    		}

    		var apps = $scope.entity.applications;
    		$scope.entity.applications = [];
        	angular.forEach(apps, function(elem){
    			$scope.entity.applications.push({application: elem.application});
    		});
    		
    		$scope.entity.$update().then(function (result) {
    			if(result && result.success) {
    				reset();
        			$scope.message.show("msg.update.success", false, undefined);
        			$location.path('/agilize/entity/list');
    			}else {
            		$scope.entity.applications = apps;
    			}
        		return;
    		});
    	}
    	
    	$scope.save = function() {
    		//Editar
        	if(!isNullValue(item) && !isNullValue(item.id)) {
        		update();
        	}
        	else {
        		save();
        	}
    	};
    	
    	function reset() {
    		$scope.entity = new Entity();
        	$scope.entity.id = null;
    		$scope.entity.name = "";
        	$scope.entity.description = "";
        	$scope.entity.fields = [];
        	$scope.entity.applications = [];
    	}
    	
    	$scope.cancel = function() {
    		reset();
    		$location.path('/agilize/entity/list');
    	};
    	
    }

])
.controller('EntityFieldModalControler', [
    '$rootScope', '$scope', '$location', 'APP_CONFIG', 'Entity', 'fields', '$modalInstance', 'applications',
    function ($rootScope, $scope, $location, APP_CONFIG, Entity, fields, $modalInstance, applications) {
    	
    	$scope.exibError = false;
    	$scope.msg = "";
    	$scope.field = {typeField: null, name: '', typeRelation: null, entityRef: null, description: null };
    	
    	$scope.types = [];
    	Entity.enums(APP_CONFIG.classFieldEnum, function(){}).then(function (result) {
    		//var types = onReturnCallBack(result);
    		var types = $scope.onSuccess(result);
    		angular.forEach(types, function(item){
    			$scope.types.push({id: item.id, label: item.label, description: item.description, msg: $rootScope.getMessage(item.label)});
    		});
		});
    	
    	$scope.typesRelations = [];
    	Entity.enums(APP_CONFIG.classRelationShipEnum, function(){}).then(function (result) {
    		var types = $scope.onSuccess(result);
    		angular.forEach(types, function(item){
    			$scope.typesRelations.push({id: item.id, label: item.label, description: item.description, msg: $rootScope.getMessage(item.label)});
    		});
		});
    	
    	$scope.entities = [];
    	$scope.changeTypeField = function() {
    		if($scope.field.typeField && $scope.field.typeField.id === APP_CONFIG.const_FieldEnum_RELATIONSHIP) {
    			$scope.entities = [];
        		Entity.entitiesForApplications(applications).then(function (result) {
        			var list = $rootScope.onSuccess(result);
        			for (var key in list) {
                        var flag = typeof list[key] === "object";
                        if (!flag) {
                            break;
                        }
                        $scope.entities.push(list[key]); 
                    }
        		});
    		}
    	};
    	
    	$scope.fields = fields;
    	
    	$scope.cancel = function () {
    		
    		$scope.exibError = false;
        	$scope.msg = "";
    		$modalInstance.dismiss('cancel');
    	};
    	
    	$scope.saveField = function() {
    		
    		$scope.exibError = false;
        	$scope.msg = "";
    		if (isNullValue($scope.field.typeField) || isNullValue($scope.field.name)) {
    			$scope.exibError = true;
            	$scope.msg = $scope.getMessage("msg.error.requireds.field");
                return;
            }
    		
    		if($scope.field.typeField.id === 0) {
    			if (isNullValue($scope.field.typeRelation) || isNullValue($scope.field.entityRef)) {
        			$scope.exibError = true;
                	$scope.msg = $scope.getMessage("msg.error.requireds.field");
                    return;
                }
    		}
    		
    		$scope.field.idUserCreate = $rootScope.login.user.id;
    		$scope.fields.push($scope.field);
    		$modalInstance.close();
    	};
    	
    	/*function onReturnCallBack(response) {
    		if(!isNullValue(response)) {
    	    	var data = response;
    	    	if((!isNullValue(data.errorCode) && data.errorCode > 0) && !isNullValue(data.i18nKey)) {
    	    		$scope.exibError = true;
    	    		$scope.msg = $scope.getMessage(data.i18nKey, data.params);
    				return;
    			}
    	    	if(!isNullValue(data.error)) {
    	    		$scope.exibError = true;
    	    		$scope.msg = $scope.getMessage(data.error.i18nKey, data.error.params);
    				return;
    	    	}
    	    	if(data.success) {
    	    		return data.data;
    	    	}
    	    	return data;
    		}
    	}*/
    }

]);
