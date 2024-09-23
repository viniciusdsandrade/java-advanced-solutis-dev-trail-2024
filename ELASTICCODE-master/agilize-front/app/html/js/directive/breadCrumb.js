angular.module('breadCrumb', []).directive('breadCrumbDirective', ['$rootScope', '$store', '$sce', '$location', function($rootScope, $store, $sce, $location) {
	  
	return {
		  restrict: 'EA',
		  templateUrl: 'views/components/breadcrumb.html',
		  scope: {
				init: '=',
				label: '=',
				location: '=',
				title: '=?'
		  },			
		  link: function(scope, element, attrs, ctrl) {
		      var listBread;  
			  if(scope.init) {
				  listBread = [{label: scope.label, labelMsg: scope.label/*$rootScope.getMessage(scope.label)*/, location: scope.location}];
				  $store.set('BREAD_CRUMB', listBread);
			  }
			  else {
				  listBread = $store.get('BREAD_CRUMB');
				  if(isNullValue(listBread)) {
					  listBread = [];
				  }
				  //Verificando se não foi dado Ctrl-F5
				  var exists = false;
				  for (var i = 0; i < listBread.length; i += 1) {
					  if(listBread[i].label === scope.label && listBread[i].location === scope.location) {
						  exists = true;
						  break;
					  }
				  }
				  if(!exists) {
					  listBread.push({label: scope.label, labelMsg: scope.label /*$rootScope.getMessage(scope.label)*/, location: scope.location});
				  }
				  $store.set('BREAD_CRUMB', listBread);
			  }
			  
			  scope.breadCrumbs = listBread;
			  scope.sizeList = listBread.length-1;
			  /*scope.title = '';
			  if(!isNullValue(scope.title)) {
				  scope.title = $rootScope.getMessage(scope.title);
			  }*/
			  
			  /*var strHtml2 = '<div id="breadcrumb2"> <ul class="crumbs2">';
			  var i = 1;
			  angular.forEach(listBread, function(item) {
				  if(i === 1 && listBread.length === 1) {
					  strHtml2 = strHtml2 + ' <li class="first"><a>'+item.labelMsg+'</a></li> ';
				  }
				  else if(i === 1 && listBread.length > 1) {
					  strHtml2 = strHtml2 + ' <li class="first"><a href="'+item.location+'">'+item.labelMsg+'</a></li> ';
				  }
				  
				  if(i === listBread.length && listBread.length > 1) {
					  strHtml2 = strHtml2 + ' <li class="last"><a>'+item.labelMsg+'</a></li>';
				  }
				  
				  if(i > 1 && i < listBread.length) {
					  strHtml2 = strHtml2 + ' <li><a href="'+item.location+'">'+item.labelMsg+'</a></li>';
				  }
				  i = i + 1;
			  });
			  scope.strHtml2 = strHtml2 + '</ul> </div>';*/
			  
			  var strHtml = '<ul>';
			  var i = 1;
			  angular.forEach(listBread, function(item) {
				  
				  if(i === listBread.length) {
					  strHtml = strHtml + ' <li class="active" style="font-size: 11px;display: inline;"> <i class="fa fa-edit"></i>'+item.labelMsg+'</li> ';
				  }
				  else {
					  strHtml = strHtml + ' <li style="font-size: 12px;display: inline;"> <i class="fa fa-dashboard"></i> <a href="#'+item.location+'">'+item.labelMsg+'</a>';
				  }
				  
				  i = i + 1;				  
			  });
			  scope.strHtml = strHtml + '</ul>';
			  
			  scope.renderHtml = function (htmlCode) {
			        if (!isNullValue(htmlCode)) {
			            return $sce.trustAsHtml(htmlCode);
			        }
			    };
			    
			    scope.redirect = function (path) {
			        if (!isNullValue(path)) {
			            return $location.path(path);
			        }
			    };
		  }
	};
}]);