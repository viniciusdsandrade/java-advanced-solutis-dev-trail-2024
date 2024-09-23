var ang = angular.module('formMenu', []);

ang.constant('APP_MENU', {
	
	menu: [ { id: '1', label: 'lbl.menu.item.entities', itens: [{id: '1.1', label: 'lbl.menu.filter', path: '/agilize/entity/list', roles: []},
																{id: '1.2', label: 'lbl.menu.new.item', path: '/agilize/entity/create', roles: []}] },
		    { id: '3', label: 'lbl.menu.item.3', itens: [ { id: '3.1', label: 'lbl.menu.item.3.1', itens: [{id:'3.1.1', label: 'lbl.menu.item.3.1.1', itens: [{id: '3.1.1.1', label: 'lbl.menu.item.3.1.1.1', path: 'XPTO'}] }, {id: '3.1.2', label: 'lbl.menu.item.3.1.2', itens: [{id: '3.1.2.1', label: 'lbl.menu.item.3.1.2.1', path: 'XPTO'}]} ] }, {id: '3.2', label: 'lbl.menu.item.3.2', itens: [{id: '3.2.1', label: 'lbl.menu.item.3.2.1', path: 'XPTO'}]}, {id:'3.3', label: 'lbl.menu.item.3.3', path: 'XPTO'} ] }
		  ],
	footer: [ {id: 1, target: '_blank', label: 'lbl.footer.1', href: 'https://github.com/rdash/rdash-angular', path: null},
			  {id: 2, target: '_blank', label: 'lbl.footer.2', href: 'https://github.com/rdash/rdash-angular/blob/master/README.md', path: null},
			  {id: 3, target: null, label: 'lbl.footer.3', href: null, path: 'INSERIR O PATH'}
			]
		    
});

ang.directive('menuDirective', ['$rootScope', 'APP_MENU', 'authorizationService', '$location', function($rootScope, APP_MENU, authorizationService, $location) {
	  
	return {
		  templateUrl: 'views/components/menu.html',
		
		  link: function(scope, element, attrs, ctrl) {
			  
			  mountHtml2(scope);
			  
			  function mountHtml3(itens, level) {

				  var varContinue;
				  var authorized;
				  var strHtml = ' <ul> ';

				  angular.forEach(itens, function(item) {
					  
					  varContinue = true;
					  if(!isNullValue(item.roles)) {
						  authorized = authorizationService.isAuthorized(true, item.roles);
						  if(authorized.level === authorizationService.levelModel.loginRequired || authorized.level === authorizationService.levelModel.notAuthorised) {
							  varContinue = false;
						  }
					  }
					  
					  if(varContinue) {
						  strHtml = strHtml + ' <li class="has-sub"> <a';
						  if(!isNullValue(item.path)) {
							  strHtml = strHtml + ' href="#'+item.path+'" ';
						  }
						  strHtml = strHtml + '>'+ $rootScope.getMessage(item.label) + '</a> ';
						  
						  if(!isNullValue(item.itens)) {
							  strHtml = strHtml + mountHtml3(item.itens, (level+1));
						  }
						  
						  for (var j = 0; j < level; j += 1) {
							  strHtml = strHtml + ' </li> ';
						  }
					  }
				  });				  
				  return strHtml+ ' </ul> ';
			  }

			  function mountHtml2(scope) {
				  
				  scope.menu = {navigation:'', footer:''};
				  
				  if(!$rootScope.login.logged) {
					  return;
				  }
				  
				  var varContinue;
				  var authorized;
				  var strHtml = '<div class="navigation"> <ul> ';
				  
				  angular.forEach(APP_MENU.menu, function(item) {
					  varContinue = true;
					  if(!isNullValue(item.roles)) {
						  authorized = authorizationService.isAuthorized(true, item.roles);
						  if(authorized.level === authorizationService.levelModel.loginRequired || authorized.level === authorizationService.levelModel.notAuthorised) {
							  varContinue = false;
						  }
					  }
					  
					  if(varContinue) {
						  strHtml = strHtml + '<li class="has-sub"> <a';
						  if(!isNullValue(item.path)) {
							  strHtml = strHtml + ' href="#'+item.path+'" ';
						  }
						  strHtml = strHtml + '> '+ $rootScope.getMessage(item.label) +' </a> ';
						  
						  if(!isNullValue(item.itens)) {
							  strHtml = strHtml + mountHtml3(item.itens, 1);
						  }
						  strHtml = strHtml + ' </li> ';
					  }
				  });
				  strHtml = strHtml + ' </ul> </div> ';
				  
				  scope.menu.navigation = strHtml;
				  
				  strHtml = ' <div class="sidebar-footer"> ';
				  
				  angular.forEach(APP_MENU.footer, function(item) {
					  strHtml = strHtml + ' <div class="col-xs-4"> <a ';
					  if(!isNullValue(item.href)) {
						  strHtml = strHtml + 'href="'+item.href+'" ';
					  }
					  if(!isNullValue(item.target)) {
						  strHtml = strHtml + 'target="'+item.target+'" ';
					  }
					  if(!isNullValue(item.path)) {
						  strHtml = strHtml + 'ng-click="openPath('+item.path+')" ';
					  }
					  strHtml = strHtml + '> '+ $rootScope.getMessage(item.label) + ' </a> </div>';
				  });
				  
				  strHtml = strHtml + ' </div> ';
				  
				  scope.menu.footer = strHtml;
			  }
		  }
	};
}]);
