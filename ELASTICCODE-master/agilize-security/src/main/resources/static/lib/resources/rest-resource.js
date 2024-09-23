angular.module('restResource', ['msgbox', 'services.localizedMessages']).factory('restResource', ['APP_CONFIG','$http', '$q', '$msgbox', 'localizedMessages', '$rootScope',
				function (APP_CONFIG, $http, $q, $msgbox, localizedMessages, $rootScope) {

  function restResourceFactory(collectionName,substantive) {

    var url = APP_CONFIG.baseUrlBack;
    var defaultParams = {};
    
      var thenFactoryMethod = function (httpPromise, successcb, errorcb, isArray, isFilter) {
      var scb = successcb || $rootScope.onSuccess;
      var ecb = errorcb || $rootScope.onError;

      if (typeof isFilter == 'undefined') {
    	  isFilter = false;
  		}
      
      return httpPromise.then(function (response) {
        var result;
        if (isFilter) {
			result = angular.copy(response.data);
			delete result.list;
			result.list = [];
			if(response.data.list && response.data.list.length > 0) {
				for (var i = 0; i < response.data.list.length; i++) {
					  result.list.push(new Resource(response.data.list[i]));
					}
			}
        } else {
	        if (isArray) {
	          result = [];
	          for (var i = 0; i < response.data.length; i++) {
	            result.push(new Resource(response.data[i]));
	          }
	        } else {
	          //Resource rest full has rather peculiar way of reporting not-found items, I would expect 404 HTTP response status...
	          if (response.data === " null "){
	            return $q.reject({
	              code:'resource.notfound',
	              collection:collectionName
	            });
	          } else {
	            result = new Resource(response.data);
	          }
	        }
        }
        return scb(result, response.status, response.headers, response.config);
      }, function (response) {
    	  if(response){
    		  return ecb(response.data, response.status, response.headers, response.config);
    	  }
        return undefined;
      });
    };

    var Resource = function (data) {
      angular.extend(this, data);
    };

    Resource.all = function (cb, errorcb) {
      return Resource.query({}, cb, errorcb);
    };

    // assume que sera retornada uma LIST, se desejar utilizar este metodo para retornar
    // um objeto, passar parametro FALSE
    Resource.queryPathVariable = function (pathVariable, successcb, errorcb, isList) {
    	if (isList === undefined) {
    		isList = true;
    	}
    	var pathVariable = substantive.concat(pathVariable);
    	$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
    	var httpPromise = $http.get(url);
    	return thenFactoryMethod(httpPromise, successcb, errorcb, isList);
    };
      
    Resource.query = function (queryJson, successcb, errorcb) {
    	var params = angular.isObject(queryJson) ? {q:JSON.stringify(queryJson)} : {};
    	var pathVariable = substantive;
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
  		var httpPromise = $http.get(url, {params:angular.extend({}, defaultParams, params)});
  		return thenFactoryMethod(httpPromise, successcb, errorcb, true);
    };
    
    Resource.filter = function (filterObj, substantiveCustom, successcb, errorcb) {
    	if (typeof filterObj.rowsPerPage == 'undefined') {
    		filterObj.rowsPerPage = APP_CONFIG.rowsPerPage; // assume qtd linhas constant
    	}
    	var params = angular.isObject(filterObj) ? {filters:JSON.stringify(filterObj)} : {};
    	var httpPromise;
    	if (substantiveCustom === undefined) {
    		var pathVariable = substantive.concat('/filter');
      		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
    	} else {
    		var pathVariable = substantive.concat(substantiveCustom).concat('/filter');
      		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
    	}
		httpPromise = $http.get(url+'/filter', {params:angular.extend({}, defaultParams, params)});
        return thenFactoryMethod(httpPromise, successcb, errorcb, false, true);
      };
      
  	function isNullValue (val) {
		return (val === null || !angular.isDefined(val) || (angular.isNumber(val) && !isFinite(val)));
	}

      
    Resource.filterCustom = function (filterObj,substantiveCustom , successcb, errorcb) {
	  	
    	if (!isNullValue(filterObj) && typeof filterObj.rowsPerPage == 'undefined') {
	  		filterObj.rowsPerPage = APP_CONFIG.rowsPerPage; // assume qtd linhas constant
	  	}
	  	var params = angular.isObject(filterObj) ? {filters:JSON.stringify(filterObj)} : {};

		var pathVariable = substantive.concat(substantiveCustom).concat('/filter');
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
	  	
	  	var httpPromise = $http.get(url, {params:angular.extend({}, defaultParams, params)});
        return thenFactoryMethod(httpPromise, successcb, errorcb, false, true);
    };
    
    Resource.getById = function (id, successcb, errorcb) {
    	
		var pathVariable = substantive + '/' + id;
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;

	      var httpPromise = $http.get(url, {params:defaultParams});
	      return thenFactoryMethod(httpPromise, successcb, errorcb);
    };

    Resource.removeVariable = function (successcb, errorcb, pathDelete, id) {
    	
		var pathVariable = substantive + "/" + pathDelete + "/" + id;
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;

        var httpPromise = $http['delete'](url, {params:defaultParams});
        return thenFactoryMethod(httpPromise, successcb, errorcb);
    };

    Resource.pagination = function (scope, result, fnRefresh, substantiveCustom, updateResult) {
    	var thisResource = this;
    	
    	if (!(scope.calcPagination)) {
    		scope.calcPagination = function (resultado) {
    			if(resultado && resultado.pageableFilterDTO){
    			  scope.currentPage = resultado.pageableFilterDTO.page + 1;
    			  scope.numPages = parseInt((resultado.pageableFilterDTO.totalRows -1) / resultado.pageableFilterDTO.rowsPerPage) + 1;
    			}
    		  };
    	}
    	/**
		 * Alguns cenários em que temos duas diretivas ou alteração na URl de
		 * paginação "substantiveCustom", o metódo será chamado para atualizar a
		 * url afim de informar ao resource que existe uma nova URL para
		 * resolver a consulta paginada.
		 * 
		 * Por isso sempre que for acionado o metodo pai [pagination] a função
		 * selectPage será atualizada no scopo com o "substativeCustom" atual.
		 */
		scope.selectPage = function(page){
			result.pageableFilterDTO.page = page-1;
			thisResource.filter(result.pageableFilterDTO, substantiveCustom).then(function(newResult) {
				fnRefresh(newResult);
				if(updateResult) {
					result = newResult;					
				}
				scope.calcPagination(newResult);
			  });
		};
    	scope.calcPagination(result);
    };
    
    Resource.get = function (pathVariable, obj, successcb, errorcb) {
    	
    	var params = angular.isObject(obj) ? {parameters:JSON.stringify(obj)} : {parameters:obj};
    	
		var pathUrl = substantive;
		if(!isNullValue(pathVariable)) {
			pathUrl = pathUrl.concat(pathVariable);
		}
		
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathUrl;
  		
		var httpPromise = $http.get(url, {params:angular.extend({}, defaultParams, params)});
		return thenFactoryMethod(httpPromise, successcb, errorcb, false);
    };

    Resource.post = function (pathVariable, parameters, successcb, errorcb) {
		var pathUrl = url.concat('/').concat(substantive);
		if(!isNullValue(pathVariable)) {
			pathUrl = pathUrl.concat(pathVariable);
		}
		
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathUrl;
		var httpPromise = $http.post(url, parameters);
		return thenFactoryMethod(httpPromise, successcb, errorcb, false, false);
    };
    
    Resource.login = function (parameters, successcb, errorcb) {
		var httpPromise = $http.post(APP_CONFIG.urlAutenticate, parameters);
		return thenFactoryMethod(httpPromise, successcb, errorcb, false, false);
    };
    
    Resource.loginGoogle = function (successcb, errorcb) {
    	var urlGoogle = APP_CONFIG.hostBack + '/signin/google';
    	var httpPromise = $http.post(urlGoogle);
		return thenFactoryMethod(httpPromise, successcb, errorcb, false, false);
    };
    
    Resource.enums = function (nameClass, successcb, errorcb) {
    	var config = {params: {nameClassEnum: nameClass}};
		var httpPromise = $http.get(url+'/enums', config);
		return thenFactoryMethod(httpPromise, successcb, errorcb, false);
    };
    
    Resource.applications = function (successcb, errorcb) {
    	var httpPromise = $http.get(url+'/applications');
		return thenFactoryMethod(httpPromise, successcb, errorcb, false);
    };
    
    //instance methods

    Resource.prototype.$id = function () {
      if (this.id) {
        return this.id;
      }
    };

    Resource.prototype.$postService = function (pathVariable, successcb, errorcb) {
    	
		var pathVariable = substantive.concat('/').concat(pathVariable);
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
  		this.idUserCreate = $rootScope.login.user.id;
        var httpPromise = $http.post(url, this);
        return thenFactoryMethod(httpPromise, successcb, errorcb, false);
      };
      
    Resource.prototype.$save = function (successcb, errorcb) {
    	
		var pathVariable = substantive;
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
    	
  		this.idUserCreate = $rootScope.login.user.id;
  		var httpPromise = $http.post(url, this, {params:defaultParams});
        return thenFactoryMethod(httpPromise, successcb, errorcb, false);
    };
    
    Resource.prototype.$update = function (successcb, errorcb) {
    	
		var pathVariable = substantive + "/" + this.$id();
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;
    	
  		this.idUserCreate = $rootScope.login.user.id;
  		var httpPromise = $http.put(url, angular.extend({}, this, {_id:undefined}), {params:defaultParams});
  		return thenFactoryMethod(httpPromise, successcb, errorcb);
    };

    Resource.prototype.$remove = function (successcb, errorcb) {

		var pathVariable = substantive + "/" + this.$id();
  		$http.defaults.headers.common[APP_CONFIG.keyPathUrl] = pathVariable;

  		var httpPromise = $http['delete'](url);
  		return thenFactoryMethod(httpPromise, successcb, errorcb);
    };
    
    Resource.validateToken = function (parameters, successcb, errorcb) {
		var httpPromise = $http.post(APP_CONFIG.urlValidate, parameters);
		return thenFactoryMethod(httpPromise, successcb, errorcb, false, false);
    };
    
    Resource.application = function (nmApplic, successcb, errorcb) {
    	var config = {params: {nmApplication: nmApplic}};
		var httpPromise = $http.get(url+'/application', config);
		return thenFactoryMethod(httpPromise, successcb, errorcb, false);
    };

    
    return Resource;
  }
  return restResourceFactory;
}]);